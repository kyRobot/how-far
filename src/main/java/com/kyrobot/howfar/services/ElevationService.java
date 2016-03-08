package com.kyrobot.howfar.services;

import static spark.Spark.exception;
import static spark.Spark.get;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.kyrobot.howfar.common.Functions;
import com.kyrobot.howfar.common.HttpFragments;
import com.kyrobot.howfar.common.Transformers;
import com.kyrobot.howfar.data.DataAccessObject;
import com.kyrobot.howfar.model.ElevationMilestone;
import com.kyrobot.howfar.model.HighTarget;
import com.kyrobot.howfar.responses.ElevationResponse;
import com.kyrobot.howfar.responses.ErrorResponse;

import spark.Request;
import spark.Response;

public class ElevationService implements RESTService {
	
	private static final String API_ROOT = "/api/elevation";

	public static final double METERS_PER_FLOOR = 3.048;
	public static final double METERS_PER_FOOT = 0.3048;
	public static final double NO_CONVERSION = 1.0;
	
	private static final int PRECISION = 3; //decimal places, not sig figs
	private static final int MATCH_LIMIT = 2;
	
	private final DataAccessObject<HighTarget> dao;
	
	public ElevationService(DataAccessObject<HighTarget> dao) {
		this.dao = dao;
	}
	
	@Override
	public void defineRoutes() {
		
		get(API_ROOT + "/floors/:floors",
			HttpFragments.APPLICATION_JSON,
			(req, res) -> elevationResponse(req, res, ":floors", METERS_PER_FLOOR, PRECISION, MATCH_LIMIT),
			Transformers.toJSON);
		
		get(API_ROOT + "/meters/:meters",
			HttpFragments.APPLICATION_JSON, 
			(req, res) ->  elevationResponse(req, res, ":meters", NO_CONVERSION,  PRECISION, MATCH_LIMIT),
			Transformers.toJSON);
		
		get(API_ROOT + "/feet/:feet",
			HttpFragments.APPLICATION_JSON,
			(req, res) -> elevationResponse(req, res, ":feet", METERS_PER_FOOT, PRECISION, MATCH_LIMIT),
			Transformers.toJSON);
		
		exception(NumberFormatException.class, (e, req, res) -> {
		    res.status(HttpFragments.BAD_REQUEST_400);
		    final String message = "Bad Request, expected a number";
		    try {
				res.body(Transformers.toJSON.render(new ErrorResponse(message)));
			} catch (Exception ex) {
				// Transform to JSON failed, fall back to non-JSON response
				res.body(message);
			}
		});
	}
	
	private ElevationResponse elevationResponse(Request req,
			Response res,
			String paramKey,
			double conversionMultiplier,
			int precision,
			int nToFind) {
		final double metersClimbed = convert(req.params(paramKey), conversionMultiplier);
		final ElevationResponse.Builder builder = ElevationResponse.builder();
		builder.target(metersClimbed, conversionMultiplier != NO_CONVERSION);
		builder.majorMilestones(milestones(dao.getMajor(), metersClimbed, precision));
		builder.closest(milestones(dao.getMatches(metersClimbed, nToFind), metersClimbed, precision));
		return builder.build();
	}
	
	private static Double convert(String userInput, Double multiplierToMeters) {
		return Functions.converter.apply(multiplierToMeters, userInput);
	}
	
	private static Double completed(final double done, final int target, final int precision) {
		return Functions.toNplaces.apply(precision, done/target);
	}
	
	private static List<ElevationMilestone> milestones(Stream<HighTarget> targets, double climbed, int completedPrecision)
	{
		return targets.map(t-> new ElevationMilestone(t, completed(climbed, t.getHeight(), completedPrecision)))
				 .collect(Collectors.toList());
	}
	
}
