package parser;

import java.util.*;
import java.util.regex.*;

public class Lexer {
    private static final Pattern TOKEN_PATTERNS = Pattern.compile(
            "\\s*(?:(Klausul|og|eller)|" +           // keywords
                    "(>=|<=|=|i)|" +                 // operators
                    "([A-Za-z][A-Za-z0-9]*)|" +      // identifiers
                    "([0-9]+)|" +                    // numbers
                    "([:,()])|" +                    // symbols
                    "(\\S))"                         // unknown
    );

    private final Matcher matcher;
    private final List<Token> tokens = new ArrayList<>();

    public Lexer(String input) {
        matcher = TOKEN_PATTERNS.matcher(input);
        tokenize();
    }

    private void tokenize() {
        while (matcher.find()) {
            if (matcher.group(1) != null)
                tokens.add(new Token(TokenType.KEYWORD, matcher.group(1)));
            else if (matcher.group(2) != null)
                tokens.add(new Token(TokenType.OPERATOR, matcher.group(2)));
            else if (matcher.group(3) != null)
                tokens.add(new Token(TokenType.IDENTIFIER, matcher.group(3)));
            else if (matcher.group(4) != null)
                tokens.add(new Token(TokenType.NUMBER, matcher.group(4)));
            else if (matcher.group(5) != null)
                tokens.add(new Token(TokenType.SYMBOL, matcher.group(5)));
            else
                throw new RuntimeException("Unknown token: " + matcher.group(6));
        }
        tokens.add(new Token(TokenType.EOF, ""));
    }

    public List<Token> getTokens() {
        return tokens;
    }
}
