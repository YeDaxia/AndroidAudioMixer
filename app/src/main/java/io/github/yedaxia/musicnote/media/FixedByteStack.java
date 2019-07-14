package io.github.yedaxia.musicnote.media;

/**
 * 定长
 *
 * @author Darcy https://yedaxia.github.io/
 * @version 2018/2/8.
 */

public class FixedByteStack {

    private final int capacity;
    private byte[] data;
    private int current;

    public FixedByteStack(int capacity) {
        this.data = new byte[capacity];
        this.capacity = capacity;
    }

    public void pushBytes(byte[] bytes){

        if(bytes == null){
            return;
        }

        int total = this.current + bytes.length;

        if(total > capacity){
            int offset = bytes.length;
            for(int i = offset; i != capacity; i ++){
                data[i - offset] = data[i];
            }
            System.arraycopy(bytes, 0, data, capacity -  bytes.length , bytes.length);
        }else{
            System.arraycopy(bytes, 0, data, current ,bytes.length);
        }

        current = total;
    }

    public byte[] getData() {
        return data;
    }
}
