package com.kyrobot.howfar.services;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.kyrobot.howfar.data.DataAccessObject;
import com.kyrobot.howfar.model.ElevationMilestone;
import com.kyrobot.howfar.model.HighTarget;
import com.kyrobot.howfar.responses.ElevationResponse;
import com.kyrobot.howfar.utils.ServiceTestUtils;

import spark.Spark;

public class ElevationServiceTest {
	
	private final static String API_ROOT = "http://0.0.0.0:4567/api/elevation/";
	private final Gson gson = new Gson();
	
	@Mock DataAccessObject<HighTarget> mockDAO;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		final Stream<HighTarget> majors = newArrayList(new HighTarget(0L, "Tall Thing", 100)).stream();
		when(mockDAO.getMajor()).thenReturn(majors);
		ElevationService service = new ElevationService(mockDAO);
		service.defineRoutes();
		Spark.awaitInitialization();
	}

	@After
	public void tearDown() throws Exception {
		Spark.stop();
	}

	@Test
	public void testMetersMajors() throws Exception {
		final String json = ServiceTestUtils.doGET(API_ROOT + "meters/100");
		final ElevationResponse elevationResponse = gson.fromJson(json, ElevationResponse.class);
		final List<ElevationMilestone> majorMilestones = Lists.newArrayList(elevationResponse.getMajorMilestones());
		
		assertTrue(majorMilestones.size() == 1);
		
		double completion = majorMilestones.get(0).completion;
		assertNotNull(completion);
		assertEquals(1.0, completion, 0);
		
		final HighTarget target = majorMilestones.get(0).milestone;
		
		assertNotNull(target);
		assertEquals("Tall Thing", target.getName());
		assertEquals(100, target.getHeight());
		assertEquals(0L, target.getId());
	}

}
