package com.variant.client.servlet.demo;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.variant.client.Session;
import com.variant.client.servlet.VariantFilter;

public class PetclinicVariantFilter extends VariantFilter {
	
	@Override
	protected void sessionObtained(ServletRequest request, ServletResponse response, Session session) {
		
		super.sessionObtained(request, response, session);
        session.setAttribute("user-agent", ((HttpServletRequest)request).getHeader("User-Agent"));
	}

}
