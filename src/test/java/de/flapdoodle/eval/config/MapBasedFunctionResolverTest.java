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
import de.flapdoodle.eval.functions.basic.Max;
import de.flapdoodle.eval.functions.basic.Min;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MapBasedFunctionResolverTest {

  @Test
  void testCreationOfFunctions() {
    Function min = new Min();
    Function max = new Max();

    @SuppressWarnings({"unchecked", "varargs"})
		FunctionResolver dictionary =
        MapBasedFunctionResolver.builder().putFunctions("min", min).putFunctions("max", max).build();

    assertThat(dictionary.hasFunction("min")).isTrue();
    assertThat(dictionary.hasFunction("max")).isTrue();

    assertThat(dictionary.getFunction("min")).isEqualTo(min);
    assertThat(dictionary.getFunction("max")).isEqualTo(max);

    assertThat(dictionary.hasFunction("medium")).isFalse();
  }

  @Test
  void testCaseInsensitivity() {
    Function min = new Min();
    Function max = new Max();

    @SuppressWarnings({"unchecked", "varargs"})
		FunctionResolver dictionary =
      MapBasedFunctionResolver.builder().putFunctions("Min", min).putFunctions("MAX", max).build();

    assertThat(dictionary.hasFunction("min")).isTrue();
    assertThat(dictionary.hasFunction("MIN")).isTrue();
    assertThat(dictionary.hasFunction("Min")).isTrue();
    assertThat(dictionary.hasFunction("max")).isTrue();
    assertThat(dictionary.hasFunction("MAX")).isTrue();
    assertThat(dictionary.hasFunction("Max")).isTrue();
  }
}
