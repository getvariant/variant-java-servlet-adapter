package com.variant.client.servlet.util;

import java.util.regex.Pattern;

import com.variant.client.servlet.ServletVariantException;
import com.variant.core.schema.Schema;
import com.variant.core.schema.State;

/**
 * <p>
 * Select a Variant state based on a path pattern. Implements a sophisticated path matching scheme:</p>
 * <ol>
 *   <li>Symbol '/' always stands for the path separator. Any path must start with '/'.</li>
 *   <li>Any sequence of symbols between two consecutive symbols '/' is taken to be a literal, 
 *       unless the first letter of the sequence is a tilde '~', in which case the string immediately 
 *       following the tilde and until, but not including, the next unescaped '/' is considered a regular expression. 
 *       Complete <@link https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html regular expression syntax}
 *       is supported.</li>
 *  <li>Although symbol '/' does not have a special meaning in the regular expression grammar, it does for Variant: 
 *      this is how Variant decides where a regular expression ends. Therefore, if '/' must be included in the regular 
 *      expression, it must be escaped with the '\' symbol, like any other special character. By including '/' symbols 
 *      in the regular expression, it is possible to match variable sections in the middle of a path.</li>
 *  <li>Symbol '//' can be used anywhere, where '/' can be used, and is a shortcut for '/~.&ast;/'. 
 *      In other words, '//' will match any string. Note that '///' is legal but superfluous because it will be 
 *      expanded to '/~.&ast;/~.&ast;/'.</li>
 *  <li>The very last '/' of the pattern is not significant, i.e. Variant will remove it, if present, from both
 *      the pattern and the path after all '//' are expanded. This enables easy prefix match: '/user//' will 
 *      match any path that starts with '/user/'.</li>
 *
 * <p>
 * Examples:
 * <table border="1" cellpadding="4">
 *   <tr>
 *     <th>Path</th>
 *     <th>Will Match</th>
 *     <th>Will Not Match</th>
 *   </tr>
 *   <tr>
 *     <td>/user</td>
 *     <td>/user<br/>/user/</td>
 *     <td>/user/new</td>
 *   </tr>
 *   <tr>
 *     <td>/user//</td>
 *     <td>/user<br/>/user/<br/>/user/new</td>
 *     <td>/service/user/</td>
 *   </tr>
 *   <tr>
 *     <td>/user//.html</td>
 *     <td>/user/new/error.html</td>
 *     <td>/user/error</td>
 *   </tr>
 * </table>
 *
 * @author Igor Urisman
 * @since 0.5
 */
public class StateSelectorByRequestPath  {

	/**
	 * This implements the whole path matching logic.
	 * Package visibility to expose to tests.
	 */
	public static boolean match(String pattern, String string) {

		if (!pattern.startsWith("/")) throw new ServletVariantException("Pattern must start with [/] but was [" + pattern + "]");
		if (!string.startsWith("/")) throw new ServletVariantException("String must start with [/] but was [" + string + "]");
		
		// Expand '//', otherwise they may get eaten by the splitter algorithm.
		// Keep looking for '//' until none. This is needed to account for '///'
		String expandedPattern = pattern;
		while (expandedPattern.indexOf("//") >= 0) {
			expandedPattern = expandedPattern.replaceAll("//", "/~.*/");
		}
		
		String[] stringTokens = string.split("/");		
		String patternTokens[] = expandedPattern.split("/");
		
		// Start with 1 because the first token in both will always be an empty string because the
		// first character in both is '/'. UNLESS the pattern or the string is just "/", in which
		// case split() creates a 0 length array -- a corner case that has to be accounted for.
		if (stringTokens.length == 0) {
			return patternTokens.length == 0;
		}
		else if  (patternTokens.length == 0) {
			return stringTokens.length == 0;
		}
		
		for (int i = 1; ;i++) {
			if (i == stringTokens.length) {
				if (i == patternTokens.length) {
					// out of input tokens and out of pattern tokens
					return true;
				}
				else {
					// out of input tokens but still have pattern tokens.
					// Ok only if hey all match the empty string.
					for (int j = i; j < patternTokens.length; j++) {
						if (!Pattern.compile(toRegex(patternTokens[j])).matcher("").matches()) return false;
					}
					return true;
				}
			}
			else {
				if (i == patternTokens.length) {
					// still have input tokens but out of pattern tokens
					return false;
				}
				else {
					// have an input token and a pattern token - match them.
					String regex = toRegex(patternTokens[i]);
					if (!Pattern.compile(regex).matcher(stringTokens[i]).matches()) return false;
				}
			}
		}
	}
	
	/**
	 * convert a pattern token to a regex.
	 * @param string
	 * @return
	 */
	private static String toRegex(String token) {
		return token.startsWith("~") ? token.substring(1) : "\\Q" + token + "\\E";
	}
    //---------------------------------------------------------------------------------------------//
	//                                    PUBLIC INTERFACE                                         //
	//---------------------------------------------------------------------------------------------//

	/**
	 * Select a Variant state based by its path.
	 *
	 * @param path Path string.
	 * @return
	 * @since 0.5
	 */
	public static State select(Schema schema, String path) {
		for (State state: schema.getStates()) {
			if (match(state.getParameters().get("path"), path)) return state;
		}
		return null;
	}
	
}
