package com.example.marqueeview.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Handler;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;
import android.widget.TextView;

import com.example.marqueeview.R;


/******************************************************
 * Copyrights @ 2018，Qianjinzhe Technology Co., Ltd.
 *               前进者科技
 * All rights reserved.
 *
 * Filename：
 *              MarqueeTextView.java
 * Description：
 *              自定义跑马灯TextView
 * Author:
 *              youngHu
 * Finished：
 *             2018年05月31日
 ********************************************************/

@SuppressLint("AppCompatCustomView")
public class MarqueeTextView extends TextView {

    /** 默认滚动速度 */
    private static final int ROLLING_SPEED_DEFAULT = 4;
    /** 第一次滚动默认延迟 */
    private static final int FIRST_SCROLL_DELAY_DEFAULT = 1000;
    /** 滚动模式-一直滚动 */
    public static final int SCROLL_FOREVER = 100;
    /** 滚动模式-只滚动一次 */
    public static final int SCROLL_ONCE = 101;

    /** 滚动器 */
    private Scroller mScroller;
    /** 滚动一次的时间 */
    private int mRollingSpeed;
    /** 滚动的初始 X 位置 */
    private int mXPaused = 0;
    /** 是否暂停 */
    private boolean mPaused = true;
    /** 是否第一次 */
    private boolean mFirst = true;
    /** 滚动模式 */
    private int mScrollMode;
    /** 初次滚动时间间隔 */
    private int mFirstScrollDelay;
    /**Handler*/
    private Handler mHandler;

    public MarqueeTextView(Context context) {
        this(context, null);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs, defStyle);
    }

    private void initView(Context context, AttributeSet attrs, int defStyle) {
        mHandler = new Handler();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MarqueeTextView);
        mRollingSpeed = typedArray.getInt(R.styleable.MarqueeTextView_scroll_speed, ROLLING_SPEED_DEFAULT);
        mScrollMode = typedArray.getInt(R.styleable.MarqueeTextView_scroll_mode, SCROLL_FOREVER);
        mFirstScrollDelay = typedArray.getInt(R.styleable.MarqueeTextView_scroll_first_delay, FIRST_SCROLL_DELAY_DEFAULT);
        typedArray.recycle();
        setSingleLine();
        setEllipsize(null);
    }

    /**
     * 开始滚动
     */
    public void startScroll() {
        mXPaused = 0;
        mPaused = true;
        mFirst = true;
        resumeScroll();
    }

    /**
     * 继续滚动
     */
    public void resumeScroll() {
        //获取文字数量
        int textNum = getText().length();
        if (!mPaused)
            return;
        // 设置水平滚动
        setHorizontallyScrolling(true);

        // 使用 LinearInterpolator 进行滚动
        if (mScroller == null) {
            mScroller = new Scroller(this.getContext(), new LinearInterpolator());
            setScroller(mScroller);
        }
        int scrollingLen = calculateScrollingLen();
        final int distance = scrollingLen - mXPaused;
        //设置规定时间滚动完
//        final int duration = (Double.valueOf(mRollingInterval * distance * 1.00000
//                / scrollingLen)).intValue();
        //设置滚动速度
        final int duration = (Double.valueOf(mRollingSpeed * 1.00000 * textNum)).intValue();
        if (mFirst) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScroller.startScroll(mXPaused, 0, distance, 0, duration);
                    invalidate();
                    mPaused = false;
                }
            }, mFirstScrollDelay);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mScroller.startScroll(mXPaused, 0, distance, 0, duration);
                    invalidate();
                    mPaused = false;
                }
            });

        }
    }

    /**
     * 暂停滚动
     */
    public void pauseScroll() {
        if (null == mScroller)
            return;

        if (mPaused)
            return;

        mPaused = true;

        mXPaused = mScroller.getCurrX();

        mScroller.abortAnimation();
    }

    /**
     * 停止滚动，并回到初始位置
     */
    public void stopScroll() {
        if (null == mScroller) {
            return;
        }
        mPaused = true;
        mScroller.startScroll(0, 0, 0, 0, 0);
    }

    /**
     * 计算滚动的距离
     *
     * @return 滚动的距离
     */
    private int calculateScrollingLen() {
        TextPaint tp = getPaint();
        Rect rect = new Rect();
        String strTxt = getText().toString();
        tp.getTextBounds(strTxt, 0, strTxt.length(), rect);
        return rect.width();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (null == mScroller) return;
        if (mScroller.isFinished() && (!mPaused)) {
            if (mScrollMode == SCROLL_ONCE) {
                stopScroll();
                return;
            }
            mPaused = true;
            mXPaused = -1 * getWidth();
            mFirst = false;
            this.resumeScroll();
        }
    }

    /** 获取滚动一次的时间 */
    public int getScrollSpeed() {
        return mRollingSpeed;
    }

    /** 设置滚动一次的时间 */
    public void setScrollSpeed(int speed) {
        this.mRollingSpeed = speed;
    }

    /** 设置滚动模式 */
    public void setScrollMode(int mode) {
        this.mScrollMode = mode;
    }

    /** 获取滚动模式 */
    public int getScrollMode() {
        return this.mScrollMode;
    }

    /** 设置第一次滚动延迟 */
    public void setScrollFirstDelay(int delay) {
        this.mFirstScrollDelay = delay;
    }

    /** 获取第一次滚动延迟 */
    public int getScrollFirstDelay() {
        return mFirstScrollDelay;
    }

    public boolean isPaused() {
        return mPaused;
    }
}