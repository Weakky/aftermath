/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser.Expressions;

import java.util.HashMap;

/**
 *
 * @author Utilisateur
 */
public class SwitchStatement extends Statement {
    
    public Expression TestedExpression;
    public HashMap<Expression, Statement> Candidates;
    
    public SwitchStatement(){
    Candidates = new HashMap<>();
    }    
}

