package token;

/**
 * The {@code Token} class represents token (lexeme). A token is a string of
 * characters, categorized according to the rules as a symbol. For example: <i>
 * Identifier, Comma, DoubleConstant</i>.
 * 
 * @author Ira Korshunova
 * 
 */

public class Token {

	/** The beginning index of this token in the input */
	private int beginIndex;

	/** The ending index of token in the input */
	private int endIndex;

	/** Type(category) of token */
	private TokenType tokenType;

	/** String of characters for this token */
	private String tokenString;

	/**
	 * Constructs new {@code Token} object with specified parameters.
	 * 
	 * @param beginIndex
	 *            the beginning index of this token in the input, inclusive
	 * @param endIndex
	 *            the ending index of token in the input, exclusive
	 * @param tokenString
	 *            string of characters
	 * @param tokenType
	 *            type of token
	 */
	public Token(int beginIndex, int endIndex, String tokenString, TokenType tokenType) {
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
		this.tokenType = tokenType;
		this.tokenString = tokenString;
	}

	/**
	 * Returns the beginning index
	 * 
	 * @return the beginning index of this token in the input, inclusive
	 */
	public int getBegin() {
		return beginIndex;
	}

	/**
	 * Returns the ending index
	 * 
	 * @return the ending index of token in the input, exclusive
	 */
	public int getEnd() {
		return endIndex;
	}

	
	/**
	 * Returns a string for the token 
	 * 
	 * @return a string of characters associated with this token
	 */
	public String getTokenString() {
		return tokenString;
	}


	/**
	 * Returns token's type 
	 * 
	 * @return type associated with this token
	 */
	public TokenType getTokenType() {
		return tokenType;
	}

	@Override
	public String toString() {
		if (!this.getTokenType().isAuxiliary())
			return tokenType + "  '" + tokenString + "' [" + beginIndex + ";" + endIndex + "] ";
		else
			return tokenType + "   [" + beginIndex + ";" + endIndex + "] ";
	}
}