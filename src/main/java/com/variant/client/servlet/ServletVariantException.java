package com.variant.client.servlet;

import com.variant.client.VariantException;

/**
 * We want to extend from ClientException to stick with the convention.
 */
public class ServletVariantException extends VariantException {

	private static final long serialVersionUID = 1L;

	public ServletVariantException(String msg) {
		super(msg);
	}
}
