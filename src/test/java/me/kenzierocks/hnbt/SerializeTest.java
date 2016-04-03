package me.kenzierocks.hnbt;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
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
import com.google.common.io.ByteStreams;

public class SerializeTest {

    private static String getHnbtExpected(String fileName)
            throws IOException, HNBTParsingException {
        InputStream data =
                SerializeTest.class.getResourceAsStream("/" + fileName);
        return StandardCharsets.UTF_8
                .decode(ByteBuffer.wrap(ByteStreams.toByteArray(data)))
                .toString();
    }

    @Test
    public void emptyCompound() throws Exception {
        String expected = getHnbtExpected("empty_expected.hnbt");
        Map<String, Tag> rootData = new HashMap<>();
        CompoundTag tag = new CompoundTag("root", rootData);
        assertEquals(expected, NbtToHnbt.parseNbtIntoHnbt(tag));
    }

    @Test
    public void oneStringInCompound() throws Exception {
        String expected = getHnbtExpected("one_s_expected.hnbt");
        Map<String, Tag> rootData = new HashMap<>();
        rootData.put("stringOne", new StringTag("stringOne", "1"));
        CompoundTag tag = new CompoundTag("root", rootData);
        assertEquals(expected, NbtToHnbt.parseNbtIntoHnbt(tag));
    }

    @Test
    public void allTypesInCompound() throws Exception {
        String expected = getHnbtExpected("all_types_expected.hnbt");
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
        CompoundTag tag = new CompoundTag("root", rootData);
        assertEquals(expected, NbtToHnbt.parseNbtIntoHnbt(tag));
    }

}
