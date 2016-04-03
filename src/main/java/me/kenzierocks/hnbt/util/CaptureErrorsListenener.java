package me.kenzierocks.hnbt.util;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import com.google.common.collect.ImmutableList;

public class CaptureErrorsListenener extends BaseErrorListener {

    private final List<RecognitionException> errors = new ArrayList<>(1);

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
            int line, int charPositionInLine, String msg,
            RecognitionException e) {
        if (e == null) {
            return;
        }
        String message = String.format("occured at line %s at pos %s, msg='%s'",
                line, charPositionInLine, msg);
        if (recognizer instanceof Lexer) {
            Lexer lex = ((Lexer) recognizer);
            message += ", mode=" + lex.getModeNames()[lex._mode];
        }
        e.addSuppressed(new RuntimeException(
                "Info from syntaxError method: " + message));
        this.errors.add(e);
    }

    public List<RecognitionException> getErrors() {
        return ImmutableList.copyOf(this.errors);
    }

    public void clearErrors() {
        this.errors.clear();
    }

}
