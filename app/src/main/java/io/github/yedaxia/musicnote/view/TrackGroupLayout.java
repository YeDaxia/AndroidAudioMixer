package io.github.yedaxia.musicnote.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * @author Darcy https://yedaxia.github.io/
 * @version 2018/2/23.
 */

public class TrackGroupLayout extends LinearLayout{

    private static final String TAG = "TrackGroupLayout";

    private GestureDetector mGestureDetector;

    private OnTrackScrollListener onTrackScrollListener;
    private boolean isTrackMove;
    private int nextPlayFramePosition;
    private boolean isScrollFirst = true;

    public TrackGroupLayout(Context context) {
        this(context, null);
    }

    public TrackGroupLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TrackGroupLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mGestureDetector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener() {
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float vx, float vy) {
                        return true;
                    }

                    @Override
                    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                        onTrackScroll((int)distanceX);
                        return super.onScroll(e1, e2, distanceX, distanceY);
                    }
                }
        );
    }

    public void setOnTrackScrollListener(OnTrackScrollListener onTrackScrollListener) {
        this.onTrackScrollListener = onTrackScrollListener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP &&
                isTrackMove && onTrackScrollListener != null){
            isScrollFirst = true;
            onTrackScrollListener.onTrackScrollUp(nextPlayFramePosition);
        }

        if(mGestureDetector.onTouchEvent(event)) {
            return true;
        }
        return true;
    }


    private void onTrackScroll(int distance){
        int trackCount = getChildCount();

        isTrackMove = true;

        if(trackCount == 0){
            return;
        }

        if(isScrollFirst && onTrackScrollListener != null){
            isScrollFirst = false;
            onTrackScrollListener.onTrackScrollStart();
        }

        int maxTrackFrames = 0;
        for(int i = 0 ; i != trackCount; i++){
            TrackView trackView = (TrackView)getChildAt(i);
            if(maxTrackFrames < trackView.frameCount()){
                maxTrackFrames = trackView.frameCount();
            }
        }

        int playingFramePosition = ((TrackView)getChildAt(0)).playingFramePosition();

        int nextPlayFramePosition = playingFramePosition + distance;

        if(nextPlayFramePosition < 0){
            nextPlayFramePosition = 0;
        }else if(nextPlayFramePosition > maxTrackFrames){
            nextPlayFramePosition = maxTrackFrames;
        }

        for(int i = 0 ; i != trackCount; i++){
            TrackView trackView = (TrackView)getChildAt(i);
            trackView.setPlayingFrame(nextPlayFramePosition);
        }

        this.nextPlayFramePosition = nextPlayFramePosition;
    }

    public interface OnTrackScrollListener{

        void onTrackScrollStart();

        void onTrackScrollUp(int nextPlayFramePosition);
    }
}
