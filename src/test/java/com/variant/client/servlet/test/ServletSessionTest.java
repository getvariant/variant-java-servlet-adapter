package com.variant.client.servlet.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
import com.variant.core.StateRequestStatus;
import com.variant.core.schema.Schema;
import com.variant.core.schema.State;
import com.variant.core.schema.Test;
import com.variant.core.util.CollectionsUtils;
import com.variant.core.util.Tuples.Pair;

public class ServletSessionTest extends ServletClientTestWithServer {


	/**
	 * Test bare and servlet signatures of getSession()
	 * for the case when there's no session ID in the tracker.
	 * 
	 * @throws Exception
	 */
	@org.junit.Test
	public void getSessionNoTrackerTest() throws Exception {
		
		final ServletConnection conn = servletClient.connectTo("big_covar_schema");
		assertNotNull(conn);
		
		// Servlet signatures
		final HttpServletRequest httpReq = mockHttpServletRequest();
		
		ServletSession ssn1 = conn.getSession(httpReq);
		assertNull(ssn1);
		
		ssn1 = conn.getOrCreateSession(httpReq);
		assertNotNull(ssn1);

		ServletSession ssn2 = conn.getSession(httpReq);
		assertNull(ssn2);
		
		ssn2 = conn.getOrCreateSession(httpReq);
		assertNotNull(ssn2);
		assertNotEquals(ssn1, ssn2);

		// Bare signatures		
		ssn1 = conn.getSession((Object) httpReq);
		assertNull(ssn1);
		
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

		final ServletConnection conn = servletClient.connectTo("big_covar_schema");

		// Servlet signatures
		HttpServletRequest httpReq = mockHttpServletRequest(newSid());
		
		Session ssn1 = conn.getSession(httpReq);
		assertNull(ssn1);
		
		ssn1 = conn.getOrCreateSession(httpReq);
		assertNotNull(ssn1);

		ServletSession ssn2 = conn.getSession(mockHttpServletRequest(ssn1.getId()));
		assertNotNull(ssn2); 
		assertEquals(ssn1.getId(), ssn2.getId());

		// Bare signatures		
		httpReq = mockHttpServletRequest(newSid());

		ssn1 = conn.getSession((Object) httpReq);
		assertNull(ssn1);
		
		ssn1 = conn.getOrCreateSession((Object)httpReq);
		assertNotNull(ssn1);
		
		httpReq = mockHttpServletRequest(ssn1.getId());
		
		ssn2 = conn.getOrCreateSession((Object)httpReq);
		assertNotNull(ssn2);
		assertEquals(ssn1.getId(), ssn2.getId());

		ssn2 = conn.getSession((Object)httpReq);
		assertNotNull(ssn2);
		assertEquals(ssn1.getId(), ssn2.getId());

		ssn2 = conn.getSessionById(ssn1.getId());
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
		
		final ServletConnection conn = servletClient.connectTo("big_covar_schema");
		
		HttpServletRequest httpReq = mockHttpServletRequest();
		HttpServletResponseMock httpResp = mockHttpServletResponse();

		ServletSession ssn1 = conn.getOrCreateSession(httpReq);
		assertNotNull(ssn1);
		assertNotNull(ssn1.getId());
		assertEquals(conn, ssn1.getConnection());
		assertNull(ssn1.getStateRequest());		
		assertEquals(0, ssn1.getTraversedStates().size());
		assertEquals(0, ssn1.getTraversedTests().size());
		assertEquals(0, httpResp.getCookies().length);
		
		ServletSession ssn2 = conn.getOrCreateSession(httpReq);
		assertNotEquals(ssn1, ssn2);  
		
		Schema schema = ssn2.getSchema();
		State state1 = schema.getState("state1");		
		ServletStateRequest req2 = ssn2.targetForState(state1);
		assertEquals(state1, req2.getState());
		assertEquals(ssn2, req2.getSession());
		assertEquals(req2, ssn2.getStateRequest());

		assertEqualAsSets(
				CollectionsUtils.pairsToMap(new Pair<State,Integer>(state1, 1)), 
				ssn2.getTraversedStates());
		
		Collection<Test> expectedTests = CollectionsUtils.list(
				schema.getTest("test2"), 
				schema.getTest("test3"), 
				schema.getTest("test4"), 
				schema.getTest("test5"), 
				schema.getTest("test6"));
		
		assertEqualAsSets(expectedTests, ssn2.getTraversedTests());

		req2.commit(httpResp);
		assertEquals(StateRequestStatus.Committed, req2.getStatus());

		// commit() has added the targeting tracker cookie.
		assertEquals(2, httpResp.getCookies().length);
		assertEquals(ssn2.getId(), httpResp.getCookie(SessionIdTrackerHttpCookie.COOKIE_NAME).getValue());
		for (Test test: expectedTests)
			assertMatches(".*\\." + test.getName() + "\\..*", httpResp.getCookie(TargetingTrackerHttpCookie.COOKIE_NAME).getValue());
		
		// The session shouldn't have changed after commit.
		assertEqualAsSets(
				CollectionsUtils.pairsToMap(new Pair<State,Integer>(state1, 1)), 
				ssn2.getTraversedStates());

		assertEqualAsSets(expectedTests, ssn2.getTraversedTests());

		// Commit should have saved the session.
		httpReq = mockHttpServletRequest(httpResp);
		ServletSession ssn3 = conn.getSession(httpReq);
		assertEquals(StateRequestStatus.Committed, req2.getStatus());
		assertEqualAsSets(
				CollectionsUtils.pairsToMap(new Pair<State,Integer>(state1, 1)), 
				ssn2.getTraversedStates());

		assertEqualAsSets(expectedTests, ssn3.getTraversedTests());		

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
		
		final ServletConnection conn = servletClient.connectTo("big_covar_schema");

		HttpServletRequest httpReq = mockHttpServletRequest(newSid());
		HttpServletResponseMock httpResp = mockHttpServletResponse();

		ServletSession ssn1 = conn.getSession(httpReq);
		assertNull(ssn1);

		ssn1 = conn.getOrCreateSession(httpReq);
		assertNotNull(ssn1);
		assertNull(ssn1.getStateRequest());		
		assertEquals(0, ssn1.getTraversedStates().size());
		assertEquals(0, ssn1.getTraversedTests().size());
		assertEquals(0, httpResp.getCookies().length);  // We didn't drop the ssnid cookie, because there was one in request.
		
		Schema schema = ssn1.getSchema();
		State state2 = schema.getState("state2");		
		StateRequest varReq = ssn1.targetForState(state2);
		assertEquals(ssn1, varReq.getSession());
		assertEqualAsSets(
				CollectionsUtils.pairsToMap(new Pair<State,Integer>(state2, 1)), 
				ssn1.getTraversedStates());
		
		Collection<Test> expectedTests = CollectionsUtils.list(
				schema.getTest("test1"), 
				schema.getTest("test2"), 
				schema.getTest("test3"), 
				schema.getTest("test4"), 
				schema.getTest("test5"), 
				schema.getTest("test6"));

		assertEqualAsSets(expectedTests, ssn1.getTraversedTests());		

		varReq.commit(httpResp);
		assertEquals(StateRequestStatus.Committed, varReq.getStatus());
		
		// Create a new HTTP request with the same VRNT-SSNID cookie.  Should fetch the same bare session.
		HttpServletRequest httpReq2 = mockHttpServletRequest(ssn1.getId());
		ServletSession ssn2 = conn.getSession(httpReq2);
		assertNull(ssn2.getStateRequest());
		assertEqualAsSets(
				CollectionsUtils.pairsToMap(new Pair<State,Integer>(state2, 1)), 
				ssn1.getTraversedStates());
		assertEqualAsSets(expectedTests, ssn1.getTraversedTests());
		
	}
	
	/**
	 * Content of SID cookie changes.
	 * 
	 * @throws Exception
	 */
	@org.junit.Test
	public void cookieForgedTest() throws Exception {
		
		final ServletConnection conn = servletClient.connectTo("big_covar_schema");

		
		// Request 1: new session.
		HttpServletRequest httpReq = mockHttpServletRequest();
		ServletSession ssn1 = conn.getOrCreateSession(httpReq);
		assertNotNull(ssn1);
		Schema schema = ssn1.getSchema();
		State state3 = schema.getState("state3");
		assertNotNull(state3);
		String sid1 = ssn1.getId();
		ServletStateRequest req = ssn1.targetForState(state3);
		HttpServletResponseMock resp = mockHttpServletResponse();
		req.commit(resp);
		
		// Request 2: Same SID from cookie.
		httpReq = mockHttpServletRequest(resp);
		ServletSession ssn2 = conn.getSession(httpReq);
		assertEquals(sid1, ssn2.getId());
		
		// Request 3: SID cookie removed
		httpReq = mockHttpServletRequest();
		ssn2 = conn.getSession(httpReq);
		assertNull(ssn2);
		
	}
	
	/**
	 */
	@org.junit.Test
	public void connClosedByClientTest() throws Exception {
		
		final ServletConnection conn = servletClient.connectTo("big_covar_schema");


		String sid1 = newSid();
		HttpServletRequest httpReq1 = mockHttpServletRequest(sid1);
		ServletSession ssn1 = conn.getOrCreateSession(httpReq1);
		assertNotNull(ssn1);
		Schema schema = ssn1.getSchema();
		final State state2 = schema.getState("state2");
		assertNotNull(state2);
		
		String sid2 = newSid();
		HttpServletRequest httpReq2 = mockHttpServletRequest(sid2);
		final ServletSession ssn2 = conn.getOrCreateSession(httpReq2);
		assertNotNull(ssn2);
	}
}
