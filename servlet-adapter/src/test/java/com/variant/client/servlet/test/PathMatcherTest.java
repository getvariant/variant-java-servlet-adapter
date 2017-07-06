package com.variant.client.servlet.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.variant.client.StateSelectorByRequestPath;

public class PathMatcherTest {

	@Test
	public void matcherTest() {
		
		assertTrue(StateSelectorByRequestPath.match("/foo", "/foo/"));
		assertTrue(StateSelectorByRequestPath.match("/foo", "/foo"));
		assertTrue(StateSelectorByRequestPath.match("/foo/", "/foo/"));
		assertTrue(StateSelectorByRequestPath.match("/foo", "/foo"));
		assertTrue(StateSelectorByRequestPath.match("/foo/", "/foo"));
		assertTrue(StateSelectorByRequestPath.match("/foo//", "/foo"));
		assertTrue(StateSelectorByRequestPath.match("/foo//", "/foo/"));
		assertFalse(StateSelectorByRequestPath.match("/foo/", "/foo/bar"));
		assertTrue(StateSelectorByRequestPath.match("/~foo/", "/foo/"));
		assertTrue(StateSelectorByRequestPath.match("/~f(o*)/", "/foo/"));
		assertTrue(StateSelectorByRequestPath.match("//", "/foo/"));
		assertTrue(StateSelectorByRequestPath.match("/~.*/", "/foo/"));
		assertTrue(StateSelectorByRequestPath.match("///", "/foo/"));
		assertTrue(StateSelectorByRequestPath.match("/foo//", "/foo/"));
		assertFalse(StateSelectorByRequestPath.match("//foo/", "/foo/"));
		
		assertTrue(StateSelectorByRequestPath.match("/foo/bar", "/foo/bar/"));
		assertTrue(StateSelectorByRequestPath.match("/foo/bar/", "/foo/bar/"));	
		assertTrue(StateSelectorByRequestPath.match("/foo/bar", "/foo/bar"));
		assertFalse(StateSelectorByRequestPath.match("/foo/bar/x", "/foo/bar"));
		assertTrue(StateSelectorByRequestPath.match("//bar/", "/foo/bar/"));		
		assertTrue(StateSelectorByRequestPath.match("//bar/", "/foo/bar/"));		
		assertTrue(StateSelectorByRequestPath.match("/foo//", "/foo/bar/"));		
		assertTrue(StateSelectorByRequestPath.match("///", "/foo/bar/"));		
		assertFalse(StateSelectorByRequestPath.match("//", "/foo/bar/"));		
		assertTrue(StateSelectorByRequestPath.match("/~f(o*)/~b(ar)?", "/foo/bar/"));		
		assertFalse(StateSelectorByRequestPath.match("/~f(o*)/b(ar)?", "/foo/bar/"));		
		assertTrue(StateSelectorByRequestPath.match("/~f(o*)/~b(ar)?/", "/foo/bar/"));	
		assertTrue(StateSelectorByRequestPath.match("/~f(o*)/~b(ar)?//", "/foo/bar/"));	
		assertTrue(StateSelectorByRequestPath.match("/~f(o*)/~b(ar)?///", "/foo/bar/"));	
		
		assertTrue(StateSelectorByRequestPath.match("/petclinic/owners/~\\d+/", "/petclinic/owners/11"));
	}
}
