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
package de.flapdoodle.eval.functions;

import de.flapdoodle.eval.data.Value;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FunctionParameterDefinitionTest {

  @Test
  void testCreation() {
    FunctionParameterDefinition definition =
        FunctionParameterDefinition.builder(Value.StringValue.class)
            .name("name")
            .isVarArg(true)
            .isLazy(true)
            .build();

    assertThat(definition.getName()).isEqualTo("name");
    assertThat(definition.parameterType()).isEqualTo(Value.StringValue.class);
    assertThat(definition.isVarArg()).isTrue();
    assertThat(definition.isLazy()).isTrue();
  }
}
