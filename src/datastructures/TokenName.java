package datastructures;

public enum TokenName {
    
    
    PROGRAM_ROOT,
    PROGRAM_STMNT,
    PROGRAM_BODY,
    DEFAULT,
    EMPTY,
    STATEMENT_NODE,
    DOLLAR_OPERATOR,
    EXPRESSION,
    PREINC,
    PREDEC,
    POSTINC,
    POSTDEC,
    
    NOTRESWRD,   
    ISINTCONST, ISEOL,
   
    //reserved words
    ENGAGE,
    TERMINATE,
    ON,
    OFF,
    
    WRITE,
    DIGITIZE,   //cast to int
    
    DIGIT,      //int
    DECIMAL,    //float
    SYMBOL,     //char
    SENTENCE,   //string
    VERIFY,     //boolean
    
    AFFIRM,     //true
    NEGATE,     //false
    
    IF,         //if
    ENDIF,
    ELSEIF,     //else if
    ELSE,       //else
    
    EXECUTE,    //do
    DURING,     //while
    
    RECEIVE,    //input
    TRANSMIT,   //output
    
    //tokens
    IDENTIFIER,
    SINGLE_COMMENT,
    MULTI_COMMENT,
    WHITESPACES,
    COMMA,
    EOL,
    ERROR,
    EOF,  
    
    LEFTBRACE,
    RIGHTBRACE,
    LEFTBRACKET,
    RIGHTBRACKET,
    LEFTPAR,
    RIGHTPAR,
    
    CONCATOPP,      
    ASSIGNOPP,     
    //------//
    
    //relational operators
    OPGREAT,    // >
    OPLESS,     // <
    OPEQUAL,    // =
    OPNOT,      
    OPGREQ,     // >=
    OPLEQ,      // <=
    LOGICAND,   // &&
    LOGICOR,    // ||
    LOGICNOT,     
    //------//
    
    //arithmeric operators
    SUM, DIFF,
    PROD, DIV,
    INC, DEC,
    POW, MOD,
    //------//
    
    //integer constant
    CONSINT,
    NUM_INIT_PRIME, NUM_INIT, NUMERIC_ASSIGN,
    //------//
    
    //decimal constant
    CONSFLOAT,
    DEC_INIT_PRIME, DEC_INIT, DEC_ASSIGN,
    //------//
    
    //character constant
    CONSCHAR,
    SYM_INIT_PRIME, SYM_INIT, SYM_ASSIGN,
    //------//
    
    //string constant
    CONSSTR,
    STR_INIT_PRIME, STR_INIT, STR_ASSIGN,
    //------//
    
    //boolean constant
    BOOL_INIT_PRIME, BOOL_INIT, BOOL_ASSIGN, BOOL_VALUE
    //------//
    
}
