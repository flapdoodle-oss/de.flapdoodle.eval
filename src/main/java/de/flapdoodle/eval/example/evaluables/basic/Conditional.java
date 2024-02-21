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
package de.flapdoodle.eval.example.evaluables.basic;

import de.flapdoodle.eval.core.EvaluationContext;
import de.flapdoodle.eval.core.VariableResolver;
import de.flapdoodle.eval.core.evaluables.Parameter;
import de.flapdoodle.eval.core.evaluables.TypedEvaluable;
import de.flapdoodle.eval.core.evaluables.TypedEvaluables;
import de.flapdoodle.eval.core.exceptions.EvaluationException;
import de.flapdoodle.eval.core.parser.Token;
import de.flapdoodle.eval.example.Value;

public class Conditional extends TypedEvaluables.Wrapper {

    public static class IfTrue<SUB extends Value<?>> implements TypedEvaluable.Arg3<Value.BooleanValue, SUB, SUB, SUB> {

        @Override
        public SUB evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, Value.BooleanValue first, SUB second, SUB third) throws EvaluationException {
            return first.wrapped() ? second : third;
        }
    }

    public Conditional() {
        super(TypedEvaluables.builder()
                .addList(
									TypedEvaluable.of(Value.class, Parameter.of(Value.BooleanValue.class), Parameter.lazyWith(Value.class), Parameter.lazyWith(Value.class), new IfTrue<>()))
                .build());
    }
}
