package com.healthguard.app.pressure.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.healthguard.app.R;
import com.healthguard.app.utils.AppConsts;

public class BloodPressureListActivity extends Fragment{
	
	private final static String TAG = "BloodPressureListView";
	private ListView listView;
	private List<Map<String, String>> listItems;
	
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.pressure_list_view, null);
		listView = (ListView)view.findViewById(R.id.list);
		listItems = getListItems();
		Log.i(TAG, "listItems size = "+listItems.size());
		BloodPressureAdapter bloodPressureAdapter = new BloodPressureAdapter(listItems, getActivity());
		listView.setAdapter(bloodPressureAdapter);
		return view;
	}


//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.pressure_list_view);
//		listView = (ListView)findViewById(R.id.list);
//		listItems = getListItems();
//		Log.i(TAG, "listItems size = "+listItems.size());
//		BloodPressureAdapter bloodPressureAdapter = new BloodPressureAdapter(listItems, this);
//		listView.setAdapter(bloodPressureAdapter);
//	}
 
	
	private List<Map<String, String>> getListItems(){
		List<Map<String, String>> listItems = new ArrayList<Map<String, String>>();
		
		Cursor cursor = getActivity().getContentResolver().query(AppConsts.BLOODPRESSURE_URI, null,  null,  null,  null);
		for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
			Map<String, String> map = new HashMap<String, String>();
			map.put("time", cursor.getString(cursor.getColumnIndex("_id")));
			map.put("systolic", cursor.getString(cursor.getColumnIndex("pressure_high")));
			map.put("diastolic", cursor.getString(cursor.getColumnIndex("pressure_low")));
			map.put("heartbeat", cursor.getString(cursor.getColumnIndex("heartbeat")));
			listItems.add(map);
		}
		return listItems;
	}
}
