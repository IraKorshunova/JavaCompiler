package token;

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

	public boolean isAuxiliary() {
		return isAuxiliary(this);
	}

	public static boolean isAuxiliary(TokenType tokenName) {
		return tokenName == BlockComment || tokenName == LineComment || tokenName == NewLine
				|| tokenName == Tab || tokenName == WhiteSpace;
	}
}