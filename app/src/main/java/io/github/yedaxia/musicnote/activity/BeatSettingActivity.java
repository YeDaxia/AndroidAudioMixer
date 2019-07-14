package io.github.yedaxia.musicnote.activity;

import android.content.Intent;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.yedaxia.musicnote.R;
import io.github.yedaxia.musicnote.app.util.AppUtils;
import io.github.yedaxia.musicnote.app.util.BundleKeys;
import io.github.yedaxia.musicnote.media.AudioConfig;
import io.github.yedaxia.musicnote.media.AudioUtils;
import io.github.yedaxia.musicnote.media.PCMAnalyser;
import io.github.yedaxia.musicnote.ui.dialog.MListDialog;
import io.github.yedaxia.musicnote.util.StringUtils;
import io.github.yedaxia.musicnote.view.TabButton;

/**
 * @author Darcy https://yedaxia.github.io/
 * @version 2018/2/27.
 */

public class BeatSettingActivity extends BaseActivity{

    private static final String TAG = "BeatSettingActivity";

    private static final int STATUS_PLAY_PREPARE = 1;
    private static final int STATUS_PLAYING =2;

    private static final short MIN_SPEED = 30;
    private static final short MAX_SPEED = 240;

    @BindView(R.id.tv_beat)
    TextView tvBeat;
    @BindView(R.id.btn_speed_down)
    TabButton btnSpeedDown;
    @BindView(R.id.tv_speed)
    TextView tvSpeed;
    @BindView(R.id.btn_speed_up)
    TabButton btnSpeedUp;
    @BindView(R.id.sb_speed)
    SeekBar sbSpeed;
    @BindView(R.id.btn_play_beat)
    TabButton btnPlayBeat;
    @BindView(R.id.tv_tune)
    TextView tvTune;
    @BindView(R.id.btn_save)
    Button btnSave;

    private AudioTrack audioTrack;
    private byte[] beatStrongBytes;
    private byte[] beetWeakBytes;
    private byte[] playBeatBytes;

    private int playStatus;
    private boolean stopPlay;
    private short currentSpeed;
    private String currentBeat;
    private String currentTone;
    private List<String> toneList = new ArrayList<>();

    private Intent beatResult = new Intent();
    private PCMAnalyser pcmAudioFile;
    private BeatPlayHandler beatPlayHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beat_setting);
        ButterKnife.bind(this);
        currentSpeed = getIntent().getShortExtra(BundleKeys.RESULT_SPEED, (short)120);
        currentBeat = getIntent().getStringExtra(BundleKeys.RESULT_BEAT);
        currentTone = getIntent().getStringExtra(BundleKeys.RESULT_TUNE);
        currentBeat = StringUtils.isEmpty(currentBeat)? "4/4" : currentBeat;
        currentTone = StringUtils.isEmpty(currentTone)? "C" : currentTone;
        tvTune.setText(String.format("1=%s", currentTone));
        tvSpeed.setText(String.valueOf(currentSpeed));
        tvBeat.setText(currentBeat);
        setTitle(R.string.beat_setting);
        enableBack();
        initToneList();
        audioTrack = AudioUtils.createTrack(AudioConfig.BEAT_CHANNEL_COUNT);
        pcmAudioFile = PCMAnalyser.createPCMAnalyser(AudioConfig.BEAT_CHANNEL_COUNT);
        sbSpeed.setProgress((int)((double)(currentSpeed - MIN_SPEED) / (MAX_SPEED - MIN_SPEED) * 100));
        sbSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentSpeed = (short) (MIN_SPEED + (MAX_SPEED - MIN_SPEED) * (progress / 100.0));
                refreshSpeed();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        HandlerThread playBeatHandlerThread = new HandlerThread("PlayBeatHandlerThread");
        playBeatHandlerThread.start();
        beatPlayHandler = new BeatPlayHandler(playBeatHandlerThread.getLooper());
        loadBeatData();
    }

    @OnClick(R.id.btn_play_beat)
    void onPlayClick(View v){
        if(playStatus == STATUS_PLAY_PREPARE){
            stopPlay = false;
            audioTrack.play();
            //预写一些填缓冲区
            beatPlayHandler.removeMessages(R.integer.PLAY_BEAT);
            beatPlayHandler.sendEmptyMessage(R.integer.PLAY_BEAT);
            playStatus= STATUS_PLAYING;
        }else if(playStatus == STATUS_PLAYING){
            stopPlay = true;
            beatPlayHandler.removeMessages(R.integer.PLAY_BEAT);
            audioTrack.stop();
            playStatus = STATUS_PLAY_PREPARE;
        }
    }

    @OnClick(R.id.tv_beat)
    void onBeatClick(View v){
        new MListDialog.Builder(this)
                .title(R.string.select_beat)
                .items(R.array.beats_array)
                .onItemClick(new MListDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(MListDialog dialog, int position, CharSequence text) {
                        dialog.dismiss();
                        refreshBeat(text.toString());
                    }
                })
                .build()
                .show();
    }

    @OnClick(R.id.tv_tune)
    void onTuneClick(View v){
        new MListDialog.Builder(this)
                .title(R.string.select_tone)
                .items(toneList)
                .onItemClick(new MListDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(MListDialog dialog, int position, CharSequence text) {
                        dialog.dismiss();
                        refreshTune(text.toString());
                    }
                })
                .build()
                .show();
    }

    @OnClick(R.id.btn_speed_up)
    void onSpeedUpClick(View v){
        if(currentSpeed != MAX_SPEED){
            currentSpeed++;
            refreshSpeed();
        }
    }

    @OnClick(R.id.btn_speed_down)
    void onSpeedDownClick(View v){
        if(currentSpeed != MIN_SPEED){
            currentSpeed--;
            refreshSpeed();
        }
    }

    @OnClick(R.id.btn_save)
    void onSaveClick(View v){
        beatResult.putExtra(BundleKeys.RESULT_SPEED, currentSpeed);
        beatResult.putExtra(BundleKeys.RESULT_BEAT, currentBeat);
        beatResult.putExtra(BundleKeys.RESULT_TUNE, currentTone);
        setResult(RESULT_OK, beatResult);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlay = true;
        audioTrack.release();
        audioTrack = null;
        beatPlayHandler.removeCallbacksAndMessages(null);
        beatPlayHandler = null;
    }

    private synchronized void generatePlayBeatBytes(){
        this.playBeatBytes = pcmAudioFile.generateBeatBytes(beatStrongBytes, beetWeakBytes, currentBeat, currentSpeed);
        audioTrack.flush();
    }

    private void initToneList(){
        for(char c = 'A'; c != 'H'; c++){
            toneList.add(String.format("%sb",c));
            toneList.add(String.valueOf(c));
            toneList.add(String.format("%s#",c));
        }
    }

    private void refreshSpeed(){
        tvSpeed.setText(String.valueOf(currentSpeed));
        beatPlayHandler.removeMessages(R.integer.REFRESH_BEAT_DATA);
        beatPlayHandler.sendEmptyMessage(R.integer.REFRESH_BEAT_DATA);
    }

    private void refreshBeat(String beatText){
        currentBeat = beatText;
        tvBeat.setText(beatText);
        beatPlayHandler.removeMessages(R.integer.REFRESH_BEAT_DATA);
        beatPlayHandler.sendEmptyMessage(R.integer.REFRESH_BEAT_DATA);
    }

    private void refreshTune(String tune){
        currentTone = tune;
        tvTune.setText(String.format("1=%s",tune));
    }

    private void loadBeatData(){
        new Thread(){
            @Override
            public void run() {
                try{
                    byte[][] beatsData = AppUtils.loadBeatSoundData();
                    beatStrongBytes = beatsData[0];
                    beetWeakBytes = beatsData[1];
                    generatePlayBeatBytes();
                }catch (IOException ex){
                    ex.printStackTrace();
                }
                playStatus = STATUS_PLAY_PREPARE;
            }
        }.start();
    }

    private class BeatPlayHandler extends Handler {

        public BeatPlayHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if(msg.what == R.integer.REFRESH_BEAT_DATA){
                generatePlayBeatBytes();
            }else{
                if(stopPlay){
                    return;
                }
                audioTrack.write(playBeatBytes, 0, playBeatBytes.length);
                super.sendEmptyMessage(R.integer.PLAY_BEAT);
            }

        }
    }
}
