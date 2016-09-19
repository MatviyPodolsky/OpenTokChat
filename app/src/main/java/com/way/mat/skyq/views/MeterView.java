package com.way.mat.skyq.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MeterView extends View {

    final private Context mContext;

    private float mValue = 0;
    final private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    final private Paint mPaintGradient = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Rect mBounds = new Rect();
    private Bitmap mIconOn;
    private Bitmap mIconOff;
    private boolean mMute = false;
    private OnClickListener mListener;

    public interface OnClickListener {
        void onClick(MeterView view);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (!mMute) {
            if (mIconOn != null) {
                canvas.drawBitmap(mIconOn,
                        mBounds.centerX() - mIconOn.getWidth() * 0.5f,
                        mBounds.centerY() - mIconOn.getHeight() * 0.5f, mPaint);
            }
            canvas.drawCircle(mBounds.centerX(), mBounds.centerY(),
                    mBounds.width() * 0.5f * mValue, mPaintGradient);

        } else {
            if (mIconOff != null) {
                canvas.drawBitmap(mIconOff,
                        mBounds.centerX() - mIconOff.getWidth() * 0.5f,
                        mBounds.centerY() - mIconOff.getHeight() * 0.5f, mPaint);
            }
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBounds.left = (int) (0 + w * 0.10);
        mBounds.top = (int) (0 + h * 0.10);
        mBounds.right = (int) (w * 0.90);
        mBounds.bottom = (int) (h * 0.90);

        final int width = getWidth();
        final int height = getHeight();
        if (width > 0 && height > 0 && mIconOff != null && mIconOn != null) {
            mIconOn = Bitmap.createScaledBitmap(mIconOn, width, height, true);
            mIconOff = Bitmap.createScaledBitmap(mIconOff, width, height, true);
        }

        // Update gradient
        mPaintGradient.setShader(new RadialGradient(w / 2, h / 2, h / 2,
                0xff98CE00, 0x8098CE00, TileMode.CLAMP));

    }

    private void init() {
        mPaint.setStyle(Style.FILL);
        mPaint.setColor(0xff1f1f1f);
        mPaintGradient.setStyle(Style.FILL);
        mPaintGradient.setColor(0xff98CE00);
    }

    public MeterView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public MeterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public void setIcons(Bitmap iconOn, Bitmap iconOff) {
        final int width = getWidth();
        final int height = getHeight();
        if (width > 0 && height > 0) {
            mIconOn = Bitmap.createScaledBitmap(iconOn, width, height, true);
            mIconOff = Bitmap.createScaledBitmap(iconOff, width, height, true);
        } else {
            mIconOn = iconOn;
            mIconOff = iconOff;
        }
    }

    public void setMeterValue(float value) {
        // Convert linear value to logarithmic
        double db = 20 * Math.log10(value);
        float floor = -40;
        float level = 0;
        if (db > floor) {
            level = (float) db - floor;
            level /= -floor;
        }
        mValue = level;
        // force redraw
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case (MotionEvent.ACTION_DOWN):
                mMute = !mMute;
                if (mListener != null) {
                    mListener.onClick(this);
                }
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    public boolean isMuted() {
        return mMute;
    }

    public void setOnClickListener(OnClickListener listener) {
        mListener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, heightMeasureSpec);
    }

    public void setIsMute(boolean isMute) {
        mMute = isMute;
        invalidate();
    }

    public void clear() {
        setIsMute(false);
        mValue = 0;
        invalidate();
    }
}
