package com.withparadox2.autoedu;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AutoEduActivity extends Activity {
	/** Called when the activity is first created. */
	public final static int NET_OK = 0; 
	public final static int START_LOGIN = 1; 
	public final static int LOGIN_SUCCESSED = 2; 
	public final static int LOGIN_FAILED = 3;

	public final static int FORCE_LOGIN = 0;
	public final static int NORMAL_LOGIN = 1;

	private final static String SP_NAME = "temp_data";
	public final static String SSID = "ssid";
	public final static String WLANACNAME = "wlanacname";
	public final static String WLANUSERIP = "wlanuserip";

	private Context context;

	private TextView messageText;
	private LoginInThread loginInThread;
	private MyHandler myHandler;
	private Button refreshButton;

	private Timer myTimer;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		messageText = (TextView)findViewById(R.id.message_text);
		refreshButton = (Button)findViewById(R.id.force_refresh_button);
		refreshButton.setOnClickListener(new OnButtonClickListener());
		context = this;
		myHandler = new MyHandler(Looper.myLooper());
		if(isStartLogin()){
			loginInThread = new LoginInThread(myHandler, AutoEduActivity.this, NORMAL_LOGIN);
			loginInThread.start();
		}
		autoUpdateEdu();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(myTimer != null){
			myTimer.cancel();
		}
	}

	private class OnButtonClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(isStartLogin()){
				loginInThread = new LoginInThread(myHandler, AutoEduActivity.this, FORCE_LOGIN);
				loginInThread.start();
			}
		}
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
		if(wifiInfo != null && wifiInfo.getSSID().equals("CMCC-EDU")){
			return true;
		}else{
			return false;
		}
	}

	private boolean isStartLogin(){
		if(isWifiConnected(context) && isConnectToEdu()){
			return true;
		}else{
			messageText.setText("未连接到CMCC-EDU...");
			return false;
		}
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


	public static void saveValueInSp(Context ctx, List<String> list){
		SharedPreferences sp = ctx.getSharedPreferences(SP_NAME, MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(SSID, list.get(0));
		editor.putString(WLANACNAME, list.get(1));
		editor.putString(WLANUSERIP, list.get(2));
		editor.commit();
	}

	public static List<String> getPostParasList(Context ctx){
		List<String> list = new ArrayList<String>();
		SharedPreferences sp = ctx.getSharedPreferences(SP_NAME, MODE_PRIVATE);
		list.add(sp.getString(SSID, ""));
		list.add(sp.getString(WLANACNAME, ""));
		list.add(sp.getString(WLANUSERIP, ""));
		return list;
	}

	private void autoUpdateEdu(){
		if(myTimer == null){
			myTimer = new Timer();
		}
		myTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				loginInThread = new LoginInThread(myHandler, AutoEduActivity.this, FORCE_LOGIN);
				loginInThread.start();
			}
		}, 1000 * 60, 1000 * 60);
	}
}