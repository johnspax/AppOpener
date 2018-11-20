package opener.app.spaxsoftware.com.appopener.Receiver;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

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
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + packageName));
            ctx.startActivity(intent);

        } catch ( ActivityNotFoundException e ) {
            //Open the generic Apps page:
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
            ctx.startActivity(intent);

        }
    }
}
