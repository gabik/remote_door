package net.kazav.gabi.kazavfaces;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.view.Gravity;
import android.view.SurfaceHolder;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by gabik on 5/22/16.
 * Analog Face
 */
public class AnalogFace extends CanvasWatchFaceService {

    @Override
    public Engine onCreateEngine() {
        /* provide your watch face implementati */
        return new Engine();
    }

    /* implement service callback methods */
    private class Engine extends CanvasWatchFaceService.Engine {

        static final int MSG_UPDATE_TIME = 0;

        Calendar mCalendar;

        // device features
        boolean mLowBitAmbient;
        boolean mRegisteredTimeZoneReceiver;
        boolean mBurnInProtection;

        // graphic objects
        Bitmap mBackgroundBitmap;
        Bitmap mBackgroundScaledBitmap;
        Paint mHourPaint;
        Paint mMinutePaint;
        Paint mSecondPaint;
        Paint mYlw;
        Bitmap mTick;

        final Handler mUpdateTimeHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                switch (message.what) {
                    case MSG_UPDATE_TIME:
                        invalidate();
                        if (shouldTimerBeRunning()) {
                            long timeMs = System.currentTimeMillis();
                            long delayMs = 5000 - (timeMs % 5000);
                            mUpdateTimeHandler
                                    .sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
                        }
                        break;
                }
            }
        };

        private boolean shouldTimerBeRunning() {return isVisible() && !isInAmbientMode();}

        final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            }
        };

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            // load the background image
            Resources resources = AnalogFace.this.getResources();
            Drawable backgroundDrawable = resources.getDrawable(R.drawable.analog_face, null);
            mBackgroundBitmap = ((BitmapDrawable) backgroundDrawable).getBitmap();

            // create graphic styles
            mHourPaint = new Paint();
            mHourPaint.setARGB(255, 192, 192, 192);
            mHourPaint.setStrokeWidth(11.0f);
            mHourPaint.setAntiAlias(true);
            mHourPaint.setStrokeCap(Paint.Cap.SQUARE);
            mMinutePaint = new Paint();
            mMinutePaint.setARGB(255, 192, 192, 192);
            mMinutePaint.setStrokeWidth(7.0f);
            mMinutePaint.setAntiAlias(true);
            mMinutePaint.setStrokeCap(Paint.Cap.SQUARE);
            mSecondPaint = new Paint();
            mSecondPaint.setARGB(255, 100, 100, 100);
            mSecondPaint.setStrokeWidth(5.0f);
            mSecondPaint.setAntiAlias(true);
            mSecondPaint.setStrokeCap(Paint.Cap.ROUND);
            mYlw = new Paint();
            mYlw.setARGB(150, 242, 242, 161);
            mYlw.setStrokeWidth(5.0f);
            mYlw.setAntiAlias(true);
            mYlw.setStrokeCap(Paint.Cap.SQUARE);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            mTick = BitmapFactory.decodeResource(getResources(), R.drawable.tick, options);

            mTick.setWidth(10);
            // allocate a Calendar to calculate local time using the UTC time and time zone
            mCalendar = Calendar.getInstance();

            setWatchFaceStyle(new WatchFaceStyle.Builder(AnalogFace.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .setStatusBarGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP)
                    .setHotwordIndicatorGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM)
                    .build());
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            /* get device features (burn-in, low-bit ambient) */
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
            mBurnInProtection = properties.getBoolean(PROPERTY_BURN_IN_PROTECTION, false);

        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            /* the time changed */
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            /* the wearable switched between modes */
            if (mLowBitAmbient) {
                boolean antiAlias = !inAmbientMode;
                mHourPaint.setAntiAlias(antiAlias);
                mMinutePaint.setAntiAlias(antiAlias);
                mSecondPaint.setAntiAlias(antiAlias);
            }
            invalidate();
            updateTimer();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            // Update the time
            mCalendar.setTimeInMillis(System.currentTimeMillis());

            // Constant to help calculate clock hand rotations
            final float TWO_PI = (float) Math.PI * 2f;

            int width = bounds.width();
            int height = bounds.height();

            canvas.drawBitmap(mBackgroundScaledBitmap, 0, 0, null);

            // Find the center. Ignore the window insets so that, on round watches
            // with a "chin", the watch face is centered on the entire screen, not
            // just the usable portion.
            float centerX = width / 2f;
            float centerY = height / 2f;

            // Compute rotations and lengths for the clock hands.
            float seconds = mCalendar.get(Calendar.SECOND) + mCalendar.get(Calendar.MILLISECOND) / 1000f;
            float secRot = seconds / 60f * TWO_PI;
            float minutes = mCalendar.get(Calendar.MINUTE) + seconds / 60f;
            float minRot = minutes / 60f * TWO_PI;
            float hours = mCalendar.get(Calendar.HOUR) + minutes / 60f;
            float hrRot = hours / 12f * TWO_PI;

            float secLength = centerX - 20;
            float minLength = centerX - 60;
            float hrLength = centerX - 100;



            // Draw the minute and hour hands.
            float minX = (float) Math.sin(minRot) * minLength;
            float minY = (float) -Math.cos(minRot) * minLength;
            canvas.drawLine(centerX, centerY, centerX + minX, centerY + minY, mMinutePaint);
            float hrX = (float) Math.sin(hrRot) * hrLength;
            float hrY = (float) -Math.cos(hrRot) * hrLength;
            canvas.drawLine(centerX, centerY, centerX + hrX, centerY + hrY, mHourPaint);

            canvas.drawLine(centerX + minX * 0.75f,
                    centerY + minY * 0.75f,
                    centerX + minX * 0.9f,
                    centerY + minY * 0.9f,
                    mYlw);
            canvas.drawLine(centerX + hrX * 0.75f,
                    centerY + hrY * 0.75f,
                    centerX + hrX * 0.9f,
                    centerY + hrY * 0.9f,
                    mYlw);
            // Only draw the second hand in interactive mode.
            if (!isInAmbientMode()) {
                float secX = (float) Math.sin(secRot) * secLength;
                float secY = (float) -Math.cos(secRot) * secLength;
                canvas.drawLine(centerX, centerY, centerX + secX, centerY + secY, mSecondPaint);
                canvas.drawLine(centerX + secX * 0.75f,
                        centerY + secY * 0.75f,
                        centerX + secX * 0.9f,
                        centerY + secY * 0.9f,
                        mYlw);
            }

        }

        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);}
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                registerReceiver();
            } else {
                unregisterReceiver();
            }
        }

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {return;}
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            AnalogFace.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {return;}
            mRegisteredTimeZoneReceiver = false;
            AnalogFace.this.unregisterReceiver(mTimeZoneReceiver);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (mBackgroundScaledBitmap == null || mBackgroundScaledBitmap.getWidth() != width || mBackgroundScaledBitmap.getHeight() != height) {
                mBackgroundScaledBitmap = Bitmap.createScaledBitmap(mBackgroundBitmap, width, height, true);
            }
            super.onSurfaceChanged(holder, format, width, height);
        }
    }
}
