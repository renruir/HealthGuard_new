package com.healthguard.app.utils;

import java.text.SimpleDateFormat;

import android.net.Uri;

public class AppConsts {
	
	public static final String TABLE_BLOODPRESSURE = "bloodpressure";
	
	public static final String TABLE_TEMPERATURE = "temperature";
	
	public static final String TABLE_WEIGHT = "weight";
	
	public static final String Value_Pressure_high = "pressure_high";
	
	public static final String Value_Pressure_low = "pressure_low";
	
	public static final String Value_HeartBeat = "heartbeat";
	
	public static final String Value_Temperature  = "temperature";
	
	public static final String Value_Weight = "weight";
	
	public static final int BLOODPRESSURE = 100;
	public static final int BLOODPRESSURE_ID = 101;

	public static final int TEMPERATURE = 200;
	public static final int TEMPERATURE_ID = 201;
    
	public static final int WEIGHT = 300;
	public static final int WEIGHT_ID = 301;
	
	public static final String AUTHORITY = "umich.framjack.apps.freqsweep.data";
	
	public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);
	
	public static final Uri BLOODPRESSURE_URI = Uri.withAppendedPath(AUTHORITY_URI, TABLE_BLOODPRESSURE);
	
	public static final Uri TEMPERATURE_URI = Uri.withAppendedPath(AUTHORITY_URI, TABLE_TEMPERATURE);
	
	public static final Uri WEIGHT_URI = Uri.withAppendedPath(AUTHORITY_URI, TABLE_WEIGHT);
	

	public static String getCurrentTime(){
		SimpleDateFormat    sDateFormat  = new SimpleDateFormat("yyyy-MM-dd    hh:mm:ss");       
		String  date  =  sDateFormat.format(new    java.util.Date());   
		System.out.println("<------The date is ------->"+date);
		return date;
	}
	

}
