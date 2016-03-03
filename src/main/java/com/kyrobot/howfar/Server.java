package com.kyrobot.howfar;

import static spark.Spark.after;

import java.util.stream.Stream;

import com.kyrobot.howfar.common.HttpFragments;
import com.kyrobot.howfar.data.InMemoryElevationStoreDAO;
import com.kyrobot.howfar.services.ElevationService;
import com.kyrobot.howfar.services.RESTService;

/**
 * Application Entry point
 *
 */
public class Server 
{
    public static void main( String[] args )
    {
        Stream.of(new ElevationService(new InMemoryElevationStoreDAO()))
        	.forEach(RESTService::defineRoutes);
        
        after((request, response) -> {
            response.header(HttpFragments.CONTENT_ENCODING, HttpFragments.GZIP);
        });
    }
}