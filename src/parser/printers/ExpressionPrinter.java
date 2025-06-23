package parser.printers;

import parser.Expression;

public class ExpressionPrinter {

    public static void print(Expression expr) {
        print(expr, 0);
    }

    private static void print(parser.Expression expr, int indent) {
        String pad = "  ".repeat(indent);

        switch (expr) {
            case Expression.Condition c -> {
                System.out.println(pad + "Condition: " + c.field() + " " + c.operator() + " " + c.values());
            }
            case Expression.BinaryExpression b -> {
                System.out.println(pad + "BinaryExpression: " + b.operator());
                print(b.left(), indent + 1);
                print(b.right(), indent + 1);
            }
            case Expression.ParenthesizedExpression p -> {
                System.out.println(pad + "Parentheses");
                print(p.inner(), indent + 1);
            }
        }
    }
}
