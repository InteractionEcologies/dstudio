package edu.umich.dstudio.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import java.util.Random;

import edu.umich.dstudio.R;
import edu.umich.dstudio.prompt.PromptConfig;

/**
 * Created by neera_000 on 4/2/2016.
 */
public class NotificationUtils {

    /**
     * Given an application context, a package manager for the context and a prompt config, creates
     * and returns a notification object with a pending intent for the intent filter relatd to the
     * config with the correct title and messages.
     * @param context
     * @param pm
     * @param config
     * @return
     */
    public static Notification createNotification(Context context,
                                                  PackageManager pm,
                                                  PromptConfig config) {

        Intent launchIntent = new Intent(context, config.getActivityType());

        // Add extras to intent
        Bundle extras = new Bundle();
        extras.putBoolean("FROM_NOTIFICATION", true);
        extras.putString("PROMPT_TYPE", config.getmPromptType().name());
        Log.d("NotificationUtils: ", config.getmPromptType().name());
        launchIntent.putExtras(extras);

        // Create pending intent from the actual intent and notification object.
        PendingIntent pIntent = PendingIntent.getActivity(context,
                (int) Math.floor(Math.random() * Integer.MAX_VALUE),
                launchIntent, PendingIntent.FLAG_ONE_SHOT);
                //PendingIntent.getActivity(context, 0, launchIntent, 0);
        Notification n  = new Notification.Builder(context)
                .setContentTitle(Constants.REMINDER_NOTIFICATION_TITLE)
                .setContentText(config.getMessage())
                .setSmallIcon(R.drawable.launcher_icon)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .build();
        n.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;

        return n;
    }
}
