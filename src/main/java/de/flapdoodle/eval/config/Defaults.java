package de.flapdoodle.eval.config;

import de.flapdoodle.eval.VariableResolver;
import de.flapdoodle.eval.evaluables.*;
import de.flapdoodle.eval.evaluables.arithmetic.Trigonometric;
import de.flapdoodle.eval.evaluables.basic.IndexedAccess;
import de.flapdoodle.eval.evaluables.basic.PropertyAccess;
import de.flapdoodle.eval.evaluables.datetime.Legacy;
import de.flapdoodle.eval.evaluables.string.Contains;
import de.flapdoodle.eval.values.Value;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;

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

    private static final VariableResolver CONSTANTS = VariableResolver.empty().withValues(standardConstants());

    private static final TypedEvaluableByName EVALUATABLES = defaultEvaluatablesMap();
    private static final TypedEvaluableByNumberOfArguments ARRAY_ACCESS = new IndexedAccess();
    private static final TypedEvaluableByNumberOfArguments PROPERTY_ACCESS = new PropertyAccess();
    private static final OperatorMap OPERATOR_MAP = defaultOperatorMap();

    public static VariableResolver constants() {
        return CONSTANTS;
    }

    public static TypedEvaluableByName evaluatables() {
        return EVALUATABLES;
    }

    public static OperatorMap operatorMap() {
        return OPERATOR_MAP;
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

    public static final TypedEvaluableByNumberOfArguments arrayAccess() {
        return ARRAY_ACCESS;
    }
    public static final TypedEvaluableByNumberOfArguments propertyAccess() {
        return PROPERTY_ACCESS;
    }

    private static TypedEvaluableByName defaultEvaluatablesMap() {
        return TypedEvaluableMap.builder()
                .putMap("abs", new de.flapdoodle.eval.evaluables.arithmetic.Abs())
                .putMap("ceiling", de.flapdoodle.eval.evaluables.basic.Round.ceiling()) // deprecated
                .putMap("factorial", new de.flapdoodle.eval.evaluables.arithmetic.Factorial())
                .putMap("floor", de.flapdoodle.eval.evaluables.basic.Round.floor()) // deprecated

                .putMap("if", new de.flapdoodle.eval.evaluables.basic.Conditional())

                .putMap("log", new de.flapdoodle.eval.evaluables.arithmetic.Log())
                .putMap("log10", new de.flapdoodle.eval.evaluables.arithmetic.Log10())
                .putMap("max", new de.flapdoodle.eval.evaluables.arithmetic.Max())
                .putMap("min", new de.flapdoodle.eval.evaluables.arithmetic.Min())
                .putMap("not", new de.flapdoodle.eval.evaluables.booleans.Not())
                .putMap("random", new de.flapdoodle.eval.evaluables.arithmetic.Random())
                .putMap("round", new de.flapdoodle.eval.evaluables.basic.Round())
                .putMap("sum", new de.flapdoodle.eval.evaluables.arithmetic.Plus())
                .putMap("sqrt", new de.flapdoodle.eval.evaluables.arithmetic.Sqrt())
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
                .putMap("toLower", new de.flapdoodle.eval.evaluables.string.ToLowerCase())
                .putMap("toUpper", new de.flapdoodle.eval.evaluables.string.ToUpperCase())
                // date time functions
                .putMap("localDateTime", new de.flapdoodle.eval.evaluables.datetime.CreateLocalDateTime())
                .putMap("parseLocalDateTime", new de.flapdoodle.eval.evaluables.datetime.DateTimeParser())
                .putMap("parseZonedDateTime", new de.flapdoodle.eval.evaluables.datetime.ZonedDateTimeParser())
                .putMap("formatLocalDateTime", new de.flapdoodle.eval.evaluables.datetime.FormatDateTime())
                .putMap("localDateTime2EpochMilli", Legacy.dateTime2Epoch())
                .putMap("localDateTimeOfEpochMilli", Legacy.epochFromNumber())
                .putMap("durationOfMillis", de.flapdoodle.eval.evaluables.datetime.DurationParser.ofMillis())
                .putMap("durationOfDays", de.flapdoodle.eval.evaluables.datetime.DurationParser.ofDays())
                .putMap("parseDuration", de.flapdoodle.eval.evaluables.datetime.DurationParser.parseDuration())

                // operators only
                .putMap("minus", new de.flapdoodle.eval.evaluables.arithmetic.Minus())
                .putMap("multiply", new de.flapdoodle.eval.evaluables.arithmetic.Multiply())
                .putMap("divide", new de.flapdoodle.eval.evaluables.arithmetic.Divide())
                .putMap("power", new de.flapdoodle.eval.evaluables.arithmetic.PowerOf())
                .putMap("modulo", new de.flapdoodle.eval.evaluables.arithmetic.Modulo())
                .putMap("equal", new de.flapdoodle.eval.evaluables.basic.Equals())
                .putMap("same", new de.flapdoodle.eval.evaluables.basic.Same())
                .putMap("notequal", de.flapdoodle.eval.evaluables.basic.Equals.not())
                .putMap("greater", de.flapdoodle.eval.evaluables.basic.Comparables.greater())
                .putMap("greaterOrEqual", de.flapdoodle.eval.evaluables.basic.Comparables.greaterOrEqual())
                .putMap("less", de.flapdoodle.eval.evaluables.basic.Comparables.less())
                .putMap("lessOrEqual", de.flapdoodle.eval.evaluables.basic.Comparables.lessOrEqual())
                .putMap("and", de.flapdoodle.eval.evaluables.booleans.Combine.and())
                .putMap("or", de.flapdoodle.eval.evaluables.booleans.Combine.or())
                .build();
    }

    public static Value.NumberValue numberFromString(String value, MathContext mathContext) {
        if (value.startsWith("0x") || value.startsWith("0X")) {
            BigInteger hexToInteger = new BigInteger(value.substring(2), 16);
            return Value.of(new BigDecimal(hexToInteger, mathContext));
        } else {
            return Value.of(new BigDecimal(value, mathContext));
        }
    }

    public static Value.StringValue valueFromString(String s) {
        return Value.of(s);
    }
}
