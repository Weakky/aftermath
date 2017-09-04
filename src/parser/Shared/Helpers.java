/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser.Shared;

/**
 *
 * Contains a bunchload of static helper function.
 */
public class Helpers {
    
    
    public static boolean isRelational(String value)
    {
        return value.equals("<") || value.equals(">") || value.equals("==") || value.equals("!=") || value.equals("<=") || value.equals("=<") || value.equals(">=") || value.equals("=>") || value.equals("is");


    }
    
    public static boolean isOperator(String value)
    {
        return value.equals("+") || value.equals("-") || value.equals("*") || value.equals("/") || value.equals("=") || value.equals("==") || value.equals("<") || value.equals(">") || value.equals("!=") || value.equals("<=") || value.equals("=<") || value.equals(">=") || value.equals("=>") || value.equals("^") || value.equals(",");
    }
    
    
    public static boolean isArithmetic(String value)
    {
        return value.equals("+") || value.equals("-") || value.equals("*") || value.equals("/") || value.equals("^");
    }
    
    public static boolean isString(Object value)
    {
        return value instanceof String;
    }

    public static boolean isBoolean(String value)
    {
        return tryParseBoolean(value);
    }

    public static Boolean tryParseBoolean(String inputBoolean)
    {
        return !(!inputBoolean.equals("true")&&!inputBoolean.equals("false"));
    }

    public static boolean isNumeric(String value) throws ScriptException
    {
       return tryParseDouble(value);
    }
    
    public static boolean tryParseDouble(String number) throws ScriptException{

         try {
              Double.parseDouble(number);
                return true;
            }
         
           catch (NumberFormatException e) {
             return false;
        }
}

    public static String toRational(double d) {
        String s = String.valueOf(d);
        int digitsDec = s.length() - 1 - s.indexOf('.');

        int denom = 1;
        for(int i = 0; i < digitsDec; i++){
            d *= 10;
            denom *= 10;
        }
        int num = (int) Math.round(d);

        return String.valueOf(num) + "/" + String.valueOf(denom);
    }

}
