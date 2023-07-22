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
package de.flapdoodle.eval.operators;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// löschen??
class OperatorTest {

  @Test
  void testPrefixOperator() {
    Operator operator = new CorrectPrefixOperator();

    assertThat(operator.getPrecedence()).isEqualTo(Precedence.OPERATOR_PRECEDENCE_UNARY.value());
    assertThat(operator.isLeftAssociative()).isFalse();
  }

  @Test
  void testPostfixOperator() {
    Operator operator = new CorrectPostfixOperator();

    assertThat(operator.getPrecedence()).isEqualTo(88);
    assertThat(operator.isLeftAssociative()).isTrue();
  }

  @Test
  void testInfixOperator() {
    Operator operator = new CorrectInfixOperator();

    assertThat(operator.getPrecedence()).isEqualTo(Precedence.OPERATOR_PRECEDENCE_MULTIPLICATIVE.value());
    assertThat(operator.isLeftAssociative()).isTrue();
  }

  private static class CorrectPrefixOperator extends DummyAnnotationOperator {
    protected CorrectPrefixOperator() {
      super(Precedence.OPERATOR_PRECEDENCE_UNARY, false);
    }
  }

  private static class CorrectPostfixOperator extends DummyAnnotationOperator {
    protected CorrectPostfixOperator() {
      super(88, true);
    }
  }

  private static class CorrectInfixOperator extends DummyAnnotationOperator {

    protected CorrectInfixOperator() {
      super(Precedence.OPERATOR_PRECEDENCE_MULTIPLICATIVE, true);
    }
  }

  private static class DummyAnnotationOperator extends AbstractBaseOperator {
    protected DummyAnnotationOperator(int precedence, boolean leftAssociative) {
      super(precedence, leftAssociative);
    }

    protected DummyAnnotationOperator(Precedence precedence, boolean leftAssociative) {
      super(precedence, leftAssociative);
    }
  }
}
