package com.variant.client.mock;

import java.util.ArrayList;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * HttpServletResponse partial mock adds state and methods to get to it.
 */
public abstract class HttpServletResponseMock implements HttpServletResponse {
	private ArrayList<Cookie> cookies = null;
	
	@Override public void addCookie(Cookie cookie) {
		if (cookies == null) cookies = new ArrayList<Cookie>();
		cookies.add(cookie);
	}
	
	/**
	 * 
	 * @return
	 */
	public Cookie[] getCookies() { 
		return cookies == null ? new Cookie[0] : cookies.toArray(new Cookie[cookies.size()]); 
	}
	
	/**
	 * 
	 * @return
	 */
	public Cookie getCookie(String name) { 
		for (Cookie cookie: cookies) {
			if (cookie.getName().equals(name)) return cookie;
		}
		return null;
	}

}
