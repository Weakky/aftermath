/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Builder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import parser.Expressions.*;
import parser.Shared.ScriptException;
import parser.Shared.Helpers;
import parser.Shared.Types;

import javax.swing.*;

/**
 *
 * @TODO: Refactor the class, it's a mess.
 */

public class Evaluator {

    private Method method;
    private Engine Engine;

    public LinkedList<Method> Methods = new LinkedList<>();
    

    public Evaluator(Engine scriptEngine){this.Engine = scriptEngine;}
    
    public String Evaluate() throws ScriptException, IOException, URISyntaxException {
        if (Methods.size() > 0)
        {
          return String.valueOf(runMethod("Main", null));
        }
        
        throw new ScriptException("There is no statement to parse.");
    }

    private Object runMethod(String name, Object... args) throws ScriptException, IOException, URISyntaxException {

        Object outputResult = "";

        if (args == null){args = new Object[0];}

        if (containsMethod(name)){
            method = getMethod(name);

            if(method.ParamLen == args.length){
                method.assignParams(args);
                outputResult = runStatement(method.Statements);
            }

            return outputResult;

        }else{
            return "Could not run the method. (from runMethod function in Evaluator.java)";
        }
    }

    private Method getMethod(String name){
        for (Method m: Methods){
            if (m.Name.equals(name)){
                return m;
            }
        }
        return null;
    }

    private boolean containsMethod(String name){
        for (Method m: Methods){
            if (m.Name.equals(name)){
                return true;
            }
        }
        return false;
    }


        Object result = null;
        private Object runStatement(Statement statement) throws ScriptException, IOException, URISyntaxException {


             if (statement instanceof Sequence)
            {
                Sequence sequence = (Sequence)statement;


                if(sequence.First instanceof ReturnStatement){
                    result = runStatement(sequence.First);
                }else{
                    runStatement(sequence.First);
                }

                if(sequence.Second instanceof ReturnStatement){
                    result = runStatement(sequence.Second);
                }else{
                    runStatement(sequence.Second);
                }


                            
            }else if (statement instanceof Assignment){
                
            Assignment assign = (Assignment)statement;
            String name = assign.Name;
            Object value = resolveExpression(assign.Value);
            method.setLocal(name, value);


            }else if (statement instanceof IfStatement){

                IfStatement ifStatement = (IfStatement)statement;

                Boolean conditionalResult = Boolean.parseBoolean(String.valueOf(resolveExpression(ifStatement.ConditionalExpression)));
              
                if (conditionalResult)
                {
                   result =  runStatement(ifStatement.TruePart);
                }else{
                   result =  runStatement(ifStatement.FalsePart);
                }
                  
            } else if (statement instanceof ForLoopStatement){

                 ForLoopStatement forLoop = (ForLoopStatement)statement;
                 Assignment assignStart =(Assignment)forLoop.StartStatement;

                runStatement(forLoop.StartStatement);

                String startValue = String.valueOf(resolveExpression(assignStart.Value));
                String limitValue = String.valueOf(resolveExpression(forLoop.LimitExpression));
                String increaseValue = String.valueOf(resolveExpression(forLoop.IncreaseExpression));

                 if (Helpers.isNumeric(startValue) && Helpers.isNumeric(limitValue) && Helpers.isNumeric(increaseValue))
                 {
                     Double start = Double.parseDouble(startValue);
                     Double limit = Double.parseDouble(limitValue);
                     Double step = Double.parseDouble(increaseValue);

                     do{
                         method.setLocal(assignStart.Name, start);
                         runStatement(forLoop.BlockStatement);
                         start += step;

                     }while((step > 0) ? start <= limit : start >= limit);

                     method.removeLocal(assignStart.Name);

                 }
                 else
                 {
                     throw new ScriptException("Invalid for-loop parameters.");
                 }

             }else if (statement instanceof SwitchStatement){
                 
                 SwitchStatement switchStatement = (SwitchStatement)statement;

                 String condition = String.valueOf(resolveExpression(switchStatement.TestedExpression));
                 
                 for(Expression ex: switchStatement.Candidates.keySet())
                 {

                     if (Helpers.isBoolean(condition) && ex.Tokens.startsWith(Types.TokenType.IS)){throw new ScriptException(String.format("'Is' operator can not be used here. '%s' is already returning a boolean Value.\nCases are forced to be true or false only..", switchStatement.TestedExpression.Value));}
                     if (Helpers.isNumeric(condition) && ex.Tokens.startsWith(Types.TokenType.IS)){ex = replaceIsExpression(ex.Operator, switchStatement.TestedExpression, ex);}

                     String resolvedCandidate = String.valueOf(resolveExpression(ex));


                     if(Helpers.isBoolean(resolvedCandidate) && Boolean.parseBoolean(resolvedCandidate))
                     {
                             runStatement(switchStatement.Candidates.get(ex));

                     }else{
                         if (condition.equals(resolvedCandidate)) {
                             runStatement(switchStatement.Candidates.get(ex));
                         }
                     }
                     
                 }

             }else if(statement instanceof WhileStatement)
             {
                 WhileStatement whileStatement = (WhileStatement)statement;

                 while (Boolean.parseBoolean(String.valueOf(resolveExpression(whileStatement.ConditionalExpression))))
                 {
                     runStatement(whileStatement.BlockStatement);
                 }

             }else if(statement instanceof FunctionStatement){
                 FunctionStatement function = (FunctionStatement)statement;
                 result =  String.valueOf(resolveFunction(function.Value));


             }else if(statement instanceof ReturnStatement){
                 ReturnStatement returnStatement = (ReturnStatement)statement;
                 String returnedValue = String.valueOf(resolveExpression(returnStatement.Value));
                 return returnedValue;
             }
            
             return result;
        }

    private Expression replaceIsExpression(Types.TokenType operator, Expression first, Expression second)
    {

        //TODO: Finish implementing function /!\

        if (second.Left.Tokens.startsWith(Types.TokenType.IS) && second.Left.Tokens.size() == 1) { //If the left part only contains an IS token
            second.Left = first;  //Then replace the IS by the switch statement tested expression
            second.Tokens.remove(0);
            second.Tokens.addAll(0, first.Tokens);
            second.Value = second.Tokens.toString();
            return second;
        }
        else
        return null;
    }



    public Object resolveExpression(Expression e) throws ScriptException, IOException, URISyntaxException {
        Object output = null;
        if (e == null) {throw new ScriptException("No expression assigned to the variable.");}
        
        switch(e.Operator)
        {
            case PLUS: case MINUS: case MULT: case DIV: case POWER:
                output = resolveArithmetic(e);
                break;
                
            case NEGATION: case POSITIVE:
                output = resolveUnary(e);
                break;
                
            case ISEQUAL: case ISLESSER: case ISGREATER: case ISNOTEQUAL: case ISGREATEROREQUAL: case ISLESSEROREQUAL:
                output = resolveRelational(e);
                break;
                
            case NUMBER:
                output = resolveNumeric(e);
                break;
                
            case GROUP:
                output = resolveGroup(e);
                break;
            case FUNCTION:
                output = resolveFunction(e);
                break;
            case DELIMITER:
                output = resolveDelimiter(e);
                break;
                
            case VARIABLE:
                output = resolveVariable(e);
                break;
            case STRING:
                output = resolveString(e);
                break;
            case BOOL:
                output = resolveBool(e);
                break;

        }

        return output;
    }

    private Object resolveDelimiter(Expression e) throws ScriptException, IOException, URISyntaxException {
        String valueA = String.valueOf(resolveExpression(e.Left));
        String valueB = String.valueOf(resolveExpression(e.Right));
        return valueA + (char)0 + valueB;
    }

    private Object resolveBool(Expression e) {
        Object output = Boolean.parseBoolean(e.Value);
       return output;
    }

    private Object resolveString(Expression e) {
        Object outputResult = null;
        outputResult = e.Value.substring(1, e.Value.length() - 1);
        return outputResult;
    }

    public Object resolveUnary(Expression e) throws ScriptException, IOException, URISyntaxException {
        Object output = null;

        String unaryValue = String.valueOf(resolveExpression(e.Left));
        
        switch(e.Operator)
        {
            case NEGATION:
                output = -Math.abs(Double.parseDouble(unaryValue));
                break;
            case POSITIVE:
                output = +Math.abs(Double.parseDouble(unaryValue));
                break;
        }
        
        return output;
    }
    
    public Object resolveVariable(Expression e) throws ScriptException
    {
        Object output = null;
        if (!method.Locals.containsKey(e.Value)){throw new ScriptException(String.format("Variable '%s' not defined", e.Value));}
        
        output = method.getLocal(e.Value);
        return output;
    }
    
    public Object resolveGroup(Expression e) throws ScriptException, IOException, URISyntaxException {
        Object output = null;
        output = resolveExpression(e.Left);   
        return output;
    }
    
    public Object resolveNumeric(Expression e) throws ScriptException
    {
        Object output = null;
        
        if (Helpers.isNumeric((e.Value)))
        {
            output = Double.parseDouble(e.Value);
        }
        
        return output;
    }
    
    public Object resolveArithmetic(Expression e) throws ScriptException, IOException, URISyntaxException {
        Object output = null;
        
        String valueA = String.valueOf(resolveExpression(e.Left));
        String valueB = String.valueOf(resolveExpression(e.Right));

               
        if (Helpers.isNumeric(valueA) && Helpers.isNumeric(valueB))
        {
         
            switch (e.Operator)
            {
                case PLUS:
                    output = Double.parseDouble(valueA) + Double.parseDouble(valueB);
                    break;
                case MINUS:
                    output = Double.parseDouble(valueA) - Double.parseDouble(valueB);
                    break;
                 case MULT:
                    output = Double.parseDouble(valueA) * Double.parseDouble(valueB);
                    break;
                 case DIV:
                    output = Double.parseDouble(valueA) / Double.parseDouble(valueB);
                    break;
                case POWER:
                    output = Math.pow(Double.parseDouble(valueA), Double.parseDouble(valueB));
                    break;
                    
            }         
        }else
        {
            output = valueA + valueB;
        }
        
        
        return output;  
    }

    private Object resolveRelational(Expression e) throws ScriptException, IOException, URISyntaxException {
        
        Object outputResult = null;
        
        Object valueA = resolveExpression(e.Left);
        Object valueB = resolveExpression(e.Right);
        
        if (valueB != null){
        
        switch(e.Operator)
        {
            
            case ISGREATER:
                
                if (Helpers.isNumeric(valueA.toString()) && Helpers.isNumeric(valueB.toString()))
                {
                    outputResult = Double.parseDouble(String.valueOf(valueA)) > Double.parseDouble(String.valueOf(valueB));

                } else if (Helpers.isBoolean(valueA.toString()) && Helpers.isBoolean(valueB.toString()))
                {
                    throw new ScriptException("Boolean can not be greater than anything");
                }
                break;
                
            case ISGREATEROREQUAL:
                if (Helpers.isNumeric(valueA.toString()) && Helpers.isNumeric(valueB.toString()))
                {
                    outputResult = Double.parseDouble(String.valueOf(valueA)) >= Double.parseDouble(String.valueOf(valueB));
                }               
                break;
                
                case ISLESSEROREQUAL:
                if (Helpers.isNumeric(valueA.toString()) && Helpers.isNumeric(valueB.toString()))
                {
                    outputResult = Double.parseDouble(String.valueOf(valueA)) <= Double.parseDouble(String.valueOf(valueB));
                }               
                break;
                
            case ISLESSER:
                if (Helpers.isNumeric(valueA.toString()) && Helpers.isNumeric(valueB.toString()))
                {
                    outputResult = Double.parseDouble(String.valueOf(valueA)) < Double.parseDouble(String.valueOf(valueB));
                }else if (Helpers.isBoolean(valueA.toString()) && Helpers.isBoolean(valueB.toString()))
                {
                    throw new ScriptException("Boolean can not be lesser than anything");
                }
                break;
            case ISEQUAL:

                if (Helpers.isNumeric(valueA.toString()) && Helpers.isNumeric(valueB.toString()))
                {
                    outputResult = Double.parseDouble(String.valueOf(valueA)) == Double.parseDouble(String.valueOf(valueB));
                } else if (Helpers.isBoolean(valueA.toString()) && Helpers.isBoolean(valueB.toString()))
                {
                    outputResult = Boolean.parseBoolean(String.valueOf(valueA)) == Boolean.parseBoolean(String.valueOf(valueB));
                }else if(Helpers.isString(valueA) && Helpers.isString(valueB))
                {
                    outputResult = valueA.toString().equals(valueB.toString());
                }
                break;
            case ISNOTEQUAL:
                if (Helpers.isNumeric(valueA.toString()) && Helpers.isNumeric(valueB.toString()))
                {
                    outputResult = Double.parseDouble(String.valueOf(valueA)) != Double.parseDouble(String.valueOf(valueB));
                }
                else if (Helpers.isBoolean(valueA.toString()) && Helpers.isBoolean(valueB.toString()))
                {
                    outputResult = Boolean.parseBoolean(String.valueOf(valueA)) != Boolean.parseBoolean(String.valueOf(valueB));
                }

                break;

        }
        }
        else //Test only one boolean Value if (boolean)
        {

        }
        return outputResult;
    }

    private Object resolveFunction(Expression e) throws ScriptException, IOException, URISyntaxException {

        String funcName = e.Left.Value;
        String Value = String.valueOf(resolveExpression(e.Right));


        if(containsMethod(funcName)){

            String[] Params = null;

            if (Value.contains(String.valueOf((char)0))){
                Params = Value.split(String.valueOf((char)0));
            }else{
                Params = new String[1];
                Params[0] = Value;
            }

            return runMethod(funcName, Params);
        }else if (Engine.Delegates.containsKey(funcName)){

            String[] Params = null;

            if (Value.contains(String.valueOf((char)0))){
                Params = Value.split(String.valueOf((char)0));
            }else{
                Params = new String[1];
                Params[0] = Value;
            }

            return Engine.Delegates.get(funcName).run(Params);
        }



        return e;
    }

    
    }
    
    
    
