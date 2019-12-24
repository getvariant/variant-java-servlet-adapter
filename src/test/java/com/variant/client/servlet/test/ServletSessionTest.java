package com.variant.client.servlet.test;


import static org.junit.Assert.*;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import com.variant.client.Session;
import com.variant.client.StateRequest;
import com.variant.client.servlet.ServletConnection;
import com.variant.client.servlet.ServletSession;
import com.variant.client.servlet.ServletStateRequest;
import com.variant.client.servlet.SessionIdTrackerHttpCookie;
import com.variant.client.servlet.TargetingTrackerHttpCookie;
import com.variant.client.servlet.mock.HttpServletResponseMock;
import com.variant.client.StateRequest.Status;
import com.variant.share.schema.Schema;
import com.variant.share.schema.State;
import com.variant.share.schema.Variation;
import com.variant.share.util.CollectionsUtils;
import com.variant.share.util.Tuples.Pair;

public class ServletSessionTest extends ServletClientTestWithServer {


	/**
	 * Test bare and servlet signatures of getSession()
	 * for the case when there's no session ID in the tracker.
	 * 
	 * @throws Exception
	 */
	@org.junit.Test
	public void getSessionNoTrackerTest() throws Exception {
		
		final ServletConnection conn = servletClient.connectTo("variant://localhost:5377/monstrosity");
		assertNotNull(conn);
		
		// Servlet signatures
		final HttpServletRequest httpReq = mockHttpServletRequest();
		
		assertFalse(conn.getSession(httpReq).isPresent());
		
		Session ssn1 = conn.getOrCreateSession(httpReq);
		assertNotNull(ssn1);

		assertFalse(conn.getSession(httpReq).isPresent());
		
		Session ssn2 = conn.getOrCreateSession(httpReq);
		assertNotNull(ssn2);
		assertNotEquals(ssn1, ssn2);

		// Bare signatures		
		assertFalse(conn.getSession((Object) httpReq).isPresent());
		
		ssn1 = conn.getOrCreateSession((Object)httpReq);
		assertNotNull(ssn1);

		new ServletClientExceptionIntercepter() {
			@Override public void toRun() { 
				conn.getSession(new Object());
			}
		}.assertThrown();

		new ServletClientExceptionIntercepter() {
			@Override public void toRun() { 
				conn.getOrCreateSession(new Object());
			}
		}.assertThrown();
		
	}
	
	/**
	 * Test bare and servlet signatures of getSession()
	 * for the case when is a session ID in the tracker.
	 * 
	 * @throws Exception
	 */
	@org.junit.Test
	public void getSessionWithTrackerTest() throws Exception {

		final ServletConnection conn = servletClient.connectTo("variant://localhost:5377/monstrosity");

		// Servlet signatures
		HttpServletRequest httpReq = mockHttpServletRequest(newSid());
		
		assertFalse(conn.getSession(httpReq).isPresent());
		
		ServletSession ssn1 = conn.getOrCreateSession(httpReq);
		assertNotNull(ssn1);

		ServletSession ssn2 = conn.getSession(mockHttpServletRequest(ssn1.getId())).get();
		assertEquals(ssn1.getId(), ssn2.getId());

		// Bare signatures		
		httpReq = mockHttpServletRequest(newSid());

		assertFalse(conn.getSession((Object) httpReq).isPresent());
		
		ssn1 = conn.getOrCreateSession((Object)httpReq);
		assertNotNull(ssn1);
		
		httpReq = mockHttpServletRequest(ssn1.getId());
		
		ssn2 = conn.getOrCreateSession((Object)httpReq);
		assertNotNull(ssn2);
		assertEquals(ssn1.getId(), ssn2.getId());

		ssn2 = conn.getSession((Object)httpReq).get();
		assertNotNull(ssn2);
		assertEquals(ssn1.getId(), ssn2.getId());

		ssn2 = conn.getSessionById(ssn1.getId()).get();
		assertNotNull(ssn2);
		assertEquals(ssn1.getId(), ssn2.getId());

	}
	
	/**
	 * No session ID in cookie.
	 * 
	 * @throws Exception
	 */
	@org.junit.Test
	public void fullStateRequestNoIdTracker() throws Exception {
		
		final ServletConnection conn = servletClient.connectTo("variant://localhost:5377/monstrosity");
		
		HttpServletRequest httpReq = mockHttpServletRequest();
		HttpServletResponseMock httpResp = mockHttpServletResponse();

		ServletSession ssn1 = conn.getOrCreateSession(httpReq);
		assertNotNull(ssn1);
		assertNotNull(ssn1.getId());
		assertEquals(conn, ssn1.getConnection());
		assertFalse(ssn1.getStateRequest().isPresent());		
		assertEquals(0, ssn1.getTraversedStates().size());
		assertEquals(0, ssn1.getTraversedVariations().size());
		assertEquals(0, httpResp.getCookies().length);
		
		ServletSession ssn2 = conn.getOrCreateSession(httpReq);
		assertNotEquals(ssn1, ssn2);  
		
		Schema schema = ssn2.getSchema();
		State state1 = schema.getState("state1").get();		
		ServletStateRequest req2 = ssn2.targetForState(state1);
		assertEquals(state1, req2.getState());
		assertEquals(ssn2, req2.getSession());
		assertEquals(req2, ssn2.getStateRequest().get());

		assertTrue(CollectionsUtils.equalAsSets(
				CollectionsUtils.pairsToMap(new Pair<State,Integer>(state1, 1)), 
				ssn2.getTraversedStates()));
		
		Collection<Variation> expectedTests = CollectionsUtils.list(
				schema.getVariation("test2").get(), 
				schema.getVariation("test3").get(), 
				schema.getVariation("test5").get(), 
				schema.getVariation("test6").get());
		
		assertTrue(CollectionsUtils.equalAsSets(expectedTests, ssn2.getTraversedVariations()));

		req2.commit(httpResp);
		assertEquals(Status.Committed, req2.getStatus());

		// commit() has added the targeting tracker cookie.
		assertEquals(2, httpResp.getCookies().length);
		assertEquals(ssn2.getId(), httpResp.getCookie(SessionIdTrackerHttpCookie.COOKIE_NAME).getValue());
		for (Variation test: expectedTests)
			assertMatches(".*\\." + test.getName() + "\\..*", httpResp.getCookie(TargetingTrackerHttpCookie.COOKIE_NAME).getValue());
		
		// The session shouldn't have changed after commit.
		assertTrue(CollectionsUtils.equalAsSets(
				CollectionsUtils.pairsToMap(new Pair<State,Integer>(state1, 1)), 
				ssn2.getTraversedStates()));

		assertTrue(CollectionsUtils.equalAsSets(expectedTests, ssn2.getTraversedVariations()));

		// Commit should have saved the session.
		httpReq = mockHttpServletRequest(httpResp);
		ServletSession ssn3 = conn.getSession(httpReq).get();
		assertEquals(Status.Committed, req2.getStatus());
		assertTrue(CollectionsUtils.equalAsSets(
				CollectionsUtils.pairsToMap(new Pair<State,Integer>(state1, 1)), 
				ssn2.getTraversedStates()));

		assertTrue(CollectionsUtils.equalAsSets(expectedTests, ssn3.getTraversedVariations()));		

		// should be a no-op.
		req2.commit(httpResp);
		
	}
	
	/**
	 * Session ID in cookie.
	 * 
	 * @throws Exception
	 */
	@org.junit.Test
	public void fullStateRequestWithIdTracker() throws Exception {
		
		final ServletConnection conn = servletClient.connectTo("variant://localhost:5377/monstrosity");

		HttpServletRequest httpReq = mockHttpServletRequest(newSid());
		HttpServletResponseMock httpResp = mockHttpServletResponse();

		assertFalse(conn.getSession(httpReq).isPresent());

		ServletSession ssn1 = conn.getOrCreateSession(httpReq);
		assertNotNull(ssn1);
		assertFalse(ssn1.getStateRequest().isPresent());		
		assertEquals(0, ssn1.getTraversedStates().size());
		assertEquals(0, ssn1.getTraversedVariations().size());
		assertEquals(0, httpResp.getCookies().length);  // We didn't drop the ssnid cookie, because there was one in request.
		
		Schema schema = ssn1.getSchema();
		State state2 = schema.getState("state2").get();		
		StateRequest varReq = ssn1.targetForState(state2);
		assertEquals(ssn1, varReq.getSession());
		assertTrue(CollectionsUtils.equalAsSets(
				CollectionsUtils.pairsToMap(new Pair<State,Integer>(state2, 1)), 
				ssn1.getTraversedStates()));
		
		Collection<Variation> expectedTests = CollectionsUtils.list(
				schema.getVariation("test1").get(), 
				schema.getVariation("test2").get(), 
				schema.getVariation("test4").get(), 
				schema.getVariation("test5").get(), 
				schema.getVariation("test6").get());

		assertTrue(CollectionsUtils.equalAsSets(expectedTests, ssn1.getTraversedVariations()));		

		varReq.commit(httpResp);
		assertEquals(Status.Committed, varReq.getStatus());
		
		// Create a new HTTP request with the same VRNT-SSNID cookie.  Should fetch the same bare session.
		HttpServletRequest httpReq2 = mockHttpServletRequest(ssn1.getId());
		ServletSession ssn2 = conn.getSession(httpReq2).get();
		assertFalse(ssn2.getStateRequest().isPresent());
		assertTrue(CollectionsUtils.equalAsSets(
				CollectionsUtils.pairsToMap(new Pair<State,Integer>(state2, 1)), 
				ssn1.getTraversedStates()));
		assertTrue(CollectionsUtils.equalAsSets(expectedTests, ssn1.getTraversedVariations()));
		
	}
	
	/**
	 * Content of SID cookie changes.
	 * 
	 * @throws Exception
	 */
	@org.junit.Test
	public void cookieForgedTest() throws Exception {
		
		final ServletConnection conn = servletClient.connectTo("variant://localhost:5377/monstrosity");

		
		// Request 1: new session.
		HttpServletRequest httpReq = mockHttpServletRequest();
		ServletSession ssn1 = conn.getOrCreateSession(httpReq);
		assertNotNull(ssn1);
		Schema schema = ssn1.getSchema();
		State state3 = schema.getState("state3").get();
		assertNotNull(state3);
		String sid1 = ssn1.getId();
		ServletStateRequest req = ssn1.targetForState(state3);
		HttpServletResponseMock resp = mockHttpServletResponse();
		req.commit(resp);
		
		// Request 2: Same SID from cookie.
		httpReq = mockHttpServletRequest(resp);
		ServletSession ssn2 = conn.getSession(httpReq).get();
		assertEquals(sid1, ssn2.getId());
		
		// Request 3: SID cookie removed
		httpReq = mockHttpServletRequest();
		assertFalse(conn.getSession(httpReq).isPresent());
		
	}
	
	/**
	 */
	@org.junit.Test
	public void connClosedByClientTest() throws Exception {
		
		final ServletConnection conn = servletClient.connectTo("variant://localhost:5377/monstrosity");


		String sid1 = newSid();
		HttpServletRequest httpReq1 = mockHttpServletRequest(sid1);
		ServletSession ssn1 = conn.getOrCreateSession(httpReq1);
		assertNotNull(ssn1);
		Schema schema = ssn1.getSchema();
		final State state2 = schema.getState("state2").get();
		assertNotNull(state2);
		
		String sid2 = newSid();
		HttpServletRequest httpReq2 = mockHttpServletRequest(sid2);
		final ServletSession ssn2 = conn.getOrCreateSession(httpReq2);
		assertNotNull(ssn2);
	}
}
