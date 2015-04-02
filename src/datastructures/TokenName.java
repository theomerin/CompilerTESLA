package datastructures;

public enum TokenName {
    
    PROGRAM_ROOT,
    PROGRAM_STMT,
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
    ARITHMETIC_EXPRESSION,
    RELATIONAL_EXPRESSION,
    LOGICAL_EXPRESSION,
    NEGEXP_NODE,
    NEGEXP_ROOT,
    NOTEXP_NODE,
    NOTEXP_ROOT,
    NUMERIC_CONSTANT_OR_IDENTIFIER,
    BOOLEAN_CONSTANT,
    ELSEIF_OR_ELSE,
    
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
    NEGBOOL,
    
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
    NEGIDENTIFIER,
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
    CONSINT, NEGINT,
    NUM_INIT_PRIME, NUM_INIT, NUMERIC_ASSIGN,
    TO_INT, TO_INTVAL, 
    //------//
    
    //decimal constant
    CONSFLOAT, NEGFLOAT,
    DEC_INIT_PRIME, DEC_INIT, DEC_ASSIGN,
    //------//
    
    //character constant
    CONSCHAR,
    SYM_INIT_PRIME, SYM_INIT, SYM_ASSIGN,
    //------//
    
    //string constant
    CONSSTR,
    STR_INIT_PRIME, STR_INIT, STR_ASSIGN,
    TO_STR, TO_STRVAL,
    //------//
    
    //boolean constant
    BOOL_INIT_PRIME, BOOL_INIT, BOOL_ASSIGN, BOOL_VALUE, CONSBOOL,
    //------//
    
    //statement - numeric value 
    NUMERIC_VALUE_NODE,
    CAST_STMT,CAST_STMT_PRIME,
    INC_PRE_STMT, DEC_PRE_STMT, 
    INC_POST_STMT, DEC_POST_STMT,
    //------//
   
    //IF statement block
    IF_STMT_PRIME,
    ELSEIF_STMT,
    ELSE_STMT,
    //------//
    
    INPUT, OUTPUT, DURING_STMT, END_IF_STMT, EXECUTE_STMT,
    
}
