/**
 * <p>Servlet Adapter for the "bare" Variant Java Client, providing greatly simplified integration
 * for host applications written on top of the Java Servlet API.
 * 
 * <p>Servlet adapter wraps the Variant Java client with a higher level API, which re-writes all 
 * environment-dependent method signatures in terms of familiar servlet objects <code>HttpServletRequest</code> 
 * and <code>HttpServletResponse</code>. The servlet adapter preserves all of the underlying Java client’s 
 * functionality and comes with out-of-the-box implementations of all environment-dependent classes
 *
 * 
 * @since 0.5
 */
package com.variant.client.servlet;
