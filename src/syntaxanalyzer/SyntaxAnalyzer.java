package syntaxanalyzer;

import datastructures.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import lexicalanalyzer.*;
import java.util.*;

public class SyntaxAnalyzer {

    LexicalAnalyzer lex = new LexicalAnalyzer();
    LexicalAnalyzer lex1 = new LexicalAnalyzer();
    ParseTree parseTree = new ParseTree();
    Token currentToken;

    public SyntaxAnalyzer() throws FileNotFoundException, IOException {

    }

    public void ADVANCE() throws IOException {
        currentToken = lex.consumeToken();
    }

    public Token ERROR(TokenName expectedToken, Token currentToken, int lineNumber) {
        String ANSI_RED = "\u001B[31m";
        String ANSI_RESET = "\u001B[0m";
        System.out.println(ANSI_RED + "PARSING NOT SUCESSFUL: Token Expected: " + expectedToken + ". Token Found at line number (" + lineNumber + "): " + currentToken.getTokenName() + " Lexeme of: " + currentToken.getLexeme());
        System.out.print(ANSI_RESET);
        return new Token(TokenName.ERROR, null, null, null, null, lineNumber);
    }

    public ParseTreeNode makeNode(Token token) {
        return new ParseTreeNode(token, null, null);
    }

    public ParseTree PROGRAM() throws IOException {
        ParseTreeNode PROGRAM_NODE = new ParseTreeNode(new Token(TokenName.PROGRAM_ROOT, null, null, null, null, 0), null, null);
        ParseTreeNode ENGAGE = new ParseTreeNode();;
        ParseTreeNode LEFTBRACKET = new ParseTreeNode();;
        ParseTreeNode PROGRAMNAME = new ParseTreeNode();;
        ParseTreeNode RIGHTBRACKET = new ParseTreeNode();;
        ParseTreeNode TERMINATE = new ParseTreeNode();;
        ParseTree STATEMENTS = new ParseTree();
        ParseTree PROGRAM = new ParseTree();
        Token ERROR = new Token();
        int errorCount = 0;

        if (currentToken.getTokenName().equals(TokenName.ENGAGE)) {
            ENGAGE = makeNode(currentToken);
            ADVANCE();
            if (currentToken.getTokenName().equals(TokenName.LEFTBRACKET)) {
                LEFTBRACKET = makeNode(currentToken);
                ADVANCE();
                if (currentToken.getTokenName().equals(TokenName.IDENTIFIER)) {
                    PROGRAMNAME = makeNode(currentToken);
                    ADVANCE();
                    if (currentToken.getTokenName().equals(TokenName.RIGHTBRACKET)) {
                        RIGHTBRACKET = makeNode(currentToken);
                        ADVANCE();
                        STATEMENTS = PROG_STMNT();
                        ADVANCE();
                        if (currentToken.getTokenName().equals(TokenName.TERMINATE)) {
                            TERMINATE = makeNode(currentToken);
                        } else {
                            ERROR = ERROR(TokenName.TERMINATE, currentToken, currentToken.getLineNumber());
                            errorCount++;
                        }
                    } else {
                        ERROR = ERROR(TokenName.RIGHTBRACKET, currentToken, currentToken.getLineNumber());
                        errorCount++;
                    }
                } else {
                    ERROR = ERROR(TokenName.IDENTIFIER, currentToken, currentToken.getLineNumber());
                    errorCount++;
                }
            } else {
                ERROR = ERROR(TokenName.LEFTBRACKET, currentToken, currentToken.getLineNumber());
                errorCount++;
            }
        } else {
            ERROR = ERROR(TokenName.ENGAGE, currentToken, currentToken.getLineNumber());
            errorCount++;
        }
        
        if (errorCount > 0) {
            return new ParseTree(new ParseTreeNode(ERROR, null, null));
        }
        
        ENGAGE.setNextSibling(LEFTBRACKET);
        LEFTBRACKET.setNextSibling(PROGRAMNAME);
        PROGRAMNAME.setNextSibling(RIGHTBRACKET);
        RIGHTBRACKET.setNextSibling(STATEMENTS.root);
        STATEMENTS.root.setNextSibling(TERMINATE);
        PROGRAM_NODE.setFirstChild(ENGAGE);
        PROGRAM.setRoot(PROGRAM_NODE);
         
        return PROGRAM;
    }

    public ParseTree PROG_STMNT() throws IOException {
        ParseTreeNode PROGRAM_STMNT_NODE = new ParseTreeNode(new Token(TokenName.PROGRAM_STMNT, null, null, null, null, 0), null, null);
        ParseTreeNode ON = new ParseTreeNode();
        ParseTreeNode OFF = new ParseTreeNode();
        ParseTree PROG_BODY = new ParseTree();
        ParseTree PROGRAM_STMNT = new ParseTree();
        Token ERROR;

        if (currentToken.getTokenName().equals(TokenName.ON)) {
            ON = makeNode(currentToken);
            
            ADVANCE();
            PROG_BODY = PROG_BODY();
            if (currentToken.getTokenName().equals(TokenName.OFF)) {
                OFF = makeNode(currentToken);
            } else {
                ERROR = ERROR(TokenName.OFF, currentToken, currentToken.getLineNumber());
                return new ParseTree(new ParseTreeNode(ERROR, null, null));
            }
        } else {
            ERROR = ERROR(TokenName.ON, currentToken, currentToken.getLineNumber());
            return new ParseTree(new ParseTreeNode(ERROR, null, null));
        }

        if (!PROG_BODY.root.token.getTokenName().equals(TokenName.EMPTY)) {
            ON.setNextSibling(PROG_BODY.root);
            PROG_BODY.root.setNextSibling(OFF);
        } else {
            ON.setNextSibling(OFF);
        }

        PROGRAM_STMNT_NODE.setFirstChild(ON);
        PROGRAM_STMNT.setRoot(PROGRAM_STMNT_NODE);

        return PROGRAM_STMNT;
    }

    public ParseTree PROG_BODY() throws IOException {
        ParseTreeNode PROGRAM_BODY_NODE = new ParseTreeNode(new Token(TokenName.EMPTY, null, null, null, null, 0), null, null);
        ParseTreeNode DATA_TYPE = new ParseTreeNode();
        ParseTree INIT = new ParseTree();
        ParseTree STATEMENT = new ParseTree();
        ParseTree PROGRAM_BODY = new ParseTree(new ParseTreeNode(new Token(TokenName.EMPTY, null, null, null, null, 0), null, null));
        ParseTree PROGRAM_BODY_LOOP = new ParseTree(new ParseTreeNode(new Token(TokenName.EMPTY, null, null, null, null, 0), null, null));

        switch (currentToken.getTokenName()) {
            case DIGIT:
                DATA_TYPE = makeNode(currentToken);
                ADVANCE();
                INIT = NUM_INIT_PRIME();
                ADVANCE();
                PROGRAM_BODY_LOOP = PROG_BODY();
                
                DATA_TYPE.setNextSibling(INIT.root);
                PROGRAM_BODY_NODE.setFirstChild(DATA_TYPE);
                PROGRAM_BODY.setRoot(PROGRAM_BODY_NODE);
                if (!PROGRAM_BODY_LOOP.root.token.getTokenName().equals(TokenName.EMPTY)) {
                    INIT.root.setNextSibling(PROGRAM_BODY_LOOP.root);
                } 
                PROGRAM_BODY_NODE.setToken(new Token(TokenName.PROGRAM_BODY, null, null, null, null, 0));
                return PROGRAM_BODY;
            case SENTENCE:
                DATA_TYPE = makeNode(currentToken);
                ADVANCE();
                INIT = STR_INIT_PRIME();
                ADVANCE();
                PROGRAM_BODY_LOOP = PROG_BODY();
                
                DATA_TYPE.setNextSibling(INIT.root);
                PROGRAM_BODY_NODE.setFirstChild(DATA_TYPE);
                PROGRAM_BODY.setRoot(PROGRAM_BODY_NODE);
                if (!PROGRAM_BODY_LOOP.root.token.getTokenName().equals(TokenName.EMPTY)) {
                    INIT.root.setNextSibling(PROGRAM_BODY_LOOP.root);
                } 
                PROGRAM_BODY_NODE.setToken(new Token(TokenName.PROGRAM_BODY, null, null, null, null, 0));
                return PROGRAM_BODY;
            case VERIFY:
                DATA_TYPE = makeNode(currentToken);
                ADVANCE();
                INIT = BOOL_INIT_PRIME();
                ADVANCE();
                PROGRAM_BODY_LOOP = PROG_BODY();
                
                DATA_TYPE.setNextSibling(INIT.root);
                PROGRAM_BODY_NODE.setFirstChild(DATA_TYPE);
                PROGRAM_BODY.setRoot(PROGRAM_BODY_NODE);
                if (!PROGRAM_BODY_LOOP.root.token.getTokenName().equals(TokenName.EMPTY)) {
                    INIT.root.setNextSibling(PROGRAM_BODY_LOOP.root);
                } 
                PROGRAM_BODY_NODE.setToken(new Token(TokenName.PROGRAM_BODY, null, null, null, null, 0));
                return PROGRAM_BODY;
            case SYMBOL:
                DATA_TYPE = makeNode(currentToken);
                ADVANCE();
                INIT = SYM_INIT_PRIME();
                ADVANCE();
                PROGRAM_BODY_LOOP = PROG_BODY();
                
                DATA_TYPE.setNextSibling(INIT.root);
                PROGRAM_BODY_NODE.setFirstChild(DATA_TYPE);
                PROGRAM_BODY.setRoot(PROGRAM_BODY_NODE);
                if (!PROGRAM_BODY_LOOP.root.token.getTokenName().equals(TokenName.EMPTY)) {
                    INIT.root.setNextSibling(PROGRAM_BODY_LOOP.root);
                } 
                PROGRAM_BODY_NODE.setToken(new Token(TokenName.PROGRAM_BODY, null, null, null, null, 0));
                return PROGRAM_BODY;
            case DECIMAL:
                DATA_TYPE = makeNode(currentToken);
                ADVANCE();
                INIT = DEC_INIT_PRIME();
                ADVANCE();
                PROGRAM_BODY_LOOP = PROG_BODY();
                
                DATA_TYPE.setNextSibling(INIT.root);
                PROGRAM_BODY_NODE.setFirstChild(DATA_TYPE);
                PROGRAM_BODY.setRoot(PROGRAM_BODY_NODE);
                if (!PROGRAM_BODY_LOOP.root.token.getTokenName().equals(TokenName.EMPTY)) {
                    INIT.root.setNextSibling(PROGRAM_BODY_LOOP.root);
                } 
                PROGRAM_BODY_NODE.setToken(new Token(TokenName.PROGRAM_BODY, null, null, null, null, 0));
                return PROGRAM_BODY;
            case EXECUTE:
            case DURING:
            case IF:
            case ELSEIF:
            case ELSE:
            case IDENTIFIER:
            case RECEIVE:
            case TRANSMIT:
                STATEMENT = STATEMENT();
                ADVANCE();
                PROGRAM_BODY_LOOP = PROG_BODY();
                
                PROGRAM_BODY_NODE.setFirstChild(STATEMENT.root);
                PROGRAM_BODY.setRoot(PROGRAM_BODY_NODE);
                if (!PROGRAM_BODY_LOOP.root.token.getTokenName().equals(TokenName.EMPTY)) {
                    STATEMENT.root.setNextSibling(PROGRAM_BODY_LOOP.root);
                }
                PROGRAM_BODY_NODE.setToken(new Token(TokenName.PROGRAM_BODY, null, null, null, null, 0));
                return PROGRAM_BODY;
            default:
                break;
        }
        PROGRAM_BODY.setRoot(PROGRAM_BODY_NODE);
        return PROGRAM_BODY;
    }

    public ParseTree NUM_INIT_PRIME() throws IOException {
        ParseTreeNode NUM_INIT_PRIME_NODE = new ParseTreeNode(new Token(TokenName.NUM_INIT_PRIME, null, null, null, null, 0), null, null); //root node
        ParseTreeNode IDENTIFIER = new ParseTreeNode(); // first child
        ParseTree NUMERIC_ASSIGN = new ParseTree(); // new parse tree for NUMERIC_ASSIGN
        ParseTree NUM_INIT_PRIME = new ParseTree(new ParseTreeNode(new Token(TokenName.EMPTY, null, null, null, null, 0), null, null));
        ParseTree NUM_INIT = new ParseTree();
        Token ERROR;

        if (currentToken.getTokenName().equals(TokenName.IDENTIFIER)) {
            IDENTIFIER = makeNode(currentToken); //current child
            ADVANCE(); //consume next token
            NUMERIC_ASSIGN = NUMERIC_ASSIGN(TokenName.CONSINT);
            if ((NUMERIC_ASSIGN.root.token.getTokenName().equals(TokenName.EMPTY))) {
                NUM_INIT = NUM_INIT();
            }
            //consume next token
            
        } else {
                ERROR = ERROR(TokenName.IDENTIFIER, currentToken, currentToken.getLineNumber());
                return new ParseTree(new ParseTreeNode(ERROR, null, null));
        }
        
        if (!NUMERIC_ASSIGN.root.token.getTokenName().equals(TokenName.EMPTY)) {
            IDENTIFIER.setNextSibling(NUMERIC_ASSIGN.root);
            NUMERIC_ASSIGN.root.setNextSibling(NUM_INIT.root);
        } else {
            IDENTIFIER.setNextSibling(NUM_INIT.root);
        }
        
        NUM_INIT_PRIME_NODE.setFirstChild(IDENTIFIER);
        NUM_INIT_PRIME.setRoot(NUM_INIT_PRIME_NODE);

        return NUM_INIT_PRIME; //return parse tree
    }

    public ParseTree NUM_INIT() throws IOException {
        ParseTreeNode NUM_INIT_NODE = new ParseTreeNode(new Token(TokenName.NUM_INIT, null, null, null, null, 0), null, null); //root node
        ParseTreeNode EOL = new ParseTreeNode(); // second child
        ParseTreeNode COMMA = new ParseTreeNode();
        ParseTree NUM_INIT = new ParseTree();
        ParseTree NUM_INIT_PRIME = new ParseTree();
        Token ERROR;

        switch(currentToken.getTokenName()) {
            case EOL:
                EOL = makeNode(currentToken); //current child
                NUM_INIT_NODE.setFirstChild(EOL);
                NUM_INIT.setRoot(NUM_INIT_NODE);
                break;
            case COMMA:
                COMMA = makeNode(currentToken); //current child
                ADVANCE(); //consume next token
                NUM_INIT_PRIME = NUM_INIT_PRIME();
                NUM_INIT_NODE.setFirstChild(COMMA);
                NUM_INIT_NODE.setNextSibling(NUM_INIT_PRIME.root);
                NUM_INIT.setRoot(NUM_INIT_NODE);
                break;
            case IDENTIFIER:
                ERROR = ERROR(TokenName.COMMA, currentToken, currentToken.getLineNumber());
                return new ParseTree(new ParseTreeNode(ERROR, null, null));
            default:
                ERROR = ERROR(TokenName.EOL, currentToken, currentToken.getLineNumber());
                return new ParseTree(new ParseTreeNode(ERROR, null, null));
        }
        return NUM_INIT; //return parse tree
    }

    public ParseTree NUMERIC_ASSIGN(TokenName name) throws IOException {
        ParseTreeNode NUMERIC_ASSIGN_NODE = new ParseTreeNode(new Token(TokenName.EMPTY, null, null, null, null, 0), null, null); //root node
        ParseTreeNode ASSIGNOPP = new ParseTreeNode(); // first child
        ParseTreeNode CONSINT = new ParseTreeNode(); // second child
        ParseTree NUMERIC_ASSIGN = new ParseTree(new ParseTreeNode(new Token(TokenName.EMPTY, null, null, null, null, 0), null, null));
        ParseTree NUM_INIT = new ParseTree();
        Token ERROR;

        if (currentToken.getTokenName().equals(TokenName.ASSIGNOPP)) {
            ASSIGNOPP = makeNode(currentToken); //current child
            ADVANCE(); //consume next token
            if (currentToken.getTokenName().equals(TokenName.CONSINT) || currentToken.getTokenName().equals(TokenName.CONSFLOAT) || currentToken.getTokenName().equals(TokenName.IDENTIFIER)) {
                CONSINT = makeNode(currentToken); //next child    
                ADVANCE(); //consume next token
                NUM_INIT = NUM_INIT();
                NUMERIC_ASSIGN_NODE.setToken(new Token(TokenName.NUMERIC_ASSIGN, null, null, null, null, 0));
            } else {
                ERROR = ERROR(name, currentToken, currentToken.getLineNumber());
                return new ParseTree(new ParseTreeNode(ERROR, null, null));
            }

            NUMERIC_ASSIGN_NODE.setFirstChild(ASSIGNOPP);
            ASSIGNOPP.setNextSibling(CONSINT);
            CONSINT.setNextSibling(NUM_INIT.root);
            NUMERIC_ASSIGN.setRoot(NUMERIC_ASSIGN_NODE);
            return NUMERIC_ASSIGN;
        }
        return NUMERIC_ASSIGN;
    }

    public ParseTree DEC_INIT_PRIME() throws IOException {
        ParseTreeNode DEC_INIT_PRIME_NODE = new ParseTreeNode(new Token(TokenName.DEC_INIT_PRIME, null, null, null, null, 0), null, null); //root node
        ParseTreeNode IDENTIFIER = new ParseTreeNode(); // first child
        ParseTree NUMERIC_ASSIGN = new ParseTree(); // new parse tree for NUMERIC_ASSIGN
        ParseTree DEC_INIT_PRIME = new ParseTree(new ParseTreeNode(new Token(TokenName.EMPTY, null, null, null, null, 0), null, null));
        ParseTree DEC_INIT = new ParseTree();
        Token ERROR;

        if (currentToken.getTokenName().equals(TokenName.IDENTIFIER)) {
            IDENTIFIER = makeNode(currentToken); //current child
            ADVANCE(); //consume next token
            NUMERIC_ASSIGN = NUMERIC_ASSIGN(TokenName.CONSFLOAT);
            if ((NUMERIC_ASSIGN.root.token.getTokenName().equals(TokenName.EMPTY))) {
                DEC_INIT = DEC_INIT();
            }
            
        } else {
                ERROR = ERROR(TokenName.IDENTIFIER, currentToken, currentToken.getLineNumber());
                return new ParseTree(new ParseTreeNode(ERROR, null, null));
        }

        if (!NUMERIC_ASSIGN.root.token.getTokenName().equals(TokenName.EMPTY)) {
            IDENTIFIER.setNextSibling(NUMERIC_ASSIGN.root);
            NUMERIC_ASSIGN.root.setNextSibling(DEC_INIT.root);
        } else {
            IDENTIFIER.setNextSibling(DEC_INIT.root);
        }
        
        DEC_INIT_PRIME_NODE.setFirstChild(IDENTIFIER);
        DEC_INIT_PRIME.setRoot(DEC_INIT_PRIME_NODE);

        return DEC_INIT_PRIME; //return parse tree
    }
    

    public ParseTree DEC_INIT() throws IOException {
        ParseTreeNode DEC_INIT_NODE = new ParseTreeNode(new Token(TokenName.DEC_INIT, null, null, null, null, 0), null, null); //root node
        ParseTreeNode EOL = new ParseTreeNode(); // second child
        ParseTreeNode COMMA = new ParseTreeNode();
        ParseTree DEC_INIT = new ParseTree();
        ParseTree DEC_INIT_PRIME = new ParseTree();
        Token ERROR;

        switch(currentToken.getTokenName()) {
            case EOL:
                EOL = makeNode(currentToken); //current child
                DEC_INIT_NODE.setFirstChild(EOL);
                DEC_INIT.setRoot(DEC_INIT_NODE);
                break;
            case COMMA:
                COMMA = makeNode(currentToken); //current child
                ADVANCE(); //consume next token
                DEC_INIT_PRIME = DEC_INIT_PRIME();
                DEC_INIT_NODE.setFirstChild(COMMA);
                DEC_INIT_NODE.setNextSibling(DEC_INIT_PRIME.root);
                DEC_INIT.setRoot(DEC_INIT_NODE);
                break;
            case IDENTIFIER:
                ERROR = ERROR(TokenName.COMMA, currentToken, currentToken.getLineNumber());
                return new ParseTree(new ParseTreeNode(ERROR, null, null));
            default:
                ERROR = ERROR(TokenName.EOL, currentToken, currentToken.getLineNumber());
                return new ParseTree(new ParseTreeNode(ERROR, null, null));
        }
        return DEC_INIT; //return parse tree
    }

    public ParseTree DEC_ASSIGN() throws IOException {
        ParseTreeNode DEC_ASSIGN_NODE = new ParseTreeNode(new Token(TokenName.EMPTY, null, null, null, null, 0), null, null); //root node
        ParseTreeNode ASSIGNOPP = new ParseTreeNode(); // first child
        ParseTreeNode CONSINT = new ParseTreeNode(); // second child
        ParseTree DEC_ASSIGN = new ParseTree(new ParseTreeNode(new Token(TokenName.EMPTY, null, null, null, null, 0), null, null));
        ParseTree DEC_INIT = new ParseTree();
        Token ERROR;

        if (currentToken.getTokenName().equals(TokenName.ASSIGNOPP)) {
            ASSIGNOPP = makeNode(currentToken); //current child
            ADVANCE(); //consume next token
            if (currentToken.getTokenName().equals(TokenName.CONSINT)) {
                CONSINT = makeNode(currentToken); //next child    
                ADVANCE(); //consume next token
                DEC_INIT = DEC_INIT();
                DEC_ASSIGN_NODE.setToken(new Token(TokenName.DEC_ASSIGN, null, null, null, null, 0));
            } else {
                ERROR = ERROR(TokenName.CONSINT, currentToken, currentToken.getLineNumber());
                return new ParseTree(new ParseTreeNode(ERROR, null, null));
            }

            DEC_ASSIGN_NODE.setFirstChild(ASSIGNOPP);
            ASSIGNOPP.setNextSibling(CONSINT);
            DEC_ASSIGN_NODE.setNextSibling(DEC_INIT.root);
            DEC_ASSIGN.setRoot(DEC_ASSIGN_NODE);
            return DEC_ASSIGN;
        }
        return DEC_ASSIGN;
    }

    public ParseTree SYM_INIT_PRIME() throws IOException {
        ParseTreeNode SYM_INIT_PRIME_NODE = new ParseTreeNode(new Token(TokenName.SYM_INIT_PRIME, null, null, null, null, 0), null, null); //root node
        ParseTreeNode IDENTIFIER = new ParseTreeNode(); // first child
        ParseTree SYM_ASSIGN = new ParseTree(); // new parse tree for SYM_ASSIGN
        ParseTree SYM_INIT_PRIME = new ParseTree(new ParseTreeNode(new Token(TokenName.EMPTY, null, null, null, null, 0), null, null));
        ParseTree SYM_INIT = new ParseTree();
        Token ERROR;

        if (currentToken.getTokenName().equals(TokenName.IDENTIFIER)) {
            IDENTIFIER = makeNode(currentToken); //current child
            ADVANCE(); //consume next token
            SYM_ASSIGN = SYM_ASSIGN();
            if (!SYM_ASSIGN.root.token.getTokenName().equals(TokenName.EMPTY)) {
                ADVANCE();
            }//consume next token
            SYM_INIT = SYM_INIT();
        } else {
                ERROR = ERROR(TokenName.IDENTIFIER, currentToken, currentToken.getLineNumber());
                return new ParseTree(new ParseTreeNode(ERROR, null, null));
        }

        if (!SYM_ASSIGN.root.token.getTokenName().equals(TokenName.EMPTY)) {
            IDENTIFIER.setNextSibling(SYM_ASSIGN.root);
            SYM_ASSIGN.root.setNextSibling(SYM_INIT.root);
        } else {
            IDENTIFIER.setNextSibling(SYM_INIT.root);
        }
        
        SYM_INIT_PRIME_NODE.setFirstChild(IDENTIFIER);
        SYM_INIT_PRIME.setRoot(SYM_INIT_PRIME_NODE);

        return SYM_INIT_PRIME; //return parse tree
    }

    public ParseTree SYM_INIT() throws IOException {
        ParseTreeNode SYM_INIT_NODE = new ParseTreeNode(new Token(TokenName.SYM_INIT, null, null, null, null, 0), null, null); //root node
        ParseTreeNode EOL = new ParseTreeNode(); // second child
        ParseTreeNode COMMA = new ParseTreeNode();
        ParseTree SYM_INIT = new ParseTree();
        ParseTree SYM_INIT_PRIME = new ParseTree();
        Token ERROR;

        switch(currentToken.getTokenName()) {
            case EOL:
                EOL = makeNode(currentToken); //current child
                SYM_INIT_NODE.setFirstChild(EOL);
                SYM_INIT.setRoot(SYM_INIT_NODE);
                break;
            case COMMA:
                COMMA = makeNode(currentToken); //current child
                ADVANCE(); //consume next token
                SYM_INIT_PRIME = SYM_INIT_PRIME();
                SYM_INIT_NODE.setFirstChild(COMMA);
                SYM_INIT_NODE.setNextSibling(SYM_INIT_PRIME.root);
                SYM_INIT.setRoot(SYM_INIT_NODE);
                break;
            case IDENTIFIER:
                ERROR = ERROR(TokenName.COMMA, currentToken, currentToken.getLineNumber());
                return new ParseTree(new ParseTreeNode(ERROR, null, null));
            default:
                ERROR = ERROR(TokenName.EOL, currentToken, currentToken.getLineNumber());
                return new ParseTree(new ParseTreeNode(ERROR, null, null));
        }
        return SYM_INIT; //return parse tree
    }

    public ParseTree SYM_ASSIGN() throws IOException {
        ParseTreeNode SYM_ASSIGN_NODE = new ParseTreeNode(new Token(TokenName.EMPTY, null, null, null, null, 0), null, null); //root node
        ParseTreeNode ASSIGNOPP = new ParseTreeNode(); // first child
        ParseTreeNode CONSINT = new ParseTreeNode(); // second child
        ParseTree SYM_ASSIGN = new ParseTree(new ParseTreeNode(new Token(TokenName.EMPTY, null, null, null, null, 0), null, null));
        ParseTree SYM_INIT = new ParseTree();
        Token ERROR;

        if (currentToken.getTokenName().equals(TokenName.ASSIGNOPP)) {
            ASSIGNOPP = makeNode(currentToken); //current child
            ADVANCE(); //consume next token
            if (currentToken.getTokenName().equals(TokenName.CONSINT)) {
                CONSINT = makeNode(currentToken); //next child    
                ADVANCE(); //consume next token
                SYM_INIT = SYM_INIT();
                SYM_ASSIGN_NODE.setToken(new Token(TokenName.SYM_ASSIGN, null, null, null, null, 0));
            } else {
                ERROR = ERROR(TokenName.CONSINT, currentToken, currentToken.getLineNumber());
                return new ParseTree(new ParseTreeNode(ERROR, null, null));
            }

            SYM_ASSIGN_NODE.setFirstChild(ASSIGNOPP);
            ASSIGNOPP.setNextSibling(CONSINT);
            SYM_ASSIGN_NODE.setNextSibling(SYM_INIT.root);
            SYM_ASSIGN.setRoot(SYM_ASSIGN_NODE);
            return SYM_ASSIGN;
        }
        return SYM_ASSIGN;
    }

    public ParseTree STR_INIT_PRIME() throws IOException {
        ParseTreeNode STR_INIT_PRIME_NODE = new ParseTreeNode(new Token(TokenName.STR_INIT_PRIME, null, null, null, null, 0), null, null); //root node
        ParseTreeNode IDENTIFIER = new ParseTreeNode(); // first child
        ParseTree STR_ASSIGN = new ParseTree(); // new parse tree for NUMERIC_ASSIGN
        ParseTree STR_INIT_PRIME = new ParseTree(new ParseTreeNode(new Token(TokenName.EMPTY, null, null, null, null, 0), null, null));
        ParseTree STR_INIT = new ParseTree();
        Token ERROR;

        if (currentToken.getTokenName().equals(TokenName.IDENTIFIER)) {
            IDENTIFIER = makeNode(currentToken); //current child
            ADVANCE(); //consume next token
            STR_ASSIGN = STR_ASSIGN();
            if (!STR_ASSIGN.root.token.getTokenName().equals(TokenName.EMPTY)) {
                ADVANCE();
            }//consume next token
            STR_INIT = STR_INIT();
        } else {
                ERROR = ERROR(TokenName.IDENTIFIER, currentToken, currentToken.getLineNumber());
                return new ParseTree(new ParseTreeNode(ERROR, null, null));
        }

        if (!STR_ASSIGN.root.token.getTokenName().equals(TokenName.EMPTY)) {
            IDENTIFIER.setNextSibling(STR_ASSIGN.root);
            STR_ASSIGN.root.setNextSibling(STR_INIT.root);
        } else {
            IDENTIFIER.setNextSibling(STR_INIT.root);
        }
        
        STR_INIT_PRIME_NODE.setFirstChild(IDENTIFIER);
        STR_INIT_PRIME.setRoot(STR_INIT_PRIME_NODE);

        return STR_INIT_PRIME; //return parse tree
    }

    public ParseTree STR_INIT() throws IOException {
        ParseTreeNode STR_INIT_NODE = new ParseTreeNode(new Token(TokenName.STR_INIT, null, null, null, null, 0), null, null); //root node
        ParseTreeNode EOL = new ParseTreeNode(); // second child
        ParseTreeNode COMMA = new ParseTreeNode();
        ParseTree STR_INIT = new ParseTree();
        ParseTree STR_INIT_PRIME = new ParseTree();
        Token ERROR;

        switch(currentToken.getTokenName()) {
            case EOL:
                EOL = makeNode(currentToken); //current child
                STR_INIT_NODE.setFirstChild(EOL);
                STR_INIT.setRoot(STR_INIT_NODE);
                break;
            case COMMA:
                COMMA = makeNode(currentToken); //current child
                ADVANCE(); //consume next token
                STR_INIT_PRIME = STR_INIT_PRIME();
                STR_INIT_NODE.setFirstChild(COMMA);
                STR_INIT_NODE.setNextSibling(STR_INIT_PRIME.root);
                STR_INIT.setRoot(STR_INIT_NODE);
                break;
            case IDENTIFIER:
                ERROR = ERROR(TokenName.COMMA, currentToken, currentToken.getLineNumber());
                return new ParseTree(new ParseTreeNode(ERROR, null, null));
            default:
                ERROR = ERROR(TokenName.EOL, currentToken, currentToken.getLineNumber());
                return new ParseTree(new ParseTreeNode(ERROR, null, null));
        }
        return STR_INIT; //return parse tree
    }

    public ParseTree STR_ASSIGN() throws IOException {
        ParseTreeNode STR_ASSIGN_NODE = new ParseTreeNode(new Token(TokenName.EMPTY, null, null, null, null, 0), null, null); //root node
        ParseTreeNode ASSIGNOPP = new ParseTreeNode(); // first child
        ParseTreeNode CONSSTR = new ParseTreeNode(); // second child
        ParseTree STR_ASSIGN = new ParseTree(new ParseTreeNode(new Token(TokenName.EMPTY, null, null, null, null, 0), null, null));
        ParseTree STR_INIT = new ParseTree();
        Token ERROR;

        if (currentToken.getTokenName().equals(TokenName.ASSIGNOPP)) {
            ASSIGNOPP = makeNode(currentToken); //current child
            ADVANCE(); //consume next token
            if (currentToken.getTokenName().equals(TokenName.CONSSTR)) {
                CONSSTR = makeNode(currentToken); //next child    
                ADVANCE(); //consume next token
                STR_INIT = NUM_INIT();
                STR_ASSIGN_NODE.setToken(new Token(TokenName.STR_ASSIGN, null, null, null, null, 0));
            } else {
                ERROR = ERROR(TokenName.CONSSTR, currentToken, currentToken.getLineNumber());
                return new ParseTree(new ParseTreeNode(ERROR, null, null));
            }

            STR_ASSIGN_NODE.setFirstChild(ASSIGNOPP);
            ASSIGNOPP.setNextSibling(CONSSTR);
            STR_ASSIGN_NODE.setNextSibling(STR_INIT.root);
            STR_ASSIGN.setRoot(STR_ASSIGN_NODE);
            return STR_ASSIGN;
        }
        return STR_ASSIGN;
    }

    public ParseTree BOOL_INIT_PRIME() throws IOException {
        ParseTreeNode BOOL_INIT_PRIME_NODE = new ParseTreeNode(new Token(TokenName.BOOL_INIT_PRIME, null, null, null, null, 0), null, null); //root node
        ParseTreeNode IDENTIFIER = new ParseTreeNode(); // first child
        ParseTree BOOL_ASSIGN = new ParseTree(); // new parse tree for BOOL_ASSIGN
        ParseTree BOOL_INIT_PRIME = new ParseTree(new ParseTreeNode(new Token(TokenName.EMPTY, null, null, null, null, 0), null, null));
        ParseTree BOOL_INIT = new ParseTree();
        Token ERROR;

        if (currentToken.getTokenName().equals(TokenName.IDENTIFIER)) {
            IDENTIFIER = makeNode(currentToken); //current child
            ADVANCE(); //consume next token
            BOOL_ASSIGN = BOOL_ASSIGN();
            if (!BOOL_ASSIGN.root.token.getTokenName().equals(TokenName.EMPTY)) {
                ADVANCE();
            }//consume next token
            BOOL_INIT = BOOL_INIT();
        } else {
                ERROR = ERROR(TokenName.IDENTIFIER, currentToken, currentToken.getLineNumber());
                return new ParseTree(new ParseTreeNode(ERROR, null, null));
        }

        if (!BOOL_ASSIGN.root.token.getTokenName().equals(TokenName.EMPTY)) {
            IDENTIFIER.setNextSibling(BOOL_ASSIGN.root);
            BOOL_ASSIGN.root.setNextSibling(BOOL_INIT.root);
        } else {
            IDENTIFIER.setNextSibling(BOOL_INIT.root);
        }
        
        BOOL_INIT_PRIME_NODE.setFirstChild(IDENTIFIER);
        BOOL_INIT_PRIME.setRoot(BOOL_INIT_PRIME_NODE);

        return BOOL_INIT_PRIME; //return parse tree
    }

    public ParseTree BOOL_INIT() throws IOException {
        ParseTreeNode BOOL_INIT_NODE = new ParseTreeNode(new Token(TokenName.BOOL_INIT, null, null, null, null, 0), null, null); //root node
        ParseTreeNode EOL = new ParseTreeNode(); // second child
        ParseTreeNode COMMA = new ParseTreeNode();
        ParseTree BOOL_INIT = new ParseTree();
        ParseTree BOOL_INIT_PRIME = new ParseTree();
        Token ERROR;

        switch(currentToken.getTokenName()) {
            case EOL:
                EOL = makeNode(currentToken); //current child
                BOOL_INIT_NODE.setFirstChild(EOL);
                BOOL_INIT.setRoot(BOOL_INIT_NODE);
                break;
            case COMMA:
                COMMA = makeNode(currentToken); //current child
                ADVANCE(); //consume next token
                BOOL_INIT_PRIME = BOOL_INIT_PRIME();
                BOOL_INIT_NODE.setFirstChild(COMMA);
                BOOL_INIT_NODE.setNextSibling(BOOL_INIT_PRIME.root);
                BOOL_INIT.setRoot(BOOL_INIT_NODE);
                break;
            case IDENTIFIER:
                ERROR = ERROR(TokenName.COMMA, currentToken, currentToken.getLineNumber());
                return new ParseTree(new ParseTreeNode(ERROR, null, null));
            default:
                ERROR = ERROR(TokenName.EOL, currentToken, currentToken.getLineNumber());
                return new ParseTree(new ParseTreeNode(ERROR, null, null));
        }
        return BOOL_INIT; //return parse tree
    }

    public ParseTree BOOL_ASSIGN() throws IOException {
        ParseTreeNode BOOL_ASSIGN_NODE = new ParseTreeNode(new Token(TokenName.EMPTY, null, null, null, null, 0), null, null); //root node
        ParseTreeNode ASSIGNOPP = new ParseTreeNode(); // first child
        ParseTree BOOL_ASSIGN = new ParseTree(new ParseTreeNode(new Token(TokenName.EMPTY, null, null, null, null, 0), null, null));
        ParseTree BOOL_INIT = new ParseTree();
        ParseTree BOOL_VALUE = new ParseTree();
        Token ERROR;

        if (currentToken.getTokenName().equals(TokenName.ASSIGNOPP)) {
            ASSIGNOPP = makeNode(currentToken); //current child
            ADVANCE(); //consume next token
            BOOL_VALUE = BOOL_VALUE();
            BOOL_ASSIGN_NODE.setFirstChild(ASSIGNOPP);
            ASSIGNOPP.setNextSibling(BOOL_VALUE.root);
            BOOL_ASSIGN_NODE.setNextSibling(BOOL_INIT.root);
            BOOL_ASSIGN.setRoot(BOOL_ASSIGN_NODE);
            return BOOL_ASSIGN;
        }
        return BOOL_ASSIGN;
    }

    public ParseTree BOOL_VALUE() throws IOException {
        ParseTreeNode BOOL_VALUE_NODE = new ParseTreeNode(new Token(TokenName.BOOL_VALUE, null, null, null, null, 0), null, null); //root node
        ParseTreeNode ASSIGNOPP = new ParseTreeNode(); // first child
        ParseTree BOOL_VALUE = new ParseTree();

        if (currentToken.getTokenName().equals(TokenName.AFFIRM)) {
            ASSIGNOPP = makeNode(currentToken); //current child
        } else if (currentToken.getTokenName().equals(TokenName.NEGATE)) {
            ASSIGNOPP = makeNode(currentToken); //current child
        } else {
            System.out.println("Error, Token AFFIRM/NEGATE not found!");
            return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
        }

        BOOL_VALUE_NODE.setFirstChild(ASSIGNOPP);
        BOOL_VALUE.setRoot(BOOL_VALUE_NODE);
        return BOOL_VALUE;
    }

    public ParseTree STATEMENT() throws IOException {
        ParseTreeNode STATEMENT_NODE = new ParseTreeNode(new Token(TokenName.STATEMENT_NODE, null, null, null, null, 0), null, null); //root node
        ParseTree STATEMENT = new ParseTree();

        STATEMENT.setRoot(STATEMENT_NODE);
        return STATEMENT;
    }
    
    public ParseTree EXPRESSION() throws IOException {
        LinkedList<ParseTreeNode> expressionInput = new LinkedList<>();
        LinkedList<ParseTreeNode> expressionStack = new LinkedList<ParseTreeNode>();
        LinkedList<ParseTreeNode> operandStack = new LinkedList<ParseTreeNode>();
        OperatorPrecedenceTable precedenceTable = new OperatorPrecedenceTable();
        ParseTreeNode holder, firstOperand, secondOperand;
        ParseTree subTree = new ParseTree();
        ParseTree EXPRESSION = new ParseTree();
        
        while (!currentToken.getTokenName().equals(TokenName.EOL)) {
            expressionInput.offerLast(new ParseTreeNode(currentToken, null, null));
            ADVANCE();
        }
        expressionInput.offerLast(new ParseTreeNode(new Token(TokenName.DOLLAR_OPERATOR, null, null, null, null, 0), null, null));
        expressionStack.push(new ParseTreeNode(new Token(TokenName.DOLLAR_OPERATOR, null, null, null, null, 0), null, null));
        
        while (!expressionInput.isEmpty()) {
            if (expressionInput.peekFirst().getToken().getTokenName().equals(TokenName.DOLLAR_OPERATOR) && expressionStack.peekLast().getToken().getTokenName().equals(TokenName.DOLLAR_OPERATOR)) {
                EXPRESSION.setRoot(operandStack.pop());
                break;
            } else if (precedenceTable.evaluatePrecedence(expressionStack.peekLast().getToken().getTokenName(), expressionInput.peekFirst().getToken().getTokenName()).equals(OperatorPrecedence.LESSER) || precedenceTable.evaluatePrecedence(expressionStack.peekLast().getToken().getTokenName(), expressionInput.peekFirst().getToken().getTokenName()).equals(OperatorPrecedence.EQUAL)) {
                expressionStack.push(expressionInput.removeFirst());
            } else if (precedenceTable.evaluatePrecedence(expressionStack.peekLast().getToken().getTokenName(), expressionInput.peekFirst().getToken().getTokenName()).equals(OperatorPrecedence.GREATER)) {
                holder = expressionStack.pop();
                if (holder.getToken().getTokenName().equals(TokenName.CONSINT) || holder.getToken().getTokenName().equals(TokenName.CONSFLOAT) || holder.getToken().getTokenName().equals(TokenName.IDENTIFIER)) {
                    operandStack.push(holder);
                } 
                else {
                    secondOperand = operandStack.pop();
                    firstOperand = operandStack.pop();
                    holder.setChildAndSibling(firstOperand, secondOperand);
                    subTree.setRoot(holder);
                    operandStack.push(subTree.root);
                }
            }
        }
        
        return EXPRESSION;
    }
    
    //C:\Users\Theodore Arnel Merin\Documents\sample.txt
    //'ÿ'
    public void sourceScanner(String absPath) throws FileNotFoundException, IOException {
        String ANSI_BLUE = "\u001B[34m";
        String ANSI_RESET = "\u001B[0m";
        String ANSI_GREEN = "\u001B[32m";
        long startTime;
        long estimatedTime;
        long divisor = 1000000;
        
        System.out.println(ANSI_BLUE + "-----------------------------------------------------------------TOKEN LIST-----------------------------------------------------------------" + ANSI_RESET);
        startTime = System.nanoTime();
        lex1.sourceScanner(absPath);
        estimatedTime = System.nanoTime() - startTime;
        System.out.println();
        System.out.println(ANSI_GREEN + "SCANNING COMPLETE");
        System.out.println(ANSI_GREEN + "Time Elapsed: " + (estimatedTime / divisor) + " milliseconds");
        System.out.println();
        System.out.println(ANSI_BLUE + "-----------------------------------------------------------------PARSE TREE-----------------------------------------------------------------" + ANSI_RESET);
        startTime = System.nanoTime();
        currentToken = lex.driver(absPath);
        ParseTree pTree = EXPRESSION();
        if (!pTree.root.token.getTokenName().equals(TokenName.ERROR)) {
            System.out.print(pTree.printParseTree());
        }
        estimatedTime = System.nanoTime() - startTime;
        System.out.println();
        System.out.println(ANSI_GREEN + "PARSING COMPLETE");
        System.out.println(ANSI_GREEN + "Time Elapsed: " + (estimatedTime / divisor) + " milliseconds");
        System.out.println();
        System.exit(0);
    }

    public static void main(String[] args) throws IOException {
        Scanner console = new Scanner(System.in);
        SyntaxAnalyzer syn = new SyntaxAnalyzer();
        String filePath = "";
        System.out.println("Enter the absolute file path of the source code: ");
        //filePath = console.nextLine();
        syn.sourceScanner("C:\\Users\\Theodore Arnel Merin\\Documents\\sample7.txt");
        
        
    }
}
