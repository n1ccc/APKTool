package com.aefyr.sai.utils;

import android.app.Notification;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import androidx.core.app.NotificationManagerCompat;

/**
 * Manages delaying notification to avoid going over notifications per second limit
 */
public class NotificationHelper {
    private static final long NOTIFICATION_CD = 1000 / 5;

    private static NotificationHelper sInstance;

    private NotificationManagerCompat mNotificationManager;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private long mLastNotificationTime = 0;

    public static NotificationHelper getInstance(Context context) {
        synchronized (NotificationHelper.class) {
            return sInstance != null ? sInstance : new NotificationHelper(context);
        }
    }

    private NotificationHelper(Context c) {
        mNotificationManager = NotificationManagerCompat.from(c.getApplicationContext());
        sInstance = this;
    }

    /**
     * Post notification when possible or skip it, if it is skipable
     *
     * @param id           id of the notification
     * @param notification notification to post
     * @param skipable     if notification can be skipped (such as progress notifications)
     */
    public synchronized void notify(int id, Notification notification, boolean skipable) {
        long timeSinceLastNotification = SystemClock.uptimeMillis() - mLastNotificationTime;

        if (timeSinceLastNotification < NOTIFICATION_CD) {
            if (!skipable) {
                mHandler.postAtTime(() -> mNotificationManager.notify(id, notification), mLastNotificationTime + NOTIFICATION_CD);
                mLastNotificationTime = mLastNotificationTime + NOTIFICATION_CD;
            }
            return;
        }

        mLastNotificationTime = SystemClock.uptimeMillis();
        mNotificationManager.notify(id, notification);
    }

}
