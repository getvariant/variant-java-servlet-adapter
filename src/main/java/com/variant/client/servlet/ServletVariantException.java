package com.variant.client.servlet;

import com.variant.client.VariantException;

/**
 * We want to extend from ClientException to stick with the convention.
 */
public class ServletVariantException extends VariantException {

	private static final long serialVersionUID = 1L;

	public ServletVariantException(ServletVariantError error, String...args) {
		super(error, args);
	}
	
	public static ServletVariantException internal(String msg) {
		return new ServletVariantException(ServletVariantError.SERVLET_ADAPTER_INTERNAL_ERROR, msg);
	}
}
