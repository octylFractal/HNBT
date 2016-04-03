package me.kenzierocks.hnbt;

import static org.junit.Assert.*;

import org.junit.Test;

import me.kenzierocks.hnbt.util.StringUtil;

public class StringParseTest {

    private static void assertParse(String expected, String input) {
        assertEquals(expected, StringUtil.unescapeString(input));
    }

    @Test
    public void emptyString() throws Exception {
        assertParse("", "");
    }

    @Test
    public void simpleString() throws Exception {
        assertParse("foobar", "foobar");
    }

    @Test
    public void regularEscapes() throws Exception {
        assertParse("\b\t\r\n\"\'\\", "\\b\\t\\r\\n\\\"\\'\\\\");
    }

    @Test
    public void unicodeEscapes() throws Exception {
        assertParse("\u2001ASpaceOdyssey\uuuuuu1337",
                "\\u2001ASpaceOdyssey\\uuuuuuu1337");
    }

    @Test
    public void octalEscapes() throws Exception {
        assertParse("ABC", "\\101\\102\\103");
    }

}
