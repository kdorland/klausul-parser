package parser.printers;

import parser.DataContext;
import parser.Expression;

import java.util.List;

public class EvalTracer {
    public record Trace(Expression expr, boolean result, List<Trace> children) {}

    public Trace trace(Expression expr, DataContext ctx) {
        return switch (expr) {
            case Expression.Condition c -> new Trace(c, evalCondition(c, ctx), List.of());
            case Expression.ParenthesizedExpression p -> {
                Trace inner = trace(p.inner(), ctx);
                yield new Trace(p, inner.result, List.of(inner));
            }
            case Expression.BinaryExpression b -> {
                Trace left = trace(b.left(), ctx);
                Trace right = trace(b.right(), ctx);
                boolean result = switch (b.operator()) {
                    case "og" -> left.result && right.result;
                    case "eller" -> left.result || right.result;
                    default -> false;
                };
                yield new Trace(b, result, List.of(left, right));
            }
        };
    }

    private boolean evalCondition(Expression.Condition c, DataContext ctx) {
        List<String> actual = ctx.get(c.field());
        return switch (c.operator()) {
            case "=" -> actual.stream().anyMatch(v -> v.equals(c.values().getFirst()));
            case "i" -> actual.stream().anyMatch(c.values()::contains);
            case ">=", "<=", ">", "<" -> {
                int target = Integer.parseInt(c.values().getFirst());
                yield actual.stream()
                        .mapToInt(Integer::parseInt)
                        .anyMatch(actualVal -> switch (c.operator()) {
                            case ">=" -> actualVal >= target;
                            case "<=" -> actualVal <= target;
                            case ">"  -> actualVal > target;
                            case "<"  -> actualVal < target;
                            default -> false;
                        });
            }
            default -> false;
        };
    }
}
