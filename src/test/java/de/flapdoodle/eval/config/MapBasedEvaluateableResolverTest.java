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

class MapBasedEvaluateableResolverTest {

  @Test
  void testCreationOfFunctions() {
    Function min = new Min();
    Function max = new Max();

    @SuppressWarnings({"unchecked", "varargs"})
		EvaluateableResolver dictionary =
        MapBasedEvaluateableResolver.builder().putMap("min", min).putMap("max", max).build();

    assertThat(dictionary.has("min")).isTrue();
    assertThat(dictionary.has("max")).isTrue();

    assertThat(dictionary.get("min")).isEqualTo(min);
    assertThat(dictionary.get("max")).isEqualTo(max);

    assertThat(dictionary.has("medium")).isFalse();
  }

  @Test
  void testCaseInsensitivity() {
    Function min = new Min();
    Function max = new Max();

    @SuppressWarnings({"unchecked", "varargs"})
    EvaluateableResolver dictionary =
      MapBasedEvaluateableResolver.builder().putMap("Min", min).putMap("MAX", max).build();

    assertThat(dictionary.has("min")).isTrue();
    assertThat(dictionary.has("MIN")).isTrue();
    assertThat(dictionary.has("Min")).isTrue();
    assertThat(dictionary.has("max")).isTrue();
    assertThat(dictionary.has("MAX")).isTrue();
    assertThat(dictionary.has("Max")).isTrue();
  }
}
