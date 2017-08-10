package com.example.zxw.myclock;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.Calendar;

/**
 * 自定义时钟View
 */
public class ClockView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private static final String TAG = ClockView.class.getSimpleName();
    // 默认半径
    private static final float DEFAULT_RADIUS = 349.5f;

    private SurfaceHolder mHolder;
    private Canvas mCanvas;
    private boolean flag;

    private OnTimeChangeListener onTimeChangeListener;

    public void setOnTimeChangeListener(OnTimeChangeListener onTimeChangeListener) {
        this.onTimeChangeListener = onTimeChangeListener;
    }

    // 圆和刻度的画笔
    private Paint mPaint;

    // 画布的宽高
    private int mCanvasWidth, mCanvasHeight;
    // 时钟半径
    private float mRadius = DEFAULT_RADIUS;

    // 时钟显示的时、分、秒
    private int mHour, mMinute, mSecond;

    public ClockView(Context context) {
        this(context, null);
    }

    public ClockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        mMinute = Calendar.getInstance().get(Calendar.MINUTE);
        mSecond = Calendar.getInstance().get(Calendar.SECOND);

        mHolder = getHolder();
        mHolder.addCallback(this);

        mPaint = new Paint();

        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);

        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int desiredWidth, desiredHeight;
        if (widthMode == MeasureSpec.EXACTLY) {
            desiredWidth = widthSize;
        } else {
            desiredWidth = (int) (mRadius * 2 + getPaddingLeft() + getPaddingRight());
            if (widthMode == MeasureSpec.AT_MOST) {
                desiredWidth = Math.min(widthSize, desiredWidth);
            }
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            desiredHeight = heightSize;
        } else {
            desiredHeight = (int) (mRadius * 2 + getPaddingTop() + getPaddingBottom());
            if (heightMode == MeasureSpec.AT_MOST) {
                desiredHeight = Math.min(heightSize, desiredHeight);
            }
        }

        setMeasuredDimension(mCanvasWidth = desiredWidth, mCanvasHeight = desiredHeight);

        mRadius = (int) (Math.min(desiredWidth - getPaddingLeft() - getPaddingRight(),
                desiredHeight - getPaddingTop() - getPaddingBottom()) * 1.0f / 2);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("TAG", "surfaceCreated");
        flag = true;
        mHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        mMinute = Calendar.getInstance().get(Calendar.MINUTE);
        mSecond = Calendar.getInstance().get(Calendar.SECOND);
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d("TAG", "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("TAG", "surfaceDestroyed");
        flag = false;
    }

    @Override
    public void run() {
        long start, end;
        while (flag) {
            Log.d("TAG", "run--");
            start = System.currentTimeMillis();
            draw();
            logic();
            end = System.currentTimeMillis();

            try {
                if (end - start < 1000) {
                    Thread.sleep(1000 - (end - start));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (onTimeChangeListener != null) {
                onTimeChangeListener.onTimeChange(ClockView.this, mHour, mMinute, mSecond);
            }
            return false;
        }
    });

    /**
     * 逻辑
     */
    private void logic() {
        mSecond++;
        if (mSecond == 60) {
            mSecond = 0;
            mMinute++;
            if (mMinute == 60) {
                mMinute = 0;
                mHour++;
                if (mHour == 24) {
                    mHour = 0;
                }
            }
        }

        handler.sendEmptyMessage(0);
    }

    /**
     * 绘制
     */
    private void draw() {
        try {
            mCanvas = mHolder.lockCanvas();
            //给Canvas加上抗锯齿标志
            mCanvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
            if (mCanvas != null) {
                //刷屏
                mCanvas.drawColor(Color.BLACK);
                //将坐标系原点移至去除内边距后的画布中心
                mCanvas.translate(mCanvasWidth * 1.0f / 2 + getPaddingLeft() - getPaddingRight(),
                        mCanvasHeight * 1.0f / 2 + getPaddingTop() - getPaddingBottom());
                //绘制圆盘
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.biaopan);
                mCanvas.drawBitmap(bitmap,-349.5f,-349.5f,mPaint);
                mCanvas.rotate(180);
                //绘制时针
                mCanvas.save();
                Bitmap shizhen = BitmapFactory.decodeResource(getResources(),R.drawable.biaopan_shizhen);
                mCanvas.rotate(180 + mHour % 12 * 30 + mMinute * 1.0f / 60 * 30);
                mCanvas.drawBitmap(shizhen,-349.5f,-349.5f,mPaint);
                mCanvas.restore();
                //绘制分针
                mCanvas.save();
                Bitmap fenzhen = BitmapFactory.decodeResource(getResources(),R.drawable.biaopan_fenzhen);
                mCanvas.rotate(180 + mMinute * 6);
                mCanvas.drawBitmap(fenzhen,-349.5f,-349.5f,mPaint);
                mCanvas.restore();
                //绘制秒针
                mCanvas.save();
                Bitmap miaozhen = BitmapFactory.decodeResource(getResources(),R.drawable.biaopan_miaozhen);
                mCanvas.rotate(180 + mSecond * 6);
                mCanvas.drawBitmap(miaozhen,-349.5f,-349.5f,mPaint);
                mCanvas.restore();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCanvas != null) {
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

    //-----------------Setter and Getter start-----------------//
    public int getHour() {
        return mHour;
    }

    public void setHour(int hour) {
        mHour = Math.abs(hour) % 24;
        if (onTimeChangeListener != null) {
            onTimeChangeListener.onTimeChange(this, mHour, mMinute, mSecond);
        }
    }

    public int getMinute() {
        return mMinute;
    }

    public void setMinute(int minute) {
        mMinute = Math.abs(minute) % 60;
        if (onTimeChangeListener != null) {
            onTimeChangeListener.onTimeChange(this, mHour, mMinute, mSecond);
        }
    }

    public int getSecond() {
        return mSecond;
    }

    public void setSecond(int second) {
        mSecond = Math.abs(second) % 60;
        if (onTimeChangeListener != null) {
            onTimeChangeListener.onTimeChange(this, mHour, mMinute, mSecond);
        }
    }

    public void setTime(Integer... time) {
        if (time.length > 3) {
            throw new IllegalArgumentException("the length of argument should bo less than 3");
        }
        if (time.length > 2)
            setSecond(time[2]);
        if (time.length > 1)
            setMinute(time[1]);
        if (time.length > 0)
            setHour(time[0]);
    }
    //-----------------Setter and Getter end-------------------//

    /**
     * 当时间改变的时候提供回调的接口
     */
    public interface OnTimeChangeListener {
        /**
         * 时间发生改变时调用
         *
         * @param view   时间正在改变的view
         * @param hour   改变后的小时时刻
         * @param minute 改变后的分钟时刻
         * @param second 改变后的秒时刻
         */
        void onTimeChange(View view, int hour, int minute, int second);
    }
}
