package io.github.yedaxia.musicnote;

import org.junit.Assert;
import org.junit.Test;

import io.github.yedaxia.musicnote.media.FixedByteStack;

/**
 * @author Darcy https://yedaxia.github.io/
 * @version 2018/2/8.
 */

public class FixedByteStackTest {

    @Test
    public void test_pushBytes(){
        FixedByteStack byteStack = new FixedByteStack(10);

        byte[] bytes1 = stringToBytes("12345");
        byteStack.pushBytes(bytes1);
        Assert.assertArrayEquals(byteStack.getData(), stringToBytes("1234500000"));

        byte[] bytes2 = stringToBytes("56789");
        byteStack.pushBytes(bytes2);

        Assert.assertArrayEquals(byteStack.getData(), stringToBytes("1234556789"));

        byte[] bytes3 = stringToBytes("123");
        byteStack.pushBytes(bytes3);
        Assert.assertArrayEquals(byteStack.getData(), stringToBytes("4556789123"));
    }

    private byte[] stringToBytes(String byteStr){
        byte[] data = new byte[byteStr.length()];
        for(int i = 0; i != byteStr.length(); i++){
            data[i] = Byte.valueOf(""+byteStr.charAt(i));
        }
        return data;
    }
}
