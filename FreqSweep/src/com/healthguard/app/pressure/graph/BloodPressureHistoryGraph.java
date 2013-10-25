package com.healthguard.app.pressure.graph;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.healthguard.app.R;
import com.healthguard.app.utils.AppConsts;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class BloodPressureHistoryGraph extends Fragment{
	
	private final static String TAG = "BloodPressureHistoryGraph";
	private GraphicalView chartView;
	private LinearLayout layout;
	private XYMultipleSeriesDataset dataSet;

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.i(TAG, "BloodPressureHistoryGraph onCreateView ");
		View view  = inflater.inflate(R.layout.blood_pressure_history_graph, null);
		layout = (LinearLayout)view.findViewById(R.id.line_chart) ;
		chartView = ChartFactory.getLineChartView(this.getActivity(), getDataSet(), getRenderer());
//		chartView.setBackgroundResource(R.drawable.bg);
		chartView.setAlpha((float) 0.5);
		layout.addView(chartView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		return view;
	}

	private XYMultipleSeriesDataset  getDataSet(){
		dataSet = new XYMultipleSeriesDataset();
		Cursor cursor = getActivity().getContentResolver().query(AppConsts.BLOODPRESSURE_URI, null,  null,  null,  null);
		XYSeries series_systolic = new XYSeries("systolic");
		XYSeries series_diastolic = new XYSeries("diastolic");
		XYSeries serier_heartBeat = new XYSeries("heartBeat");
		cursor.moveToFirst();
		for(int i =0; i < cursor.getCount(); i++){
			cursor.moveToPosition(i);
			String systolic = cursor.getString(cursor.getColumnIndex("pressure_high"));
			series_systolic.add(i, Integer.parseInt(systolic)	);
			
			String diastolic = cursor.getString(cursor.getColumnIndex("pressure_low"));
			series_diastolic.add(i, Integer.parseInt(diastolic));
			
			String heartBeat = cursor.getString(cursor.getColumnIndex("heartbeat"));
			serier_heartBeat.add(i, Integer.parseInt(heartBeat));
		}
		
		dataSet.addSeries(series_systolic);
		dataSet.addSeries(series_diastolic);
		dataSet.addSeries(serier_heartBeat);
		return dataSet;
	}
	
	private XYMultipleSeriesRenderer getRenderer(){
		  XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		     
		     //设置图表中曲线本身的样式，包括颜色、点的大小以及线的粗细等
		     XYSeriesRenderer r_systolic = new XYSeriesRenderer();
		     r_systolic.setColor(Color.GREEN);
		     r_systolic.setPointStyle(PointStyle.CIRCLE);
		     r_systolic.setFillPoints(true);
		     r_systolic.setLineWidth(3);
		     renderer.addSeriesRenderer(0, r_systolic);
		     
		     XYSeriesRenderer r_diastolic  = new XYSeriesRenderer();
		     r_diastolic.setColor(Color.RED);
		     r_diastolic.setPointStyle(PointStyle.CIRCLE);
		     r_diastolic.setFillPoints(true);
		     r_diastolic.setLineWidth(3);
		     renderer.addSeriesRenderer(1, r_diastolic);
		     
		     XYSeriesRenderer r_hreatBeat  = new XYSeriesRenderer();
		     r_hreatBeat.setColor(Color.YELLOW);
		     r_hreatBeat.setPointStyle(PointStyle.CIRCLE);
		     r_hreatBeat.setFillPoints(true);
		     r_hreatBeat.setLineWidth(3);
		     renderer.addSeriesRenderer(1, r_hreatBeat);
		     return renderer;
	}
	
}
