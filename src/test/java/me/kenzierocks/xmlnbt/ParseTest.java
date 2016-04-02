package me.kenzierocks.xmlnbt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.jnbt.CompoundTag;
import org.jnbt.IntTag;
import org.jnbt.ListTag;
import org.jnbt.ShortTag;
import org.jnbt.StringTag;
import org.jnbt.Tag;
import org.junit.Test;

import me.kenzierocks.hnbt.grammar.HNBTLexer;
import me.kenzierocks.hnbt.grammar.HNBTParser;

public class ParseTest {

    private static ANTLRInputStream getANTLRStream(String fileName)
            throws IOException {
        InputStream data = ParseTest.class.getResourceAsStream("/" + fileName);
        return new ANTLRInputStream(data);
    }

    @Test
    public void simpleCompound() throws Exception {
        ANTLRInputStream in = getANTLRStream("test.hnbt");
        HNBTLexer lexer = new HNBTLexer(in);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        HNBTParser parser = new HNBTParser(tokens);
        CompoundTag rootTag = parser.root().rootTag;

        assertEquals("root", rootTag.getName());
        Map<String, Tag> tags = rootTag.getValue();

        Tag list = tags.get("foobar");
        assertNotNull(list);
        assertEquals(ListTag.class, list.getClass());
        ListTag listTag = (ListTag) list;
        assertEquals(IntTag.class, listTag.getType());
        assertEquals(1, listTag.getValue().size());
        assertEquals(42, (listTag.getValue().get(0).getValue()));

        Tag theQuestion = tags.get("theQuestion");
        assertNotNull(theQuestion);
        assertEquals(StringTag.class, theQuestion.getClass());
        assertEquals("the question is not available in your country",
                ((StringTag) theQuestion).getValue());

        Tag special = tags.get("special");
        assertNotNull(special);
        assertEquals(StringTag.class, special.getClass());
        assertEquals("not really \"special\"",
                ((StringTag) special).getValue());

        Tag nested = tags.get("nested");
        assertNotNull(nested);
        assertEquals(CompoundTag.class, nested.getClass());
        Map<String, Tag> nestedTags = ((CompoundTag) nested).getValue();
        Tag cut = nestedTags.get("cut");
        assertNotNull(cut);
        assertEquals(ShortTag.class, cut.getClass());
        assertEquals(4, ((ShortTag) cut).getValue().shortValue());
    }

}
