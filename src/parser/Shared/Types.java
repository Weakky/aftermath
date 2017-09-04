/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser.Shared;

/**
 *
 * Contains all the tokens types.
 */
public class Types {
    
    public enum TokenType {
        KEYWORD,
        STRING,
        NUMBER,
        BOOL,
        PLUS,
        MINUS,
        MULT,
        DIV,
        MOD,
        AND,
        OR,
        XOR,
        EQUAL,
        NQ,
        GR,
        LE,
        PAREN_OPEN,
        PAREN_CLOSE,
        CBRACKET_OPEN,
        CBRACKET_CLOSE,
        BRACKET_OPEN,
        BRACKET_CLOSE,
        GROUP,
        CONCAT,
        COMMA,
        DOT,
        COLON,
        
        SEMICOLON,
        FUNCTION,
        ENDFUNCTION,
        
        IF,
        THEN,
        ELSE,
        ENDIF,
        
        SWITCH,
        CASE,
        ENDSWITCH,
        
        FOR,
        ENDFOR,
        
        RETURN,
        CRLF,
        WS,
        EOF,
        NOTHING,
        NEGATION,
        POSITIVE,

        VARIABLE,
        DELIMITER,
        ISEQUAL,
        ISNOTEQUAL,
        ISGREATER,
        ISGREATEROREQUAL,
        ISLESSER,
        ISLESSEROREQUAL,
        UNKNOWN, BREAK, WHILE, ENDWHILE, FROM, TO, IS, RESOLVE, POWER, STEP
        
    }
    
    
    public enum PrecedenceType
    {
        None(12),
        Unary(11),
        Power(10),
        IntDiv(9),
        Div(9),
        Mult(9),
        Mod(9),
        Plus(8),
        Minus(8),
        RelationalIs(7),
        RelationalGreater(6),
        RelationalLesser(6),
        RelationalNotEqual(5),
        RelationalEqual(5),
        LogicalXor(4),
        LogicalAnd(3),
        LogicalOr(2),
        Delimiter(1);
        
        private final int value;
        
        private PrecedenceType(int value)
        {
        this.value = value;
        }
        
        public int getValue() {
		return this.value;
	}
        
    }
   
    
    
}
