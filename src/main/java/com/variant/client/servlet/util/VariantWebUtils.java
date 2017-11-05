package com.variant.client.servlet.util;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author Igor
 * @since 1.0
 */
public class VariantWebUtils {

	/**
	 * Reconstruct request URL.
	 * @param request
	 * @return
	 */
	public static String requestUrl(HttpServletRequest request) {
		
		String uri = request.getRequestURI();
		String queryString = request.getQueryString();
		return uri + (queryString == null ? "" : "?" + queryString);
	}
	
}
