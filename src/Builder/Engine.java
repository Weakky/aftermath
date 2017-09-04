package Builder;

import parser.Parsing.AstParser;
import parser.Shared.ScriptException;
import parser.Tokenizer.TokenList;
import parser.Tokenizer.Tokenizer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

/**
 * Created by Utilisateur on 25/03/2015.
 */
public class Engine {

public HashMap<String, ScriptDelegate> Delegates;

    private Tokenizer tokenizer;
    private AstParser parser;
    public Evaluator evaluator;

    public Engine(){
        Delegates = new HashMap<>();
    }

    //Execute everything
    public String Execute(String script) throws ScriptException, IOException, URISyntaxException {

        tokenizer = new Tokenizer(script);
        TokenList tokens = tokenizer.tokenize();

        //Create the expressions trees, and format them into an array of assignment
        parser = new AstParser(this, tokens);

        evaluator = parser.process();

        String output = evaluator.Evaluate();
        output = output + "--------------------------\nScript executed properly.";

        return output;
    }

    public  String Evaluate(String function) throws ScriptException, IOException, URISyntaxException {

        String script = "function Main()\nreturn " + function +";\nend function";
        Tokenizer tokenizer = new Tokenizer(script);
        //Create the expressions trees, and format them into an array of assignment
        AstParser parser = new AstParser(this, tokenizer.tokenize());

        Evaluator evaluator = parser.process();

        String output = evaluator.Evaluate();

        return output;

    }

    public void addDelegates(String name, ScriptDelegate delegate){

        if (!Delegates.containsKey(name)){
            Delegates.put(name, delegate);
        }else{
            Delegates.replace(name, delegate);
        }

    }





}

