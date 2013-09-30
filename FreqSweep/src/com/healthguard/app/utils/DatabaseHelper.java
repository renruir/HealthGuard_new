package com.healthguard.app.utils;

import com.healthguard.app.utils.AppConsts;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.SystemClock;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	  private static final String  DATABASE_NAME = "userData.db";
	  private static final int  DATABASE_VERSION= 1;
	  private String CREATE_BLOOD_PRESSURE_TABLE = "CREATE TABLE IF NOT EXISTS "+ AppConsts.TABLE_BLOODPRESSURE+
	  		 " 	(_id STRING PRIMARY KEY  ," 
	  		 +AppConsts.Value_Pressure_high+" INTEGER  , "
	  		 +AppConsts.Value_Pressure_low+"  INTEGER  , "
	  		 +AppConsts.Value_HeartBeat+"  INTEGER )"; 
	  
	  private String CREATE_TEMPERATURE_TABLE = "CREATE TABLE "+ AppConsts.TABLE_TEMPERATURE+
		  		 "(_id STRING PRIMARY KEY ," 
		  		+AppConsts.Value_Temperature+"  INTEGER)"; 
	  
	  private String CREATE_WEIGHT_TABLE = "CREATE TABLE "+ AppConsts.TABLE_WEIGHT+
		  		 "(_id STRING PRIMARY KEY ," 
		  		+AppConsts.Value_Temperature+"  INTEGER)"; 
	
	  DatabaseHelper(Context context) { 
    	super(context, DATABASE_NAME, null, DATABASE_VERSION);       
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db = SQLiteDatabase.openOrCreateDatabase("/mnt/sdcard/health/userData.db", null);
		db.execSQL(CREATE_BLOOD_PRESSURE_TABLE);
		db.execSQL(CREATE_TEMPERATURE_TABLE);
		db.execSQL(CREATE_WEIGHT_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	

}
