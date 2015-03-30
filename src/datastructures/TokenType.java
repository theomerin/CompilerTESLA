package datastructures;

public enum TokenType {
    
    DEFAULT,
    RESERVED_WORD,
    EOF,
    EOL,
    ERROR,
    IDENTIFIER,
    COMMENT,
    WHITE_SPACE,
    RELOP,
    LOGICOP,
    ARITHMETICOP,
    ASSIGNMENTOP,
    STRING_CONSTANT,
    INT_CONSTANT,
    BOOL_CONSTANT,
    BRACE,
    BRACKET,
    PARENTHESIS,
    COMMA,
    NOT_RESERVED_WORD,
    CONCATOPP, CHAR_CONSTANT, FLOAT_CONSTANT,
    ISINTCONST, ISEOL
}