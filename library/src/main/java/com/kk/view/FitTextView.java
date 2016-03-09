package com.kk.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;

public class FitTextView extends CompactTextView {

    private boolean mMeasured = false;
    /**
     * 不需要调整大小
     */
    private boolean mNeedFit = true;
    protected float mOriginalTextSize = 0;
    private float mMinTextSize, mMaxTextSize;
    protected CharSequence mOriginalText;
    /**
     * 正在调整字体大小
     */
    protected volatile boolean mFittingText = false;
    protected FitTextHelper mFitTextHelper;

    public FitTextView(Context context) {
        this(context, null);
    }

    public FitTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FitTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mOriginalTextSize = getTextSize();
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, new int[]{
                    com.kk.justifytextview.R.attr.ftMaxTextSize,
                    com.kk.justifytextview.R.attr.ftMinTextSize,
            });
            mMinTextSize = a.getDimension(0, mOriginalTextSize / 2.0f);
            mMaxTextSize = a.getDimension(1, mOriginalTextSize * 2.0f);
            a.recycle();
        } else {
            mMinTextSize = mOriginalTextSize;
            mMaxTextSize = mOriginalTextSize;
        }
    }

    protected FitTextHelper getFitTextHelper() {
        if (mFitTextHelper == null) {
            mFitTextHelper = new FitTextHelper(this);
        }
        return mFitTextHelper;
    }

    /**
     * @return 最小字体大小
     */
    public float getMinTextSize() {
        return mMinTextSize;
    }

    /**
     * @param minTextSize 最小字体大小
     */
    public void setMinTextSize(float minTextSize) {
        mMinTextSize = minTextSize;
    }

    /**
     * @return 最大字体大小
     */
    public float getMaxTextSize() {
        return mMaxTextSize;
    }

    /**
     * @param maxTextSize 最大字体大小
     */
    public void setMaxTextSize(float maxTextSize) {
        mMaxTextSize = maxTextSize;
    }

    /**
     * 是否需要调整字体
     *
     * @return
     */
    public boolean isNeedFit() {
        return mNeedFit;
    }

    /**
     * @param needFit 是否需要调整字体大小
     */
    public void setNeedFit(boolean needFit) {
        mNeedFit = needFit;
    }

    @Override
    public void setTextSize(int unit, float size) {
        super.setTextSize(unit, size);
        mOriginalTextSize = getTextSize();
    }

    public float getOriginalTextSize() {
        return mOriginalTextSize;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.UNSPECIFIED
                && heightMode == MeasureSpec.UNSPECIFIED) {
            super.setTextSize(TypedValue.COMPLEX_UNIT_PX, mOriginalTextSize);
            mMeasured = false;
        } else {
            mMeasured = true;
            fitText(getOriginalText());
        }
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        mOriginalText = text;
        super.setText(text, type);
        fitText(text);
    }

    public CharSequence getOriginalText() {
        return mOriginalText;
    }

    /**
     * 调整字体大小
     *
     * @param text 内容
     */
    protected void fitText(CharSequence text) {
        if (!mNeedFit) {
            return;
        }
        if (!mMeasured || mFittingText || mSingleLine || TextUtils.isEmpty(text))
            return;
        mFittingText = true;
        TextPaint oldPaint = getPaint();
        float size = getFitTextHelper().fitTextSize(oldPaint, text, mMaxTextSize, mMinTextSize);
        super.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        super.setText(getFitTextHelper().getLineBreaks(text, getPaint()));
        mFittingText = false;
    }
}
