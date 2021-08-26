package demo;

public class Main {

    /** Red color for displaying errors. */
    public static final String ANSI_RED = "\u001B[31m";

    /** Evaluate test inputs. */
    public static void main(String[] args) {

        // Input expression strings.
        String[] inputs = {  
                "true", //true               
                "false", //false
                "(false)", //false
                "(false)", //false
                "!true", //false
                "!false", //true
                "!!true", //true
                "!!false", //false
                "!(!true)", //true
                "!(!false)", //false        		
                "true & false", //false               
                "true | false", //true
                "true & false | true", //true
                "true & false | true & false", //false
                "!true & false", //false
                "!true | false", //false
                "!(true & false)", //true ------
                "!(true | false)", //false -------
                "(true & false) | (true | false)", //true
                "!(true & false) | !(true | false)", //true ?
                "!(!true & !(true | (false | true)))", //false ?
                "!(!true & !(true | (false | true)))", //false ?
                "(!(!(true | (false | !(true & false)) | false)))",//
                "(!(!(true & (false | !(true & false) & false))))",//
                "true $", //error
                "& true", //error
                "true || false", //false
                "true ! false",  //error
                "true)", //error
                "(true", //error               
                "true &", //error
                "true && false", //false

        };

        // Evaluate each expression.
        IEvaluator evaluator = new Evaluator();

        for (String input : inputs) {
            try {
                boolean result = evaluator.evaluate(input);
                System.out.println(input + " = " + result);
            } catch (SyntaxException e) {
                System.out.println(ANSI_RED + "Syntax error in '" + input + "' - " + e.getMessage());
            }
        }
    }
}
