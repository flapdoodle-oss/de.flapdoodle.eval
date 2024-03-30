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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface TypedEvaluableByNumberOfArguments {
    Optional<? extends TypedEvaluableByArguments> filterByNumberOfArguments(int numberOfArguments);

    static List<TypedEvaluable<?>> filterByNumberOfArguments(List<TypedEvaluable<?>> list, int numberOfArguments) {
        List<TypedEvaluable<?>> filtered = list.stream()
          .filter(entry -> entry.signature().minNumberOfArguments() <= numberOfArguments && entry.signature().maxNumberOfArguments() >= numberOfArguments)
          .collect(Collectors.toList());
        
        return filtered;
//        return !filtered.isEmpty()
//          ? Optional.of(TypedEvaluables.builder().list(filtered).build())
//          : Optional.empty();
    }
}
