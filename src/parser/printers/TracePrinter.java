package parser.printers;

import parser.Expression;

public class TracePrinter {
    public static void print(EvalTracer.Trace node) {
        print(node, 0);
    }

    private static void print(EvalTracer.Trace node, int indent) {
        String pad = "  ".repeat(indent);
        String status = node.result() ? "[✓]" : "[✗]";

        switch (node.expr()) {
            case Expression.Condition c -> {
                System.out.println(pad + "Condition: " + c.field() + " " + c.operator() + " " + c.values() + " " + status);
            }
            case Expression.BinaryExpression b -> {
                System.out.println(pad + "BinaryExpression: " + b.operator() + " " + status);
                for (var child : node.children()) {
                    print(child, indent + 1);
                }
            }
            case Expression.ParenthesizedExpression p -> {
                System.out.println(pad + "Parentheses " + status);
                for (var child : node.children()) {
                    print(child, indent + 1);
                }
            }
        }
    }
}
