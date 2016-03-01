package com.kyrobot.howfar.services;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.kyrobot.howfar.common.Transformers;
import com.kyrobot.howfar.data.DataAccessObject;
import com.kyrobot.howfar.model.ElevationMilestone;
import com.kyrobot.howfar.model.HighTarget;
import com.kyrobot.howfar.responses.ElevationResponse;
import com.kyrobot.howfar.utils.ServiceTestUtils;

import spark.Spark;

public class ElevationServiceTest {
	
	private final static String API_ROOT = "http://0.0.0.0:4567/api/elevation/";
	
	private final long major_id = 0L;
	private final String major_name = "Major Thing";
	private final int major_height = 100;
	
	private final Gson gson = Transformers.getGson();
	
	@Mock DataAccessObject<HighTarget> mockDAO;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(mockDAO.getMajor()).thenReturn(Stream.of(new HighTarget(major_id, major_name, major_height)));
		when(mockDAO.getMatches(anyDouble(), anyInt())).thenReturn(Stream.empty());
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

		final List<ElevationMilestone> majorMilestones = newArrayList(elevationResponse.getMajorMilestones());
		assertTrue(majorMilestones.size() == 1);
		
		double completion = majorMilestones.get(0).completion;
		assertNotNull(completion);
		assertEquals(1.0, completion, 0);
		
		final HighTarget target = majorMilestones.get(0).milestone;
		checkTarget(target, major_id, major_name, major_height);
	}
	
	@Test
	public void testFloorsMajors() throws Exception {
		// A Floor is 3.048 meters -> 5 floors is 15.24m
		// Target is 100 meters
		// Expect completion -> 0.152 to 3 decimal places
		
		final String json = ServiceTestUtils.doGET(API_ROOT + "floors/5");
		final ElevationResponse elevationResponse = gson.fromJson(json, ElevationResponse.class);
		
		final List<ElevationMilestone> majorMilestones = Lists.newArrayList(elevationResponse.getMajorMilestones());
		assertTrue(majorMilestones.size() == 1);
		
		double completion = majorMilestones.get(0).completion;
		assertNotNull(completion);
		assertEquals(0.152, completion, 0);
		
		final HighTarget target = majorMilestones.get(0).milestone;
		checkTarget(target, major_id, major_name, major_height);
		
	}
	
	@Test
	public void testFeetMajors() throws Exception {
		// A Foot is 0.3048 -> 8 feet is 2.4384m
		// Target is 100 meters
		// Expect completion -> 0.024 to 3 decimal places
		
		final String json = ServiceTestUtils.doGET(API_ROOT + "feet/8");
		final ElevationResponse elevationResponse = gson.fromJson(json, ElevationResponse.class);
		
		final List<ElevationMilestone> majorMilestones = Lists.newArrayList(elevationResponse.getMajorMilestones());
		assertTrue(majorMilestones.size() == 1);
		
		double completion = majorMilestones.get(0).completion;
		assertNotNull(completion);
		assertEquals(0.024, completion, 0);
		
		final HighTarget target = majorMilestones.get(0).milestone;
		checkTarget(target, major_id, major_name, major_height);
		
	}
	
	
	@Test
	public void testMetersClosest() throws Exception {
		final HighTarget one = new HighTarget(1, "One", 1);
		final HighTarget two = new HighTarget(2, "Two", 2);
		final HighTarget three = new HighTarget(3, "Three", 3);
		
		when(mockDAO.getMatches(anyDouble(), anyInt())).thenReturn(Stream.of(three, two, one));
		
		final String json = ServiceTestUtils.doGET(API_ROOT + "meters/3");
		final ElevationResponse elevationResponse = gson.fromJson(json, ElevationResponse.class);
		
		final Collection<ElevationMilestone> closest = elevationResponse.getClosestAchievements();
		assertTrue(closest.size() == 3);
		
		final Set<HighTarget> closestTargets = closest.stream().map(em -> em.milestone).collect(toSet());
		final Set<Double> closestCompletions = closest.stream().map(em -> em.completion).collect(toSet());
		assertEquals(Sets.newHashSet(one, two, three), closestTargets);
		assertEquals(Sets.newHashSet(1.0, 1.5, 3.0), closestCompletions);
	}
	private void checkTarget(HighTarget target, long expectedId, String expectedName, int expectedHeight) {
		assertNotNull(target);
		assertEquals(expectedName, target.getName());
		assertEquals(expectedHeight, target.getHeight());
		assertEquals(expectedId, target.getId());
	}

}
