package lexicalanalyzer;

import datastructures.*;
import java.io.*;
import static java.lang.Character.*;
import java.util.HashMap;
import java.util.Scanner;
import java.util.*;

public class LexicalAnalyzer {

    private HashMap<String, Token> SymbolTable;
    File f;
    FileInputStream source;
    BufferedInputStream in;
    PushbackInputStream lookAhead;
    PushbackInputStream lastGoodPointer;
    int lineNumber = 1;
    int colNumber = 0;
    int goodColNum;

    public LexicalAnalyzer() throws FileNotFoundException {
        SymbolTable = new HashMap<>(100);
        //preload the symbol table
        SymbolTable.put("ENGAGE", new Token(TokenName.ENGAGE, TokenType.RESERVED_WORD, "ENGAGE", "ENGAGE", null, lineNumber));
        SymbolTable.put("TERMINATE", new Token(TokenName.TERMINATE, TokenType.RESERVED_WORD, "TERMINATE", "TERMINATE", null, lineNumber));
        SymbolTable.put("ON", new Token(TokenName.ON, TokenType.RESERVED_WORD, "ON", "ON", null, lineNumber));
        SymbolTable.put("OFF", new Token(TokenName.OFF, TokenType.RESERVED_WORD, "OFF", "OFF", null, lineNumber));
        SymbolTable.put("VERIFY", new Token(TokenName.VERIFY, TokenType.RESERVED_WORD, "VERIFY", "VERIFY", null, lineNumber));
        SymbolTable.put("DIGIT", new Token(TokenName.DIGIT, TokenType.RESERVED_WORD, "DIGIT", "DIGIT", null, lineNumber));
        SymbolTable.put("SENTENCE", new Token(TokenName.SENTENCE, TokenType.RESERVED_WORD, "SENTENCE", "SENTENCE", null, lineNumber));
        SymbolTable.put("IF", new Token(TokenName.IF, TokenType.RESERVED_WORD, "IF", "IF", null, lineNumber));
        SymbolTable.put("ENDIF", new Token(TokenName.ENDIF, TokenType.RESERVED_WORD, "ENDIF", "ENDIF", null, lineNumber));
        SymbolTable.put("ELSEIF", new Token(TokenName.ELSEIF, TokenType.RESERVED_WORD, "ENDIF", "ENDIF", null, lineNumber));
        SymbolTable.put("ELSE", new Token(TokenName.ELSE, TokenType.RESERVED_WORD, "ELSE", "ELSE", null, lineNumber));
        SymbolTable.put("EXECUTE", new Token(TokenName.EXECUTE, TokenType.RESERVED_WORD, "EXECUTE", "EXECUTE", null, lineNumber));
        SymbolTable.put("DURING", new Token(TokenName.DURING, TokenType.RESERVED_WORD, "DURING", "DURING", null, lineNumber));
        SymbolTable.put("RECEIVE", new Token(TokenName.RECEIVE, TokenType.RESERVED_WORD, "RECEIVE", "RECEIVE", null, lineNumber));
        SymbolTable.put("TRANSMIT", new Token(TokenName.TRANSMIT, TokenType.RESERVED_WORD, "TRANSMIT", "TRANSMIT", null, lineNumber));
        SymbolTable.put("DIGITIZE", new Token(TokenName.DIGITIZE, TokenType.RESERVED_WORD, "DIGITIZE", "DIGITIZE", null, lineNumber));
        SymbolTable.put("WRITE", new Token(TokenName.WRITE, TokenType.RESERVED_WORD, "WRITE", "WRITE", null, lineNumber));
        SymbolTable.put("AFFIRM", new Token(TokenName.AFFIRM, TokenType.RESERVED_WORD, "AFFIRM", "AFFIRM", null, lineNumber));
        SymbolTable.put("NEGATE", new Token(TokenName.NEGATE, TokenType.RESERVED_WORD, "NEGATE", "NEGATE", null, lineNumber));
    }

        public void checkForWhiteSpace() throws IOException {
        PushbackInputStream ptr = lookAhead;
        int pointer;
        char current;
        pointer = ptr.read();
        current = (char) pointer;
        colNumber++;
        while (isWhitespace(current)) {
            pointer = ptr.read();
            current = (char) pointer;
            colNumber++;
            if (current == '\n') {
                lineNumber++;
                colNumber = 0;
                //System.out.println();
            } else if (current == '\t') {
                colNumber = colNumber + 4;
            } else if (pointer == 32) {
                colNumber++;
            }
        }
        ptr.unread(pointer);
        lookAhead = ptr;
        colNumber--;
    }

    public Token checkForIdentifier() throws IOException {
        PushbackInputStream ptr = lookAhead;
        int state = 1;
        int pointer = 0;
        char current;
        String value = "";
        while (true) {
            switch (state) {
                case 1:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (isLetter(current)) {
                        state = 2;
                        value = value + current;
                    } else {
                        state = 4;
                    }
                    break;
                case 2:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (isLetterOrDigit(current)) {
                        state = 2;
                        value = value + current;
                    } else {
                        state = 3;
                    }
                    break;
                case 3:
                    ptr.unread(pointer);
                    lookAhead = ptr;
                    colNumber--;
                    return new Token(TokenName.IDENTIFIER, TokenType.IDENTIFIER, value, value, null, lineNumber);
                case 4:
                    ptr.unread(pointer);
                    lookAhead = ptr;
                    colNumber--;
                    return new Token(TokenName.ERROR, TokenType.ERROR, "ERROR", "ERROR on line number (" + lineNumber + ") : Invalid identifier name \n", null, lineNumber);
            }
        }
    }

    public Token checkForRelationalOperator() throws IOException {
        PushbackInputStream ptr = lookAhead;
        int state = 1;
        int pointer = 0;
        char current;
        String value = "";
        while (true) {
            switch (state) {
                case 1:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == '=') {
                        state = 2;
                        value = value + current;
                    } else if (current == '>') {
                        state = 4;
                        value = value + current;
                    } else if (current == '<') {
                        state = 6;
                        value = value + current;
                    } else if (current == '!') {
                        state = 8;
                        value = value + current;
                    } else {
                        state = 10;
                    }
                    break;
                case 2:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == '=') {
                        state = 3;
                        value = value + current;
                    } else {
                        state = 10;
                    }
                    break;
                case 3:
                    lookAhead = ptr;
                    return new Token(TokenName.OPEQUAL, TokenType.RELOP, value, value, null, lineNumber);
                case 4:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == '=') {
                        state = 5;
                        value = value + current;
                    } else {
                        ptr.unread(pointer);
                        lookAhead = ptr;
                        colNumber--;
                        return new Token(TokenName.OPGREAT, TokenType.RELOP, value, value, null, lineNumber);
                    }
                    break;
                case 5:
                    lookAhead = ptr;
                    return new Token(TokenName.OPGREQ, TokenType.RELOP, value, value, null, lineNumber);
                case 6:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == '=') {
                        state = 7;
                        value = value + current;
                    } else {
                        ptr.unread(pointer);
                        lookAhead = ptr;
                        colNumber--;
                        return new Token(TokenName.OPLESS, TokenType.RELOP, value, value, null, lineNumber);
                    }
                    break;
                case 7:
                    lookAhead = ptr;
                    return new Token(TokenName.OPLEQ, TokenType.RELOP, value, value, null, lineNumber);
                case 8:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == '=') {
                        state = 9;
                        value = value + current;
                    } else {
                        ptr.unread(pointer);
                        lookAhead = ptr;
                        colNumber--;
                        return new Token(TokenName.LOGICNOT, TokenType.LOGICOP, value, value, null, lineNumber);
                    }
                    break;
                case 9:
                    lookAhead = ptr;
                    return new Token(TokenName.OPNOT, TokenType.RELOP, value, value, null, lineNumber);
                case 10:
                    ptr.unread(pointer);
                    colNumber--;
                    lookAhead = ptr;
                    return new Token(TokenName.ERROR, TokenType.ERROR, "ERROR", "ERROR on line number (" + lineNumber + ") column number (" + colNumber + "): Invalid relational operator \n", null, lineNumber);
            }
        }
    }

    public Token checkForLogicalOperator() throws IOException {
        PushbackInputStream ptr = lookAhead;
        int state = 1;
        int pointer = 0;
        char current;
        String value = "";
        while (true) {
            switch (state) {
                case 1:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == '&') {
                        state = 2;
                        value = value + current;
                    } else if (current == '|') {
                        state = 4;
                        value = value + current;
                    } else if (current == '!') {
                        state = 6;
                        value = value + current;
                    } else {
                        state = 7;
                    }
                    break;
                case 2:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == '&') {
                        state = 3;
                        value = value + current;
                    } else {
                        state = 7;
                    }
                    break;
                case 3:
                    lookAhead = ptr;
                    return new Token(TokenName.LOGICAND, TokenType.LOGICOP, value, value, null, lineNumber);
                case 4:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == '|') {
                        state = 5;
                        value = value + current;
                    } else {
                        state = 7;
                    }
                    break;
                case 5:
                    lookAhead = ptr;
                    return new Token(TokenName.LOGICOR, TokenType.LOGICOP, value, value, null, lineNumber);
                case 6:
                    lookAhead = ptr;
                    return new Token(TokenName.LOGICNOT, TokenType.LOGICOP, value, value, null, lineNumber);
                case 7:
                    ptr.unread(pointer);
                    colNumber--;
                    lookAhead = ptr;
                    return new Token(TokenName.ERROR, TokenType.ERROR, "ERROR", "ERROR on line number (" + lineNumber + ") column number (" + colNumber + "): Invalid logical operator \n", null, lineNumber);
            }
        }
    }

    public Token checkForConcatOperator() throws IOException {
        PushbackInputStream ptr = lookAhead;
        int state = 1;
        int pointer;
        char current;
        String value = "";
        boolean loop = true;
        while (loop) {
            switch (state) {
                case 1:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == '#') {
                        state = 2;
                        value = value + current;
                    }
                    break;
                case 2:
                    lookAhead = ptr;
                    return new Token(TokenName.CONCATOPP, TokenType.CONCATOPP, value, value, null, lineNumber);
            }
        }
        return new Token();
    }

    public Token checkForArithmeticOperator() throws IOException {
        PushbackInputStream ptr = lookAhead;
        int state = 1;
        int pointer;
        char current;
        String value = "";
        while (true) {
            switch (state) {
                case 1:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == '+') {
                        state = 2;
                        value = value + current;
                    } else if (current == '-') {
                        state = 4;
                        value = value + current;
                    } else if (current == '*') {
                        state = 6;
                        value = value + current;
                    } else if (current == '/') {
                        state = 7;
                        value = value + current;
                    } else if (current == '^') {
                        state = 8;
                        value = value + current;
                    } else if (current == '%') {
                        state = 9;
                        value = value + current;
                    } else {
                        ptr.unread(pointer);
                        lookAhead = ptr;
                        colNumber--;
                        return new Token();
                    }
                    break;
                case 2:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == '+') {
                        state = 3;
                        value = value + current;
                    } else {
                        ptr.unread(pointer);
                        lookAhead = ptr;
                        colNumber--;
                        return new Token(TokenName.SUM, TokenType.ARITHMETICOP, value, value, null, lineNumber);
                    }
                    break;
                case 3:
                    lookAhead = ptr;
                    return new Token(TokenName.INC, TokenType.ARITHMETICOP, value, value, null, lineNumber);
                case 4:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == '-') {
                        state = 3;
                        value = value + current;
                    } else {
                        ptr.unread(pointer);
                        lookAhead = ptr;
                        colNumber--;
                        return new Token(TokenName.DIFF, TokenType.ARITHMETICOP, value, value, null, lineNumber);
                    }
                    break;
                case 5:
                    lookAhead = ptr;
                    return new Token(TokenName.DEC, TokenType.ARITHMETICOP, value, value, null, lineNumber);
                case 6:
                    lookAhead = ptr;
                    return new Token(TokenName.PROD, TokenType.ARITHMETICOP, value, value, null, lineNumber);
                case 7:
                    lookAhead = ptr;
                    return new Token(TokenName.DIV, TokenType.ARITHMETICOP, value, value, null, lineNumber);
                case 8:
                    lookAhead = ptr;
                    return new Token(TokenName.POW, TokenType.ARITHMETICOP, value, value, null, lineNumber);
                case 9:
                    lookAhead = ptr;
                    return new Token(TokenName.MOD, TokenType.ARITHMETICOP, value, value, null, lineNumber);
            }
        }
    }

    public Token checkForIntConstant() throws IOException {
        PushbackInputStream ptr = lookAhead;
        int state = 1;
        int pointer;
        char current;
        String value = "";
        boolean loop = true;
        while (loop) {
            switch (state) {
                case 1:
                    pointer = ptr.read();
                    current = (char) pointer;
                    if (pointer == 48)  //if value = 0
                    {
                        state = 3;
                        value = value + current;
                    } else {
                        value = value + current;
                        state = 2;
                    }
                    break;
                case 2:
                    pointer = ptr.read();
                    current = (char) pointer;
                    if (isDigit(current)) {
                        state = 2;
                        value = value + current;
                    } else {
                        ptr.unread(pointer);
                        lookAhead = ptr;
                        state = 3;
                    }
                    break;
                case 3:
                    return new Token(TokenName.CONSINT, TokenType.INT_CONSTANT, value, value, null, lineNumber);
            }
        }
        return new Token();
    }
    
    public Token checkForFloatConstant() throws IOException {
        PushbackInputStream ptr = lookAhead;
        int state = 1;
        int pointer;
        char current;
        String value = "";
        boolean loop = true;
        while (loop) {
            switch (state) {
                case 1:
                    pointer = ptr.read();
                    current = (char) pointer;
                    if (isDigit(current))
                    {
                        state = 2;
                        value = value + current;
                    }
                    else if (current == '.') 
                    {
                        value = value + current;
                        state = 3;
                    }
                    else
                    {
                        ptr.unread(pointer);
                        state = 3;
                    }
                    break;
                case 2:
                    pointer = ptr.read();
                    current = (char) pointer;
                    if (isDigit(current)) {
                        state = 2;
                        value = value + current;
                    }
                    else if(current == '.')
                    {
                        value = value + current;
                        state = 3;
                    }
                    else 
                    {
                        ptr.unread(pointer);
                        state = 3;
                    }
                    break;
                case 3:
                    pointer = ptr.read();
                    current = (char) pointer;
                    if (isDigit(current)) {
                        state = 4;
                        value = value + current;
                    }
                    else
                    {
                        //The input is either an INTEGER CONSTANT or an EOL
                        lookAhead = ptr;
                        if(value.startsWith(".")) {
                            in.reset();
                            return new Token(TokenName.ISEOL, TokenType.ISEOL, "EOL: Not Floating Point", "EOL: Not Floating Point",null, lineNumber);        
                        }
                        else{
                            in.reset();
                            return new Token(TokenName.ISINTCONST, TokenType.ISINTCONST, "INT_CONST: Not Floating Point", "INT_CONST: Not Floating Point", null, lineNumber);
                        }
                    }
                    
                case 4:
                    pointer = ptr.read();
                    current = (char) pointer;
                    if(isDigit(current))
                    {
                        value = value + current;
                        state = 4;
                    }
                    else
                    {
                        ptr.unread(pointer);
                        state = 5;
                    }
                    break;
                
                case 5:
                    // The input is VALID FLOAT CONSTANT
                    lookAhead = ptr;
                    return new Token(TokenName.CONSFLOAT, TokenType.FLOAT_CONSTANT, value, value, null, lineNumber);
            }
        }
        return new Token();
    }
    
    public Token checkForCharConstant() throws IOException {
        PushbackInputStream ptr = lookAhead;
        int state = 1;
        int pointer = 0;
        char current = 0;
        String value = "";
        boolean loop = true;
        while (loop) {
            switch (state) {
                case 1:
                    pointer = ptr.read();
                    current = (char) pointer;
                    if (current == '\'') {
                        value = value + current;
                        state = 2;
                    } else {
                        state = 4;
                    }
                    break;
                case 2:
                    pointer = ptr.read();
                    current = (char) pointer;
                    if ((pointer < 34 && pointer > 31) || (pointer < 127 && pointer > 34) )//&& (pointer != 9 || pointer != 10))
                    {
                        value = value + current;
                        state = 3;
                    }
                    else
                    {
                        state = 4;
                    }
                    break;
                
                case 3:
                    if(current == '\'')
                    {
                        pointer = ptr.read();
                        current = (char) pointer;
                        if(current != '\'')
                        {
                            ptr.unread(pointer);
                            lookAhead = ptr;
                            return new Token(TokenName.CONSCHAR, TokenType.CHAR_CONSTANT, value , value, null, 0 );
                        }
                        else
                        {
                            lookAhead = ptr;
                            value = value + current;
                            return new Token(TokenName.CONSCHAR, TokenType.CHAR_CONSTANT, value , value, null, 0 );
                        }
                    }
                    
                    pointer = ptr.read();
                    current = (char) pointer;
                    if (current == '\'') {
                        value = value + current;
                        return new Token(TokenName.CONSCHAR, TokenType.CHAR_CONSTANT, value , value, null, 0 );
                    } else {
                         state = 4;
                    }
                    
                case 4:
                    ptr.unread(pointer);
                    lookAhead = ptr;
                    return new Token(TokenName.ERROR, TokenType.ERROR, "ERROR", "ERROR on line number (" + lineNumber + ") column number (" + colNumber + "): Invalid char constant \n", null, lineNumber);
            }
        }
        return new Token();
    }

    public Token checkForStringConstant() throws IOException {
        PushbackInputStream ptr = lookAhead;
        int state = 1;
        int pointer = 0;
        char current;
        String value = "";
        boolean loop = true;
        while (loop) {
            switch (state) {
                case 1:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == '"') {
                        state = 2;
                    } else {
                        state = 4;
                    }
                    break;
                case 2:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    char newline = (char) 10;
                    char tab = (char) 9;
                    if (pointer == 92) {
                        pointer = ptr.read();
                        current = (char) pointer;
                        colNumber++;
                        if (current == 'n') {
                            value = value + newline;
                        } else if (current == 't') {
                            value = value + tab;
                        } else if (current == '"') {
                            value = value + current;
                        }
                        state = 2;
                    } else if (((pointer < 34 && pointer > 31) || (pointer < 127 && pointer > 34) && (pointer != 92))) {
                        state = 2;
                        value = value + current;
                    } else {
                        lookAhead.unread(pointer);
                        colNumber--;
                        state = 3;
                    }
                    break;
                case 3:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == '"') {
                        return new Token(TokenName.CONSSTR, TokenType.STRING_CONSTANT, "\"" + value + "\"", "\"" + value + "\"", null, lineNumber);
                    } else {
                        state = 4;
                    }
                case 4:
                    ptr.unread(pointer);
                    lookAhead = ptr;
                    colNumber--;
                    return new Token(TokenName.ERROR, TokenType.ERROR, "ERROR", "ERROR on line number (" + lineNumber + ") column number (" + colNumber + "): Invalid string constant \n", null, lineNumber);
            }
        }
        return new Token();
    }

    public Token checkForComments() throws IOException {
        PushbackInputStream ptr = lookAhead;
        int state = 1;
        int pointer = 0;
        char current;
        String value = "";
        boolean loop = true;
        while (loop) {
            switch (state) {
                case 1:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == '~') {
                        value = value + current;
                        pointer = ptr.read();
                        current = (char) pointer;
                        colNumber++;
                        if (current == '#') {
                            value = value + current;
                            state = 2;
                        } else {
                            ptr.unread(pointer);
                            colNumber--;
                            state = 4;
                        }
                    } else {
                        state = 6;
                    }
                    break;
                case 2:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (pointer == -1) {//RETURN ERROR SINCE TOKEN IS EOF 
                        state = 6;
                        break;
                    }
                    if (current == '#') {
                        value = value + current;
                        pointer = ptr.read();
                        current = (char) pointer;
                        colNumber++;
                        if (pointer == -1) {//RETURN ERROR SINCE TOKEN IS EOF 
                            state = 6;
                            break;
                        }
                        if (current == '~') {
                            value = value + current;
                            state = 3;
                        } else {
                            ptr.unread(pointer);
                            colNumber--;
                            state = 2;
                        }
                    } else if (pointer >= 32 || pointer <= 126 || pointer == 9 || pointer == 10) {
                        state = 2;
                        value = value + current;
                    } else {
                        state = 6;
                    }
                    break;
                case 3:
                    return new Token(TokenName.MULTI_COMMENT, TokenType.COMMENT, value, value, null, lineNumber);
                case 4:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (pointer == -1) {
                        state = 5;
                        break;
                    }
                    if (pointer != 10) {
                        value = value + current;
                        state = 4;
                    } else {
                        value = value + current;
                        state = 5;
                    }
                    break;
                case 5:
                    value = value.trim();
                    colNumber--;
                    return new Token(TokenName.SINGLE_COMMENT, TokenType.COMMENT, value, value, null, lineNumber);
                case 6:
                    ptr.unread(pointer);
                    lookAhead = ptr;
                    colNumber--;
                    return new Token(TokenName.ERROR, TokenType.ERROR, "ERROR", "ERROR on line number (" + lineNumber + ") column number (" + colNumber + "): Invalid comment expression", null, lineNumber);
            }
        }
        return new Token();
    }

    public Token checkForAssignmentOperator() throws IOException {
        PushbackInputStream ptr = lookAhead;
        int state = 1;
        int pointer = 0;
        char current;
        String value = "";
        boolean loop = true;
        while (loop) {
            switch (state) {
                case 1:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == ':') {
                        state = 2;
                        value = value + current;
                    } else {
                        state = 4;
                    }
                    break;
                case 2:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == '=') {
                        state = 3;
                        value = value + current;
                    } else {
                        state = 5;
                    }
                    break;
                case 3:
                    lookAhead = ptr;
                    return new Token(TokenName.ASSIGNOPP, TokenType.ASSIGNMENTOP, value, value, null, lineNumber);
                case 4:
                    ptr.unread(pointer);
                    lookAhead = ptr;
                    colNumber--;
                    return new Token();
                case 5:
                    ptr.unread(pointer);
                    lookAhead = ptr;
                    //colNumber--;
                    return new Token(TokenName.ERROR, TokenType.ERROR, "ERROR", "ERROR on line number (" + lineNumber + ") column number (" + colNumber + "): Invalid assignment operator (Expected symbol: := )", null, lineNumber);
            }
        }
        return new Token();
    }

    public Token checkForLeftEnclosure() throws IOException {
        PushbackInputStream ptr = lookAhead;
        int state = 1;
        int pointer = 0;
        char current;
        String value = "";
        boolean loop = true;
        while (loop) {
            switch (state) {
                case 1:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == '(') {
                        state = 2;
                        value = value + current;
                    } else if (current == '{') {
                        state = 3;
                        value = value + current;
                    } else if (current == '[') {
                        state = 4;
                        value = value + current;
                    } else {
                        state = 5;
                        value = value + current;
                    }
                    break;
                case 2:
                    lookAhead = ptr;
                    return new Token(TokenName.LEFTPAR, TokenType.PARENTHESIS, value, value, null, lineNumber);
                case 3:
                    lookAhead = ptr;
                    return new Token(TokenName.LEFTBRACE, TokenType.BRACE, value, value, null, lineNumber);
                case 4:
                    lookAhead = ptr;
                    return new Token(TokenName.LEFTBRACKET, TokenType.BRACKET, value, value, null, lineNumber);
                case 5:
                    ptr.unread(pointer);
                    colNumber--;
                    lookAhead = ptr;
                    return new Token(TokenName.ERROR, TokenType.ERROR, "ERROR", "ERROR on line number (" + lineNumber + ") column number (" + colNumber + "): Invalid left enclosure symbol (Expected symbol(s): (,[,{ )", null, lineNumber);
            }
        }
        return new Token();
    }

    public Token checkForRightEnclosure() throws IOException {
        PushbackInputStream ptr = lookAhead;
        int state = 1;
        int pointer = 0;
        char current;
        String value = "";
        boolean loop = true;
        while (loop) {
            switch (state) {
                case 1:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == ')') {
                        state = 2;
                        value = value + current;
                    } else if (current == '}') {
                        state = 3;
                        value = value + current;
                    } else if (current == ']') {
                        state = 4;
                        value = value + current;
                    } else {
                        state = 5;
                        value = value + current;
                    }
                    break;
                case 2:
                    lookAhead = ptr;
                    return new Token(TokenName.RIGHTPAR, TokenType.PARENTHESIS, value, value, null, lineNumber);
                case 3:
                    lookAhead = ptr;
                    return new Token(TokenName.RIGHTBRACE, TokenType.BRACE, value, value, null, lineNumber);
                case 4:
                    lookAhead = ptr;
                    return new Token(TokenName.RIGHTBRACKET, TokenType.BRACKET, value, value, null, lineNumber);
                case 5:
                    ptr.unread(pointer);
                    lookAhead = ptr;
                    colNumber--;
                    return new Token(TokenName.ERROR, TokenType.ERROR, "ERROR", "ERROR on line number (" + lineNumber + ") column number (" + colNumber + "): Invalid right enclosure symbol (Expected symbol(s): ),],} )",null, lineNumber);
            }
        }
        return new Token();
    }

    public Token checkForEOL() throws IOException {
        PushbackInputStream ptr = lookAhead;
        int state = 1;
        int pointer;
        char current;
        String value = "";
        boolean loop = true;
        while (loop) {
            switch (state) {
                case 1:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == '.') {
                        state = 2;
                        value = value + current;
                    } else {
                        state = 3;
                        value = value + current;
                    }
                    break;
                case 2:
                    lookAhead = ptr;
                    return new Token(TokenName.EOL, TokenType.EOL, value, value, null, lineNumber);
                case 3:
                    lookAhead = ptr;
                    return new Token(TokenName.ERROR, TokenType.ERROR, "ERROR", "ERROR on line number (" + lineNumber + ") column number (" + colNumber + "): Invalid end-of-line symbol (Expected symbol: . )",null, lineNumber);
            }
        }
        return new Token();
    }

    public Token checkForComma() throws IOException {
        PushbackInputStream ptr = lookAhead;
        int state = 1;
        int pointer;
        char current;
        String value = "";
        boolean loop = true;
        while (loop) {
            switch (state) {
                case 1:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == ',') {
                        state = 2;
                        value = value + current;
                    } else {
                        state = 3;
                        value = value + current;
                    }
                    break;
                case 2:
                    lookAhead = ptr;
                    return new Token(TokenName.COMMA, TokenType.COMMA, value, value, null, lineNumber);
                case 3:
                    lookAhead = ptr;
                    return new Token(TokenName.ERROR, TokenType.ERROR, "ERROR", "ERROR on line number (" + lineNumber + ") column number (" + colNumber + "): Invalid multiple declaration separator symbol (Expected Symbol: , )", null, lineNumber);

            }
        }
        return new Token();
    }

    public Token checkForReservedWord() throws IOException {
        PushbackInputStream ptr = lookAhead;
        int state = 1;
        int pointer = 0;
        char current;
        String value = "";
        while (true) {
            switch (state) {
                case 1:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'V') {	//VERIFY
                        state = 2;
                        value = value + current;
                    } else if (current == 'D') {	//DIGIT + IZE and DURING, DECIMAL = 8,26,27,37,38,103,104
                        state = 8;
                        value = value + current;
                    } else if (current == 'S') {	//SENTENCE, SYMBOL = 17,37,38,101,83,102
                        state = 17;
                        value = value + current;
                    } else if (current == 'R') {	//RECEIVE
                        state = 25;
                        value = value + current;
                    } else if (current == 'T') {	//TRANSMIT AND TERMINATE
                        state = 33;
                        value = value + current;
                    } else if (current == 'I') {	//IF
                        state = 50;
                        value = value + current;
                    } else if (current == 'E') {        //ELSE, ENDIF, ELSEIF, EXECUTE, ENGAGE
                        state = 52;
                        value = value + current;
                    } else if (current == 'A') {	//AFFIRM
                        state = 69;
                        value = value + current;
                    } else if (current == 'N') {	//NEGATE
                        state = 75;
                        value = value + current;
                    } else if (current == 'O') {	//ON, OFF
                        state = 83;
                        value = value + current;
                    } else if (current == 'W') {	//WRITE
                        state = 96;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;

                case 2:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'E') {
                        state = 3;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;

                case 3:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'R') {
                        state = 4;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 4:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'I') {
                        state = 5;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 5:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'F') {
                        state = 6;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 6:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'Y') {
                        state = 16;
                        value = value + current;

                    } else {
                        state = 12;
                    }
                    break;

                case 8:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'I') {
                        state = 9;
                        value = value + current;
                    } else if (current == 'U') {
                        state = 86;
                        value = value + current;
                    } else if (current == 'E') {
                        state = 26;
                        value = value + current;
                    } 
                    else {
                        state = 12;
                    }
                    break;
                case 9:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'G') {
                        state = 10;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 10:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'I') {
                        state = 11;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 11:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'T') {
                        state = 7;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 12:
                    in.reset();
                    colNumber = goodColNum;
                    return new Token(TokenName.NOTRESWRD, TokenType.NOT_RESERVED_WORD, value, value, null, lineNumber);
                case 7:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'I') {
                        state = 13;
                        value = value + current;
                    } else if (!isLetterOrDigit(current)) {
                        ptr.unread(pointer);
                        lookAhead = ptr;
                        colNumber--;
                        return new Token(TokenName.DIGIT, TokenType.RESERVED_WORD, value, value, null, lineNumber);
                    } else {
                        in.reset();
                        colNumber = goodColNum;
                        return new Token(TokenName.NOTRESWRD, TokenType.NOT_RESERVED_WORD, value, value, null, lineNumber);
                    }
                    break;
                case 13:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'Z') {
                        state = 14;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 14:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'E') {
                        state = 15;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 15:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (!isLetterOrDigit(current)) {
                        ptr.unread(pointer);
                        lookAhead = ptr;
                        colNumber--;
                        return new Token(TokenName.DIGITIZE, TokenType.RESERVED_WORD, value, value, null, lineNumber);
                    } else {
                        in.reset();
                        colNumber = goodColNum;
                        return new Token(TokenName.NOTRESWRD, TokenType.NOT_RESERVED_WORD, value, value, null, lineNumber);
                    }
                case 16:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (!isLetterOrDigit(current)) {
                        ptr.unread(pointer);
                        lookAhead = ptr;
                        colNumber--;
                        return new Token(TokenName.VERIFY, TokenType.RESERVED_WORD, value, value, null, lineNumber);
                    } else {
                        in.reset();
                        colNumber = goodColNum;
                        return new Token(TokenName.NOTRESWRD, TokenType.NOT_RESERVED_WORD, value, value, null, lineNumber);
                    }
                case 17:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'E') {
                        state = 18;
                        value = value + current;
                    }
                    else if (current == 'Y'){
                        state = 37;
                        value = value + current;
                    }
                    else {
                            
                        state = 12;
                    }
                    break;
                case 18:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'N') {
                        state = 19;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 19:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'T') {
                        state = 20;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 20:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'E') {
                        state = 21;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 21:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'N') {
                        state = 22;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 22:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'C') {
                        state = 23;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 23:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'E') {
                        state = 24;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 24:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (!isLetterOrDigit(current)) {
                        ptr.unread(pointer);
                        lookAhead = ptr;
                        colNumber--;
                        return new Token(TokenName.SENTENCE, TokenType.RESERVED_WORD, value, value, null, lineNumber);
                    } else {
                        in.reset();
                        colNumber = goodColNum;
                        return new Token(TokenName.NOTRESWRD, TokenType.NOT_RESERVED_WORD, value, value, null, lineNumber);
                    }
                case 25:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'E') {
                        state = 26;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 26:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'C') {
                        state = 27;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 27:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'E') {
                        state = 28;
                        value = value + current;
                    } else if (current == 'I') {
                        state = 37;
                        value = value + current;
                    } 
                    else {
                        state = 12;
                    }
                    break;
                case 28:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'I') {
                        state = 29;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 29:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'V') {
                        state = 31;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 31:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'E') {
                        state = 32;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 32:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (!isLetterOrDigit(current)) {
                        ptr.unread(pointer);
                        lookAhead = ptr;
                        colNumber--;
                        return new Token(TokenName.RECEIVE, TokenType.RESERVED_WORD, value, value, null, lineNumber);
                    } else {
                        in.reset();
                        colNumber = goodColNum;
                        return new Token(TokenName.NOTRESWRD, TokenType.NOT_RESERVED_WORD, value, value, null, lineNumber);
                    }
                case 33:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'R') {
                        state = 34;
                        value = value + current;
                    } else if (current == 'E') {
                        state = 42;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 34:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'A') {
                        state = 35;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 35:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'N') {
                        state = 36;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 36:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'S') {
                        state = 37;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 37:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'M') {
                        state = 38;
                        value = value + current;
                    }
                    else {
                        state = 12;
                    }
                    break;
                case 38:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'I') {
                        state = 39;
                        value = value + current;
                    }
                    else if(current == 'B')
                    {
                        state = 101;
                        value = value + current;
                    }
                    else if(current == 'A')
                    {
                        state = 103;
                        value = value + current;
                    }
                    else {
                        state = 12;
                    }
                    break;
                case 39:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'T') {
                        state = 40;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 40:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (!isLetterOrDigit(current)) {
                        ptr.unread(pointer);
                        lookAhead = ptr;
                        colNumber--;
                        return new Token(TokenName.TRANSMIT, TokenType.RESERVED_WORD, value, value, null, lineNumber);
                    } else {
                        in.reset();
                        colNumber = goodColNum;
                        return new Token(TokenName.NOTRESWRD, TokenType.NOT_RESERVED_WORD, value, value, null, lineNumber);
                    }
                case 41:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'E') {
                        state = 42;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 42:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'R') {
                        state = 43;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 43:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'M') {
                        state = 44;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 44:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'I') {
                        state = 45;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 45:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'N') {
                        state = 46;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 46:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'A') {
                        state = 47;
                        value = value + current;
                    }

                    break;
                case 47:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'T') {
                        state = 48;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 48:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'E') {
                        state = 49;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 49:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (!isLetterOrDigit(current)) {
                        ptr.unread(pointer);
                        lookAhead = ptr;
                        colNumber--;
                        return new Token(TokenName.TERMINATE, TokenType.RESERVED_WORD, value, value, null, lineNumber);
                    } else {
                        in.reset();
                        colNumber = goodColNum;
                        return new Token(TokenName.NOTRESWRD, TokenType.NOT_RESERVED_WORD, value, value, null, lineNumber);
                    }
                case 50:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'F') {
                        state = 51;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 51:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (!isLetterOrDigit(current)) {
                        ptr.unread(pointer);
                        lookAhead = ptr;
                        colNumber--;
                        return new Token(TokenName.IF, TokenType.RESERVED_WORD, value, value, null, lineNumber);
                    } else {
                        in.reset();
                        colNumber = goodColNum;
                        return new Token(TokenName.NOTRESWRD, TokenType.NOT_RESERVED_WORD, value, value, null, lineNumber);
                    }
                case 52:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'N') {
                        state = 53;
                        value = value + current;
                    } else if (current == 'L') {
                        state = 57;
                        value = value + current;
                    } else if (current == 'X') {
                        state = 63;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 53:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'D') {
                        state = 54;
                        value = value + current;
                    } else if (current == 'G') {
                        state = 92;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 54:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'I') {
                        state = 55;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 55:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'F') {
                        state = 56;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 56:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (!isLetterOrDigit(current)) {
                        ptr.unread(pointer);
                        lookAhead = ptr;
                        colNumber--;
                        return new Token(TokenName.ENDIF, TokenType.RESERVED_WORD, value, value, null, lineNumber);
                    } else {
                        in.reset();
                        colNumber = goodColNum;
                        return new Token(TokenName.NOTRESWRD, TokenType.NOT_RESERVED_WORD, value, value, null, lineNumber);
                    }
                case 57:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'S') {
                        state = 58;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 58:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'E') {
                        state = 59;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 59:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'I') {
                        state = 60;
                        value = value + current;
                    } else if (!isLetterOrDigit(current)) {
                        ptr.unread(pointer);
                        lookAhead = ptr;
                        colNumber--;
                        return new Token(TokenName.ELSE, TokenType.RESERVED_WORD, value, value, null, lineNumber);
                    } else {
                        in.reset();
                        colNumber = goodColNum;
                        return new Token(TokenName.NOTRESWRD, TokenType.NOT_RESERVED_WORD, value, value, null, lineNumber);
                    }
                    break;
                case 60:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'F') {
                        state = 61;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 61:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (!isLetterOrDigit(current)) {
                        ptr.unread(pointer);
                        lookAhead = ptr;
                        colNumber--;
                        return new Token(TokenName.ELSEIF, TokenType.RESERVED_WORD, value, value, null, lineNumber);
                    } else {
                        in.reset();
                        colNumber = goodColNum;
                        return new Token(TokenName.NOTRESWRD, TokenType.NOT_RESERVED_WORD, value, value, null, lineNumber);
                    }
                case 63:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'E') {
                        state = 64;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 64:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'C') {
                        state = 65;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 65:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'U') {
                        state = 66;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 66:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'T') {
                        state = 67;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 67:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'E') {
                        state = 68;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 68:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (!isLetterOrDigit(current)) {
                        ptr.unread(pointer);
                        lookAhead = ptr;
                        colNumber--;
                        return new Token(TokenName.EXECUTE, TokenType.RESERVED_WORD, value, value, null, lineNumber);
                    } else {
                        in.reset();
                        colNumber = goodColNum;
                        return new Token(TokenName.NOTRESWRD, TokenType.NOT_RESERVED_WORD, value, value, null, lineNumber);
                    }
                case 69:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'F') {
                        state = 70;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 70:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'F') {
                        state = 71;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 71:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'I') {
                        state = 72;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 72:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'R') {
                        state = 73;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 73:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'M') {
                        state = 74;
                        value = value + current;

                    } else {
                        state = 12;
                    }
                    break;
                case 74:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (!isLetterOrDigit(current)) {
                        ptr.unread(pointer);
                        lookAhead = ptr;
                        colNumber--;
                        return new Token(TokenName.AFFIRM, TokenType.RESERVED_WORD, value, true, null, lineNumber);
                    } else {
                        in.reset();
                        colNumber = goodColNum;
                        return new Token(TokenName.NOTRESWRD, TokenType.NOT_RESERVED_WORD, value, value, null, lineNumber);
                    }
                case 75:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'E') {
                        state = 76;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 76:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'G') {
                        state = 77;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 77:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'A') {
                        state = 79;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 79:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'T') {
                        state = 81;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 81:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'E') {
                        state = 82;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 82: //ERROR in NEGATE
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (!isLetterOrDigit(current)) {
                        ptr.unread(pointer);
                        lookAhead = ptr;
                        colNumber--;
                        return new Token(TokenName.NEGATE, TokenType.RESERVED_WORD, value, false, null, lineNumber);
                    } else {
                        in.reset();
                        colNumber = goodColNum;
                        return new Token(TokenName.NOTRESWRD, TokenType.NOT_RESERVED_WORD, value, value, null, lineNumber);
                    }
                case 83:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'F') {
                        value = value + current;
                        pointer = ptr.read();
                        current = (char) pointer;
                        colNumber++;
                        if (current == 'F') {
                            state = 84;
                            value = value + current;
                        }
                    } else if (current == 'N') {
                        state = 85;
                        value = value + current;

                    } 
                    else if (current == 'L') {
                        state = 102;
                        value = value + current;

                    } else {
                        state = 12;
                    }
                    break;
                case 84:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (!isLetterOrDigit(current)) {
                        ptr.unread(pointer);
                        lookAhead = ptr;
                        colNumber--;
                        return new Token(TokenName.OFF, TokenType.RESERVED_WORD, value, value, null, lineNumber);
                    } else {
                        in.reset();
                        colNumber = goodColNum;
                        return new Token(TokenName.NOTRESWRD, TokenType.NOT_RESERVED_WORD, value, value, null, lineNumber);
                    }
                case 85:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (!isLetterOrDigit(current)) {
                        ptr.unread(pointer);
                        lookAhead = ptr;
                        colNumber--;
                        return new Token(TokenName.ON, TokenType.RESERVED_WORD, value, value, null, lineNumber);
                    } else {
                        in.reset();
                        colNumber = goodColNum;
                        return new Token(TokenName.NOTRESWRD, TokenType.NOT_RESERVED_WORD, value, value, null, lineNumber);
                    }
                case 86:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'R') {
                        state = 87;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 87:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'I') {
                        state = 88;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 88:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'N') {
                        state = 89;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 89:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'G') {
                        state = 90;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 90:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (!isLetterOrDigit(current)) {
                        ptr.unread(pointer);
                        lookAhead = ptr;
                        colNumber--;
                        return new Token(TokenName.DURING, TokenType.RESERVED_WORD, value, value, null, lineNumber);
                    } else {
                        in.reset();
                        colNumber = goodColNum;
                        return new Token(TokenName.NOTRESWRD, TokenType.NOT_RESERVED_WORD, value, value, null, lineNumber);
                    }
                case 92:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'A') {
                        state = 93;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 93:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'G') {
                        state = 94;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 94:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'E') {
                        state = 95;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 95:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (!isLetterOrDigit(current)) {
                        ptr.unread(pointer);
                        lookAhead = ptr;
                        colNumber--;
                        return new Token(TokenName.ENGAGE, TokenType.RESERVED_WORD, value, value, null, lineNumber);
                    } else {
                        in.reset();
                        colNumber = goodColNum;
                        return new Token(TokenName.NOTRESWRD, TokenType.NOT_RESERVED_WORD, value, value, null, lineNumber);
                    }
                case 96:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'R') {
                        state = 97;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 97:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'I') {
                        state = 98;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 98:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'T') {
                        state = 99;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 99:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'E') {
                        state = 100;
                        value = value + current;
                    } else {
                        state = 12;
                    }
                    break;
                case 100:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (!isLetterOrDigit(current)) {
                        ptr.unread(pointer);
                        lookAhead = ptr;
                        colNumber--;
                        return new Token(TokenName.WRITE, TokenType.RESERVED_WORD, value, value, null, lineNumber);
                    } else {
                        in.reset();
                        colNumber = goodColNum;
                        return new Token(TokenName.NOTRESWRD, TokenType.NOT_RESERVED_WORD, value, value, null, lineNumber);
                    }
                case 101:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'O') {
                        state = 83;
                        value = value + current;
                    }
                    else {
                        state = 12;
                    }
                    break;
                case 102:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (!isLetterOrDigit(current)) {
                        ptr.unread(pointer);
                        lookAhead = ptr;
                        colNumber--;
                        return new Token(TokenName.SYMBOL, TokenType.RESERVED_WORD, value, value, null, lineNumber);
                    } else {
                        in.reset();
                        colNumber = goodColNum;
                        return new Token(TokenName.NOTRESWRD, TokenType.NOT_RESERVED_WORD, value, value, null, lineNumber);
                    }
                case 103:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (current == 'L') {
                        state = 104;
                        value = value + current;
                    }
                    else {
                        state = 12;
                    }
                    break;
                case 104:
                    pointer = ptr.read();
                    current = (char) pointer;
                    colNumber++;
                    if (!isLetterOrDigit(current)) {
                        ptr.unread(pointer);
                        lookAhead = ptr;
                        colNumber--;
                        return new Token(TokenName.DECIMAL, TokenType.RESERVED_WORD, value, value, null, lineNumber);
                    } else {
                        in.reset();
                        colNumber = goodColNum;
                        return new Token(TokenName.NOTRESWRD, TokenType.NOT_RESERVED_WORD, value, value, null, lineNumber);
                    }
            }
        }
    }

    public void checkForIntEOF() throws IOException {
        int pointer;
        if ((pointer = lookAhead.read()) != -1) {

            System.out.println("before exit 4");
            System.exit(0);
        }
        lookAhead.unread(pointer);
    }

    public boolean isLeftEnclosure(char op) {
        return op == '(' || op == '{' || op == '[';
    }

    public boolean isRightEnclosure(char op) {
        return op == ')' || op == '}' || op == ']';
    }

    public boolean isOperator(char op) {
        return op == '+' || op == '-' || op == '*' || op == '/' || op == '^' || op == '%';
    }

    public boolean isRelOp(char op) {
        return op == '<' || op == '>' || op == '!' || op == '=';
    }

    public boolean isLogOp(char op) {
        return op == '!' || op == '&' || op == '|';
    }

    public boolean isRecognizedCharacter(char pointer) {
        return (isWhitespace(pointer) || isLetterOrDigit(pointer) || isLogOp(pointer) || isRelOp(pointer) || isOperator(pointer) || pointer == '.' || pointer == ',' || pointer == '~' || pointer == '#');
    }
    
    public Token getToken(int pointer) throws IOException {

        Token tok = new Token();
        checkForWhiteSpace();
        if (isLetter((char) pointer) && isUpperCase((char) pointer)) {
            in.mark(0);
            goodColNum = colNumber;
            tok = checkForReservedWord();
            if (tok.getTokenName().equals(TokenName.NOTRESWRD) || tok.getTokenName().equals(TokenName.ERROR)) {
                lookAhead.unread(pointer);
                colNumber--;
                tok = checkForIdentifier();
            }
        } else if (isLetter((char) pointer) || isUpperCase((char) pointer)) {
            tok = checkForIdentifier();
        } else if ((char) pointer == ':') {
            tok = checkForAssignmentOperator();
        } else if (isOperator((char) pointer)) {
            tok = checkForArithmeticOperator();
        } else if (isRelOp((char) pointer)) {
            tok = checkForRelationalOperator();
        } else if (isLogOp((char) pointer)) {
            tok = checkForLogicalOperator();
        } else if ((char) pointer == '"') {
            tok = checkForStringConstant();
        } else if ((char) pointer == '\'') {
            tok = checkForCharConstant();
        } else if (isDigit((char) pointer) || (char) pointer == '.') {
            in.mark(0);
            tok = checkForFloatConstant();
            if (tok.getTokenName().equals(TokenName.ISINTCONST)) {
                lookAhead.unread(pointer);
                colNumber--;
                tok = checkForIntConstant();
            }
            if (tok.getTokenName().equals(TokenName.ISEOL)) {
                lookAhead.unread(pointer);
                colNumber--;
                tok = checkForEOL();
            }
        } else if (isDigit((char) pointer)) {
            tok = checkForIntConstant();
        } else if (isLeftEnclosure((char) pointer)) {
            tok = checkForLeftEnclosure();
        } else if (isRightEnclosure((char) pointer)) {
            tok = checkForRightEnclosure();
        } else if ((char) pointer == '.') {
            tok = checkForEOL();
        } else if ((char) pointer == ',') {
            tok = checkForComma();
        } else if ((char) pointer == '~') {
            tok = checkForComments();
            if (tok.getTokenName().equals(TokenName.SINGLE_COMMENT)) {
                System.out.println();
            }
        } else if ((char) pointer == '#') {
            tok = checkForConcatOperator();
        } else if (!isRecognizedCharacter((char) pointer)) {
            tok = new Token(TokenName.ERROR, TokenType.ERROR, "ERROR", "ERROR on line number (" + lineNumber + ") column number (" + colNumber + "): Character unrecognized by the Lexical Analyzer", null, lineNumber);
            pointer = lookAhead.read();
            colNumber++;
        } else if (pointer == 10) {
            colNumber = 0;
        }
        return tok;
    }

    public void tokenDriver() throws IOException {
        int pointer;
        Token tok;

        pointer = lookAhead.read();
        while (pointer != -1 && (char) pointer != 'ÿ') {
            lookAhead.unread(pointer);
            tok = getToken(pointer);
            
            switch (tok.getTokenType()) {
                case RESERVED_WORD:
                    if (!SymbolTable.containsKey(tok.getLexeme()) && !SymbolTable.containsValue(tok)) {
                        SymbolTable.putIfAbsent(tok.getLexeme(), tok);
                    } else {
                        SymbolTable.replace(tok.getLexeme(), tok);
                    }
                case RELOP:
                case LOGICOP:
                case ARITHMETICOP:
                case ASSIGNMENTOP:
                case BRACE:
                case BRACKET:
                case PARENTHESIS:
                case COMMA:
                case CONCATOPP:
                case EOL:
                case BOOL_CONSTANT:
                case CHAR_CONSTANT:
                case STRING_CONSTANT:
                case INT_CONSTANT:
                case FLOAT_CONSTANT:
                    System.out.print(tok.getTokenContent() + " ");
                    break;
                case IDENTIFIER:
                    System.out.print(tok.getTokenContent() + " ");
                    if (!SymbolTable.containsKey(tok.getLexeme()) && !SymbolTable.containsValue(tok)) {
                        SymbolTable.putIfAbsent(tok.getLexeme(), tok);
                    } else {
                        SymbolTable.replace(tok.getLexeme(), tok);
                    }
                    break;

                case ERROR:
                    System.out.print("[" + tok.getValue() + "] ");
                    break;
                default:
                    break;
            }
            pointer = lookAhead.read();
        }
        
    }

    public Token driver(String absPath) throws FileNotFoundException, IOException {
        f = new File(absPath);
        source = new FileInputStream(f);
        in = new BufferedInputStream(source);
        lookAhead = new PushbackInputStream(in);
        in.mark(0);
        goodColNum = colNumber;
        
        int pointer;
        Token tok = new Token();

        pointer = lookAhead.read();
        if (pointer != -1 && (char) pointer != 'ÿ') {
            lookAhead.unread(pointer);
            tok = getToken(pointer);
            if (tok.getTokenName().equals(TokenName.WHITESPACES) || tok.getTokenName().equals(TokenName.DEFAULT)|| tok.getTokenName().equals(TokenName.SINGLE_COMMENT) || tok.getTokenName().equals(TokenName.MULTI_COMMENT)) {
                while (tok.getTokenName().equals(TokenName.WHITESPACES) || tok.getTokenName().equals(TokenName.DEFAULT) || tok.getTokenName().equals(TokenName.SINGLE_COMMENT) || tok.getTokenName().equals(TokenName.MULTI_COMMENT)) {
                    pointer = lookAhead.read();
                    if (pointer != -1 && (char) pointer != 'ÿ') {
                        lookAhead.unread(pointer);
                        tok = getToken(pointer);
                    }   
                }
            }
        }
        return tok;
    }
    
    public Token consumeToken() throws IOException {
        int pointer;
        Token tok = new Token();

        pointer = lookAhead.read();
        if (pointer != -1 && (char) pointer != 'ÿ') {
            lookAhead.unread(pointer);
            tok = getToken(pointer);
            if (tok.getTokenName().equals(TokenName.WHITESPACES) || tok.getTokenName().equals(TokenName.DEFAULT) || tok.getTokenName().equals(TokenName.SINGLE_COMMENT) || tok.getTokenName().equals(TokenName.MULTI_COMMENT)) {
                while (tok.getTokenName().equals(TokenName.WHITESPACES) || tok.getTokenName().equals(TokenName.DEFAULT) || tok.getTokenName().equals(TokenName.SINGLE_COMMENT) || tok.getTokenName().equals(TokenName.MULTI_COMMENT)) {
                    pointer = lookAhead.read();
                    if (pointer != -1 && (char) pointer != 'ÿ') {
                        lookAhead.unread(pointer);
                        tok = getToken(pointer);
                    }   
                }
            }
        }
        return tok;
    }
    
    //C:\Users\Theodore Arnel Merin\Documents\sample.txt
    //'ÿ'
    public void sourceScanner(String absPath) throws FileNotFoundException, IOException {
        f = new File(absPath);
        source = new FileInputStream(f);
        in = new BufferedInputStream(source);
        lookAhead = new PushbackInputStream(in);
        in.mark(0);
        goodColNum = colNumber;
        tokenDriver();
        //System.exit(0);
    }
}
