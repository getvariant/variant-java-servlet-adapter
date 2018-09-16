package com.variant.client.servlet.test;


import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;

import com.variant.client.servlet.ServletConnection;
import com.variant.client.servlet.ServletSession;
import com.variant.client.servlet.ServletStateRequest;
import com.variant.core.schema.Schema;
import com.variant.core.schema.State;
import com.variant.core.util.Tuples.Tripple;

public class ServletTargetingTest extends ServletClientTestWithServer {


	/**
	 * No session ID, but have tracker cookie.
	 * 
	 * @throws Exception
	 */
	@org.junit.Test
	public void noSIDwithTargeting() throws Exception {
		
		ServletConnection conn = servletClient.connectTo("big_covar_schema");
		
		Tripple<Long,String,String> targetingCookie = 
			new Tripple<Long,String,String>(System.currentTimeMillis(), "test2", "B");
		
		HttpServletRequest httpReq = mockHttpServletRequest(targetingCookie);

		ServletSession ssn = conn.getOrCreateSession(httpReq);		
		Schema schema = ssn.getSchema();
		State state1 = schema.getState("state1");		
		ServletStateRequest req = ssn.targetForState(state1);
		assertEquals("test2", req.getLiveExperience(schema.getTest("test2")).getTest().getName());
		assertEquals("B", req.getLiveExperience(schema.getTest("test2")).getName());		
	}

	// TODO: same with SID in tracker.
}
