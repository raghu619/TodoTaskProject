package com.example.android.todo.reminderservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.android.todo.MainActivity;

public class AlarmReceiver extends BroadcastReceiver {
    String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {


        if (intent.getAction() != null && context != null) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {


                LocalData localData = new LocalData(context);
                NotificationScheduler.setReminder(context, AlarmReceiver.class, localData.get_hour(), localData.get_min());
                return;
            }
        }



        LocalData localData = new LocalData(context);
        NotificationScheduler.showNotification(context, MainActivity.class,
                localData.getTask(), "Task Reminder");

    }
}
