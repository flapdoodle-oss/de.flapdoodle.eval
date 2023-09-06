package de.flapdoodle.eval.config;

import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.functions.basic.*;
import de.flapdoodle.eval.functions.datetime.*;
import de.flapdoodle.eval.functions.string.CaseInsensitiveContains;
import de.flapdoodle.eval.functions.string.ToLowerCase;
import de.flapdoodle.eval.functions.string.ToUpperCase;
import de.flapdoodle.eval.functions.trigonometric.*;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class Defaults {
	private Defaults() {
		// no instance
	}

	static Map<String, Value<?>> standardConstants() {
		Map<String, Value<?>> constants = new LinkedHashMap<>();

		constants.put("true", Value.of(true));
		constants.put("false", Value.of(false));
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
		constants.put("null", Value.ofNull());
		return Collections.unmodifiableMap(constants);
	}

	private static final MathContext MATH_CONTEXT = new MathContext(68, RoundingMode.HALF_EVEN);
	private static final ValueResolver CONSTANTS = ValueResolver.empty().withValues(standardConstants());

	private static final EvaluateableResolver FUNCTIONS = defaultFunctions();
	private static final MapBasedOperatorResolver OPERATORS = OperatorResolver.defaults();

	private static final ZoneId ZONE_ID=ZoneId.systemDefault();

	public static MathContext mathContext() {
		return MATH_CONTEXT;
	}

	public static ValueResolver constants() {
		return CONSTANTS;
	}

	public static EvaluateableResolver functions() {
		return FUNCTIONS;
	}

	public static MapBasedOperatorResolver operators() {
		return OPERATORS;
	}

	public static ZoneId zoneId() {
		return ZONE_ID;
	}

	private static EvaluateableResolver defaultFunctions() {
		return MapBasedEvaluateableResolver.builder()
			.putMap("abs", new Abs())
			.putMap("ceiling", new RoundCeiling())
			.putMap("factorial", new Factorial())
			.putMap("floor", new RoundFloor())
			.putMap("if", new Conditional())
			.putMap("log", new Log())
			.putMap("log10", new Log10())
			.putMap("max", new Max())
			.putMap("min", new Min())
			.putMap("not", new Not())
			.putMap("random", new RandomNumber())
			.putMap("round", new Round())
			.putMap("sum", new Sum())
			.putMap("sqrt", new Sqrt())
//			// trigonometric
			.putMap("acos", new Acos())
			.putMap("acosH", new AcosH())
			.putMap("acosR", new AcosRadians())
			.putMap("acot", new Acot())
			.putMap("acotH", new AcotH())
			.putMap("acotR", new AcotRadians())
			.putMap("asin", new Asin())
			.putMap("asinH", new AsinH())
			.putMap("asinR", new AsinRadians())
			.putMap("atan", new Atan())
			.putMap("atan2", new Atan2())
			.putMap("atan2R", new Atan2Radians())
			.putMap("atanH", new AtanH())
			.putMap("atanR", new AtanR())
			.putMap("cos", new Cos())
			.putMap("cosH", new CosH())
			.putMap("cosR", new CosRadians())
			.putMap("cot", new Cot())
			.putMap("cotH", new CotH())
			.putMap("cotR", new CotR())
			.putMap("csc", new Csc())
			.putMap("cscH", new CscH())
			.putMap("cscR", new CscRadians())
			.putMap("deg", new Deg())
			.putMap("rad", new Rad())
			.putMap("sin", new Sin())
			.putMap("sinH", new SinH())
			.putMap("sinR", new SinRadians())
			.putMap("sec", new Sec())
			.putMap("secH", new SecH())
			.putMap("secR", new SecRadians())
			.putMap("tan", new Tan())
			.putMap("tanH", new TanH())
			.putMap("tanR", new TanR())
			// string functions
			.putMap("contains", new CaseInsensitiveContains())
			.putMap("toLower", new ToLowerCase())
			.putMap("toUpper", new ToUpperCase())
			// date time functions
			.putMap("localDateTime", new CreateLocalDateTime())
			.putMap("parseLocalDateTime", new DateTimeParser())
			.putMap("parseZonedDateTime", new ZonedDateTimeParser())
			.putMap("formatLocalDateTime", new FormatDateTime())
			.putMap("localDateTime2EpochMilli", new DateTime2Epoch())
			.putMap("localDateTimeOfEpochMilli", new EpochFromNumber())
			.putMap("durationOfMillis", new DurationMillisFromNumber())
			.putMap("durationOfDays", new DurationDaysFromNumber())
			.putMap("parseDuration", new DurationParser())
			.build();
	}

}
