/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser.Tokenizer;

import parser.Shared.Types;

/**
 *
 * @author Utilisateur
 */
public class Token {
    
    public String Value;
    public Types.TokenType Type;
    public int Length;
   // public int Index;
    //public int Position;
    
    public Token(){
   
    }
    
    public Token(String _value, Types.TokenType _type){
        
        this.Value = _value;
        this.Type = _type;
        this.Length = _value.length();
        
    }
    
}
