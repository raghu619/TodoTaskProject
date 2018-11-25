package com.example.android.todo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class NotifyService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        Uri sound=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager mNM=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent intent=new Intent(this.getApplicationContext(),TaskDetailActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,0);
        Notification mNotify=new Notification.Builder(this)
                .setContentTitle("Testing")
                .setContentText("Testing Big")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .setSound(sound)
                .build();

        mNM.notify(1,mNotify);

    }
}
