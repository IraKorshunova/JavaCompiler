package parser;

/**
 * The {@code NonTerminal} class represents nonterminal symbol of grammar
 * 
 * @author Ira Korshunova
 * 
 */
public class NonTerminal extends Symbol {

	/**
	 * Creates new {@code NonTerminal} object with specified code and
	 * designation
	 * 
	 * @param code code of nonterminal symbol
	 * @param name designation of the nonterminal in the grammar
	 */
	public NonTerminal(int code, String name) {
		super(code, name);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null)
			return false;
		if (obj.getClass() != NonTerminal.class)
			return false;
		NonTerminal nts = (NonTerminal) obj;
		return this.getCode() == nts.getCode();
	}

	@Override
	public boolean isTerminal() {
		return false;
	}

	@Override
	public boolean isNonTerminal() {
		return true;
	}
}