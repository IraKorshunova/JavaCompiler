package parser;

import java.util.Arrays;

/**
 * Represents productions in context-free grammar.In this type of grammars left
 * side of productions contains only one nonterminal symbol.
 * 
 * @author Ira Korshunova
 * 
 */
public class Rule {

	/** Number of the rule */
	private int ruleNumber;

	/** Left side of production */
	private NonTerminal leftSide;

	/** Right side of production */
	private Symbol[] rightSide;

	/**
	 * Creates a rule
	 * 
	 * @param ruleNumber
	 *            number of rule as it is in grammar description
	 * @param leftSide
	 *            nonterminal symbol in the left side of rule
	 * @param rightSide
	 *            terminals and nonterminals in the right side
	 */
	public Rule(int ruleNumber, NonTerminal leftSide, Symbol[] rightSide) {
		this.ruleNumber = ruleNumber;
		this.leftSide = leftSide;
		this.rightSide = rightSide;
	}

	public int getRuleNumber() {
		return ruleNumber;
	}

	public NonTerminal getLeftSide() {
		return leftSide;
	}

	public Symbol[] getRightSide() {
		return rightSide;
	}

	@Override
	public String toString() {
		return "Rule number: " + ruleNumber + "| " + leftSide + " -> " + Arrays.toString(rightSide);
	}

}