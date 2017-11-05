package com.variant.client.mock;

import java.util.HashMap;

import javax.servlet.http.HttpSession;

/**
 * HttpSession partial mock adds state and methods to get to it.
 */
public abstract class HttpSessionMock implements HttpSession {
	
	private HashMap<String, Object> attributes = null;
	
	@Override public void setAttribute(String key, Object value) {
		if (attributes == null) attributes = new HashMap<String, Object>();
		attributes.put(key, value);
	}
	
	@Override public Object getAttribute(String key) {
		return attributes == null ? null : attributes.get(key);
	}
}
