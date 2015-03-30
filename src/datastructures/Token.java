package datastructures;

public class Token {
    private TokenName Name;
    private TokenType Type;
    private String Lexeme;
    private Object Value;
    private Object DataType;
    private int LineNumber;
    
    public Token() {
        Name = TokenName.DEFAULT;
        Type = TokenType.DEFAULT;
        Lexeme = "";
        Value = null;
        DataType = null;
        LineNumber = 0;
    }
    
    public Token(TokenName name, TokenType type, String lexeme, Object value, Object datatype, int linenumber) {
        Name = name;
        Type = type;
        Lexeme = lexeme;
        Value = value;
        DataType = datatype;
        LineNumber = linenumber;
    }
    
    public String getType() {
        return Type.toString();
    }
    
    public String getLexeme() {
        return Lexeme.toString();
    }
    
    public TokenName getTokenName() {
        return Name;
    }
    
    public String getValue() {
        return Value.toString();
    }
    
    public String getTokenContent() {
        return "[ Token Name = " + Name.toString() + " | Lexeme = " + Lexeme.toString() + " ]";
    }
    
    public int getLineNumber() {
        return LineNumber;
    }
}