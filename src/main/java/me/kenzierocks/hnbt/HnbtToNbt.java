package me.kenzierocks.hnbt;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.jnbt.CompoundTag;

import com.google.common.base.Throwables;

import me.kenzierocks.hnbt.grammar.HNBTLexer;
import me.kenzierocks.hnbt.grammar.HNBTParser;
import me.kenzierocks.hnbt.grammar.HNBTParserBaseListener;
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
            List<HNBTParsingException> errorNodeErrors = new ArrayList<>(1);
            parser.addParseListener(new HNBTParserBaseListener() {

                @Override
                public void visitErrorNode(ErrorNode node) {
                    errorNodeErrors.add(
                            new HNBTParsingException("Error node " + node));
                }
            });
            CompoundTag tag = parser.root().rootTag;
            List<Exception> errors = Stream
                    .concat(cap.getErrors().stream(), errorNodeErrors.stream())
                    .collect(Collectors.toList());
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
            throw Throwables.propagate(e);
        }
    }

    private HnbtToNbt() {
        throw new AssertionError();
    }

}
