package com.aqua.anttask.jsystem;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Verifies Java port coverage of all ifScriptCondition.js flows (str/math operators).
 */
public class IfScriptConditionEvaluatorTest {

	private static final String SEP = IfScriptConditionEvaluator.SEPARATOR;

	@Test
	public void stringEqualsTrue() {
		assertTrue(evalStr("success", "EQUALS", "success", true));
	}

	@Test
	public void stringEqualsFalse() {
		assertFalse(evalStr("success", "EQUALS", "fail", true));
	}

	@Test
	public void stringNotEquals() {
		assertTrue(evalStr("a", "NOT_EQUALS", "b", true));
		assertFalse(evalStr("a", "NOT_EQUALS", "a", true));
	}

	@Test
	public void stringContains() {
		assertTrue(evalStr("abcdef", "CONTAINS", "cd", true));
		assertFalse(evalStr("abcdef", "CONTAINS", "xy", true));
	}

	@Test
	public void stringStartsWith() {
		assertTrue(evalStr("abcdef", "STARTS_WITH", "abc", true));
		assertFalse(evalStr("abcdef", "STARTS_WITH", "def", true));
	}

	@Test
	public void stringEndsWith() {
		assertTrue(evalStr("abcdef", "ENDS_WITH", "def", true));
		assertFalse(evalStr("abcdef", "ENDS_WITH", "abc", true));
	}

	@Test
	public void stringCaseSensitive() {
		assertFalse(evalStr("Success", "EQUALS", "success", true));
		assertTrue(evalStr("Success", "EQUALS", "success", false));
		assertTrue(evalStr("ABCdef", "CONTAINS", "cde", false));
	}

	@Test
	public void mathEquals() {
		assertTrue(evalMath("5", "=", "5"));
		assertFalse(evalMath("5", "=", "6"));
	}

	@Test
	public void mathNotEquals() {
		assertTrue(evalMath("5", "!=", "6"));
		assertFalse(evalMath("5", "!=", "5"));
	}

	@Test
	public void mathGreaterLess() {
		assertTrue(evalMath("6", ">", "5"));
		assertFalse(evalMath("5", ">", "6"));
		assertTrue(evalMath("5", "<", "6"));
		assertFalse(evalMath("6", "<", "5"));
	}

	@Test
	public void mathGreaterOrEqualLessOrEqual() {
		assertTrue(evalMath("5", ">=", "5"));
		assertTrue(evalMath("6", ">=", "5"));
		assertFalse(evalMath("4", ">=", "5"));
		assertTrue(evalMath("5", "<=", "5"));
		assertTrue(evalMath("4", "<=", "5"));
		assertFalse(evalMath("6", "<=", "5"));
	}

	@Test
	public void mathFloatValues() {
		assertTrue(evalMath("2.5", ">", "2.4"));
		assertTrue(evalMath("2.0", "=", "2"));
	}

	@Test
	public void jenkinsReportedCase() {
		assertTrue(IfScriptConditionEvaluator.evaluate(
				"str" + SEP + "success" + SEP + "EQUALS" + SEP + "success" + SEP + "true"));
	}

	@Test
	public void flowControlSampleCase() {
		assertTrue(IfScriptConditionEvaluator.evaluate(
				"str" + SEP + "1" + SEP + "EQUALS" + SEP + "1" + SEP + "false"));
	}

	@Test
	public void invalidExpressionsReturnFalse() {
		assertFalse(IfScriptConditionEvaluator.evaluate(null));
		assertFalse(IfScriptConditionEvaluator.evaluate(""));
		assertFalse(IfScriptConditionEvaluator.evaluate("no-separator"));
		assertFalse(IfScriptConditionEvaluator.evaluate("custom" + SEP + "x"));
		assertFalse(IfScriptConditionEvaluator.evaluate("math" + SEP + "abc" + SEP + "=" + SEP + "1"));
		assertFalse(IfScriptConditionEvaluator.evaluate("str" + SEP + "onlyOnePart"));
	}

	private static boolean evalStr(String left, String op, String right, boolean caseSensitive) {
		return IfScriptConditionEvaluator.evaluate(
				"str" + SEP + left + SEP + op + SEP + right + SEP + caseSensitive);
	}

	private static boolean evalMath(String left, String op, String right) {
		return IfScriptConditionEvaluator.evaluate("math" + SEP + left + SEP + op + SEP + right);
	}
}
