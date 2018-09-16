package com.variant.client.servlet.mock;

import java.util.ArrayList;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * HttpServletResponse partial mock adds state and methods to get to it.
 */
public abstract class HttpServletResponseMock implements HttpServletResponse {
	
	// Note that Mockito never instantiates this class, so initializing cookies below
	// won't do any good. 
	private ArrayList<Cookie> cookies = null;
	
	@Override public void addCookie(Cookie cookie) {
		if (cookies == null) cookies = new ArrayList<Cookie>();
		cookies.add(cookie);
	}
	
	/**
	 */
	public Cookie[] getCookies() {
		return cookies == null ? new Cookie[0] : cookies.toArray(new Cookie[cookies.size()]); 
	}
	
	/**
	 * 
	 */
	public Cookie getCookie(String name) {
		if (cookies == null) return null;
		else for (Cookie cookie: cookies) if (cookie.getName().equals(name)) return cookie;
		return null;
	}

}
