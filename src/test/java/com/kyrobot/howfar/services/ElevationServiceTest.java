package com.kyrobot.howfar.services;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.kyrobot.howfar.common.HttpFragments;
import com.kyrobot.howfar.data.DataAccessObject;
import com.kyrobot.howfar.model.ElevationMilestone;
import com.kyrobot.howfar.model.HighTarget;
import com.kyrobot.howfar.responses.ElevationResponse;
import com.kyrobot.howfar.responses.ErrorResponse;
import com.kyrobot.howfar.utils.ServiceTestUtils;

import spark.Spark;

public class ElevationServiceTest {

	private final static String API_ROOT = "http://0.0.0.0:4567/api/elevation/";

	private final long major_id = 0L;
	private final String major_name = "Major Thing";
	private final int major_height = 100;

	@Mock
	DataAccessObject<HighTarget> mockDAO;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(mockDAO.getMajor()).thenReturn(Stream.of(new HighTarget(major_id, major_name, major_height)));
		when(mockDAO.getMatches(anyDouble(), anyInt())).thenReturn(Stream.empty());
		new ElevationService(mockDAO).defineRoutes();
		ServiceTestUtils.triggerProductionExceptionMappings();
		Spark.awaitInitialization();
	}

	@After
	public void tearDown() throws Exception {
		Spark.stop();
	}

	private static ElevationResponse callAPI(String uri) throws Exception {
		return ServiceTestUtils.marshalJSON(ServiceTestUtils.doGET(API_ROOT + uri), ElevationResponse.class);
	}

	private static ErrorResponse callAPIIncorrectly(String uri, int expectedStatus) throws Exception {
		final HttpResponse getResponse = ServiceTestUtils.doGETResponse(API_ROOT + uri);
		assertEquals(expectedStatus, getResponse.getStatusLine().getStatusCode());
		return ServiceTestUtils.marshalJSON(EntityUtils.toString(getResponse.getEntity()), ErrorResponse.class);
	}

	@Test
	public void testTargetHeightMeters() throws Exception {
		final ElevationResponse elevationResponse = callAPI("meters/9876");
		assertEquals(9876, elevationResponse.getTargetHeight(), 0.0);
		assertNull(elevationResponse.isHeightConverted());
	}

	@Test
	public void testTargetHeightFeet() throws Exception {
		final ElevationResponse elevationResponse = callAPI("feet/85");
		assertEquals(25.908, elevationResponse.getTargetHeight(), 0.0);
		assertTrue(elevationResponse.isHeightConverted());
	}

	@Test
	public void testTargetHeightFloors() throws Exception {
		final ElevationResponse elevationResponse = callAPI("floors/1");
		assertEquals(3.048, elevationResponse.getTargetHeight(), 0.0);
		assertTrue(elevationResponse.isHeightConverted());
	}

	@Test
	public void testTargetHeightAlphabetic() throws Exception {
		final ErrorResponse errorResponse = callAPIIncorrectly("floors/xyz", HttpFragments.BAD_REQUEST_400);
		assertNotNull(errorResponse.getMessage());
	}

	@Test
	public void testMetersMajors() throws Exception {
		final ElevationResponse elevationResponse = callAPI("meters/100");

		assertNull(elevationResponse.isHeightConverted());
		assertEquals(100, elevationResponse.getTargetHeight(), 0);

		final List<ElevationMilestone> majorMilestones = newArrayList(elevationResponse.getMajorMilestones());
		assertTrue(majorMilestones.size() == 1);
		checkMilestone(majorMilestones.get(0), 1, 0.0);

		final HighTarget target = majorMilestones.get(0).target;
		checkTarget(target, major_id, major_name, major_height);
	}

	@Test
	public void testFloorsMajors() throws Exception {
		// A Floor is 3.048 meters -> 5 floors is 15.24m
		// Target is 100 meters
		// Expect attained: null, progress: 15.2

		final ElevationResponse elevationResponse = callAPI("floors/5");

		assertTrue(elevationResponse.isHeightConverted());
		assertEquals(15.24, elevationResponse.getTargetHeight(), 0);

		final List<ElevationMilestone> majorMilestones = Lists.newArrayList(elevationResponse.getMajorMilestones());
		assertTrue(majorMilestones.size() == 1);
		checkMilestone(majorMilestones.get(0), null, 15.2);

		final HighTarget target = majorMilestones.get(0).target;
		checkTarget(target, major_id, major_name, major_height);
	}

	@Test
	public void testFeetMajors() throws Exception {
		// A Foot is 0.3048 -> 8 feet is 2.4384m
		// Target is 100 meters
		// Expect attained: null progress: 2.4
		final ElevationResponse elevationResponse = callAPI("feet/8");

		assertTrue(elevationResponse.isHeightConverted());
		assertEquals(2.4384, elevationResponse.getTargetHeight(), 0);

		final List<ElevationMilestone> majorMilestones = Lists.newArrayList(elevationResponse.getMajorMilestones());
		assertTrue(majorMilestones.size() == 1);
		checkMilestone(majorMilestones.get(0), null, 2.4);

		final HighTarget target = majorMilestones.get(0).target;
		checkTarget(target, major_id, major_name, major_height);
	}

	@Test
	public void testMetersClosest() throws Exception {
		final HighTarget one = new HighTarget(99, "One", 1);
		final HighTarget two = new HighTarget(100, "Two", 2);
		final HighTarget three = new HighTarget(101, "Three", 3);

		when(mockDAO.getMatches(anyDouble(), anyInt())).thenReturn(Stream.of(three, two, one));

		final ElevationResponse elevationResponse = callAPI("meters/3");

		final Collection<ElevationMilestone> closest = elevationResponse.getClosestAchievements();
		assertTrue(closest.size() == 3);

		final Set<HighTarget> closestTargets = closest.stream().map(em -> em.target).collect(toSet());
		assertEquals(Sets.newHashSet(one, two, three), closestTargets);

		for (ElevationMilestone milestone : closest) {
			switch (Long.valueOf(milestone.target.getId()).intValue()) {
			case 99:
				checkMilestone(milestone, 3, 0.0);
				break;
			case 100:
				checkMilestone(milestone, 1, 50.0);
				break;
			case 101:
				checkMilestone(milestone, 1, 0.0);
				break;
			default:
				fail("Unexpected target returned");
			}
		}

	}

	private static void checkTarget(HighTarget target, long expectedId, String expectedName, int expectedHeight) {
		assertNotNull(target);
		assertEquals(expectedName, target.getName());
		assertEquals(expectedHeight, target.getHeight());
		assertEquals(expectedId, target.getId());
	}

	private static void checkMilestone(ElevationMilestone milestone, Integer attainment, Double progress) {
		assertNotNull(milestone);
		assertEquals(attainment, milestone.attained);
		assertEquals(progress, milestone.progress, 0);
	}

}
