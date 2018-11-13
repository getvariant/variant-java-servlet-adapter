package com.variant.client.servlet.impl;

import com.typesafe.config.Config;
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
	public ServletClientImpl() {
		this.bareClient = VariantClient.Factory.getInstance();
	}

	@Override
	public Config getConfig() {
		return bareClient.getConfig();
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
