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
public class IfStatement extends Statement {
    
    public Expression ConditionalExpression;
    public Statement TruePart;
    public Statement FalsePart;
    
    public IfStatement(){}
    
    public IfStatement(Expression value)
    {
     ConditionalExpression = value;
    }
    
    public IfStatement(Expression value, Statement truePart)
    {
        ConditionalExpression = value;
        TruePart = truePart;
    }
    
     public IfStatement(Expression value, Statement truePart, Statement falsePart)
    {
        ConditionalExpression = value;
        TruePart = truePart;
        FalsePart = falsePart;
    }
    
}
