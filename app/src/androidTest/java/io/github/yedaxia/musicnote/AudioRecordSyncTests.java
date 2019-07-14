package io.github.yedaxia.musicnote;

import android.media.AudioRecord;
import android.media.AudioTrack;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

import io.github.yedaxia.musicnote.media.AudioUtils;
import io.github.yedaxia.musicnote.media.PCMAnalyser;

/**
 * @author Darcy https://yedaxia.github.io/
 * @version 2018/3/17.
 */
@RunWith(AndroidJUnit4.class)
public class AudioRecordSyncTests {


    private static final String TAG = "AudioRecordSyncTests";


    AtomicInteger finishThreadCount = new AtomicInteger();

    AudioTrack audioTrack;
    AudioRecord audioRecord;

    int playBytesLen;
    PCMAnalyser pcmAnalyser;

    @Before
    public void setUp() throws InterruptedException {
        audioTrack = AudioUtils.createTrack(1);
        audioRecord = AudioUtils.createAudioRecord();
        pcmAnalyser = PCMAnalyser.createPCMAnalyser();
        playBytesLen = pcmAnalyser.bytesPerSecond() * 2;
    }

    @Test
    public void testRecord() throws InterruptedException {
        audioRecord.startRecording();
        final int readBufferSize = playBytesLen; // 2 秒的长度
        byte[] readBuffer = new byte[readBufferSize];
        long startRecordTime = System.currentTimeMillis();
        int ret = audioRecord.read(readBuffer, 0, readBufferSize);
        Log.i(TAG, "record thread cost time : " + (System.currentTimeMillis() - startRecordTime) + "，totalReadByteLen: "+ ret);
        audioRecord.release();
    }

    @Test
    public void testPlay(){
        byte[] playCostBytes = new byte[playBytesLen]; //2秒的数据
        audioTrack.play();
        final long startWriteTime = System.currentTimeMillis();
        audioTrack.setNotificationMarkerPosition(playCostBytes.length / 2); // 88200
        audioTrack.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {
            @Override
            public void onMarkerReached(AudioTrack track) {
                Log.i(TAG, "play thread cost time : " + (System.currentTimeMillis() - startWriteTime));
            }
            @Override
            public void onPeriodicNotification(AudioTrack track) {}
        });

        audioTrack.write(playCostBytes, 0, playCostBytes.length);
        sleep(3000);
        Log.i(TAG,"onPeriodicNotification getPlaybackHeadPosition: "+ audioTrack.getPlaybackHeadPosition());
        audioTrack.release();
    }

    private void sleep(int milSeconds){
        try {
            Thread.sleep(milSeconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class PlayThread extends Thread{
        @Override
        public void run() {
            byte[] playCostBytes = new byte[playBytesLen]; //2秒的数据
            long startRecordTime = System.currentTimeMillis();
            audioTrack.write(playCostBytes, 0, playCostBytes.length);
            audioTrack.release();
            Log.i(TAG, "play thread cost time : " + (System.currentTimeMillis() - startRecordTime));
            finishThreadCount.incrementAndGet();
        }
    }

    class RecordThread extends Thread{
        @Override
        public void run() {
            final int readBufferSize = playBytesLen; // 2 秒的长度
            byte[] readBuffer = new byte[readBufferSize];
            long startRecordTime = System.currentTimeMillis();
            int ret = audioRecord.read(readBuffer, 0, readBufferSize);
            Log.i(TAG, "record thread cost time : " + (System.currentTimeMillis() - startRecordTime) + "，totalReadByteLen: "+ ret);
            audioRecord.release();
            finishThreadCount.incrementAndGet();
        }
    }

}
