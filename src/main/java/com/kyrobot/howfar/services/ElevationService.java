package com.kyrobot.howfar.services;

import static com.kyrobot.howfar.common.Transformers.JSON;
import static java.util.stream.Collectors.toList;
import static spark.Spark.exception;
import static spark.Spark.get;

import java.util.List;
import java.util.stream.Stream;

import com.kyrobot.howfar.common.Functions;
import com.kyrobot.howfar.data.DataAccessObject;
import com.kyrobot.howfar.model.ElevationMilestone;
import com.kyrobot.howfar.model.HighTarget;
import com.kyrobot.howfar.responses.ElevationResponse;

import spark.Request;
import spark.Response;

public class ElevationService implements RESTService {
	
	private static final String APPLICATION_JSON = "application/json";
	private static final String API_ROOT = "/api/elevation";

	public static final double METERS_PER_FLOOR = 3.048;
	public static final double METERS_PER_FOOT = 0.3048;
	
	private static final int PRECISION = 3; //decimal places, not sig figs
	private static final int MATCH_LIMIT = 3;
	
	private final DataAccessObject<HighTarget> dao;
	
	public ElevationService(DataAccessObject<HighTarget> dao) {
		this.dao = dao;
	}
	
	@Override
	public void defineRoutes() {
		
		get(API_ROOT + "/floors/:floors",
			APPLICATION_JSON,
			(req, res) -> elevationResponse(req, res, ":floors", METERS_PER_FLOOR, PRECISION, MATCH_LIMIT),
			JSON);
		
		get(API_ROOT + "/meters/:meters",
			APPLICATION_JSON, 
			(req, res) ->  elevationResponse(req, res, ":meters", 1.0,  PRECISION, MATCH_LIMIT),
			JSON);
		
		get(API_ROOT + "/feet/:feet",
			APPLICATION_JSON,
			(req, res) -> elevationResponse(req, res, ":feet", METERS_PER_FOOT, PRECISION, MATCH_LIMIT),
			JSON);
		
		exception(NumberFormatException.class, (e, req, res) -> {
		    res.status(400);
		    res.body("Bad Request, expected a number");
		});
	}
	
	private ElevationResponse elevationResponse(Request req,
			Response res,
			String paramKey,
			double conversionMultiplier,
			int completionPrecision,
			int numberOfClosestMatches) {
		final double climbed = convert(req.params(paramKey), conversionMultiplier);
		final ElevationResponse.Builder builder = ElevationResponse.builder();
		builder.majorMilestones(milestones(dao.getMajor(), climbed, completionPrecision));
		builder.closest(milestones(dao.getMatches(climbed, numberOfClosestMatches), climbed, completionPrecision));
		return builder.build();
	}
	
	private Double convert(String userInput, Double multiplierToMeters) {
		return Functions.converter.apply(multiplierToMeters, userInput);
	}
	
	private Double completed(final double done, final int target, final int precision) {
		return Functions.toNplaces.apply(precision, done/target);
	}
	
	private List<ElevationMilestone> milestones(Stream<HighTarget> targets, double climbed, int completedPrecision)
	{
		return targets.map(t-> new ElevationMilestone(t, completed(climbed, t.getHeight(), completedPrecision)))
				 .collect(toList());
	}
	
}
