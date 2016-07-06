package com.zju.rascontroller;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.gsm.SmsMessage;

public class SMSService extends Service {
	private IntentFilter receiverFilter;
	private MessageReceiver messageReceiver;
	private MyBinder myBinder = new MyBinder();
	class MyBinder extends Binder{
		public void myTask()
		{
			new Thread(new Runnable()
			{

				@Override
				public void run() {
					// TODO Auto-generated method stub
				}
				
			}).start();
		}
	}

	public SMSService() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return myBinder;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		Notification notification = new Notification(R.drawable.ic_launcher,
				"RASController", System.currentTimeMillis());
		Intent notificationIntent = new Intent(this, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(this, "实时水质监控智能终端", "正在监控......",
				pendingIntent);
		startForeground(1, notification);
		receiverFilter = new IntentFilter();
		receiverFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
		receiverFilter.setPriority(100);
		messageReceiver = new MessageReceiver();
		registerReceiver(messageReceiver, receiverFilter);
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new Thread(new Runnable() {  
	        @Override  
	        public void run() {  
	            // do something here
	        }  
	    }).start();
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(messageReceiver);
	}
	private class MessageReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Bundle bundle = intent.getExtras();
			Object[] pdus=(Object[])bundle.get("pdus");
			SmsMessage[] messages = new SmsMessage[pdus.length];
			for(int i = 0;i < messages.length;i++)
			{
				messages[i]=SmsMessage.createFromPdu((byte[])pdus[i]);
			}
			String address = messages[0].getOriginatingAddress();
			long timeReceive = messages[0].getTimestampMillis();//获取发送时间
			String fullMessage = "";
			for(SmsMessage message:messages){
				fullMessage+=message.getMessageBody();
			}
			if(address.equals("13732244094"))
			{
				String[] info=fullMessage.split("#");
				if(info.length>=5&& info[1].equals("data")&& info[4].equals("en"))
				{
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
					Date date = new Date(timeReceive);
					String getDate = formatter.format(date);
					int fre = (int) ((Double.valueOf(info[2]))/100);
					double x = date.getHours()+date.getMinutes()/60.0+date.getSeconds()/3600.0;
					double oxy = ( Double.valueOf(info[3]))/1280-5;
					Uri uri = Uri.parse("content://com.zju.rascontroller.provider/oxygen");
					ContentValues values = new ContentValues();
					values.put("frequency", fre);
					values.put("dateQ", getDate);
					values.put("timeX", x);
					values.put("oxygen",oxy);
					getContentResolver().insert(uri, values);
				}
				abortBroadcast();
			}
		}
		
	}
}
