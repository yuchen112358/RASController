package com.zju.rascontroller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.gsm.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener{

	private static final int OPEN=1;
	private static final int CLOSE=2;
	private static final int FOPEN=3;
	private static final int FCLOSE=4;
	private ImageButton watchButton;
	private ImageButton queryButton;
	private ImageButton openButton;//增氧开关
	private ImageButton closeButton;
	private ImageButton openFeed;//投饵开关
	private ImageButton closeFeed;
	private ImageButton updateButton;
	private EditText queryTime;
	private IntentFilter sendFilter;
	private SendStatusReceiver sendStatusReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		updateButton = (ImageButton)findViewById(R.id.update_button);
		watchButton = (ImageButton)findViewById(R.id.watch_button);
		queryButton = (ImageButton)findViewById(R.id.query_button);
		openButton = (ImageButton)findViewById(R.id.open_button);
		closeButton = (ImageButton)findViewById(R.id.close_button);
		openFeed=(ImageButton)findViewById(R.id.open_feed);
		closeFeed=(ImageButton)findViewById(R.id.close_feed);
		queryTime = (EditText)findViewById(R.id.query_time);
		sendFilter = new IntentFilter();
		sendFilter.addAction("SENT_SMS_ACTION");
		sendStatusReceiver=new SendStatusReceiver();
		registerReceiver(sendStatusReceiver, sendFilter);
		updateButton.setOnClickListener(this);
		watchButton.setOnClickListener(this);
		queryButton.setOnClickListener(this);
		openButton.setOnClickListener(this);
		closeButton.setOnClickListener(this);
		openFeed.setOnClickListener(this);
		closeFeed.setOnClickListener(this);
		Intent startIntent = new Intent(this,SMSService.class);
		startService(startIntent);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(sendStatusReceiver);
	}
	//1、创建数据库
	//2、创建service，接收短信存入数据库
	//3、取数据库数据,创建曲线

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.update_button:
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle("数据库更新操作");
			dialog.setMessage("更新数据库将删除原有数据，请慎重操作！");
			dialog.setCancelable(false);
			dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Uri uri = Uri.parse("content://com.zju.rascontroller.provider/oxygen");
					getContentResolver().delete(uri, null, null);
					Toast.makeText(MainActivity.this, "已删除数据库原有数据！", Toast.LENGTH_SHORT).show();
				}
			});
			dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Toast.makeText(MainActivity.this, "已取消更新，数据库将保留原有数据！", Toast.LENGTH_SHORT).show();			
				}
			});
			dialog.show();
			break;
		case R.id.watch_button:
			Intent intent = new Intent(this,DynamicChartActivity.class);
			startActivity(intent);
			break;
		case R.id.query_button:
			String date = queryTime.getText().toString();
			QueryChartActivity.actionStart(this, date);
			break;
		case R.id.open_button:
			SmsManager onSmsManager = SmsManager.getDefault();
			Intent onIntent = new Intent("SENT_SMS_ACTION");
			onIntent.putExtra("flag", OPEN);
			PendingIntent piOn = PendingIntent.getBroadcast(this, OPEN, onIntent,0);
			onSmsManager.sendTextMessage("13732244094", null, "on", piOn, null);
			break;
		case R.id.close_button:
			SmsManager offSmsManager = SmsManager.getDefault();
			Intent offIntent = new Intent("SENT_SMS_ACTION");
			offIntent.putExtra("flag", CLOSE);
			PendingIntent piOff = PendingIntent.getBroadcast(this, CLOSE, offIntent,0);
			offSmsManager.sendTextMessage("13732244094", null, "of", piOff, null);
			break;
		case R.id.open_feed:
			SmsManager fonSmsManager = SmsManager.getDefault();
			Intent fonIntent = new Intent("SENT_SMS_ACTION");
			fonIntent.putExtra("flag", FOPEN);
			PendingIntent fpiOn = PendingIntent.getBroadcast(this, FOPEN, fonIntent,0);
			fonSmsManager.sendTextMessage("13732244094", null, "fw", fpiOn, null);
			break;
		case R.id.close_feed:
			SmsManager foffSmsManager = SmsManager.getDefault();
			Intent foffIntent = new Intent("SENT_SMS_ACTION");
			foffIntent.putExtra("flag", FCLOSE);
			PendingIntent fpiOff = PendingIntent.getBroadcast(this, FCLOSE, foffIntent,0);
			foffSmsManager.sendTextMessage("13732244094", null, "fz", fpiOff, null);
			break;
		default:
				break;
		}
		
	}
	private class SendStatusReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			int flag = intent.getIntExtra("flag", 0);
			switch(flag)
			{
			case OPEN:
				if(getResultCode()==RESULT_OK){
					Toast.makeText(context, "打开增氧机成功！", Toast.LENGTH_SHORT).show();
				}
				else
				{
					Toast.makeText(context, "打开增氧机失败！", Toast.LENGTH_SHORT).show();
				}
				break;
			case CLOSE:
				if(getResultCode()==RESULT_OK){
					Toast.makeText(context, "关闭增氧机成功！", Toast.LENGTH_SHORT).show();
				}
				else
				{
					Toast.makeText(context, "关闭增氧机失败！", Toast.LENGTH_SHORT).show();
				}
				break;
			case FOPEN:
				if(getResultCode()==RESULT_OK){
					Toast.makeText(context, "打开投饵机成功！", Toast.LENGTH_SHORT).show();
				}
				else
				{
					Toast.makeText(context, "打开投饵机失败！", Toast.LENGTH_SHORT).show();
				}
				break;
			case FCLOSE:
				if(getResultCode()==RESULT_OK){
					Toast.makeText(context, "关闭投饵机成功！", Toast.LENGTH_SHORT).show();
				}
				else
				{
					Toast.makeText(context, "关闭投饵机失败！", Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
			}
		}
		
	}
}
