/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser.Tokenizer;

import java.util.regex.*;
import parser.Shared.Types.TokenType;

/**
 *
 * @author Utilisateur
 */
public class TokenPattern {
    
    public Pattern p;
    public TokenType type;
    
    
    public TokenPattern(String patternToken, TokenType typeToken)
    {
        this.p = Pattern.compile(String.format("^%s", patternToken), Pattern.DOTALL); //MATCH THE BEGINNING OF THE STRING ONLY!! ("^")
        this.type = typeToken;
    }
    
}
