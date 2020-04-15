package opener.app.spaxsoftware.com.appopener.Alarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import opener.app.spaxsoftware.com.appopener.R;
import opener.app.spaxsoftware.com.appopener.Receiver.notificationReceiver;
import opener.app.spaxsoftware.com.appopener.Util.MyConstants;

import static android.app.Notification.PRIORITY_HIGH;
import static android.content.Context.KEYGUARD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class Alarm extends BroadcastReceiver {
    String time, packageName, appName, strMsg;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    PowerManager.WakeLock wl;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    int pid = 1, pIntentId = 2;
    Context ctx;
    long intSleep = 300000, restartMillis = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            this.ctx = context;

            // Code to be run after every X time.
            prefs = context.getSharedPreferences(MyConstants.MY_PREFERENCES, MODE_PRIVATE);
            time = prefs.getString(MyConstants.SET_SLEEP_TIME, "06:01");
            packageName = prefs.getString(MyConstants.PACKAGE_NAME, "");
            appName = prefs.getString(MyConstants.APP_NAME, "");
            SimpleDateFormat stf = new SimpleDateFormat("HH:mm");
            Date t = stf.parse(time);
            String rt = stf.format(t);
            SimpleDateFormat dfmt = new SimpleDateFormat("yyyy-MM-dd");
            String dt = dfmt.format(new Date()) + " " + rt;
            restartMillis = timeInMillis(dt);//time when alarm rang today in milliseconds, System will sleep from this time to next day same time.

            //Set Sleep time in shared preferences
            intSleep = getMilliseconds(time);
            editor = context.getSharedPreferences(MyConstants.MY_PREFERENCES, MODE_PRIVATE).edit();
            editor.putLong(MyConstants.INT_SLEEP, intSleep);
            editor.apply();

            String seTime = prefs.getString(MyConstants.SET_TIME_FORMATTED, "06:01 AM");
            strMsg = appName + " app will be opened at exactly " + seTime + " in " + getHours(intSleep) + " time.";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                NotifyO("App Opener", strMsg, context);
            else
                Notify("App Opener", strMsg, context);

            /*if (currentTime.equals(seTime)) {*/
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            if (launchIntent != null) {
                context.startActivity(launchIntent);//null pointer check in case package name was not found
            }
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent("opener.app.spaxsoftware.com.appopener.START_ALARM");
            i.addCategory("android.intent.category.DEFAULT");
            i.putExtra("Next Alarm", "Set Time " + seTime);
            i.setClass(context, Alarm.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
            //am.setRepeating(AlarmManager.RTC_WAKEUP, restartMillis + intSleep, intSleep, pi); // Millisec * Second * Minute
            am.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + intSleep , pi);
            //}

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAlarm(Context context, String time) {
        try {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            //wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
            wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK  | PowerManager.ACQUIRE_CAUSES_WAKEUP, "myWakeLock");
            wl.acquire();

            intSleep = getMilliseconds(time);
            String hrs = getHours(intSleep);

            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent("opener.app.spaxsoftware.com.appopener.START_ALARM");
            i.addCategory("android.intent.category.DEFAULT");
            i.putExtra("Alarm", "Sleep hours " + hrs);
            i.setClass(context, Alarm.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
            //am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + intSleep, intSleep, pi); // Millisec * Second * Minute
            am.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + intSleep , pi);

            SharedPreferences p = context.getSharedPreferences(MyConstants.MY_PREFERENCES, MODE_PRIVATE);
            strMsg = p.getString(MyConstants.APP_NAME, "App") + " app will be opened at exactly " +
                    p.getString(MyConstants.SET_TIME_FORMATTED, "06:01 AM") + " in " + hrs + " time.";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                NotifyO("App Opener", strMsg, context);
            else
                Notify("App Opener", strMsg, context);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void cancelAlarm(Context context) {
        try {
            //Intent intent = new Intent(context, Alarm.class);
            Intent i = new Intent("opener.app.spaxsoftware.com.appopener.START_ALARM");
            i.addCategory("android.intent.category.DEFAULT");
            i.setClass(context, Alarm.class);
            PendingIntent sender = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(sender);
            //Notify("App Opener", "App restart has been cancelled!", context);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                NotifyO("App Opener", "App restart has been cancelled!", context);
            else
                Notify("App Opener", "App restart has been cancelled!", context);
            //wl.release();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private int getMilliseconds(String time) {
        int diff = 300, setDiff;
        long timeDiff = 0, setDateInMillis, currentDateInMillis, nextMidnightInMillis, thisMidnightInMillis, midNightDiff;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            String dt = sdf.format(new Date()) + " " + time;
            time = dt;
            setDateInMillis = timeInMillis(time);
            Calendar currentDate = Calendar.getInstance();
            currentDateInMillis = currentDate.getTimeInMillis();
            timeDiff = (setDateInMillis - currentDateInMillis);
            diff = (int) timeDiff;//between now and the set time in ms
            if (isNegativity(diff)) {
                nextMidnightInMillis = timeInMillis(sdf.format(new Date()) + " 24:00");
                thisMidnightInMillis = timeInMillis(sdf.format(new Date()) + " 00:00");
                midNightDiff = nextMidnightInMillis - currentDateInMillis;
                setDiff = (int) (setDateInMillis - thisMidnightInMillis);
                diff = (int) (midNightDiff + setDiff);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return diff;
    }

    private long timeInMillis(String dateTime) {
        long lTime = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date dt = sdf.parse(dateTime);
            Calendar calDate = Calendar.getInstance();
            calDate.setTime(dt);
            lTime = calDate.getTimeInMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lTime;
    }

    private boolean isNegativity(int number) {
        boolean rslt = true;
        if (number >= 0)
            rslt = false;
        return rslt;
    }

    private String getHours(long milliSeconds) {
        String hrs, mins, secs, strMsg = "0 Hrs";
        try {
            BigDecimal ms = new BigDecimal(milliSeconds);
            BigDecimal toHrs = new BigDecimal(1000 * 60 * 60);
            BigDecimal toMins = new BigDecimal(1000 * 60);
            BigDecimal toSecs = new BigDecimal(1000);
            DecimalFormat formatter = new DecimalFormat("0");//# means if 0 it will be ignored
            hrs = formatter.format(ms.divide(toHrs, 100, RoundingMode.HALF_UP)).replace(".", "");
            mins = formatter.format(ms.divide(toMins, 100, RoundingMode.HALF_UP)).replace(".", "");
            secs = formatter.format(ms.divide(toSecs, 100, RoundingMode.HALF_UP)).replace(".", "");
            if (hrs.equals("0")) {
                if (mins.equals("0"))
                    strMsg = secs + " Second(s)";
                else
                    strMsg = mins + " Minute(s)";
            } else
                strMsg = hrs + " Hour(s)";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strMsg;
    }

    public void Notify(String Title, String strMsg, Context ctx) {
        mNotifyManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(ctx);
        mBuilder.setContentTitle(Title)
                .setContentText(strMsg)
                .setContentInfo("Info")
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setOnlyAlertOnce(false)
                .setOngoing(false)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher_round);
        //This is the intent of PendingIntent
        Intent intentAction = new Intent("opener.app.spaxsoftware.com.appopener.string.close");
        intentAction.addCategory("android.intent.category.DEFAULT");
        intentAction.putExtra("Notification", "Set Notification");
        intentAction.setClass(ctx, Alarm.class);
        intentAction.setClass(ctx, notificationReceiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(ctx, pIntentId, intentAction, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentText(strMsg);
        mBuilder.setContentIntent(pIntent);
        mBuilder.addAction(R.drawable.ic_close_green_24dp, "Close", pIntent);
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(strMsg));
        mBuilder.setPriority(PRIORITY_HIGH);
        mNotifyManager.notify(pid, mBuilder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void NotifyO(String Title, String strMsg, Context ctx) {
        // Sets an ID for the notification, so it can be updated.
        int notifyID = 1;
        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        CharSequence name = "Test";// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
        // Create a notification and set the notification channel.
        Intent intentAction = new Intent("opener.app.spaxsoftware.com.appopener.string.close");
        intentAction.addCategory("android.intent.category.DEFAULT");
        intentAction.putExtra("Notification", "Set Notification");
        intentAction.setClass(ctx, Alarm.class);
        intentAction.setClass(ctx, notificationReceiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(ctx, pIntentId, intentAction, PendingIntent.FLAG_UPDATE_CURRENT);

        //Notification.Action act = new Notification.Action(R.drawable.ic_close_green_24dp, "Close", pIntent);

        Notification.Action action = new Notification.Action.Builder(
                Icon.createWithResource(ctx, R.drawable.ic_close_green_24dp),
                "Close",
                pIntent).build();

        Notification notification = new Notification.Builder(ctx)
                .setContentTitle(Title)
                .setContentText(strMsg)
                .setContentInfo("Info")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setChannelId(CHANNEL_ID)
                .addAction(action)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setOnlyAlertOnce(false)
                .setOngoing(false)
                .setStyle(new Notification.BigTextStyle().bigText(strMsg))
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .build();

        NotificationManager mNotificationManager =
                (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.createNotificationChannel(mChannel);

        // Issue the notification.
        mNotificationManager.notify(pid, notification);
    }
}
