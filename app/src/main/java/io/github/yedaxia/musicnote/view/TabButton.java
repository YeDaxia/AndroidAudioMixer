package io.github.yedaxia.musicnote.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.RadioButton;

import io.github.yedaxia.musicnote.R;
import io.github.yedaxia.musicnote.app.util.ViewUtils;


/**
 * <p>Tab with drawable and text elements, you can use both or one of them.
 * Also , there has simple tip for the Tap , you can decide the Style all by your self..
 * <p> hope you like it :-)
 *
 * @author Darcy https://yedaxia.github.io/
 */
public class TabButton extends RadioButton {

    private static final int LABEL_TEXT_ALIGN_LEFT = 1;
    private static final int LABEL_TEXT_ALIGN_RIGHT = 2;
    private static final int LABEL_TEXT_ALIGN_TOP = 3;
    private static final int LABEL_TEXT_ALIGN_BOTTOM = 4;

    private Drawable buttonDrawable;

    private int mDrawableWidth;
    private int mDrawableHeight;

    private int mLabelTextAlign;

    private int mTipBackgroundColor;
    private int mTipTextColor;
    private int mTipTextSize;

    private int mTipDotRadius;
    private int mTextDotRadius;

    private TextPaint mTipTextPaint;
    private Paint mTipBgPaint;

    private boolean mHasTip;
    private String mTipText;

    private int mTextDotTopMargin;
    private int mTextDotRightMargin;
    private int mDrawableLabelGap;

    private Rect mRect = new Rect();
    private RectF mRectF = new RectF();

    private int mDotTopMargin;

    public TabButton(Context context) {
        this(context, null);
    }

    public TabButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TabButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray cbTypedArray = context.obtainStyledAttributes(attrs, R.styleable.CompoundButton, 0, 0);
        buttonDrawable = cbTypedArray.getDrawable(R.styleable.CompoundButton_android_button);
        mDrawableWidth = cbTypedArray.getDimensionPixelSize(R.styleable.CompoundButton_drawableWidth, 0);
        mDrawableHeight = cbTypedArray.getDimensionPixelSize(R.styleable.CompoundButton_drawableHeight, 0);
        cbTypedArray.recycle();

        TypedArray tbTypedArray = context.obtainStyledAttributes(attrs, R.styleable.TabButton, 0, 0);
        mTipBackgroundColor = tbTypedArray.getColor(R.styleable.TabButton_tipBackgroundColor, 0xFFFF0000);
        mTipTextColor = tbTypedArray.getColor(R.styleable.TabButton_tipTextColor, 0xFF000000);
        mTipTextSize = tbTypedArray.getDimensionPixelSize(R.styleable.TabButton_tipTextSize, 10);
        mTipDotRadius = tbTypedArray.getDimensionPixelSize(R.styleable.TabButton_tipDotRadius, 5);
        mTextDotRadius = tbTypedArray.getDimensionPixelSize(R.styleable.TabButton_tipTextDotRadius, 10);
        mDrawableLabelGap = tbTypedArray.getDimensionPixelSize(R.styleable.TabButton_drawableLabelGap, 2);
        mLabelTextAlign = tbTypedArray.getInt(R.styleable.TabButton_labelTextAlign, LABEL_TEXT_ALIGN_BOTTOM);
        mTipText = tbTypedArray.getString(R.styleable.TabButton_tipText);
        mTextDotTopMargin = tbTypedArray.getDimensionPixelSize(R.styleable.TabButton_tipTextDotTopMargin, 0);
        mTextDotRightMargin = tbTypedArray.getDimensionPixelSize(R.styleable.TabButton_tipTextDotRightMargin, 0);
        tbTypedArray.recycle();

        setButtonDrawable(R.drawable.empty_drawable);

        CharSequence labelText = getText();
        if (buttonDrawable == null || labelText == null || labelText.length() == 0) {
            mDrawableLabelGap = 0;
        }

        mTipBgPaint = new Paint();
        mTipBgPaint.setColor(mTipBackgroundColor);
        mTipBgPaint.setAntiAlias(true);
        mTipBgPaint.setStyle(Paint.Style.FILL);

        mTipTextPaint = new TextPaint();
        mTipTextPaint.setAntiAlias(true);
        mTipTextPaint.setColor(mTipTextColor);
        mTipTextPaint.setTextSize(mTipTextSize);
    }

    /**
     * when there exists tip text and dot tip at the same time, text will show to front
     *
     * @param hasTip
     * @see {@link #setTipText(String tipText)}
     */
    public void setHasTip(boolean hasTip) {
        if (mHasTip == hasTip){
            return;
        }

        mHasTip = hasTip;
        invalidate();
    }

    /**
     * set tip text margin
     * @param marginTop
     * @param marginRight
     */
    public void setTipTextMargin(int marginTop, int marginRight) {
        mTextDotTopMargin = marginTop;
        mTextDotRightMargin = marginRight;
        invalidate();
    }

    /**
     * if has red tip, can be text tip  or a dot tip
     * @return
     */
    public boolean hasTip() {
        return mHasTip;
    }

    /**
     * when there exists tip text and dot tip at the same time, text will show to front
     *
     * @param tipText
     * @see {@link #setHasTip(boolean hasTip)}
     */
    public void setTipText(String tipText) {
        if (mTipText != null && mTipText.equals(tipText)){
            return;
        }

        mTipText = tipText;
        invalidate();
    }

    /**
     * set Button Icon
     * @param drawable
     */
    public void setIcon(Drawable drawable) {
        buttonDrawable = drawable;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int width = getWidth();
        final int height = getHeight();
        final int[] drawableState = getDrawableState();

        //draw background
        Drawable backgroundDrawable = getBackground();
        if (backgroundDrawable != null) {
            backgroundDrawable.setState(drawableState);
            backgroundDrawable.setBounds(0, 0, width, height);
            backgroundDrawable.draw(canvas);
        }

        final CharSequence labelText = getText();
        final TextPaint labelPaint = getPaint();

        boolean noLabel = false;
        float labelTextHeight = 0;
        float labelTextWidth = 0;
        if (labelText == null || labelText.length() == 0) {
            noLabel = true;
        } else {
            labelTextHeight = ViewUtils.calculateFontHeight(labelPaint);
            labelTextWidth = ViewUtils.calculateStringWidth(labelPaint, labelText.toString());
        }

        if (noLabel && buttonDrawable == null) {
            return;
        }

        float buttonDrawableLeft = 0;
        float buttonDrawableRight = 0;
        float buttonDrawableTop = 0;
        float buttonDrawableBottom = 0;

        int drawableHeight = 0;
        int drawableWidth = 0;

        //draw drawable
        if (buttonDrawable != null) {
            buttonDrawable.setState(drawableState);
            drawableWidth = mDrawableWidth == 0 ? buttonDrawable.getIntrinsicWidth() : mDrawableWidth;
            drawableHeight = mDrawableHeight == 0 ? buttonDrawable.getIntrinsicHeight() : mDrawableHeight;

            if (noLabel) {
                buttonDrawableLeft = (width - drawableWidth) / 2;
                buttonDrawableTop = (height - drawableHeight) / 2;
            } else if (mLabelTextAlign == LABEL_TEXT_ALIGN_BOTTOM) {
                buttonDrawableLeft = (width - drawableWidth) / 2;
                buttonDrawableTop = (height - drawableHeight - labelTextHeight - mDrawableLabelGap) / 2;
            } else if (mLabelTextAlign == LABEL_TEXT_ALIGN_RIGHT) {
                buttonDrawableLeft = (width - drawableWidth - labelTextWidth - mDrawableLabelGap) / 2;
                buttonDrawableTop = (height - drawableHeight) / 2;
            } else if (mLabelTextAlign == LABEL_TEXT_ALIGN_LEFT) {
                buttonDrawableLeft = (width - drawableWidth - labelTextWidth - mDrawableLabelGap) / 2 + labelTextWidth + mDrawableLabelGap;
                buttonDrawableTop = (height - drawableHeight) / 2;
            } else if (mLabelTextAlign == LABEL_TEXT_ALIGN_TOP) {
                buttonDrawableLeft = (width - drawableWidth) / 2;
                buttonDrawableTop = (height - drawableHeight - labelTextHeight - mDrawableLabelGap) / 2 + labelTextWidth + mDrawableLabelGap;
            }

            buttonDrawableRight = buttonDrawableLeft + drawableWidth;
            buttonDrawableBottom = buttonDrawableTop + drawableHeight;
            mRect.set((int) buttonDrawableLeft, (int) buttonDrawableTop, (int) buttonDrawableRight, (int) buttonDrawableBottom);
            buttonDrawable.setBounds(mRect);
            buttonDrawable.draw(canvas);
        }

        float labelTextX = 0;
        float labelTextY = 0;

        //draw label text
        if (!noLabel) {
            ColorStateList labelCSList = getTextColors();
            int curColor = labelCSList.getColorForState(drawableState, labelCSList.getDefaultColor());
            labelPaint.setColor(curColor);
            final String drawLableText = labelText.toString();
            if (mLabelTextAlign == LABEL_TEXT_ALIGN_BOTTOM) {
                labelTextX = (width - labelTextWidth) / 2;
                labelTextY = (height - drawableHeight - labelTextHeight - mDrawableLabelGap) / 2 + drawableHeight + mDrawableLabelGap;
            } else if (mLabelTextAlign == LABEL_TEXT_ALIGN_RIGHT) {
                labelTextX = (width - drawableWidth - mDrawableLabelGap - labelTextWidth) / 2 + drawableWidth + mDrawableLabelGap;
                labelTextY = (height - labelTextHeight) / 2;
            } else if (mLabelTextAlign == LABEL_TEXT_ALIGN_LEFT) {
                labelTextX = (width - drawableWidth - mDrawableLabelGap - labelTextWidth) / 2;
                labelTextY = (height - labelTextHeight) / 2;
            } else if (mLabelTextAlign == LABEL_TEXT_ALIGN_TOP) {
                labelTextX = (width - labelTextWidth) / 2;
                labelTextY = (height - drawableHeight - labelTextHeight - mDrawableLabelGap) / 2;
            }
            float adjustTextY = adjustTextY(labelPaint, labelTextY);
            canvas.drawText(drawLableText, labelTextX, adjustTextY, labelPaint);
        }

        // draw tip dot
        if (mHasTip) {
            float cx = 0;
            float cy = 0;
            if (noLabel) {
                cx = buttonDrawableRight;
                cy = buttonDrawableTop;
            } else if (buttonDrawable == null) {
                cx = labelTextX + labelTextWidth;
                cy = labelTextY;
            } else {
                if (mLabelTextAlign == LABEL_TEXT_ALIGN_RIGHT || mLabelTextAlign == LABEL_TEXT_ALIGN_TOP) {
                    cx = labelTextX + labelTextWidth;
                    cy = labelTextY;
                } else {
                    cx = buttonDrawableRight;
                    cy = buttonDrawableTop;
                }
            }

            canvas.drawCircle(cx, cy + mDotTopMargin, mTipDotRadius, mTipBgPaint);
        }

        // draw tip text
        if (!TextUtils.isEmpty(mTipText)) {
            float tipTextHeight = ViewUtils.calculateFontHeight(mTipTextPaint);
            float tipTextWidth = ViewUtils.calculateStringWidth(mTipTextPaint, mTipText);

            mRectF.right = width - mTextDotRightMargin;
            float tipTextLen = mTipText.length();
            if (tipTextLen == 1) {
                mRectF.left = mRectF.right - mTextDotRadius * 2;
            } else {
                mRectF.left = mRectF.right - mTextDotRadius * 2 - tipTextWidth * ((tipTextLen - 1) / tipTextLen);
            }

            mRectF.top = mTextDotTopMargin;
            mRectF.bottom = mRectF.top + mTextDotRadius * 2;
            canvas.drawRoundRect(mRectF, mTextDotRadius, mTextDotRadius, mTipBgPaint);

            float tipTextX = mRectF.left + (mRectF.width() - tipTextWidth) * 0.5f;
            float tipTextY = mRectF.top + (mRectF.height() - tipTextHeight) * 0.5f;
            tipTextY = adjustTextY(mTipTextPaint, tipTextY);
            canvas.drawText(mTipText, tipTextX, tipTextY, mTipTextPaint);
        }
    }

    private float adjustTextY(Paint p, float textY) {
        return textY - p.getFontMetrics().top;
    }

}