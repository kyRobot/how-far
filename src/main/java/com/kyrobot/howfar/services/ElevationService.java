package com.kyrobot.howfar.services;

import static com.kyrobot.howfar.common.Transformers.JSON;
import static java.util.stream.Collectors.toList;
import static spark.Spark.exception;
import static spark.Spark.get;

import java.math.BigDecimal;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import com.kyrobot.howfar.data.DataAccessObject;
import com.kyrobot.howfar.model.ElevationMilestone;
import com.kyrobot.howfar.model.TallThing;

public class ElevationService implements RESTService {
	
	private static final String API_ROOT = "/api/elevation";
	
	private static final double METERS_PER_FLOOR = 3.048;
	private static final double METERS_PER_FOOT = 0.3048;
	
	private static final Function<Double, Double> precision = 
			(value) -> new BigDecimal(value).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
			
	private static final BiFunction<Double, Integer, Double> percent = 
			(climbed, height) -> climbed/height;
	
	private static final BinaryOperator<Double> converter = 
			(rate, value) -> rate * value;

	private final DataAccessObject<TallThing> dao;
	
	public ElevationService(DataAccessObject<TallThing> dao) {
		this.dao = dao;
	}
	
	@Override
	public void defineRoutes() {
		
		final BiFunction<Double, Integer, Double> completed = percent.andThen(precision);
		
		get(API_ROOT + "/floors/:floors", "application/json", (req, res) -> {
			final double floors = Double.parseDouble(req.params(":floors"));
			final double heightClimbed = converter.apply(METERS_PER_FLOOR, floors);
			return dao.getAll()
				.map(t -> new ElevationMilestone(t, completed.apply(heightClimbed, t.getHeight())))
				.collect(toList());
			
		}, JSON);
		
		get(API_ROOT + "/meters/:meters", "application/json", (req, res) -> {
			final double heightClimbed = Double.parseDouble(req.params(":meters"));
			return dao.getAll()
				.map(t ->  new ElevationMilestone(t, completed.apply(heightClimbed, t.getHeight())))
				.collect(toList());
			
		}, JSON);
		
		get(API_ROOT + "/feet/:feet", "application/json", (req, res) -> {
			final double feet = Double.parseDouble(req.params(":feet"));
			final double heightClimbed = converter.apply(METERS_PER_FOOT, feet);
			return dao.getAll()
				.map(t ->  new ElevationMilestone(t, completed.apply(heightClimbed, t.getHeight())))
				.collect(toList());
			
		}, JSON);
		
		exception(NumberFormatException.class, (e, req, res) -> {
		    res.status(400);
		    res.body("Bad Request, we expected a number");
		});
	}

}
