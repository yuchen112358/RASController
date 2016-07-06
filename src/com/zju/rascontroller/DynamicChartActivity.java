package com.zju.rascontroller;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.achartengine.GraphicalView;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DynamicChartActivity extends Activity {
	 public static final int UPDATE_CHART = 1;
	 private LinearLayout oxygenCurveLayout;//存放图表的布局容器
	 private GraphicalView chartView;//图表
	 private ChartDrawer oxygenChart;
	 private Timer timer;
	 private TextView frequencyText;
	 private TextView oxygenText;
	 private ChartHandler handler = new ChartHandler(this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dynamic_chart);
		oxygenCurveLayout = (LinearLayout) findViewById(R.id.dynamic_curve);
		frequencyText = (TextView)findViewById(R.id.current_frequency);
		oxygenText=(TextView)findViewById(R.id.current_oxygen);
		oxygenChart = new ChartDrawer(this);
		oxygenChart.setXYMultipleSeriesDataset("溶氧值曲线系列");
		oxygenChart.setXYMultipleSeriesRenderer(24, 25, "溶氧值实时曲线图", "时间(hour)", "溶氧值(mg/L)",
				Color.BLACK, Color.BLUE, Color.RED, Color.GRAY);
		chartView = oxygenChart.getGraphicalView();
		//将图表添加到布局容器中
		oxygenCurveLayout.addView(chartView, new LayoutParams(
		            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		   
		timer = new Timer();
		timer.schedule(new TimerTask() {
		          	@Override
		            public void run() {
		          		Message message = new Message();
		          		message.what = UPDATE_CHART;
		                handler.sendMessage(message);
		               }
					}, 10, 11*1000);//10毫秒后开启，周期为11秒
	}
	@Override
	protected void onDestroy() {
			super.onDestroy();
			if (timer != null) {
					timer.cancel();
	          }
	      } 
	static class ChartHandler extends Handler
	{
		WeakReference<DynamicChartActivity> mActivity;
		ChartHandler(DynamicChartActivity activity)
		{
			mActivity = new WeakReference<DynamicChartActivity>(activity) ;
		}
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			DynamicChartActivity thisActivity = mActivity.get();
			 switch(msg.what)
			 {
			 case UPDATE_CHART:
				 Uri uri = Uri.parse("content://com.zju.rascontroller.provider/oxygen");
				 long timeR = System.currentTimeMillis();
				 SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
				 Date d = new Date(timeR);
				 String date = formatter.format(d);
				 Cursor cursor = thisActivity.getContentResolver().query(uri, new String[]{"frequency","timeX","oxygen"}, 
						 "dateQ=?", new String[]{date}, "timeX");
				 List<Double> xData = new ArrayList<Double>();
				 List<Double> yData = new ArrayList<Double>();
				 int frequencyValue=0;
				 double oxygenValue=0.0;
				 if(cursor!=null)
				 {
					 while(cursor.moveToNext())
					 {
						 xData.add(cursor.getDouble(cursor.getColumnIndex("timeX")));
						 yData.add(cursor.getDouble(cursor.getColumnIndex("oxygen")));
						 frequencyValue=cursor.getInt(cursor.getColumnIndex("frequency"));	
						 oxygenValue=cursor.getDouble(cursor.getColumnIndex("oxygen"));
					 }
					 cursor.close();
				 }
				 DecimalFormat formatD= new DecimalFormat("#0.00");  
				 thisActivity.oxygenChart.updateChart(xData,yData);
				 thisActivity.frequencyText.setText(String.valueOf(frequencyValue)+"Hz");
				 thisActivity.oxygenText.setText(formatD.format(oxygenValue)+"mg/L");
				 break;
			default:
				 break;
			 }
		}
	}
}
