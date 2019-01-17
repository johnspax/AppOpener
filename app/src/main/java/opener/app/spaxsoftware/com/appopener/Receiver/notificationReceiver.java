package opener.app.spaxsoftware.com.appopener.Receiver;

import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import opener.app.spaxsoftware.com.appopener.Alarm.Alarm;

import static android.content.Context.NOTIFICATION_SERVICE;

public class notificationReceiver extends BroadcastReceiver {
    Context ctx;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.ctx = context;
        openAppInfo();
    }

    private void openAppInfo(){
        //redirect user to app Settings
        String packageName = "opener.app.spaxsoftware.com.appopener";
        try {
            //Open the specific App Info page:
            /*Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory("android.intent.category.DEFAULT");
            intent.putExtra("Notification Receiver", "Open package " + packageName);
            intent.setClass(ctx, notificationReceiver.class);
            intent.setData(Uri.parse("package:" + packageName));
            ctx.startActivity(intent);*/
            NotificationManager manager = (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
            manager.cancel(1);

        } catch ( ActivityNotFoundException e ) {
            //Open the generic Apps page:
            /*Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
            ctx.startActivity(intent);*/
            NotificationManager manager = (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
            manager.cancel(1);
        }
    }
}
