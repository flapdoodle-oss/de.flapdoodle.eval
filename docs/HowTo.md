## HowTo

```java
ExpressionFactory expressionFactory = ExpressionFactory.defaults();
Expression expression = expressionFactory.parse("a*2");
Object result = expression.evaluate(VariableResolver.empty()
  .with("a", Value.of(2)));

assertThat(result).isEqualTo(Value.of(4.0));
```

```java
ExpressionFactory expressionFactory = ExpressionFactory.defaults();
Expression expression = expressionFactory.parse("a*2");
assertThat(expression.usedVariables())
  .containsExactly("a");
```

```java
ExpressionFactory expressionFactory = ExpressionFactory.builder()
  .constants(VariableResolver.empty().with("pi",BigDecimal.valueOf(3.1415)))
  .evaluatables(TypedEvaluableMap.builder()
    .putMap("add", TypedEvaluables.builder()
      .addList(TypedEvaluable.of(Value.NumberValue.class, Value.NumberValue.class, Value.NumberValue.class,
        (valueResolver, evaluationContext, token, first, second) -> Value.of(first.wrapped().add(second.wrapped()))))
      .build())
    .build())
  .operatorMap(OperatorMap.builder()
    .putInfix("+", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_UNARY, "add"))
    .build())
  .arrayAccess(TypedEvaluables.builder()
    .addList(TypedEvaluable.of(Value.StringValue.class, Value.StringValue.class, Value.NumberValue.class,
      (valueResolver, evaluationContext, token, first, second) -> Value.of("" + first.wrapped().charAt(second.wrapped().intValue()))))
    .build())
  .propertyAccess(TypedEvaluables.builder()
    .addList(TypedEvaluable.of(Value.StringValue.class, Value.MapValue.class, Value.StringValue.class,
      (valueResolver, evaluationContext, token, first, second) -> Value.of("" + first.wrapped().get(second.wrapped()).wrapped())))
    .build())
  .build();

assertThat(expressionFactory.parse("pi").evaluate(VariableResolver.empty()))
  .isEqualTo(BigDecimal.valueOf(3.1415));
assertThat(expressionFactory.parse("add(2,3)").evaluate(VariableResolver.empty()))
  .isEqualTo(Value.of(BigDecimal.valueOf(5L)));
assertThat(expressionFactory.parse("2+3").evaluate(VariableResolver.empty()))
  .isEqualTo(Value.of(BigDecimal.valueOf(5L)));
assertThat(expressionFactory.parse("\"fun\"[1]").evaluate(VariableResolver.empty()))
  .isEqualTo(Value.of("u"));
    MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
    ValueMap value = ValueMap.builder()
        .putValues("key", Value.of("stuff"))
        .build();
    assertThat(expressionFactory.parse("map.key")
  .evaluate(mapBasedValueResolver.with("map", Value.of(value))))
  .isEqualTo(Value.of("stuff"));
```