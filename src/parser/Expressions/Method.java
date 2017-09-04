package parser.Expressions;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Utilisateur on 24/03/2015.
 */
public class Method {

    public String Name;
    public Map<Integer, String> ParamTable;
    public Map<String, Object> Locals;
    public int ParamLen;
    public Statement Statements;

    public Method(){
        Locals = new HashMap<String, Object>();
        ParamTable = new HashMap<Integer, String>();
        ParamLen = 0;
    }

    public Method(String methodName){
        Name = methodName;
    }

    public void addParam(int index, String ref){
        ParamTable.put(index, ref);
        ParamLen += 1;
    }

    public void assignParams(Object[] args){
        for(int i = 0; i < args.length ; i++){
            setLocal(ParamTable.get(i), args[i]);
        }
    }


    public void setLocal(String name, Object value) {
        if (Locals.containsKey(name.toLowerCase())){
            Locals.replace(name, value);
        }else{
            Locals.put(name, value);
        }
    }

    public void removeLocal(String name){
        if (Locals.containsKey(name)){
            Locals.remove(name);
        }
    }

    public Object getLocal(String name){
        if (Locals.containsKey(name)){
            return Locals.get(name.toLowerCase());
        }else{
            return null;
        }
    }
}
