package com.healthguard.app.utils;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class DataContentProvider extends ContentProvider{
	

    private SQLiteDatabase     sqlDB;    
    private DatabaseHelper    dbHelper;  
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
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues contentvalues) {
		// TODO Auto-generated method stub
		sqlDB = dbHelper.getWritableDatabase();   
		System.out.println("<------The uri is ------->"+uri);
		final int match = matcher.match(uri);
		System.out.println("<------The match is ------->"+match);
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
		System.out.println("<----The rowid is ----->"+rowId);
		if (rowId > 0) {            
			Uri rowUri = ContentUris.withAppendedId(uri, rowId);            
			getContext().getContentResolver().notifyChange(rowUri, null);            
			return rowUri;        
		}        
		return null;
	}


	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		 SQLiteQueryBuilder qb = new SQLiteQueryBuilder();        
		 SQLiteDatabase db = dbHelper.getReadableDatabase(); 
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
		 Cursor c = qb.query(db, projection, selection, null, null, null, sortOrder);        
		 c.setNotificationUri(getContext().getContentResolver(), uri);        
		 return c;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return 0;
	}
	

}
