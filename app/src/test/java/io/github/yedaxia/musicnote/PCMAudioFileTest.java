package io.github.yedaxia.musicnote;

import junit.framework.Assert;

import org.junit.Test;

import java.io.IOException;

import io.github.yedaxia.musicnote.media.MultiAudioMixer;
import io.github.yedaxia.musicnote.media.PCMAnalyser;


/**
 * @author Darcy https://yedaxia.github.io/
 * @version 2018/2/21.
 */

public class PCMAudioFileTest {

    @Test
    public void test_readFile()throws IOException{
        Assert.assertEquals("hello", 100, 101, 10);
    }
}
