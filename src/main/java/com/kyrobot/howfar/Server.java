package com.kyrobot.howfar;

import static spark.Spark.after;

import java.util.Arrays;

import com.kyrobot.howfar.data.InMemoryElevationStoreDAO;
import com.kyrobot.howfar.services.ElevationService;
import com.kyrobot.howfar.services.RESTService;

/**
 * Main App Entry point
 *
 */
public class Server 
{
	public static class WelcomeMessage {
		public String greeting;
	}
	
    public static void main( String[] args )
    {
        final RESTService[] services = {new ElevationService(new InMemoryElevationStoreDAO())};
        Arrays.stream(services).forEach(RESTService::defineRoutes);
        
        after((request, response) -> {
            response.header("Content-Encoding", "gzip");
        });
    }
}