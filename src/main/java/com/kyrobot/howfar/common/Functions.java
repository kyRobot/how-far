package com.kyrobot.howfar.common;

import static java.lang.Double.parseDouble;

import java.math.BigDecimal;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Utilities class holding useful {@link Function}s
 */
public class Functions {

	private Functions() {
		// prevent instantiation
	}

	public static BiFunction<Integer, Double, Double> toNplaces = (n, val) -> BigDecimal.valueOf(val)
			.setScale(n, BigDecimal.ROUND_HALF_UP).doubleValue();

	public static final BiFunction<Double, String, Double> converter = (rate, value) -> rate * parseDouble(value);

}
