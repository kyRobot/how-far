package com.kyrobot.howfar.common;

import static com.google.gson.FieldNamingPolicy.UPPER_CAMEL_CASE;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import spark.ResponseTransformer;

public final class Transformers {
	
	private static Gson gson = new GsonBuilder()
									.setPrettyPrinting()
									.setFieldNamingPolicy(UPPER_CAMEL_CASE)
									.create();
	
	private Transformers() {
		// no instantiation
	}
	
	public static Gson getGson() {
		return gson;
	}

	public static final ResponseTransformer JSON = o -> gson.toJson(o);
	
}

