package com.healthguard.app.datamanager;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public abstract interface DataManagerInterFace {
	
	abstract public void insert(Uri uri, ContentValues values);
	
	abstract public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs);
	
	abstract public int update(Uri uri, ContentValues values, String where, String[] selectionArgs);
	
	abstract public void delete(Uri uri, String where, String[] selectionArgs);
	
	abstract public void deleteAll(Uri uri);

}
