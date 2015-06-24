package com.uptech.intelligentgreenhouse;

import com.uptech.sensorinfo.SensorFrameFilter;
import com.uptech.sensorinfo.SocketTheadManager;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class SensorCollectionApplication extends Application{

	private SensorFrameFilter filter;
	private SocketTheadManager socketThreadManager;
	private String ipstr,ssid;
	private int port;
	private static Context context;
	private static Handler handler = new Handler()
	{
		@Override
		public void handleMessage(android.os.Message msg) {
			Toast.makeText(context, (String)(msg.obj), Toast.LENGTH_SHORT).show();
		};
	};
	@Override
	public void onCreate() {
		super.onCreate();
		context = this.getApplicationContext();
	}
	/**
	 * @return the socketThreadManager
	 */
	public SocketTheadManager getSocketThreadManager() {
		return socketThreadManager;
	}
	/**
	 * @param clientSocketThread the clientSocketThread to set
	 */
	public void setSocketThreadManager(SocketTheadManager socketThreadManager) {
		this.socketThreadManager = socketThreadManager;
	}
	/**
	 * @return the SensorFrameFilter
	 */
	public SensorFrameFilter getSensorFrameFilter() {
		return filter;
	}
	/**
	 * @param filter the SensorFrameFilter to set
	 */
	public void setSensorFrameFilter(SensorFrameFilter filter) {
		this.filter = filter;
	}
	/**
	 * @return the ipstr
	 */
	public String getIpstr() {
		return ipstr;
	}
	/**
	 * @param ipstr the ipstr to set
	 */
	public void setIpstr(String ipstr) {
		this.ipstr = ipstr;
	}
	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}
	/**
	 * show message
	 * 
	 * @param str
	 *            the message to be shown
	 */
	public static void showMsg(String str) {
		handler.sendMessage(handler.obtainMessage(0, str));
	}
	/**
	 * @return the ssid
	 */
	public String getSsid() {
		return ssid;
	}
	/**
	 * @param ssid the ssid to set
	 */
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}
}
