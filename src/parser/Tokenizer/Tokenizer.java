/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser.Tokenizer;

import java.util.ArrayList;
import java.util.regex.Matcher;
import parser.Shared.Types.TokenType;

/**
 *
 * Tokenize a string expression into a TokenArray
 */
public class Tokenizer {
    
    
    public String InputString;
    private final TokenPattern[] patterns = RegexPresets.getTokensPattern();

    public Tokenizer(){}
      
    public Tokenizer(String source){
        
        this.InputString = source;
        
    }
    
    public TokenList tokenize()
    {
        
        ArrayList<Token> output = new ArrayList<>();
        int matchLength =0;
    
        do{
            
        for(TokenPattern tokenPattern: patterns)
        {
            
            Matcher m = tokenPattern.p.matcher(InputString);
            
            if (m.find())
            {
                String isolatedToken = m.group().trim(); //get the matched string from the regex pattern
                
                matchLength = isolatedToken.length(); 
                
                if(tokenPattern.type == TokenType.CRLF || tokenPattern.type == TokenType.WS){matchLength = 1;}
                
                InputString = InputString.substring(matchLength);   //Get the index of the matched string, and substring it from the sequence.
                
                //Escape WhiteSpace Token.
                if (tokenPattern.type != TokenType.WS){output.add(new Token(isolatedToken, tokenPattern.type));}
                
                    break;
            }              
        }
        
    }while(InputString.length() != 0);
        
    return new TokenList(output);
    }
    
}
