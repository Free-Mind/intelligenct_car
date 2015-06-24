package com.uptech.intelligentgreenhouse;

import java.io.IOException;
import java.net.UnknownHostException;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.uptech.sensorinfo.SensorCollection;
import com.uptech.sensorinfo.SensorDataTools;
import com.uptech.sensorinfo.SensorFrameFilter;
import com.uptech.sensorinfo.SocketTheadManager;

public class IntelligentGreenHouseActivity extends Activity {
	private static final String TAG = "IntelligentGreenHouseActivity";
	private TextView[] mTextView;
	private String action;
	private MyBroadcastReceiver recv;
	private SocketTheadManager socketThreadManager;
	private SocketTheadManager socketThreadManager2;
	private SensorCollectionApplication app;
	private SensorFrameFilter filter;
	private Context context;
	private ImageView lightView;
	private long exitTime = 0;
	private int count = 0;
	private LinearLayout linearLayout;
	public static byte[] sendBuffer = new byte[] { (byte) 0xFE, (byte) 0xEF,
		0x09, 0x78, 0x71, 0x00, (byte) 0xFF, (byte) 0xFF, 0x0A };
	public byte wBuffer[] = new byte[] { (byte) 0xFE, (byte) 0xE0, 0x0B, 0x58,
			0x72, 0x00, 0x00, 0x00, 0x70, 0x00, 0x0A };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intelligentgreenhouse);
		mTextView = new TextView[6];
		//app = (SensorCollectionApplication) getApplicationContext();
		mTextView[0] = (TextView) findViewById(R.id.door);//车门关闭状态
		mTextView[1] = (TextView) findViewById(R.id.temp);//光照
		mTextView[2] = (TextView) findViewById(R.id.humi);//天气
		mTextView[3] = (TextView) findViewById(R.id.airp);//紫外线
		mTextView[4] = (TextView) findViewById(R.id.snow);//温度
		mTextView[5] = (TextView) findViewById(R.id.vum);//湿度
		
		linearLayout = (LinearLayout) findViewById(R.id.LinearLayout1);
		recv = new MyBroadcastReceiver();
		action = getPackageName();
		context = this.getApplicationContext();
		filter = SensorFrameFilter.getinstance(context);
		filter.setAction(action);
		Runnable startClient=new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					socketThreadManager = SocketTheadManager.getSocketThreadManager(SensorDataTools.getLocalIpAddress(), 6008);
					socketThreadManager.setDebug(true);
					socketThreadManager.setReadRate(10);
					socketThreadManager.setSendRate(200);
					socketThreadManager.setFilter(filter);
					socketThreadManager.setSenddata(sendBuffer);
					socketThreadManager.startSocketThread();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		};
		new Thread(startClient).start();
		
		Runnable buzzer=new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					socketThreadManager2 = SocketTheadManager.getSocketThreadManager(SensorDataTools.getLocalIpAddress(), 6008);
					socketThreadManager2.setDebug(true);
					socketThreadManager2.setReadRate(10);
					socketThreadManager2.setSendRate(200);
					socketThreadManager2.setFilter(filter);
					socketThreadManager2.setSenddata(wBuffer);
					socketThreadManager2.startSocketThread();
					System.out.println("send wBuffer!");
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		new Thread(buzzer).start();
		
		IntentFilter filter = new IntentFilter();
		//filter.addAction(action + SensorCollection.SensorID.AIRP);
		filter.addAction(action + SensorCollection.SensorID.IRDS);
		filter.addAction(action + SensorCollection.SensorID.LLUX);
		filter.addAction(action + SensorCollection.SensorID.SHT);
		filter.addAction(action + SensorCollection.SensorID.SNOW);
		filter.addAction(action + SensorCollection.SensorID.SMOG);
		this.registerReceiver(recv, filter);
		
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 200) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				stopClientThread();
				this.unregisterReceiver(recv);
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void stopClientThread() {
		if (socketThreadManager != null)
			socketThreadManager.release();
		if (socketThreadManager2 != null)
			socketThreadManager2.release();
		Log.e(TAG, "stoped");
	}
	/**
	 * Broadcast Receiver
	 * 
	 */
	private class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals( action + SensorCollection.SensorID.IRDS))
			{
				if(SensorCollection.SensorData.SIRDS.status == 0x01){
					mTextView[0].setText("车门全部关闭");
					//如果之前有提醒过、现在需要把状态重置为正常状态
					wBuffer[6] = 0x00;
				}
				else{
					mTextView[0].setText("车门未关闭！");
					//这里需要突出提醒，可以把画面整个变成红色，再打开蜂鸣器
					wBuffer[6] = 0x01;
				}
			}
			else if (intent.getAction().equals(action + SensorCollection.SensorID.LLUX))
			{
					mTextView[1].setText("光照："+SensorCollection.SensorData.SLLUX.llux + " llux");
					light(SensorCollection.SensorData.SLLUX.llux);
					
			}
			else if (intent.getAction().equals( action + SensorCollection.SensorID.SNOW))
			{
				if(SensorCollection.SensorData.SSNOW.status == 0x01){
					if(count == 0){
						linearLayout.setBackgroundResource(R.drawable.bg_rainy_on);
					}
					if(count == 1){
						linearLayout.setBackgroundResource(R.drawable.bg_rainy);
					}
					mTextView[2].setText("天气：下雨，雨刷工作。");
					}
				else{
					if(count == 0){
						linearLayout.setBackgroundResource(R.drawable.bg_intelligentgreenhouse_on);
					}
					if(count == 1){
						linearLayout.setBackgroundResource(R.drawable.bg_intelligentgreenhouse_off);
					}
					mTextView[2].setText("天气：晴朗");
				}
			}
			else if (intent.getAction().equals( action + SensorCollection.SensorID.SMOG)) {
				if(SensorCollection.SensorData.SSMOG.status == 0x01){
					mTextView[3].setText("烟雾浓度：" + "异常");
					wBuffer[6] = 0x01;
				}else{
					mTextView[3].setText("烟雾浓度：" + "正常");
					wBuffer[6] = 0x00;
				}
			}
			else if (intent.getAction().equals( action + SensorCollection.SensorID.SHT)) {
				if(SensorCollection.SensorData.SSHT.temp <= 25){
					mTextView[4].setText("温度："+SensorCollection.SensorData.SSHT.temp + " ℃");
				}else{
					mTextView[4].setText("高温，空调已经启动！");
				}
				if(SensorCollection.SensorData.SSHT.humi < 45){
					mTextView[5].setText("湿度："+SensorCollection.SensorData.SSHT.humi + " %");
				}else{
					mTextView[5].setText("湿度大，除湿器已经启动！");
				}
			}
		}
	}
	/**根据当前光照强第一判断是否需要开启灯光**/
	public void light(int lux){
		//开启灯光
		if(lux<50){ 
			count = 0;
			linearLayout.setBackgroundResource(R.drawable.bg_intelligentgreenhouse_on);
		}
		//关闭灯光
		else{
			count = 1;
			linearLayout.setBackgroundResource(R.drawable.bg_intelligentgreenhouse_off);
		}
			
		
	}
}