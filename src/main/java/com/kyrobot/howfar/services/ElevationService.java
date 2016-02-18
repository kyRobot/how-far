package com.kyrobot.howfar.services;

import static com.kyrobot.howfar.common.Transformers.JSON;
import static java.util.stream.Collectors.toList;
import static spark.Spark.exception;
import static spark.Spark.get;

import com.kyrobot.howfar.data.DataAccessObject;
import com.kyrobot.howfar.model.ElevationMilestone;
import com.kyrobot.howfar.model.TallThing;

public class ElevationService implements RESTService {
	
	private static final String API_ROOT = "/api/elevation";
	private static final double METERS_PER_FLOOR = 3.048;
	private final DataAccessObject<TallThing> dao;
	
	public ElevationService(DataAccessObject<TallThing> dao) {
		this.dao = dao;
	}
	
	@Override
	public void defineRoutes() {
		
		get(API_ROOT + "/:floors", "application/json", (req, res) -> {
			
			final int floorsAsInt = Integer.parseInt(req.params(":floors"));
			final double heightClimbed = floorsAsInt * METERS_PER_FLOOR;
			return dao.getAll()
				.map(t ->  new ElevationMilestone(t, heightClimbed / t.getHeight()))
				.collect(toList());
			
		}, JSON);
		
		exception(NumberFormatException.class, (e, req, res) -> {
		    res.status(400);
		    res.body("Bad Request, we expected a number");
		});
	}

}
