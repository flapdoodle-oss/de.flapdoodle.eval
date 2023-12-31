## HowTo

```java
ExpressionFactory expressionFactory = Defaults.expressionFactory();
Expression expression = expressionFactory.parse("a*2");
Object result = expression.evaluate(VariableResolver.empty()
  .with("a", Value.of(2)));

assertThat(result).isEqualTo(Value.of(4.0));
```

```java
ExpressionFactory expressionFactory = Defaults.expressionFactory();
Expression expression = expressionFactory.parse("a*2");
assertThat(expression.usedVariables())
  .containsExactly("a");
```

```java
ImmutableTypedEvaluables add = TypedEvaluables.builder()
  .addList(TypedEvaluable.of(BigDecimal.class, BigDecimal.class, BigDecimal.class,
    (valueResolver, evaluationContext, token, first, second) -> first.add(second)))
  .build();

ExpressionFactory expressionFactory = ExpressionFactory.builder()
  .constants(VariableResolver.empty().with("pi", BigDecimal.valueOf(3.1415)))
  .evaluatables(TypedEvaluableMap.builder()
    .putMap("add", add)
    .build())
  .operatorMap(OperatorMap.builder()
    .putInfix("+", OperatorMapping.of(Precedence.OPERATOR_PRECEDENCE_ADDITIVE, "add"))
    .build())
  .arrayAccess(TypedEvaluables.builder()
    .addList(TypedEvaluable.of(String.class, String.class, BigDecimal.class,
      (valueResolver, evaluationContext, token, first, second) -> "" + first.charAt(second.intValue())))
    .build())
  .propertyAccess(TypedEvaluables.builder()
    .addList(TypedEvaluable.of(String.class, Map.class, String.class,
      (valueResolver, evaluationContext, token, first, second) -> "" + first.get(second)))
    .build())
  .numberAsValue((s, m) -> new BigDecimal(s))
  .stringAsValue(s -> s)
  .exceptionMapper(EvalFailedWithException.mapper())
  .build();

assertThat(expressionFactory.parse("pi").evaluate(VariableResolver.empty()))
  .isEqualTo(BigDecimal.valueOf(3.1415));
assertThat(expressionFactory.parse("add(2,3)").evaluate(VariableResolver.empty()))
  .isEqualTo(BigDecimal.valueOf(5L));
assertThat(expressionFactory.parse("2+3").evaluate(VariableResolver.empty()))
  .isEqualTo(BigDecimal.valueOf(5L));
assertThat(expressionFactory.parse("\"fun\"[1]").evaluate(VariableResolver.empty()))
  .isEqualTo("u");
MapBasedVariableResolver mapBasedValueResolver = VariableResolver.empty();
Map<Object, Object> value = new LinkedHashMap<>();
value.put("key", "stuff");
assertThat(expressionFactory.parse("map.key")
  .evaluate(mapBasedValueResolver.with("map", value)))
  .isEqualTo("stuff");
```