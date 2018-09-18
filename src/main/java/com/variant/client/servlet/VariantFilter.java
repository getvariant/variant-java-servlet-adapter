package com.variant.client.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.variant.client.VariantException;
import com.variant.client.servlet.util.StateSelectorByRequestPath;
import com.variant.core.StateRequestStatus;
import com.variant.core.TraceEvent;
import com.variant.core.schema.State;

/**
 * <p>The preferred way of instrumenting Variant experiments for host applications written on top of the Java Servlet API.
 * 
 * <h4>Overview</h4> 
 *
 * <p>By using this filter, the application programmer can, in many cases, instrument experiments
 * with no to very little coding. The filter intercepts HTTP requests for the instrumented
 * pages, obtains the resolved variant and, if non-control, forwards the request to the path
 * contained in the resolved state parameter {@code path}.
 * 
 * <p>The application programmer should use this filter if the following assumptions hold:
 * <ul>
 * <li>The host applicaiton is written on top of the Servlet API.
 * <li>All state variants have a single point of entry, which can be mapped to a request path.
 * <li>The test schema defines the {@code path} state parameter, so that 1) its base value 
 * denotes the resource path to the this state's control; and 2) its variant value denotes the 
 * resource path to the single point of entry of that state variant.
 * </ul>
 * 
 * <h4>Configuration</h4>
 * <p>The filter is configured as following:
 * <pre>
 * {@code
 *  <filter>
 *      <filter-name>variantFilter</filter-name>
 *      <filter-class>com.variant.client.servlet.VariantFilter</filter-class> 
 *      <init-param>
 *          <param-name>schemaResourceName</param-name>
 *          <param-value>/path/to/schema.json</param-value>
 *      </init-param>
 *      <init-param>
 *          <param-name>propsResourceName</param-name>
 *          <param-value>/path/to/variant.props</param-value>
 *      </init-param>
 *  </filter>
 *   
 *  <filter-mapping>
 *      <filter-name>variantFilter</filter-name>
 *      <url-pattern>/*</url-pattern>
 *  </filter-mapping>
 * }
 * </pre>
 *
 * The {@link #init(FilterConfig)} method looks for two initialization parameters:
 * <ul>
 * <li>{@code schemaResourceName}: Name of the schema file as a classpath resource. 
 * Parsed once during filter initialization. To change a schema, application restart will be required. 
 * (Temporary limitaion to be removed in an upcoming version.)
 * 
 * <li>{@code propsResourceName}: Name of the application properties file as a
 * classpath resource. Its content will override default properties, but will be overridden
 * by {@code /varaint.props}, if found on the classpath.
 * </ul>
 * 
 * <p>The URL pattern in filter mapping can be something narrower than {@code /*}, of course, so long
 * as it matches all state {@code path}s listed in the schema.
 * 
 * <p>{@link StateParsedHookListenerImpl} hook listener is registered prior to parsing of the schema,
 * which checks that each state defines the {@code path} parameter and that its value starts with a
 * forward slash. If that is not the case, a parser error will be emitted by the listener.
 * 
 * <h4>Execution Semantics</h4>
 * 
 * <p>It is assumed that the base {@code path} state parameter, i.e. the one specified at the {@code State}
 * level, denotes the resource path to the control variation of this state. This allows this filter to
 * identify whether an incoming HTTP request is for an instrumented state or not.  If not, the request
 * is forwarded down the filter chain by calling {@code chain.doFilter(ServletRequest, ServletResponse)}.
 * 
 * <p>If the requested path corresponds to an instrumented state, the session (obtained from 
 * Variant client servlet adapter by calling {@link ServletVariantClient#getOrCreateSession(HttpServletRequest)}) 
 * is targeted for this state with {@link ServletSession#targetForState(State)}. The resulting
 * {@link ServletStateRequest} object contains information about the outcome of the targeting
 * operation, including the resulting variant and the resolved state parameters. This 
 * {@link ServletStateRequest} object is added to the current {@link HttpServletRequest} as
 * an attribute named {@link #VARIANT_REQUEST_ATTRIBUTE_NAME}, should the downstream application code 
 * wish to extend the semantics, e.g. trigger a custom {@link VariantEvent}.  
 * 
 * <p>The resolved variant's {@code path} 
 * state parameter is interpreted as the request path to the resource which implements the targeted
 * variant. The request is forwarded to that path with {@code ServletRequestDispatcher.forward()}.
 * 
 * <p>Upon return from either forward or a fall-through down the filter chain, the 
 * {@link #doFilter(ServletRequest, ServletResponse, FilterChain)} method below adds the status of the
 * HTTP response in progress to the pending state visited event and commits the Variant state request
 * in progress.
 * 
 * <p>Any exceptions due to Variant are caught and logged. Should such an exception occur, an attempt
 * is made to mark the state of the pending state visited event as failed and to allow the request
 * proceed down the filter chain. In other words, should the instrumentation fail due to an internal
 * Varaint exception, the user session will see the control experience. 
 * 
 * @author Igor Urisman
 * @since 1.0
 */
public class VariantFilter implements Filter {

	private static final Logger LOG = LoggerFactory.getLogger(VariantFilter.class);

	private static final ServletVariantClient client = ServletVariantClient.Factory.getInstance();
	
	private String schemaName = null;
	private ServletConnection connection = null;
	
	//---------------------------------------------------------------------------------------------//
	//                                          PUBLIC                                             //
	//---------------------------------------------------------------------------------------------//

	public static final String VARIANT_REQUEST_ATTR_NAME = "variant-state-request";

	/**
	 * Initialize the Variant servlet adapter and parse the experiment schema.
	 * @see Filter#init(FilterConfig)
	 * @since 0.5
	 */
	@Override
	public void init(FilterConfig config) throws ServletException {
		
		schemaName = config.getInitParameter("schema");
		
		if (schemaName == null) 
			throw new ServletVariantException("Filter init parameter [schema] must be specified");
		else {
			try {
				connection = client.connectTo(schemaName);
				LOG.info("Connected to Variant schema [" + schemaName + "]");
			}
			catch (VariantException e) {
				LOG.error("Variant error " + e.getMessage());
			}
		}
	}
	
	/**
	 * Identify the state, target and commit the state request.
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 * @since 1.0
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
			throws IOException, ServletException {
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		ServletSession variantSsn = null; 
		ServletStateRequest stateRequest = null;
		
		long start = System.currentTimeMillis();

		String resolvedPath = null;
		boolean isForwarding = false;
		String path = httpRequest.getRequestURI();

		LOG.debug("VariantFilter for path [" + path + "]");

		try {
			
			// If we're not connected, try to reconnect.
			if (connection == null) {
				connection = client.connectTo(schemaName);
				LOG.info("Connected to Variant schema [" + schemaName + "]");
			}

			// Get Variant session.
			variantSsn = connection.getOrCreateSession(httpRequest);
			
			// Is this request's URI mapped in Variant?
			State state = StateSelectorByRequestPath.select(variantSsn.getSchema(), path);
			
			if (state == null) {
				// Variant doesn't know about this path.
				if (LOG.isTraceEnabled()) LOG.trace("Path [" + path + "] is not instrumented");
			}
			else {

				// Path instrumented by Variant and we have variant session. 
				variantSsn.setAttribute("user-agent", ((HttpServletRequest)request).getHeader("User-Agent"));
				stateRequest = variantSsn.targetForState(state);
				request.setAttribute(VARIANT_REQUEST_ATTR_NAME, stateRequest);

				resolvedPath = stateRequest.getResolvedParameters().get("path");
				isForwarding = !resolvedPath.equals(state.getParameters().get("path"));
				
				if (LOG.isDebugEnabled()) {
					String msg = 
							"Variant dispatcher for path [" + path +
							"] completed in " + DurationFormatUtils.formatDuration(System.currentTimeMillis() - start, "mm:ss.SSS") +". ";
					if (isForwarding) {
						msg += "Forwarding to path [" + resolvedPath + "].";							
					}
					else {
						msg += "Falling through to requested URL";
					}
					LOG.debug(msg);
				}
				
			}
		}
		catch (VariantException e) {
			LOG.error("Unhandled Variant exception for path [" + path + "]", e);
			isForwarding = false;
			if (stateRequest != null) stateRequest.fail(httpResponse);
		}
		
		if (isForwarding) {
				request.getRequestDispatcher(resolvedPath).forward(request, response);
		}				
		else {
			chain.doFilter(request, response);							
		}
							
		// Commit state request if not yet.
		if (stateRequest != null && stateRequest.getStatus() == StateRequestStatus.InProgress) {
			try {
				// Add some extra info to the state visited event(s)
				TraceEvent sve = stateRequest.getStateVisitedEvent();
				if (sve != null) sve.setAttribute("HTTP_STATUS", Integer.toString(httpResponse.getStatus()));

				// The following line throws NPE on Safari, most likely to disqualification => no SVE.
				//stateRequest.getStateVisitedEvent().getParameterMap().put("HTTP_STATUS", String.valueOf(httpResponse.getStatus()));
				stateRequest.commit(httpResponse);
			}
			catch (VariantException e) {
				LOG.error("Unhandled exception in Variant for path [" + path + "]", e);
				try {
					if (stateRequest.getStatus() == StateRequestStatus.InProgress)
					stateRequest.fail(httpResponse);
				}
				catch (VariantException e2) {
					LOG.error("Unhandled exception in Variant for path [" + path + "]", e2);					
				}
			}
		}
	}

	/**
	 * @see Filter#destroy()
	 * @since 1.0
	 */
	@Override
	public void destroy() {
		// Anything ?
	}

	
}
