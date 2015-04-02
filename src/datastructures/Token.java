package datastructures;

public class Token {
    private TokenName Name;
    private TokenType Type;
    private String Lexeme;
    private Object Value;
    private DataType DataType;
    private int LineNumber;
    
    public Token() {
        Name = TokenName.DEFAULT;
        Type = TokenType.DEFAULT;
        Lexeme = "";
        Value = null;
        DataType = null;
        LineNumber = 0;
    }
    
    public Token(TokenName name, TokenType type, String lexeme, Object value, DataType datatype, int linenumber) {
        Name = name;
        Type = type;
        Lexeme = lexeme;
        Value = value;
        DataType = datatype;
        LineNumber = linenumber;
    }
    
    public TokenType getTokenType() {
        return Type;
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
    
    public DataType getDataType() {
        return DataType;
    }
    
    public void setDataType(DataType datatype) {
        DataType = datatype;
    }
}