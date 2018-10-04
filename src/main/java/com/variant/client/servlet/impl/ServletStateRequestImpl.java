package com.variant.client.servlet.impl;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import com.variant.client.StateRequest;
import com.variant.client.servlet.ServletSession;
import com.variant.client.servlet.ServletStateRequest;
import com.variant.client.servlet.ServletVariantException;
import com.variant.core.StateRequestStatus;
import com.variant.core.TraceEvent;
import com.variant.core.schema.State;
import com.variant.core.schema.StateVariant;
import com.variant.core.schema.Variation;
import com.variant.core.schema.Variation.Experience;

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
		
		if (bareRequest == null) throw new ServletVariantException("Bare state request cannot be null");
		if (wrapSession == null) throw new ServletVariantException("Servlet session cannot be null");
		this.bareRequest = bareRequest;
		this.wrapSession = wrapSession;
	}
	
	@Override
	public void commit(Object... userData) {
		commit((HttpServletResponse)userData[0]);
	}

	@Override
	public void commit(HttpServletResponse resp) {
		bareRequest.commit(resp);
	}

	@Override
	public void fail(Object... userData) {
		fail((HttpServletResponse)userData[0]);
	}

	@Override
	public void fail(HttpServletResponse resp) {
		bareRequest.fail(resp);
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
	public Optional<StateVariant> getResolvedStateVariant() {
		return bareRequest.getResolvedStateVariant();
	}


	@Override
	public Set<Experience> getLiveExperiences() {
		return bareRequest.getLiveExperiences();
	}

	@Override
	public Optional<Experience> getLiveExperience(Variation test) {
		return bareRequest.getLiveExperience(test);
	}

	@Override
	public TraceEvent getStateVisitedEvent() {
		return bareRequest.getStateVisitedEvent();
	}

	@Override
	public StateRequestStatus getStatus() {
		return bareRequest.getStatus();
	}

	@Override
	public Map<String, String> getResolvedParameters() {
		return bareRequest.getResolvedParameters();
	}

	// ---------------------------------------------------------------------------------------------//
	//                                         PUBLIC EXT                                           //
	// ---------------------------------------------------------------------------------------------//

}
