package com.variant.client.servlet;

import com.variant.client.VariantError;

public class ServletVariantError extends VariantError {

	/**
	 * Okay to use reserved range 301-400
	 */
	protected ServletVariantError(int code, String format) {
		super(code, format);
	}
		
	public static final ServletVariantError SERVLET_ADAPTER_INTERNAL_ERROR =
			new ServletVariantError(301, "Servlet adapter internal error: %s");

	public static final ServletVariantError USER_DATA_INVALID =
			new ServletVariantError(302, "User data  must have single element of type HttpServletRequest");

	public static final ServletVariantError PATTERN_INVALID = 
			new ServletVariantError(303,"Pattern must start with [/] but was [%s]");

	public static final ServletVariantError FILTER_INIT_MISSING = 
			new ServletVariantError(304,"Filter init parameter [schema] must be specified");

}

