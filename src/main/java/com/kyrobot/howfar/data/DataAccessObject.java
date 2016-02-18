package com.kyrobot.howfar.data;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * DAO Pattern Interface for all Data Access Object implementations to 
 * conform to
 * 
 * @param <T> a model object representation of a datasource stored item
 */
public interface DataAccessObject<T> {
	
	/**
	 * @return a {@link Stream} of all T in the datasource 
	 */
	Stream<T> getAll();
	
	
	/**
	 * @param id a unique identifier for a T in the datasource
	 * @return an {@link Optional} containing the T represented by
	 * the given id if found in the datasource, otherwise Empty
	 */
	Optional<T> getById(long id);
	

}
