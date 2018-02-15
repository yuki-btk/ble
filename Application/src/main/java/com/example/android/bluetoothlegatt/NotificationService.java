package com.example.android.bluetoothlegatt;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

/**
 * Created by yorke on 2018/02/12.
 * from https://qiita.com/araiyusuke/items/f37f88a9da6dc1989945
 */

public class NotificationService extends NotificationListenerService {

    private String TAG = "Notification";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        //通知が更新
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        //通知が削除
    }
}


