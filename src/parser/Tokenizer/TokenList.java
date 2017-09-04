/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser.Tokenizer;
import java.util.ArrayList;
import java.util.Arrays;
import parser.Shared.Types.TokenType;

/**
 * Hold an array of token, with additional helper functions.
 * 
 */
public class TokenList extends ArrayList<Token> {
    
    
    public TokenList(){
    }
 
    public TokenList(Token[] tokens){ 
        this.addAll(Arrays.asList(tokens)); 
    }
    
    public TokenList(ArrayList<Token> tokens){ 
        this.addAll(tokens); 
    }
    
    
   public boolean startsWith(TokenType T)
   {
       return this.get(0).Type == T;
   }
  
   public boolean endsWith(TokenType T)
   {
       return this.get(this.size() - 1).Type == T;
   }  
   

   public TokenList getTokensBetweenIndex(int index)
   {
      return getTokensBetweenIndex(index, this.size() - 1);
       }

   public TokenList getTokensBetweenIndex(int index, int finalIndex)
   {
       TokenList buffer = new TokenList();
       
       for (int i = index; i <= finalIndex; i++)
       {
           buffer.add(this.get(i));
       }
       
       return buffer;
       }

    public TokenList getTokensBetween(int index, TokenType endType){
        TokenList outputList = new TokenList();
        TokenList tokenList = getTokensBetweenIndex(index);
        int tokenIndex = 0;

        while(tokenList.get(tokenIndex).Type != endType){
            outputList.add(tokenList.get(tokenIndex));
            tokenIndex++;
        }

        return outputList;
    }

    public int indexFrom(TokenType type){
        for (int i = 0; i < this.size(); i++){
            if (this.get(i).Type == type){
                return i;
            }
        }

        return -1;
    }
   
   
   public TokenList AsOne(TokenType type)
   {
       StringBuilder builder = new StringBuilder();
        for(Token t : this) {
             builder.append(t.Value);
        }
      return new TokenList(new Token[] {new Token(builder.toString(), type)});
   }
   
   
   @Override
   public String toString()
   {
       StringBuilder builder = new StringBuilder();
        for(Token t : this) {
             builder.append(t.Value);
        }
      return builder.toString();
   }
      
  
       
   }

 
   
