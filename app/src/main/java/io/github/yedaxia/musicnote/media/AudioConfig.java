package io.github.yedaxia.musicnote.media;

import android.media.AudioFormat;

/**
 * @author Darcy https://yedaxia.github.io/
 * @version 2018/2/5.
 */

public interface AudioConfig {
    int AUDIO_SAMPLE_RATE = 44100;
    int AUDIO_IN_CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    int MAX_SUPPORT_CHANNEL_COUNT = 8;
    int BEAT_CHANNEL_COUNT = 1;

    //SamplesPerFrame
    int SAMPLES_PER_FRAME = 1024;
}
