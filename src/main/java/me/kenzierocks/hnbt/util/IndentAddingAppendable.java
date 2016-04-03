package me.kenzierocks.hnbt.util;

import java.io.IOException;

public class IndentAddingAppendable implements Appendable {

    private final String indentString;
    private final Appendable delegate;
    private boolean lastWasNewline = false;

    public IndentAddingAppendable(String indentString, Appendable delegate) {
        this.indentString = indentString;
        this.delegate = delegate;
    }

    @Override
    public Appendable append(CharSequence csq) throws IOException {
        append(csq, 0, csq.length());
        return this;
    }

    @Override
    public Appendable append(CharSequence csq, int start, int end)
            throws IOException {
        for (int i = start; i < end; i++) {
            append(csq.charAt(i));
        }
        return this;
    }

    @Override
    public Appendable append(char c) throws IOException {
        if (this.lastWasNewline && c != '\n') {
            this.delegate.append(this.indentString);
        }
        this.delegate.append(c);
        this.lastWasNewline = c == '\n';
        return this;
    }

}
