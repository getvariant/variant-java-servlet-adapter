/**
 * <p>Servlet Adapter for the "bare" Variant Java Client, providing greatly simplified integration
 * for host applications written on top of the Java Servlet API.
 * 
 * <p>A wrapper API around the "bare" Java Client. Replaces the environment-dependent method signatures
 * with their environment-aware counterparts, e.g. 
 * {@link com.variant.client.servlet.ServletConnection#getSession(javax.servlet.http.HttpServletRequest)}
 * in place of the bare {@link com.variant.client.Connection#getSession(Object...)}.
 * 
 * @since 1.0
 */
package com.variant.client.servlet;
