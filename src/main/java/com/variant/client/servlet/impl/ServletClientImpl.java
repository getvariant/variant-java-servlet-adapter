package com.variant.client.servlet.impl;

import com.variant.client.Connection;
import com.variant.client.VariantClient;
import com.variant.client.servlet.ServletConnection;
import com.variant.client.servlet.ServletVariantClient;

/**
 * The implementation of {@link ServletVariantClient}.
 * 
 * @author Igor Urisman
 * @since 1.0
 */
public class ServletClientImpl implements ServletVariantClient {

	private VariantClient bareClient;
	
	//---------------------------------------------------------------------------------------------//
	//                                          PUBLIC                                             //
	//---------------------------------------------------------------------------------------------//

	/**
	 */		
	public ServletClientImpl(VariantClient bareClient) {
		this.bareClient = bareClient;
	}

	@Override
	public ServletConnection connectTo(String url) {
		Connection bareConnection = bareClient.connectTo(url);
		return new ServletConnectionImpl(this, bareConnection);
	}

	//---------------------------------------------------------------------------------------------//
	//                                        PUBLIC EXT                                           //
	//---------------------------------------------------------------------------------------------//

	/**
	 */
	public VariantClient getBareClient() {
		return bareClient;
	}

}
