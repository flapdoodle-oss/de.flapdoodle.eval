/*
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
package de.flapdoodle.eval.core.evaluables;

import de.flapdoodle.eval.core.EvaluationContext;
import de.flapdoodle.eval.core.VariableResolver;
import de.flapdoodle.eval.core.exceptions.EvaluationException;
import de.flapdoodle.eval.core.parser.Token;

import java.util.List;

public interface Evaluable<T> {
    T evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, List<?> arguments) throws EvaluationException;

    default Evaluable<T> named(String name) {
        Evaluable<T> that = this;
        return new Evaluable<T>() {
            @Override
            public T evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, List<?> arguments)
              throws EvaluationException {
                return that.evaluate(variableResolver,evaluationContext,token,arguments);
            }

            @Override
            public String toString() {
                return name;
            }
        };
    }
}
