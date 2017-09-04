/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser.Tokenizer;

import java.util.ArrayList;
import parser.Shared.Types.TokenType;

/**
 *
 * @author Utilisateur
 */
public class RegexPresets {
    
    public static ArrayList<TokenPattern> patternsList = new ArrayList<TokenPattern>();
    
    
    public static TokenPattern[] getTokensPattern()
    {
        
        patternsList.add(new TokenPattern("(>=|=>)", TokenType.ISGREATEROREQUAL));
        patternsList.add(new TokenPattern("(<=|=<)", TokenType.ISLESSEROREQUAL));       
        patternsList.add(new TokenPattern("(!=)", TokenType.ISNOTEQUAL));
        patternsList.add(new TokenPattern("(==)", TokenType.ISEQUAL));
        patternsList.add(new TokenPattern("(>)", TokenType.ISGREATER));
        patternsList.add(new TokenPattern("(<)", TokenType.ISLESSER));
        
        patternsList.add(new TokenPattern("(=)", TokenType.EQUAL));
        patternsList.add(new TokenPattern("\\+", TokenType.PLUS));
        patternsList.add(new TokenPattern("\\-", TokenType.MINUS));
        patternsList.add(new TokenPattern("\\*", TokenType.MULT));
        patternsList.add(new TokenPattern("\\/", TokenType.DIV));
        patternsList.add(new TokenPattern("\\,", TokenType.COMMA));
        patternsList.add(new TokenPattern("\\^", TokenType.POWER));
        patternsList.add(new TokenPattern("\\(", TokenType.PAREN_OPEN));
        patternsList.add(new TokenPattern("\\)", TokenType.PAREN_CLOSE));

        patternsList.add(new TokenPattern(("\\b(function)\\b"), TokenType.FUNCTION));
        patternsList.add(new TokenPattern(("\\b(end)\\s(function)\\b"), TokenType.ENDFUNCTION));
        patternsList.add(new TokenPattern(("\\b(return)\\b"), TokenType.RETURN));
        patternsList.add(new TokenPattern("\\b(if)\\b", TokenType.IF));
        patternsList.add(new TokenPattern("\\b(then)\\b", TokenType.THEN));
        patternsList.add(new TokenPattern("\\b(else)\\b", TokenType.ELSE));
        patternsList.add(new TokenPattern("\\b(end)\\s(if)\\b", TokenType.ENDIF));
        patternsList.add(new TokenPattern("\\b(switch)\\b", TokenType.SWITCH));
        patternsList.add(new TokenPattern("\\b(is)\\b", TokenType.IS));
        patternsList.add(new TokenPattern("\\b(to)\\b", TokenType.TO));
        patternsList.add(new TokenPattern("\\b(step)\\b", TokenType.STEP));
        patternsList.add(new TokenPattern("\\b(case)\\b", TokenType.CASE));
        patternsList.add(new TokenPattern("\\b(end)\\s(switch)\\b", TokenType.ENDSWITCH));
        patternsList.add(new TokenPattern("\\b(break)\\b", TokenType.BREAK));
        patternsList.add(new TokenPattern("\\b(while)\\b", TokenType.WHILE));
        patternsList.add(new TokenPattern("\\b(end)\\s(while)\\b", TokenType.ENDWHILE));
        patternsList.add(new TokenPattern("\\:", TokenType.COLON));
        patternsList.add(new TokenPattern("\\b(resolve)\\b", TokenType.RESOLVE));
 

        patternsList.add(new TokenPattern("\\b(for)\\b", TokenType.FOR));
        patternsList.add(new TokenPattern("\\b(end)\\s(for)\\b", TokenType.ENDFOR));

        patternsList.add((new TokenPattern("([\"'])(?:(?=(\\\\?))\\2.)*?\\1", TokenType.STRING)));
        patternsList.add(new TokenPattern("\\b(true|false)\\b", TokenType.BOOL));
        
        patternsList.add(new TokenPattern("[a-zA-Z][a-zA-Z0-9_]*", TokenType.KEYWORD));
        patternsList.add(new TokenPattern("[0-9\\.]+", TokenType.NUMBER));
        patternsList.add(new TokenPattern("\\r?\\n", TokenType.CRLF));
        patternsList.add(new TokenPattern("\\;", TokenType.SEMICOLON));
        patternsList.add(new TokenPattern("\\s", TokenType.WS));
        
        
        
        
        
        
        return patternsList.toArray(new TokenPattern[patternsList.size()]);
    }
    
    
}
