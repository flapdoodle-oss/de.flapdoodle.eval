package de.flapdoodle.eval;

import de.flapdoodle.types.Either;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.InstanceOfAssertFactories;

import java.util.function.Consumer;

public class AssertEither<L, R> extends AbstractObjectAssert<AssertEither<L, R>, Either<L, R>> {

	public AssertEither(Either<L, R> either) {
		super(either, AssertEither.class);
	}

	public AssertEither<L, R> isLeft() {
		extracting(Either::isLeft, InstanceOfAssertFactories.BOOLEAN)
			.describedAs("isLeft")
			.isTrue();
		return myself;
	}

	public AssertEither<L, R> isRight() {
		extracting(Either::isLeft, InstanceOfAssertFactories.BOOLEAN)
			.describedAs("isLeft")
			.isFalse();
		return myself;
	}

	public AssertEither<L, R> containsLeft(L left) {
		extracting(either -> either.left())
			.describedAs("left")
			.isEqualTo(left);
		return myself;
	}

	public AssertEither<L, R> containsRight(R right) {
		extracting(either -> either.right())
			.describedAs("right")
			.isEqualTo(right);
		return myself;
	}

	public void rightSatisfies(Consumer<? super R> check) {
		extracting(either -> either.right())
			.describedAs("right")
			.satisfies(check);
	}

	public static <L, R> AssertEither<L, R> assertThat(Either<L, R> actual) {
		return new AssertEither<>(actual);
	}
}
