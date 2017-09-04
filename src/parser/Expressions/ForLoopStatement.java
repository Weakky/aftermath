package parser.Expressions;

/**
 * Created by Utilisateur on 29/09/2014.
 */
public class ForLoopStatement extends Statement {


    public Statement StartStatement;
    public Expression LimitExpression;
    public Expression IncreaseExpression;
    public Statement BlockStatement;

    public ForLoopStatement(){}

    public ForLoopStatement(Statement startStatement)
    {
        StartStatement = startStatement;
    }
}
