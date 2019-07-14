package io.github.yedaxia.musicnote.media;

import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import io.github.yedaxia.musicnote.data.sp.SpConfig;
import io.github.yedaxia.musicnote.util.IOUtils;

import static io.github.yedaxia.musicnote.media.AudioConfig.AUDIO_ENCODING;
import static io.github.yedaxia.musicnote.media.AudioConfig.AUDIO_IN_CHANNEL;
import static io.github.yedaxia.musicnote.media.AudioConfig.AUDIO_SAMPLE_RATE;

/**
 * 音乐录音
 * @author Darcy https://yedaxia.github.io/
 * @version 2018/2/4.
 */

public class MusicRecorder {

    private final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;

    private static final int STATUS_NOT_READY = 0x0;
    private static final int STATUS_READY = 0x1;
    private static final int STATUS_RECORDING = 0x2;
    private static final int STATUS_STOP = 0x3;

    private AudioRecord audioRecord;
    private File outputFile;
    private volatile int status = STATUS_NOT_READY;

    private OnRecordListener onRecordListener;
    private int frameBufferSize;
    private int recordAdjustLen;

    public MusicRecorder(File outputFile, int frameBufferSize) {
        this.outputFile = outputFile;
        this.frameBufferSize = frameBufferSize;
        this.recordAdjustLen = SpConfig.sp().getRecordAdjustLen();
    }

    public void setOnRecordListener(OnRecordListener onRecordListener) {
        this.onRecordListener = onRecordListener;
    }

    /**
     * 开始录音
     */
    public void start(){
        if(audioRecord == null){
            initAudioRecord();
        }

        if(status == STATUS_NOT_READY
                || status == STATUS_RECORDING){
            return;
        }

        audioRecord.startRecording();
        saveToDisk();
    }

    /**
     * 暂停录音
     */
    public void stop(){

        if(status != STATUS_RECORDING){
            return;
        }

        status = STATUS_STOP;
        audioRecord.stop();
    }


    /**
     * 释放资源
     */
    public void release(){
        if(audioRecord != null){
            audioRecord.release();
            audioRecord = null;
        }
    }

    private void initAudioRecord() {
        this.audioRecord = AudioUtils.createAudioRecord();
        this.status = STATUS_READY;
    }

    private void saveToDisk(){
        new Thread(){
            @Override
            public void run() {
                FileOutputStream tempAudioFile = null;
                FileChannel fileChannel = null;
                try{
                    tempAudioFile = new FileOutputStream(outputFile, true);
                    fileChannel =  tempAudioFile.getChannel();
                    final int bufferSize  = frameBufferSize;
                    int readSize;
                    ByteBuffer buffer = ByteBuffer.allocateDirect(bufferSize);
                    status = STATUS_RECORDING;
                    if(onRecordListener != null){
                        onRecordListener.onRecordStart();
                    }

                    //跳过校准字节
                    audioRecord.read(new byte[recordAdjustLen], 0, recordAdjustLen);

                    while (status == STATUS_RECORDING) {
                        buffer.clear();
                        readSize = audioRecord.read(buffer, bufferSize);
                        if (readSize > 0) {
                            if(onRecordListener != null){
                                buffer.limit(bufferSize);
                                buffer.rewind();
                                onRecordListener.onRecording(buffer);
                            }
                            buffer.position(bufferSize);
                            buffer.flip();
                            fileChannel.write(buffer);
                        }
                    }
                }catch (IOException ex){
                    ex.printStackTrace();
                }finally {
                    IOUtils.closeSilently(fileChannel);
                    IOUtils.closeSilently(tempAudioFile);
                }
            }
        }.start();
    }

    public interface OnRecordListener{

        void onRecordStart();

        void onRecording(ByteBuffer data);
    }
}
