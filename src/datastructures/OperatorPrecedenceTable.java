package datastructures;

public class OperatorPrecedenceTable {

    public OperatorPrecedenceTable() {

    }

    public OperatorPrecedence evaluatePrecedenceArithmetic(TokenName previous, TokenName current) {
        switch (previous) {
            case SUM:
            case DIFF:
                switch (current) {
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
                    case NEGEXP_ROOT:
                        return OperatorPrecedence.LESSER;
                }
                break;
            case PROD:
            case DIV:
            case MOD:
                switch (current) {
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
                    case NEGEXP_ROOT:
                        return OperatorPrecedence.LESSER;
                }
                break;
            case POW:
                switch (current) {
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
                    case NEGEXP_ROOT:
                        return OperatorPrecedence.LESSER;
                }
                break;
            case LEFTPAR:
                switch (current) {
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
                    case NEGEXP_ROOT:
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
            case NEGEXP_ROOT:
                switch (current) {
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
                    case NEGEXP_ROOT:
                        return OperatorPrecedence.ERROR;
                }
                break;
            case DOLLAR_OPERATOR:
                switch (current) {
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
                    case NEGEXP_ROOT:
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

    public OperatorPrecedence evaluatePrecedenceLogical(TokenName previous, TokenName current) {
        switch (previous) {
            case LOGICNOT:
                switch (current) {
                    case LOGICNOT:
                    case LEFTPAR:
                        return OperatorPrecedence.LESSER;
                    case LOGICAND:
                    case LOGICOR:
                    case RIGHTPAR:
                    case DOLLAR_OPERATOR:
                        return OperatorPrecedence.GREATER;
                    case IDENTIFIER:
                    case AFFIRM:
                    case NEGATE:
                    case NOTEXP_NODE:
                    case RELATIONAL_EXPRESSION:
                        return OperatorPrecedence.DO_NOT;
                }
                break;
            case LOGICAND:
            case LOGICOR:
                switch (current) {
                    case LOGICAND:
                    case LOGICOR:
                    case RIGHTPAR:
                    case DOLLAR_OPERATOR:
                        return OperatorPrecedence.GREATER;
                    case LEFTPAR:
                    case IDENTIFIER:
                    case AFFIRM:
                    case NEGATE:
                    case NOTEXP_NODE:
                    case RELATIONAL_EXPRESSION:
                    case LOGICNOT:
                        return OperatorPrecedence.LESSER;
                }
                break;
            case LEFTPAR:
                switch (current) {
                    case LOGICAND:
                    case LOGICOR:
                    case LEFTPAR:
                    case IDENTIFIER:
                    case AFFIRM:
                    case NEGATE:
                    case NOTEXP_NODE:
                    case RELATIONAL_EXPRESSION:
                    case LOGICNOT:
                        return OperatorPrecedence.LESSER;
                    case RIGHTPAR:
                        return OperatorPrecedence.EQUAL;
                    case DOLLAR_OPERATOR:
                        return OperatorPrecedence.ERROR;
                }
                break;
            case RIGHTPAR:
            case IDENTIFIER:
            case AFFIRM:
            case NEGATE:
            case NOTEXP_NODE:
            case RELATIONAL_EXPRESSION:
                switch (current) {
                    case LOGICAND:
                    case LOGICOR:
                    case RIGHTPAR:
                    case DOLLAR_OPERATOR:
                        return OperatorPrecedence.GREATER;
                    case LEFTPAR:
                    case IDENTIFIER:
                    case AFFIRM:
                    case NEGATE:
                    case NOTEXP_NODE:
                    case RELATIONAL_EXPRESSION:
                        return OperatorPrecedence.ERROR;
                }
                break;
            case DOLLAR_OPERATOR:
                switch (current) {
                    case LOGICAND:
                    case LOGICOR:
                    case LEFTPAR:
                    case IDENTIFIER:
                    case AFFIRM:
                    case NEGATE:
                    case NOTEXP_NODE:
                    case RELATIONAL_EXPRESSION:
                    case LOGICNOT:
                        return OperatorPrecedence.LESSER;
                    case RIGHTPAR:
                    case DOLLAR_OPERATOR:
                        return OperatorPrecedence.ERROR;
                }
                break;
        }
        return OperatorPrecedence.ERROR;
    }

    public OperatorPrecedence evaluatePrecedenceRelational(TokenName previous, TokenName current) {
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
                    case RIGHTPAR:
                    case DOLLAR_OPERATOR:
                        return OperatorPrecedence.GREATER;
                    case LEFTPAR:
                    case IDENTIFIER:
                    case CONSINT:
                    case CONSFLOAT:
                    case LOGICAL_EXPRESSION:
                    case ARITHMETIC_EXPRESSION:
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
                    case LEFTPAR:
                    case IDENTIFIER:
                    case CONSINT:
                    case CONSFLOAT:
                    case LOGICAL_EXPRESSION:
                    case ARITHMETIC_EXPRESSION:
                        return OperatorPrecedence.LESSER;
                    case OPEQUAL:
                    case OPNOT:
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
                    case LEFTPAR:
                    case OPEQUAL:
                    case OPNOT:
                    case IDENTIFIER:
                    case CONSINT:
                    case CONSFLOAT:
                    case LOGICAL_EXPRESSION:
                    case ARITHMETIC_EXPRESSION:
                        return OperatorPrecedence.LESSER;
                    case RIGHTPAR:
                        return OperatorPrecedence.EQUAL;
                    case DOLLAR_OPERATOR:
                        return OperatorPrecedence.GREATER;
                }
                break;
            case RIGHTPAR:
                switch (current) {
                    case OPGREAT:
                    case OPLESS:
                    case OPGREQ:
                    case OPLEQ:
                    case OPEQUAL:
                    case OPNOT:
                    case RIGHTPAR:
                    case DOLLAR_OPERATOR:
                        return OperatorPrecedence.GREATER;
                    case LEFTPAR:
                    case IDENTIFIER:
                    case CONSINT:
                    case CONSFLOAT:
                    case LOGICAL_EXPRESSION:
                    case ARITHMETIC_EXPRESSION:
                        return OperatorPrecedence.ERROR;
                }
                break;
            case IDENTIFIER:
            case CONSINT:
            case CONSFLOAT:
            case LOGICAL_EXPRESSION:
            case ARITHMETIC_EXPRESSION:
                switch(current) {
                    case OPGREAT:
                    case OPLESS:
                    case OPGREQ:
                    case OPLEQ:
                    case OPEQUAL:
                    case OPNOT:
                    case RIGHTPAR:
                    case DOLLAR_OPERATOR:
                        return OperatorPrecedence.GREATER;
                    case LEFTPAR:
                    case IDENTIFIER:
                    case CONSINT:
                    case CONSFLOAT:
                    case LOGICAL_EXPRESSION:
                    case ARITHMETIC_EXPRESSION:
                        return OperatorPrecedence.ERROR;
                }
                break;
            case DOLLAR_OPERATOR:
                switch(current) {
                    case OPGREAT:
                    case OPLESS:
                    case OPGREQ:
                    case OPLEQ:
                    case OPEQUAL:
                    case OPNOT:
                    case LEFTPAR:
                    case IDENTIFIER:
                    case CONSINT:
                    case CONSFLOAT:
                    case LOGICAL_EXPRESSION:
                    case ARITHMETIC_EXPRESSION:
                        return OperatorPrecedence.LESSER;
                    case RIGHTPAR:
                    case DOLLAR_OPERATOR:
                        return OperatorPrecedence.ERROR;
                }
                break;
        }
        return OperatorPrecedence.ERROR;
    }
}
