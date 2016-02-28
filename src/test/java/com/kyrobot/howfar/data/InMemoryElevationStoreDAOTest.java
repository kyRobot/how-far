package com.kyrobot.howfar.data;

import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.kyrobot.howfar.model.HighTarget;

public class InMemoryElevationStoreDAOTest {

	private DataAccessObject<HighTarget> dao;
	
	@Before
	public void setUp() throws Exception {
		dao = new InMemoryElevationStoreDAO();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testGetAll() {
		assertEquals(10L, dao.getAll().distinct().count());
	}

	@Test
	public final void testGetByIdExists() {
		final Optional<HighTarget> target = dao.getById(5);
		assertTrue(target.isPresent());
		assertEquals(new HighTarget(5L, "London Eye", 134), target.get());
		
	}
	
	@Test
	public final void testGetByIdDoesntExist() {
		final Optional<HighTarget> target = dao.getById(99);
		assertFalse(target.isPresent());
		
	}

	@Test
	public final void testGetMajor() {
		// Expect: {2L, "The Shard", 306},  {3L, "Empire State Building", 381}
		final Set<HighTarget> majors = dao.getMajor().collect(toSet());
		
		assertTrue(majors.size() == 2);
		Set<HighTarget> expected = Sets.newHashSet(
				new HighTarget(2L, "The Shard", 306),
				new HighTarget(3L, "Empire State Building", 381));
		
		assertEquals(expected, majors);
		
	}

	@Test
	public final void testGetMatches() {
		final Set<HighTarget> matches = dao.getMatches(185, 3).collect(toSet());
		final Set<HighTarget> expected = getMany(1,7,9);
		assertEquals(expected, matches);
	}
	
	private Set<HighTarget> getMany(long... ids) {
		return Arrays.stream(ids)
				.mapToObj(dao::getById)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(toSet());
	}

}
