package com.kyrobot.howfar.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.common.net.HttpHeaders;
import com.google.gson.Gson;
import com.kyrobot.howfar.common.HttpFragments;
import com.kyrobot.howfar.common.Transformers;

public class ServiceTestUtils {
	
	private static Gson productionGson = Transformers.getGson();
	
	/**
	 * @param url the URL to call, as a String
	 * @return the String representation of the resulting response entity
	 * @throws Exception
	 */
	public static String doGET(String url) throws Exception {
		final HttpGet get = new HttpGet(url);
		get.addHeader(HttpHeaders.CONTENT_ENCODING, HttpFragments.GZIP);
		final HttpEntity entity = HttpClients.createMinimal().execute(get).getEntity();
		return EntityUtils.toString(entity);
	}
	
	/**
	 * @param url the URL to call, as a String
	 * @return the {@link HttpResponse}
	 * @throws Exception
	 */
	public static HttpResponse doGETResponse(String url) throws Exception {
		final HttpGet get = new HttpGet(url);
		get.addHeader(HttpHeaders.CONTENT_ENCODING, HttpFragments.GZIP);
		return HttpClients.createMinimal().execute(get);
	}
	
	public static <T> T marshalJSON(String json, Class<T> model) {
		return productionGson.fromJson(json, model);
	}
	
	
}