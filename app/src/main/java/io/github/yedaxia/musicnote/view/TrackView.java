package io.github.yedaxia.musicnote.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;

import java.util.List;

import io.github.yedaxia.musicnote.R;
import io.github.yedaxia.musicnote.app.util.ResUtils;

/**
 * 波形图
 *
 * @author Darcy https://yedaxia.github.io/
 * @version 2018/2/8.
 */

public class TrackView extends View{

    private int waveColor;
    private int middleLineColor;
    private int playLineColor ;
    private int topLineColor;

    private Paint paint;
    private List<Double> frameGains;
    private int playingFramePos = 0; //当前播放帧

    public TrackView(Context context) {
        this(context, null);
    }

    public TrackView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TrackView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        topLineColor = ResUtils.getColor(R.color.light_line_color);
        waveColor = ResUtils.getColor(R.color.wave_line_color_primary);
        middleLineColor = ResUtils.getColor(R.color.wave_middle_line_color);
        playLineColor = ResUtils.getColor(R.color.wave_play_line_color);
    }

    /**
     * 填充波形数据
     * @param frameGains 和最大振幅的比率
     */
    public void setWaveData(List<Double> frameGains){
        this.frameGains = frameGains;
        invalidate();
    }

    /**
     * 设置当前播放的帧
     * @param position
     */
    public void setPlayingFrame(int position){
        playingFramePos = position;
        invalidate();
    }

    /**
     * 设置波形的颜色
     * @param resId
     */
    public void setWaveColor(@ColorRes int resId){
        waveColor = ResUtils.getColor(resId);
        invalidate();
    }

    public int frameCount(){
        return frameGains.size();
    }

    public int playingFramePosition(){
        return playingFramePos;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //draw top line
        paint.setColor(topLineColor);
        canvas.drawLine(0,0, getWidth(), 0, paint);

        int frameWidth = frameGains.size();

        int canvasHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        int canvasWidth = getWidth() - getPaddingLeft() - getPaddingRight();

        paint.setColor(middleLineColor);

        int lineStartX = getPaddingLeft();
        int lineStartY = getHeight() / 2;

        // draw middle line
        canvas.drawLine(lineStartX, lineStartY, getWidth() - getPaddingRight(), lineStartY, paint);

        if(frameGains == null || frameGains.size() == 0){
            return;
        }

        //最终播放位置
        final int playingLineStopX = canvasWidth - 10;
        final int playingX = playingFramePos < playingLineStopX ? playingFramePos : playingLineStopX;

        // draw waveform
        paint.setColor(waveColor);
        final double halfCanvasHeight = 0.5 * canvasHeight;

        int waveOffset = 0, waveLen = 0;

        if(playingFramePos < frameWidth){
            if(playingFramePos <= playingLineStopX){
                waveOffset = 0;
                waveLen = frameWidth > canvasWidth? canvasWidth : frameWidth;
            }else{
                waveOffset = playingFramePos - playingLineStopX;
                waveLen = (frameWidth - playingFramePos) > playingLineStopX ? canvasWidth : (frameWidth - playingFramePos + playingLineStopX);
            }
        }else{
            if(frameWidth + canvasWidth > playingFramePos){
                if(playingFramePos <= canvasWidth){
                    waveOffset = 0;
                    waveLen = frameWidth;
                }else{
                    waveOffset = playingFramePos - canvasWidth;
                    waveLen = frameWidth - waveOffset;
                }
            }else{
                waveOffset = 0;
                waveLen = 0;
            }
        }

        for(int i = 0; i != waveLen; i++){
            double am = halfCanvasHeight * frameGains.get(waveOffset + i);
            int waveX = getPaddingLeft() + i;
            int startY = (int)(halfCanvasHeight - am);
            int stopY = (int)(halfCanvasHeight + am);
            canvas.drawLine(waveX, startY, waveX, stopY, paint);
        }

        //draw playing line
        paint.setColor(playLineColor);
        canvas.drawLine(playingX, 0, playingX, canvasHeight, paint);
    }
}
