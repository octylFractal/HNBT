package me.kenzierocks.hnbt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jnbt.ByteArrayTag;
import org.jnbt.ByteTag;
import org.jnbt.CompoundTag;
import org.jnbt.DoubleTag;
import org.jnbt.EndTag;
import org.jnbt.FloatTag;
import org.jnbt.IntArrayTag;
import org.jnbt.IntTag;
import org.jnbt.ListTag;
import org.jnbt.LongTag;
import org.jnbt.ShortTag;
import org.jnbt.StringTag;
import org.jnbt.Tag;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class ParseTest {

    private static CompoundTag parseWithHnbtToNbt(String fileName)
            throws IOException, HNBTParsingException {
        InputStream data = ParseTest.class.getResourceAsStream("/" + fileName);
        return HnbtToNbt.parseHnbtIntoNbt(data);
    }

    @Test
    public void simpleCompound() throws Exception {
        CompoundTag rootTag = parseWithHnbtToNbt("test.hnbt");

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

    @Test
    public void allTypesInCompound() throws Exception {
        CompoundTag tag = parseWithHnbtToNbt("all_types_expected.hnbt");
        Map<String, Tag> rootData = new LinkedHashMap<>();
        rootData.put("list",
                new ListTag("list", EndTag.class, Collections.emptyList()));
        rootData.put("byte", new ByteTag("byte", Byte.MAX_VALUE));
        rootData.put("byte-array", new ByteArrayTag("byte-array", new byte[0]));
        rootData.put("short", new ShortTag("short", Short.MAX_VALUE));
        rootData.put("int", new IntTag("int", Integer.MAX_VALUE));
        rootData.put("int-array", new IntArrayTag("int-array", new int[0]));
        rootData.put("long", new LongTag("long", Long.MAX_VALUE));
        rootData.put("float", new FloatTag("float", 42.0f));
        rootData.put("double", new DoubleTag("double", 42.5));
        rootData.put("string", new StringTag("string", "\n to you too"));
        rootData.put("compound", new CompoundTag("compound",
                ImmutableMap.of("nested", new StringTag("nested", "lvl up!"))));
        CompoundTag expected = new CompoundTag("root", rootData);
        assertEquals(expected, tag);
    }

    @Test
    public void badTokens() throws Exception {
        try {
            CompoundTag rootTag = parseWithHnbtToNbt("bad_tokens.hnbt");
            fail("Parsed incorrectly into " + rootTag);
        } catch (HNBTParsingException expected) {
            // ok.
        }
    }

    @Test
    public void badSyntax() throws Exception {
        try {
            CompoundTag rootTag = parseWithHnbtToNbt("bad_syntax.hnbt");
            fail("Parsed incorrectly into " + rootTag);
        } catch (HNBTParsingException expected) {
            // ok.
        }
    }

}
