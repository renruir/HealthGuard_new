package com.healthguard.app.pressure;

import com.healthguard.app.R;
import com.healthguard.app.pressure.list.BloodPressureListActivity;
import com.healthguard.app.pressure.measure.BloodPressureMeasureActivity;

import android.os.Bundle;
import android.support.v4.app.*;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;


public class BloodPressureActivity extends FragmentActivity  {

	private final static String TAG = "BloodPressureActivity";
	private FragmentTabHost mTabHost;  
	private LayoutInflater layoutInflater;  
	
	private Class fragmentArray[] = {BloodPressureMeasureActivity.class, BloodPressureListActivity.class, 
			BloodPressureListActivity.class};
	
	private String mTextviewArray[] = {"测量", "记录", "图形"};
	private int mImageViewArray[] = {R.drawable.ic_measure_btn, R.drawable.ic_history_btn, R.drawable.ic_graph_btn};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.i(TAG, "===>BloodPressureActivityonCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blood_pressure_tab_activity);
		initView(); 
	}
	
	
	private void initView() {
		// TODO Auto-generated method stub
		 layoutInflater = LayoutInflater.from(this);  
		 mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);  
		 mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);  
		 int count = fragmentArray.length;
		 for(int i = 0; i < count; i++){    
			  //为每一个Tab按钮设置图标、文字和内容  
	            TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
	          //将Tab按钮添加进Tab选项卡中  
	            mTabHost.addTab(tabSpec, fragmentArray[i], null);  
	          //设置Tab按钮的背景  
	            mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.ic_measure_background);  
		 }
		
	}

	
	 private View getTabItemView(int index){  
	        View view = layoutInflater.inflate(R.layout.blood_pressure_tab_item, null);  
	      
	        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);  
	        imageView.setImageResource(mImageViewArray[index]);  
	          
	        TextView textView = (TextView) view.findViewById(R.id.textview);          
	        textView.setText(mTextviewArray[index]);  
	      
	        return view;  
	    }  
	
}
