package io.github.yedaxia.musicnote.activity;

import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.yedaxia.musicnote.R;
import io.github.yedaxia.musicnote.app.util.AppUtils;
import io.github.yedaxia.musicnote.app.util.ToastUtils;
import io.github.yedaxia.musicnote.data.sp.SpConfig;
import io.github.yedaxia.musicnote.media.AudioConfig;
import io.github.yedaxia.musicnote.media.AudioUtils;
import io.github.yedaxia.musicnote.media.PCMAnalyser;

/**
 * @author Darcy https://yedaxia.github.io/
 * @version 2018/3/3.
 */

public class AdjustRecordActivity extends BaseActivity {

    private static final String TAG = "AdjustRecordActivity";

    private static final int STATUS_PLAY_PREPARE = 1;
    private static final int STATUS_PLAYING =2;

    /**
     * 分析长度
     */
    private static final int ANALYZE_BEAT_LEN = 8;


    @BindView(R.id.tv_result)
    TextView tvResult;

    private AudioTrack audioTrack;
    private AudioRecord audioRecord;
    private byte[] playBeatBytes;

    private PCMAnalyser pcmAudioFile;
    private BeatPlayHandler beatPlayHandler;
    private boolean stopPlay;

    private int playStatus;
    private int readRecordBytesLen;
    private int beatFirstSoundBytePos;
    private int errorRangeByteLen;

    private boolean isFirstRecordWrite;
    private boolean isFirstPlayWrite;
    private CyclicBarrier recordBarrier = new CyclicBarrier(2);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjust);
        ButterKnife.bind(this);
        enableBack();
        setTitle(R.string.adjust_record);
        audioTrack =  AudioUtils.createTrack(AudioConfig.BEAT_CHANNEL_COUNT);
        pcmAudioFile = PCMAnalyser.createPCMAnalyser(AudioConfig.BEAT_CHANNEL_COUNT);
        audioRecord = AudioUtils.createAudioRecord();
        HandlerThread playBeatHandlerThread = new HandlerThread("PlayBeatHandlerThread");
        playBeatHandlerThread.start();
        beatPlayHandler = new BeatPlayHandler(playBeatHandlerThread.getLooper());
        loadBeatData();
    }

    @OnClick(R.id.btn_play_beat)
    void onStartPlayBeat(View v) {
        if(playStatus == STATUS_PLAY_PREPARE){
            stopPlay = false;
            recordBarrier.reset();
            audioTrack.play();
            audioRecord.startRecording();
            //预写一些填缓冲区
            beatPlayHandler.removeMessages(R.integer.PLAY_BEAT);
            beatPlayHandler.sendEmptyMessage(R.integer.PLAY_BEAT);
            isFirstRecordWrite = true;
            isFirstPlayWrite = true;
            playStatus= STATUS_PLAYING;
            new AdjustThread().start();
        }else if(playStatus == STATUS_PLAYING){
            stopPlay = true;
            beatPlayHandler.removeMessages(R.integer.PLAY_BEAT);
            audioTrack.stop();
            audioRecord.stop();
            playStatus = STATUS_PLAY_PREPARE;
        }
    }

    @Override
    protected void handleUiMessage(Message msg) {
        super.handleUiMessage(msg);
        if(msg.what == R.integer.ADJUST_RECORD_DISTANCE_DONE){
            int distanceBytes = (int)msg.obj;
            SpConfig.sp().saveRecordAdjustLen(distanceBytes);
            tvResult.setVisibility(View.VISIBLE);
            setResult(RESULT_OK);
            ToastUtils.showSuccessToast(this, R.string.adjust_success_tip);
        }
    }

    private void waitRecordToSync(){
        if(recordBarrier != null){
            try {
                recordBarrier.await();
            } catch (BrokenBarrierException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlay = true;
        audioTrack.release();
        audioRecord.release();
        audioTrack = null;
        beatPlayHandler.removeCallbacksAndMessages(null);
        beatPlayHandler = null;
    }

    private void loadBeatData() {
        new Thread() {
            @Override
            public void run() {
                try {

                    byte[][] beatsData = AppUtils.loadBeatSoundData();
                    byte[] beatStrongBytes = beatsData[0];

                    beatFirstSoundBytePos = getMaxSamplePos(beatStrongBytes);

                    playBeatBytes = pcmAudioFile.generateBeatBytes(beatStrongBytes, null, "1/4", 72);
                    readRecordBytesLen = playBeatBytes.length * ANALYZE_BEAT_LEN;
                    errorRangeByteLen = (int)(pcmAudioFile.bytesPerSecond() / 1000.0 * 10);
                    playStatus = STATUS_PLAY_PREPARE;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }.start();
    }

    private int getMaxSamplePos(byte[] audioBytes){
        //audioBytes
        ByteBuffer beatBuffer = ByteBuffer.wrap(audioBytes);
        beatBuffer.order(ByteOrder.LITTLE_ENDIAN);
        int sampleValue;
        int maxSampleValue = 0;

        int maxPosition = 0;

        while(beatBuffer.hasRemaining()){
            sampleValue = Math.abs(beatBuffer.getShort());
            if(maxSampleValue < sampleValue){
                maxSampleValue = sampleValue;
            }
        }

        beatBuffer.rewind();
        while(beatBuffer.hasRemaining()){
            if(Math.abs(beatBuffer.getShort()) == maxSampleValue){
                maxPosition = beatBuffer.position() - 2;
                break;
            }
        }

        return maxPosition;
    }

    private class BeatPlayHandler extends Handler {

        BeatPlayHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (stopPlay) {
                return;
            }
            if(isFirstPlayWrite){
                isFirstPlayWrite = false;
                waitRecordToSync();
            }
            audioTrack.write(playBeatBytes, 0, playBeatBytes.length);
            super.sendEmptyMessage(R.integer.PLAY_BEAT);
        }
    }

    private class AdjustThread extends Thread{

        @Override
        public void run() {
            while(!stopPlay){
                byte[] readSamples = new byte[readRecordBytesLen];

                if(isFirstRecordWrite){
                    isFirstRecordWrite = false;
                    waitRecordToSync();
                }

                int readSize = audioRecord.read(readSamples, 0, readRecordBytesLen);
                if(readSize > 0){
                    byte[] segBytes = new byte[playBeatBytes.length];

                    int[] maxPositions = new int[ANALYZE_BEAT_LEN];
                    for(int i = 0; i != ANALYZE_BEAT_LEN; i++){
                        System.arraycopy(readSamples, i * playBeatBytes.length, segBytes, 0, playBeatBytes.length);
                        maxPositions[i] = getMaxSamplePos(segBytes);
                    }

                    Arrays.sort(maxPositions);

                    //取中间一半的值，如果平均值误差在 10 毫秒内，就认为是正确的
                    int sampleTotalValue = 0;
                    int sampleLen = ANALYZE_BEAT_LEN / 2;
                    int[] sampleValues = new int[sampleLen];

                    for(int beginIndex = sampleLen / 2, i=0; i != sampleLen; i++){
                        sampleValues[i] = maxPositions[ i + beginIndex];
                        sampleTotalValue += sampleValues[i];
                    }

                    int averSampleValue = sampleTotalValue / sampleLen;

                    boolean isValid = true;
                    for(int sampleValue : sampleValues){
                        if(Math.abs(averSampleValue - sampleValue) > errorRangeByteLen){
                            isValid = false;
                        }
                    }

                    if(isValid){
                        stopPlay = true;
                        int result = averSampleValue;
                        Message doneMsg = Message.obtain();
                        doneMsg.what = R.integer.ADJUST_RECORD_DISTANCE_DONE;
                        //平均值
                        doneMsg.obj = result;
                        sendUiMessage(doneMsg);
                    }
                }
            }
        }
    }
}
