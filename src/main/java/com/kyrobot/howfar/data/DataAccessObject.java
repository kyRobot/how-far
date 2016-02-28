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
	
	/**
	 * @return a {@link Stream} of all T considered major/significant
	 * in the datasource e.g significance is implementation specific
	 */
	Stream<T> getMajor();
	
	/**
	 * @param target the target to match eg height, distance
	 * @param n the max number of T to find
	 * @return a {@link Stream} of all n Ts that closest match the
	 * 	supplied target. Implementers decide what constitutes
	 * 	a match.
	 */
	Stream<T> getMatches(int target, int n);
	

}
