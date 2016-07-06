package com.zju.rascontroller;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.GraphicalView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class QueryChartActivity extends Activity {
	 private LinearLayout oxygenCurveLayout;//存放图表的布局容器
	 private GraphicalView chartView;//图表
	 private ChartDrawer oxygenChart;
	 
	 public static void actionStart(Context context,String data)
	 {
		 Intent intent = new Intent(context,QueryChartActivity.class);
		 intent.putExtra("date", data);
		 context.startActivity(intent);
	 }

	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.query_chart);
		Intent intent = getIntent();
		String dateQuery = intent.getStringExtra("date");
		oxygenCurveLayout = (LinearLayout) findViewById(R.id.query_curve);
		oxygenChart = new ChartDrawer(this);
		oxygenChart.setXYMultipleSeriesDataset("溶氧值曲线系列");
		oxygenChart.setXYMultipleSeriesRenderer(24, 15, "溶氧值查询曲线图("+dateQuery+")", "时间(hour)", "溶氧值(mg/L)",
				Color.BLACK, Color.BLUE, Color.RED, Color.GRAY);
		chartView = oxygenChart.getGraphicalView();
		//将图表添加到布局容器中
		oxygenCurveLayout.addView(chartView, new LayoutParams(
		            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		Uri uri = Uri.parse("content://com.zju.rascontroller.provider/oxygen");
		Cursor cursor = getContentResolver().query(uri, new String[]{"timeX","oxygen"}, 
				 "dateQ=?", new String[]{dateQuery}, "timeX");
		List<Double> xData = new ArrayList<Double>();
		List<Double> yData = new ArrayList<Double>();
		if(cursor!=null)
		 {
			 while(cursor.moveToNext())
			 {
				 xData.add(cursor.getDouble(cursor.getColumnIndex("timeX")));
				 yData.add(cursor.getDouble(cursor.getColumnIndex("oxygen")));
				 
			 }
			 cursor.close();
		 }
		 oxygenChart.updateChart(xData,yData);
	 }
	@Override
	protected void onDestroy() {
			super.onDestroy();
	} 
}
