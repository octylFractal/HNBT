package me.kenzierocks.hnbt.util;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.tree.ErrorNode;

import me.kenzierocks.hnbt.HNBTParsingException;
import me.kenzierocks.hnbt.grammar.HNBTParserBaseListener;
import me.kenzierocks.hnbt.grammar.StringLexer;
import me.kenzierocks.hnbt.grammar.StringParser;

public final class StringUtil {

    public static String escapedString(String unescaped) {
        return stringLiteralWithDoubleQuotes(unescaped);
    }

    public static String unescapeString(String escaped) {
        ANTLRInputStream stream = new ANTLRInputStream(escaped);
        StringLexer lexer = new StringLexer(stream);
        lexer.removeErrorListeners();
        CaptureErrorsListenener cap = new CaptureErrorsListenener();
        lexer.addErrorListener(cap);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        StringParser parser = new StringParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(cap);
        parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
        List<HNBTParsingException> errorNodeErrors = new ArrayList<>(1);
        parser.addParseListener(new HNBTParserBaseListener() {

            @Override
            public void visitErrorNode(ErrorNode node) {
                errorNodeErrors.add(new HNBTParsingException(
                        "Error node " + node.toStringTree(parser) + "@"
                                + node.getSymbol().getLine() + ":"
                                + node.getSymbol().getCharPositionInLine()));
            }
        });
        String val = null;
        try {
            val = parser.stringNoQuotes().unescaped;
        } catch (Exception e) {
            tokens.reset();
            parser.reset();
            errorNodeErrors.clear();
            cap.clearErrors();
            parser.getInterpreter().setPredictionMode(PredictionMode.LL);
            val = parser.stringNoQuotes().unescaped;
        }

        List<Exception> errors = Stream
                .concat(cap.getErrors().stream(), errorNodeErrors.stream())
                .collect(Collectors.toList());
        if (!errors.isEmpty()) {
            RuntimeException ex = new RuntimeException("Lexer errors occured");
            errors.forEach(ex::addSuppressed);
            throw ex;
        }
        return val;
    }

    public static char unescapeEscapeSequence(String escSeq) {
        checkArgument(escSeq.length() == 2,
                "escape sequences are 2 characters long: %s", escSeq);
        checkArgument(escSeq.charAt(0) == '\\',
                "escape sequences begin with \\: %s", escSeq);
        char after = escSeq.charAt(1);
        switch (after) {
            case 'b':
                return '\b';
            case 't':
                return '\t';
            case 'n':
                return '\n';
            case 'f':
                return '\f';
            case 'r':
                return '\r';
            case '"':
            case '\'':
            case '\\':
                return after;
            default:
                throw new IllegalArgumentException(
                        "Not an escape sequence: \\" + after);
        }
    }

    public static char unescapeUnicodeEscape(String unicodeEsc) {
        checkArgument(unicodeEsc.charAt(0) == '\\',
                "unicode escapes begin with \\: %s", unicodeEsc);
        int last4StartIndex = unicodeEsc.length() - 4;
        checkArgument(
                unicodeEsc.substring(1, last4StartIndex).codePoints()
                        .allMatch(x -> x == 'u'),
                "unicode escapes must have u's after the \\: %s", unicodeEsc);
        String charValue = unicodeEsc.substring(last4StartIndex);
        checkArgument(
                charValue.codePoints()
                        .allMatch(x -> '0' <= x && x <= '9'
                                || 'a' <= x && x <= 'f'
                                || 'A' <= x && x <= 'F'),
                "unicode escapes must be all hex digits after the \\u+: %s (digits: %s)",
                unicodeEsc, charValue);
        return (char) Integer.parseInt(charValue, 16);
    }

    public static char unescapeOctalEscape(String octalEsc) {
        checkArgument(octalEsc.charAt(0) == '\\',
                "octal escapes begin with \\: %s", octalEsc);
        int length = octalEsc.length();
        checkArgument(length <= 4,
                "octal escapes are 1-3 digits, got %s for %s", length,
                octalEsc);
        String digits = octalEsc.substring(1);
        checkArgument(digits.codePoints().allMatch(x -> '0' <= x && x <= '7'),
                "non-octal digits in octal escape %s", octalEsc);
        if (length == 2 || length == 3) {
            // One or two digits
            return (char) Integer.parseInt(digits, 8);
        } else /* if (length == 4) */ {
            assert length == 4;
            // Three digits, 3rd must be <= 3
            checkArgument(digits.charAt(0) <= '3',
                    "first digit in 3-digit octal escape must be <= 3: %s",
                    octalEsc);
            return (char) Integer.parseInt(digits, 8);
        }
    }

    // From JavaPoet with slight modifications to newline logic
    /*
     * Copyright (C) 2015 Square, Inc.
     *
     * Licensed under the Apache License, Version 2.0 (the "License"); you may
     * not use this file except in compliance with the License. You may obtain a
     * copy of the License at
     *
     * http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
     * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
     * License for the specific language governing permissions and limitations
     * under the License.
     */
    private static String characterLiteralWithoutSingleQuotes(char c) {
        // see
        // https://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.10.6
        switch (c) {
            case '\b':
                return "\\b"; /* \u0008: backspace (BS) */
            case '\t':
                return "\\t"; /* \u0009: horizontal tab (HT) */
            case '\n':
                return "\\n"; /* \u000a: linefeed (LF) */
            case '\f':
                return "\\f"; /* \u000c: form feed (FF) */
            case '\r':
                return "\\r"; /* \u000d: carriage return (CR) */
            case '\"':
                return "\""; /* \u0022: double quote (") */
            case '\'':
                return "\\'"; /* \u0027: single quote (') */
            case '\\':
                return "\\\\"; /* \u005c: backslash (\) */
            default:
                return Character.isISOControl(c)
                        ? String.format("\\u%04x", (int) c)
                        : Character.toString(c);
        }
    }

    /**
     * Returns the string literal representing {@code value}, including wrapping
     * double quotes.
     */
    private static String stringLiteralWithDoubleQuotes(String value) {
        StringBuilder result = new StringBuilder(value.length() + 2);
        result.append('"');
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            // trivial case: single quote must not be escaped
            if (c == '\'') {
                result.append("'");
                continue;
            }
            // trivial case: double quotes must be escaped
            if (c == '\"') {
                result.append("\\\"");
                continue;
            }
            // default case: just let character literal do its work
            result.append(characterLiteralWithoutSingleQuotes(c));
        }
        result.append('"');
        return result.toString();
    }

    private StringUtil() {
        throw new AssertionError();
    }

}
