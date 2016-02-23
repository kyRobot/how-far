package com.kyrobot.howfar.data;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.kyrobot.howfar.model.TallThing;

public class InMemoryElevationStoreDAO implements DataAccessObject<TallThing> {
	
	private final List<TallThing> STORE;
	private final int significantHeight = 300;

	public InMemoryElevationStoreDAO() {
		Builder<TallThing> immutableModel = ImmutableList.builder();
		immutableModel.add(new TallThing(123L, "Big Ben", 96));
		immutableModel.add(new TallThing(456L, "Empire State Building", 381));
		STORE = immutableModel.build();
	}
	
	@Override
	public Stream<TallThing> getAll() {
		return STORE.stream();
	}

	@Override
	public Optional<TallThing> getById(long id) {
		return STORE.parallelStream()
				.filter(t -> t.getId() == id)
				.findFirst();
	}

	@Override
	public Stream<TallThing> getMajor() {
		return STORE.parallelStream()
				.filter(t -> t.getHeight() >= significantHeight);
				
				
	}

}
