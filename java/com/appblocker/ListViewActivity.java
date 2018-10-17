package apps.sony.com.appblocker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 5013003471 on 5/27/2016.
 */
public class ListViewActivity extends Activity {

    ListView listView;
    PackageManager packageManager;
    List<PackageInfo> packages;
    List<PackageInfo> packageList;
    CustomAdapter adapter;
    Context context;
    SharedPreferences sPreferences;
    SharedPreferences.Editor editor;
    static String currentPackage ="";
    Button btnclear;
    AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.applist);

        Intent intent = new Intent(getApplicationContext(),MonitorService.class);
        startService(intent);

        listView = (ListView) findViewById(R.id.appList);
        btnclear = (Button) findViewById(R.id.btnclear);

        sPreferences = getSharedPreferences("TickPreferences", MODE_PRIVATE);


        context= this;
        currentPackage =this.getPackageName();



    }
    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
                : false;
    }

    public void openTimeActivity(String packageName){
        Intent intent = new Intent(context,SetTimeActivity.class);
        intent.putExtra("AppName",packageName);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("debug","In onresume Activity");
        packageManager = getPackageManager();

        editor  = sPreferences.edit();
        appDatabase =new AppDatabase(context);
           /*To filter out System apps*/
        packageList = new ArrayList<>();
        packages = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        for(PackageInfo pi : packages) {
            boolean b = isSystemPackage(pi);
            if((!b) && !((pi.packageName).equals(currentPackage))) {
                packageList.add(pi);
                if(sPreferences == null) {
                    editor.putBoolean(pi.packageName, false);
                }
            }
        }

        editor.commit();
        adapter = new CustomAdapter(this,packageList,packageManager,appDatabase);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String packageName = packageList.get(position).packageName;
                openTimeActivity(packageName);
            }
        });
        btnclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("debug", "In clear button");
                SharedPreferences.Editor ed = sPreferences.edit();

                ed.clear();
                ed.apply();
                onResume();

                appDatabase.dropTable();


            }
        });
    }
}
