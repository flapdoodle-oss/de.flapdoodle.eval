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

	private static final MathContext MATH_CONTEXT = new MathContext(68, RoundingMode.HALF_EVEN);
	private static final ValueResolver CONSTANTS = ValueResolver.empty().withValues(standardConstants());

	private static final EvaluateableResolver FUNCTIONS = defaultFunctions();
	private static final OperatorResolver OPERATORS = OperatorResolver.defaults();

	public static MathContext mathContext() {
		return MATH_CONTEXT;
	}

	public static ValueResolver constants() {
		return CONSTANTS;
	}

	public static EvaluateableResolver functions() {
		return FUNCTIONS;
	}

	public static OperatorResolver operators() {
		return OPERATORS;
	}

	private static EvaluateableResolver defaultFunctions() {
		return MapBasedEvaluateableResolver.builder()
			.putMap("ABS", new Abs())
			.putMap("CEILING", new RoundCeiling())
			.putMap("FACT", new Factorial())
			.putMap("FLOOR", new RoundFloor())
			.putMap("IF", new Conditional())
			.putMap("LOG", new Log())
			.putMap("LOG10", new Log10())
			.putMap("MAX", new Max())
			.putMap("MIN", new Min())
			.putMap("NOT", new Not())
			.putMap("RANDOM", new RandomNumber())
			.putMap("ROUND", new Round())
			.putMap("SUM", new Sum())
			.putMap("SQRT", new Sqrt())
//			// trigonometric
			.putMap("ACOS", new Acos())
			.putMap("ACOSH", new AcosH())
			.putMap("ACOSR", new AcosRadians())
			.putMap("ACOT", new Acot())
			.putMap("ACOTH", new AcotH())
			.putMap("ACOTR", new AcotRadians())
			.putMap("ASIN", new Asin())
			.putMap("ASINH", new AsinH())
			.putMap("ASINR", new AsinRadians())
			.putMap("ATAN", new Atan())
			.putMap("ATAN2", new Atan2())
			.putMap("ATAN2R", new Atan2Radians())
			.putMap("ATANH", new AtanH())
			.putMap("ATANR", new AtanR())
			.putMap("COS", new Cos())
			.putMap("COSH", new CosH())
			.putMap("COSR", new CosRadians())
			.putMap("COT", new Cot())
			.putMap("COTH", new CotH())
			.putMap("COTR", new CotR())
			.putMap("CSC", new Csc())
			.putMap("CSCH", new CscH())
			.putMap("CSCR", new CscRadians())
			.putMap("DEG", new Deg())
			.putMap("RAD", new Rad())
			.putMap("SIN", new Sin())
			.putMap("SINH", new SinH())
			.putMap("SINR", new SinRadians())
			.putMap("SEC", new Sec())
			.putMap("SECH", new SecH())
			.putMap("SECR", new SecRadians())
			.putMap("TAN", new Tan())
			.putMap("TANH", new TanH())
			.putMap("TANR", new TanR())
			// string functions
			.putMap("STR_CONTAINS", new CaseInsensitiveContains())
			.putMap("STR_LOWER", new ToLowerCase())
			.putMap("STR_UPPER", new ToUpperCase())
			// date time functions
			.putMap("DT_DATE_TIME", new CreateLocalDateTime())
			.putMap("DT_PARSE", new DateTimeParser())
			.putMap("DT_ZONED_PARSE", new ZonedDateTimeParser())
			.putMap("DT_FORMAT", new FormatDateTime())
			.putMap("DT_EPOCH", new DateTime2Epoch())
			.putMap("DT_DATE_TIME_EPOCH", new EpochFromNumber())
			.putMap("DT_DURATION_MILLIS", new DurationMillisFromNumber())
			.putMap("DT_DURATION_DAYS", new DurationDaysFromNumber())
			.putMap("DT_DURATION_PARSE", new DurationParser())
			.build();
	}

}
