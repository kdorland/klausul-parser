package parser;

import java.util.List;

public class Evaluator {

    public boolean eval(Expression expr, DataContext ctx) {
        return switch (expr) {
            case Expression.Condition c -> evalCondition(c, ctx);
            case Expression.BinaryExpression b -> {
                boolean left = eval(b.left(), ctx);
                boolean right = eval(b.right(), ctx);
                yield switch (b.operator()) {
                    case "og" -> left && right;
                    case "eller" -> left || right;
                    default -> throw new RuntimeException("Unknown logical operator: " + b.operator());
                };
            }
            case Expression.ParenthesizedExpression p -> eval(p.inner(), ctx);
        };
    }

    private boolean evalCondition(Expression.Condition c, DataContext ctx) {
        List<String> actualValues = ctx.get(c.field());

        return switch (c.operator()) {
            case "=" -> actualValues.stream().anyMatch(v -> v.equals(c.values().getFirst()));
            case "i" -> actualValues.stream().anyMatch(c.values()::contains);
            case ">=", "<=", ">", "<" -> {
                int target = Integer.parseInt(c.values().getFirst());
                yield actualValues.stream()
                        .mapToInt(Integer::parseInt)
                        .anyMatch(actual -> switch (c.operator()) {
                            case ">=" -> actual >= target;
                            case "<=" -> actual <= target;
                            case ">"  -> actual > target;
                            case "<"  -> actual < target;
                            default -> false;
                        });
            }
            default -> throw new RuntimeException("Unknown comparison operator: " + c.operator());
        };
    }
}
