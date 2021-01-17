package com.barlificent.ratify1.CustomClasses;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;

import com.barlificent.ratify1.R;

/**
 * Created by 2015 on 1/8/2018.
 */
public class RateBar extends View {
    private final Paint paint;
    private final Paint textPaint;
    private final float textHeight;
    private final Path path;
    private boolean active;
    private float progressFraction;
    private float targetProgressFraction;
    private ObjectAnimator progressAnimator;
    private int width;
    private int height;
    private int radius;
    private int padding;
    private int widthMinusPadding;
    private int count;
    private int max;
    @ColorInt
    private int activeColor;
    @ColorInt
    private int inactiveColor;
    @ColorInt
    private int activeTextColor;
    @ColorInt
    private int inactiveTextColor;
    RateBar.OnRateBarChangeListener listener;

    public RateBar(Context context) {
        this(context, (AttributeSet)null);
    }

    public RateBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RateBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, com.trafi.ratingseekbar.R.styleable.rsb_RateBar, defStyleAttr, 0);

        float textSize;
        try {
            this.max = 10;
            this.activeColor = Color.RED;
            this.activeTextColor = Color.WHITE;
            this.inactiveColor = Color.WHITE;
            this.inactiveTextColor = Color.BLACK;
            textSize = 48;
        } finally {
//            a.recycle();
        }

        this.paint = new Paint(1);
        this.textPaint = new Paint(1);
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        this.textPaint.setTextSize(textSize);
        this.textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        Rect bounds = new Rect();
        this.textPaint.getTextBounds("1", 0, 1, bounds);
        this.textHeight = (float)bounds.height();
        this.path = new Path();
        this.setMax(this.max);
        this.targetProgressFraction = 0.0F;
        this.progressAnimator = ObjectAnimator.ofFloat(this, "progressFraction", new float[]{0.0F, 0.0F});
        this.progressAnimator.setInterpolator(new DecelerateInterpolator());
        this.progressAnimator.setDuration(100L);
        this.active = false;
        if(ViewCompat.getImportantForAccessibility(this) == ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_AUTO) {
            ViewCompat.setImportantForAccessibility(this, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES);
        }

    }

    public void setOnSeekBarChangeListener(RateBar.OnRateBarChangeListener listener) {
        this.listener = listener;
    }

    private int getProgress() {
        return Math.max(0, Math.min((int)(this.targetProgressFraction * (float)this.count), this.max));
    }

    public boolean setProgress(int progress) {
        int prevProgress = this.getProgress();
        if(progress != prevProgress) {
            this.setTargetProgressFraction(((float)progress + 0.5F) / (float)this.count, false);
            return true;
        } else {
            return false;
        }
    }

    float getProgressFraction() {
        return this.progressFraction;
    }

    void setProgressFraction(float progressFraction) {
        this.progressFraction = Math.max(0.0F, Math.min(progressFraction, 1.0F));
        this.invalidate();
    }

    private void setTargetProgressFraction(float newTargetProgressFraction, boolean animate) {
        newTargetProgressFraction = Math.max(0.0F, Math.min(newTargetProgressFraction, 1.0F));
        if(!this.active || newTargetProgressFraction != this.targetProgressFraction) {
            int prevProgress = this.getProgress();
            this.targetProgressFraction = newTargetProgressFraction;
            int newProgress = this.getProgress();
            if(animate) {
                this.progressAnimator.setFloatValues(new float[]{this.progressFraction, this.targetProgressFraction});
                this.progressAnimator.start();
            } else {
                this.setProgressFraction(this.targetProgressFraction);
            }

            if(null != this.listener && (!this.active || newProgress != prevProgress)) {
                this.listener.onProgressChanged(this, newProgress);
            }
        }

        this.active = true;
    }

    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredHeight = this.getResources().getDimensionPixelSize(com.trafi.ratingseekbar.R.dimen.rsb_default_height);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        switch(heightMode) {
            case -2147483648:
                desiredHeight = Math.min(desiredHeight, heightSize);
            case 0:
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(desiredHeight, MeasureSpec.EXACTLY);
            case 1073741824:
            default:
                this.setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        }
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        this.height = h;
        this.radius = this.height / 2;
        this.padding = (int)((float)(this.count * this.height - this.width) / (float)(2 * (this.count - 1)));
        this.widthMinusPadding = this.width - 2 * this.padding;
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getActionMasked()) {
            case 0:
                this.setTargetProgressFraction((event.getX() - (float)this.padding) / (float)this.widthMinusPadding, true);
                return true;
            case 1:
            case 3:
                this.setTargetProgressFraction(((float)this.getProgress() + 0.5F) / (float)this.count, true);
                return true;
            case 2:
                this.setTargetProgressFraction((event.getX() - (float)this.padding) / (float)this.widthMinusPadding, false);
                return true;
            default:
                return false;
        }
    }

    public int getMax() {
        return this.max;
    }

    public void setMax(int max) {
        this.max = max;
        this.count = max + 1;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.path.rewind();
        this.path.addCircle((float)this.radius, (float)this.radius, (float)this.radius, Path.Direction.CCW);
        this.path.addRect((float)this.radius, 0.0F, (float)(this.width - this.radius), (float)this.height, Path.Direction.CCW);
        this.path.addCircle((float)(this.width - this.radius), (float)this.radius, (float)this.radius, Path.Direction.CCW);
        this.paint.setColor(this.inactiveColor);
        canvas.drawPath(this.path, this.paint);
        float progressX = Math.max((float)this.radius, Math.min((float)this.padding + this.progressFraction * (float)this.widthMinusPadding, (float)(this.width - this.radius)));
        if(this.active) {
            this.path.rewind();
            this.path.addCircle((float)this.radius, (float)this.radius, (float)this.radius, Path.Direction.CCW);
            this.path.addRect((float)this.radius, 0.0F, progressX, (float)this.height, Path.Direction.CCW);
            this.path.addCircle(progressX, (float)this.radius, (float)this.radius, Path.Direction.CCW);
            this.paint.setColor(this.activeColor);
            canvas.drawPath(this.path, this.paint);
        }

        this.textPaint.setColor(this.inactiveTextColor);
        canvas.save();
        if(this.active) {
            if(Build.VERSION.SDK_INT >= 18) {
                canvas.clipPath(this.path, Region.Op.DIFFERENCE);
            } else {
                canvas.clipRect(progressX + (float)this.radius, 0.0F, (float)this.width, (float)this.height, Region.Op.INTERSECT);
            }
        }

        this.drawLabels(canvas, this.count);
        canvas.restore();
        if(this.active) {
            this.textPaint.setColor(this.activeTextColor);
            canvas.save();
            if(Build.VERSION.SDK_INT >= 18) {
                canvas.clipPath(this.path, Region.Op.INTERSECT);
            } else {
                canvas.clipRect(0.0F, 0.0F, progressX + (float)this.radius, (float)this.height, Region.Op.INTERSECT);
            }

            this.drawLabels(canvas, this.count);
            canvas.restore();
        }

    }

    private void drawLabels(Canvas canvas, int count) {
        float stepWidth = (float)(this.widthMinusPadding / count);

        for(int i = 1; i < count; ++i) {
            canvas.drawText(Integer.toString(i), (float)this.padding + (float)i * stepWidth + stepWidth / 2.0F, (float)(this.height / 2) + this.textHeight / 2.0F, this.textPaint);
        }

    }

    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        RateBar.SavedState ss = new RateBar.SavedState(superState);
        ss.targetProgressFraction = this.targetProgressFraction;
        return ss;
    }

    protected void onRestoreInstanceState(Parcelable state) {
        RateBar.SavedState ss = (RateBar.SavedState)state;
        super.onRestoreInstanceState(ss.getSuperState());
        this.setTargetProgressFraction(ss.targetProgressFraction, false);
    }

    public CharSequence getAccessibilityClassName() {
        return RateBar.class.getName();
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setItemCount(this.max);
        event.setCurrentItemIndex(this.getProgress());
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        if(this.isEnabled()) {
            int progress = this.getProgress();
            if(progress > 0) {
                info.addAction(8192);
            }

            if(progress < this.getMax()) {
                info.addAction(4096);
            }
        }

    }

    public boolean performAccessibilityAction(int action, Bundle arguments) {
        if(super.performAccessibilityAction(action, arguments)) {
            return true;
        } else if(!this.isEnabled()) {
            return false;
        } else {
            switch(action) {
                case 4096:
                case 8192:
                    int increment1 = Math.max(1, Math.round((float)this.getMax() / 20.0F));
                    if(action == 8192) {
                        increment1 = -increment1;
                    }

                    return this.setProgress(this.getProgress() + increment1);
                case 16908349:
                    if(arguments != null && arguments.containsKey("android.view.accessibility.action.ARGUMENT_PROGRESS_VALUE")) {
                        float increment = arguments.getFloat("android.view.accessibility.action.ARGUMENT_PROGRESS_VALUE");
                        return this.setProgress((int)increment);
                    }

                    return false;
                default:
                    return false;
            }
        }
    }

    static class SavedState extends BaseSavedState {
        float targetProgressFraction;
        public static final Creator<RateBar.SavedState> CREATOR = new Creator() {
            public RateBar.SavedState createFromParcel(Parcel in) {
                return new RateBar.SavedState(in);
            }

            public RateBar.SavedState[] newArray(int size) {
                return new RateBar.SavedState[size];
            }
        };

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.targetProgressFraction = in.readFloat();
        }

        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeFloat(this.targetProgressFraction);
        }
    }

    public interface OnRateBarChangeListener {
        void onProgressChanged(RateBar var1, int var2);
    }
}
