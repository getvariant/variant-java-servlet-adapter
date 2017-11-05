package com.variant.client.servlet.impl;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import com.variant.client.ClientException;
import com.variant.client.StateRequest;
import com.variant.client.servlet.ServletSession;
import com.variant.client.servlet.ServletStateRequest;
import com.variant.core.StateRequestStatus;
import com.variant.core.VariantEvent;
import com.variant.core.schema.State;
import com.variant.core.schema.StateVariant;
import com.variant.core.schema.Test;
import com.variant.core.schema.Test.Experience;

/**
 * <p>The implementation of {@link ServletStateRequest}.
 * Replaces bare client's {@link VariantCoreStateRequest#commit(Object...)} with the
 * servlet-aware signature {@link #commit(HttpServletResponse)}. 
 * 
 * @author Igor Urisman
 * @since 1.0
 */

public class ServletStateRequestImpl implements ServletStateRequest {

	private StateRequest bareRequest;
	private ServletSession wrapSession;
	
	// ---------------------------------------------------------------------------------------------//
	//                                      PUBLIC AUGMENTED                                        //
	// ---------------------------------------------------------------------------------------------//

	public ServletStateRequestImpl(ServletSession wrapSession, StateRequest bareRequest) {
		
		if (bareRequest == null) throw new ClientException.Internal("Bare state request cannot be null");
		if (wrapSession == null) throw new ClientException.Internal("Servlet session cannot be null");
		this.bareRequest = bareRequest;
		this.wrapSession = wrapSession;
	}
	
	@Override
	public boolean commit(Object... userData) {
		return commit((HttpServletResponse)userData[0]);
	}

	@Override
	public boolean commit(HttpServletResponse resp) {
		return bareRequest.commit(resp);
	}

	@Override
	public ServletSession getSession() {
		return wrapSession;
	}

	@Override
	public State getState() {
		return bareRequest.getState();
	}

	@Override
	public StateVariant getResolvedStateVariant() {
		return bareRequest.getResolvedStateVariant();
	}


	@Override
	public Set<Experience> getLiveExperiences() {
		return bareRequest.getLiveExperiences();
	}

	@Override
	public Experience getLiveExperience(Test test) {
		return bareRequest.getLiveExperience(test);
	}

	@Override
	public VariantEvent getStateVisitedEvent() {
		return bareRequest.getStateVisitedEvent();
	}

	@Override
	public boolean isCommitted() {
		return bareRequest.isCommitted();
	}

	@Override
	public Date getCreateDate() {
		return bareRequest.getCreateDate();
	}

	@Override
	public Map<String, String> getResolvedParameters() {
		return bareRequest.getResolvedParameters();
	}

	@Override
	public StateRequestStatus getStatus() {
		return bareRequest.getStatus();
	}

	@Override
	public void setStatus(StateRequestStatus status) {
		bareRequest.setStatus(status);
	}

	// ---------------------------------------------------------------------------------------------//
	//                                         PUBLIC EXT                                           //
	// ---------------------------------------------------------------------------------------------//

}