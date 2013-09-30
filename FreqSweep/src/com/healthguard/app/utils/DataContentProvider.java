package com.healthguard.app.utils;

import com.healthguard.app.utils.AppConsts;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.v4.util.LogWriter;
import android.util.Log;

public class DataContentProvider extends ContentProvider{
	

    public static final String TAG = "HealthManager";
	private SQLiteDatabase  sqlDB;    
    private DatabaseHelper  dbHelper;  
    public static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
  
	
    static {
        final String authority = AppConsts.AUTHORITY;

        matcher.addURI(authority, "bloodpressure", AppConsts.BLOODPRESSURE);
        matcher.addURI(authority, "bloodpressure/#", AppConsts.BLOODPRESSURE_ID);
        
        matcher.addURI(authority, "temperature", AppConsts.TEMPERATURE);
        matcher.addURI(authority, "temperature/#", AppConsts.TEMPERATURE_ID);
        
        matcher.addURI(authority, "weight", AppConsts.WEIGHT);
        matcher.addURI(authority, "weight/#", AppConsts.WEIGHT_ID);
    }
    
	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		dbHelper = new DatabaseHelper(getContext());        
		return (dbHelper == null) ? false : true;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		sqlDB = SQLiteDatabase.openOrCreateDatabase(AppConsts.databasePath, null);
		final int match = matcher.match(uri);
		int count;
		switch(match){
		case AppConsts.BLOODPRESSURE:
			count = sqlDB.delete(AppConsts.TABLE_BLOODPRESSURE, selection, selectionArgs);
			break;
		case AppConsts.TEMPERATURE:
			count = sqlDB.delete(AppConsts.TABLE_TEMPERATURE, selection, selectionArgs);
			break;
		case AppConsts.WEIGHT:
			count = sqlDB.delete(AppConsts.TABLE_WEIGHT, selection, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("不合法的URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues contentvalues) {
		// TODO Auto-generated method stub
//		sqlDB = dbHelper.getWritableDatabase();
		Log.i(TAG, "===>insert");
		sqlDB = SQLiteDatabase.openOrCreateDatabase(AppConsts.databasePath, null);
		final int match = matcher.match(uri);
		long rowId = -1;
		switch (match) {
		case AppConsts.BLOODPRESSURE:
			rowId = sqlDB.insert(AppConsts.TABLE_BLOODPRESSURE, null, contentvalues);
			break;
		case AppConsts.TEMPERATURE:
			rowId = sqlDB.insert(AppConsts.TABLE_TEMPERATURE, null, contentvalues);
			break;
		case AppConsts.WEIGHT:
			rowId = sqlDB.insert(AppConsts.TABLE_WEIGHT, null, contentvalues);
			break;
		  default: 
	            throw new UnsupportedOperationException("Unknown insert URI " + uri);
		}
		if (rowId > 0) {            
			Uri rowUri = ContentUris.withAppendedId(uri, rowId);            
			getContext().getContentResolver().notifyChange(rowUri, null); 
			Log.i(TAG, "===>insert data success");
			return rowUri;        
		}
		Log.i(TAG, "===>insert data failed");
		return null;
	}


	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		 Log.i(TAG, "===>query");
		 SQLiteDatabase sqlDB = SQLiteDatabase.openOrCreateDatabase(AppConsts.databasePath, null); 
		 SQLiteQueryBuilder qb = new SQLiteQueryBuilder();       
		 final int match = matcher.match(uri);
			switch (match) {
			case AppConsts.BLOODPRESSURE:
				qb.setTables(AppConsts.TABLE_BLOODPRESSURE); 
				break;
			case AppConsts.TEMPERATURE:
				qb.setTables(AppConsts.TABLE_TEMPERATURE); 
				break;
			case AppConsts.WEIGHT:
				qb.setTables(AppConsts.TABLE_WEIGHT); 
				break;
			  default: 
				  throw new UnsupportedOperationException("Unknown query URI " + uri);
			}
		 Cursor c = qb.query(sqlDB, projection, selection, null, null, null, sortOrder);        
		 c.setNotificationUri(getContext().getContentResolver(), uri);
		 return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		sqlDB = SQLiteDatabase.openOrCreateDatabase(AppConsts.databasePath, null);
		final int match = matcher.match(uri);
		int rowId = -1;
		switch (match) {
		case AppConsts.BLOODPRESSURE:
			rowId = sqlDB.update(AppConsts.TABLE_BLOODPRESSURE, values, selection, selectionArgs);
			break;
		case AppConsts.TEMPERATURE:
			rowId = sqlDB.update(AppConsts.TABLE_TEMPERATURE, values, selection, selectionArgs);
			break;
		case AppConsts.WEIGHT:
			rowId = sqlDB.update(AppConsts.TABLE_WEIGHT, values, selection, selectionArgs);
			break;
		 default: 
	        throw new UnsupportedOperationException("Unknown insert URI " + uri);
		}
		Uri rowUri = ContentUris.withAppendedId(uri, rowId);            
		getContext().getContentResolver().notifyChange(rowUri, null); 
//		sqlDB.close();
		return rowId;
	}


}
