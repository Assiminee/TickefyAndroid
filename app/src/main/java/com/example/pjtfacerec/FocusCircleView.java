package com.example.pjtfacerec;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

public class FocusCircleView extends View {

    private final Paint paint;
    private RectF focusCircle;
    private final Handler handler;
    private Runnable removeFocusRunnable;

    public FocusCircleView(Context context) {
        this(context, null);
    }

    public FocusCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // init fields
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5f);

        handler = new Handler(Looper.getMainLooper());
        // no-op placeholder; will be replaced on first draw
        removeFocusRunnable = new Runnable() {
            @Override
            public void run() { }
        };
    }

    /**
     * Call this to set the focus circle's bounds and trigger a redraw.
     */
    public void setFocusCircle(RectF rect) {
        focusCircle = rect;
        invalidate();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (focusCircle != null) {
            // Calculate the outer circle radius
            float outerRadius = focusCircle.width() / 1.2f;
            // Calculate the inner circle radius
            float innerRadius = outerRadius / 2f;
            float cx = focusCircle.centerX();
            float cy = focusCircle.centerY();

            // Draw the outer circle
            canvas.drawCircle(cx, cy, outerRadius, paint);
            // Draw the inner circle
            canvas.drawCircle(cx, cy, innerRadius, paint);

            scheduleFocusCircleRemoval();
        }
    }

    private void scheduleFocusCircleRemoval() {
        // Remove any pending removal
        handler.removeCallbacks(removeFocusRunnable);

        // Create a new Runnable that clears the circle and invalidates
        removeFocusRunnable = new Runnable() {
            @Override
            public void run() {
                focusCircle = null;
                invalidate();
            }
        };
        // Post it with a 2 second delay
        handler.postDelayed(removeFocusRunnable, 2_000);
    }
}
