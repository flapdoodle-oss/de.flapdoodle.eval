package de.flapdoodle.eval.evaluatables;

import de.flapdoodle.eval.EvaluationContext;
import de.flapdoodle.eval.EvaluationException;
import de.flapdoodle.eval.values.Value;
import de.flapdoodle.eval.values.ValueResolver;
import de.flapdoodle.eval.parser.Token;

import java.util.List;
import java.util.stream.Collectors;

public interface TypedEvaluatable<T extends Value<?>> extends Evaluatable<T> {
    Signature<T> signature();

    interface Arg0<T extends Value<?>> {
        T evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token) throws EvaluationException;
    }

    interface Arg1<S extends Value<?>, T extends Value<?>> {
        T evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, S argument) throws EvaluationException;
    }

    interface VarArg1<S extends Value<?>, T extends Value<?>> {
        T evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, List<S> arguments) throws EvaluationException;
    }

    interface Arg2<A extends Value<?>, B extends Value<?>, T extends Value<?>> {
        T evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, A first, B second) throws EvaluationException;
    }

    interface Arg3<A extends Value<?>, B extends Value<?>, C extends Value<?>, T extends Value<?>> {
        T evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, A first, B second, C third) throws EvaluationException;
    }

    interface Arg4<A extends Value<?>, B extends Value<?>, C extends Value<?>, D extends Value<?>, T extends Value<?>> {
        T evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, A first, B second, C third, D fourth) throws EvaluationException;
    }

    interface Arg5<A extends Value<?>, B extends Value<?>, C extends Value<?>, D extends Value<?>, E extends Value<?>, T extends Value<?>> {
        T evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, A first, B second, C third, D fourth, E fifth) throws EvaluationException;
    }

    interface Arg6<A extends Value<?>, B extends Value<?>, C extends Value<?>, D extends Value<?>, E extends Value<?>, F extends Value<?>, T extends Value<?>> {
        T evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, A first, B second, C third, D fourth, E fifth, F sixth) throws EvaluationException;
    }

    interface Arg7<A extends Value<?>, B extends Value<?>, C extends Value<?>, D extends Value<?>, E extends Value<?>, F extends Value<?>, G extends Value<?>, T extends Value<?>> {
        T evaluate(ValueResolver valueResolver, EvaluationContext evaluationContext, Token token, A first, B second, C third, D fourth, E fifth, F sixth, G seventh) throws EvaluationException;
    }

    static <T extends Value<?>> TypedEvaluatable<T> of(Class<T> returnType, TypedEvaluatable.Arg0<T> function) {
        Evaluatable<T> evaluatable = (valueResolver, evaluationContext, token, arguments) -> function.evaluate(valueResolver, evaluationContext, token);
        return new TypedEvaluatableAdapter<>(Signature.of(returnType), evaluatable.named(function.toString()));
    }

    static <T extends Value<?>, A extends Value<?>> TypedEvaluatable<T> of(Class<T> returnType, Parameter<A> a, TypedEvaluatable.Arg1<A, T> function) {
        Evaluatable<T> evaluatable = (valueResolver, evaluationContext, token, arguments) -> function.evaluate(valueResolver, evaluationContext, token,
                a.type().cast(arguments.get(0)));
        return new TypedEvaluatableAdapter<>(Signature.of(returnType, a), evaluatable.named(function.toString()));
    }

    static <T extends Value<?>, A extends Value<?>> TypedEvaluatable<T> of(Class<T> returnType, Class<A> a, TypedEvaluatable.Arg1<A, T> function) {
        return of(returnType, Parameter.of(a), function);
    }

    static <T extends Value<?>, A extends Value<?>> TypedEvaluatable<T> ofVarArg(Class<T> returnType, Class<A> a, TypedEvaluatable.VarArg1<A, T> function) {
        Evaluatable<T> evaluatable = (valueResolver, evaluationContext, token, arguments) -> function.evaluate(valueResolver, evaluationContext, token,
                arguments.stream()
                        .map(a::cast).collect(Collectors.toList()));
        return new TypedEvaluatableAdapter<>(Signature.ofVarArg(returnType, Parameter.of(a)), evaluatable.named(function.toString()));
    }

    static <T extends Value<?>, A extends Value<?>, B extends Value<?>> TypedEvaluatable<T> of(Class<T> returnType, Parameter<A> a, Parameter<B> b,
                                                                                               TypedEvaluatable.Arg2<A, B, T> function) {
        Evaluatable<T> evaluatable = (valueResolver, evaluationContext, token, arguments) -> function.evaluate(valueResolver, evaluationContext, token,
                a.type().cast(arguments.get(0)),
                b.type().cast(arguments.get(1)));
        return new TypedEvaluatableAdapter<>(Signature.of(returnType, a, b), evaluatable.named(function.toString()));
    }

    static <T extends Value<?>, A extends Value<?>, B extends Value<?>> TypedEvaluatable<T> of(Class<T> returnType, Class<A> a, Class<B> b,
                                                                                               TypedEvaluatable.Arg2<A, B, T> function) {
        return of(returnType, Parameter.of(a), Parameter.of(b), function);
    }

    static <T extends Value<?>, A extends Value<?>, B extends Value<?>, C extends Value<?>> TypedEvaluatable<T> of(Class<T> returnType, Class<A> a, Class<B> b, Class<C> c,
                                                                                                                   TypedEvaluatable.Arg3<A, B, C, T> function) {
        return of(returnType, Parameter.of(a), Parameter.of(b), Parameter.of(c), function);
    }

    static <T extends Value<?>, A extends Value<?>, B extends Value<?>, C extends Value<?>> TypedEvaluatable<T> of(Class<T> returnType, Parameter<A> a, Parameter<B> b, Parameter<C> c,
                                                                                                                   TypedEvaluatable.Arg3<A, B, C, T> function) {
        Evaluatable<T> evaluatable = (valueResolver, evaluationContext, token, arguments) -> function.evaluate(valueResolver, evaluationContext, token,
                a.type().cast(arguments.get(0)),
                b.type().cast(arguments.get(1)),
                c.type().cast(arguments.get(2)));
        return new TypedEvaluatableAdapter<>(Signature.of(returnType, a, b, c), evaluatable.named(function.toString()));
    }

    static <T extends Value<?>, A extends Value<?>, B extends Value<?>, C extends Value<?>, D extends Value<?>> TypedEvaluatable<T> of(Class<T> returnType, Parameter<A> a, Parameter<B> b, Parameter<C> c, Parameter<D> d,
                                                                                                                                       TypedEvaluatable.Arg4<A, B, C, D, T> function) {
        Evaluatable<T> evaluatable = (valueResolver, evaluationContext, token, arguments) -> function.evaluate(valueResolver, evaluationContext, token,
                a.type().cast(arguments.get(0)),
                b.type().cast(arguments.get(1)),
                c.type().cast(arguments.get(2)),
                d.type().cast(arguments.get(3)));
        return new TypedEvaluatableAdapter<>(Signature.of(returnType, a, b, c, d), evaluatable.named(function.toString()));
    }

    static <T extends Value<?>, A extends Value<?>, B extends Value<?>, C extends Value<?>, D extends Value<?>, E extends Value<?>> TypedEvaluatable<T> of(Class<T> returnType, Parameter<A> a, Parameter<B> b, Parameter<C> c, Parameter<D> d, Parameter<E> e,
                                                                                                                                       TypedEvaluatable.Arg5<A, B, C, D, E, T> function) {
        Evaluatable<T> evaluatable = (valueResolver, evaluationContext, token, arguments) -> function.evaluate(valueResolver, evaluationContext, token,
                a.type().cast(arguments.get(0)),
                b.type().cast(arguments.get(1)),
                c.type().cast(arguments.get(2)),
                d.type().cast(arguments.get(3)),
                e.type().cast(arguments.get(4)));
        return new TypedEvaluatableAdapter<>(Signature.of(returnType, a, b, c, d, e), evaluatable.named(function.toString()));
    }

    static <T extends Value<?>, A extends Value<?>, B extends Value<?>, C extends Value<?>, D extends Value<?>, E extends Value<?>, F extends Value<?>> TypedEvaluatable<T> of(Class<T> returnType, Parameter<A> a, Parameter<B> b, Parameter<C> c, Parameter<D> d, Parameter<E> e, Parameter<F> f,
                                                                                                                                                           TypedEvaluatable.Arg6<A, B, C, D, E, F, T> function) {
        Evaluatable<T> evaluatable = (valueResolver, evaluationContext, token, arguments) -> function.evaluate(valueResolver, evaluationContext, token,
                a.type().cast(arguments.get(0)),
                b.type().cast(arguments.get(1)),
                c.type().cast(arguments.get(2)),
                d.type().cast(arguments.get(3)),
                e.type().cast(arguments.get(4)),
                f.type().cast(arguments.get(5)));
        return new TypedEvaluatableAdapter<>(Signature.of(returnType, a, b, c, d, e, f), evaluatable.named(function.toString()));
    }

    static <T extends Value<?>, A extends Value<?>, B extends Value<?>, C extends Value<?>, D extends Value<?>, E extends Value<?>, F extends Value<?>, G extends Value<?>> TypedEvaluatable<T> of(Class<T> returnType, Parameter<A> a, Parameter<B> b, Parameter<C> c, Parameter<D> d, Parameter<E> e, Parameter<F> f, Parameter<G> g,
                                                                                                                                                                               TypedEvaluatable.Arg7<A, B, C, D, E, F, G, T> function) {
        Evaluatable<T> evaluatable = (valueResolver, evaluationContext, token, arguments) -> function.evaluate(valueResolver, evaluationContext, token,
                a.type().cast(arguments.get(0)),
                b.type().cast(arguments.get(1)),
                c.type().cast(arguments.get(2)),
                d.type().cast(arguments.get(3)),
                e.type().cast(arguments.get(4)),
                f.type().cast(arguments.get(5)),
                g.type().cast(arguments.get(6)));
        return new TypedEvaluatableAdapter<>(Signature.of(returnType, a, b, c, d, e, f, g), evaluatable.named(function.toString()));
    }
}
