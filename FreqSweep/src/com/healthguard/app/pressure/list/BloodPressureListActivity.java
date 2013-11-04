package com.healthguard.app.pressure.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.healthguard.app.R;
import com.healthguard.app.utils.AppConsts;

public class BloodPressureListActivity extends Fragment{
	
	private final static String TAG = "BloodPressureListView";
	private ListView listView;
	private List<Map<String, String>> listItems;
	private PopupWindow popupWindow;
	private Button btn_check_detail;
	private Button btn_delete_item;
	
	

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
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				System.out.println("<----click position is ---->"+position);
				showPopUp(view, position);
			}
			
		});
		return view;
	}

	private void showPopUp(View v, final int position) {
		LayoutInflater inflater = this.getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.pop_up_windows_layout, null);
		btn_check_detail = (Button)view.findViewById(R.id.check_detail);
		btn_delete_item = (Button)view.findViewById(R.id.delete_item);
		btn_delete_item.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Cursor c = getActivity().getContentResolver().query(AppConsts.BLOODPRESSURE_URI, null,  null,  null,  null);
				c.moveToPosition(position);
				String delete_id = c.getString(c.getColumnIndex("_id"));
				System.out.println("<----The delete id is ----->"+delete_id);
				int index = getActivity().getContentResolver().
						delete(AppConsts.BLOODPRESSURE_URI, "_id=?", new String[]{delete_id});
			}
		});
		
		popupWindow = new PopupWindow(view,200,120);
		
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(this.getActivity().getResources().getDrawable(R.drawable.pop_background));
		
		int[] location = new int[2];
		v.getLocationOnScreen(location);
		
		popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1]-popupWindow.getHeight());
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
