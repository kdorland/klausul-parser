package parser;

import java.util.*;

public class Parser {
    private final List<Token> tokens;
    private int pos = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private Token peek() {
        return tokens.get(pos);
    }

    private Token next() {
        return tokens.get(pos++);
    }

    private boolean match(String text) {
        if (peek().text().equals(text)) {
            next();
            return true;
        }
        return false;
    }

    private Token expect(String text) {
        Token token = next();
        if (!token.text().equals(text)) {
            throw new RuntimeException("Expected '" + text + "', got '" + token.text() + "'");
        }
        return token;
    }

    public Expression parseKlausul() {
        expect("Klausul");
        Token name = next();
        System.out.println("Parsing Klausul: " + name.text());
        expect(":");
        return parseExpression();
    }

    public Expression parseExpression() {
        return parseOrExpression();
    }

    private Expression parseOrExpression() {
        Expression left = parseAndExpression();
        while (peek().text().equals("eller")) {
            next(); // consume 'eller'
            Expression right = parseAndExpression();
            left = new Expression.BinaryExpression(left, "eller", right);
        }
        return left;
    }

    private Expression parseAndExpression() {
        Expression left = parseOperand();
        while (peek().text().equals("og")) {
            next(); // consume 'og'
            Expression right = parseOperand();
            left = new Expression.BinaryExpression(left, "og", right);
        }
        return left;
    }

    private Expression parseOperand() {
        expect("(");
        Expression result;
        if (peekAheadIsCondition()) {
            result = parseConditionOperand();
        } else {
            result = parseLogicalGroup();
        }
        return result;
    }

    private Expression parseConditionOperand() {
        return parseCondition();
    }

    private Expression parseLogicalGroup() {
        Expression inner = parseExpression();
        expect(")");
        return new Expression.ParenthesizedExpression(inner);
    }


    private boolean peekAheadIsCondition() {
        if (pos + 1 < tokens.size()) {
            Token t1 = tokens.get(pos);     // FIELD
            Token t2 = tokens.get(pos + 1); // OPERATOR
            return t1.type() == TokenType.IDENTIFIER &&
                    t2.type() == TokenType.OPERATOR;
        }
        return false;
    }

    private Expression.Condition parseCondition() {
        Token field = next();    // IDENTIFIER
        Token op = next();       // OPERATOR
        List<String> values = new ArrayList<>();
        do {
            values.add(next().text());
        } while (match(","));
        expect(")");
        return new Expression.Condition(field.text(), op.text(), values);
    }
}
