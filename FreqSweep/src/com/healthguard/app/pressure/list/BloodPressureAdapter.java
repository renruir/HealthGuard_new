package com.healthguard.app.pressure.list;

import java.util.List;
import java.util.Map;

import com.healthguard.app.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BloodPressureAdapter extends BaseAdapter{
	
	private LayoutInflater listContainer;  
	private List<Map<String, String>> listItems;
	
	public BloodPressureAdapter(List<Map<String, String>> list, Context context){
		this.listContainer = LayoutInflater.from(context);
		this.listItems = list;
	}
	
	class ListViewItem{
		TextView measureTime;
		TextView diastolicValue;
		TextView diastolicState;
		TextView systolicValue;
		TextView systolicState;
		TextView heartRateValue;
		TextView heartRateState;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		 final int selectID = position;   
		 ListViewItem listItemView = null;
		 if(convertView == null){
			 listItemView = new ListViewItem();
			 convertView = listContainer.inflate(R.layout.bloodpressure_list_item_layout, null);
			//获取控件对象
			 listItemView.measureTime = (TextView)convertView.findViewById(R.id.pressure_meausure_time);
			 listItemView.diastolicValue = (TextView)convertView.findViewById(R.id.diastolic);
			 listItemView.diastolicState = (TextView)convertView.findViewById(R.id.diastolic_state);
			 listItemView.systolicValue = (TextView)convertView.findViewById(R.id.systolic);
			 listItemView.systolicState = (TextView)convertView.findViewById(R.id.systolic_state);
			 listItemView.heartRateValue = (TextView)convertView.findViewById(R.id.list_heart_rate);
			 listItemView.heartRateState = (TextView)convertView.findViewById(R.id.list_heart_rate_state);
			//设置控件集到convertView 
			 convertView.setTag(listItemView);
		 }
		 else{
			 listItemView = (ListViewItem)convertView.getTag();
		 }
		 
		//设置文字和图片 
		 listItemView.measureTime.setText(listItems.get(position).get("time"));
		 listItemView.diastolicValue.setText(listItems.get(position).get("diastolic"));
//		 listItemView.diastolicState.setText(listItems.get(position).get("diastolic"));
		 listItemView.systolicValue.setText(listItems.get(position).get("systolic"));
//		 listItemView.systolicState.setText(listItems.get(position).get("diastolic"));
		 listItemView.heartRateValue.setText(listItems.get(position).get("heartbeat"));
//		 listItemView.heartRateState.setText(listItems.get(position).get("heartbeat"));
		 
		return convertView;
	}


}
