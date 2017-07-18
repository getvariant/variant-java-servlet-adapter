package com.variant.client.servlet.demo;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.variant.client.Session;
import com.variant.client.servlet.VariantFilter;

/**
 * Variant Java client + Servlet adapter demo application.
 * Demonstrates instrumentation of a basic Variant experiment.
 * See https://github.com/getvariant/variant-java-servlet-adapter/tree/master/servlet-adapter-demo
 * for details.
 * 
 * Copyright © 2015-2017 Variant, Inc. All Rights Reserved.
 *
 * Extend the basic {@link VariantFilter} with Petclinic specific semantics.
 *
 * @author Igor Urisman
 *
 */
public class PetclinicVariantFilter extends VariantFilter {
	
	/**
	 * Whenever the servlet adapter obtains a foreground session, save the User-Agent header
	 * from the incoming request. This will be used by the server side user hooks in order
	 * to disqualify or target user sessions based on what Web browser they are coming from.
	 * 
	 */
	@Override
	protected void sessionObtained(ServletRequest request, ServletResponse response, Session session) {
		
		super.sessionObtained(request, response, session);
		session.setAttribute("user-agent", ((HttpServletRequest)request).getHeader("User-Agent"));
	}

}
