package com.kyrobot.howfar.data;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.kyrobot.howfar.model.HighTarget;

public class InMemoryElevationStoreDAO implements DataAccessObject<HighTarget> {
	
	private final List<HighTarget> STORE;
	private final int significantHeight = 300;

	public InMemoryElevationStoreDAO() {
		Builder<HighTarget> immutableModel = ImmutableList.builder();
		immutableModel.add(new HighTarget(0L, "Big Ben", 96));
		immutableModel.add(new HighTarget(1L, "The Gherkin", 180));
		immutableModel.add(new HighTarget(2L, "The Shard", 306));
		immutableModel.add(new HighTarget(3L, "Empire State Building", 381));
		immutableModel.add(new HighTarget(4L, "Millenium Dome", 95));
		immutableModel.add(new HighTarget(5L, "London Eye", 134));
		immutableModel.add(new HighTarget(6L, "One Canada Square, Canary Wharf", 235));
		immutableModel.add(new HighTarget(7L, "Tower 42", 182));
		immutableModel.add(new HighTarget(8L, "Orbit", 135));
		immutableModel.add(new HighTarget(9L, "Walkie Talkie", 160));
		STORE = immutableModel.build();
	}
	
	@Override
	public Stream<HighTarget> getAll() {
		return STORE.stream();
	}

	@Override
	public Optional<HighTarget> getById(long id) {
		return STORE.parallelStream()
					.filter(t -> t.getId() == id)
					.findFirst();
	}
	
	@Override
	public Stream<HighTarget> getManyByIds(long... ids) {
		Arrays.sort(ids);
		return STORE.parallelStream()
					.filter(t -> Arrays.binarySearch(ids, t.getId()) >= 0);
					
	}

	@Override
	public Stream<HighTarget> getMajor() {
		return STORE.parallelStream()
					.filter(t -> t.getHeight() >= significantHeight);
				
				
	}

	/**
	 * @param see {@link DataAccessObject#getMatches(double, int)
	 * @param see {@link DataAccessObject}{@link #getMatches(double, int)}
	 * @return the at most n {@link HighTarget}s that are closest to target in height,
	 * 	both taller and shorter targets will be returned. If n is odd then targets
	 * 	taller than target will benefit from the extra item e.g if n is 3 then 2 taller
	 *  and 1 shorter will be returned (if found)
	 */
	@Override
	public Stream<HighTarget> getMatches(double target, int n) {
		final int shortLimit = n / 2;
		final int tallLimit = (n % 2 == 1) ? shortLimit + 1 : shortLimit;
		final Stream<HighTarget> shorter = STORE.stream()
					.filter(t -> t.getHeight() < target)
					.sorted((a,b) -> b.getHeight() - a.getHeight())
					.limit(shortLimit);
		
		final Stream<HighTarget> taller = STORE.stream()
					.filter(t -> t.getHeight() >= target)
					.sorted((a,b) -> a.getHeight() - b.getHeight())
					.limit(tallLimit);
		
		return Stream.concat(shorter, taller);
	}

}
