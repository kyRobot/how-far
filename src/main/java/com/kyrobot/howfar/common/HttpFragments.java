package com.kyrobot.howfar.common;

import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;

/**
 *	Constants class for common Http fragments
 */
public final class HttpFragments {
	
	private HttpFragments() {
		// no instantiation
	}
	// MIME TYPES
	public static final String APPLICATION_JSON = MediaType.JSON_UTF_8.toString();
	
	// ENCODINGS
	public static final String GZIP = "gzip";
	
	// HEADER KEYS
	public static final String CONTENT_ENCODING = HttpHeaders.CONTENT_ENCODING;
	
	// STATUS CODES
	public static final int BAD_REQUEST_400 = 400;

}
