package com.appblocker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by banani on 5/31/2016.
 */

public class AppDatabase {

    public static AppOpenHelper appOpenHelper;
    static  SQLiteDatabase db;

    AppDatabase(Context context){
        if(appOpenHelper==null) {
            appOpenHelper = new AppOpenHelper(context, "BlockingApps", null, 1);
        }
    }

   public static class AppOpenHelper extends SQLiteOpenHelper {
        Context context;
        String name;
        int version;
        SQLiteDatabase.CursorFactory factory;


        public AppOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
            this.context = context;
            this.name = name;
            this.version = version;
            this.factory = factory;

        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL("CREATE TABLE IF NOT EXISTS BlockApps( appName Text, startTime Integer, endTime Integer);");
            Log.i("debug create table", "created");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            this.onCreate(db);
        }

       @Override
       public void onOpen(SQLiteDatabase db) {
           super.onOpen(db);
           Log.i("debug","databse is opened");
       }

       @Override
       public synchronized void close()
       {
           super.close();
           if(db != null)
               db.close();
           Log.i("debug","databse is closed");

       }

   }
    public boolean insertApps(String appName,long start, long end) {

        ContentValues cv = new ContentValues();
        db =appOpenHelper.getWritableDatabase();
        db.beginTransaction();

            try  {
                    cv.put("appName", appName);
                    cv.put("startTime", start);
                    cv.put("endTime", end);
                    db.insertOrThrow("BlockApps", null, cv);
                    Log.i("appName", appName);
                    Log.i("startTime", start+"");
                    Log.i("endTime", end+ "");
                    return true;

            } catch (Exception e) {
                Log.e("insertDetails err : ", "" + e);
                return false;
            }
            finally {
//                if(db!=null)
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    //db.close();

        }
    }
    public boolean updateApps(String appName,long start, long end) {

        ContentValues cv = new ContentValues();
        db =appOpenHelper.getWritableDatabase();
        db.beginTransaction();

        try {
                cv.put("startTime", start);
                cv.put("endTime", end);
                Log.i("debug", "Row Updated");
                db.update("BlockApps", cv, "appName like " + "\'" + appName +"\'", null);
                return true;
        } catch (Exception e) {
            Log.e("udateDetails err : ", "" + e);
            return false;
        }
        finally {
            db.setTransactionSuccessful();
            db.endTransaction();
            //db.close();
        }
    }

    public synchronized List<String> retrieveAppNames(){

        List<String> blockApps = new ArrayList<>();
        Cursor cursor=null;
        if(cursor != null && !cursor.isClosed()){
            cursor.close();
        }

        try {

            db = appOpenHelper.getReadableDatabase();
            db.beginTransaction();
            cursor = db.rawQuery("Select appName from BlockApps",null);
            if(cursor!=null) {
                if (cursor.moveToFirst()) {
                    do {
                        String appName = cursor.getString(cursor.getColumnIndex("appName"));
                        blockApps.add(appName);
                    } while (cursor.moveToNext());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.endTransaction();
        }
        return blockApps;

    }
    public long retrieveStartTimes(String appName){

       long res=0;
      // SQLiteDatabase db =null;
       Cursor cursor= null;
        if(cursor != null && !cursor.isClosed()){
            cursor.close();
        }
       try {
           db = appOpenHelper.getReadableDatabase();
           db.beginTransaction();
           cursor = db.rawQuery("Select startTime from BlockApps where appName like " + "\'" + appName +"\'", null);
           while (cursor.moveToNext()) {
               res = cursor.getLong(0);
           }
       }
       catch (Exception e){
           e.printStackTrace();
       }
       finally {
           db.endTransaction();
       }

       // db.close();
       // db.endTransaction();
        return res;

    }

    public long retrieveEndTimes(String appName){

        long res=0;
       // SQLiteDatabase db =null;
        Cursor cursor= null;
        if(cursor != null && !cursor.isClosed()){
            cursor.close();
        }
        try {
            db = appOpenHelper.getReadableDatabase();
            db.beginTransaction();
            cursor = db.rawQuery("Select endTime from BlockApps where appName like " + "\'" + appName +"\'", null);
            while (cursor.moveToNext()) {
                res = cursor.getLong(0);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            db.endTransaction();
        }


        // db.close();
        // db.endTransaction();
        return res;

    }
    public boolean dropTable(){
       db = appOpenHelper.getWritableDatabase();
       try {
           db.execSQL("Delete from BlockApps");
       }catch (Exception e){
           Log.i("Delete table error", e +"");
       }

           return  true;


    }
}