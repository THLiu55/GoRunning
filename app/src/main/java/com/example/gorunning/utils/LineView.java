package com.example.gorunning.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.example.gorunning.R;

import java.util.ArrayList;
import java.util.List;

public class LineView extends View {
    private int mViewMargin;
    private int mLineColor;
    private int mShadowColor;
    private int mTextSize;
    private int mTextColor;
    private int mHeight, mWidth;
    private float mMarginLeft;

    double scale_x, scale_y;

    private List<Point> mListPoint = getPointList();
    private int maxY = 0, minY = 0;
    private Paint mPaint;

    public LineView(Context context) {
        this(context, null);
    }

    public LineView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("NonConstantResourceId")
    public LineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LineView, defStyleAttr, 0);
        int count = array.getIndexCount();
        for (int i = 0; i < count; i++) {
            int index = array.getIndex(i);
            switch (index) {
                case R.styleable.LineView_viewMargin:
                    mViewMargin = array.getDimensionPixelSize(index, (int) TypedValue.
                            applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.LineView_lineColor:
                    mLineColor = array.getColor(index, Color.WHITE);
                    break;
                case R.styleable.LineView_shadowColor:
                    mShadowColor = array.getColor(index, Color.BLUE);
                    break;
                case R.styleable.LineView_lineTextColor:
                    mTextColor = array.getColor(index, Color.BLACK);
                    break;
                case R.styleable.LineView_lineTextSize:
                    mTextSize = array.getDimensionPixelSize(index, (int) TypedValue.
                            applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
                    break;
            }
        }
        array.recycle();
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mLineColor);
        mPaint.setTextSize(mTextSize);
        mPaint.setAntiAlias(true);                    //取消锯齿
        mPaint.setStyle(Paint.Style.STROKE);          //设置画笔为空心
        mMarginLeft = (mViewMargin * 2);              //设置左边的偏移距离
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mHeight == 0) {
            mHeight = getHeight() - mViewMargin * 2;
        }
        if (mWidth == 0) {
            mWidth = getWidth() - mViewMargin * 2;
        }
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mTextColor);
        mPaint.setStrokeWidth(15);
        mPaint.setAntiAlias(true);
        drawLineView(canvas);
        drawBaseLine(canvas);
    }

    private void drawBaseLine(Canvas canvas) {
        canvas.drawLine(mMarginLeft, getHeight() - mViewMargin, getWidth() - mViewMargin, getHeight() - mViewMargin, mPaint);
        canvas.drawLine(mMarginLeft, mViewMargin, mMarginLeft, getHeight() - mViewMargin + 7, mPaint);
    }

    private void drawLineView(Canvas canvas) {
        Path path = new Path();
        path.moveTo(mListPoint.get(0).x, mListPoint.get(0).y);
        float minY = Float.MAX_VALUE, maxY = Float.MIN_VALUE;
        for (int i = 1; i < mListPoint.size(); i++) {
            float x = mListPoint.get(i).x;
            float y = mListPoint.get(i).y;
            path.lineTo(x, y);
            if (minY > y) minY = y;
            if (maxY < y) maxY = y;
        }
        Path path2 = new Path();
        path2.moveTo(mMarginLeft, maxY + 7);
        path2.lineTo(getWidth() - 2 * mViewMargin, maxY + 7);
        path2.lineTo(getWidth() - 2 * mViewMargin, minY - 13);
        path2.lineTo(mMarginLeft, minY - 7);
        path2.close();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mShadowColor);
        canvas.drawPath(path2, mPaint);

        mPaint.setColor(mTextColor);
        canvas.drawText(this.minY + " ℃", mViewMargin - 25, maxY + 7, mPaint);
        canvas.drawText(this.maxY + " ℃", mViewMargin - 25, minY - 7, mPaint);

        mPaint.setStrokeWidth(13);
        mPaint.setColor(mTextColor);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, mPaint);
        mPaint.setColor(mLineColor);
        mPaint.setStrokeWidth(20);
        canvas.drawPoint(mListPoint.get(0).x, mListPoint.get(0).y, mPaint);
        mPaint.setStrokeWidth(2);
        canvas.drawText("NOW", mListPoint.get(0).x - dpToPx(getContext(), 5), mListPoint.get(0).y + mViewMargin, mPaint);
        mPaint.setColor(mTextColor);
        mPaint.setStrokeWidth(14);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private float dpToPx(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (pxValue * scale + 0.5f);
    }

    private List<Point> getPointList() {
        List<Point> mList = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Point point = new Point();
            point.x = 0;
            point.y = 0;
            mList.add(point);
        }
        return mList;
    }

    public void setmListPoint(List<Point> mListPoint) {
        double minH = Double.MAX_VALUE, maxH = Double.MIN_VALUE, minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE;
        for (Point point : mListPoint) {
            if (point.x > maxX) {
                maxX = point.x;
            }
            if (point.x < minX) {
                minX = point.x;
            }
            if (point.y > maxH) {
                maxH = point.y;
            }
            if (point.y < minH) {
                minH = point.y;
            }
        }
        minY = (int) minH;
        maxY = (int) maxH;
        double w = maxX, h = 3 * (maxH + 1) / 2;
        System.out.println("h: " + h);
        scale_x = (mWidth - 2 * mViewMargin) * 8 / (9 * w);
        scale_y = (mHeight - 2 * mViewMargin) * 10 / (h * 11);
        System.out.println("scale_y = " + scale_y);

        for (Point p : mListPoint) {
            p.x *= scale_x;
            p.x += 2 * mMarginLeft;
            p.y *= scale_y;
            p.y = (mHeight - 2 * mViewMargin) - p.y;
            System.out.println("test" + p.x + " " + p.y);
        }
        this.mListPoint = mListPoint;
    }

    public static class Point {
        public float x;
        public float y;

        public Point() {}

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
