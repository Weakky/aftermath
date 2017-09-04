/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser.Expressions;

/**
 *
 * @author Utilisateur
 */
public class Sequence extends Statement {
    
    public Statement First;
    public Statement Second;
    
    public Sequence(){}
      
    public Sequence(Statement firstSequence)
    {
       First = firstSequence;
    }
    
    public Sequence(Statement firstSequence, Statement secondSequence)
    {
        First = firstSequence;
        Second = secondSequence;
    }
    
}
