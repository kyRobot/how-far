package com.kyrobot.howfar.data;

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
	public Stream<HighTarget> getMajor() {
		return STORE.parallelStream()
				.filter(t -> t.getHeight() >= significantHeight);
				
				
	}

}
