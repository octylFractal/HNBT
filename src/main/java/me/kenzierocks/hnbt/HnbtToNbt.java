package me.kenzierocks.hnbt;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.jnbt.CompoundTag;

import me.kenzierocks.hnbt.grammar.HNBTLexer;
import me.kenzierocks.hnbt.grammar.HNBTParser;
import me.kenzierocks.hnbt.util.CaptureErrorsListenener;

public final class HnbtToNbt {

    public static CompoundTag parseHnbtIntoNbt(String hnbt)
            throws HNBTParsingException {
        return parseHnbtIntoNbt(new ANTLRInputStream(hnbt));
    }

    public static CompoundTag parseHnbtIntoNbt(InputStream hnbt)
            throws IOException, HNBTParsingException {
        return parseHnbtIntoNbt(new ANTLRInputStream(hnbt));
    }

    public static CompoundTag parseHnbtIntoNbt(Reader hnbt)
            throws IOException, HNBTParsingException {
        return parseHnbtIntoNbt(new ANTLRInputStream(hnbt));
    }

    public static CompoundTag parseHnbtIntoNbt(ANTLRInputStream hnbt)
            throws HNBTParsingException {
        HNBTLexer lexer = new HNBTLexer(hnbt);
        lexer.removeErrorListeners();
        CaptureErrorsListenener cap = new CaptureErrorsListenener();
        lexer.addErrorListener(cap);
        CommonTokenStream comTokStream = new CommonTokenStream(lexer);
        HNBTParser parser = new HNBTParser(comTokStream);
        parser.removeErrorListeners();
        parser.addErrorListener(cap);
        try {
            CompoundTag tag = parser.root().rootTag;
            List<RecognitionException> errors = cap.getErrors();
            if (!errors.isEmpty()) {
                HNBTParsingException ex =
                        new HNBTParsingException("Lexer errors occured");
                errors.forEach(ex::addSuppressed);
                throw ex;
            }
            return tag;
        } catch (Exception e) {
            if (e instanceof HNBTParsingException) {
                throw e;
            }
            HNBTParsingException ex = new HNBTParsingException(e);
            List<RecognitionException> errors = cap.getErrors();
            errors.forEach(ex::addSuppressed);
            throw ex;
        }
    }

    private HnbtToNbt() {
        throw new AssertionError();
    }

}
