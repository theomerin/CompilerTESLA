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
