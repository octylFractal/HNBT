package me.kenzierocks.hnbt;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.Token;
import org.jnbt.ByteArrayTag;
import org.jnbt.ByteTag;
import org.jnbt.CompoundTag;
import org.jnbt.DoubleTag;
import org.jnbt.FloatTag;
import org.jnbt.IntArrayTag;
import org.jnbt.IntTag;
import org.jnbt.ListTag;
import org.jnbt.LongTag;
import org.jnbt.ShortTag;
import org.jnbt.StringTag;
import org.jnbt.Tag;

import com.google.common.base.Strings;

import me.kenzierocks.hnbt.grammar.HNBTLexer;
import me.kenzierocks.hnbt.util.IndentAddingAppendable;
import me.kenzierocks.hnbt.util.StringUtil;

final class HnbtSerializer {

    private static final String FOUR_SPACES = Strings.repeat(" ", 4).intern();

    private final Appendable target;
    private final String indentString = FOUR_SPACES; // TODO: configurable
    private transient HnbtSerializer indented;

    HnbtSerializer(Appendable target) {
        this.target = checkNotNull(target);
    }

    private HnbtSerializer getIndentedSerializer() {
        if (this.indented == null) {
            this.indented = new HnbtSerializer(
                    new IndentAddingAppendable(this.indentString, this.target));
        }
        return this.indented;
    }

    private void writePreTag(String type, @Nullable String name)
            throws IOException {
        this.target.append(type);
        if (name != null) {
            ANTLRInputStream data = new ANTLRInputStream(name);
            HNBTLexer lexer = new HNBTLexer(data);
            List<? extends Token> allTokens = lexer.getAllTokens();
            checkArgument(!allTokens.isEmpty(), "name is invalid: %s", name);
            checkArgument(allTokens.get(0).getType() == HNBTLexer.TagName,
                    "name is invalid: %s", name);
            this.target.append(' ').append(name);
        }
        this.target.append(" = ");
    }

    private void writeSimpleTag(String type, Object value,
            @Nullable String name) throws IOException {
        checkNotNull(type);
        checkNotNull(value);
        writePreTag(type, name);
        this.target.append(value.toString());
    }

    private void writeListLikeTag(String type, Stream<? extends Object> values,
            @Nullable String name) throws IOException {
        checkNotNull(type);
        checkNotNull(values);
        writePreTag(type, name);
        this.target.append('[');
        HnbtSerializer subserializer = getIndentedSerializer();
        Iterator<? extends Object> iter = values.iterator();
        if (iter.hasNext()) {
            subserializer.target.append('\n');
        }
        while (iter.hasNext()) {
            Object obj = iter.next();
            checkNotNull(obj);

            if (obj instanceof Tag) {
                subserializer.writeAnyTag((Tag) obj, null);
            } else {
                subserializer.target.append(obj.toString());
            }
            if (iter.hasNext()) {
                subserializer.target.append(',');
            }
            subserializer.target.append('\n');
        }
        this.target.append(']');
    }

    void writeAnyTag(Tag tag, @Nullable String name) throws IOException {
        checkNotNull(tag);
        if (tag instanceof CompoundTag) {
            writeCompoundTag((CompoundTag) tag, name);
        } else if (tag instanceof ListTag) {
            writeListTag((ListTag) tag, name);
        } else if (tag instanceof ByteTag) {
            writeByteTag((ByteTag) tag, name);
        } else if (tag instanceof ByteArrayTag) {
            writeByteArrayTag((ByteArrayTag) tag, name);
        } else if (tag instanceof ShortTag) {
            writeShortTag((ShortTag) tag, name);
        } else if (tag instanceof IntTag) {
            writeIntTag((IntTag) tag, name);
        } else if (tag instanceof IntArrayTag) {
            writeIntArrayTag((IntArrayTag) tag, name);
        } else if (tag instanceof LongTag) {
            writeLongTag((LongTag) tag, name);
        } else if (tag instanceof FloatTag) {
            writeFloatTag((FloatTag) tag, name);
        } else if (tag instanceof DoubleTag) {
            writeDoubleTag((DoubleTag) tag, name);
        } else if (tag instanceof StringTag) {
            writeStringTag((StringTag) tag, name);
        } else {
            throw new IllegalArgumentException(
                    "Don't know how to serialize a " + tag.getClass());
        }
    }

    void writeCompoundTag(CompoundTag nbt, @Nullable String name)
            throws IOException {
        checkNotNull(nbt);
        writePreTag("compound", name);
        this.target.append('{');
        HnbtSerializer subserializer = getIndentedSerializer();
        if (!nbt.getValue().isEmpty()) {
            subserializer.target.append('\n');
        }
        for (Iterator<Entry<String, Tag>> iter =
                nbt.getValue().entrySet().iterator(); iter.hasNext();) {
            Entry<String, Tag> tagEntry = iter.next();

            subserializer.writeAnyTag(tagEntry.getValue(), tagEntry.getKey());
            if (iter.hasNext()) {
                subserializer.target.append(',');
            }
            subserializer.target.append('\n');
        }
        this.target.append('}');
    }

    void writeListTag(ListTag nbt, @Nullable String name) throws IOException {
        checkNotNull(nbt);
        writeListLikeTag("list", nbt.getValue().stream(), name);
    }

    void writeByteTag(ByteTag nbt, @Nullable String name) throws IOException {
        checkNotNull(nbt);
        writeSimpleTag("byte", nbt.getValue(), name);
    }

    void writeByteArrayTag(ByteArrayTag nbt, @Nullable String name)
            throws IOException {
        checkNotNull(nbt);
        byte[] val = nbt.getValue();
        writeListLikeTag("byte-array",
                IntStream.range(0, val.length).mapToObj(x -> val[x]), name);
    }

    void writeShortTag(ShortTag nbt, @Nullable String name) throws IOException {
        checkNotNull(nbt);
        writeSimpleTag("short", nbt.getValue(), name);
    }

    void writeIntTag(IntTag nbt, @Nullable String name) throws IOException {
        checkNotNull(nbt);
        writeSimpleTag("int", nbt.getValue(), name);
    }

    void writeIntArrayTag(IntArrayTag nbt, @Nullable String name)
            throws IOException {
        checkNotNull(nbt);
        writeListLikeTag("int-array", IntStream.of(nbt.getValue()).boxed(),
                name);
    }

    void writeLongTag(LongTag nbt, @Nullable String name) throws IOException {
        checkNotNull(nbt);
        writeSimpleTag("long", nbt.getValue(), name);
    }

    void writeFloatTag(FloatTag nbt, @Nullable String name) throws IOException {
        checkNotNull(nbt);
        writeSimpleTag("float", nbt.getValue(), name);
    }

    void writeDoubleTag(DoubleTag nbt, @Nullable String name)
            throws IOException {
        checkNotNull(nbt);
        writeSimpleTag("double", nbt.getValue(), name);
    }

    void writeStringTag(StringTag nbt, @Nullable String name)
            throws IOException {
        checkNotNull(nbt);
        writeSimpleTag("string", StringUtil.escapedString(nbt.getValue()),
                name);
    }

}
