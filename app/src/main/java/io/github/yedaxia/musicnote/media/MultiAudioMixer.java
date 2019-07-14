package io.github.yedaxia.musicnote.media;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * 目前只能对相同采样率，通道和采样精度的音频进行混音
 *
 * @author Darcy
 */
public abstract class MultiAudioMixer {

    private OnAudioMixListener mOnAudioMixListener;

    public static MultiAudioMixer createAudioMixer() {
        return new ChannelAlignAudioMixer();
    }

    public void setOnAudioMixListener(OnAudioMixListener l) {
        this.mOnAudioMixListener = l;
    }

    /**
     * <p>start to mix , you can call {@link #setOnAudioMixListener(OnAudioMixListener)} before this method to get mixed data.
     */
    public void mixAudios(File[] rawAudioFiles, int bufferSize) {

        final int fileSize = rawAudioFiles.length;

        FileInputStream[] audioFileStreams = new FileInputStream[fileSize];
        File audioFile = null;

        FileInputStream inputStream;
        byte[][] allAudioBytes = new byte[fileSize][];
        boolean[] streamDoneArray = new boolean[fileSize];
        byte[] buffer = new byte[bufferSize];
        int offset;

        try {

            for (int fileIndex = 0; fileIndex < fileSize; ++fileIndex) {
                audioFile = rawAudioFiles[fileIndex];
                audioFileStreams[fileIndex] = new FileInputStream(audioFile);
            }

            while (true) {

                for (int streamIndex = 0; streamIndex < fileSize; ++streamIndex) {

                    inputStream = audioFileStreams[streamIndex];
                    if (!streamDoneArray[streamIndex] && (offset = inputStream.read(buffer)) != -1) {
                        allAudioBytes[streamIndex] = Arrays.copyOf(buffer, buffer.length);
                    } else {
                        streamDoneArray[streamIndex] = true;
                        allAudioBytes[streamIndex] = new byte[bufferSize];
                    }
                }

                byte[] mixBytes = mixRawAudioBytes(allAudioBytes);
                if (mixBytes != null && mOnAudioMixListener != null) {
                    mOnAudioMixListener.onMixing(mixBytes);
                }

                boolean done = true;
                for (boolean streamEnd : streamDoneArray) {
                    if (!streamEnd) {
                        done = false;
                    }
                }

                if (done) {
                    if (mOnAudioMixListener != null)
                        mOnAudioMixListener.onMixComplete();
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            if (mOnAudioMixListener != null)
                mOnAudioMixListener.onMixError(1);
        } finally {
            try {
                for (FileInputStream in : audioFileStreams) {
                    if (in != null)
                        in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public abstract byte[] mixRawAudioBytes(byte[][] data);

    public interface OnAudioMixListener {
        /**
         * invoke when mixing, if you want to stop the mixing process, you can throw an AudioMixException
         *
         * @param mixBytes
         * @throws AudioMixException
         */
        void onMixing(byte[] mixBytes) throws IOException;

        void onMixError(int errorCode);

        /**
         * invoke when mix success
         */
        void onMixComplete();
    }

    public static class AudioMixException extends IOException {
        private static final long serialVersionUID = -1344782236320621800L;

        public AudioMixException(String msg) {
            super(msg);
        }
    }

    /**
     * 平均值算法
     *
     * @author Darcy
     */
    private static class AverageAudioMixer extends MultiAudioMixer {

        @Override
        public byte[] mixRawAudioBytes(byte[][] bMulRoadAudios) {

            if (bMulRoadAudios == null || bMulRoadAudios.length == 0)
                return null;

            byte[] realMixAudio = bMulRoadAudios[0];
            if(realMixAudio == null){
                return null;
            }

            final int row = bMulRoadAudios.length;

            //单路音轨
            if (bMulRoadAudios.length == 1)
                return realMixAudio;

            //不同轨道长度要一致，不够要补齐

            for (int rw = 0; rw < bMulRoadAudios.length; ++rw) {
                if (bMulRoadAudios[rw] == null || bMulRoadAudios[rw].length != realMixAudio.length) {
                    Log.e("app", "column of the road of audio + " + rw + " is diffrent.");
                    return null;
                }
            }

            /**
             * 精度为 16位
             */
            int col = realMixAudio.length / 2;
            short[][] sMulRoadAudios = new short[row][col];

            for (int r = 0; r < row; ++r) {
                for (int c = 0; c < col; ++c) {
                    sMulRoadAudios[r][c] = (short) ((bMulRoadAudios[r][c * 2] & 0xff) | (bMulRoadAudios[r][c * 2 + 1] & 0xff) << 8);
                }
            }

            short[] sMixAudio = new short[col];
            int mixVal;
            int sr = 0;
            for (int sc = 0; sc < col; ++sc) {
                mixVal = 0;
                sr = 0;
                for (; sr < row; ++sr) {
                    mixVal += sMulRoadAudios[sr][sc];
                }
                sMixAudio[sc] = (short) (mixVal / row);
            }

            for (sr = 0; sr < col; ++sr) {
                realMixAudio[sr * 2] = (byte) (sMixAudio[sr] & 0x00FF);
                realMixAudio[sr * 2 + 1] = (byte) ((sMixAudio[sr] & 0xFF00) >> 8);
            }

            return realMixAudio;
        }

    }

    /**
     * 自适应音频混合方案
     */
    private static class AutoAlignAudioMixer extends MultiAudioMixer{

        @Override
        public byte[] mixRawAudioBytes(byte[][] bMulRoadAudios) {

            if (bMulRoadAudios == null || bMulRoadAudios.length == 0)
                return null;

            byte[] realMixAudio = bMulRoadAudios[0];
            final int row = bMulRoadAudios.length;

            //单路音轨
            if (bMulRoadAudios.length == 1)
                return realMixAudio;

            //不同轨道长度要一致，不够要补齐
            for (int rw = 0; rw < bMulRoadAudios.length; ++rw) {
                if (bMulRoadAudios[rw].length != realMixAudio.length) {
                    Log.e("app", "column of the road of audio + " + rw + " is diffrent.");
                    return null;
                }
            }

            /**
             * 精度为 16位
             */
            int col = realMixAudio.length / 2;
            short[][] sMulRoadAudios = new short[row][col];

            for (int r = 0; r < row; ++r) {
                for (int c = 0; c < col; ++c) {
                    sMulRoadAudios[r][c] = (short) ((bMulRoadAudios[r][c * 2] & 0xff) | (bMulRoadAudios[r][c * 2 + 1] & 0xff) << 8);
                }
            }

            short[] sMixAudio = new short[col];
            int sr = 0;

            double wValue;
            double absSumVal;

            for (int sc = 0; sc < col; ++sc) {
                sr = 0;

                wValue = 0;
                absSumVal = 0;

                for (; sr < row; ++sr) {
                    wValue += Math.pow(sMulRoadAudios[sr][sc], 2) * Math.signum(sMulRoadAudios[sr][sc]);
                    absSumVal += Math.abs(sMulRoadAudios[sr][sc]);
                }

                sMixAudio[sc] = absSumVal == 0 ? 0 : (short) (wValue / absSumVal);
            }

            for (sr = 0; sr < col; ++sr) {
                realMixAudio[sr * 2] = (byte) (sMixAudio[sr] & 0x00FF);
                realMixAudio[sr * 2 + 1] = (byte) ((sMixAudio[sr] & 0xFF00) >> 8);
            }

            return realMixAudio;
        }
    }

    /**
     * 多声道方案
     */
    private static class ChannelAlignAudioMixer extends MultiAudioMixer{

        @Override
        public byte[] mixRawAudioBytes(byte[][] bMulRoadAudios) {

            if (bMulRoadAudios == null || bMulRoadAudios.length == 0)
                return null;

            int roadLen = bMulRoadAudios.length;

            //单路音轨
            if (roadLen == 1)
                return bMulRoadAudios[0];

            int maxRoadByteLen = 0;

            for(byte[] audioData : bMulRoadAudios){
                if(maxRoadByteLen < audioData.length){
                    maxRoadByteLen = audioData.length;
                }
            }

            byte[] resultMixData = new byte[maxRoadByteLen * roadLen];

            for(int i = 0; i != maxRoadByteLen; i = i + 2){
                for(int r = 0; r != roadLen; r++){
                    resultMixData[i * roadLen + 2 * r] = bMulRoadAudios[r][i];
                    resultMixData[i * roadLen + 2 * r + 1] = bMulRoadAudios[r][i+1];
                }
            }
            return resultMixData;
        }
    }
}
