package opener.app.spaxsoftware.com.appopener.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import opener.app.spaxsoftware.com.appopener.Alarm.Alarm;
import opener.app.spaxsoftware.com.appopener.Util.MyConstants;

import static android.content.Context.MODE_PRIVATE;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = context.getSharedPreferences(MyConstants.MY_PREFERENCES, MODE_PRIVATE);
        String time = prefs.getString(MyConstants.SET_SLEEP_TIME, "06:01");
        Alarm alarm = new Alarm();
        alarm.setAlarm(context, time);
    }
}
