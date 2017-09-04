/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package parser.Expressions;
import parser.Tokenizer.TokenList;
import parser.Shared.Types.TokenType;


/**
 *
 * @author Utilisateur
 */
public class Expression extends Statement {
    
    
    public String Value;
    public Expression Left;
    public Expression Right;
    public TokenType Operator;
    public TokenList Tokens;

    public Expression(){
        Tokens = new TokenList();
    }
    
    public Expression(TokenList tokens)
    {
    this.Tokens = tokens;
    this.Value = tokens.toString();
    this.Operator = TokenType.UNKNOWN;
    }
    
    public Expression(TokenList tokens, TokenType op)
    {
        this.Tokens = tokens;
        this.Operator = op;
        this.Value = tokens.toString();
       
    }
    
    
}
   
