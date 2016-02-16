package com.kyrobot.howfar;

import static spark.Spark.get;

import java.util.Arrays;
import java.util.Optional;

import com.google.gson.Gson;
import com.kyrobot.howfar.services.ElevationService;
import com.kyrobot.howfar.services.RESTService;

import spark.ResponseTransformer;

/**
 * Main App Entry point
 *
 */
public class Server 
{
	public static final ResponseTransformer JSON = (ResponseTransformer) (o) -> new Gson().toJson(o);
	
	public static class WelcomeMessage {
		public String greeting;
	}
	
    public static void main( String[] args )
    {
        get("/", (req, res) -> {
        	final Optional<String> maybeName = Optional.ofNullable(req.queryParams("name"));
        	final WelcomeMessage hello = new WelcomeMessage();
        	hello.greeting="Hello, " + maybeName.orElse("World");
        	return hello;
        }, JSON);
        
        final RESTService[] services = {new ElevationService()};
        Arrays.stream(services).forEach(RESTService::defineRoutes);
        
        
        
    }
}