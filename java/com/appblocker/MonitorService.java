package com.appblocker;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by banani on 5/27/2016.
 */

public class MonitorService extends Service {
    private Handler handler;
    Runnable runnable;
    AlarmManager alarmManager;
    Context context;
    AppDatabase appDatabase;
    List<String> blockApps;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
     //Do nothing
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;
        appDatabase =new AppDatabase(context);
        blockApps = new ArrayList<>();
        Log.i("debug", "in onCreateService");


        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {


                new Thread(new Runnable() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void run() {


                        blockApps= appDatabase.retrieveAppNames();

                        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

                        final Set<String> activePackages = new HashSet();
                        List<ActivityManager.RunningAppProcessInfo> list = activityManager.getRunningAppProcesses();
                        for (ActivityManager.RunningAppProcessInfo processInfo : list) {
                            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                                activePackages.addAll(Arrays.asList(processInfo.pkgList));
                            }
                        }

                        for(String a : activePackages.toArray(new String[activePackages.size()]))
                        {
                            //Log.i("debug",a);
                            if(blockApps.contains(a))
                          {
                              Log.i("debug","App in foreground " + a );
                              long time= System.currentTimeMillis();
                             // Log.i("Times",time +"..." + (preferences.getLong("start",0))+ ",,,"+ (preferences.getLong("end",0)));
                              if(((appDatabase.retrieveStartTimes(a)) < time) && ((appDatabase.retrieveEndTimes(a))>time))
                              {
                                Log.i("debug","Blocking application" + a );
                                Intent intent = new Intent(getApplicationContext(),BlockingActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                              }

                              /*alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                              Intent in = new Intent(context, BlockingActivity.class);
                              in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                              PendingIntent alarmIntent = PendingIntent.getActivity(context, PendingIntent.FLAG_UPDATE_CURRENT, in, 0);
                              alarmManager.set(AlarmManager.RTC_WAKEUP,preferences.getLong("start",0), alarmIntent);*/

                            }
                        }


                    }
                }).start();
             handler.postDelayed(this,3000);
            }
        };

      handler.postDelayed(runnable,3000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}
