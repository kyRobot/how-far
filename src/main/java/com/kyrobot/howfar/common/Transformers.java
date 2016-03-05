package com.kyrobot.howfar.common;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import spark.ResponseTransformer;

public final class Transformers {
	
	private static Gson gson = new GsonBuilder()
									.setPrettyPrinting()
									.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
									.create();
	
	private Transformers() {
		// no instantiation
	}
	
	@VisibleForTesting
	public static Gson getGson() {
		return gson;
	}

	public static final ResponseTransformer toJSON = o -> gson.toJson(o);
	
}

