package com.variant.client.servlet;

import com.variant.client.VariantException;

/**
 * We want to extend from ClientException to stick with the convention.
 */
@SuppressWarnings("serial")
public class ServletVariantException extends VariantException {

	public ServletVariantException(String msg) {
		super(msg);
	}
}
