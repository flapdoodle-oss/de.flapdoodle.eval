package de.flapdoodle.eval.config;

import de.flapdoodle.eval.evaluatables.*;
import de.flapdoodle.eval.evaluatables.basic.IndexedAccess;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;
import de.flapdoodle.eval.evaluatables.arithmetic.Trigonometric;
import de.flapdoodle.eval.evaluatables.datetime.Legacy;
import de.flapdoodle.eval.evaluatables.string.Contains;

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

    private static final TypedEvaluatableByName EVALUATABLES = defaultEvaluatablesMap();
    private static final TypedEvaluatableByNumberOfArguments ARRAY_ACCESS = new IndexedAccess();
    private static final OperatorMap OPERATOR_MAP = defaultOperatorMap();

    private static final ZoneId ZONE_ID = ZoneId.systemDefault();

    public static MathContext mathContext() {
        return MATH_CONTEXT;
    }

    public static ValueResolver constants() {
        return CONSTANTS;
    }

    public static TypedEvaluatableByName evaluatables() {
        return EVALUATABLES;
    }

    public static OperatorMap operatorMap() {
        return OPERATOR_MAP;
    }

    public static ZoneId zoneId() {
        return ZONE_ID;
    }

    private static OperatorMap defaultOperatorMap() {
        return OperatorMap.builder()
          .putPrefix("+", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_UNARY, false, "sum"))
          .putPrefix("-", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_UNARY, false, "minus"))
          .putPrefix("!", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_UNARY, false, "not"))

          .putInfix("+", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_ADDITIVE, "sum"))
          .putInfix("-", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_ADDITIVE, "minus"))
          .putInfix("*", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_MULTIPLICATIVE,  "multiply"))
          .putInfix("/", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_MULTIPLICATIVE,  "divide"))
          .putInfix("^", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_POWER, false,  "power"))
          .putInfix("%", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_MULTIPLICATIVE,  "modulo"))
          // booleans
          .putInfix("=", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_EQUALITY, "equal"))
          .putInfix("==", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_EQUALITY, "equal"))
          .putInfix("===", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_EQUALITY, "same"))
          .putInfix("!=", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_EQUALITY, "notequal"))
          .putInfix("<>", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_EQUALITY, "notequal"))
          .putInfix(">", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_COMPARISON, "greater"))
          .putInfix(">=", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_COMPARISON, "greaterOrEqual"))
          .putInfix("<", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_COMPARISON, "less"))
          .putInfix("<=", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_COMPARISON, "lessOrEqual"))
          .putInfix("&&", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_AND, "and"))
          .putInfix("||", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_OR, "or"))
          .putPrefix("!", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_UNARY, "not"))
          .build();
    }

    public static final TypedEvaluatableByNumberOfArguments arrayAccess() {
        return ARRAY_ACCESS;
    }

    private static TypedEvaluatableByName defaultEvaluatablesMap() {
        return TypedEvaluatablesMap.builder()
                .putMap("abs", new de.flapdoodle.eval.evaluatables.arithmetic.Abs())
                .putMap("ceiling", de.flapdoodle.eval.evaluatables.basic.Round.ceiling()) // deprecated
                .putMap("factorial", new de.flapdoodle.eval.evaluatables.arithmetic.Factorial())
                .putMap("floor", de.flapdoodle.eval.evaluatables.basic.Round.floor()) // deprecated

                .putMap("if", new de.flapdoodle.eval.evaluatables.basic.Conditional())

                .putMap("log", new de.flapdoodle.eval.evaluatables.arithmetic.Log())
                .putMap("log10", new de.flapdoodle.eval.evaluatables.arithmetic.Log10())
                .putMap("max", new de.flapdoodle.eval.evaluatables.arithmetic.Max())
                .putMap("min", new de.flapdoodle.eval.evaluatables.arithmetic.Min())
                .putMap("not", new de.flapdoodle.eval.evaluatables.booleans.Not())
                .putMap("random", new de.flapdoodle.eval.evaluatables.arithmetic.Random())
                .putMap("round", new de.flapdoodle.eval.evaluatables.basic.Round())
                .putMap("sum", new de.flapdoodle.eval.evaluatables.arithmetic.Plus())
                .putMap("sqrt", new de.flapdoodle.eval.evaluatables.arithmetic.Sqrt())
                // trigonometric
                .putMap("acos", Trigonometric.acos())
                .putMap("acosH", Trigonometric.acosH())
                .putMap("acosR", Trigonometric.acosR())
                .putMap("acot", Trigonometric.acot())
                .putMap("acotH", Trigonometric.acotH())
                .putMap("acotR", Trigonometric.acotR())
                .putMap("asin", Trigonometric.asin())
                .putMap("asinH", Trigonometric.asinH())
                .putMap("asinR", Trigonometric.asinR())
                .putMap("atan", Trigonometric.atan())
                .putMap("atan2", Trigonometric.atan2())
                .putMap("atan2R", Trigonometric.atan2R())
                .putMap("atanH", Trigonometric.atanH())
                .putMap("atanR", Trigonometric.atanR())
                .putMap("cos", Trigonometric.cos())
                .putMap("cosH", Trigonometric.cosH())
                .putMap("cosR", Trigonometric.cosR())
                .putMap("cot", Trigonometric.cot())
                .putMap("cotH", Trigonometric.cotH())
                .putMap("cotR", Trigonometric.cotR())
                .putMap("csc", Trigonometric.csc())
                .putMap("cscH", Trigonometric.cscH())
                .putMap("cscR", Trigonometric.cscR())
                .putMap("deg", Trigonometric.deg())
                .putMap("rad", Trigonometric.rad())
                .putMap("sin", Trigonometric.sin())
                .putMap("sinH", Trigonometric.sinH())
                .putMap("sinR", Trigonometric.sinR())
                .putMap("sec", Trigonometric.sec())
                .putMap("secH", Trigonometric.secH())
                .putMap("secR", Trigonometric.secR())
                .putMap("tan", Trigonometric.tan())
                .putMap("tanH", Trigonometric.tanH())
                .putMap("tanR", Trigonometric.tanR())
                // string functions
                .putMap("contains", new Contains())
                .putMap("toLower", new de.flapdoodle.eval.evaluatables.string.ToLowerCase())
                .putMap("toUpper", new de.flapdoodle.eval.evaluatables.string.ToUpperCase())
                // date time functions
                .putMap("localDateTime", new de.flapdoodle.eval.evaluatables.datetime.CreateLocalDateTime())
                .putMap("parseLocalDateTime", new de.flapdoodle.eval.evaluatables.datetime.DateTimeParser())
                .putMap("parseZonedDateTime", new de.flapdoodle.eval.evaluatables.datetime.ZonedDateTimeParser())
                .putMap("formatLocalDateTime", new de.flapdoodle.eval.evaluatables.datetime.FormatDateTime())
                .putMap("localDateTime2EpochMilli", Legacy.dateTime2Epoch())
                .putMap("localDateTimeOfEpochMilli", Legacy.epochFromNumber())
                .putMap("durationOfMillis", de.flapdoodle.eval.evaluatables.datetime.DurationParser.ofMillis())
                .putMap("durationOfDays", de.flapdoodle.eval.evaluatables.datetime.DurationParser.ofDays())
                .putMap("parseDuration", de.flapdoodle.eval.evaluatables.datetime.DurationParser.parseDuration())

                // operators only
                .putMap("minus", new de.flapdoodle.eval.evaluatables.arithmetic.Minus())
                .putMap("multiply", new de.flapdoodle.eval.evaluatables.arithmetic.Multiply())
                .putMap("divide", new de.flapdoodle.eval.evaluatables.arithmetic.Divide())
                .putMap("power", new de.flapdoodle.eval.evaluatables.arithmetic.PowerOf())
                .putMap("modulo", new de.flapdoodle.eval.evaluatables.arithmetic.Modulo())
                .putMap("equal", new de.flapdoodle.eval.evaluatables.basic.Equals())
                .putMap("same", new de.flapdoodle.eval.evaluatables.basic.Same())
                .putMap("notequal", de.flapdoodle.eval.evaluatables.basic.Equals.not())
                .putMap("greater", de.flapdoodle.eval.evaluatables.basic.Comparables.greater())
                .putMap("greaterOrEqual", de.flapdoodle.eval.evaluatables.basic.Comparables.greaterOrEqual())
                .putMap("less", de.flapdoodle.eval.evaluatables.basic.Comparables.less())
                .putMap("lessOrEqual", de.flapdoodle.eval.evaluatables.basic.Comparables.lessOrEqual())
                .putMap("and", de.flapdoodle.eval.evaluatables.booleans.Combine.and())
                .putMap("or", de.flapdoodle.eval.evaluatables.booleans.Combine.or())
                .build();
    }
}
