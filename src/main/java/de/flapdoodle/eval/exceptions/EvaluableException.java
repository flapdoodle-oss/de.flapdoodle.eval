/**
 * Copyright (C) 2023
 * Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.eval.exceptions;

public class EvaluableException extends Exception {

	public EvaluableException(String message) {
		super(message);
	}

	public static EvaluableException ofUnsupportedDataTypeInOperation() {
		return new EvaluableException("Unsupported data types in operation");
	}

	public static EvaluableException of(String message, Object ... args) {
		return new EvaluableException(String.format(message, (args)));
	}
}
