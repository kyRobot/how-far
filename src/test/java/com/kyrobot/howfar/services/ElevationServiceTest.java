package com.kyrobot.howfar.services;

import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.Matchers.is;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;
import com.kyrobot.howfar.data.DataAccessObject;
import com.kyrobot.howfar.model.HighTarget;

import spark.Spark;

public class ElevationServiceTest {
	
	@Mock DataAccessObject<HighTarget> mockDAO;
	private final static String API_ROOT = "http://0.0.0.0:4567/api/elevation/";

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		List<HighTarget> targets = Lists.newArrayList(
				new HighTarget(0L, "Tall Thing", 100));
		
		BDDMockito.when(mockDAO.getMajor()).thenReturn(targets.stream());
		
		ElevationService service = new ElevationService(mockDAO);
		service.defineRoutes();
		Spark.awaitInitialization();
	}

	@After
	public void tearDown() throws Exception {
		Spark.stop();
	}

	@Test
	public void testMetersMajors() throws Exception{
		
		when().
			get(API_ROOT + "meters/100").
		then()
			.assertThat()
				.body("majorMilestones.size()", is(1));
	}

}
