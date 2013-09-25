package com.healthguard.app;

import java.util.ArrayList;

import com.healthguard.app.utils.ImageInfo;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;

public class MainActivity extends Activity{
	
	public final static String TAG = "HealthManager";
	private ArrayList<ImageInfo> data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main1);
		initView();
		
		ViewPager vpager = (ViewPager) findViewById(R.id.vPager);
		vpager.setAdapter(new ViewPagerAdapter(MainActivity.this, data));
		vpager.setPageMargin(50);
	}

	private void initView() {
		// TODO Auto-generated method stub
		Log.i(TAG, "===>initView");
		data = new ArrayList<ImageInfo>();
		data.add(new ImageInfo("测量血压", R.drawable.ic_blood_pressure));
		data.add(new ImageInfo("测量体温", R.drawable.ic_body_temperature));
		data.add(new ImageInfo("测量体重", R.drawable.ic_body_weight));
	}
	
}
