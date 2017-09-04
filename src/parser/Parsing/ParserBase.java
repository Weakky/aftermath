/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser.Parsing;

import java.util.ArrayList;
import java.util.Arrays;
import parser.Shared.Types;
import parser.Shared.Types.TokenType;
import parser.Tokenizer.Token;
import parser.Tokenizer.TokenList;

public abstract class ParserBase {
    
    public TokenList Tokens; //Hold the tokens array that will be parsed
    public int Position; //Hold the index position of the token array
    
    //test a sequence of token to check what type of statement it is
    public boolean testSequence(Types.TokenType... args)
    {
        if ((Position + args.length) > Tokens.size()){return false;} //TODO: Continue if statement
        
        int tokensCount = args.length;          
        for (int i = 0; i < tokensCount; i ++)
        {
            Token token = Tokens.get(Position + i);
            Types.TokenType tokenType = (Types.TokenType)token.Type;
            
            
            if (tokenType != args[i]){return false;}
        }
        return true;
    }
     
    //get the current token in the stream
    public Token lookAhead(int skipToken)
    {
        if ((Position + skipToken) >= Tokens.size()){return new Token("", Types.TokenType.EOF);}   
        return Tokens.get(Position + skipToken);
    }
    
    public Token lookAhead()
    {
       return lookAhead(0);
    }
    
    //get the current token in the stream, and increase the index
    public Token lookAheadAndPush(int skipToken)
    {
        if ((Position + skipToken) >= Tokens.size()){return new Token("", Types.TokenType.EOF);}
        Token currentToken = Tokens.get(Position + skipToken);
        Position += skipToken + 1;
        return currentToken;
    }
    
    public Token lookAheadAndPush()
    {
        return lookAheadAndPush(0);
    }
    
    //create a token array from the current index, to a specified token type.
        public TokenList ToArray(Types.TokenType endType, int skipToken)
    {
        Token T = lookAheadAndPush(skipToken);
        TokenList output = new TokenList();
        
        while (T.Type != endType)
        {
            output.add(T);
            T = lookAheadAndPush();
            
        }
        
        return output;
    }
        //escape all tokens until it reaches a specified token type
        public void escapeTokenUntilType(TokenType untilType)
        {
            while (lookAhead().Type == untilType)
            {
                lookAheadAndPush();
            }
        }
        
        public void escapeAllTokens(TokenType... args)
        {
            ArrayList<TokenType> types;
        types = new ArrayList<>(Arrays.asList(args));
            
            do
            {
                lookAheadAndPush();
            }while (types.contains(lookAhead().Type));
        }
    
}
