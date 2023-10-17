package de.flapdoodle.eval.core;

import java.math.MathContext;

public interface NumberAsValue {
	Object parse(String value, MathContext mathContext);
}
