package parser;

import parser.printers.EvalTracer;
import parser.printers.ExpressionPrinter;
import parser.printers.TracePrinter;

import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        DataContext ctx = new DataContext(Map.of(
                "ATC", List.of("C10BA02", "C10BA03"),
                "INDIKATION", List.of("31"),
                "ALDER", List.of("12")
        ));

        String input = """
        Klausul CHOL:
        (ATC = C10BA03)
        eller
        (ATC i C10BA02, C10BA05) og (ALDER >= 13)
        """;

        validtEksempel(input, ctx);

        //fejlSyntax(input);
    }

    public static void fejlSyntax(String input) {
        // Parsing af input til tokens
        System.out.println("*** Parsning af tokens ***");
        Lexer lexer = new Lexer(input);
        var tokens = lexer.getTokens();
        System.out.println(tokens);

        // Parsing af tokens til et udtryk
        System.out.println("\n*** Parsning af udtryk ***");
        Parser parser = new Parser(tokens);
        Expression expression = null;
        try {
            expression = parser.parseKlausul();
            ExpressionPrinter.print(expression);
        } catch (RuntimeException e) {
            System.err.println("Error parsing expression: " + e.getMessage());
        }

    }

    public static void validtEksempel(String input, DataContext ctx) {
        // Parsing af input til tokens
        System.out.println("*** Parsning af tokens ***");
        Lexer lexer = new Lexer(input);
        var tokens = lexer.getTokens();
        System.out.println(tokens);

        // Parsing af tokens til et udtryk
        System.out.println("\n*** Parsning af udtryk ***");
        Parser parser = new Parser(tokens);
        Expression expression = null;
        try {
            expression = parser.parseKlausul();
            ExpressionPrinter.print(expression);
        } catch (RuntimeException e) {
            System.err.println("Error parsing expression: " + e.getMessage());
        }

        // Evaluering af udtryk
        System.out.println("\n*** Evaluering af udtryk ***");
        System.out.println("DataContext: " + ctx.fields());
        assert expression != null;
        boolean result = new Evaluator().eval(expression, ctx);
        System.out.println("Evaluated to: " + result);

        // Udskrift af evalueringstrace
        System.out.println("\n*** Udskrift af evaluering ***");
        var tracer = new EvalTracer();
        var traced = tracer.trace(expression, ctx);

        TracePrinter.print(traced);
    }
}