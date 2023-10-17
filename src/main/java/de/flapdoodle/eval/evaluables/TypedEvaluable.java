package de.flapdoodle.eval.evaluables;

import de.flapdoodle.eval.core.EvaluationContext;
import de.flapdoodle.eval.core.VariableResolver;
import de.flapdoodle.eval.core.parser.Token;
import de.flapdoodle.eval.core.exceptions.EvaluationException;

import java.util.List;
import java.util.stream.Collectors;

public interface TypedEvaluable<T> extends Evaluable<T> {
    Signature<T> signature();

    interface Arg0<T> {
        T evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token) throws EvaluationException;
    }

    interface Arg1<S, T> {
        T evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, S argument) throws EvaluationException;
    }

    interface VarArg1<S, T> {
        T evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, List<S> arguments) throws EvaluationException;
    }

    interface Arg2<A, B, T> {
        T evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, A first, B second) throws EvaluationException;
    }

    interface Arg3<A, B, C, T> {
        T evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, A first, B second, C third) throws EvaluationException;
    }

    interface Arg4<A, B, C, D, T> {
        T evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, A first, B second, C third, D fourth) throws EvaluationException;
    }

    interface Arg5<A, B, C, D, E, T> {
        T evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, A first, B second, C third, D fourth, E fifth) throws EvaluationException;
    }

    interface Arg6<A, B, C, D, E, F, T> {
        T evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, A first, B second, C third, D fourth, E fifth, F sixth) throws EvaluationException;
    }

    interface Arg7<A, B, C, D, E, F, G, T> {
        T evaluate(VariableResolver variableResolver, EvaluationContext evaluationContext, Token token, A first, B second, C third, D fourth, E fifth, F sixth, G seventh) throws EvaluationException;
    }

    static <T> TypedEvaluable<T> of(Class<T> returnType, TypedEvaluable.Arg0<T> function) {
        Evaluable<T> evaluable = (valueResolver, evaluationContext, token, arguments) -> function.evaluate(valueResolver, evaluationContext, token);
        return new TypedEvaluableAdapter<>(Signature.of(returnType), evaluable.named(function.toString()));
    }

    static <T, A> TypedEvaluable<T> of(Class<T> returnType, Parameter<A> a, TypedEvaluable.Arg1<A, T> function) {
        Evaluable<T> evaluable = (valueResolver, evaluationContext, token, arguments) -> function.evaluate(valueResolver, evaluationContext, token,
                a.type().cast(arguments.get(0)));
        return new TypedEvaluableAdapter<>(Signature.of(returnType, a), evaluable.named(function.toString()));
    }

    static <T, A> TypedEvaluable<T> of(Class<T> returnType, Class<A> a, TypedEvaluable.Arg1<A, T> function) {
        return of(returnType, Parameter.of(a), function);
    }

    static <T, A> TypedEvaluable<T> ofVarArg(Class<T> returnType, Class<A> a, TypedEvaluable.VarArg1<A, T> function) {
        Evaluable<T> evaluable = (valueResolver, evaluationContext, token, arguments) -> function.evaluate(valueResolver, evaluationContext, token,
                arguments.stream()
                        .map(a::cast).collect(Collectors.toList()));
        return new TypedEvaluableAdapter<>(Signature.ofVarArg(returnType, Parameter.of(a)), evaluable.named(function.toString()));
    }

    static <T, A, B> TypedEvaluable<T> of(Class<T> returnType, Parameter<A> a, Parameter<B> b,
                                                                                               TypedEvaluable.Arg2<A, B, T> function) {
        Evaluable<T> evaluable = (valueResolver, evaluationContext, token, arguments) -> function.evaluate(valueResolver, evaluationContext, token,
                a.type().cast(arguments.get(0)),
                b.type().cast(arguments.get(1)));
        return new TypedEvaluableAdapter<>(Signature.of(returnType, a, b), evaluable.named(function.toString()));
    }

    static <T, A, B> TypedEvaluable<T> of(Class<T> returnType, Class<A> a, Class<B> b,
                                                                                               TypedEvaluable.Arg2<A, B, T> function) {
        return of(returnType, Parameter.of(a), Parameter.of(b), function);
    }

    static <T, A, B, C> TypedEvaluable<T> of(Class<T> returnType, Class<A> a, Class<B> b, Class<C> c,
                                                                                                                   TypedEvaluable.Arg3<A, B, C, T> function) {
        return of(returnType, Parameter.of(a), Parameter.of(b), Parameter.of(c), function);
    }

    static <T, A, B, C> TypedEvaluable<T> of(Class<T> returnType, Parameter<A> a, Parameter<B> b, Parameter<C> c,
                                                                                                                   TypedEvaluable.Arg3<A, B, C, T> function) {
        Evaluable<T> evaluable = (valueResolver, evaluationContext, token, arguments) -> function.evaluate(valueResolver, evaluationContext, token,
                a.type().cast(arguments.get(0)),
                b.type().cast(arguments.get(1)),
                c.type().cast(arguments.get(2)));
        return new TypedEvaluableAdapter<>(Signature.of(returnType, a, b, c), evaluable.named(function.toString()));
    }

    static <T, A, B, C, D> TypedEvaluable<T> of(Class<T> returnType, Parameter<A> a, Parameter<B> b, Parameter<C> c, Parameter<D> d,
                                                                                                                                       TypedEvaluable.Arg4<A, B, C, D, T> function) {
        Evaluable<T> evaluable = (valueResolver, evaluationContext, token, arguments) -> function.evaluate(valueResolver, evaluationContext, token,
                a.type().cast(arguments.get(0)),
                b.type().cast(arguments.get(1)),
                c.type().cast(arguments.get(2)),
                d.type().cast(arguments.get(3)));
        return new TypedEvaluableAdapter<>(Signature.of(returnType, a, b, c, d), evaluable.named(function.toString()));
    }

    static <T, A, B, C, D, E> TypedEvaluable<T> of(Class<T> returnType, Parameter<A> a, Parameter<B> b, Parameter<C> c, Parameter<D> d, Parameter<E> e,
                                                                                                                                       TypedEvaluable.Arg5<A, B, C, D, E, T> function) {
        Evaluable<T> evaluable = (valueResolver, evaluationContext, token, arguments) -> function.evaluate(valueResolver, evaluationContext, token,
                a.type().cast(arguments.get(0)),
                b.type().cast(arguments.get(1)),
                c.type().cast(arguments.get(2)),
                d.type().cast(arguments.get(3)),
                e.type().cast(arguments.get(4)));
        return new TypedEvaluableAdapter<>(Signature.of(returnType, a, b, c, d, e), evaluable.named(function.toString()));
    }

    static <T, A, B, C, D, E, F> TypedEvaluable<T> of(Class<T> returnType, Parameter<A> a, Parameter<B> b, Parameter<C> c, Parameter<D> d, Parameter<E> e, Parameter<F> f,
                                                                                                                                                           TypedEvaluable.Arg6<A, B, C, D, E, F, T> function) {
        Evaluable<T> evaluable = (valueResolver, evaluationContext, token, arguments) -> function.evaluate(valueResolver, evaluationContext, token,
                a.type().cast(arguments.get(0)),
                b.type().cast(arguments.get(1)),
                c.type().cast(arguments.get(2)),
                d.type().cast(arguments.get(3)),
                e.type().cast(arguments.get(4)),
                f.type().cast(arguments.get(5)));
        return new TypedEvaluableAdapter<>(Signature.of(returnType, a, b, c, d, e, f), evaluable.named(function.toString()));
    }

    static <T, A, B, C, D, E, F, G> TypedEvaluable<T> of(Class<T> returnType, Parameter<A> a, Parameter<B> b, Parameter<C> c, Parameter<D> d, Parameter<E> e, Parameter<F> f, Parameter<G> g,
                                                                                                                                                                               TypedEvaluable.Arg7<A, B, C, D, E, F, G, T> function) {
        Evaluable<T> evaluable = (valueResolver, evaluationContext, token, arguments) -> function.evaluate(valueResolver, evaluationContext, token,
                a.type().cast(arguments.get(0)),
                b.type().cast(arguments.get(1)),
                c.type().cast(arguments.get(2)),
                d.type().cast(arguments.get(3)),
                e.type().cast(arguments.get(4)),
                f.type().cast(arguments.get(5)),
                g.type().cast(arguments.get(6)));
        return new TypedEvaluableAdapter<>(Signature.of(returnType, a, b, c, d, e, f, g), evaluable.named(function.toString()));
    }
}
