package token;

/**
 * The {@code TokeType} enumeration represents types of tokens in subset of Java
 * language
 * 
 * @author Ira Korshunova
 * 
 */
public enum TokenType {
	BlockComment,

	LineComment,

	WhiteSpace,

	Tab,

	NewLine,

	CloseBrace,

	OpenBrace,

	OpeningCurlyBrace,

	ClosingCurlyBrace,

	DoubleConstant,

	IntConstant,

	Plus,

	Minus,

	Multiply,

	Divide,

	Point,

	EqualEqual,

	Equal,

	ExclameEqual,

	Greater,

	Less,

	Static,

	Public,

	Private,

	Int,

	Double,

	Void,

	False,

	True,

	Null,

	Return,

	New,

	Class,

	If,

	While,

	Else,

	Semicolon,

	Comma,

	Identifier;

	/**
	 * Determines if this token is auxiliary
	 * 
	 * @return {@code true} if token is auxiliary, {@code false} otherwise
	 */
	public boolean isAuxiliary() {
		return this == BlockComment || this == LineComment || this == NewLine || this == Tab
				|| this == WhiteSpace;
	}
}