package datastructures;

public class OperatorPrecedenceTable {
    
    public OperatorPrecedenceTable() {
        
    }
    public OperatorPrecedence evaluatePrecedenceArithmetic(TokenName previous, TokenName current) {
        switch(previous) {
            case SUM:
            case DIFF:
                switch(current) {
                    case SUM:
                    case DIFF:
                    case RIGHTPAR:
                    case DOLLAR_OPERATOR:
                        return OperatorPrecedence.GREATER;
                    case PROD:
                    case DIV:
                    case MOD:
                    case POW:
                    case LEFTPAR:
                    case IDENTIFIER:
                    case CONSINT:
                    case CONSFLOAT:
                    case PREINC:
                    case PREDEC:
                    case POSTINC:
                    case POSTDEC: 
                    case NEGINT:
                    case NEGFLOAT:
                    case NEGIDENTIFIER:
                        return OperatorPrecedence.LESSER;
                }
                break;
            case PROD:
            case DIV:
            case MOD:
                switch(current) {
                    case SUM:
                    case DIFF:
                    case PROD:
                    case DIV:
                    case MOD:
                    case RIGHTPAR:
                    case DOLLAR_OPERATOR:
                        return OperatorPrecedence.GREATER;
                    case POW:
                    case LEFTPAR:
                    case IDENTIFIER:
                    case CONSINT:
                    case CONSFLOAT:
                    case PREINC:
                    case PREDEC:
                    case POSTINC:
                    case POSTDEC:
                    case NEGINT:
                    case NEGFLOAT:
                    case NEGIDENTIFIER:
                        return OperatorPrecedence.LESSER;
                }
                break;
            case POW:
                switch(current) {
                    case SUM:
                    case DIFF:
                    case PROD:
                    case DIV:
                    case MOD:
                    case RIGHTPAR:
                    case DOLLAR_OPERATOR:
                    case POW:
                        return OperatorPrecedence.GREATER;
                    case LEFTPAR:
                    case IDENTIFIER:
                    case CONSINT:
                    case CONSFLOAT:
                    case PREINC:
                    case PREDEC:
                    case POSTINC:
                    case POSTDEC:
                    case NEGINT:
                    case NEGFLOAT:
                    case NEGIDENTIFIER:
                        return OperatorPrecedence.LESSER;
                }
                break;
            case LEFTPAR:
                switch(current) {
                    case SUM:
                    case DIFF:
                    case PROD:
                    case DIV:
                    case MOD:
                    case POW:
                    case LEFTPAR:
                    case IDENTIFIER:
                    case CONSINT:
                    case CONSFLOAT:
                    case PREINC:
                    case PREDEC:
                    case POSTINC:
                    case POSTDEC:
                    case NEGINT:
                    case NEGFLOAT:
                    case NEGIDENTIFIER:
                        return OperatorPrecedence.LESSER;
                    case RIGHTPAR:
                        return OperatorPrecedence.EQUAL;
                    case DOLLAR_OPERATOR:
                        return OperatorPrecedence.ERROR;
                }
                break;
            case RIGHTPAR:
            case IDENTIFIER:
            case CONSINT:
            case CONSFLOAT:
            case PREINC:
            case PREDEC:
            case POSTINC:
            case POSTDEC:
            case NEGINT:
            case NEGFLOAT:
            case NEGIDENTIFIER:
                switch(current) {
                    case SUM:
                    case DIFF:
                    case PROD:
                    case DIV:
                    case MOD:
                    case POW:
                    case RIGHTPAR:    
                    case DOLLAR_OPERATOR:
                        return OperatorPrecedence.GREATER;
                    case LEFTPAR:
                    case IDENTIFIER:
                    case CONSINT:
                    case CONSFLOAT:
                    case PREINC:
                    case PREDEC:
                    case POSTINC:
                    case POSTDEC:
                    case NEGINT:
                    case NEGFLOAT:
                    case NEGIDENTIFIER:
                        return OperatorPrecedence.ERROR;
                }
                break;
            case DOLLAR_OPERATOR:
                switch(current) {
                    case SUM:
                    case DIFF:
                    case PROD:
                    case DIV:
                    case MOD:
                    case POW:
                    case LEFTPAR:
                    case IDENTIFIER:
                    case CONSINT:
                    case CONSFLOAT:
                    case PREINC:
                    case PREDEC:
                    case POSTINC:
                    case POSTDEC:   
                    case NEGINT:
                    case NEGFLOAT:
                    case NEGIDENTIFIER:
                        return OperatorPrecedence.LESSER;
                    case RIGHTPAR:
                    case DOLLAR_OPERATOR:
                        return OperatorPrecedence.ERROR;
                }
                break;
            default:
                return OperatorPrecedence.ERROR;
        }
        return OperatorPrecedence.ERROR;
    }
    
     public OperatorPrecedence evaluatePrecedenceRelationalAndLogical(TokenName previous, TokenName current) {
         switch (previous) {
            case OPGREAT:
            case OPLESS:
            case OPGREQ:
            case OPLEQ:
                switch (current) {
                    case OPGREAT:
                    case OPLESS:
                    case OPGREQ:
                    case OPLEQ:
                    case OPEQUAL:
                    case OPNOT:
                    case LOGICAND:
                    case LOGICOR:
                    case RIGHTPAR:
                    case DOLLAR_OPERATOR: 
                        return OperatorPrecedence.GREATER;
                    case LOGICNOT:
                    case LEFTPAR:
                        return OperatorPrecedence.LESSER;
                 }
                 break;
            case OPEQUAL:
            case OPNOT:
                switch (current) {
                    case OPGREAT:
                    case OPLESS:
                    case OPGREQ:
                    case OPLEQ:
                    case LOGICNOT:
                    case LEFTPAR:
                        return OperatorPrecedence.LESSER;
                    case OPEQUAL:
                    case OPNOT:
                    case LOGICAND:
                    case LOGICOR:
                    case RIGHTPAR:
                    case DOLLAR_OPERATOR: 
                        return OperatorPrecedence.GREATER;
                }
                break;
            case LOGICNOT:
                if (current.equals(TokenName.LEFTPAR)) {
                    return OperatorPrecedence.LESSER;
                } else {
                    return OperatorPrecedence.GREATER;
                }
            case LOGICAND:
            case LOGICOR:
                switch (current) {
                    case OPGREAT:
                    case OPLESS:
                    case OPGREQ:
                    case OPLEQ:
                    case OPEQUAL:
                    case OPNOT:
                    case LOGICNOT:
                    case LEFTPAR:
                        return OperatorPrecedence.LESSER;
                    case LOGICAND:
                    case LOGICOR:
                    case RIGHTPAR:
                    case DOLLAR_OPERATOR:
                        return OperatorPrecedence.GREATER;
                }
                break;
            case LEFTPAR:
                switch (current) {
                    case OPGREAT:
                    case OPLESS:
                    case OPGREQ:
                    case OPLEQ:
                    case OPEQUAL:
                    case OPNOT:
                    case LOGICNOT:
                    case LEFTPAR:
                    case LOGICAND:
                    case LOGICOR:
                        return OperatorPrecedence.GREATER;
                    case RIGHTPAR:
                        return OperatorPrecedence.EQUAL;
                    case DOLLAR_OPERATOR:
                        return OperatorPrecedence.ERROR;
                }
                break;
            case RIGHTPAR:
                if (current.equals(TokenName.LEFTPAR)) {
                    return OperatorPrecedence.ERROR;
                } else {
                    return OperatorPrecedence.GREATER;
                }
            case DOLLAR_OPERATOR:
                switch (current) {
                    case OPGREAT:
                    case OPLESS:
                    case OPGREQ:
                    case OPLEQ:
                    case OPEQUAL:
                    case OPNOT:
                    case LOGICNOT:
                    case LEFTPAR:
                    case LOGICAND:
                    case LOGICOR:
                        return OperatorPrecedence.LESSER;
                    case RIGHTPAR:
                    case DOLLAR_OPERATOR:
                        return OperatorPrecedence.ERROR;
                }
                break;
            default:
                return OperatorPrecedence.ERROR;
        }
        return OperatorPrecedence.ERROR;
    }
}
