package com.withparadox2.autoedu;

import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.TextView;

public class AutoEduActivity extends Activity {
    /** Called when the activity is first created. */
	public final static int NET_OK = 0; 
	public final static int START_LOGIN = 1; 
	public final static int LOGIN_SUCCESSED = 2; 
	public final static int LOGIN_FAILED = 3;
	
	private Context context;
	
	private TextView messageText;
	private LoginInThread loginInThread;
	private MyHandler myHandler;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        messageText = (TextView)findViewById(R.id.message_text);
        context = this;
        myHandler = new MyHandler(Looper.myLooper());
      
    }


	/**
	 * 访问百度，通过是否重定向来判断网络有没有连接，重定向则表明网络没有连接...
	 */
	private boolean isNetConnected() {  
		if(LoginInThread.getLocationHeader()==null){
			System.out.println("asdfasdf");
		}else{
			System.out.println("vvvvvvvv");
		}
		return LoginInThread.getLocationHeader()==null;
    }
	
	public boolean isWifiConnected(Context context) { 
		if (context != null) { 
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context 
		.getSystemService(Context.CONNECTIVITY_SERVICE); 
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager 
		.getNetworkInfo(ConnectivityManager.TYPE_WIFI); 
			if (mWiFiNetworkInfo != null) { 
				return mWiFiNetworkInfo.isAvailable(); 
			} 
		} 
		return false; 
	}
    
	private boolean isConnectToEdu(){
		WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//		messageText.setText(wifiInfo.getSSID());
		if(wifiInfo != null && wifiInfo.getSSID().equals("CMCC-EDU")){
			return true;
		}else{
			return false;
		}
	}
    
	private boolean isStartLogin(){
		boolean result = false;
		if(isWifiConnected(this)){
			if(isConnectToEdu()){
				if(!isNetConnected()){
					result = true;
				}else{
					messageText.setText(getStr(R.string.login_successed));
				}
			}else{
				messageText.setText("未连接到CMCC-EDU...");
			}
		}else{
			messageText.setText("未连接到CMCC-EDU...");
		}
		return result;
	}
	
	class MyHandler extends Handler{

		public MyHandler(Looper looper){
			super(looper);
		}
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.arg1) {
			case START_LOGIN:
				messageText.setText(getStr(R.string.start_login));
				break;
			case LOGIN_SUCCESSED:
				messageText.setText(getStr(R.string.login_successed));
				break;
			case NET_OK:
				messageText.setText(getStr(R.string.login_successed));
				break;				
			case LOGIN_FAILED:
				messageText.setText(getStr(R.string.login_failed));
				break;				
			}
		}
		
	}
	
	private String getStr(int id){
		return context.getResources().getString(id);
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
	   if(hasFocus){  
		  if(isStartLogin()){
        	  loginInThread = new LoginInThread(myHandler);
        	  loginInThread.start();
          }
	   }
	}
}