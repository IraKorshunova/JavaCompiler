package lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import exceptions.AnalyzerException;

import token.Token;
import token.TokenType;

/**
 * The {@code Lexer} class represents lexical analyzer for subset of Java
 * language.
 * 
 * @author Ira Korshunova
 * 
 */
public class Lexer {

	/** Mapping from type of token to its regular expression */
	private Map<TokenType, String> regEx;

	/** List of tokens as they appear in the input source */
	private List<Token> result;

	/**
	 * Initializes a newly created {@code Lexer} object
	 */
	public Lexer() {
		regEx = new TreeMap<TokenType, String>();
		launchRegEx();
		result = new ArrayList<Token>();
	}

	/**
	 * Performs the tokenization of the input source code.
	 * 
	 * @param source
	 *            string to be analyzed
	 * @throws AnalyzerException
	 *             if lexical error exists in the source
	 * 
	 */
	public void tokenize(String source) throws AnalyzerException {
		int position = 0;
		Token token = null;
		do {
			token = separateToken(source, position);
			if (token != null) {
				position = token.getEnd();
				result.add(token);
			}
		} while (token != null && position != source.length());
		if (position != source.length()) {
			throw new AnalyzerException("Lexical error at position # "+ position, position);

		}
	}

	/**
	 * Returns a sequence of tokens
	 * 
	 * @return list of tokens
	 */
	public List<Token> getTokens() {
		return result;
	}

	/**
	 * Returns a sequence of tokens without types {@code BlockComment},
	 * {@code LineComment} , {@code NewLine}, {@code Tab}, {@code WhiteSpace}
	 * 
	 * @return list of tokens
	 */
	public List<Token> getFilteredTokens() {
		List<Token> filteredResult = new ArrayList<Token>();
		for (Token t : this.result) {
			if (!t.getTokenType().isAuxiliary()) {
				filteredResult.add(t);
			}
		}
		return filteredResult;
	}

	/**
	 * Scans the source from the specific index and returns the first separated
	 * token
	 * 
	 * @param source
	 *            source code to be scanned
	 * @param fromIndex
	 *            the index from which to start the scanning
	 * @return first separated token or {@code null} if no token was found
	 * 
	 */
	private Token separateToken(String source, int fromIndex) {
		if (fromIndex < 0 || fromIndex >= source.length()) {
			throw new IllegalArgumentException("Illegal index in the input stream!");
		}
		for (TokenType tokenType : TokenType.values()) {
			Pattern p = Pattern.compile(".{" + fromIndex + "}" + regEx.get(tokenType),
					Pattern.DOTALL);
			Matcher m = p.matcher(source);
			if (m.matches()) {
				String lexema = m.group(1);
				return new Token(fromIndex, fromIndex + lexema.length(), lexema, tokenType);
			}
		}

		return null;
	}

	/**
	 * Creates map from token types to its regular expressions
	 * 
	 */
	private void launchRegEx() {
		regEx.put(TokenType.BlockComment, "(/\\*.*?\\*/).*");
		regEx.put(TokenType.LineComment, "(//(.*?)[\r$]?\n).*");
		regEx.put(TokenType.WhiteSpace, "( ).*");
		regEx.put(TokenType.OpenBrace, "(\\().*");
		regEx.put(TokenType.CloseBrace, "(\\)).*");
		regEx.put(TokenType.Semicolon, "(;).*");
		regEx.put(TokenType.Comma, "(,).*");
		regEx.put(TokenType.OpeningCurlyBrace, "(\\{).*");
		regEx.put(TokenType.ClosingCurlyBrace, "(\\}).*");
		regEx.put(TokenType.DoubleConstant, "\\b(\\d{1,9}\\.\\d{1,32})\\b.*");
		regEx.put(TokenType.IntConstant, "\\b(\\d{1,9})\\b.*");
		regEx.put(TokenType.Void, "\\b(void)\\b.*");
		regEx.put(TokenType.Int, "\\b(int)\\b.*");
		regEx.put(TokenType.Double, "\\b(int|double)\\b.*");
		regEx.put(TokenType.Tab, "(\\t).*");
		regEx.put(TokenType.NewLine, "(\\n).*");
		regEx.put(TokenType.Public, "\\b(public)\\b.*");
		regEx.put(TokenType.Private, "\\b(private)\\b.*");
		regEx.put(TokenType.False, "\\b(false)\\b.*");
		regEx.put(TokenType.True, "\\b(true)\\b.*");
		regEx.put(TokenType.Null, "\\b(null)\\b.*");
		regEx.put(TokenType.Return, "\\b(return)\\b.*");
		regEx.put(TokenType.New, "\\b(new)\\b.*");
		regEx.put(TokenType.Class, "\\b(class)\\b.*");
		regEx.put(TokenType.If, "\\b(if)\\b.*");
		regEx.put(TokenType.Else, "\\b(else)\\b.*");
		regEx.put(TokenType.While, "\\b(while)\\b.*");
		regEx.put(TokenType.Static, "\\b(static)\\b.*");
		regEx.put(TokenType.Point, "(\\.).*");
		regEx.put(TokenType.Plus, "(\\+{1}).*");
		regEx.put(TokenType.Minus, "(\\-{1}).*");
		regEx.put(TokenType.Multiply, "(\\*).*");
		regEx.put(TokenType.Divide, "(/).*");
		regEx.put(TokenType.EqualEqual, "(==).*");
		regEx.put(TokenType.Equal, "(=).*");
		regEx.put(TokenType.ExclameEqual, "(\\!=).*");
		regEx.put(TokenType.Greater, "(>).*");
		regEx.put(TokenType.Less, "(<).*");
		regEx.put(TokenType.Identifier, "\\b([a-zA-Z]{1}[0-9a-zA-Z_]{0,31})\\b.*");
	}
}