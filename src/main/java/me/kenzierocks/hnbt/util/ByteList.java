package me.kenzierocks.hnbt.util;

import java.util.Arrays;

/**
 * A stripped down version of List for the grammar parser.
 */
public final class ByteList {

    /**
     * The maximum size of array to allocate. Some VMs reserve some header words
     * in an array. Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * Increases the capacity to ensure that it can hold at least the number of
     * elements specified by the minimum capacity argument.
     *
     * @param minCapacity
     *            the desired minimum capacity
     */
    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = this.data.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is a win:
        this.data = Arrays.copyOf(this.data, newCapacity);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE
                : MAX_ARRAY_SIZE;
    }

    private byte[] data;
    private int size;

    public ByteList() {
        this.data = new byte[0];
    }

    public ByteList(byte[] init) {
        this.data = init.clone();
    }

    // growth code shamelessly stolen from ArrayList
    private void ensureCapacity(int minCapacity) {
        // Convert minCapacity to the bytes needed
        int off = minCapacity % 8;
        minCapacity /= Byte.SIZE;
        if (off > 0) {
            minCapacity += 8;
        }
        // overflow-conscious code
        if (minCapacity - this.data.length > 0) {
            grow(minCapacity);
        }
    }

    public void add(byte b) {
        ensureCapacity(this.size + 1);
        this.data[this.size] = b;
        this.size++;
    }

    public byte[] toArray() {
        return Arrays.copyOf(this.data, this.size);
    }

}
