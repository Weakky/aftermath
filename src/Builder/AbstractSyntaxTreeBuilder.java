/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Builder;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import parser.Expressions.*;

import java.util.LinkedList;

/**
 *
 * TODO: Implement While statement
 */
public class AbstractSyntaxTreeBuilder {

    private Evaluator evaluator;
    private DefaultTreeModel model = null;
    private DefaultMutableTreeNode rootNode = null;
    
    public AbstractSyntaxTreeBuilder(Evaluator evaluator)
    {
        this.evaluator = evaluator;
    }

    public void Create(JTree jTree){

        if (model == null) model = (DefaultTreeModel) jTree.getModel();
        if(rootNode == null) rootNode = (DefaultMutableTreeNode) model.getRoot();

            createAST(evaluator, rootNode, jTree);


    }

        private void createAST(Object script, DefaultMutableTreeNode parent, JTree jTree1){



            if(script instanceof Evaluator) {

                Evaluator evaluator = (Evaluator)script;
                DefaultMutableTreeNode scriptNode = new DefaultMutableTreeNode("[Script]", true);
                parent.add(scriptNode);


                for(Method m: evaluator.Methods){
                    createAST(m, scriptNode, jTree1);
                }


            }else if (script instanceof Method){

                Method method = (Method)script;

                DefaultMutableTreeNode methodNode = new DefaultMutableTreeNode("[Method]", true);
                methodNode.add(new DefaultMutableTreeNode("Name: " + method.Name, false));
                methodNode.add(new DefaultMutableTreeNode("Args: " + method.ParamLen, false));

                parent.add(methodNode);

                createAST(method.Statements, methodNode, jTree1);

            }else if (script instanceof Sequence){

            Sequence sequence = (Sequence)script;

            createAST(sequence.First, parent, jTree1);
            createAST(sequence.Second, parent, jTree1);

        }else if (script instanceof Assignment)
        {
            Assignment assign = (Assignment)script;
            DefaultMutableTreeNode assignNode = new DefaultMutableTreeNode("[Assign]", true);
            assignNode.add(new DefaultMutableTreeNode("Variable: " + assign.Name, true));
            assignNode.add(createTreeExpression(assign.Value));

            parent.add(assignNode);

        }else if (script instanceof IfStatement)
        {

            IfStatement ifStatement = (IfStatement)script;
            DefaultMutableTreeNode ifNode = new DefaultMutableTreeNode("[If Statement]", true);

            DefaultMutableTreeNode conditionNode = new DefaultMutableTreeNode("[Condition]", true);
            conditionNode.add(createTreeExpression(ifStatement.ConditionalExpression));
            ifNode.add(conditionNode);

            DefaultMutableTreeNode truePartNode = new DefaultMutableTreeNode("[True Part]", true);
            createAST(ifStatement.TruePart, truePartNode, jTree1);

            DefaultMutableTreeNode falsePartNode = new DefaultMutableTreeNode("[False Part]", true);
            createAST(ifStatement.FalsePart, falsePartNode, jTree1);

            ifNode.add(truePartNode);
            ifNode.add(falsePartNode);

            parent.add(ifNode);
        }else if (script instanceof ForLoopStatement)
        {
            ForLoopStatement forLoop = (ForLoopStatement)script;
            Assignment startAssign = (Assignment)forLoop.StartStatement;

            DefaultMutableTreeNode forLoopNode = new DefaultMutableTreeNode("[For Loop]", true);
            DefaultMutableTreeNode startNode = new DefaultMutableTreeNode("[Start Value]", true);
            DefaultMutableTreeNode limitNode = new DefaultMutableTreeNode("[Limit Value]", true);
            DefaultMutableTreeNode stepNode = new DefaultMutableTreeNode("[Step Value", true);
            DefaultMutableTreeNode blockStatementNode = new DefaultMutableTreeNode("[Block Statement]", true);


            startNode.add(createTreeExpression(startAssign.Value));
            limitNode.add(createTreeExpression(forLoop.LimitExpression));
            stepNode.add(createTreeExpression(forLoop.IncreaseExpression));

            createAST(forLoop.BlockStatement, blockStatementNode, jTree1);

            forLoopNode.add(startNode);
            forLoopNode.add(limitNode);
            forLoopNode.add(stepNode);

            forLoopNode.add(blockStatementNode);

            rootNode.add(forLoopNode);

        }else if (script instanceof SwitchStatement)
        {
            SwitchStatement switchStatement = (SwitchStatement)script;

            DefaultMutableTreeNode switchNode = new DefaultMutableTreeNode("[Switch Statement]", true);

            DefaultMutableTreeNode conditionNode = new DefaultMutableTreeNode("[Condition]", true);
            conditionNode.add(createTreeExpression(switchStatement.TestedExpression));

            switchNode.add(conditionNode);

            DefaultMutableTreeNode candidatesNode = new DefaultMutableTreeNode("[Candidates]", true);

            for (Expression ex: switchStatement.Candidates.keySet())
            {
                DefaultMutableTreeNode caseNode = new DefaultMutableTreeNode("[Case]", true);
                DefaultMutableTreeNode valueNode = new DefaultMutableTreeNode("[Value]", true);
                DefaultMutableTreeNode statementNode = new DefaultMutableTreeNode("[Block Statement]", true);

                valueNode.add(createTreeExpression(ex));
                createAST(switchStatement.Candidates.get(ex), statementNode, jTree1);

                caseNode.add(valueNode);
                caseNode.add(statementNode);

                candidatesNode.add(caseNode);

            }

            switchNode.add(candidatesNode);

            parent.add(switchNode);

        }else if(script instanceof FunctionStatement){

                FunctionStatement func = (FunctionStatement)script;

                DefaultMutableTreeNode funcNode = new DefaultMutableTreeNode("[Function]");
                funcNode.add(createTreeExpression(func.Value));

                parent.add(funcNode);

        }else if(script instanceof ReturnStatement){

                ReturnStatement returnStatement = (ReturnStatement)script;
                DefaultMutableTreeNode returnNode = new DefaultMutableTreeNode("[Return]");
                returnNode.add(createTreeExpression(returnStatement.Value));

                parent.add(returnNode);


            }



    }


    private DefaultMutableTreeNode createTreeExpression(Expression expression)
    {
        if (expression == null) return null;

        DefaultMutableTreeNode expressionNode = new DefaultMutableTreeNode("[Expression]");
        expressionNode.add(new DefaultMutableTreeNode("Value: " + expression.Value));
        expressionNode.add(new DefaultMutableTreeNode("Type: " + expression.Operator.toString()));
        
        
               DefaultMutableTreeNode leftExpr = null;
               DefaultMutableTreeNode rightExpr = null;
               
  
        if(expression.Left != null){
            leftExpr = new DefaultMutableTreeNode("[Left]", true);
            leftExpr.add(createTreeExpression(expression.Left));
                    expressionNode.add(leftExpr);
        }
        
        if (expression.Right != null){
            rightExpr = new DefaultMutableTreeNode("[Right]", true);
            rightExpr.add(createTreeExpression(expression.Right));   
                    expressionNode.add(rightExpr);
        }
        
        return expressionNode;      
    }
    
}
