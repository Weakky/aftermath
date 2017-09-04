/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser.Parsing;

import Builder.Engine;
import Builder.Evaluator;
import parser.Expressions.*;
import parser.Shared.Helpers;
import parser.Shared.ScriptException;
import parser.Tokenizer.Token;
import parser.Tokenizer.TokenList;
import parser.Shared.Types;
import parser.Shared.Types.PrecedenceType;
import parser.Shared.Types.TokenType;

/**
 *
 * TODO: Implement Logical operands (&&, || etc..)
 */
public class AstParser extends ParserBase {

//region Constructor

    private Engine ScriptEngine;

    public AstParser(){}
    public AstParser(Engine scriptEngine, TokenList parserTokens)
    {
        this.ScriptEngine = scriptEngine;
        this.Tokens = parserTokens;

    }


    //endregion


    public Evaluator process() throws ScriptException {

        Evaluator outputScript = new Evaluator(ScriptEngine);
        int index = 0;

        do{

            if(testSequence(TokenType.FUNCTION, TokenType.KEYWORD, TokenType.PAREN_OPEN)){

                Method myMethod = new Method();
                myMethod.Name = lookAheadAndPush(1).Value;


                for(Token t: ToArray(TokenType.PAREN_CLOSE, 0)){

                    if(t.Type == TokenType.KEYWORD){
                        myMethod.addParam(index, t.Value);
                        index += 1;
                    }

                }

                escapeTokenUntilType(TokenType.CRLF);

                if(lookAhead().Type != TokenType.ENDFUNCTION){myMethod.Statements = parseStatement();}
                outputScript.Methods.add(myMethod);

                escapeAllTokens(TokenType.ENDFUNCTION, TokenType.CRLF);
            }

        }while(lookAhead().Type != TokenType.EOF);


        return outputScript;
    }

//region Public Methods

    //does the actual parsing.

    private static Expression parseExpression(Expression e) throws ScriptException
    {
        if (e.Tokens.size() == 0){throw new ScriptException("No tokens to parse");}

        //Hold the current token in the token stream.
        Token currentToken;

        //Hold the new precedence of each token
        PrecedenceType newPrec = PrecedenceType.None;

        //Hold the lowest precedence ; which is the best one to evaluate first.
        PrecedenceType bestPrec = PrecedenceType.None;

        //Hold the actual token with lowest precedence
        Token bestToken = new Token("", TokenType.NOTHING);

        //Hold the bestIndex of the best token
        int bestIndex = 0;

        //Hold the parentheses count to make sure they have been closed properly
        int parenthesesCount = 0;


        boolean isUnary;
        boolean nextUnary = true;
       /*isUnary is only true: - if the token is the first one in the stream "-a+b"
                                - if a parenthesis was opened "a+b(-c)"
                                - if the last token was an operand "a+b*-c"
        else, it will be considered as a MINUS token.
        */



        for (int i = 0; i < e.Tokens.size(); i ++)
        {
            isUnary = nextUnary;

            currentToken = e.Tokens.get(i);

            //Escape all tokens if there are between parentheses

            if(currentToken.Type == TokenType.PAREN_OPEN)
            {
                nextUnary = true;
                parenthesesCount++;

            }else if (currentToken.Type == TokenType.PAREN_CLOSE)
            {
                nextUnary = false;
                if (parenthesesCount < 0) throw new ScriptException("Unexpected ')'");
                parenthesesCount--;

            }else if (parenthesesCount == 0){
                if (Helpers.isOperator(currentToken.Value)){

                    nextUnary = true;
                    newPrec = evaluatePrecedenceType(currentToken, isUnary);

                    //get the lowest precedence, the token associated to it, and its index.
                    if (bestPrec.getValue() >= newPrec.getValue()){
                        bestPrec = newPrec;
                        bestToken = currentToken;
                        bestIndex = i;
                    }

                }
                else{
                    nextUnary = false;
                }
            }

        }

        //Check if parentheses were properly closed.
        if (parenthesesCount != 0){
            throw new ScriptException("Missing parenthesis.");
        }


        //Parse Unary expressions
        if (bestPrec == PrecedenceType.Unary)
        {
            if (e.Tokens.startsWith(TokenType.MINUS))
            {
                e.Operator = TokenType.NEGATION;
                e.Left = parseExpression(new Expression(e.Tokens.getTokensBetweenIndex(1), TokenType.MINUS));

            }else{

                e.Operator = TokenType.POSITIVE;
                e.Left = parseExpression(new Expression(e.Tokens.getTokensBetweenIndex(1), TokenType.PLUS));
            }

            //Parse relational expressions (for instance: a < b)
        }else if (Helpers.isRelational(bestToken.Value)){

            e.Operator = bestToken.Type;
            e.Left = parseExpression(new Expression(e.Tokens.getTokensBetweenIndex(0, bestIndex - 1)));
            e.Right = parseExpression(new Expression(e.Tokens.getTokensBetweenIndex(bestIndex + 1)));

        }

        else if (Helpers.isArithmetic(bestToken.Value))
        {
            //Split the expression into two parts around the operator having the lowest precedence
            //a+b*c => Left part: a (PLUS expression)
            //         Right part: b*c (MULT expression)

            e.Operator = bestToken.Type;
            e.Left = parseExpression(new Expression(e.Tokens.getTokensBetweenIndex(0, bestIndex - 1)));
            e.Right = parseExpression(new Expression(e.Tokens.getTokensBetweenIndex(bestIndex + 1)));


        }else if (bestToken.Type == TokenType.COMMA) {

            e.Operator = TokenType.DELIMITER;
            e.Left = parseExpression(new Expression(e.Tokens.getTokensBetweenIndex(0, bestIndex - 1)));
            e.Right = parseExpression(new Expression(e.Tokens.getTokensBetweenIndex(bestIndex + 1)));

            int i = 0;

            //Check if it deals with an expression between parentheses
        }else if(e.Tokens.startsWith(TokenType.PAREN_OPEN) && e.Tokens.endsWith(TokenType.PAREN_CLOSE)){

            //Remove the parenthese around a GROUP
            //(a + b) => a + b

            e.Operator = TokenType.GROUP;
            e.Left = parseExpression(new Expression(e.Tokens.getTokensBetweenIndex(1, e.Tokens.size() - 2)));

        }else if (e.Tokens.endsWith(TokenType.PAREN_CLOSE)){

            e.Operator = TokenType.FUNCTION;
            e.Left = new Expression(e.Tokens.getTokensBetween(0, TokenType.PAREN_OPEN), TokenType.KEYWORD);
            e.Right = parseExpression(new Expression(e.Tokens.getTokensBetweenIndex(e.Tokens.indexFrom(TokenType.PAREN_OPEN))));
        }

        //Check if it deals with a NUMBER, VARIABLE, BOOLEAN, STRING type.
        else{

            if (e.Tokens.size() > 1) throw new ScriptException(String.format("Invalid expression: '%s'", e.Value));

            TokenType tokenType = (TokenType)e.Tokens.get(0).Type;

            switch(tokenType)
            {
                case NUMBER:
                    e = new Expression(e.Tokens.AsOne(TokenType.NUMBER), TokenType.NUMBER);
                    break;
                case KEYWORD:
                    e = new Expression(e.Tokens.AsOne(TokenType.VARIABLE), TokenType.VARIABLE);
                    break;
                case VARIABLE:
                    e = new Expression(e.Tokens.AsOne(TokenType.VARIABLE), TokenType.VARIABLE);
                    break;
                case STRING:
                    e = new Expression(e.Tokens.AsOne(TokenType.STRING), TokenType.STRING);
                    break;
                case BOOL:
                    e = new Expression(e.Tokens.AsOne(TokenType.BOOL), TokenType.BOOL);
                    break;

            }

        }

        return e;
    }

 //endregion

//region Private Methods

    //region Parse Statement

    //region Parsing Statement Functions

    //get the precedence of a token based on its type
    private static PrecedenceType evaluatePrecedenceType(Token currentToken, boolean isUnary)
    {

            switch(currentToken.Type) {
                case PLUS:
                    if (isUnary) {
                        return Types.PrecedenceType.Unary;

                    } else {
                        return PrecedenceType.Plus;
                    }
                case MINUS:
                    if (isUnary) {
                        return Types.PrecedenceType.Unary;

                    } else {
                        return PrecedenceType.Minus;
                    }
                case MULT:
                    return Types.PrecedenceType.Mult;
                case DIV:
                    return Types.PrecedenceType.Div;
                case POWER:
                    return PrecedenceType.Power;
                case COMMA:
                    return PrecedenceType.Delimiter;
            }

         if (Helpers.isRelational(currentToken.Value)){

            switch (currentToken.Type)
            {
                case IS:
                    return PrecedenceType.RelationalIs;
                case ISGREATER: case ISGREATEROREQUAL:
                return PrecedenceType.RelationalGreater;
                case ISLESSER: case ISLESSEROREQUAL:
                return PrecedenceType.RelationalLesser;
                case ISEQUAL:
                    return PrecedenceType.RelationalEqual;
                case ISNOTEQUAL:
                    return PrecedenceType.RelationalNotEqual;
            }
        }
        return PrecedenceType.None;
    }

    private Assignment parseAssignment() throws ScriptException {
        return parseAssignment(TokenType.SEMICOLON);
    }

    private Assignment parseAssignment(TokenType untilType) throws ScriptException {

        if (testSequence(TokenType.KEYWORD, TokenType.EQUAL)) {
            Assignment newAssignment = new Assignment(lookAheadAndPush().Value);
            newAssignment.Value = parseExpression(new Expression(ToArray(untilType, 1)));
            return newAssignment;
        }else
            throw new ScriptException(String.format("Unexpected '%s'.", lookAhead().Value));
    }

    private IfStatement parseIfStatement() throws ScriptException{

        IfStatement ifStatement = new IfStatement();

        ifStatement.ConditionalExpression = parseExpression(new Expression(ToArray(TokenType.THEN, 1))); //Get the Value until the THEN Token

        escapeTokenUntilType(TokenType.CRLF); //skip the break lines

        ifStatement.TruePart = parseStatement(); //parse all the true part

        //Parse else statement ; inside the if statement
        if (lookAhead().Type == TokenType.ELSE) // check if there's an else
        {
            escapeAllTokens(TokenType.ELSE, TokenType.CRLF); //remove everything until the beginning of the else part
            ifStatement.FalsePart = parseStatement(); //parse the false part
        }

        escapeAllTokens(TokenType.ENDIF, TokenType.CRLF);
        return ifStatement;
    }

    private ForLoopStatement parseForLoopStatement() throws ScriptException {

        lookAheadAndPush(1); //Push the FOR and PAREN_OPEN Token to start parsing the assignment (i = 0 for instance)

        ForLoopStatement forLoop = new ForLoopStatement();

        forLoop.StartStatement = parseAssignment(TokenType.TO);
        forLoop.LimitExpression = parseExpression(new Expression(ToArray(TokenType.STEP, 0)));
        forLoop.IncreaseExpression = parseExpression(new Expression(ToArray(TokenType.PAREN_CLOSE,0)));


        escapeTokenUntilType(TokenType.CRLF);

        forLoop.BlockStatement = parseStatement();
        escapeAllTokens(TokenType.ENDFOR, TokenType.CRLF);

        return forLoop;

    }

    private SwitchStatement parseSwitchStatement() throws ScriptException {

        SwitchStatement switchStatement = new SwitchStatement();

        switchStatement.TestedExpression = parseExpression(new Expression(ToArray(TokenType.PAREN_CLOSE, 2)));
        escapeTokenUntilType(TokenType.CRLF);

        //Parse all the cases declared
        while(lookAhead().Type == TokenType.CASE)
        {

            Expression candidateValue = parseExpression(new Expression(ToArray(TokenType.COLON, 1)));
            escapeTokenUntilType(TokenType.CRLF);

            Statement candidateStatement = parseStatement();

            switchStatement.Candidates.put(candidateValue, candidateStatement);

            escapeAllTokens(TokenType.BREAK, TokenType.SEMICOLON, TokenType.CRLF);

        }

        return switchStatement;
    }

    private WhileStatement parseWhileStatement() throws ScriptException {

        WhileStatement whileStatement = new WhileStatement();
        whileStatement.ConditionalExpression = parseExpression(new Expression(ToArray(TokenType.PAREN_CLOSE, 2)));
        escapeAllTokens(TokenType.CRLF);
        whileStatement.BlockStatement = parseStatement();

        escapeAllTokens(TokenType.ENDWHILE, TokenType.CRLF);

        return whileStatement;
    }

    //endregion

    private FunctionStatement parseFunction() throws ScriptException {
        FunctionStatement func = new FunctionStatement();
        func.Value = parseExpression(new Expression(ToArray(TokenType.SEMICOLON, 0)));
        return func;
    }

    private ReturnStatement parseReturnStatement() throws ScriptException {
        ReturnStatement returnStatement = new ReturnStatement();
        returnStatement.Value = parseExpression(new Expression(ToArray(TokenType.SEMICOLON, 1)));
        return returnStatement;
    }


    //endregion

    //region Parse Expression

    private Statement parseStatement() throws ScriptException {
        Statement outputStatement = null;

        //Parse Assignment
        if (testSequence(TokenType.KEYWORD, TokenType.EQUAL)) {
            Assignment assign = parseAssignment();
            outputStatement = assign;

            //Parse If Statement
        } else if (testSequence(TokenType.IF, TokenType.PAREN_OPEN)) {
            IfStatement ifStatement = parseIfStatement();
            outputStatement = ifStatement;

            //Parse a For-Loop
        } else if (testSequence(TokenType.FOR, TokenType.PAREN_OPEN)) {

            ForLoopStatement forLoop = parseForLoopStatement();
            outputStatement = forLoop;

            //Parse a switch statement
        } else if (testSequence(TokenType.SWITCH, TokenType.PAREN_OPEN)) {
            SwitchStatement switchStatement = parseSwitchStatement();
            outputStatement = switchStatement;

            //Parse a While Statement
        } else if (testSequence(TokenType.WHILE, TokenType.PAREN_OPEN)) {
            WhileStatement whileStatement = parseWhileStatement();
            outputStatement = whileStatement;

            //Parse a function
        } else if(testSequence(TokenType.KEYWORD, TokenType.PAREN_OPEN)) {
            FunctionStatement func = parseFunction();
            outputStatement = func;

       } else if(testSequence(TokenType.RETURN)){
            ReturnStatement returnStatement = parseReturnStatement();
            outputStatement = returnStatement;
        }


        escapeTokenUntilType(TokenType.CRLF);


        if (lookAhead().Type == TokenType.CASE) throw new ScriptException("Missing 'break;'");
        if (lookAhead().Type == TokenType.ENDIF || lookAhead().Type == TokenType.ELSE || lookAhead().Type == TokenType.ENDFOR || lookAhead().Type == TokenType.ENDSWITCH || lookAhead().Type == TokenType.BREAK || lookAhead().Type == TokenType.ENDWHILE || lookAhead().Type == TokenType.ENDFUNCTION) {
            return outputStatement;
        }

        if (outputStatement == null) throw new ScriptException(String.format("Unexpected '%s'.", lookAhead().Value));
        if (lookAhead().Type != TokenType.EOF) {
            outputStatement = new Sequence(outputStatement, parseStatement());
        }


        return outputStatement; //returns a tree of sequences.
    }

    private ResolveStatement parseResolveStatement() throws ScriptException {

        ResolveStatement resolveStatement = new ResolveStatement();

        resolveStatement.ResolveExpression = parseExpression(new Expression(ToArray(TokenType.COMMA, 2)));
        resolveStatement.VarExpression = parseExpression(new Expression(ToArray(TokenType.PAREN_CLOSE,0)));

        escapeAllTokens(TokenType.SEMICOLON);

        return resolveStatement;
    }

    //endregion


    //endregion

}