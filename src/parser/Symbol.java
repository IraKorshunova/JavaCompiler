package parser;

public abstract class Symbol {
	private int code;
	private String name;

	public Symbol(int code, String name) {
		this.code = code;
		this.name = name;
	}

	public int getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public abstract boolean isTerminal();

	public abstract boolean isNonTerminal();

	@Override
	public String toString() {
		return name; 
	}

	@Override
	public int hashCode() {
		return code;
	}
}