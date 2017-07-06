package com.variant.client.servlet.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import com.variant.client.Connection.Status;
import com.variant.client.ConnectionClosedException;
import com.variant.client.Session;
import com.variant.client.StateRequest;
import com.variant.client.mock.HttpServletResponseMock;
import com.variant.client.servlet.SessionIdTrackerHttpCookie;
import com.variant.client.servlet.TargetingTrackerHttpCookie;
import com.variant.client.servlet.ServletVariantClient;
import com.variant.client.servlet.ServletConnection;
import com.variant.client.servlet.ServletSession;
import com.variant.client.servlet.ServletStateRequest;
import com.variant.core.schema.Schema;
import com.variant.core.schema.State;
import com.variant.core.schema.Test;
import com.variant.core.util.Tuples.Pair;
import com.variant.core.util.VariantCollectionsUtils;

public class ServletSessionTest extends ServletClientBaseTest {

	//private static Random rand = new Random(System.currentTimeMillis());
	private static final ServletVariantClient servletClient = ServletVariantClient.Factory.getInstance();
	
	/**
	 * Test bare and servlet signatures of getSession()
	 * for the case when there's no session ID in the tracker.
	 * 
	 * @throws Exception
	 */
	@org.junit.Test
	public void getSessionNoTrackerTest() throws Exception {
		
		final ServletConnection conn = servletClient.getConnection("big_covar_schema");
		assertEquals(ServletConnection.Status.OPEN, conn.getStatus());
		
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
		
		conn.close();
		assertEquals(Status.CLOSED_BY_CLIENT,conn.getStatus());
	}
	
	/**
	 * Test bare and servlet signatures of getSession()
	 * for the case when is a session ID in the tracker.
	 * 
	 * @throws Exception
	 */
	@org.junit.Test
	public void getSessionWithTrackerTest() throws Exception {

		final ServletConnection conn = servletClient.getConnection("big_covar_schema");
		assertEquals(ServletConnection.Status.OPEN, conn.getStatus());

		// Servlet signatures
		String sid = newSid();
		final HttpServletRequest httpReq = mockHttpServletRequest(sid);
		
		Session ssn1 = conn.getSession(httpReq);
		assertNull(ssn1);
		
		ssn1 = conn.getOrCreateSession(httpReq);
		assertNotNull(ssn1);

		ServletSession ssn2 = conn.getSession(httpReq);
		assertNotNull(ssn2);  // ID in tracker => session already created by previous call.
		
		ssn2 = conn.getOrCreateSession(httpReq);
		assertNotNull(ssn2);
		assertEquals(ssn1, ssn2);

		// Bare signatures		
		sid = newSid();
		final HttpServletRequest httpReq2 = mockHttpServletRequest(sid);

		ssn1 = conn.getSession((Object) httpReq2);
		assertNull(ssn1);
		
		ssn1 = conn.getOrCreateSession((Object)httpReq2);
		assertNotNull(ssn1);
		
		ssn2 = conn.getOrCreateSession((Object)httpReq2);
		assertNotNull(ssn2);
		assertEquals(ssn1, ssn2);

		ssn2 = conn.getSession((Object)httpReq2);
		assertNotNull(ssn2);
		assertEquals(ssn1, ssn2);

		ssn2 = conn.getSessionById(sid);
		assertNotNull(ssn2);
		assertEquals(ssn1, ssn2);

		conn.close();
		assertEquals(Status.CLOSED_BY_CLIENT,conn.getStatus());

		//
		conn.close();
		assertEquals(Status.CLOSED_BY_CLIENT,conn.getStatus());

	}
	
	/**
	 * No session ID in cookie.
	 * 
	 * @throws Exception
	 */
	@org.junit.Test
	public void fullStateRequestNoIdTracker() throws Exception {
		
		final ServletConnection conn = servletClient.getConnection("big_covar_schema");
		assertEquals(ServletConnection.Status.OPEN, conn.getStatus());
		
		Schema schema = conn.getSchema();
		assertNotNull(schema);
		
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
		assertNotEquals(ssn1, ssn2);  // getSession is not idempotent, until committed.  
		
		State state1 = schema.getState("state1");		
		ServletStateRequest varReq = ssn2.targetForState(state1);
		assertEquals(state1, varReq.getState());
		assertEquals(varReq, ssn2.getStateRequest());
		assertEqualAsSets(
				VariantCollectionsUtils.pairsToMap(new Pair<State,Integer>(state1, 1)), 
				ssn2.getTraversedStates());
		
		Collection<Test> expectedTests = VariantCollectionsUtils.list(
				schema.getTest("test2"), 
				schema.getTest("test3"), 
				schema.getTest("test4"), 
				schema.getTest("test5"), 
				schema.getTest("test6"));
		
		assertEqualAsSets(expectedTests, ssn2.getTraversedTests());

		assertTrue(varReq.commit(httpResp));
		assertTrue(varReq.isCommitted());

		// commit() has added the targeting tracker cookie.
		assertEquals(2, httpResp.getCookies().length);
		assertEquals(ssn2, varReq.getSession());
		assertEquals(varReq, ssn2.getStateRequest());
		assertEquals(ssn2.getId(), httpResp.getCookie(SessionIdTrackerHttpCookie.COOKIE_NAME).getValue());
		for (Test test: expectedTests)
			assertMatches(".*\\." + test.getName() + "\\..*", httpResp.getCookie(TargetingTrackerHttpCookie.COOKIE_NAME).getValue());
		
		// The session shouldn't have changed after commit.
		assertEqualAsSets(
				VariantCollectionsUtils.pairsToMap(new Pair<State,Integer>(state1, 1)), 
				ssn2.getTraversedStates());

		assertEqualAsSets(expectedTests, ssn2.getTraversedTests());

		// Commit should have saved the session.
		httpReq = mockHttpServletRequest(httpResp);
		ServletSession ssn3 = conn.getSession(httpReq);
		assertTrue(varReq.isCommitted());
		assertEquals(ssn3, ssn2);
		assertEqualAsSets(
				VariantCollectionsUtils.pairsToMap(new Pair<State,Integer>(state1, 1)), 
				ssn2.getTraversedStates());

		assertEqualAsSets(expectedTests, ssn3.getTraversedTests());		

		// should be a no-op.
		assertFalse(varReq.commit(httpResp));
		
		conn.close();
		assertEquals(Status.CLOSED_BY_CLIENT,conn.getStatus());
	}
	
	/**
	 * Session ID in cookie.
	 * 
	 * @throws Exception
	 */
	@org.junit.Test
	public void fullStateRequestWithIdTracker() throws Exception {
		
		final ServletConnection conn = servletClient.getConnection("big_covar_schema");
		assertEquals(ServletConnection.Status.OPEN, conn.getStatus());
		
		Schema schema = conn.getSchema();
		assertNotNull(schema);

		String sid = newSid();
		HttpServletRequest httpReq = mockHttpServletRequest(sid);
		HttpServletResponseMock httpResp = mockHttpServletResponse();

		ServletSession ssn1 = conn.getSession(httpReq);
		assertNull(ssn1);

		ssn1 = conn.getOrCreateSession(httpReq);
		assertNotNull(ssn1);
		assertEquals(sid, ssn1.getId());
		assertNull(ssn1.getStateRequest());		
		assertEquals(0, ssn1.getTraversedStates().size());
		assertEquals(0, ssn1.getTraversedTests().size());
		assertEquals(0, httpResp.getCookies().length);  // We didn't drop the ssnid cookie, because there was one in request.
		
		State state2 = schema.getState("state2");		
		StateRequest varReq = ssn1.targetForState(state2);
		assertEquals(ssn1, varReq.getSession());
		assertEqualAsSets(
				VariantCollectionsUtils.pairsToMap(new Pair<State,Integer>(state2, 1)), 
				ssn1.getTraversedStates());
		
		Collection<Test> expectedTests = VariantCollectionsUtils.list(
				schema.getTest("test1"), 
				schema.getTest("test2"), 
				schema.getTest("test3"), 
				schema.getTest("test4"), 
				schema.getTest("test5"), 
				schema.getTest("test6"));

		assertEqualAsSets(expectedTests, ssn1.getTraversedTests());		

		assertTrue(varReq.commit(httpResp));
		
		// Create a new HTTP request with the same VRNT-SSNID cookie.  Should fetch the same bare session.
		HttpServletRequest httpReq2 = mockHttpServletRequest(sid);
		ServletSession ssn2 = conn.getSession(httpReq2);
		assertEquals(ssn2, varReq.getSession());
		assertEquals(ssn2.getStateRequest(), varReq);
		assertEqualAsSets(
				VariantCollectionsUtils.pairsToMap(new Pair<State,Integer>(state2, 1)), 
				ssn1.getTraversedStates());
		assertEqualAsSets(expectedTests, ssn1.getTraversedTests());
		
		conn.close();
		assertEquals(Status.CLOSED_BY_CLIENT,conn.getStatus());
	}
	
	/**
	 * Content of SID cookie changes.
	 * 
	 * @throws Exception
	 */
	@org.junit.Test
	public void cookieForgedTest() throws Exception {
		
		final ServletConnection conn = servletClient.getConnection("big_covar_schema");
		assertEquals(ServletConnection.Status.OPEN, conn.getStatus());
		
		Schema schema = conn.getSchema();
		assertNotNull(schema);

		State state3 = schema.getState("state3");
		assertNotNull(state3);
		
		// Request 1: new session.
		HttpServletRequest httpReq = mockHttpServletRequest();
		ServletSession ssn1 = conn.getOrCreateSession(httpReq);
		assertNotNull(ssn1);
		String sid1 = ssn1.getId();
		ServletStateRequest req = ssn1.targetForState(state3);
		HttpServletResponseMock resp = mockHttpServletResponse();
		req.commit(resp);
		
		// Request 2: Same SID from cookie.
		httpReq = mockHttpServletRequest(resp);
		ServletSession ssn2 = conn.getSession(httpReq);
		assertEquals(ssn1, ssn2);
		assertEquals(sid1, ssn2.getId());
		
		// Request 3: SID cookie removed
		httpReq = mockHttpServletRequest();
		ssn2 = conn.getSession(httpReq);
		assertNull(ssn2);

		// Request 4: New SID in cookie
		String sid2 = newSid();
		assertNotEquals(sid1, sid2);
		httpReq = mockHttpServletRequest(sid2);
		ssn2 = conn.getSession(httpReq);
		assertNull(ssn2);
		ssn2 = conn.getOrCreateSession(httpReq);
		assertNotNull(ssn2);
		assertEquals(sid2, ssn2.getId());
		
		conn.close();
		assertEquals(Status.CLOSED_BY_CLIENT,conn.getStatus());
	}
	
	/**
	 */
	@org.junit.Test
	public void connClosedTest() throws Exception {
		
		final ServletConnection conn = servletClient.getConnection("big_covar_schema");
		assertEquals(ServletConnection.Status.OPEN, conn.getStatus());

		Schema schema = conn.getSchema();
		assertNotNull(schema);

		final State state2 = schema.getState("state2");
		assertNotNull(state2);

		String sid1 = newSid();
		HttpServletRequest httpReq1 = mockHttpServletRequest(sid1);
		ServletSession ssn1 = conn.getOrCreateSession(httpReq1);
		assertNotNull(ssn1);
		final ServletStateRequest varReq1 = ssn1.targetForState(state2);
		final HttpServletResponseMock resp1 = mockHttpServletResponse();
		
		String sid2 = newSid();
		HttpServletRequest httpReq2 = mockHttpServletRequest(sid2);
		final ServletSession ssn2 = conn.getOrCreateSession(httpReq2);
		assertNotNull(ssn2);

		conn.close();
		assertEquals(Status.CLOSED_BY_CLIENT,conn.getStatus());
		
		new ServletClientExceptionIntercepter() {
			@Override public void toRun() { 
				varReq1.commit(resp1);
			}
		}.assertThrown(ConnectionClosedException.class);

		new ServletClientExceptionIntercepter() {
			@Override public void toRun() { 
				ssn2.targetForState(state2);
			}
		}.assertThrown(ConnectionClosedException.class);		
	}

}
