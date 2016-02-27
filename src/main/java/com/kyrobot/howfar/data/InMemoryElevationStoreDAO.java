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
		immutableModel.add(new HighTarget(123L, "Big Ben", 96));
		immutableModel.add(new HighTarget(456L, "Empire State Building", 381));
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
