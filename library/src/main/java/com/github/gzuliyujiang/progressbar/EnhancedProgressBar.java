/*
 * Copyright (c) 2019-2021 gzu-liyujiang <1032694760@qq.com>
 *
 * The software is licensed under the Mulan PSL v1.
 * You can use this software according to the terms and conditions of the Mulan PSL v1.
 * You may obtain a copy of Mulan PSL v1 at:
 *     http://license.coscl.org.cn/MulanPSL
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR
 * PURPOSE.
 * See the Mulan PSL v1 for more details.
 *
 */

package com.github.gzuliyujiang.progressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/**
 * 增强版{@link ProgressBar}，带数字的水平滚动条（支持长方形、平行四边形及椭圆角矩形）。
 * 基于 https://github.com/hongyangAndroid/Android-ProgressBarWidthNumber 修改。
 *
 * @author 贵州山野羡民（1032694760@qq.com）
 * @since 2021/3/20 10:17
 */
public class EnhancedProgressBar extends ProgressBar {
    private static final int DEFAULT_TEXT_SIZE = 10;
    private static final int DEFAULT_TEXT_COLOR = 0XFFFC00D1;
    private static final int DEFAULT_COLOR_UNREACHED_COLOR = 0xFFd3d6da;
    private static final int DEFAULT_HEIGHT_REACHED_BAR = 2;
    private static final int DEFAULT_HEIGHT_UNREACHED_BAR = 2;
    private static final int DEFAULT_SIZE_TEXT_OFFSET = 5;
    private static final int CUT_CORNER_NONE = 0;
    private static final int CUT_CORNER_PARALLELOGRAM = 1;
    private static final int CUT_CORNER_ELLIPSE = 2;
    private static final int TEXT_ALIGN_MIDDLE = 0;
    private static final int TEXT_ALIGN_TOP = 1;
    private final RectF mRectF = new RectF();
    private final Paint mPaint = new Paint();
    private int mProgress = 0;
    private boolean mTextVisible = false;
    private int mTextColor = DEFAULT_TEXT_COLOR;
    private int mTextSize = sp2px(DEFAULT_TEXT_SIZE);
    private int mTextOffset = dp2px(DEFAULT_SIZE_TEXT_OFFSET);
    private int mTextAlign = TEXT_ALIGN_MIDDLE;
    private boolean mTextBold = true;
    private int mCutCorner = CUT_CORNER_NONE;
    private int mReachedBarHeight = dp2px(DEFAULT_HEIGHT_REACHED_BAR);
    private int mReachedBarColor = DEFAULT_TEXT_COLOR;
    private int mUnReachedBarColor = DEFAULT_COLOR_UNREACHED_COLOR;
    private int mUnReachedBarHeight = dp2px(DEFAULT_HEIGHT_UNREACHED_BAR);
    private int mBarHeight;
    private int mRealWidth;

    public EnhancedProgressBar(Context context) {
        super(context);
        init(context, null);
    }

    public EnhancedProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EnhancedProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EnhancedProgressBar);
            mProgress = a.getInt(R.styleable.EnhancedProgressBar_android_progress, 0);
            mTextColor = a.getColor(R.styleable.EnhancedProgressBar_lyj_text_color, DEFAULT_TEXT_COLOR);
            mTextSize = (int) a.getDimension(R.styleable.EnhancedProgressBar_lyj_text_size, mTextSize);
            mReachedBarColor = a.getColor(R.styleable.EnhancedProgressBar_lyj_reached_color, mTextColor);
            mUnReachedBarColor = a.getColor(R.styleable.EnhancedProgressBar_lyj_unreached_color, DEFAULT_COLOR_UNREACHED_COLOR);
            mReachedBarHeight = (int) a.getDimension(R.styleable.EnhancedProgressBar_lyj_reached_height, mReachedBarHeight);
            mUnReachedBarHeight = (int) a.getDimension(R.styleable.EnhancedProgressBar_lyj_unreached_height, mUnReachedBarHeight);
            mTextOffset = (int) a.getDimension(R.styleable.EnhancedProgressBar_lyj_text_offset, mTextOffset);
            mTextAlign = a.getInt(R.styleable.EnhancedProgressBar_lyj_text_align, TEXT_ALIGN_TOP);
            mTextBold = a.getBoolean(R.styleable.EnhancedProgressBar_lyj_text_bold, true);
            mCutCorner = a.getInt(R.styleable.EnhancedProgressBar_lyj_cut_corner, CUT_CORNER_ELLIPSE);
            mTextVisible = a.getBoolean(R.styleable.EnhancedProgressBar_lyj_text_visible, false);
            if (!mTextVisible) {
                mTextOffset = 0;
                mTextAlign = TEXT_ALIGN_MIDDLE;
            }
            a.recycle();
        }
        mBarHeight = Math.max(mReachedBarHeight, mUnReachedBarHeight);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mTextSize);
        mPaint.setColor(mTextColor);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec,
                                          int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
        mRealWidth = getMeasuredWidth() - getPaddingRight() - getPaddingLeft();
    }

    private int measureHeight(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            float heightExcludeText = getPaddingTop() + getPaddingBottom() + mBarHeight;
            if (mTextVisible) {
                Paint.FontMetrics metrics = mPaint.getFontMetrics();
                float textHeight = Math.abs(metrics.bottom - metrics.top + metrics.leading);
                if (mTextAlign == TEXT_ALIGN_MIDDLE) {
                    result = (int) Math.max(heightExcludeText, textHeight);
                } else {
                    result = (int) (heightExcludeText + textHeight * 2);
                }
            } else {
                result = (int) heightExcludeText;
            }
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    public synchronized int getProgress() {
        return mProgress;
    }

    @Override
    public synchronized void setProgress(int progress) {
        mProgress = progress;
        invalidate();
    }

    @Override
    public void setProgress(int progress, boolean animate) {
        mProgress = progress;
        invalidate();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getPaddingLeft(), getHeight() / 2f);
        boolean needDrawUnreachedBar = true;
        String text = mProgress + "%";
        float textWidth;
        if (mTextVisible) {
            textWidth = mPaint.measureText(text);
        } else {
            textWidth = 0;
        }
        float progressPosX = mRealWidth * (mProgress * 1.0f / getMax());
        float textPosX = progressPosX;
        float reachedEndX;
        if (mTextAlign == TEXT_ALIGN_MIDDLE) {
            reachedEndX = progressPosX - mTextOffset;
        } else {
            reachedEndX = progressPosX;
        }
        float unreachedStartX;
        if (mTextAlign == TEXT_ALIGN_MIDDLE) {
            unreachedStartX = progressPosX + textWidth * 1.4f + mTextOffset;
        } else {
            unreachedStartX = progressPosX;
        }
        if (progressPosX + textWidth > mRealWidth) {
            textPosX = mRealWidth - textWidth;
            if (mTextAlign == TEXT_ALIGN_MIDDLE) {
                needDrawUnreachedBar = false;
            }
        }
        if (needDrawUnreachedBar) {
            drawUnreachedBar(canvas, unreachedStartX);
        }
        drawReachedBar(canvas, reachedEndX);
        if (mTextVisible) {
            drawText(canvas, text, textPosX);
        }
        canvas.restore();
    }

    private void drawUnreachedBar(Canvas canvas, float unreachedStartX) {
        mPaint.setColor(mUnReachedBarColor);
        if (mCutCorner == CUT_CORNER_NONE) {
            //noinspection SuspiciousNameCombination
            mPaint.setStrokeWidth(mUnReachedBarHeight);
            canvas.drawLine(unreachedStartX, 0, mRealWidth, 0, mPaint);
            return;
        }
        mPaint.setStrokeWidth(0);
        unreachedStartX = Math.max(0, unreachedStartX - mBarHeight);
        int barGap = mReachedBarHeight - mUnReachedBarHeight;
        if (mCutCorner == CUT_CORNER_ELLIPSE) {
            barGap = barGap / 2;
        }
        float top = -1 * mBarHeight / 2f + Math.max(0, barGap);
        mRectF.set(unreachedStartX, top, mRealWidth, mUnReachedBarHeight + top);
        Path path;
        if (mCutCorner == CUT_CORNER_PARALLELOGRAM) {
            path = generateParallelogram(mRectF, mUnReachedBarHeight);
        } else {
            path = generateEllipse(mRectF, mUnReachedBarHeight);
        }
        canvas.drawPath(path, mPaint);
    }

    private void drawReachedBar(Canvas canvas, float reachedEndX) {
        mPaint.setColor(mReachedBarColor);
        if (mCutCorner == CUT_CORNER_NONE) {
            //noinspection SuspiciousNameCombination
            mPaint.setStrokeWidth(mReachedBarHeight);
            canvas.drawLine(0, 0, reachedEndX, 0, mPaint);
            return;
        }
        if (mCutCorner == CUT_CORNER_PARALLELOGRAM && reachedEndX < mBarHeight) {
            return;
        }
        mPaint.setStrokeWidth(0);
        int barGap = mUnReachedBarHeight - mReachedBarHeight;
        if (mCutCorner == CUT_CORNER_ELLIPSE) {
            barGap = barGap / 2;
        }
        float top = -1 * mBarHeight / 2f + Math.max(0, barGap);
        mRectF.set(0, top, reachedEndX, mReachedBarHeight + top);
        Path path;
        if (mCutCorner == CUT_CORNER_PARALLELOGRAM) {
            path = generateParallelogram(mRectF, mReachedBarHeight);
        } else {
            path = generateEllipse(mRectF, mReachedBarHeight);
        }
        canvas.drawPath(path, mPaint);
    }

    private void drawText(Canvas canvas, String text, float textPosX) {
        mPaint.setColor(mTextColor);
        mPaint.setTypeface(Typeface.defaultFromStyle(mTextBold ? Typeface.BOLD : Typeface.NORMAL));
        Paint.FontMetrics metrics = mPaint.getFontMetrics();
        float textPosY;
        if (mTextAlign == TEXT_ALIGN_MIDDLE) {
            textPosY = -1 * (metrics.top + metrics.bottom) / 2f;
        } else {
            textPosY = -1 * mBarHeight;
        }
        canvas.drawText(text, textPosX, textPosY, mPaint);
    }

    private Path generateParallelogram(RectF rect, float diameter) {
        // See com.github.florent37.shapeofview.shapes.CutCornerView
        diameter = diameter < 0 ? 0 : diameter;
        float topLeftDiameter = diameter;
        float topRightDiameter = 0;
        float bottomRightDiameter = diameter;
        float bottomLeftDiameter = 0;
        final Path path = new Path();
        path.moveTo(rect.left + topLeftDiameter, rect.top);
        path.lineTo(rect.right - topRightDiameter, rect.top);
        path.lineTo(rect.right, rect.top + topRightDiameter);
        path.lineTo(rect.right, rect.bottom - bottomRightDiameter);
        path.lineTo(rect.right - bottomRightDiameter, rect.bottom);
        path.lineTo(rect.left + bottomLeftDiameter, rect.bottom);
        path.lineTo(rect.left, rect.bottom - bottomLeftDiameter);
        path.lineTo(rect.left, rect.top + topLeftDiameter);
        path.lineTo(rect.left + topLeftDiameter, rect.top);
        path.close();
        return path;
    }

    @SuppressWarnings({"ConstantConditions", "UnnecessaryLocalVariable"})
    private Path generateEllipse(RectF rect, float radius) {
        // See com.github.florent37.shapeofview.shapes.RoundRectView
        boolean useBezier = false;
        float topLeftRadius = radius;
        float topRightRadius = radius;
        float bottomLeftRadius = radius;
        float bottomRightRadius = radius;
        final Path path = new Path();
        final float left = rect.left;
        final float top = rect.top;
        final float bottom = rect.bottom;
        final float right = rect.right;
        final float maxSize = Math.min(rect.width() / 2f, rect.height() / 2f);
        float topLeftRadiusAbs = Math.abs(topLeftRadius);
        float topRightRadiusAbs = Math.abs(topRightRadius);
        float bottomLeftRadiusAbs = Math.abs(bottomLeftRadius);
        float bottomRightRadiusAbs = Math.abs(bottomRightRadius);
        if (topLeftRadiusAbs > maxSize) {
            topLeftRadiusAbs = maxSize;
        }
        if (topRightRadiusAbs > maxSize) {
            topRightRadiusAbs = maxSize;
        }
        if (bottomLeftRadiusAbs > maxSize) {
            bottomLeftRadiusAbs = maxSize;
        }
        if (bottomRightRadiusAbs > maxSize) {
            bottomRightRadiusAbs = maxSize;
        }
        path.moveTo(left + topLeftRadiusAbs, top);
        path.lineTo(right - topRightRadiusAbs, top);
        if (useBezier) {
            path.quadTo(right, top, right, top + topRightRadiusAbs);
        } else {
            final float arc = topRightRadius > 0 ? 90 : -270;
            path.arcTo(new RectF(right - topRightRadiusAbs * 2f, top, right, top + topRightRadiusAbs * 2f), -90, arc);
        }
        path.lineTo(right, bottom - bottomRightRadiusAbs);
        if (useBezier) {
            path.quadTo(right, bottom, right - bottomRightRadiusAbs, bottom);
        } else {
            final float arc = bottomRightRadiusAbs > 0 ? 90 : -270;
            path.arcTo(new RectF(right - bottomRightRadiusAbs * 2f, bottom - bottomRightRadiusAbs * 2f, right, bottom), 0, arc);
        }
        path.lineTo(left + bottomLeftRadiusAbs, bottom);
        if (useBezier) {
            path.quadTo(left, bottom, left, bottom - bottomLeftRadiusAbs);
        } else {
            final float arc = bottomLeftRadiusAbs > 0 ? 90 : -270;
            path.arcTo(new RectF(left, bottom - bottomLeftRadiusAbs * 2f, left + bottomLeftRadiusAbs * 2f, bottom), 90, arc);
        }
        path.lineTo(left, top + topLeftRadiusAbs);
        if (useBezier) {
            path.quadTo(left, top, left + topLeftRadiusAbs, top);
        } else {
            final float arc = topLeftRadiusAbs > 0 ? 90 : -270;
            path.arcTo(new RectF(left, top, left + topLeftRadiusAbs * 2f, top + topLeftRadiusAbs * 2f), 180, arc);
        }
        path.close();
        return path;
    }

    private int sp2px(@SuppressWarnings("SameParameterValue") int spVal) {
        return (int) (spVal * getResources().getDisplayMetrics().scaledDensity);
    }

    private int dp2px(int dpVal) {
        return (int) (dpVal * getResources().getDisplayMetrics().density);
    }

}
