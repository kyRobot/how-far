package com.kyrobot.howfar;

import static spark.Spark.after;
import static spark.Spark.exception;

import java.util.stream.Stream;

import com.google.common.annotations.VisibleForTesting;
import com.kyrobot.howfar.common.HttpFragments;
import com.kyrobot.howfar.common.Transformers;
import com.kyrobot.howfar.data.InMemoryElevationStoreDAO;
import com.kyrobot.howfar.reporting.ServerRuntimeException;
import com.kyrobot.howfar.responses.ErrorResponse;
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

    	mapExceptions();

    	Stream.of(new ElevationService(new InMemoryElevationStoreDAO()))
        	.forEach(RESTService::defineRoutes);
        
        after((request, response) -> {
            response.header(HttpFragments.CONTENT_ENCODING, HttpFragments.GZIP);
        });
        
    }

    
	/**
	 * Map thrown exceptions to {@link ErrorResponse}
	 * 
	 * 
	 */
    @VisibleForTesting
	public static void mapExceptions() {
		exception(ServerRuntimeException.class, (e, req, res) -> {
			final ServerRuntimeException serverRuntimeException = (ServerRuntimeException) e;
			
			res.status(serverRuntimeException.getHttpStatus());
			final String message = serverRuntimeException.getUserMessage();
			try {
				res.body(Transformers.toJSON.render(new ErrorResponse(message)));
			} catch (Exception ex) {
				res.body(message);
			}
		});
		
		exception(RuntimeException.class, (e, req, res) ->{
    		res.status(HttpFragments.SERVER_ERROR_500);
    		String message = "Oops, Internal Error";
    		try {
    			res.body(Transformers.toJSON.render(new ErrorResponse(message)));
			} catch (Exception ex) {
				res.body(message);
			}
    	});
	}
}