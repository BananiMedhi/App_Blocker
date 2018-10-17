package com.appblocker;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by banani on 5/27/2016.
 */
public class CustomAdapter extends BaseAdapter {

    Context context;
    List<PackageInfo> packageList;
    PackageManager packageManager;
    LayoutInflater inflater;
    SharedPreferences sharedPreferences;
    AppDatabase appDatabase;

    CustomAdapter(Context context, List<PackageInfo> packageList,
                  PackageManager packageManager,AppDatabase appDatabase) {

        this.context =context;
        this.packageList =packageList;
        this.packageManager =packageManager;
        this.appDatabase =appDatabase;

        inflater = LayoutInflater.from(context);
        sharedPreferences = context.getSharedPreferences("TickPreferences", Context.MODE_PRIVATE);


    }
    public class ViewHolder {
        TextView appName;
        Button btnTick;
        TextView time;
    }

    @Override
    public int getCount() {
        return packageList.size();
    }

    @Override
    public Object getItem(int position) {
        return packageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        String startTime, endTime;
        if(convertView==null){
            convertView =inflater.inflate(R.layout.listrows,null);
            holder = new ViewHolder();
            holder.appName = (TextView) convertView.findViewById(R.id.textView);
            holder.btnTick = (Button) convertView.findViewById(R.id.btn_tick);
            holder.time = (TextView) convertView.findViewById(R.id.textView2);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        PackageInfo pInfo = (PackageInfo) packageList.get(position);
        String aName = (String) packageManager.getApplicationLabel(pInfo.applicationInfo);
        Drawable appIcon = packageManager
                .getApplicationIcon(pInfo.applicationInfo);
        appIcon.setBounds(0, 0, 40, 40);
        holder.appName.setCompoundDrawables(appIcon, null, null, null);
        holder.appName.setCompoundDrawablePadding(15);
        holder.appName.setText(aName);

        Log.i("pInfo.packageName",pInfo.packageName +" " + sharedPreferences.getBoolean(pInfo.packageName,false));
        if(sharedPreferences.getBoolean(pInfo.packageName,false)) {
           holder.btnTick.setVisibility(View.VISIBLE);
           holder.appName.setTextColor(Color.parseColor("#228B22"));

           long t = Long.parseLong(String.valueOf(appDatabase.retrieveStartTimes(pInfo.packageName)));
           Calendar c = Calendar.getInstance();

           c.setTimeInMillis(t);
           int h = c.get(Calendar.HOUR_OF_DAY);
           int m = c.get(Calendar.MINUTE);
           startTime = getTime(h, m);



           t = Long.parseLong(String.valueOf(appDatabase.retrieveEndTimes(pInfo.packageName)));

           c.setTimeInMillis(t);
           h = c.get(Calendar.HOUR_OF_DAY);
           m = c.get(Calendar.MINUTE);
           endTime = getTime(h,m);



           holder.time.setText(startTime + " - " + endTime );
       }
        else{
            holder.btnTick.setVisibility(View.GONE);
            holder.appName.setTextColor(Color.parseColor("#000000"));
            holder.time.setText("");
        }
        return convertView;
    }

    String getTime(int h, int m){
        String mer;
        if(h==0){
            h=12;
            mer="P.M";
        }else  if(h==12){
            mer="A.M";
        }
        else if(h<12){
            mer = "A.M";
        }
        else{
            h-=12;
            mer ="P.M";
        }

        String res = h +":" + m  + mer;
        return  res;
    }
}
