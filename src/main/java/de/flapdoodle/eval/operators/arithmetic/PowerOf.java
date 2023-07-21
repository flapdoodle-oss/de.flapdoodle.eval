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
package de.flapdoodle.eval.operators.arithmetic;

import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.Expression;
import de.flapdoodle.eval.data.Value;
import de.flapdoodle.eval.operators.AbstractInfixOperator;
import de.flapdoodle.eval.operators.Precedence;
import de.flapdoodle.eval.parser.Token;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class PowerOf extends AbstractInfixOperator {

  public PowerOf() {
    super(Precedence.OPERATOR_PRECEDENCE_POWER, false);
  }

  @Override public Value<?> evaluate(Expression expression, Token operatorToken, Value<?> leftOperand, Value<?> rightOperand) throws EvaluationException {
    return evaluate(operatorToken, leftOperand, rightOperand)
      .using(Value.NumberValue.class, Value.NumberValue.class, (l,r) -> {
        /*-
         * Thanks to Gene Marin:
         * http://stackoverflow.com/questions/3579779/how-to-do-a-fractional-power-on-bigdecimal-in-java
         */

        MathContext mathContext = expression.getConfiguration().getMathContext();
        BigDecimal v1 = l.wrapped();
        BigDecimal v2 = r.wrapped();

        int signOf2 = v2.signum();
        double dn1 = v1.doubleValue();
        v2 = v2.multiply(new BigDecimal(signOf2)); // n2 is now positive
        BigDecimal remainderOf2 = v2.remainder(BigDecimal.ONE);
        BigDecimal n2IntPart = v2.subtract(remainderOf2);
        BigDecimal intPow = v1.pow(n2IntPart.intValueExact(), mathContext);
        BigDecimal doublePow = BigDecimal.valueOf(Math.pow(dn1, remainderOf2.doubleValue()));

        BigDecimal result = intPow.multiply(doublePow, mathContext);
        if (signOf2 == -1) {
          result = BigDecimal.ONE.divide(result, mathContext.getPrecision(), RoundingMode.HALF_UP);
        }
        return Value.of(result);
      })
      .get();
  }
}
