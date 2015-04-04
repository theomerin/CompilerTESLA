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
    Token overReadToken = new Token();
    int unmatchedLeftPar = 0;

    public SyntaxAnalyzer() throws FileNotFoundException, IOException {

    }

    public void ADVANCE() throws IOException {
        currentToken = lex.consumeToken();
    }

    public Token ERROR(TokenName expectedToken, Token currentToken, int lineNumber) {
        String ANSI_RED = "\u001B[31m";
        String ANSI_RESET = "\u001B[0m";
        if (expectedToken.equals(TokenName.DATA_TYPE_MISMATCH)) {
            System.out.println(ANSI_RED + "PARSING NOT SUCCESSFUL: " + expectedToken + " of token found at line number (" + lineNumber + "): " + currentToken.getTokenName() + " Lexeme of: " + currentToken.getLexeme());
        } else if (expectedToken.equals(TokenName.IDENTIFIER_NOT_DECLARED)) {
            System.out.println(ANSI_RED + "PARSING NOT SUCCESSFUL: " + expectedToken + " at line number (" + lineNumber + "): " + currentToken.getTokenName() + " Lexeme of: " + currentToken.getLexeme());
        } else {
            System.out.println(ANSI_RED + "PARSING NOT SUCCESSFUL: Token Expected: " + expectedToken + ". token found at line number (" + lineNumber + "): " + currentToken.getTokenName() + " Lexeme of: " + currentToken.getLexeme());
        }
        System.out.print(ANSI_RESET);
        return new Token(TokenName.ERROR, null, null, null, null, lineNumber);
    }

    public ParseTreeNode makeNode(Token token) {
        return new ParseTreeNode(token, null, null);
    }

    public ParseTree PROGRAM() throws IOException {
        ParseTreeNode PROGRAM_NODE = new ParseTreeNode(new Token(TokenName.PROGRAM_ROOT, null, null, null, null, 0), null, null);
        ParseTreeNode ENGAGE = new ParseTreeNode();
        ParseTreeNode LEFTBRACKET = new ParseTreeNode();
        ParseTreeNode PROGRAMNAME = new ParseTreeNode();
        ParseTreeNode RIGHTBRACKET = new ParseTreeNode();
        ParseTreeNode TERMINATE = new ParseTreeNode();
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
                        STATEMENTS = PROG_STMT();
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

    public ParseTree PROG_STMT() throws IOException {
        ParseTreeNode PROGRAM_STMT_NODE = new ParseTreeNode(new Token(TokenName.PROGRAM_STMT, null, null, null, null, 0), null, null);
        ParseTreeNode ON = new ParseTreeNode();
        ParseTreeNode OFF = new ParseTreeNode();
        ParseTree PROG_BODY = new ParseTree();
        ParseTree PROGRAM_STMT = new ParseTree();
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

        PROGRAM_STMT_NODE.setFirstChild(ON);
        PROGRAM_STMT.setRoot(PROGRAM_STMT_NODE);

        return PROGRAM_STMT;
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

            case IDENTIFIER:
            case INC:
            case DEC:
            case IF:
            case TRANSMIT:
            case RECEIVE:
            case DURING:
            case EXECUTE:
                STATEMENT = STATEMENT();
                //ADVANCE();
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
            currentToken.setDataType(DataType.INTEGER);
            lex.SymbolTable.putIfAbsent(currentToken.getLexeme(), currentToken);
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

        switch (currentToken.getTokenName()) {
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
        ParseTree EXPRESSION = new ParseTree(new ParseTreeNode(new Token(TokenName.EXPRESSION, null, null, null, null, 0), null, null));
        ParseTree NUMERIC_ASSIGN = new ParseTree(new ParseTreeNode(new Token(TokenName.EMPTY, null, null, null, null, 0), null, null));
        ParseTree NUM_INIT = new ParseTree();
        Token ERROR;

        if (currentToken.getTokenName().equals(TokenName.ASSIGNOPP)) {
            ASSIGNOPP = makeNode(currentToken); //current child
            ADVANCE(); //consume next token
            if (currentToken.getTokenName().equals(TokenName.CONSINT) || currentToken.getTokenName().equals(TokenName.CONSFLOAT) || currentToken.getTokenName().equals(TokenName.IDENTIFIER) || currentToken.getTokenName().equals(TokenName.LEFTPAR) || currentToken.getTokenName().equals(TokenName.INC) || currentToken.getTokenName().equals(TokenName.DEC) || currentToken.getTokenName().equals(TokenName.DIFF)) {
                EXPRESSION.root.setFirstChild(ARITHMETIC_EXPRESSION().root);
                NUM_INIT = NUM_INIT();
                NUMERIC_ASSIGN_NODE.setToken(new Token(TokenName.NUMERIC_ASSIGN, null, null, null, null, 0));
            } else {
                ERROR = ERROR(name, currentToken, currentToken.getLineNumber());
                return new ParseTree(new ParseTreeNode(ERROR, null, null));
            }

            NUMERIC_ASSIGN_NODE.setFirstChild(ASSIGNOPP);
            ASSIGNOPP.setNextSibling(EXPRESSION.root);
            EXPRESSION.root.setNextSibling(NUM_INIT.root);
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
            currentToken.setDataType(DataType.FLOATING_POINT);
            lex.SymbolTable.putIfAbsent(currentToken.getLexeme(), currentToken);
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

        switch (currentToken.getTokenName()) {
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

        switch (currentToken.getTokenName()) {
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

        switch (currentToken.getTokenName()) {
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
            currentToken.setDataType(DataType.BOOLEAN);
            lex.SymbolTable.putIfAbsent(currentToken.getLexeme(), currentToken);
            IDENTIFIER = makeNode(currentToken); //current child
            ADVANCE(); //consume next token
            BOOL_ASSIGN = BOOL_ASSIGN();
//            if (!BOOL_ASSIGN.root.token.getTokenName().equals(TokenName.EMPTY)) {
//                //ADVANCE();
//            }//consume next token
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

        switch (currentToken.getTokenName()) {
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
            BOOL_VALUE = LOGICAL_EXPRESSION();

            if (BOOL_VALUE.root.getToken().getTokenName().equals(TokenName.LOGICAL_EXPRESSION)) {
                BOOL_ASSIGN = new ParseTree(new ParseTreeNode(new Token(TokenName.BOOL_ASSIGN, null, null, null, null, 0), null, null));
                BOOL_ASSIGN_NODE = new ParseTreeNode(new Token(TokenName.BOOL_ASSIGN, null, null, null, null, 0), null, null);
            }
            //BOOL_VALUE = BOOL_VALUE();
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
        Token ERROR;

        if (currentToken.getTokenName().equals(TokenName.AFFIRM)) {
            ASSIGNOPP = makeNode(currentToken); //current child
        } else if (currentToken.getTokenName().equals(TokenName.NEGATE)) {
            ASSIGNOPP = makeNode(currentToken); //current child
        } else {
            ERROR = ERROR(TokenName.BOOLEAN_CONSTANT, currentToken, currentToken.getLineNumber());
            return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
        }

        BOOL_VALUE_NODE.setFirstChild(ASSIGNOPP);
        BOOL_VALUE.setRoot(BOOL_VALUE_NODE);
        return BOOL_VALUE;
    }

    public ParseTree NUMERIC_VALUE() throws IOException {
        ParseTreeNode NUMERIC_VALUE_NODE = new ParseTreeNode(new Token(TokenName.NUMERIC_VALUE_NODE, null, null, null, null, 0), null, null); //root node
        ParseTreeNode NUMERIC_NODE = new ParseTreeNode();
        ParseTree NUMERIC_VALUE = new ParseTree();
        Token ERROR;

        if (currentToken.getTokenName().equals(TokenName.IDENTIFIER)) {
            NUMERIC_NODE = makeNode(currentToken);
        } else if (currentToken.getTokenName().equals(TokenName.CONSINT)) {
            NUMERIC_NODE = makeNode(currentToken);
        } else if (currentToken.getTokenName().equals(TokenName.CONSFLOAT)) {
            NUMERIC_NODE = makeNode(currentToken);
        } else {
            ERROR = ERROR(TokenName.NUMERIC_CONSTANT_OR_IDENTIFIER, currentToken, currentToken.getLineNumber());
            return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
        }

        NUMERIC_VALUE_NODE.setFirstChild(NUMERIC_NODE);
        NUMERIC_VALUE.setRoot(NUMERIC_VALUE_NODE);
        return NUMERIC_VALUE;
    }

    public ParseTree STATEMENT() throws IOException {
        ParseTree STATEMENT = new ParseTree(new ParseTreeNode(new Token(TokenName.EMPTY, null, null, null, null, 0), null, null));
        ParseTree STATEMENT_LOOP = new ParseTree(new ParseTreeNode(new Token(TokenName.EMPTY, null, null, null, null, 0), null, null));
        ParseTreeNode EOL = new ParseTreeNode();
        Token ERROR;

        switch (currentToken.getTokenName()) {
            case IDENTIFIER:
                //Prioritize IDENTIFIER of CAST_STMT_PRIME() over INC_POST_STMT and DEC_POST_STMT
                STATEMENT = CAST_STMT_PRIME();
                ADVANCE();
                STATEMENT_LOOP = STATEMENT();
                if (!STATEMENT_LOOP.root.token.getTokenName().equals(TokenName.EMPTY)) {
                    STATEMENT.root.setNextSibling(STATEMENT_LOOP.root);
                }
                return STATEMENT;
            case INC:
                STATEMENT = INC_PRE_STMT();
                ADVANCE();
                if (currentToken.getTokenName().equals(TokenName.EOL)) {
                    EOL = makeNode(currentToken);
                    ADVANCE();
                    STATEMENT_LOOP = STATEMENT();
                    if (!STATEMENT_LOOP.root.token.getTokenName().equals(TokenName.EMPTY)) {
                        EOL.setNextSibling(STATEMENT_LOOP.root);
                    }
                    STATEMENT.root.setNextSibling(EOL);
                    return STATEMENT;
                } else {
                    ERROR = ERROR(TokenName.EOL, currentToken, currentToken.getLineNumber());
                    return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
                }
            case DEC:
                STATEMENT = DEC_PRE_STMT();
                ADVANCE();
                if (currentToken.getTokenName().equals(TokenName.EOL)) {
                    EOL = makeNode(currentToken);
                    ADVANCE();
                    STATEMENT_LOOP = STATEMENT();
                    if (!STATEMENT_LOOP.root.token.getTokenName().equals(TokenName.EMPTY)) {
                        EOL.setNextSibling(STATEMENT_LOOP.root);
                    }
                    STATEMENT.root.setNextSibling(EOL);
                    return STATEMENT;
                } else {
                    ERROR = ERROR(TokenName.EOL, currentToken, currentToken.getLineNumber());
                    return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
                }
            case IF:
                STATEMENT = IF_STMT_PRIME();
                ADVANCE();
                return STATEMENT;
            case TRANSMIT:
                STATEMENT = OUTPUT();
                ADVANCE();
                return STATEMENT;
            case RECEIVE:
                STATEMENT = INPUT();
                ADVANCE();
                STATEMENT_LOOP = STATEMENT();
                if (!STATEMENT_LOOP.root.token.getTokenName().equals(TokenName.EMPTY)) {
                    STATEMENT.root.setNextSibling(STATEMENT_LOOP.root);
                }
                return STATEMENT;
            case DURING:
                STATEMENT = DURING_STMT();
                ADVANCE();
                STATEMENT_LOOP = STATEMENT();
                if (!STATEMENT_LOOP.root.token.getTokenName().equals(TokenName.EMPTY)) {
                    STATEMENT.root.setNextSibling(STATEMENT_LOOP.root);
                }
                return STATEMENT;
            case EXECUTE:
                STATEMENT = EXECUTE_STMT();
                ADVANCE();
                STATEMENT_LOOP = STATEMENT();
                if (!STATEMENT_LOOP.root.token.getTokenName().equals(TokenName.EMPTY)) {
                    STATEMENT.root.setNextSibling(STATEMENT_LOOP.root);
                }
                return STATEMENT;
            default:
                return STATEMENT;
        }
    }

    public ParseTree CAST_STMT_PRIME() throws IOException {
        ParseTreeNode CAST_STMT_PRIME_NODE = new ParseTreeNode(new Token(TokenName.CAST_STMT_PRIME, null, null, null, null, 0), null, null); //root node
        ParseTreeNode IDENTIFIER = new ParseTreeNode();
        ParseTreeNode ASSIGNOPP = new ParseTreeNode();
        ParseTree CAST_STMT = new ParseTree();
        ParseTree CAST_STMT_PRIME = new ParseTree();
        Token ERROR;

        if (currentToken.getTokenName().equals(TokenName.IDENTIFIER)) {
            IDENTIFIER = makeNode(currentToken);
            ADVANCE();
            if (currentToken.getTokenName().equals(TokenName.ASSIGNOPP)) {
                ASSIGNOPP = makeNode(currentToken);
                ADVANCE();
                CAST_STMT = CAST_STMT();
            } else {
                ERROR = ERROR(TokenName.ASSIGNOPP, currentToken, currentToken.getLineNumber());
                return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
            }
        } else {
            ERROR = ERROR(TokenName.IDENTIFIER, currentToken, currentToken.getLineNumber());
            return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
        }

        CAST_STMT_PRIME_NODE.setFirstChild(IDENTIFIER);
        IDENTIFIER.setNextSibling(ASSIGNOPP);
        ASSIGNOPP.setNextSibling(CAST_STMT.root);
        CAST_STMT_PRIME.setRoot(CAST_STMT_PRIME_NODE);
        return CAST_STMT_PRIME;
    }

    public ParseTree CAST_STMT() throws IOException {
        ParseTreeNode CAST_STMT_NODE = new ParseTreeNode(new Token(TokenName.CAST_STMT, null, null, null, null, 0), null, null); //root node 
        ParseTree TO_INT = new ParseTree();
        ParseTree TO_STR = new ParseTree();
        ParseTreeNode EOL = new ParseTreeNode();
        Token ERROR;
        ParseTree CAST_STMT = new ParseTree();

        switch (currentToken.getTokenName()) {
            case DIGITIZE:
                TO_INT = TO_INT();
                ADVANCE();
                if (currentToken.getTokenName().equals(TokenName.EOL)) {
                    EOL = makeNode(currentToken);
                    CAST_STMT_NODE.setFirstChild(TO_INT.root);
                    TO_INT.root.setNextSibling(EOL);
                    break;
                } else {
                    ERROR = ERROR(TokenName.EOL, currentToken, currentToken.getLineNumber());
                    return new ParseTree(new ParseTreeNode(ERROR, null, null));
                }
            case WRITE:
                TO_STR = TO_STR();
                ADVANCE();
                if (currentToken.getTokenName().equals(TokenName.EOL)) {
                    EOL = makeNode(currentToken);
                    CAST_STMT_NODE.setFirstChild(TO_STR.root);
                    TO_STR.root.setNextSibling(EOL);
                    break;
                } else {
                    ERROR = ERROR(TokenName.EOL, currentToken, currentToken.getLineNumber());
                    return new ParseTree(new ParseTreeNode(ERROR, null, null));
                }
            default:
                ERROR = ERROR(TokenName.EOL, currentToken, currentToken.getLineNumber());
                return new ParseTree(new ParseTreeNode(ERROR, null, null));
        }
        CAST_STMT.setRoot(CAST_STMT_NODE);
        return CAST_STMT;
    }

    public ParseTree TO_INT() throws IOException {
        ParseTreeNode TO_INT_NODE = new ParseTreeNode(new Token(TokenName.TO_INT, null, null, null, null, 0), null, null); //root node
        ParseTreeNode DIGITIZE = new ParseTreeNode();
        ParseTreeNode LEFTPAR = new ParseTreeNode();
        ParseTreeNode RIGHTPAR = new ParseTreeNode();
        ParseTree TO_INTVAL = new ParseTree(); // first child
        ParseTree TO_INT = new ParseTree();
        Token ERROR;

        if (currentToken.getTokenName().equals(TokenName.DIGITIZE)) {
            DIGITIZE = makeNode(currentToken);
            ADVANCE();
            if (currentToken.getTokenName().equals(TokenName.LEFTPAR)) {
                LEFTPAR = makeNode(currentToken);
                ADVANCE();
                TO_INTVAL = TO_INTVAL();
                ADVANCE();
                if (currentToken.getTokenName().equals(TokenName.RIGHTPAR)) {
                    RIGHTPAR = makeNode(currentToken);

                } else {
                    ERROR = ERROR(TokenName.RIGHTPAR, currentToken, currentToken.getLineNumber());
                    return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
                }
            } else {
                ERROR = ERROR(TokenName.LEFTPAR, currentToken, currentToken.getLineNumber());
                return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
            }
        } else {
            ERROR = ERROR(TokenName.DIGITIZE, currentToken, currentToken.getLineNumber());
            return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
        }

        TO_INT_NODE.setFirstChild(DIGITIZE);
        DIGITIZE.setNextSibling(LEFTPAR);
        LEFTPAR.setNextSibling(TO_INTVAL.root);
        TO_INTVAL.root.setNextSibling(RIGHTPAR);
        TO_INT.setRoot(TO_INT_NODE);
        return TO_INT;
    }

    public ParseTree TO_INTVAL() throws IOException {
        ParseTreeNode TO_INTVAL_NODE = new ParseTreeNode(new Token(TokenName.TO_INTVAL, null, null, null, null, 0), null, null); //root node
        ParseTreeNode IDENTIFIER = new ParseTreeNode();
        ParseTreeNode CONSCHAR = new ParseTreeNode();
        ParseTreeNode CONSINT = new ParseTreeNode();
        ParseTreeNode CONSFLOAT = new ParseTreeNode();

        Token ERROR;
        ParseTree TO_INTVAL = new ParseTree();

        switch (currentToken.getTokenName()) {
            case IDENTIFIER:
                IDENTIFIER = makeNode(currentToken);
                TO_INTVAL_NODE.setFirstChild(IDENTIFIER);
                break;
            case CONSCHAR:
                CONSCHAR = makeNode(currentToken);
                TO_INTVAL_NODE.setFirstChild(CONSCHAR);
                break;
            case CONSINT:
                CONSINT = makeNode(currentToken);
                TO_INTVAL_NODE.setFirstChild(CONSINT);
                break;
            case CONSFLOAT:
                CONSFLOAT = makeNode(currentToken);
                TO_INTVAL_NODE.setFirstChild(CONSFLOAT);
                break;
            default:
                ERROR = ERROR(TokenName.EOL, currentToken, currentToken.getLineNumber());
                return new ParseTree(new ParseTreeNode(ERROR, null, null));
        }
        TO_INTVAL.setRoot(TO_INTVAL_NODE);
        return TO_INTVAL;
    }

    public ParseTree TO_STR() throws IOException {
        ParseTreeNode TO_STR_NODE = new ParseTreeNode(new Token(TokenName.TO_STR, null, null, null, null, 0), null, null); //root node
        ParseTreeNode WRITE = new ParseTreeNode();
        ParseTreeNode LEFTPAR = new ParseTreeNode();
        ParseTreeNode RIGHTPAR = new ParseTreeNode();
        ParseTree TO_STRVAL = new ParseTree(); // first child
        ParseTree TO_STR = new ParseTree();
        Token ERROR;

        if (currentToken.getTokenName().equals(TokenName.WRITE)) {
            WRITE = makeNode(currentToken);
            ADVANCE();
            if (currentToken.getTokenName().equals(TokenName.LEFTPAR)) {
                LEFTPAR = makeNode(currentToken);
                ADVANCE();
                TO_STRVAL = TO_STRVAL();
                ADVANCE();

                if (currentToken.getTokenName().equals(TokenName.RIGHTPAR)) {
                    RIGHTPAR = makeNode(currentToken);

                } else {
                    ERROR = ERROR(TokenName.RIGHTPAR, currentToken, currentToken.getLineNumber());
                    return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
                }
            } else {
                ERROR = ERROR(TokenName.LEFTPAR, currentToken, currentToken.getLineNumber());
                return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
            }
        } else {
            ERROR = ERROR(TokenName.WRITE, currentToken, currentToken.getLineNumber());
            return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
        }

        TO_STR_NODE.setFirstChild(WRITE);
        WRITE.setNextSibling(LEFTPAR);
        LEFTPAR.setNextSibling(TO_STRVAL.root);
        TO_STRVAL.root.setNextSibling(RIGHTPAR);
        TO_STR.setRoot(TO_STR_NODE);
        return TO_STR;
    }

    public ParseTree TO_STRVAL() throws IOException {
        ParseTreeNode TO_STRVAL_NODE = new ParseTreeNode(new Token(TokenName.TO_STRVAL, null, null, null, null, 0), null, null); //root node
        ParseTreeNode IDENTIFIER = new ParseTreeNode();
        ParseTreeNode CONSSTR = new ParseTreeNode();
        Token ERROR;
        ParseTree TO_STRVAL = new ParseTree();

        switch (currentToken.getTokenName()) {
            case IDENTIFIER:
                IDENTIFIER = makeNode(currentToken);
                TO_STRVAL_NODE.setFirstChild(IDENTIFIER);
                break;
            case CONSSTR:
                CONSSTR = makeNode(currentToken);
                TO_STRVAL_NODE.setFirstChild(CONSSTR);
                break;
            default:
                ERROR = ERROR(TokenName.EOL, currentToken, currentToken.getLineNumber());
                return new ParseTree(new ParseTreeNode(ERROR, null, null));
        }
        TO_STRVAL.setRoot(TO_STRVAL_NODE);
        return TO_STRVAL;
    }

    public ParseTree INC_PRE_STMT() throws IOException {
        ParseTreeNode INC_PRE_STMT_NODE = new ParseTreeNode(new Token(TokenName.INC_PRE_STMT, null, null, null, null, 0), null, null); //root node
        ParseTreeNode INC = new ParseTreeNode();
        ParseTree NUMERIC_VALUE = new ParseTree();
        ParseTree INC_PRE_STMT = new ParseTree();
        Token ERROR;

        if (currentToken.getTokenName().equals(TokenName.INC)) {
            INC = makeNode(currentToken);
            ADVANCE();
            INC_PRE_STMT = NUMERIC_VALUE();
        } else {
            ERROR = ERROR(TokenName.INC, currentToken, currentToken.getLineNumber());
            return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
        }

        INC_PRE_STMT_NODE.setFirstChild(INC);
        INC.setNextSibling(NUMERIC_VALUE.root);
        INC_PRE_STMT.setRoot(INC_PRE_STMT_NODE);
        return INC_PRE_STMT;
    }

    public ParseTree INC_POST_STMT() throws IOException {
        ParseTreeNode INC_POST_STMT_NODE = new ParseTreeNode(new Token(TokenName.INC_POST_STMT, null, null, null, null, 0), null, null); //root node
        ParseTree NUMERIC_VALUE = new ParseTree();
        ParseTreeNode INC = new ParseTreeNode();
        ParseTree INC_POST_STMT = new ParseTree();
        Token ERROR;

        INC_POST_STMT = NUMERIC_VALUE();
        ADVANCE();
        if (currentToken.getTokenName().equals(TokenName.INC)) {
            INC = makeNode(currentToken);
        } else {
            ERROR = ERROR(TokenName.INC, currentToken, currentToken.getLineNumber());
            return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
        }

        INC_POST_STMT_NODE.setFirstChild(NUMERIC_VALUE.root);
        NUMERIC_VALUE.root.setNextSibling(INC);
        return INC_POST_STMT;
    }

    public ParseTree DEC_PRE_STMT() throws IOException {
        ParseTreeNode DEC_PRE_STMT_NODE = new ParseTreeNode(new Token(TokenName.DEC_PRE_STMT, null, null, null, null, 0), null, null); //root node
        ParseTreeNode DEC = new ParseTreeNode();
        ParseTree NUMERIC_VALUE = new ParseTree();
        Token ERROR;
        ParseTree DEC_PRE_STMT = new ParseTree();

        if (currentToken.getTokenName().equals(TokenName.DEC)) {
            DEC = makeNode(currentToken);
            ADVANCE();
            DEC_PRE_STMT = NUMERIC_VALUE();
        } else {
            ERROR = ERROR(TokenName.DEC, currentToken, currentToken.getLineNumber());
            return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
        }

        DEC_PRE_STMT_NODE.setFirstChild(DEC);
        DEC.setNextSibling(NUMERIC_VALUE.root);
        DEC_PRE_STMT.setRoot(DEC_PRE_STMT_NODE);
        return DEC_PRE_STMT;
    }

    public ParseTree DEC_POST_STMT() throws IOException {
        ParseTreeNode DEC_POST_STMT_NODE = new ParseTreeNode(new Token(TokenName.DEC_POST_STMT, null, null, null, null, 0), null, null); //root node
        ParseTree NUMERIC_VALUE = new ParseTree();
        ParseTreeNode DEC = new ParseTreeNode();
        ParseTree DEC_POST_STMT = new ParseTree();
        Token ERROR;

        DEC_POST_STMT = NUMERIC_VALUE();
        ADVANCE();
        if (currentToken.getTokenName().equals(TokenName.DEC)) {
            DEC = makeNode(currentToken);
        } else {
            ERROR = ERROR(TokenName.DEC, currentToken, currentToken.getLineNumber());
            return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
        }

        DEC_POST_STMT_NODE.setFirstChild(NUMERIC_VALUE.root);
        NUMERIC_VALUE.root.setNextSibling(DEC);
        return DEC_POST_STMT;
    }

    public ParseTree IF_STMT_PRIME() throws IOException {
        ParseTreeNode IF_STMT_PRIME_NODE = new ParseTreeNode(new Token(TokenName.IF_STMT_PRIME, null, null, null, null, 0), null, null); //root node
        ParseTree PROG_BODY_LOOP = new ParseTree(new ParseTreeNode(new Token(TokenName.EMPTY, null, null, null, null, 0), null, null));
        ParseTreeNode IF = new ParseTreeNode();
        ParseTreeNode LEFTPAR = new ParseTreeNode();
        ParseTreeNode LEFTBRACE = new ParseTreeNode();
        ParseTreeNode RIGHTPAR = new ParseTreeNode();
        ParseTreeNode RIGHTBRACE = new ParseTreeNode();
        ParseTree RegExp = new ParseTree();
        ParseTree IF_STMT_PRIME = new ParseTree();
        ParseTree IF_STMT = new ParseTree();
        Token ERROR;

        if (currentToken.getTokenName().equals(TokenName.IF)) {
            IF = makeNode(currentToken);
            ADVANCE();
            
            if (currentToken.getTokenName().equals(TokenName.LEFTPAR)) {
                LEFTPAR = makeNode(currentToken);
                ADVANCE();
                
                // Build/Create a grammar and code for Reg Exp
                RegExp = RELATIONAL_EXPRESSION();
                // ADVANCE();
                if (overReadToken.getTokenName().equals(TokenName.RIGHTPAR)) {
                    RIGHTPAR = makeNode(overReadToken);
                    //ADVANCE();
                    if (currentToken.getTokenName().equals(TokenName.LEFTBRACE)) {
                        LEFTBRACE = makeNode(currentToken);
                        ADVANCE();
                        PROG_BODY_LOOP = PROG_BODY();
                        if (!PROG_BODY_LOOP.root.token.getTokenName().equals(TokenName.EMPTY)) {
                            LEFTBRACE.setNextSibling(PROG_BODY_LOOP.root);
                        }
                        if (currentToken.getTokenName().equals(TokenName.RIGHTBRACE)) {
                            RIGHTBRACE = makeNode(currentToken);
                            ADVANCE();
                            if (!PROG_BODY_LOOP.root.token.getTokenName().equals(TokenName.EMPTY)) {
                                PROG_BODY_LOOP.root.setNextSibling(RIGHTBRACE);
                            } else {
                                LEFTBRACE.setNextSibling(RIGHTBRACE);
                            }
                            IF_STMT = IF_STMT();
                        } else {
                            ERROR = ERROR(TokenName.RIGHTBRACE, currentToken, currentToken.getLineNumber());
                            return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
                        }
                    } else {
                        ERROR = ERROR(TokenName.LEFTBRACE, currentToken, currentToken.getLineNumber());
                        return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
                    }
                } else {
                    ERROR = ERROR(TokenName.RIGHTPAR, currentToken, currentToken.getLineNumber());
                    return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
                }
            } else {
                ERROR = ERROR(TokenName.LEFTPAR, currentToken, currentToken.getLineNumber());
                return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
            }
        } else {
            ERROR = ERROR(TokenName.IF, currentToken, currentToken.getLineNumber());
            return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
        }

        IF_STMT_PRIME_NODE.setFirstChild(IF);
        IF.setNextSibling(LEFTPAR);
        //Insert if there's a RegExp
        LEFTPAR.setNextSibling(RegExp.root);
        RegExp.root.setNextSibling(RIGHTPAR);
        //LEFTPAR.setNextSibling(RIGHTPAR); // remove this if there's RegExp
        RIGHTPAR.setNextSibling(LEFTBRACE);
        RIGHTBRACE.setNextSibling(IF_STMT.root);
        IF_STMT_PRIME.setRoot(IF_STMT_PRIME_NODE);
        return IF_STMT_PRIME;
    }

    public ParseTree IF_STMT() throws IOException {
        ParseTree ELSEIF_STMT = new ParseTree();
        ParseTree ELSE_STMT = new ParseTree();
        ParseTree ENDIF_STMT = new ParseTree(new ParseTreeNode(new Token(TokenName.END_IF_STMT, null, null, null, null, 0), null, null));
        ParseTreeNode ENDIF = new ParseTreeNode();
        ParseTree Empty = new ParseTree();
        Token ERROR;

        switch (currentToken.getTokenName()) {
            case ELSEIF:
                ELSEIF_STMT = ELSEIF_STMT();
                return ELSEIF_STMT;
            case ELSE:
                ELSE_STMT = ELSE_STMT();
                return ELSE_STMT;
            case ENDIF:
                ENDIF = makeNode(currentToken);
                ENDIF_STMT.root.setFirstChild(ENDIF);
                return ENDIF_STMT;
            default:
                ERROR = ERROR(TokenName.ENDIF, currentToken, currentToken.getLineNumber());
                return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));

        }
    }

    public ParseTree ELSEIF_STMT() throws IOException {
        ParseTreeNode ELSEIF_STMT_NODE = new ParseTreeNode(new Token(TokenName.ELSEIF_STMT, null, null, null, null, 0), null, null); //root node
        ParseTree PROG_BODY_LOOP = new ParseTree(new ParseTreeNode(new Token(TokenName.EMPTY, null, null, null, null, 0), null, null));
        ParseTree ELSEIF_STMT_LOOP = new ParseTree(new ParseTreeNode(new Token(TokenName.EMPTY, null, null, null, null, 0), null, null));
        ParseTreeNode ELSEIF = new ParseTreeNode();
        ParseTreeNode LEFTPAR = new ParseTreeNode();
        ParseTreeNode RIGHTPAR = new ParseTreeNode();
        ParseTreeNode LEFTBRACE = new ParseTreeNode();
        ParseTreeNode RIGHTBRACE = new ParseTreeNode();
        ParseTree ELSE_STMT = new ParseTree();
        ParseTree ELSEIF_STMT = new ParseTree();
        ParseTree RegExp = new ParseTree();
        Token ERROR;

        if (currentToken.getTokenName().equals(TokenName.ELSEIF)) {
            ELSEIF = makeNode(currentToken);
            ADVANCE();
            if (currentToken.getTokenName().equals(TokenName.LEFTPAR)) {
                LEFTPAR = makeNode(currentToken);
                ADVANCE();
                // Build/Create a grammar and code for Reg Exp
                // RegExp = REXP();
                //ADVANCE();
                RegExp = RELATIONAL_EXPRESSION();
                if (overReadToken.getTokenName().equals(TokenName.RIGHTPAR)) {
                    RIGHTPAR = makeNode(overReadToken);
                    //ADVANCE();
                    if (currentToken.getTokenName().equals(TokenName.LEFTBRACE)) {
                        LEFTBRACE = makeNode(currentToken);
                        ADVANCE();
                        PROG_BODY_LOOP = PROG_BODY();
                        if (!PROG_BODY_LOOP.root.token.getTokenName().equals(TokenName.EMPTY)) {
                            LEFTBRACE.setNextSibling(PROG_BODY_LOOP.root);
                            PROG_BODY_LOOP.root.setNextSibling(RIGHTBRACE);
                        }
                        if (currentToken.getTokenName().equals(TokenName.RIGHTBRACE)) {
                            RIGHTBRACE = makeNode(currentToken);
                            ADVANCE();
                            if (!PROG_BODY_LOOP.root.token.getTokenName().equals(TokenName.EMPTY)) {
                                PROG_BODY_LOOP.root.setNextSibling(RIGHTBRACE);
                            } else {

                                LEFTBRACE.setNextSibling(RIGHTBRACE);
                            }

                            if (currentToken.getTokenName().equals(TokenName.ELSEIF)) {
                                ELSEIF_STMT_LOOP = ELSEIF_STMT();
                                RIGHTBRACE.setNextSibling(ELSEIF_STMT_LOOP.root);
                            } else {
                                if (!ELSEIF_STMT_LOOP.root.token.getTokenName().equals(TokenName.EMPTY)) {
                                    RIGHTBRACE.setNextSibling(ELSEIF_STMT_LOOP.root);
                                } else {
                                    ELSE_STMT = ELSE_STMT();
                                    RIGHTBRACE.setNextSibling(ELSE_STMT.root);
                                }
                            }

                        } else {
                            ERROR = ERROR(TokenName.RIGHTBRACE, currentToken, currentToken.getLineNumber());
                            return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
                        }
                    } else {
                        ERROR = ERROR(TokenName.LEFTBRACE, currentToken, currentToken.getLineNumber());
                        return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
                    }

                } else {
                    ERROR = ERROR(TokenName.RIGHTPAR, currentToken, currentToken.getLineNumber());
                    return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
                }
            } else {
                ERROR = ERROR(TokenName.LEFTPAR, currentToken, currentToken.getLineNumber());
                return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
            }
        } else if (currentToken.getTokenName().equals(TokenName.ELSE)) {
            ELSE_STMT = ELSE_STMT();
            ADVANCE();
        } else {
            ERROR = ERROR(TokenName.ELSEIF_OR_ELSE, currentToken, currentToken.getLineNumber());
            return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
        }

        ELSEIF_STMT_NODE.setFirstChild(ELSEIF);
        ELSEIF.setNextSibling(LEFTPAR);
        //Insert if there's a RegExp
        LEFTPAR.setNextSibling(RegExp.root);
        RegExp.root.setNextSibling(RIGHTPAR);
        //LEFTPAR.setNextSibling(RIGHTPAR); // remove this if there's RegExp
        RIGHTPAR.setNextSibling(LEFTBRACE);
        ELSEIF_STMT.setRoot(ELSEIF_STMT_NODE);
        return ELSEIF_STMT;
    }

    public ParseTree ELSE_STMT() throws IOException {
        ParseTreeNode ELSE_STMT_NODE = new ParseTreeNode(new Token(TokenName.ELSE_STMT, null, null, null, null, 0), null, null); //root node
        ParseTree PROG_BODY_LOOP = new ParseTree(new ParseTreeNode(new Token(TokenName.EMPTY, null, null, null, null, 0), null, null));
        ParseTreeNode ELSE = new ParseTreeNode();
        ParseTreeNode LEFTBRACE = new ParseTreeNode();
        ParseTreeNode RIGHTBRACE = new ParseTreeNode();
        ParseTreeNode ENDIF = new ParseTreeNode();
        ParseTree ELSE_STMT = new ParseTree();
        Token ERROR;

        if (currentToken.getTokenName().equals(TokenName.ELSE)) {
            ELSE = makeNode(currentToken);
            ADVANCE();
            if (currentToken.getTokenName().equals(TokenName.LEFTBRACE)) {
                LEFTBRACE = makeNode(currentToken);
                ADVANCE();
                PROG_BODY_LOOP = PROG_BODY();
                if (!PROG_BODY_LOOP.root.token.getTokenName().equals(TokenName.EMPTY)) {
                    LEFTBRACE.setNextSibling(PROG_BODY_LOOP.root);
                    PROG_BODY_LOOP.root.setNextSibling(RIGHTBRACE);
                }

                if (currentToken.getTokenName().equals(TokenName.RIGHTBRACE)) {
                    RIGHTBRACE = makeNode(currentToken);
                    ADVANCE();
                    if (!PROG_BODY_LOOP.root.token.getTokenName().equals(TokenName.EMPTY)) {
                        PROG_BODY_LOOP.root.setNextSibling(RIGHTBRACE);
                    } else {
                        LEFTBRACE.setNextSibling(RIGHTBRACE);
                    }
                    if (currentToken.getTokenName().equals(TokenName.ENDIF)) {
                        ENDIF = makeNode(currentToken);
                    } else {
                        ERROR = ERROR(TokenName.ENDIF, currentToken, currentToken.getLineNumber());
                        return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
                    }
                } else {
                    ERROR = ERROR(TokenName.RIGHTBRACE, currentToken, currentToken.getLineNumber());
                    return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
                }
            } else {
                ERROR = ERROR(TokenName.LEFTBRACE, currentToken, currentToken.getLineNumber());
                return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
            }
        } else {
            ERROR = ERROR(TokenName.ELSE, currentToken, currentToken.getLineNumber());
            return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
        }

        ELSE_STMT_NODE.setFirstChild(ELSE);
        ELSE.setNextSibling(LEFTBRACE);
        RIGHTBRACE.setNextSibling(ENDIF);
        ELSE_STMT.setRoot(ELSE_STMT_NODE);
        return ELSE_STMT;
    }

    public ParseTree DURING_STMT() throws IOException {
        ParseTreeNode DURING_STMT_NODE = new ParseTreeNode(new Token(TokenName.DURING_STMT, null, null, null, null, 0), null, null); //root node
        ParseTree PROG_BODY_LOOP = new ParseTree(new ParseTreeNode(new Token(TokenName.EMPTY, null, null, null, null, 0), null, null));
        ParseTreeNode DURING = new ParseTreeNode();
        ParseTreeNode LEFTPAR = new ParseTreeNode();
        ParseTreeNode RIGHTPAR = new ParseTreeNode();
        ParseTreeNode LEFTBRACE = new ParseTreeNode();
        ParseTreeNode RIGHTBRACE = new ParseTreeNode();
        ParseTree RegExp = new ParseTree();
        ParseTree DURING_STMT = new ParseTree();
        Token ERROR;

        if (currentToken.getTokenName().equals(TokenName.DURING)) {
            DURING = makeNode(currentToken);
            ADVANCE();
            if (currentToken.getTokenName().equals(TokenName.LEFTPAR)) {
                LEFTPAR = makeNode(currentToken);
                ADVANCE();
                // Build/Create a grammar and code for Reg Exp
                RegExp = RELATIONAL_EXPRESSION();
                // ADVANCE();
                if (overReadToken.getTokenName().equals(TokenName.RIGHTPAR)) {
                    RIGHTPAR = makeNode(overReadToken);
                    //ADVANCE();
                    if (currentToken.getTokenName().equals(TokenName.LEFTBRACE)) {
                        LEFTBRACE = makeNode(currentToken);
                        ADVANCE();
                        PROG_BODY_LOOP = PROG_BODY();
                        if (!PROG_BODY_LOOP.root.token.getTokenName().equals(TokenName.EMPTY)) {
                            LEFTBRACE.setNextSibling(PROG_BODY_LOOP.root);
                        }
                        if (currentToken.getTokenName().equals(TokenName.RIGHTBRACE)) {
                            RIGHTBRACE = makeNode(currentToken);
                            if (!PROG_BODY_LOOP.root.token.getTokenName().equals(TokenName.EMPTY)) {
                                PROG_BODY_LOOP.root.setNextSibling(RIGHTBRACE);

                            } else {
                                LEFTBRACE.setNextSibling(RIGHTBRACE);
                            }
                        } else {
                            ERROR = ERROR(TokenName.RIGHTBRACE, currentToken, currentToken.getLineNumber());
                            return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
                        }
                    } else {
                        ERROR = ERROR(TokenName.LEFTBRACE, currentToken, currentToken.getLineNumber());
                        return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
                    }
                } else {
                    ERROR = ERROR(TokenName.RIGHTPAR, currentToken, currentToken.getLineNumber());
                    return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
                }
            } else {
                ERROR = ERROR(TokenName.LEFTPAR, currentToken, currentToken.getLineNumber());
                return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
            }
        } else {
            ERROR = ERROR(TokenName.DURING, currentToken, currentToken.getLineNumber());
            return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
        }

        DURING_STMT_NODE.setFirstChild(DURING);
        DURING.setNextSibling(LEFTPAR);
        //Insert if there's a RegExp
        //LEFTPAR.setNextSibling(RegExp.root);
        //RegExp.root.setNextSibling(RIGHTPAR);
        LEFTPAR.setNextSibling(RIGHTPAR); // remove this if there's RegExp
        RIGHTPAR.setNextSibling(LEFTBRACE);
        DURING_STMT.setRoot(DURING_STMT_NODE);
        return DURING_STMT;
    }

    public ParseTree EXECUTE_STMT() throws IOException {
        ParseTreeNode EXECUTE_STMT_NODE = new ParseTreeNode(new Token(TokenName.EXECUTE_STMT, null, null, null, null, 0), null, null);
        ParseTree PROG_BODY_LOOP = new ParseTree(new ParseTreeNode(new Token(TokenName.EMPTY, null, null, null, null, 0), null, null));
        ParseTreeNode EXECUTE = new ParseTreeNode();
        ParseTreeNode LEFTBRACE = new ParseTreeNode();
        ParseTreeNode RIGHTBRACE = new ParseTreeNode();
        ParseTreeNode DURING = new ParseTreeNode();
        ParseTreeNode LEFTPAR = new ParseTreeNode();
        ParseTreeNode RIGHTPAR = new ParseTreeNode();
        ParseTreeNode EOL = new ParseTreeNode();
        ParseTree RegExp = new ParseTree();
        ParseTree EXECUTE_STMT = new ParseTree();
        Token ERROR;

        if (currentToken.getTokenName().equals(TokenName.EXECUTE)) {
            EXECUTE = makeNode(currentToken);
            ADVANCE();
            if (currentToken.getTokenName().equals(TokenName.LEFTBRACE)) {
                LEFTBRACE = makeNode(currentToken);
                ADVANCE();
                PROG_BODY_LOOP = PROG_BODY();
                if (!PROG_BODY_LOOP.root.token.getTokenName().equals(TokenName.EMPTY)) {
                    LEFTBRACE.setNextSibling(PROG_BODY_LOOP.root);
                }
                if (currentToken.getTokenName().equals(TokenName.RIGHTBRACE)) {
                    RIGHTBRACE = makeNode(currentToken);
                    if (!PROG_BODY_LOOP.root.token.getTokenName().equals(TokenName.EMPTY)) {
                        PROG_BODY_LOOP.root.setNextSibling(RIGHTBRACE);

                    } else {
                        LEFTBRACE.setNextSibling(RIGHTBRACE);
                    }
                    ADVANCE();
                    if (currentToken.getTokenName().equals(TokenName.DURING)) {
                        DURING = makeNode(currentToken);
                        ADVANCE();
                        if (currentToken.getTokenName().equals(TokenName.LEFTPAR)) {
                            LEFTPAR = makeNode(currentToken);
                            ADVANCE();
                            // Build/Create a grammar and code for Reg Exp
                            RegExp = RELATIONAL_EXPRESSION();
                            // ADVANCE();
                            if (overReadToken.getTokenName().equals(TokenName.RIGHTPAR)) {
                                RIGHTPAR = makeNode(overReadToken);
                                if (currentToken.getTokenName().equals(TokenName.EOL)) {
                                    EOL = makeNode(currentToken);
                                } else {
                                    ERROR = ERROR(TokenName.RIGHTPAR, currentToken, currentToken.getLineNumber());
                                    return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
                                }
                            } else {
                                ERROR = ERROR(TokenName.LEFTPAR, currentToken, currentToken.getLineNumber());
                                return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
                            }

                        } else {
                            ERROR = ERROR(TokenName.DURING, currentToken, currentToken.getLineNumber());
                            return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
                        }
                    }
                } else {
                    ERROR = ERROR(TokenName.RIGHTBRACE, currentToken, currentToken.getLineNumber());
                    return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
                }
            } else {
                ERROR = ERROR(TokenName.LEFTBRACE, currentToken, currentToken.getLineNumber());
                return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
            }
        } else {
            ERROR = ERROR(TokenName.EXECUTE, currentToken, currentToken.getLineNumber());
            return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
        }

        EXECUTE_STMT_NODE.setFirstChild(EXECUTE);
        EXECUTE.setNextSibling(LEFTBRACE);
        DURING.setNextSibling(LEFTPAR);
        RIGHTBRACE.setNextSibling(DURING);
        //Insert if there's a RegExp
        LEFTPAR.setNextSibling(RegExp.root);
        RegExp.root.setNextSibling(RIGHTPAR);
        RIGHTPAR.setNextSibling(EOL);
        //LEFTPAR.setNextSibling(RIGHTPAR); // remove this if there's RegExp
        EXECUTE_STMT.setRoot(EXECUTE_STMT_NODE);
        return EXECUTE_STMT;
    }

    public ParseTree OUTPUT() throws IOException {
        ParseTreeNode OUTPUT_NODE = new ParseTreeNode(new Token(TokenName.OUTPUT, null, null, null, null, 0), null, null); //root node
        ParseTreeNode TRANSMIT = new ParseTreeNode();
        ParseTreeNode LEFTPAR = new ParseTreeNode();
        ParseTree EXPRESSION = new ParseTree();
        ParseTreeNode RIGHTPAR = new ParseTreeNode();
        ParseTreeNode EOL = new ParseTreeNode();
        ParseTree OUTPUT = new ParseTree();
        Token ERROR;
        boolean loopBreak = true;
        DataType type = DataType.NULL;

        if (currentToken.getTokenName().equals(TokenName.TRANSMIT)) {
            TRANSMIT = makeNode(currentToken);
            ADVANCE();
            if (currentToken.getTokenName().equals(TokenName.LEFTPAR)) {
                LEFTPAR = makeNode(currentToken);
                ADVANCE();
                while (loopBreak) {
                    switch (currentToken.getTokenName()) {
                        case LOGICNOT:
                        case AFFIRM:
                        case NEGATE:
                            EXPRESSION = LOGICAL_EXPRESSION();
                            loopBreak = false;
                            break;
                        case DIFF:
                        case CONSINT:
                        case CONSFLOAT:
                            EXPRESSION = ARITHMETIC_EXPRESSION();
                            loopBreak = false;
                            break;
                        case LEFTPAR:
                            unmatchedLeftPar++;
                            ADVANCE();
                            break;
                        case IDENTIFIER:
                            try {
                                type = lex.SymbolTable.get(currentToken.getLexeme()).getDataType();
                            } catch (Exception e) {
                                ERROR = ERROR(TokenName.IDENTIFIER_NOT_DECLARED, currentToken, currentToken.getLineNumber());
                                System.exit(0);
                            }
                            switch (type) {
                                case INTEGER:
                                case FLOATING_POINT:
                                    EXPRESSION = ARITHMETIC_EXPRESSION();
                                    loopBreak = false;
                                    break;
                                case BOOLEAN:
                                    EXPRESSION = LOGICAL_EXPRESSION();
                                    loopBreak = false;
                                    break;
                                default:
                                    break;
                            }
                            break;
                    }
                }
                //EXPRESSION = ARITHMETIC_EXPRESSION();
                if (overReadToken.getTokenName().equals(TokenName.RIGHTPAR)) {
                    RIGHTPAR = makeNode(overReadToken);
                    if (currentToken.getTokenName().equals(TokenName.EOL)) {
                        EOL = makeNode(currentToken);
                    } else {
                        ERROR = ERROR(TokenName.EOL, currentToken, currentToken.getLineNumber());
                        return new ParseTree(new ParseTreeNode(ERROR, null, null));
                    }
                } else {
                    ERROR = ERROR(TokenName.RIGHTPAR, currentToken, currentToken.getLineNumber());
                    return new ParseTree(new ParseTreeNode(ERROR, null, null));
                }
            } else {
                ERROR = ERROR(TokenName.LEFTPAR, currentToken, currentToken.getLineNumber());
                return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
            }
        } else {
            ERROR = ERROR(TokenName.TRANSMIT, currentToken, currentToken.getLineNumber());
            return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
        }

        OUTPUT_NODE.setFirstChild(TRANSMIT);
        TRANSMIT.setNextSibling(LEFTPAR);
        LEFTPAR.setNextSibling(EXPRESSION.root);
        EXPRESSION.root.setNextSibling(RIGHTPAR);
        RIGHTPAR.setNextSibling(EOL);
        OUTPUT.setRoot(OUTPUT_NODE);
        return OUTPUT;
    }

    public ParseTree INPUT() throws IOException {
        ParseTreeNode INPUT_NODE = new ParseTreeNode(new Token(TokenName.INPUT, null, null, null, null, 0), null, null); //root node
        ParseTreeNode RECEIVE = new ParseTreeNode();
        ParseTreeNode IDENTIFIER = new ParseTreeNode();
        ParseTreeNode EOL = new ParseTreeNode();
        ParseTree EXPRESSION = new ParseTree();
        ParseTree INPUT = new ParseTree();
        Token ERROR;

        if (currentToken.getTokenName().equals(TokenName.RECEIVE)) {
            RECEIVE = makeNode(currentToken);
            ADVANCE();
            if (currentToken.getTokenName().equals(TokenName.IDENTIFIER)) {
                IDENTIFIER = makeNode(currentToken);
                ADVANCE();
                if (currentToken.getTokenName().equals(TokenName.EOL)) {
                    EOL = makeNode(currentToken);
                } else {
                    ERROR = ERROR(TokenName.EOL, currentToken, currentToken.getLineNumber());
                    return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
                }
            } else {
                ERROR = ERROR(TokenName.IDENTIFIER, currentToken, currentToken.getLineNumber());
                return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
            }
        } else if (currentToken.getTokenName().equals(TokenName.EOL)) {
            EOL = makeNode(currentToken);
        } else {
            ERROR = ERROR(TokenName.RECEIVE, currentToken, currentToken.getLineNumber());
            return new ParseTree(new ParseTreeNode(new Token(TokenName.ERROR, null, null, null, null, 0), null, null));
        }

        INPUT_NODE.setFirstChild(RECEIVE);
        RECEIVE.setNextSibling(IDENTIFIER);
        IDENTIFIER.setNextSibling(EOL);
        INPUT.setRoot(INPUT_NODE);
        return INPUT;
    }

    public ParseTree ARITHMETIC_EXPRESSION() throws IOException {
        LinkedList<ParseTreeNode> expressionInput = new LinkedList<>();
        LinkedList<ParseTreeNode> exInput = new LinkedList<>();
        Stack<ParseTreeNode> expressionStack = new Stack<>();
        Stack<ParseTreeNode> operandStack = new Stack<>();
        Stack<ParseTreeNode> exStack = new Stack<>();
        Stack<ParseTreeNode> opStack = new Stack<>();

        OperatorPrecedenceTable precedenceTable = new OperatorPrecedenceTable();

        ParseTreeNode operatorHolder = new ParseTreeNode();
        ParseTreeNode holder = new ParseTreeNode();
        ParseTreeNode firstOperand = new ParseTreeNode();
        ParseTreeNode secondOperand = new ParseTreeNode();
        ParseTreeNode EXPRESSION_NODE = new ParseTreeNode(new Token(TokenName.ARITHMETIC_EXPRESSION, null, null, null, null, 0), null, null);
        ParseTreeNode NEGEXP_NODE = new ParseTreeNode(new Token(TokenName.NEGEXP_NODE, null, null, null, null, 0), null, null);
        ParseTreeNode NEGEXP_ROOT = new ParseTreeNode(new Token(TokenName.NEGEXP_ROOT, null, null, null, null, 0), null, null);

        ParseTree EXPRESSION = new ParseTree();
        ParseTree NEGEXP = new ParseTree();

        Token ERROR;
        int leftPar = 0;
        int rightPar = 0;

        while (unmatchedLeftPar != 0) {
            expressionInput.offerLast(makeNode(new Token(TokenName.LEFTPAR, null, null, null, null, 0)));
            unmatchedLeftPar--;
        }

        if (currentToken.getTokenName().equals(TokenName.DIFF)) {
            firstOperand = makeNode(currentToken);
            operatorHolder = firstOperand;
            ADVANCE();
            if (currentToken.getTokenName().equals(TokenName.IDENTIFIER) || currentToken.getTokenName().equals(TokenName.CONSINT) || currentToken.getTokenName().equals(TokenName.CONSFLOAT)) {
                secondOperand = makeNode(currentToken);
                expressionInput.offerLast(makeNode(new Token(TokenName.LEFTPAR, null, null, null, null, 0)));
                if (secondOperand.getToken().getTokenName().equals(TokenName.CONSINT)) {
                    expressionInput.offerLast(new ParseTreeNode(new Token(TokenName.NEGINT, null, null, null, null, 0), firstOperand, secondOperand));
                } else if (secondOperand.getToken().getTokenName().equals(TokenName.CONSFLOAT)) {
                    expressionInput.offerLast(new ParseTreeNode(new Token(TokenName.NEGFLOAT, null, null, null, null, 0), firstOperand, secondOperand));
                } else if (secondOperand.getToken().getTokenName().equals(TokenName.IDENTIFIER)) {
                    expressionInput.offerLast(new ParseTreeNode(new Token(TokenName.NEGIDENTIFIER, null, null, null, null, 0), firstOperand, secondOperand));
                }
                expressionInput.offerLast(makeNode(new Token(TokenName.RIGHTPAR, null, null, null, null, 0)));
                ADVANCE();
            } else if (currentToken.getTokenName().equals(TokenName.LEFTPAR)) {
                leftPar++;
                exInput.offerLast(makeNode(currentToken));
                ADVANCE();
                while (leftPar != rightPar) {
                    if (currentToken.getTokenName().equals(TokenName.DIFF)) {
                        firstOperand = makeNode(currentToken);
                        ADVANCE();
                        if (currentToken.getTokenName().equals(TokenName.IDENTIFIER) || currentToken.getTokenName().equals(TokenName.CONSINT) || currentToken.getTokenName().equals(TokenName.CONSFLOAT)) {
                            secondOperand = makeNode(currentToken);
                            exInput.offerLast(makeNode(new Token(TokenName.LEFTPAR, null, null, null, null, 0)));
                            if (secondOperand.getToken().getTokenName().equals(TokenName.CONSINT)) {
                                exInput.offerLast(new ParseTreeNode(new Token(TokenName.NEGINT, null, null, null, null, 0), firstOperand, secondOperand));
                            } else if (secondOperand.getToken().getTokenName().equals(TokenName.CONSFLOAT)) {
                                exInput.offerLast(new ParseTreeNode(new Token(TokenName.NEGFLOAT, null, null, null, null, 0), firstOperand, secondOperand));
                            } else if (secondOperand.getToken().getTokenName().equals(TokenName.IDENTIFIER)) {
                                exInput.offerLast(new ParseTreeNode(new Token(TokenName.NEGIDENTIFIER, null, null, null, null, 0), firstOperand, secondOperand));
                            }
                            exInput.offerLast(makeNode(new Token(TokenName.RIGHTPAR, null, null, null, null, 0)));
                            ADVANCE();
                        }
                    } else if (currentToken.getTokenName().equals(TokenName.LEFTPAR)) {
                        leftPar++;
                        holder = makeNode(currentToken);
                        ADVANCE();
                        if (currentToken.getTokenName().equals(TokenName.DIFF)) {
                            exInput.offerLast(holder);
                            firstOperand = makeNode(currentToken);
                            ADVANCE();
                            if (currentToken.getTokenName().equals(TokenName.IDENTIFIER) || currentToken.getTokenName().equals(TokenName.CONSINT) || currentToken.getTokenName().equals(TokenName.CONSFLOAT)) {
                                secondOperand = makeNode(currentToken);
                                if (secondOperand.getToken().getTokenName().equals(TokenName.CONSINT)) {
                                    exInput.offerLast(new ParseTreeNode(new Token(TokenName.NEGINT, null, null, null, null, 0), firstOperand, secondOperand));
                                } else if (secondOperand.getToken().getTokenName().equals(TokenName.CONSFLOAT)) {
                                    exInput.offerLast(new ParseTreeNode(new Token(TokenName.NEGFLOAT, null, null, null, null, 0), firstOperand, secondOperand));
                                } else if (secondOperand.getToken().getTokenName().equals(TokenName.IDENTIFIER)) {
                                    exInput.offerLast(new ParseTreeNode(new Token(TokenName.NEGIDENTIFIER, null, null, null, null, 0), firstOperand, secondOperand));
                                }
                                ADVANCE();
                                if (currentToken.getTokenName().equals(TokenName.RIGHTPAR)) {
                                    rightPar++;
                                    exInput.offerLast(makeNode(currentToken));
                                    ADVANCE();
                                }
                            }
                        } else {
                            exInput.offerLast(holder);
                            exInput.offerLast(makeNode(currentToken));
                            ADVANCE();
                        }
                    } else if (currentToken.getTokenType().equals(TokenType.ARITHMETICOP)) {
                        holder = makeNode(currentToken);
                        ADVANCE();
                        if (currentToken.getTokenName().equals(TokenName.DIFF)) {
                            exInput.offerLast(holder);
                            firstOperand = makeNode(currentToken);
                            ADVANCE();
                            if (currentToken.getTokenName().equals(TokenName.IDENTIFIER) || currentToken.getTokenName().equals(TokenName.CONSINT) || currentToken.getTokenName().equals(TokenName.CONSFLOAT)) {
                                secondOperand = makeNode(currentToken);
                                exInput.offerLast(makeNode(new Token(TokenName.LEFTPAR, null, null, null, null, 0)));
                                if (secondOperand.getToken().getTokenName().equals(TokenName.CONSINT)) {
                                    exInput.offerLast(new ParseTreeNode(new Token(TokenName.NEGINT, null, null, null, null, 0), firstOperand, secondOperand));
                                } else if (secondOperand.getToken().getTokenName().equals(TokenName.CONSFLOAT)) {
                                    exInput.offerLast(new ParseTreeNode(new Token(TokenName.NEGFLOAT, null, null, null, null, 0), firstOperand, secondOperand));
                                } else if (secondOperand.getToken().getTokenName().equals(TokenName.IDENTIFIER)) {
                                    exInput.offerLast(new ParseTreeNode(new Token(TokenName.NEGIDENTIFIER, null, null, null, null, 0), firstOperand, secondOperand));
                                }
                                exInput.offerLast(makeNode(new Token(TokenName.RIGHTPAR, null, null, null, null, 0)));
                                ADVANCE();
                            }
                        } else if (currentToken.getTokenName().equals(TokenName.LEFTPAR)) {
                            leftPar++;
                            exInput.offerLast(holder);
                            holder = makeNode(currentToken);
                            ADVANCE();
                            if (currentToken.getTokenName().equals(TokenName.DIFF)) {
                                exInput.offerLast(holder);
                                firstOperand = makeNode(currentToken);
                                ADVANCE();
                                if (currentToken.getTokenName().equals(TokenName.IDENTIFIER) || currentToken.getTokenName().equals(TokenName.CONSINT) || currentToken.getTokenName().equals(TokenName.CONSFLOAT)) {
                                    secondOperand = makeNode(currentToken);
                                    if (secondOperand.getToken().getTokenName().equals(TokenName.CONSINT)) {
                                        exInput.offerLast(new ParseTreeNode(new Token(TokenName.NEGINT, null, null, null, null, 0), firstOperand, secondOperand));
                                    } else if (secondOperand.getToken().getTokenName().equals(TokenName.CONSFLOAT)) {
                                        exInput.offerLast(new ParseTreeNode(new Token(TokenName.NEGFLOAT, null, null, null, null, 0), firstOperand, secondOperand));
                                    } else if (secondOperand.getToken().getTokenName().equals(TokenName.IDENTIFIER)) {
                                        exInput.offerLast(new ParseTreeNode(new Token(TokenName.NEGIDENTIFIER, null, null, null, null, 0), firstOperand, secondOperand));
                                    }
                                    ADVANCE();
                                    if (currentToken.getTokenName().equals(TokenName.RIGHTPAR)) {
                                        rightPar++;
                                        exInput.offerLast(makeNode(currentToken));
                                        ADVANCE();
                                    }
                                }
                            } else {
                                exInput.offerLast(holder);
                                exInput.offerLast(makeNode(currentToken));
                                ADVANCE();
                            }
                        } else {
                            exInput.offerLast(holder);
                            exInput.offerLast(makeNode(currentToken));
                            ADVANCE();
                        }
                    } else if (currentToken.getTokenName().equals(TokenName.INC) || currentToken.getTokenName().equals(TokenName.DEC)) {
                        firstOperand = makeNode(currentToken);
                        ADVANCE();
                        if (currentToken.getTokenName().equals(TokenName.IDENTIFIER)) {
                            secondOperand = makeNode(currentToken);
                            exInput.offerLast(makeNode(new Token(TokenName.LEFTPAR, null, null, null, null, 0)));
                            if (firstOperand.getToken().getTokenName().equals(TokenName.INC)) {
                                exInput.offerLast(new ParseTreeNode(new Token(TokenName.PREINC, null, null, null, null, 0), firstOperand, secondOperand));
                            } else if (firstOperand.getToken().getTokenName().equals(TokenName.DEC)) {
                                exInput.offerLast(new ParseTreeNode(new Token(TokenName.PREDEC, null, null, null, null, 0), firstOperand, secondOperand));
                            }
                            exInput.offerLast(makeNode(new Token(TokenName.RIGHTPAR, null, null, null, null, 0)));
                            ADVANCE();
                        } else {
                            ERROR = ERROR(TokenName.IDENTIFIER, currentToken, currentToken.getLineNumber());
                            return new ParseTree(new ParseTreeNode(ERROR, null, null));
                        }
                    } else if (currentToken.getTokenName().equals(TokenName.IDENTIFIER)) {
                        firstOperand = makeNode(currentToken);
                        ADVANCE();
                        if (currentToken.getTokenName().equals(TokenName.INC) || currentToken.getTokenName().equals(TokenName.DEC)) {
                            secondOperand = makeNode(currentToken);
                            exInput.offerLast(makeNode(new Token(TokenName.LEFTPAR, null, null, null, null, 0)));
                            if (secondOperand.getToken().getTokenName().equals(TokenName.INC)) {
                                exInput.offerLast(new ParseTreeNode(new Token(TokenName.POSTINC, null, null, null, null, 0), firstOperand, secondOperand));
                            } else if (secondOperand.getToken().getTokenName().equals(TokenName.DEC)) {
                                exInput.offerLast(new ParseTreeNode(new Token(TokenName.POSTDEC, null, null, null, null, 0), firstOperand, secondOperand));
                            }
                            exInput.offerLast(makeNode(new Token(TokenName.RIGHTPAR, null, null, null, null, 0)));
                            ADVANCE();
                        } else {
                            exInput.offerLast(firstOperand);
                        }
                    } else {
                        if (currentToken.getTokenName().equals(TokenName.LEFTPAR)) {
                            leftPar++;
                        } else if (currentToken.getTokenName().equals(TokenName.RIGHTPAR)) {
                            rightPar++;
                        }
                        exInput.offerLast(makeNode(currentToken));
                        ADVANCE();
                    }
                }

                exInput.offerLast(new ParseTreeNode(new Token(TokenName.DOLLAR_OPERATOR, null, null, null, null, 0), null, null));
                exStack.push(new ParseTreeNode(new Token(TokenName.DOLLAR_OPERATOR, null, null, null, null, 0), null, null));

                while (!exInput.isEmpty()) {
                    if (exInput.peekFirst().getToken().getTokenName().equals(TokenName.DEFAULT) && exInput.size() > 0) {
                        exInput.removeFirst();
                    } else if (exInput.peekFirst().getToken().getTokenName().equals(TokenName.DOLLAR_OPERATOR) && exStack.peek().getToken().getTokenName().equals(TokenName.DOLLAR_OPERATOR) && !operandStack.isEmpty()) {
                        NEGEXP_NODE.setFirstChild(operatorHolder);
                        NEGEXP_NODE.setNextSibling(operandStack.pop());
                        NEGEXP_ROOT.setFirstChild(NEGEXP_NODE);
                        NEGEXP.setRoot(NEGEXP_ROOT);
                        break;
                    } else if (precedenceTable.evaluatePrecedenceArithmetic(exStack.peek().getToken().getTokenName(), exInput.peekFirst().getToken().getTokenName()).equals(OperatorPrecedence.LESSER)) {
                        exStack.push(exInput.removeFirst());
                    } else if (precedenceTable.evaluatePrecedenceArithmetic(exStack.peek().getToken().getTokenName(), exInput.peekFirst().getToken().getTokenName()).equals(OperatorPrecedence.GREATER)) {
                        holder = exStack.pop();
                        if (holder.getToken().getTokenName().equals(TokenName.CONSINT) || holder.getToken().getTokenName().equals(TokenName.CONSFLOAT) || holder.getToken().getTokenName().equals(TokenName.IDENTIFIER) || holder.getToken().getTokenName().equals(TokenName.PREINC) || holder.getToken().getTokenName().equals(TokenName.PREDEC) || holder.getToken().getTokenName().equals(TokenName.POSTINC) || holder.getToken().getTokenName().equals(TokenName.NEGINT) || holder.getToken().getTokenName().equals(TokenName.NEGFLOAT) || holder.getToken().getTokenName().equals(TokenName.NEGIDENTIFIER) || holder.getToken().getTokenName().equals(TokenName.NEGEXP_ROOT)) {
                            operandStack.push(holder);
                        } else {
                            secondOperand = operandStack.pop();
                            firstOperand = operandStack.pop();
                            holder.setChildAndSibling(firstOperand, secondOperand);
                            operandStack.push(holder);
                        }
                    } else if (precedenceTable.evaluatePrecedenceArithmetic(exStack.peek().getToken().getTokenName(), exInput.peekFirst().getToken().getTokenName()).equals(OperatorPrecedence.EQUAL)) {
                        exStack.pop();
                        exInput.removeFirst();
                    }
                }
            }

            expressionInput.offerLast(makeNode(new Token(TokenName.LEFTPAR, null, null, null, null, 0)));
            expressionInput.offerLast(NEGEXP_ROOT);
            expressionInput.offerLast(makeNode(new Token(TokenName.RIGHTPAR, null, null, null, null, 0)));

        } else if (!currentToken.getTokenType().equals(TokenType.ARITHMETICOP)) {
            expressionInput.offerLast(firstOperand);
        }
        
        while (!currentToken.getTokenName().equals(TokenName.EOL) && !currentToken.getTokenName().equals(TokenName.COMMA) && !currentToken.getTokenType().equals(TokenType.RELOP)) {
            if (currentToken.getTokenName().equals(TokenName.DIFF) && !(expressionInput.getLast().getToken().getTokenName().equals(TokenName.CONSINT) || expressionInput.getLast().getToken().getTokenName().equals(TokenName.CONSFLOAT) || expressionInput.getLast().getToken().getTokenName().equals(TokenName.NEGFLOAT) || expressionInput.getLast().getToken().getTokenName().equals(TokenName.NEGINT) || expressionInput.getLast().getToken().getTokenName().equals(TokenName.POSTINC) || expressionInput.getLast().getToken().getTokenName().equals(TokenName.POSTDEC) || expressionInput.getLast().getToken().getTokenName().equals(TokenName.PREINC) || expressionInput.getLast().getToken().getTokenName().equals(TokenName.PREDEC))) {
                firstOperand = makeNode(currentToken);
                ADVANCE();
                if (currentToken.getTokenName().equals(TokenName.IDENTIFIER) || currentToken.getTokenName().equals(TokenName.CONSINT) || currentToken.getTokenName().equals(TokenName.CONSFLOAT)) {
                    secondOperand = makeNode(currentToken);
                    expressionInput.offerLast(makeNode(new Token(TokenName.LEFTPAR, null, null, null, null, 0)));
                    if (secondOperand.getToken().getTokenName().equals(TokenName.CONSINT)) {
                        expressionInput.offerLast(new ParseTreeNode(new Token(TokenName.NEGINT, null, null, null, null, 0), firstOperand, secondOperand));
                    } else if (secondOperand.getToken().getTokenName().equals(TokenName.CONSFLOAT)) {
                        expressionInput.offerLast(new ParseTreeNode(new Token(TokenName.NEGFLOAT, null, null, null, null, 0), firstOperand, secondOperand));
                    } else if (secondOperand.getToken().getTokenName().equals(TokenName.IDENTIFIER)) {
                        expressionInput.offerLast(new ParseTreeNode(new Token(TokenName.NEGIDENTIFIER, null, null, null, null, 0), firstOperand, secondOperand));
                    }
                    expressionInput.offerLast(makeNode(new Token(TokenName.RIGHTPAR, null, null, null, null, 0)));
                    ADVANCE();
                }
            } else if (currentToken.getTokenName().equals(TokenName.LEFTPAR)) {
                holder = makeNode(currentToken);
                ADVANCE();
                if (currentToken.getTokenName().equals(TokenName.DIFF)) {
                    expressionInput.offerLast(holder);
                    firstOperand = makeNode(currentToken);
                    ADVANCE();
                    if (currentToken.getTokenName().equals(TokenName.IDENTIFIER) || currentToken.getTokenName().equals(TokenName.CONSINT) || currentToken.getTokenName().equals(TokenName.CONSFLOAT)) {
                        secondOperand = makeNode(currentToken);
                        if (secondOperand.getToken().getTokenName().equals(TokenName.CONSINT)) {
                            expressionInput.offerLast(new ParseTreeNode(new Token(TokenName.NEGINT, null, null, null, null, 0), firstOperand, secondOperand));
                        } else if (secondOperand.getToken().getTokenName().equals(TokenName.CONSFLOAT)) {
                            expressionInput.offerLast(new ParseTreeNode(new Token(TokenName.NEGFLOAT, null, null, null, null, 0), firstOperand, secondOperand));
                        } else if (secondOperand.getToken().getTokenName().equals(TokenName.IDENTIFIER)) {
                            expressionInput.offerLast(new ParseTreeNode(new Token(TokenName.NEGIDENTIFIER, null, null, null, null, 0), firstOperand, secondOperand));
                        }
                        ADVANCE();
                        if (currentToken.getTokenName().equals(TokenName.RIGHTPAR)) {
                            expressionInput.offerLast(makeNode(currentToken));
                            ADVANCE();
                        }
                    }
                } else {
                    expressionInput.offerLast(holder);
                    expressionInput.offerLast(makeNode(currentToken));
                    ADVANCE();
                }
            } else if (currentToken.getTokenType().equals(TokenType.ARITHMETICOP) && !(currentToken.getTokenName().equals(TokenName.INC) || currentToken.getTokenName().equals(TokenName.DEC))) {
                holder = makeNode(currentToken);
                ADVANCE();
                if (currentToken.getTokenName().equals(TokenName.DIFF)) {
                    expressionInput.offerLast(holder);
                    firstOperand = makeNode(currentToken);
                    ADVANCE();
                    if (currentToken.getTokenName().equals(TokenName.IDENTIFIER) || currentToken.getTokenName().equals(TokenName.CONSINT) || currentToken.getTokenName().equals(TokenName.CONSFLOAT)) {
                        secondOperand = makeNode(currentToken);
                        expressionInput.offerLast(makeNode(new Token(TokenName.LEFTPAR, null, null, null, null, 0)));
                        if (secondOperand.getToken().getTokenName().equals(TokenName.CONSINT)) {
                            expressionInput.offerLast(new ParseTreeNode(new Token(TokenName.NEGINT, null, null, null, null, 0), firstOperand, secondOperand));
                        } else if (secondOperand.getToken().getTokenName().equals(TokenName.CONSFLOAT)) {
                            expressionInput.offerLast(new ParseTreeNode(new Token(TokenName.NEGFLOAT, null, null, null, null, 0), firstOperand, secondOperand));
                        } else if (secondOperand.getToken().getTokenName().equals(TokenName.IDENTIFIER)) {
                            expressionInput.offerLast(new ParseTreeNode(new Token(TokenName.NEGIDENTIFIER, null, null, null, null, 0), firstOperand, secondOperand));
                        }
                        expressionInput.offerLast(makeNode(new Token(TokenName.RIGHTPAR, null, null, null, null, 0)));
                        ADVANCE();
                    }
                } else if (currentToken.getTokenName().equals(TokenName.LEFTPAR)) {
                    expressionInput.offerLast(holder);
                    holder = makeNode(currentToken);
                    ADVANCE();
                    if (currentToken.getTokenName().equals(TokenName.DIFF)) {
                        expressionInput.offerLast(holder);
                        firstOperand = makeNode(currentToken);
                        ADVANCE();
                        if (currentToken.getTokenName().equals(TokenName.IDENTIFIER) || currentToken.getTokenName().equals(TokenName.CONSINT) || currentToken.getTokenName().equals(TokenName.CONSFLOAT)) {
                            secondOperand = makeNode(currentToken);
                            if (secondOperand.getToken().getTokenName().equals(TokenName.CONSINT)) {
                                expressionInput.offerLast(new ParseTreeNode(new Token(TokenName.NEGINT, null, null, null, null, 0), firstOperand, secondOperand));
                            } else if (secondOperand.getToken().getTokenName().equals(TokenName.CONSFLOAT)) {
                                expressionInput.offerLast(new ParseTreeNode(new Token(TokenName.NEGFLOAT, null, null, null, null, 0), firstOperand, secondOperand));
                            } else if (secondOperand.getToken().getTokenName().equals(TokenName.IDENTIFIER)) {
                                expressionInput.offerLast(new ParseTreeNode(new Token(TokenName.NEGIDENTIFIER, null, null, null, null, 0), firstOperand, secondOperand));
                            }
                            ADVANCE();
                            if (currentToken.getTokenName().equals(TokenName.RIGHTPAR)) {
                                expressionInput.offerLast(makeNode(currentToken));
                                ADVANCE();
                            }
                        }
                    } else {
                        expressionInput.offerLast(holder);
                        expressionInput.offerLast(makeNode(currentToken));
                        ADVANCE();
                    }
                } else {
                    expressionInput.offerLast(holder);
                    expressionInput.offerLast(makeNode(currentToken));
                    ADVANCE();
                }
            } else if (currentToken.getTokenName().equals(TokenName.INC) || currentToken.getTokenName().equals(TokenName.DEC)) {
                firstOperand = makeNode(currentToken);
                ADVANCE();
                if (currentToken.getTokenName().equals(TokenName.IDENTIFIER)) {
                    secondOperand = makeNode(currentToken);
                    expressionInput.offerLast(makeNode(new Token(TokenName.LEFTPAR, null, null, null, null, 0)));
                    if (firstOperand.getToken().getTokenName().equals(TokenName.INC)) {
                        expressionInput.offerLast(new ParseTreeNode(new Token(TokenName.PREINC, null, null, null, null, 0), firstOperand, secondOperand));
                    } else if (firstOperand.getToken().getTokenName().equals(TokenName.DEC)) {
                        expressionInput.offerLast(new ParseTreeNode(new Token(TokenName.PREDEC, null, null, null, null, 0), firstOperand, secondOperand));
                    }
                    expressionInput.offerLast(makeNode(new Token(TokenName.RIGHTPAR, null, null, null, null, 0)));
                    ADVANCE();
                } else {
                    ERROR = ERROR(TokenName.IDENTIFIER, currentToken, currentToken.getLineNumber());
                    return new ParseTree(new ParseTreeNode(ERROR, null, null));
                }
            } else if (currentToken.getTokenName().equals(TokenName.IDENTIFIER)) {
                firstOperand = makeNode(currentToken);
                ADVANCE();
                if (currentToken.getTokenName().equals(TokenName.INC) || currentToken.getTokenName().equals(TokenName.DEC)) {
                    secondOperand = makeNode(currentToken);
                    expressionInput.offerLast(makeNode(new Token(TokenName.LEFTPAR, null, null, null, null, 0)));
                    if (secondOperand.getToken().getTokenName().equals(TokenName.INC)) {
                        expressionInput.offerLast(new ParseTreeNode(new Token(TokenName.POSTINC, null, null, null, null, 0), firstOperand, secondOperand));
                    } else if (secondOperand.getToken().getTokenName().equals(TokenName.DEC)) {
                        expressionInput.offerLast(new ParseTreeNode(new Token(TokenName.POSTDEC, null, null, null, null, 0), firstOperand, secondOperand));
                    }
                    expressionInput.offerLast(makeNode(new Token(TokenName.RIGHTPAR, null, null, null, null, 0)));
                    ADVANCE();
                } else {
                    expressionInput.offerLast(firstOperand);
                }
            } else {
                expressionInput.offerLast(makeNode(currentToken));
                ADVANCE();
                if (currentToken.getTokenName().equals(TokenName.LEFTBRACE)) {
                    break;
                }
            }
        }

        expressionInput.offerLast(new ParseTreeNode(new Token(TokenName.DOLLAR_OPERATOR, null, null, null, null, 0), null, null));
        expressionStack.push(new ParseTreeNode(new Token(TokenName.DOLLAR_OPERATOR, null, null, null, null, 0), null, null));

        while (!expressionInput.isEmpty()) {
            if (expressionInput.peekFirst().getToken().getTokenName().equals(TokenName.DEFAULT) && expressionInput.size() > 0) {
                expressionInput.removeFirst();
            } else if (expressionInput.peekFirst().getToken().getTokenName().equals(TokenName.DOLLAR_OPERATOR) && expressionStack.peek().getToken().getTokenName().equals(TokenName.DOLLAR_OPERATOR) && !operandStack.isEmpty()) {
                EXPRESSION_NODE.setFirstChild(operandStack.pop());
                EXPRESSION.setRoot(EXPRESSION_NODE);
                return EXPRESSION;
            } else if ((expressionInput.peekFirst().getToken().getTokenName().equals(TokenName.RIGHTPAR) || expressionInput.peekFirst().getToken().getTokenName().equals(TokenName.LEFTBRACE)) && expressionInput.peekLast().getToken().getTokenName().equals(TokenName.DOLLAR_OPERATOR) && expressionStack.peek().getToken().getTokenName().equals(TokenName.DOLLAR_OPERATOR) && expressionStack.size() == 1) {
                overReadToken = expressionInput.removeFirst().getToken();
                EXPRESSION_NODE.setFirstChild(operandStack.pop());
                EXPRESSION.setRoot(EXPRESSION_NODE);
                return EXPRESSION;
            } else if (precedenceTable.evaluatePrecedenceArithmetic(expressionStack.peek().getToken().getTokenName(), expressionInput.peekFirst().getToken().getTokenName()).equals(OperatorPrecedence.LESSER)) {
                expressionStack.push(expressionInput.removeFirst());
            } else if (precedenceTable.evaluatePrecedenceArithmetic(expressionStack.peek().getToken().getTokenName(), expressionInput.peekFirst().getToken().getTokenName()).equals(OperatorPrecedence.GREATER)) {
                holder = expressionStack.pop();
                if (holder.getToken().getTokenName().equals(TokenName.CONSINT) || holder.getToken().getTokenName().equals(TokenName.CONSFLOAT) || holder.getToken().getTokenName().equals(TokenName.IDENTIFIER) || holder.getToken().getTokenName().equals(TokenName.PREINC) || holder.getToken().getTokenName().equals(TokenName.PREDEC) || holder.getToken().getTokenName().equals(TokenName.POSTINC) || holder.getToken().getTokenName().equals(TokenName.NEGINT) || holder.getToken().getTokenName().equals(TokenName.NEGFLOAT) || holder.getToken().getTokenName().equals(TokenName.NEGIDENTIFIER) || holder.getToken().getTokenName().equals(TokenName.NEGEXP_ROOT)) {
                    operandStack.push(holder);
                } else {
                    secondOperand = operandStack.pop();
                    firstOperand = operandStack.pop();
                    holder.setChildAndSibling(firstOperand, secondOperand);
                    operandStack.push(holder);
                }
            } else if (precedenceTable.evaluatePrecedenceArithmetic(expressionStack.peek().getToken().getTokenName(), expressionInput.peekFirst().getToken().getTokenName()).equals(OperatorPrecedence.EQUAL)) {
                expressionStack.pop();
                expressionInput.removeFirst();
            }
        }

        return EXPRESSION;
    }

    public ParseTree LOGICAL_EXPRESSION() throws IOException {
        LinkedList<ParseTreeNode> expressionInput = new LinkedList<>();
        Stack<ParseTreeNode> expressionStack = new Stack<>();
        Stack<ParseTreeNode> operandStack = new Stack<>();

        OperatorPrecedenceTable precedenceTable = new OperatorPrecedenceTable();

        ParseTreeNode operatorHolder = new ParseTreeNode();
        ParseTreeNode holder = new ParseTreeNode();
        ParseTreeNode firstOperand = new ParseTreeNode();
        ParseTreeNode secondOperand = new ParseTreeNode();
        ParseTreeNode EXPRESSION_NODE = new ParseTreeNode(new Token(TokenName.EMPTY, null, null, null, null, 0), null, null);
        ParseTreeNode NOTEXP_NODE = new ParseTreeNode(new Token(TokenName.NOTEXP_NODE, null, null, null, null, 0), null, null);
        ParseTreeNode NOTEXP_ROOT = new ParseTreeNode(new Token(TokenName.NOTEXP_ROOT, null, null, null, null, 0), null, null);

        ParseTree EXPRESSION = new ParseTree();
        ParseTree NOTEXP = new ParseTree();

        Token ERROR;

        while (unmatchedLeftPar != 0) {
            expressionInput.offerLast(makeNode(new Token(TokenName.LEFTPAR, null, null, null, null, 0)));
            unmatchedLeftPar--;
        }

        while (!currentToken.getTokenName().equals(TokenName.EOL) && !currentToken.getTokenName().equals(TokenName.COMMA) && !currentToken.getTokenName().equals(TokenName.LEFTBRACE) && !currentToken.getTokenType().equals(TokenType.RELOP)) {
            expressionInput.offerLast(makeNode(currentToken));
            ADVANCE();
        }

        expressionInput.offerLast(new ParseTreeNode(new Token(TokenName.DOLLAR_OPERATOR, null, null, null, null, 0), null, null));
        expressionStack.push(new ParseTreeNode(new Token(TokenName.DOLLAR_OPERATOR, null, null, null, null, 0), null, null));

        while (!expressionInput.isEmpty()) {
            if (expressionInput.peekFirst().getToken().getTokenName().equals(TokenName.DEFAULT) && expressionInput.size() > 0) {
                expressionInput.removeFirst();
            } else if (expressionInput.peekFirst().getToken().getTokenName().equals(TokenName.DOLLAR_OPERATOR) && expressionStack.peek().getToken().getTokenName().equals(TokenName.DOLLAR_OPERATOR) && !operandStack.isEmpty()) {
                EXPRESSION_NODE = new ParseTreeNode(new Token(TokenName.LOGICAL_EXPRESSION, null, null, null, null, 0), null, null);
                EXPRESSION_NODE.setFirstChild(operandStack.pop());
                EXPRESSION.setRoot(EXPRESSION_NODE);
                return EXPRESSION;
            } else if (precedenceTable.evaluatePrecedenceLogical(expressionStack.peek().getToken().getTokenName(), expressionInput.peekFirst().getToken().getTokenName()).equals(OperatorPrecedence.LESSER)) {
                expressionStack.push(expressionInput.removeFirst());
            } else if (precedenceTable.evaluatePrecedenceLogical(expressionStack.peek().getToken().getTokenName(), expressionInput.peekFirst().getToken().getTokenName()).equals(OperatorPrecedence.DO_NOT)) {
                firstOperand = expressionStack.pop();
                secondOperand = expressionInput.removeFirst();
                expressionInput.offerFirst(new ParseTreeNode(new Token(TokenName.RIGHTPAR, null, null, null, null, 0), null, null));
                expressionInput.offerFirst(new ParseTreeNode(new Token(TokenName.NOTEXP_NODE, null, null, null, null, 0), firstOperand, secondOperand));
                expressionInput.offerFirst(new ParseTreeNode(new Token(TokenName.LEFTPAR, null, null, null, null, 0), null, null));
            } else if (expressionInput.peekFirst().getToken().getTokenName().equals(TokenName.RIGHTPAR) && expressionInput.peekLast().getToken().getTokenName().equals(TokenName.DOLLAR_OPERATOR) && expressionStack.peek().getToken().getTokenName().equals(TokenName.DOLLAR_OPERATOR) && expressionStack.size() == 1) {
                EXPRESSION_NODE = new ParseTreeNode(new Token(TokenName.LOGICAL_EXPRESSION, null, null, null, null, 0), null, null);
                overReadToken = expressionInput.removeFirst().getToken();
                EXPRESSION_NODE.setFirstChild(operandStack.pop());
                EXPRESSION.setRoot(EXPRESSION_NODE);
                return EXPRESSION;
            } else if (precedenceTable.evaluatePrecedenceLogical(expressionStack.peek().getToken().getTokenName(), expressionInput.peekFirst().getToken().getTokenName()).equals(OperatorPrecedence.GREATER)) {
                holder = expressionStack.pop();
                if (holder.getToken().getTokenName().equals(TokenName.AFFIRM) || holder.getToken().getTokenName().equals(TokenName.NEGATE) || holder.getToken().getTokenName().equals(TokenName.IDENTIFIER) || holder.getToken().getTokenName().equals(TokenName.NOTEXP_NODE)) {
                    operandStack.push(holder);
                } else if (holder.getToken().getTokenName().equals(TokenName.LOGICNOT) || operandStack.size() == 1) {
                    secondOperand = operandStack.pop();
                    operandStack.push(new ParseTreeNode(new Token(TokenName.NOTEXP_NODE, null, null, null, null, 0), holder, secondOperand));
                } else {
                    secondOperand = operandStack.pop();
                    firstOperand = operandStack.pop();
                    holder.setChildAndSibling(firstOperand, secondOperand);
                    operandStack.push(holder);
                }
            } else if (precedenceTable.evaluatePrecedenceLogical(expressionStack.peek().getToken().getTokenName(), expressionInput.peekFirst().getToken().getTokenName()).equals(OperatorPrecedence.EQUAL)) {
                expressionStack.pop();
                expressionInput.removeFirst();
                if (expressionStack.peek().getToken().getTokenName().equals(TokenName.LOGICNOT) && (operandStack.peek().getToken().getTokenName().equals(TokenName.AFFIRM) || operandStack.peek().getToken().getTokenName().equals(TokenName.NEGATE) || operandStack.peek().getToken().getTokenName().equals(TokenName.IDENTIFIER) || operandStack.peek().getToken().getTokenName().equals(TokenName.NOTEXP_NODE))) {
                    firstOperand = expressionStack.pop();
                    secondOperand = operandStack.pop();
                    operandStack.push(new ParseTreeNode(new Token(TokenName.NOTEXP_NODE, null, null, null, null, 0), firstOperand, secondOperand));
                }
            }
        }

        return EXPRESSION;
    }

    public ParseTree RELATIONAL_EXPRESSION() throws IOException {
        LinkedList<ParseTreeNode> expressionInput = new LinkedList<>();
        Stack<ParseTreeNode> expressionStack = new Stack<>();
        Stack<ParseTreeNode> operandStack = new Stack<>();

        OperatorPrecedenceTable precedenceTable = new OperatorPrecedenceTable();

        ParseTreeNode operatorHolder = new ParseTreeNode();
        ParseTreeNode holder = new ParseTreeNode();
        ParseTreeNode firstOperand = new ParseTreeNode();
        ParseTreeNode secondOperand = new ParseTreeNode();
        ParseTreeNode EXPRESSION_NODE = new ParseTreeNode(new Token(TokenName.EMPTY, null, null, null, null, 0), null, null);
        ParseTreeNode ARITHMETIC_EXPRESSION = new ParseTreeNode(new Token(TokenName.ARITHMETIC_EXPRESSION, null, null, null, null, 0), null, null);
        ParseTreeNode LOGICAL_EXPRESSION = new ParseTreeNode(new Token(TokenName.LOGICAL_EXPRESSION, null, null, null, null, 0), null, null);

        ParseTree EXPRESSION = new ParseTree();
        ParseTree NOTEXP = new ParseTree();

        Token ERROR;
        boolean loopBreak = true;
        DataType type = DataType.NULL;

        while (!currentToken.getTokenName().equals(TokenName.EOL) && !currentToken.getTokenName().equals(TokenName.COMMA) && !currentToken.getTokenName().equals(TokenName.LEFTBRACE)) {
            switch (currentToken.getTokenName()) {
                case OPGREAT:
                case OPLESS:
                case OPGREQ:
                case OPLEQ:
                case OPNOT:
                case OPEQUAL:
                    expressionInput.offerLast(makeNode(currentToken));
                    ADVANCE();
                    break;
                case LOGICNOT:
                case AFFIRM:
                case NEGATE:
                    while (unmatchedLeftPar != 0) {
                        expressionInput.offerLast(makeNode(new Token(TokenName.LEFTPAR, null, null, null, null, 0)));
                        unmatchedLeftPar--;
                    }
                    unmatchedLeftPar = 0;
                    LOGICAL_EXPRESSION = LOGICAL_EXPRESSION().root;
                    expressionInput.offerLast(LOGICAL_EXPRESSION);

                    if (overReadToken.getTokenName().equals(TokenName.RIGHTPAR)) {
                        expressionInput.offerLast(makeNode(overReadToken));
                    }
                    break;
                case DIFF:
                case CONSINT:
                case CONSFLOAT:
                    while (unmatchedLeftPar != 0) {
                        expressionInput.offerLast(makeNode(new Token(TokenName.LEFTPAR, null, null, null, null, 0)));
                        unmatchedLeftPar--;
                    }
                    unmatchedLeftPar = 0;
                    
                    ARITHMETIC_EXPRESSION = ARITHMETIC_EXPRESSION().root;
                    expressionInput.offerLast(ARITHMETIC_EXPRESSION);

                    if (overReadToken.getTokenName().equals(TokenName.RIGHTPAR)) {
                        expressionInput.offerLast(makeNode(overReadToken));
                    }
                    break;
                case LEFTPAR:
                    unmatchedLeftPar++;
                    ADVANCE();
                    break;
                case IDENTIFIER:
                    while (unmatchedLeftPar != 0) {
                        expressionInput.offerLast(makeNode(new Token(TokenName.LEFTPAR, null, null, null, null, 0)));
                        unmatchedLeftPar--;
                    }
                    unmatchedLeftPar = 0;
                    try {
                        type = lex.SymbolTable.get(currentToken.getLexeme()).getDataType();
                    } catch (Exception e) {
                        ERROR = ERROR(TokenName.IDENTIFIER_NOT_DECLARED, currentToken, currentToken.getLineNumber());
                        System.exit(0);
                    }
                    switch (type) {

                        case INTEGER:
                        case FLOATING_POINT:
                            ARITHMETIC_EXPRESSION = ARITHMETIC_EXPRESSION().root;
                            expressionInput.offerLast(ARITHMETIC_EXPRESSION);

                            if (overReadToken.getTokenName().equals(TokenName.RIGHTPAR)) {
                                expressionInput.offerLast(makeNode(overReadToken));
                            }
                            break;
                        case BOOLEAN:
                            LOGICAL_EXPRESSION = LOGICAL_EXPRESSION().root;
                            expressionInput.offerLast(LOGICAL_EXPRESSION);

                            if (overReadToken.getTokenName().equals(TokenName.RIGHTPAR)) {
                                expressionInput.offerLast(makeNode(overReadToken));
                            }
                            break;
                        default:
                            ERROR = ERROR(TokenName.DATA_TYPE_MISMATCH, currentToken, currentToken.getLineNumber());
                            break;
                    }
                    break;
            }
        }

        expressionInput.offerLast(new ParseTreeNode(new Token(TokenName.DOLLAR_OPERATOR, null, null, null, null, 0), null, null));
        expressionStack.push(new ParseTreeNode(new Token(TokenName.DOLLAR_OPERATOR, null, null, null, null, 0), null, null));

        while (!expressionInput.isEmpty()) {
            if (expressionInput.peekFirst().getToken().getTokenName().equals(TokenName.DEFAULT) && expressionInput.size() > 0) {
                expressionInput.removeFirst();
            } else if (expressionInput.peekFirst().getToken().getTokenName().equals(TokenName.DOLLAR_OPERATOR) && expressionStack.peek().getToken().getTokenName().equals(TokenName.DOLLAR_OPERATOR) && !operandStack.isEmpty()) {
                EXPRESSION_NODE = new ParseTreeNode(new Token(TokenName.RELATIONAL_EXPRESSION, null, null, null, null, 0), null, null);
                EXPRESSION_NODE.setFirstChild(operandStack.pop());
                EXPRESSION.setRoot(EXPRESSION_NODE);
                return EXPRESSION;
            } else if (precedenceTable.evaluatePrecedenceRelational(expressionStack.peek().getToken().getTokenName(), expressionInput.peekFirst().getToken().getTokenName()).equals(OperatorPrecedence.LESSER)) {
                expressionStack.push(expressionInput.removeFirst());
            } else if (expressionInput.peekFirst().getToken().getTokenName().equals(TokenName.RIGHTPAR) && expressionInput.peekLast().getToken().getTokenName().equals(TokenName.DOLLAR_OPERATOR) && expressionStack.peek().getToken().getTokenName().equals(TokenName.DOLLAR_OPERATOR) && expressionStack.size() == 1) {
                EXPRESSION_NODE = new ParseTreeNode(new Token(TokenName.RELATIONAL_EXPRESSION, null, null, null, null, 0), null, null);
                overReadToken = expressionInput.removeFirst().getToken();
                EXPRESSION_NODE.setFirstChild(operandStack.pop());
                EXPRESSION.setRoot(EXPRESSION_NODE);
                return EXPRESSION;
            } else if (precedenceTable.evaluatePrecedenceRelational(expressionStack.peek().getToken().getTokenName(), expressionInput.peekFirst().getToken().getTokenName()).equals(OperatorPrecedence.GREATER)) {
                holder = expressionStack.pop();
                if (holder.getToken().getTokenName().equals(TokenName.ARITHMETIC_EXPRESSION) || holder.getToken().getTokenName().equals(TokenName.LOGICAL_EXPRESSION)) {
                    operandStack.push(holder);
                } else {
                    secondOperand = operandStack.pop();
                    firstOperand = operandStack.pop();
                    holder.setChildAndSibling(firstOperand, secondOperand);
                    operandStack.push(holder);
                }
            } else if (precedenceTable.evaluatePrecedenceRelational(expressionStack.peek().getToken().getTokenName(), expressionInput.peekFirst().getToken().getTokenName()).equals(OperatorPrecedence.EQUAL)) {
                expressionStack.pop();
                expressionInput.removeFirst();
            }
        }

        return EXPRESSION;
    }
    //C:\Users\Theodore Arnel Merin\Documents\sample.txt
    //'Ã¿'

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
        ParseTree pTree = PROGRAM();
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
        syn.sourceScanner("C:\\Users\\Theodore Arnel Merin\\Documents\\sample6.txt");

    }
}
