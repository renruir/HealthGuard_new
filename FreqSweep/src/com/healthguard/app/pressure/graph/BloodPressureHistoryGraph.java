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

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.i(TAG, "BloodPressureHistoryGraph onCreateView ");
//		View view  = inflater.inflate(R.layout.blood_pressure_history_graph, null);
//		layout = (LinearLayout)view.findViewById(R.id.line_chart) ;
		chartView = ChartFactory.getLineChartView(this.getActivity(), getDataSet(), getRenderer());
//		layout.addView(chartView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		return chartView;
	}

	private XYMultipleSeriesDataset  getDataSet(){
		dataSet = new XYMultipleSeriesDataset();
		Cursor cursor = getActivity().getContentResolver().query(AppConsts.BLOODPRESSURE_URI, null,  null,  null,  null);
		XYSeries series_systolic = new XYSeries("systolic");
		for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
			series_systolic.add(cursor.getFloat(0), cursor.getFloat(1));
		}
		
		dataSet.addSeries(series_systolic);
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
		     return renderer;
	}
	
}
