package com.kyrobot.howfar.common;

import com.google.gson.Gson;

import spark.ResponseTransformer;

public final class Transformers {
	
	private Transformers() {
		// no instantiation
	}
	
	public static final ResponseTransformer JSON = (ResponseTransformer) (o) -> new Gson().toJson(o);
	
}

