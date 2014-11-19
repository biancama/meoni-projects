package com.biancama.utils.gui;

import java.io.UnsupportedEncodingException;
import java.nio.Buffer;
import java.nio.ByteBuffer;

import com.biancama.log.BiancaLogger;

public class DynByteBuffer {
    private ByteBuffer buffer;

    public DynByteBuffer(int l) {
        this.buffer = ByteBuffer.allocateDirect(l);
    }

    public void put(byte[] buffer, int read) {
        checkBufferSize(read);
        this.buffer.put(buffer);
    }

    public void clear() {
        this.buffer.clear();
    }

    @Override
    public String toString() {
        return new String(this.getLast(buffer.position()));
    }

    public byte[] toByteArray() {
        return this.getLast(buffer.position());
    }

    public String toString(String codepage) {

        try {
            return new String(this.getLast(buffer.position()), codepage);
        } catch (UnsupportedEncodingException e) {
            BiancaLogger.exception(e);
            return new String(this.getLast(buffer.position()));
        }
    }

    public int capacity() {
        return this.buffer.capacity();
    }

    public int limit() {
        return this.buffer.limit();
    }

    public int position() {
        return this.buffer.position();
    }

    private void checkBufferSize(int read) {
        if (this.buffer.remaining() < read) {
            ByteBuffer newbuffer = ByteBuffer.allocateDirect(this.buffer.capacity() * 2);
            this.buffer.flip();
            newbuffer.put(this.buffer);
            this.buffer = newbuffer;
        }
    }

    public byte get() {
        return buffer.get();
    }

    public Buffer flip() {
        return this.buffer.flip();
    }

    public ByteBuffer compact() {
        return this.buffer.compact();
    }

    public byte[] getLast(int num) {
        int posi = buffer.position();
        num = Math.min(posi, num);
        buffer.position(posi - num);
        byte[] b = new byte[num];
        buffer.get(b);
        buffer.position(posi);
        return b;
    }

    public byte[] getSub(int start, int end) {
        int posi = buffer.position();
        buffer.position(start);
        byte[] b = new byte[end - start];
        buffer.get(b);
        buffer.position(posi);
        return b;
    }

}
