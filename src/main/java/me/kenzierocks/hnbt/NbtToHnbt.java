package me.kenzierocks.hnbt;

import java.io.CharArrayWriter;
import java.io.IOException;

import org.jnbt.CompoundTag;

public final class NbtToHnbt {

    public static String parseNbtIntoHnbt(CompoundTag nbt) {
        CharArrayWriter appendable = new CharArrayWriter();
        try {
            parseNbtIntoHnbt(nbt, appendable);
        } catch (IOException e) {
            throw new IllegalStateException("impossible exception", e);
        }
        return appendable.toString();
    }

    public static void parseNbtIntoHnbt(CompoundTag nbt, Appendable target)
            throws IOException {
        new HnbtSerializer(target).writeCompoundTag(nbt, "root");
        target.append('\n');
    }

    private NbtToHnbt() {
        throw new AssertionError();
    }

}
