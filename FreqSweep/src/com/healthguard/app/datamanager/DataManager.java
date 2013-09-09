package com.healthguard.app.datamanager;

import com.healthguard.app.utils.AppConsts;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class DataManager implements DataManagerInterFace{
	
	public static final String TAG = "DataManager";
	private Context mContext;
	private ContentResolver mContentResolver;
	 static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
	
	public DataManager(Context context){
		mContext = context;
		mContentResolver =  mContext.getContentResolver();
	}
	
	
	public void insert(Uri uri, ContentValues values){
			mContentResolver.insert(uri, values);
			Log.i(TAG, "===>insert OK");
	}
	
	
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs){
		final int match = URI_MATCHER.match(uri);
		Cursor cursor = null;
		if((match == AppConsts.BLOODPRESSURE)||(match == AppConsts.TEMPERATURE)||(match == AppConsts.WEIGHT)){
			cursor = mContentResolver.query(uri, projection, selection, selectionArgs, null);
			Log.i(TAG, "===>query OK");
		}
		else{
			throw new UnsupportedOperationException("Unknown query URI " + uri);
		}
		return cursor;
	}
	
	
	public int update(Uri uri, ContentValues values, String where, String[] selectionArgs){
		final int match = URI_MATCHER.match(uri);
		int rows;
		if((match == AppConsts.BLOODPRESSURE)||(match == AppConsts.TEMPERATURE)||(match == AppConsts.WEIGHT)){
			rows  = mContentResolver.update(uri, values, where, selectionArgs);
			Log.i(TAG, "===>update OK");
		}
		else{
			throw new UnsupportedOperationException("Unknown query URI " + uri);
		}
		return rows;
	}
	
	
	public void delete(Uri uri, String where, String[] selectionArgs){
		final int match = URI_MATCHER.match(uri);
		if((match == AppConsts.BLOODPRESSURE)||(match == AppConsts.TEMPERATURE)||(match == AppConsts.WEIGHT)){
			mContentResolver.delete(uri, where, selectionArgs);
			Log.i(TAG, "===>delete OK");
		}
	}
	
	public void deleteAll(Uri uri){
		final int match = URI_MATCHER.match(uri);
		if((match == AppConsts.BLOODPRESSURE)||(match == AppConsts.TEMPERATURE)||(match == AppConsts.WEIGHT)){
			mContentResolver.delete(uri, null, null);
			Log.i(TAG, "===>deleteAll OK");
		}
		else{
			throw new UnsupportedOperationException("Unknown query URI " + uri);
		}
	}
	
	
	
	
}
