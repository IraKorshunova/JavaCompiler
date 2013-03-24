package parser;

/**
 * The {@code Terminal} class represents a terminal symbol of grammar
 * 
 * @author Ira Korshunova
 * 
 */
public class Terminal extends Symbol{

	public Terminal(int code, String name) {
		super(code,name);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if(obj == null)
			return false;
		if (obj.getClass() != Terminal.class)
			return false;
		Terminal ts = (Terminal) obj;
		return this.getCode() == ts.getCode();
	}

	@Override
	public boolean isTerminal() {
		return true;
	}

	@Override
	public boolean isNonTerminal() {
		return false;
	}
}