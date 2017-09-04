/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser.Expressions;

/**
 *
 * Hold an assignment. (x = y)
 */
public class Assignment extends Statement {
    
    public String Name;
    public Expression Value;
    
    
    public Assignment(String assignName)
    {
        this.Name = assignName;
    }
    
    
    public Assignment(String assignName, Expression assignValue)
    {
        this.Name = assignName;
        this.Value = assignValue;
    }
    
}
