package me.kenzierocks.hnbt;

import static org.junit.Assert.*;

import org.junit.Test;

import me.kenzierocks.hnbt.util.ByteList;

public class ByteListTest {

    @Test
    public void addNothing() throws Exception {
        ByteList list = new ByteList();
        assertEquals(0, list.toArray().length);
    }

    @Test
    public void addOneByte() throws Exception {
        ByteList list = new ByteList();
        list.add((byte) 123);
        byte[] data = list.toArray();
        assertEquals(1, data.length);
        assertEquals(123, data[0]);
    }

    @Test
    public void forceExpand() throws Exception {
        ByteList list = new ByteList();
        for (int i = 0; i < 100; i++) {
            list.add((byte) 123);
        }
        byte[] data = list.toArray();
        assertEquals(100, data.length);
        int sum = 0;
        for (int i = 0; i < data.length; i++) {
            byte b = data[i];
            sum += b;
        }
        assertEquals(123 * 100, sum);
        assertEquals(123, data[0]);
    }

}
