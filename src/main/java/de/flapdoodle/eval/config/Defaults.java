package de.flapdoodle.eval.config;

import de.flapdoodle.eval.data.Value;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class Defaults {
	private Defaults() {
		// no instance
	}

	static Map<String, Value<?>> standardConstants() {
		Map<String, Value<?>> constants = new LinkedHashMap<>();

		constants.put("TRUE", Value.of(true));
		constants.put("FALSE", Value.of(false));
		constants.put(
			"PI",
			Value.of(
				new BigDecimal(
					"3.1415926535897932384626433832795028841971693993751058209749445923078164062862089986280348253421170679")));
		constants.put(
			"E",
			Value.of(
				new BigDecimal(
					"2.71828182845904523536028747135266249775724709369995957496696762772407663")));
		constants.put("NULL", Value.ofNull());
		return Collections.unmodifiableMap(constants);
	}

	private static final MathContext MATH_CONTEXT=new MathContext(68, RoundingMode.HALF_EVEN);
	private static final ValueResolver CONSTANTS=ValueResolver.empty().withValues(standardConstants());
	private static final FunctionResolver FUNCTIONS=FunctionResolver.defaults();
	private static final OperatorResolver OPERATORS=OperatorResolver.defaults();

	public static MathContext mathContext() {
		return MATH_CONTEXT;
	}

	public static ValueResolver constants() {
		return CONSTANTS;
	}

	public static FunctionResolver functions() {
		return FUNCTIONS;
	}

	public static OperatorResolver operators() {
		return OPERATORS;
	}
}
