/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.anttask.jsystem;

/**
 * Java port of {@code ifScriptCondition.js} used by Ant if-flow conditions.
 */
public final class IfScriptConditionEvaluator {

	static final String SEPARATOR = " <SEP> ";

	private IfScriptConditionEvaluator() {
	}

	/**
	 * Evaluate a JSystem if-condition parameters string.
	 *
	 * @return comparison result; {@code false} for unrecognized / invalid expressions
	 *         (same behavior as the original JavaScript)
	 */
	public static boolean evaluate(String expression) {
		if (expression == null) {
			return false;
		}
		int typeEnd = expression.indexOf(SEPARATOR);
		if (typeEnd < 0) {
			return false;
		}
		// Match JS: type = split(separator)[0], rest = substring(type.length())
		String type = expression.substring(0, typeEnd);
		String rest = expression.substring(type.length());

		if ("math".equals(type)) {
			return evaluateMath(rest.replace(SEPARATOR, " "));
		}
		if ("str".equals(type)) {
			int lastSep = rest.lastIndexOf(SEPARATOR);
			if (lastSep < 0) {
				return false;
			}
			String caseSensitive = rest.substring(lastSep + SEPARATOR.length());
			String exp = rest.substring(0, lastSep).replace(SEPARATOR, " ");
			return evaluateString(exp, caseSensitive);
		}
		return false;
	}

	private static boolean evaluateMath(String expression) {
		String[] operators = { ">=", "<=", "!=", "=", "<", ">" };
		String[] parts = splitByOperator(stripSeparators(expression), operators);
		if (parts == null) {
			return false;
		}
		double left;
		double right;
		try {
			left = Double.parseDouble(parts[0].trim());
			right = Double.parseDouble(parts[1].trim());
		} catch (NumberFormatException e) {
			return false;
		}
		String cleaned = stripSeparators(expression);
		if (cleaned.contains(">=")) {
			return left >= right;
		}
		if (cleaned.contains("<=")) {
			return left <= right;
		}
		if (cleaned.contains("!=")) {
			return left != right;
		}
		if (cleaned.contains("=")) {
			return left == right;
		}
		if (cleaned.contains(">")) {
			return left > right;
		}
		if (cleaned.contains("<")) {
			return left < right;
		}
		return false;
	}

	private static boolean evaluateString(String expression, String isCaseSensitive) {
		String[] operators = { "NOT_EQUALS", "EQUALS", "CONTAINS", "STARTS_WITH", "ENDS_WITH" };
		String cleaned = stripSeparators(expression);
		String[] parts = splitByOperator(cleaned, operators);
		if (parts == null) {
			return false;
		}
		String left = parts[0].trim();
		String right = parts[1].trim();
		if ("false".equals(String.valueOf(isCaseSensitive))) {
			left = left.toLowerCase();
			right = right.toLowerCase();
		}
		if (cleaned.contains("NOT_EQUALS")) {
			return !left.equals(right);
		}
		if (cleaned.contains("EQUALS")) {
			return left.equals(right);
		}
		if (cleaned.contains("CONTAINS")) {
			return left.contains(right);
		}
		if (cleaned.contains("STARTS_WITH")) {
			return left.startsWith(right);
		}
		if (cleaned.contains("ENDS_WITH")) {
			return left.endsWith(right);
		}
		return false;
	}

	private static String stripSeparators(String expression) {
		return expression.replace(SEPARATOR, "");
	}

	/**
	 * @return [left, right] or null if no operator matched / split invalid
	 */
	private static String[] splitByOperator(String expression, String[] operators) {
		for (String operator : operators) {
			if (expression.contains(operator)) {
				String[] temp = expression.split(java.util.regex.Pattern.quote(operator), 2);
				if (temp.length < 2) {
					return null;
				}
				return new String[] { temp[0].trim(), temp[1].trim() };
			}
		}
		return null;
	}
}
