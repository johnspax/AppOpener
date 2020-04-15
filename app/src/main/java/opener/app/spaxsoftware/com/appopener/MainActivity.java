package opener.app.spaxsoftware.com.appopener;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import opener.app.spaxsoftware.com.appopener.Adopter.ListViewAdapter;
import opener.app.spaxsoftware.com.appopener.Alarm.Alarm;
import opener.app.spaxsoftware.com.appopener.Model.PackageNames;
import opener.app.spaxsoftware.com.appopener.Util.MyConstants;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    ArrayList<PackageNames> pnArray = new ArrayList<PackageNames>();
    ListView list;
    ListViewAdapter adapter;
    SearchView editsearch;
    TimePicker tp;
    Button btn_Start, btn_Stop;
    private String pName, AppName, time;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!Settings.canDrawOverlays(this)) {
            new AlertDialog.Builder(this)
                    .setTitle("Allow Permission!")
                    .setMessage("You need to allow permission for the app to work properly.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Continue with operation
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                            startActivityForResult(intent, 0);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        // Locate the ListView in listview_main.xml
        list = findViewById(R.id.listview);
        tp = findViewById(R.id.simpleTimePicker);
        btn_Start = findViewById(R.id.btnStart);
        btn_Stop = findViewById(R.id.btnStop);
        editsearch = findViewById(R.id.search);

        pName = "";
        AppName = "";

        // Flags: See below
        int flags = PackageManager.GET_META_DATA |
                PackageManager.GET_SHARED_LIBRARY_FILES |
                PackageManager.GET_UNINSTALLED_PACKAGES;

        PackageManager pm = getPackageManager();
        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(flags);

        for (ApplicationInfo packageInfo : packages) {
            if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 1) {
                PackageNames pn = new PackageNames();
                pn.setApplicationName(pm.getApplicationLabel(packageInfo).toString());
                pn.setPackageName(packageInfo.packageName);
                pn.setSourceDir(packageInfo.sourceDir);
                pnArray.add(pn);
            }
        }

        // Pass results to ListViewAdapter Class
        adapter = new ListViewAdapter(this, pnArray);

        // Binds the Adapter to the ListView
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //editsearch.setQuery(pName, false);
                list.setVisibility(View.GONE);
                pName = pnArray.get(position).getPackageName();
                AppName = pnArray.get(position).getApplicationName();
            }
        });

        btn_Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!pName.equals("")) {
                        time = tp.getHour() + ":" + tp.getMinute();
                        //time = "11:30";

                        SimpleDateFormat stf = new SimpleDateFormat("HH:mm");
                        Date t = stf.parse(time);
                        android.text.format.DateFormat f = new android.text.format.DateFormat();
                        String strRestart = f.format("hh:mm a", t).toString();

                        editor = getSharedPreferences(MyConstants.MY_PREFERENCES, MODE_PRIVATE).edit();
                        editor.putString(MyConstants.SET_SLEEP_TIME, time);
                        editor.putString(MyConstants.APP_NAME, AppName);
                        editor.putString(MyConstants.PACKAGE_NAME, pName);
                        editor.putString(MyConstants.SET_TIME_FORMATTED, strRestart);
                        editor.putBoolean(MyConstants.BOOT_ALARM_SET, true);
                        editor.apply();

                        Alarm alarm = new Alarm();
                        alarm.setAlarm(MainActivity.this, time);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btn_Stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alarm alarm = new Alarm();
                alarm.cancelAlarm(MainActivity.this);
                editor = getSharedPreferences(MyConstants.MY_PREFERENCES, MODE_PRIVATE).edit();
                editor.putBoolean(MyConstants.BOOT_ALARM_SET, false);
                editor.apply();
            }
        });

        // Locate the EditText in listview_main.xml
        editsearch.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        list.setVisibility(View.VISIBLE);
        String text = newText;
        adapter.filter(text);
        return false;
    }
}
