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
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.jnbt.CompoundTag;

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
        parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
        CompoundTag tag = null;
        try {
            tag = parser.root().rootTag;
        } catch (Exception e) {
            comTokStream.reset();
            parser.reset();
            errorNodeErrors.clear();
            cap.clearErrors();
            parser.getInterpreter().setPredictionMode(PredictionMode.LL);
            tag = parser.root().rootTag;
        }
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
    }

    private HnbtToNbt() {
        throw new AssertionError();
    }

}
