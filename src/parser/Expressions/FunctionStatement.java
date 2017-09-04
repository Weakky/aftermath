package parser.Expressions;

/**
 * Created by Utilisateur on 24/03/2015.
 */
public class FunctionStatement extends Statement {

    public Expression Value;

    public FunctionStatement(){}

    public FunctionStatement(Expression expressionValue){
        Value = expressionValue;
    }


}
