package com.variant.client.servlet.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Cookie class encapsulates everything common to all Variant HTTP cookies.
 * 
 * @author noneofyourbusiness
 * @since 1.0
 */
public abstract class VariantCookie {

	private String name = null;
	private String value = null;
	
	abstract protected int getMaxAge();
	
	/**
	 * New cookie.
	 */
	protected VariantCookie(String name) {
		this.name = name;
	}

	/**
	 * Existing from request.
	 */
	protected VariantCookie(String name, HttpServletRequest request) {
		this.name = name;
		for (Cookie c: request.getCookies()) {
			if (c.getName().equals(name)) {
				this.value = c.getValue();
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	public String getValue() {
		return value;
	}

	/**
	 * 
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * 
	 * @param response
	 */
	public void send(HttpServletResponse response) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath("/");
		cookie.setHttpOnly(false);  // We want varaint.js to have access.
		cookie.setMaxAge(getMaxAge());
		response.addCookie(cookie);
	}
}
