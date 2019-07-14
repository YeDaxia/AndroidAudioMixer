package io.github.yedaxia.musicnote.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.yedaxia.musicnote.R;
import io.github.yedaxia.musicnote.app.AppContext;
import io.github.yedaxia.musicnote.app.util.AppUtils;
import io.github.yedaxia.musicnote.app.util.BundleKeys;
import io.github.yedaxia.musicnote.app.util.IntentUtils;
import io.github.yedaxia.musicnote.app.util.ResUtils;
import io.github.yedaxia.musicnote.app.util.ToastUtils;
import io.github.yedaxia.musicnote.data.entity.Project;
import io.github.yedaxia.musicnote.data.entity.ProjectDao;
import io.github.yedaxia.musicnote.data.entity.Track;
import io.github.yedaxia.musicnote.data.entity.TrackDao;
import io.github.yedaxia.musicnote.data.sp.SpApp;
import io.github.yedaxia.musicnote.data.sp.SpConfig;
import io.github.yedaxia.musicnote.media.AudioConfig;
import io.github.yedaxia.musicnote.media.AudioEncoder;
import io.github.yedaxia.musicnote.media.AudioUtils;
import io.github.yedaxia.musicnote.media.MultiAudioMixer;
import io.github.yedaxia.musicnote.media.MusicRecorder;
import io.github.yedaxia.musicnote.media.PCMAnalyser;
import io.github.yedaxia.musicnote.ui.dialog.MAlertDialog;
import io.github.yedaxia.musicnote.ui.dialog.MDialogBuilder;
import io.github.yedaxia.musicnote.ui.dialog.MEditorDialog;
import io.github.yedaxia.musicnote.ui.dialog.MListDialog;
import io.github.yedaxia.musicnote.ui.dialog.MLoadingDialog;
import io.github.yedaxia.musicnote.util.FileUtils;
import io.github.yedaxia.musicnote.util.IOUtils;
import io.github.yedaxia.musicnote.util.ListUtils;
import io.github.yedaxia.musicnote.util.StringUtils;
import io.github.yedaxia.musicnote.view.TabButton;
import io.github.yedaxia.musicnote.view.TrackGroupLayout;
import io.github.yedaxia.musicnote.view.TrackView;

/**
 * @author Darcy https://yedaxia.github.io/
 * @version 2018/2/6.
 */

public class WorkspaceActivity extends BaseActivity {

    private static final String TAG = "WorkspaceActivity";

    private static final int STATUS_RECORD_PREPARE = 1;
    private static final int STATUS_RECORD_RECORDING = 2;

    private static final int STATUS_PLAY_PREPARE = 1;
    private static final int STATUS_PLAYING = 2;

    private static final int REQ_CODE_BEAT_SETTING = 0x11;
    private static final int REQ_CODE_PERMISSION_SETTING = 0x12;
    private static final int REQ_CODE_ADJUST_RECORD = 0x13;

    private static final String EX_PROJECT = "project";

    private static final short DEFAULT_SPEED = 120;
    private static final String DEFAULT_BEAT = "4/4";
    private static final String DEFAULT_TUNE = "C";

    @BindView(R.id.layout_beat_setting)
    LinearLayout layoutBeatSetting;
    @BindView(R.id.rb_beat)
    RadioButton rbBeat;
    @BindView(R.id.tv_speed)
    TextView tvSpeed;
    @BindView(R.id.tv_tune_and_beat)
    TextView tvTuneAndBeat;
    @BindView(R.id.btn_record)
    TabButton btnRecord;
    @BindView(R.id.layout_control)
    LinearLayout layoutControl;
    @BindView(R.id.layout_tracks)
    TrackGroupLayout layoutTracks;
    @BindView(R.id.layout_bottom_bar)
    RelativeLayout layoutBottomBar;
    @BindView(R.id.tv_new_track)
    TextView tvNewTrack;
    @BindView(R.id.btn_play)
    TabButton btnPlay;
    @BindView(R.id.btn_skip_begin)
    TabButton btnSkipBegin;
    @BindView(R.id.btn_loop)
    TabButton btnLoop;
    @BindView(R.id.tv_first_adjust_tip)
    TextView tvFirstAdjustTip;

    private ArrayList<TrackHolder> trackHolderList = new ArrayList<>();
    private TrackHolder recordingTrackHolder;
    private int trackHeight;

    private MusicRecorder musicRecorder;
    private PCMAnalyser recordPcmAudioFile;
    private int recordStatus;
    private long totalPlayBytes; //当前已播放字节数
    private MultiAudioMixer audioMixer = MultiAudioMixer.createAudioMixer();
    private AudioTrack musicAudioTrack;

    private byte[] beatStrongBytes;
    private byte[] beetWeakBytes;
    private short currentSpeed;
    private String currentBeat;
    private String currentTune;
    private byte[] playBeatBytes;
    private volatile boolean stopBeatPlay;

    private int playStatus;
    private int bytesPerFrame;
    private boolean stopPlay;
    private boolean isLoopPlay = false;

    private Project project;
    private ProjectDao projectDao;
    private TrackDao trackDao;

    private boolean stopUpdatePlayFrame;
    private boolean isExporting;

    private MLoadingDialog dlgExpertLoading;

    //录音和音乐播放同步
    private CyclicBarrier recordBarrier = new CyclicBarrier(2);

    public static void launch(Context context, Project project) {
        Intent intent = new Intent(context, WorkspaceActivity.class);
        intent.putExtra(EX_PROJECT, project);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workspace);
        ButterKnife.bind(this);
        trackHeight = getResources().getDimensionPixelSize(R.dimen.track_height);
        recordPcmAudioFile = PCMAnalyser.createPCMAnalyser();
        project = (Project) getIntent().getSerializableExtra(EX_PROJECT);
        currentSpeed = project.getSpeed() == null || project.getSpeed() == 0 ? DEFAULT_SPEED : project.getSpeed();
        currentBeat = StringUtils.isEmpty(project.getBeat()) ? DEFAULT_BEAT : project.getBeat();
        currentTune = StringUtils.isEmpty(project.getTune()) ? DEFAULT_TUNE : project.getTune();
        trackDao = AppContext.getDaoSession().getTrackDao();
        projectDao = AppContext.getDaoSession().getProjectDao();
        bytesPerFrame = recordPcmAudioFile.bytesPerFrame();
        enableBack();
        setTitle(project.getName());
        tvSpeed.setText(String.format("= %s", currentSpeed));
        tvTuneAndBeat.setText(String.format("1=%s  %s", currentTune, currentBeat));
        requestPermissions();
        layoutTracks.setOnTrackScrollListener(new TrackGroupLayout.OnTrackScrollListener() {

            @Override
            public void onTrackScrollUp(int nextPlayFramePosition) {
                removeUiMessage(R.integer.REPLAY_ON_SCROLL);
                Message message = Message.obtain();
                message.what = R.integer.REPLAY_ON_SCROLL;
                message.arg1 = bytesPerFrame * nextPlayFramePosition;
                sendUiMessageDelayed(message, 200);
            }

            @Override
            public void onTrackScrollStart() {
                stopUpdatePlayFrame = true;
            }
        });
        dlgExpertLoading = new MLoadingDialog.Builder(this)
                .title(R.string.exporting)
                .canceledOnTouchOutside(false)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .build();

        if (SpConfig.sp().getRecordAdjustLen() == 0) {
            tvFirstAdjustTip.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.tv_new_track)
    void onNewTrackClick(View v) {

        if (playStatus == STATUS_PLAYING
                || recordStatus == STATUS_RECORD_RECORDING) {
            return;
        }

        if(trackHolderList.size() > AudioConfig.MAX_SUPPORT_CHANNEL_COUNT){
            ToastUtils.showErrorToast(this, R.string.exceed_max_support_track_count);
            return;
        }

        new MEditorDialog.Builder(this)
                .title(R.string.track_name_setting)
                .positiveText(R.string.confirm)
                .positiveClickListener(new MDialogBuilder.OnButtonClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        String trackName = ((MEditorDialog) dialog).getText().toString().trim();
                        if (StringUtils.isEmpty(trackName)) {
                            ToastUtils.showErrorToast(WorkspaceActivity.this, R.string.error_empty_track_name);
                            return;
                        }
                        addNewTrack(trackName);
                    }
                })
                .build()
                .show();
    }

    @OnClick(R.id.btn_record)
    void onRecordClick(View v) {

        if (ContextCompat.checkSelfPermission(this, Permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            showRequestPermissionDialog();
            return;
        }

        if (recordingTrackHolder == null) {
            ToastUtils.showErrorToast(this, R.string.toast_no_record_track);
            return;
        }

        if (STATUS_RECORD_PREPARE == recordStatus) {
            recordStatus = STATUS_RECORD_RECORDING;
            recordBarrier.reset();
            startPlay();
            musicRecorder.start();
            btnRecord.setIcon(ResUtils.getDrawable(R.drawable.ic_record_pause));
            disableViews(tvNewTrack, btnPlay, btnSkipBegin, btnLoop);
        } else if (STATUS_RECORD_RECORDING == recordStatus) {
            stopPlay = true;
            recordStatus = STATUS_RECORD_PREPARE;
            musicAudioTrack.stop();
            musicRecorder.stop();
            btnRecord.setIcon(ResUtils.getDrawable(R.drawable.ic_record_on));
            closeTrackInputs();
            stopPlay();
            enableViews(tvNewTrack, btnPlay, btnSkipBegin, btnLoop);
        } else {
            ToastUtils.showErrorToast(this, R.string.toast_waiting_data_loading);
        }
    }

    @OnClick(R.id.btn_play)
    void onPlayClick(View v) {
        if (ListUtils.isNotEmpty(trackHolderList)) {
            if (playStatus == STATUS_PLAY_PREPARE) {
                recordBarrier.reset();
                newEmptyThreadToSync();
                startPlay();
                disableViews(btnRecord, tvNewTrack);
            } else if (playStatus == STATUS_PLAYING) {
                stopPlay();
                enableViews(btnRecord, tvNewTrack);
            }
        }
    }

    @OnClick(R.id.btn_skip_begin)
    void onResetClick(View v) {
        if (playStatus == STATUS_PLAY_PREPARE) {
            resetPlay();
        } else if (playStatus == STATUS_PLAYING) {
            stopPlay();
            resetPlay();
            startPlay();
            recordBarrier.reset();
            newEmptyThreadToSync();
            disableViews(btnRecord, tvNewTrack);
        }
    }

    @OnClick(R.id.btn_loop)
    void onRepeatClick(View v) {
        isLoopPlay = !isLoopPlay;
        if (isLoopPlay) {
            ToastUtils.showInfoToast(this, R.string.loop_play_tip);
            btnLoop.setIcon(ResUtils.getDrawable(R.drawable.ic_repeat));
        } else {
            btnLoop.setIcon(ResUtils.getDrawable(R.drawable.ic_repeat_one));
        }
    }

    @OnClick({R.id.rb_beat_setting, R.id.tv_tune_and_beat, R.id.tv_speed})
    void onBeatSettingClick(View v) {
        Intent intent = new Intent(this, BeatSettingActivity.class);
        intent.putExtra(BundleKeys.RESULT_BEAT, currentBeat);
        intent.putExtra(BundleKeys.RESULT_SPEED, currentSpeed);
        intent.putExtra(BundleKeys.RESULT_TUNE, currentTune);
        startActivityForResult(intent, REQ_CODE_BEAT_SETTING);
    }

    @OnClick(R.id.rb_beat)
    void onBeatSoundClick(View v) {
        stopBeatPlay = !stopBeatPlay;
        if (stopBeatPlay) {
            rbBeat.setButtonDrawable(R.drawable.ic_volume_off);
            ToastUtils.showInfoToast(this, R.string.close_beat_sound);
        } else {
            rbBeat.setButtonDrawable(R.drawable.ic_volume_up);
            ToastUtils.showInfoToast(this, R.string.open_beat_sound);
        }
    }

    @OnClick(R.id.tv_adjust)
    void onAdjustClick(View v) {
        Intent intent = new Intent();
        intent.setClass(this, AdjustRecordActivity.class);
        startActivityForResult(intent, REQ_CODE_ADJUST_RECORD);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (musicRecorder != null) {
            musicRecorder.release();
        }
        if (musicAudioTrack != null) {
            stopPlay();
            musicAudioTrack.release();
            musicAudioTrack = null;
        }
    }

    private void initTrackInputs() {
        try {
            for (TrackHolder trackHolder : trackHolderList) {
                if (recordStatus == STATUS_RECORD_RECORDING && trackHolder == recordingTrackHolder) {
                    continue;
                }
                FileInputStream audioStream = new FileInputStream(trackHolder.audioFile);
                audioStream.skip(totalPlayBytes > trackHolder.audioFile.length() ? trackHolder.audioFile.length() : totalPlayBytes);
                trackHolder.audioStream = audioStream;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void requestPermissions() {
        AndPermission.with(this)
                .permission(Permission.WRITE_EXTERNAL_STORAGE, Permission.RECORD_AUDIO)
                .rationale(new Rationale() {
                    @Override
                    public void showRationale(Context context, List<String> permissions, final RequestExecutor executor) {
                        new MAlertDialog.Builder(context)
                                .title(R.string.tips)
                                .message(R.string.request_permissions)
                                .positiveText(R.string.i_know)
                                .canceledOnTouchOutside(false)
                                .positiveClickListener(new MDialogBuilder.OnButtonClickListener() {
                                    @Override
                                    public void onClick(Dialog dialog) {
                                        dialog.dismiss();
                                        executor.execute();
                                    }
                                })
                                .build()
                                .show();
                    }
                })
                .onGranted(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        loadProjectData();
                    }
                }).onDenied(new Action() {

            @Override
            public void onAction(List<String> permissions) {
                showRequestPermissionDialog();
            }
        }).start();
    }

    private void showRequestPermissionDialog() {
        new MAlertDialog.Builder(WorkspaceActivity.this)
                .title(R.string.tips)
                .message(R.string.request_permissions)
                .canceledOnTouchOutside(false)
                .positiveText(R.string.i_know)
                .positiveClickListener(new MDialogBuilder.OnButtonClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        Intent intent = IntentUtils.getPermissionSettingIntent();
                        startActivityForResult(intent, REQ_CODE_PERMISSION_SETTING);
                    }
                })
                .build()
                .show();
    }

    private void loadProjectData() {
        if (ContextCompat.checkSelfPermission(this, Permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            new Thread() {
                @Override
                public void run() {
                    List<Track> trackList = trackDao
                            .queryBuilder()
                            .where(TrackDao.Properties.ProjectId.eq(project.getId()))
                            .orderDesc(TrackDao.Properties.CreateTime)
                            .list();


                    if (ListUtils.isNotEmpty(trackList)) {

                        for (Track track : trackList) {
                            TrackHolder trackHolder = new TrackHolder();

                            try {
                                File audioFile = new File(AppContext.getAudioTempPath(), track.getFileName());
                                trackHolder.audioFile = audioFile;
                                recordPcmAudioFile.readRawFile(audioFile);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }

                            trackHolder.track = track;
                            for (double frameGain : recordPcmAudioFile.getFrameGains()) {
                                trackHolder.audioFrames.add(frameGain);
                            }

                            trackHolderList.add(trackHolder);
                        }
                    }

                    try {
                        byte[][] beatsData = AppUtils.loadBeatSoundData();
                        beatStrongBytes = beatsData[0];
                        beetWeakBytes = beatsData[1];
                        refreshBeatData();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    playStatus = STATUS_PLAY_PREPARE;
                    recordStatus = STATUS_RECORD_PREPARE;
                    sendEmptyUiMessage(R.integer.LOAD_DATA_SUCCESS);
                }
            }.start();
        }
    }


    @Override
    protected void handleUiMessage(Message msg) {
        super.handleUiMessage(msg);
        switch (msg.what) {
            case R.integer.RECEIVE_RECORDING_DATA:
                onReceiveRecordingData();
                break;
            case R.integer.LOAD_DATA_SUCCESS:
                onLoadTracksData();
                break;
            case R.integer.PLAY_AUDIO_FRAME:
                onPlayFrame();
                break;
            case R.integer.PLAY_DONE:
                onPlayDone();
                break;
            case R.integer.REPLAY_ON_SCROLL:
                onTrackScrollUp(msg.arg1);
                break;
            case R.integer.EXPORT_AUDIO_SUCCESS:
                onExpertAudioSuccess(msg.obj.toString());
                break;
            case R.integer.EXPORT_AUDIO_FAIL:
                onExpertAudioFail();
                break;
        }
    }

    private void onExpertAudioFail() {
        dlgExpertLoading.dismiss();
        ToastUtils.showErrorToast(this, R.string.export_fail);
    }

    private void onExpertAudioSuccess(String outputFilePath) {
        dlgExpertLoading.dismiss();
        new MAlertDialog.Builder(this)
                .title(R.string.export_success)
                .message(ResUtils.getString(R.string.expert_path_tip, outputFilePath))
                .positiveText(R.string.i_know)
                .build()
                .show();
    }

    private void onTrackScrollUp(int totalCostBytes) {
        if (playStatus == STATUS_PLAY_PREPARE) {
            totalPlayBytes = totalCostBytes;
        } else if (playStatus == STATUS_PLAYING) {
            totalPlayBytes = totalCostBytes;
            stopPlay();
            startPlay();
            newEmptyThreadToSync();
        }
        stopUpdatePlayFrame = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.workspace_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                onExpertAudio();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onExpertAudio() {
        if (!isExporting && ListUtils.isNotEmpty(trackHolderList)) {
            dlgExpertLoading.show();
            new ExpertThread().start();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_BEAT_SETTING && resultCode == RESULT_OK) {
            if (data.hasExtra(BundleKeys.RESULT_SPEED)) {
                currentSpeed = data.getShortExtra(BundleKeys.RESULT_SPEED, currentSpeed);
            }
            if (data.hasExtra(BundleKeys.RESULT_BEAT)) {
                currentBeat = data.getStringExtra(BundleKeys.RESULT_BEAT);
            }
            if (data.hasExtra(BundleKeys.RESULT_TUNE)) {
                currentTune = data.getStringExtra(BundleKeys.RESULT_TUNE);
            }
            tvSpeed.setText(String.format("= %s", currentSpeed));
            tvTuneAndBeat.setText(String.format("1=%s  %s", currentTune, currentBeat));
            refreshBeatData();
        } else if (requestCode == REQ_CODE_PERMISSION_SETTING) {
            loadProjectData();
        }
    }

    private void addNewTrack(String trackName) {
        String fileName = UUID.randomUUID().toString();

        Track track = new Track();
        track.setCreateTime(new Date());
        track.setName(trackName);
        track.setFileName(fileName);
        track.setProjectId(project.getId());
        long trackId = trackDao.insert(track);
        track.setId(trackId);

        TrackHolder newTrackHolder = new TrackHolder(new File(AppContext.getAudioTempPath(), fileName));
        newTrackHolder.track = track;
        addTrackViews(newTrackHolder, true);

        if (recordingTrackHolder != null) {
            recordingTrackHolder.trackView.setWaveColor(R.color.wave_line_color_primary);
        }
        newTrackHolder.trackView.setWaveColor(R.color.wave_line_recording_color);

        recordingTrackHolder = newTrackHolder;
        trackHolderList.add(0, newTrackHolder);

        if (musicRecorder != null) {
            musicRecorder.release();
        }

        totalPlayBytes = 0;
        musicRecorder = new MusicRecorder(newTrackHolder.audioFile, bytesPerFrame);
        musicRecorder.setOnRecordListener(new MusicRecorder.OnRecordListener() {

            @Override
            public void onRecordStart() {
                Log.i(TAG, "RecordThread Barrier...");
                waitRecordToSync();
            }

            @Override
            public void onRecording(ByteBuffer data) {
                // 数据帧
                data.rewind();
                recordPcmAudioFile.readByteBuffer(data);
                for (double frameGain : recordPcmAudioFile.getFrameGains()) {
                    recordingTrackHolder.audioFrames.add(frameGain);
                }

                sendEmptyUiMessage(R.integer.RECEIVE_RECORDING_DATA);
            }
        });
    }

    private void deleteTrack(Track track) {
        //删除记录
        trackDao.delete(track);
        int position = getPositionInTracks(track);

        //删除文件
        FileUtils.deleteFile(trackHolderList.get(position).audioFile);

        trackHolderList.remove(position);

        if (recordingTrackHolder != null && recordingTrackHolder.track == track) {
            recordingTrackHolder = null;
        }

        layoutControl.removeViewAt(position);
        layoutTracks.removeViewAt(position);
    }

    private void updateTrack(Track track) {
        trackDao.update(track);
        int position = getPositionInTracks(track);
        trackHolderList.get(position).tvTrackName.setText(track.getName());
    }

    private int getPositionInTracks(Track track) {
        int position = 0;

        for (TrackHolder trackHolder : trackHolderList) {
            if (trackHolder.track == track) {
                break;
            }
            position++;
        }

        return position;
    }

    private void onPlayDone() {
        if (recordStatus != STATUS_RECORD_RECORDING) {
            enableViews(tvNewTrack, btnRecord);
            resetPlay();
            playStatus = STATUS_PLAY_PREPARE;
            if (isLoopPlay) {
                btnPlay.performClick();
            }
        }
    }

    private void onPlayFrame() {
        int currentPlayFrame = (int) (totalPlayBytes / bytesPerFrame);
        setCurrentPlayFrame(currentPlayFrame);
    }

    private void onReceiveRecordingData() {
        final int currentFrame = recordingTrackHolder.audioFrames.size() - 1;
        setCurrentPlayFrame(currentFrame);
    }

    private void setCurrentPlayFrame(int currentFrame) {
        if (!stopUpdatePlayFrame) {
            for (TrackHolder trackHolder : trackHolderList) {
                trackHolder.trackView.setPlayingFrame(currentFrame);
            }
        }
    }

    private void closeTrackInputs() {
        for (TrackHolder trackHolder : trackHolderList) {
            IOUtils.closeSilently(trackHolder.audioStream);
        }
    }


    private void onLoadTracksData() {
        if (ListUtils.isNotEmpty(trackHolderList)) {
            for (TrackHolder trackHolder : trackHolderList) {
                addTrackViews(trackHolder, false);
            }
        }
    }

    private void addTrackViews(TrackHolder trackHolder, boolean insertToFirst) {
        View trackSoundItem = View.inflate(this, R.layout.item_track_sound, null);
        trackSoundItem.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, trackHeight));

        TabButton btnSound = (TabButton) trackSoundItem.findViewById(R.id.btn_sound);
        TextView tvTrackName = (TextView) trackSoundItem.findViewById(R.id.tv_track_name);
        tvTrackName.setText(trackHolder.track.getName());

        TrackView trackView = new TrackView(this);
        trackView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, trackHeight));

        if (insertToFirst) {
            layoutControl.addView(trackSoundItem, 0);
            layoutTracks.addView(trackView, 0);
        } else {
            layoutControl.addView(trackSoundItem);
            layoutTracks.addView(trackView);
        }
        trackHolder.setViews(btnSound, trackView, tvTrackName);
    }

    private void startPlay() {
        if (ListUtils.isNotEmpty(trackHolderList)) {
            int newChannelCount = recordStatus == STATUS_RECORD_RECORDING ?  trackHolderList.size() : trackHolderList.size() + 1;
            if (musicAudioTrack == null) {
                musicAudioTrack = AudioUtils.createTrack(newChannelCount);
            } else if (musicAudioTrack.getChannelCount() != newChannelCount) {
                musicAudioTrack.release();
                musicAudioTrack = AudioUtils.createTrack(newChannelCount);
            }
            playStatus = STATUS_PLAYING;
            btnPlay.setIcon(ResUtils.getDrawable(R.drawable.ic_pause));
            stopPlay = false;
            initTrackInputs();
            musicAudioTrack.play();
            new PlayThread().start();
        }
    }

    private void stopPlay() {
        stopPlay = true;
        playStatus = STATUS_PLAY_PREPARE;
        btnPlay.setIcon(ResUtils.getDrawable(R.drawable.ic_play));
        musicAudioTrack.stop();
        closeTrackInputs();
    }

    private void resetPlay() {
        totalPlayBytes = 0;
        setCurrentPlayFrame(0);
        btnPlay.setIcon(ResUtils.getDrawable(R.drawable.ic_play));
    }

    private void refreshBeatData() {
        project.setBeat(currentBeat);
        project.setSpeed(currentSpeed);
        project.setTune(currentTune);
        projectDao.update(project);

        playBeatBytes = recordPcmAudioFile.generateBeatBytes(beatStrongBytes, beetWeakBytes, currentBeat, currentSpeed);
    }

    private void waitRecordToSync() {
        if (recordBarrier != null) {
            try {
                recordBarrier.await();
            } catch (BrokenBarrierException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void newEmptyThreadToSync() {
        new Thread() {
            @Override
            public void run() {
                waitRecordToSync();
            }
        }.start();
    }

    private void enableViews(View... views) {
        for (View view : views) {
            view.setEnabled(true);
            view.setAlpha(1);
        }
    }

    private void disableViews(View... views) {
        for (View view : views) {
            view.setEnabled(false);
            view.setAlpha(0.5f);
        }
    }

    class TrackHolder implements View.OnClickListener, View.OnLongClickListener {

        private static final int MENU_RENAME = 0;
        private static final int MENU_DEL_POS = 1;

        Track track;
        TabButton btnSound;
        TrackView trackView;
        TextView tvTrackName;
        File audioFile;
        List<Double> audioFrames = new ArrayList<>();
        volatile boolean isSoundOn = true;
        InputStream audioStream;

        TrackHolder() {

        }

        TrackHolder(File audioFile) {
            this.audioFile = audioFile;
        }

        void setViews(TabButton btnSound, TrackView trackView, TextView tvTrackName) {
            this.btnSound = btnSound;
            this.trackView = trackView;
            this.tvTrackName = tvTrackName;
            btnSound.setOnClickListener(this);
            this.btnSound.setOnLongClickListener(this);
            this.trackView.setOnLongClickListener(this);
            this.tvTrackName.setOnLongClickListener(this);
            this.trackView.setWaveData(audioFrames);
        }

        @Override
        public void onClick(View v) {
            if (v == trackView) {
                onTrackClick(v);
            } else if (v == btnSound) {
                onSoundClick(v);
            }
        }

        @Override
        public boolean onLongClick(View v) {

            if(playStatus == STATUS_PLAYING
                    || recordStatus == STATUS_RECORD_RECORDING){
                return true;
            }

            new MListDialog.Builder(v.getContext())
                    .items(R.array.track_menu_array)
                    .onItemClick(new MListDialog.OnItemClickListener() {
                        @Override
                        public void onItemClick(MListDialog dialog, int position, CharSequence text) {
                            if (position == MENU_RENAME) {
                                onTrackRenameClick(dialog.getContext());
                            } else {
                                onTrackDeleteClick();
                            }
                        }
                    })
                    .build()
                    .show();
            return true;
        }

        private synchronized void onSoundClick(View v) {
            this.isSoundOn = !isSoundOn;
            if (isSoundOn) {
                btnSound.setIcon(ResUtils.getDrawable(R.drawable.ic_volume_up));
            } else {
                btnSound.setIcon(ResUtils.getDrawable(R.drawable.ic_volume_off));
            }
        }

        private void onTrackClick(View v) {
            recordingTrackHolder = TrackHolder.this;
        }

        private void onTrackDeleteClick() {
            new MAlertDialog.Builder(WorkspaceActivity.this)
                    .title(R.string.warning)
                    .message(R.string.delete_track_tip)
                    .positiveText(R.string.delete)
                    .negativeText(R.string.cancel)
                    .positiveClickListener(new MDialogBuilder.OnButtonClickListener() {
                        @Override
                        public void onClick(Dialog dialog) {
                            deleteTrack(track);
                            dialog.dismiss();
                        }
                    })
                    .build()
                    .show();
        }

        private void onTrackRenameClick(Context cxt) {
            new MEditorDialog.Builder(cxt)
                    .title(R.string.track_name_setting)
                    .positiveText(R.string.confirm)
                    .inputText(track.getName())
                    .positiveClickListener(new MDialogBuilder.OnButtonClickListener() {
                        @Override
                        public void onClick(Dialog dialog) {
                            String trackName = ((MEditorDialog) dialog).getText().toString().trim();
                            if (StringUtils.isEmpty(trackName)) {
                                ToastUtils.showErrorToast(WorkspaceActivity.this, R.string.error_empty_track_name);
                                return;
                            }
                            tvNewTrack.setText(trackName);
                            track.setName(trackName);
                            updateTrack(track);
                        }
                    })
                    .build()
                    .show();
        }
    }

    private class PlayThread extends Thread {

        private boolean firstWrite = true;

        @Override
        public void run() {

            final int frameBytes = bytesPerFrame;
            int inputSize = trackHolderList.size();

            //非录音播放状态
            if (recordStatus != STATUS_RECORD_RECORDING) {
                inputSize = inputSize + 1;
            }

            boolean[] streamDoneArray = new boolean[inputSize - 1];
            while (true) {

                if (stopPlay || (streamDoneArray.length != 0
                        && isArrayAllTrue(streamDoneArray))) {
                    break;
                }

                byte[][] allAudioBytes = new byte[inputSize][];

                InputStream audioStream;
                byte[] readBuffer = new byte[frameBytes];
                TrackHolder trackHolder;
                int streamIndex = 0;
                try {
                    for (int i = 0; i < trackHolderList.size(); ++i) {
                        trackHolder = trackHolderList.get(i);
                        if (recordStatus == STATUS_RECORD_RECORDING
                                && trackHolder == recordingTrackHolder) {
                            continue;
                        }
                        audioStream = trackHolder.audioStream;
                        if (audioStream != null && !streamDoneArray[streamIndex]
                                && (audioStream.read(readBuffer)) != -1) {
                            if (trackHolder.isSoundOn) {
                                allAudioBytes[streamIndex] = Arrays.copyOf(readBuffer, readBuffer.length);
                            } else {
                                allAudioBytes[streamIndex] = new byte[frameBytes];
                            }
                        } else {
                            streamDoneArray[streamIndex] = true;
                            allAudioBytes[streamIndex] = new byte[frameBytes];
                        }

                        streamIndex++;
                    }

                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                if (!stopBeatPlay) {
                    final int beginBeatByteLen = (int) (totalPlayBytes % playBeatBytes.length);
                    //剩下的长度
                    final int leftBeatBytesLen = playBeatBytes.length - beginBeatByteLen;
                    System.arraycopy(playBeatBytes, beginBeatByteLen, readBuffer, 0, leftBeatBytesLen > readBuffer.length ? readBuffer.length : leftBeatBytesLen);
                    //剩下的长度不够buffer的长度时
                    if (readBuffer.length > leftBeatBytesLen) {
                        System.arraycopy(playBeatBytes, 0, readBuffer, leftBeatBytesLen, readBuffer.length - leftBeatBytesLen);
                    }
                    allAudioBytes[inputSize - 1] = readBuffer;
                } else {
                    Arrays.fill(readBuffer, (byte) 0);
                }

                allAudioBytes[inputSize - 1] = readBuffer;

                byte[] resultBytes = audioMixer.mixRawAudioBytes(allAudioBytes);
                if (firstWrite) {
                    firstWrite = false;
                    Log.i(TAG, "PlayThread Barrier...");
                    waitRecordToSync();
                }

                if (resultBytes == null) {
                    resultBytes = readBuffer;
                }

                musicAudioTrack.write(resultBytes, 0, resultBytes.length);

                totalPlayBytes += frameBytes;

                if (recordStatus != STATUS_RECORD_RECORDING) {
                    sendEmptyUiMessage(R.integer.PLAY_AUDIO_FRAME);
                }
            }

            if (isArrayAllTrue(streamDoneArray)) {
                sendEmptyUiMessage(R.integer.PLAY_DONE);
            }
        }

        private boolean isArrayAllTrue(boolean[] resultArray) {
            boolean done = true;
            for (boolean streamEnd : resultArray) {
                if (!streamEnd) {
                    done = false;
                }
            }
            return done;
        }
    }

    private class ExpertThread extends Thread {

        @Override
        public void run() {
            isExporting = true;

            if (ListUtils.isEmpty(trackHolderList)) {
                return;
            }

            File[] audioFiles = new File[trackHolderList.size()];

            for (int i = 0, size = audioFiles.length; i != size; i++) {
                audioFiles[i] = trackHolderList.get(i).audioFile;
            }

            try {
                File tempMixAudioFile = new File(AppContext.getAudioTempPath(), UUID.randomUUID().toString());
                final FileOutputStream mixTempOutStream = new FileOutputStream(tempMixAudioFile);
                audioMixer.setOnAudioMixListener(new MultiAudioMixer.OnAudioMixListener() {

                    @Override
                    public void onMixing(byte[] mixBytes) throws IOException {
                        mixTempOutStream.write(mixBytes);
                    }

                    @Override
                    public void onMixError(int errorCode) {

                    }

                    @Override
                    public void onMixComplete() {

                    }
                });
                audioMixer.mixAudios(audioFiles, recordPcmAudioFile.bytesPerSample());
                mixTempOutStream.close();

                File outputFile = new File(AppContext.getAudioOutPath(), project.getName() + ".mp3");
                int channelCount = trackHolderList.size();
                AudioEncoder accEncoder = AudioEncoder.createAccEncoder(tempMixAudioFile, channelCount);
                accEncoder.encodeToFile(outputFile);

                Message successMsg = Message.obtain();
                successMsg.what = R.integer.EXPORT_AUDIO_SUCCESS;
                successMsg.obj = outputFile.getAbsolutePath();
                sendUiMessage(successMsg);
            } catch (IOException ex) {
                ex.printStackTrace();
                sendEmptyUiMessage(R.integer.EXPORT_AUDIO_FAIL);
            }

            isExporting = false;
        }
    }

}
