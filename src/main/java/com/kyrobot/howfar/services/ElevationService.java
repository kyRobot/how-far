package com.kyrobot.howfar.services;

import static com.kyrobot.howfar.common.Transformers.JSON;
import static java.util.stream.Collectors.toList;
import static spark.Spark.exception;
import static spark.Spark.get;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import com.kyrobot.howfar.common.Functions;
import com.kyrobot.howfar.data.DataAccessObject;
import com.kyrobot.howfar.model.ElevationMilestone;
import com.kyrobot.howfar.model.TallThing;
import com.kyrobot.howfar.responses.ElevationResponse;

public class ElevationService implements RESTService {
	
	private static final String APPLICATION_JSON = "application/json";
	private static final String API_ROOT = "/api/elevation";

	public static final double METERS_PER_FLOOR = 3.048;
	public static final double METERS_PER_FOOT = 0.3048;
	
	private final DataAccessObject<TallThing> dao;
	
	public ElevationService(DataAccessObject<TallThing> dao) {
		this.dao = dao;
	}
	
	@Override
	public void defineRoutes() {
		
		final BiFunction<Stream<TallThing>, Double, List<ElevationMilestone>> 
			milestones = (targets, climbed) -> 
				targets.map(t-> new ElevationMilestone(t, completed(climbed, t.getHeight(), 3)))
					.collect(toList());
		
		get(API_ROOT + "/floors/:floors", APPLICATION_JSON, (req, res) -> {
			final ElevationResponse.Builder builder = ElevationResponse.builder();
			builder.majorMilestones(milestones.apply(dao.getMajor(), convert(req.params(":floors"), METERS_PER_FLOOR)));
			return builder.build();
		}, JSON);
		
		get(API_ROOT + "/meters/:meters", APPLICATION_JSON, (req, res) -> {
			final ElevationResponse.Builder builder = ElevationResponse.builder();
			builder.majorMilestones(milestones.apply(dao.getAll(), convert(req.params(":meters"), 1.0)));
			return builder.build();
		}, JSON);
		
		get(API_ROOT + "/feet/:feet", APPLICATION_JSON, (req, res) -> {
			final ElevationResponse.Builder builder = ElevationResponse.builder();
			builder.majorMilestones(milestones.apply(dao.getAll(), convert(req.params(":feet"), METERS_PER_FOOT)));
			return builder.build();
		}, JSON);
		
		exception(NumberFormatException.class, (e, req, res) -> {
		    res.status(400);
		    res.body("Bad Request, expected a number");
		});
	}
	
	private Double convert(String userInput, Double multiplierToMeters) {
		return Functions.converter.apply(multiplierToMeters, userInput);
	}
	
	private Double completed(final double done, final int target, final int precision) {
		return Functions.toNplaces.apply(precision, done/target);
	}
	
}
