package parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;

import token.Token;
import exceptions.AnalyzerException;

/**
 * The {@code Parser} class represents predictive parser. It accepts only LL(1)
 * grammar. If the grammar is not LL(1) most likely you will get
 * {@code StackOverflowError}. Productions in the grammar use the following
 * format, for example:
 * 
 * <blockquote>
 * 
 * <pre>
 *  Goal -> A 
 *  A -> ( A ) | Two 
 *  Two -> a
 * </pre>
 * 
 * </blockquote>
 * 
 * Symbol is inferred as nonterminal by first uppercase char. "->" designates
 * definition, "|" designates alternation, and newlines designate termination.
 * Use "EPSILON" to represent an empty string. Place spaces between things you
 * don't want to read as one symbol: ( A ) != (A).
 * 
 * @author Ira Korshunova
 * 
 */

public class Parser {

	/** Terminal symbol of grammar which represents empty string */
	public static Terminal epsilon = new Terminal(0, "EPSILON");

	/** Terminal symbol which represents end of program */
	public static Terminal endOfProgram = new Terminal(-1, "ENDOFPROGRAM");

	/** Start symbol of the grammar */
	private NonTerminal startSymbol;

	/** List of rules in the grammar without alternations */
	private List<Rule> rules;

	/** Grammar's alphabet. Contains terminal and nonterminal symbols */
	private Set<Symbol> alphabet;

	/** Mapping from string representation of symbol to its object */
	private Map<String, Symbol> nameToSymbol;

	/** Mapping from symbol to it's first set */
	private Map<Symbol, Set<Terminal>> firstSet;

	/** Mapping from symbol to it's follow set */
	private Map<Symbol, Set<Terminal>> followSet;

	/** Representation of parsing table for LL(1) parser */
	private Map<SimpleEntry<NonTerminal, Terminal>, Symbol[]> parsingTable;

	/** Stack of terminals, which were constructed from input tokens */
	private Stack<Terminal> input;

	/** Sequence of applied rules during the derivations */
	private List<Rule> sequenceOfAppliedRules;

	/**
	 * Initializes a newly created {@code Parser} object
	 * 
	 */

	public Parser() {
		rules = new ArrayList<Rule>();
		alphabet = new HashSet<Symbol>();
		nameToSymbol = new HashMap<String, Symbol>();
		alphabet.add(epsilon);
		firstSet = new HashMap<Symbol, Set<Terminal>>();
		followSet = new HashMap<Symbol, Set<Terminal>>();
		parsingTable = new HashMap<SimpleEntry<NonTerminal, Terminal>, Symbol[]>();
		sequenceOfAppliedRules = new ArrayList<Rule>();
	}

	/**
	 * Parses the source, represented by the list of tokens, using the specified
	 * LL(1) grammar rules
	 * 
	 * @param grammarFile
	 *            file with grammar rules
	 * @param list
	 *            list of tokens from the input
	 * @throws FileNotFoundException
	 *             if file doesn't exist
	 * @throws AnalyzerException
	 *             if the input contains syntax error
	 */
	public void parse(File grammarFile, List<Token> list) throws FileNotFoundException,
			AnalyzerException {
		parseRules(grammarFile);
		calculateFirst();
		calculateFollow();
		buildParsingTable();
		input = convertTokensToStack(list);
		performParsingAlgorithm();
	}

	/**
	 * Returns the sequence of grammar rules, which were applied during the
	 * parsing
	 * 
	 * @return list of applied rules
	 * 
	 */
	public List<Rule> getSequenceOfAppliedRules() {
		return sequenceOfAppliedRules;
	}

	/**
	 * Implements LL(1) predictive parsing algorithm
	 * 
	 * @throws AnalyzerException
	 *             if syntax error was found
	 */
	private void performParsingAlgorithm() throws AnalyzerException {
		Stack<Symbol> stack = new Stack<Symbol>();
		stack.push(endOfProgram);
		stack.push(startSymbol);
		int parsedTokensCount = 0;
		do {

			Symbol stackTop = stack.peek();
			Terminal inputTop = input.peek();
			if (stackTop.isTerminal()) {
				if (stackTop.equals(inputTop)) {
					stack.pop();
					input.pop();
					parsedTokensCount++;
				} else {
					throw new AnalyzerException("Syntax error after token #" + parsedTokensCount,
							parsedTokensCount);
				}
			} else {
				SimpleEntry<NonTerminal, Terminal> tableKey = new SimpleEntry<NonTerminal, Terminal>(
						(NonTerminal) stackTop, inputTop);
				if (parsingTable.containsKey(tableKey)) {
					stack.pop();
					Symbol[] tableEntry = parsingTable.get(tableKey);
					for (int j = tableEntry.length - 1; j > -1; j--) {
						if (!tableEntry[j].equals(epsilon))
							stack.push(tableEntry[j]);
					}
					sequenceOfAppliedRules.add(getRule((NonTerminal) stackTop, tableEntry));
				} else {
					throw new AnalyzerException("Syntax error after token #" + parsedTokensCount,
							parsedTokensCount);
				}
			}
		} while (!stack.isEmpty() && !input.isEmpty());

		if (!input.isEmpty()) {
			throw new AnalyzerException("Syntax error after token #" + parsedTokensCount,
					parsedTokensCount);
		}
	}

	/**
	 * Converts a list of tokens from lexer to stack of terminals for parser.
	 * Fist token in the input will be at the top of the stack.
	 * 
	 * @param inputTokens
	 *            list of input tokens
	 * @return stack of terminal symbols
	 */
	private Stack<Terminal> convertTokensToStack(List<Token> inputTokens) {
		Stack<Terminal> input = new Stack<Terminal>();
		Collections.reverse(inputTokens);
		input.push(endOfProgram);
		for (Token token : inputTokens) {
			Terminal s = (Terminal) nameToSymbol.get(token.getTokenString());
			if (s == null) {
				switch (token.getTokenType()) {
				case Identifier:
					s = (Terminal) nameToSymbol.get("id");
					break;
				case IntConstant:
					s = (Terminal) nameToSymbol.get("intConst");
					break;
				case DoubleConstant:
					s = (Terminal) nameToSymbol.get("doubleConst");
					break;
				default:
					throw new RuntimeException("Somethig is wrong!");
				}
			}
			input.push(s);
		}
		return input;
	}

	/**
	 * Automatically builds LL(1) parsing table by using follow and first set
	 */
	private void buildParsingTable() {
		for (Rule r : rules) {
			Symbol[] rightSide = r.getRightSide();
			NonTerminal leftSide = r.getLeftSide();
			Set<Terminal> firstSetForRightSide = first(rightSide);
			Set<Terminal> followSetForLeftSide = followSet.get(leftSide);

			for (Terminal s : firstSetForRightSide) {
				parsingTable.put(new SimpleEntry<NonTerminal, Terminal>(leftSide, s), rightSide);
			}

			if (firstSetForRightSide.contains(epsilon)) {
				for (Terminal s : followSetForLeftSide) {
					parsingTable
							.put(new SimpleEntry<NonTerminal, Terminal>(leftSide, s), rightSide);
				}
			}
		}
	}

	private void calculateFirst() {
		for (Symbol s : alphabet) {
			firstSet.put(s, new HashSet<Terminal>());
		}
		for (Symbol s : alphabet) {
			first(s);
		}
	}

	/**
	 * Calculates first set for specified symbol. By using the next rules:
	 * <blockquote>
	 * 
	 * <pre>
	 * 1. If X is terminal, then FIRST(X) is {X}.
	 * 2. If X -> EPSILON is production, then add EPSILON to FIRST(X).
	 * 3. If X is nonterminal and X -> Y1 Y2 ... Yk is a production, 
	 * then place <i>a</i> (terminal) in FIRST(X) if for some i <i>a</i> is in FIRST(Yi), and Y1, ... ,Yi-1 -> EPSILON. 
	 * If EPSILON is in FIRST(Yj) for all j = 1, 2, ... , k, then add EPSILON to FIRST(X).
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * 
	 * @param s
	 *            terminal or nonterminal symbol of grammar
	 */
	private void first(Symbol s) {
		Set<Terminal> first = firstSet.get(s);
		Set<Terminal> auxiliarySet;
		if (s.isTerminal()) {
			first.add((Terminal) s);
			return;
		}

		for (Rule r : getRulesWithLeftSide((NonTerminal) s)) {
			Symbol[] rightSide = r.getRightSide();
			first(rightSide[0]);
			auxiliarySet = new HashSet<Terminal>(firstSet.get(rightSide[0]));
			auxiliarySet.remove(epsilon);
			first.addAll(auxiliarySet);

			for (int i = 1; i < rightSide.length
					&& firstSet.get(rightSide[i - 1]).contains(epsilon); i++) {
				first(rightSide[i]);
				auxiliarySet = new HashSet<Terminal>(firstSet.get(rightSide[i]));
				auxiliarySet.remove(epsilon);
				first.addAll(auxiliarySet);
			}

			boolean allContainEpsilon = true;
			for (Symbol rightS : rightSide) {
				if (!firstSet.get(rightS).contains(epsilon)) {
					allContainEpsilon = false;
					break;
				}
			}
			if (allContainEpsilon)
				first.add(epsilon);
		}
	}

	/**
	 * Calculates first set for chain of symbols
	 * 
	 * @param chain
	 *            string of symbols
	 * @return first set for the specified string
	 */
	private Set<Terminal> first(Symbol[] chain) {
		Set<Terminal> firstSetForChain = new HashSet<Terminal>();
		Set<Terminal> auxiliarySet;
		auxiliarySet = new HashSet<Terminal>(firstSet.get(chain[0]));
		auxiliarySet.remove(epsilon);
		firstSetForChain.addAll(auxiliarySet);

		for (int i = 1; i < chain.length && firstSet.get(chain[i - 1]).contains(epsilon); i++) {
			auxiliarySet = new HashSet<Terminal>(firstSet.get(chain[i]));
			auxiliarySet.remove(epsilon);
			firstSetForChain.addAll(auxiliarySet);
		}

		boolean allContainEpsilon = true;
		for (Symbol s : chain) {
			if (!firstSet.get(s).contains(epsilon)) {
				allContainEpsilon = false;
				break;
			}
		}
		if (allContainEpsilon)
			firstSetForChain.add(epsilon);

		return firstSetForChain;
	}

	private void calculateFollow() {
		for (Symbol s : alphabet) {
			if (s.isNonTerminal())
				followSet.put(s, new HashSet<Terminal>());
		}

		Map<SimpleEntry<Symbol, Symbol>, Boolean> callTable = new HashMap<SimpleEntry<Symbol, Symbol>, Boolean>();
		for (Symbol firstS : alphabet) {
			for (Symbol secondS : alphabet) {
				callTable.put(new SimpleEntry<Symbol, Symbol>(firstS, secondS), false);
			}
		}

		NonTerminal firstSymbol = rules.get(0).getLeftSide();
		followSet.get(firstSymbol).add(endOfProgram);
		for (Symbol s : alphabet) {
			if (s.isNonTerminal()) {
				follow((NonTerminal) s, null, callTable);
			}
		}
	}

	/**
	 * Calculates follow set for nonterminal symbols
	 */
	private void follow(NonTerminal s, Symbol caller,
			Map<SimpleEntry<Symbol, Symbol>, Boolean> callTable) {
		Boolean called = callTable.get(new SimpleEntry<Symbol, Symbol>(caller, s));
		if (called != null) {
			if (called == true)
				return;
			else
				callTable.put(new SimpleEntry<Symbol, Symbol>(caller, s), true);
		}

		Set<Terminal> follow = followSet.get(s);
		Set<Terminal> auxiliarySet;

		List<SimpleEntry<NonTerminal, Symbol[]>> list = getLeftSideRightChain(s);
		for (SimpleEntry<NonTerminal, Symbol[]> pair : list) {
			Symbol[] rightChain = pair.getValue();
			NonTerminal leftSide = pair.getKey();
			if (rightChain.length != 0) {
				auxiliarySet = first(rightChain);
				auxiliarySet.remove(epsilon);
				follow.addAll(auxiliarySet);
				if (first(rightChain).contains(epsilon)) {
					follow(leftSide, s, callTable);
					follow.addAll(followSet.get(leftSide));
				}
			} else {
				follow(leftSide, s, callTable);
				follow.addAll(followSet.get(leftSide));
			}
		}
	}

	/**
	 * Constructs grammar rules from file
	 * 
	 * @param grammarFile
	 *            file with grammar rules
	 * @throws FileNotFoundException
	 *             if file with the specified pathname does not exist
	 */
	private void parseRules(File grammarFile) throws FileNotFoundException {
		nameToSymbol.put("EPSILON", epsilon);

		Scanner data = new Scanner(grammarFile);
		int code = 1;
		int ruleNumber = 0;
		while (data.hasNext()) {
			StringTokenizer t = new StringTokenizer(data.nextLine());
			String symbolName = t.nextToken();
			if (!nameToSymbol.containsKey(symbolName)) {
				Symbol s = new NonTerminal(code, symbolName);
				if (code == 1)
					startSymbol = (NonTerminal) s;
				nameToSymbol.put(symbolName, s);
				alphabet.add(s);
				code++;
			}
			t.nextToken();// ->

			NonTerminal leftSide = (NonTerminal) nameToSymbol.get(symbolName);
			while (t.hasMoreTokens()) {
				List<Symbol> rightSide = new ArrayList<Symbol>();
				do {
					symbolName = t.nextToken();
					if (!symbolName.equals("|")) {
						if (!nameToSymbol.containsKey(symbolName)) {
							Symbol s;
							if (Character.isUpperCase(symbolName.charAt(0)))
								s = new NonTerminal(code++, symbolName);
							else
								s = new Terminal(code++, symbolName);
							nameToSymbol.put(symbolName, s);
							alphabet.add(s);
						}
						rightSide.add(nameToSymbol.get(symbolName));
					}
				} while (!symbolName.equals("|") && t.hasMoreTokens());
				rules.add(new Rule(ruleNumber++, leftSide, rightSide.toArray(new Symbol[] {})));
			}
		}
	}

	/**
	 * Returns rules with specified left side
	 * 
	 * @param nonTerminalSymbol
	 *            symbol in the left side of the production
	 * @return set of rules which contain the specified symbol in the left side
	 */
	private Set<Rule> getRulesWithLeftSide(NonTerminal nonTerminalSymbol) {
		Set<Rule> set = new HashSet<Rule>();
		for (Rule r : rules) {
			if (r.getLeftSide().equals(nonTerminalSymbol))
				set.add(r);
		}
		return set;
	}

	/**
	 * Returns list of pairs. First element of the pair is the left side of the
	 * rule if this rule contains specified symbol {@code s} in the right side.
	 * The second element contains symbols after {@code s} in the right side of
	 * the rule.
	 * 
	 * @param s
	 * @return
	 */
	private List<SimpleEntry<NonTerminal, Symbol[]>> getLeftSideRightChain(Symbol s) {
		List<SimpleEntry<NonTerminal, Symbol[]>> list = new ArrayList<SimpleEntry<NonTerminal, Symbol[]>>();
		for (Rule r : rules) {
			Symbol[] rightChain = r.getRightSide();
			int index = Arrays.asList(rightChain).indexOf(s);
			if (index != -1) {
				rightChain = Arrays.copyOfRange(rightChain, index + 1, rightChain.length);
				list.add(new SimpleEntry<NonTerminal, Symbol[]>(r.getLeftSide(), rightChain));
			}
		}
		return list;
	}

	/**
	 * Returns the rule with specified left and right side
	 * 
	 * @param leftSide
	 *            symbol in the left side of the production
	 * @param rightSide
	 *            symbols in the right side
	 * @return rule with specified left and right side or {@code null} if such
	 *         rule doesn't exist in grammar
	 */
	private Rule getRule(NonTerminal leftSide, Symbol[] rightSide) {
		Set<Rule> setOfRules = getRulesWithLeftSide(leftSide);
		for (Rule r : setOfRules) {
			if (rightSide.length != r.getRightSide().length)
				continue;
			for (int i = 0; i < rightSide.length; i++) {
				if (r.getRightSide()[i] != rightSide[i])
					break;
				else {
					if (i == rightSide.length - 1) {
						return r;
					}
				}
			}
		}

		return null;
	}
}
