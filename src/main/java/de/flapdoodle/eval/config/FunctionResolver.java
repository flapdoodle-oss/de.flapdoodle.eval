/**
 * Copyright (C) 2023
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.eval.config;

import de.flapdoodle.eval.functions.Function;
import de.flapdoodle.eval.functions.basic.*;
import de.flapdoodle.eval.functions.datetime.*;
import de.flapdoodle.eval.functions.string.CaseInsensitiveContains;
import de.flapdoodle.eval.functions.string.ToLowerCase;
import de.flapdoodle.eval.functions.string.ToUpperCase;
import de.flapdoodle.eval.functions.trigonometric.*;

public interface FunctionResolver {
	Function get(String functionName);

	default boolean hasFunction(String functionName) {
		return get(functionName) != null;
	}

	default FunctionResolver andThen(FunctionResolver fallback) {
		FunctionResolver that=this;

		return functionName -> {
			Function function = that.get(functionName);
			if (function==null) {
				return fallback.get(functionName);
			}
			return function;
		};
	}

	static FunctionResolver defaults() {
		return MapBasedFunctionResolver.builder()
			.putFunctions("ABS", new Abs())
			.putFunctions("CEILING", new RoundCeiling())
			.putFunctions("FACT", new Factorial())
			.putFunctions("FLOOR", new RoundFloor())
			.putFunctions("IF", new Conditional())
			.putFunctions("LOG", new Log())
			.putFunctions("LOG10", new Log10())
			.putFunctions("MAX", new Max())
			.putFunctions("MIN", new Min())
			.putFunctions("NOT", new Not())
			.putFunctions("RANDOM", new RandomNumber())
			.putFunctions("ROUND", new Round())
			.putFunctions("SUM", new Sum())
			.putFunctions("SQRT", new Sqrt())
//			// trigonometric
			.putFunctions("ACOS", new Acos())
			.putFunctions("ACOSH", new AcosH())
			.putFunctions("ACOSR", new AcosRadians())
			.putFunctions("ACOT", new Acot())
			.putFunctions("ACOTH", new AcotH())
			.putFunctions("ACOTR", new AcotRadians())
			.putFunctions("ASIN", new Asin())
			.putFunctions("ASINH", new AsinH())
			.putFunctions("ASINR", new AsinRadians())
			.putFunctions("ATAN", new Atan())
			.putFunctions("ATAN2", new Atan2())
			.putFunctions("ATAN2R", new Atan2Radians())
			.putFunctions("ATANH", new AtanH())
			.putFunctions("ATANR", new AtanR())
			.putFunctions("COS", new Cos())
			.putFunctions("COSH", new CosH())
			.putFunctions("COSR", new CosRadians())
			.putFunctions("COT", new Cot())
			.putFunctions("COTH", new CotH())
			.putFunctions("COTR", new CotR())
			.putFunctions("CSC", new Csc())
			.putFunctions("CSCH", new CscH())
			.putFunctions("CSCR", new CscRadians())
			.putFunctions("DEG", new Deg())
			.putFunctions("RAD", new Rad())
			.putFunctions("SIN", new Sin())
			.putFunctions("SINH", new SinH())
			.putFunctions("SINR", new SinRadians())
			.putFunctions("SEC", new Sec())
			.putFunctions("SECH", new SecH())
			.putFunctions("SECR", new SecRadians())
			.putFunctions("TAN", new Tan())
			.putFunctions("TANH", new TanH())
			.putFunctions("TANR", new TanR())
			// string functions
			.putFunctions("STR_CONTAINS", new CaseInsensitiveContains())
			.putFunctions("STR_LOWER", new ToLowerCase())
			.putFunctions("STR_UPPER", new ToUpperCase())
			// date time functions
			.putFunctions("DT_DATE_TIME", new CreateLocalDateTime())
			.putFunctions("DT_PARSE", new DateTimeParser())
			.putFunctions("DT_ZONED_PARSE", new ZonedDateTimeParser())
			.putFunctions("DT_FORMAT", new FormatDateTime())
			.putFunctions("DT_EPOCH", new DateTime2Epoch())
			.putFunctions("DT_DATE_TIME_EPOCH", new EpochFromNumber())
			.putFunctions("DT_DURATION_MILLIS", new DurationMillisFromNumber())
			.putFunctions("DT_DURATION_DAYS", new DurationDaysFromNumber())
			.putFunctions("DT_DURATION_PARSE", new DurationParser())
			.build();
	}

}
