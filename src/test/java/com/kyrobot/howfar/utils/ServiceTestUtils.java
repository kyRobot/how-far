package com.kyrobot.howfar.utils;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class ServiceTestUtils {
	
	public static String doGET(String url) throws Exception {
		final HttpGet get = new HttpGet(url);
		get.addHeader("Content-Encoding", "gzip");
		final HttpEntity entity = HttpClients.createMinimal().execute(get).getEntity();
		return EntityUtils.toString(entity);
	}

}
