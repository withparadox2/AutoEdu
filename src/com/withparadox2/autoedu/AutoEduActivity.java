package com.withparadox2.autoedu;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.*;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
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
	public final static int OPENNING_WIFI = 4;
	public final static int OPENNING_WIFI_SUCCEED = 5;
	public final static int SCAN_WIFI_START = 6;
	public final static int SCAN_WIFI_OVER = 7;
	public final static int SCAN_EDU_SUCCEED = 8;
	public final static int SCAN_EDU_FAILED = 9;
	public final static int CONNECT_TO_EDU_START = 10;
	public final static int CONNECT_TO_EDU_OVER = 11;
	public final static int CONNECT_TO_EDU_SUCCEED = 12;






	public final static int FORCE_LOGIN = 0;
	public final static int NORMAL_LOGIN = 1;

	private final static String SP_NAME = "temp_data";
	public final static String SSID_SP = "ssid";
	public final static String WLANACNAME = "wlanacname";
	public final static String WLANUSERIP = "wlanuserip";
	public final static String SSID = "\"" + "360-ZS1BD0" + "\"";

	private Context context;

	private TextView messageText;
	private LoginInThread loginInThread;
	private MyHandler myHandler;
	private Button refreshButton;

	private Timer myTimer;
	private WifiHelper wifiHelper;

	private final static String TAG = "AutoEdu";


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		messageText = (TextView)findViewById(R.id.message_text);
		refreshButton = (Button)findViewById(R.id.force_refresh_button);
		refreshButton.setOnClickListener(new OnButtonClickListener());
		context = this;
		myHandler = new MyHandler(Looper.myLooper());
		wifiHelper = new WifiHelper(context, myHandler);
		if(!wifiHelper.isStartLogin()){
			wifiHelper.openWifiByTest();
		}else{
			loginInThread = new LoginInThread(myHandler, AutoEduActivity.this, NORMAL_LOGIN);
			loginInThread.start();
		}

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
			if(wifiHelper.isStartLogin()){
				loginInThread = new LoginInThread(myHandler, AutoEduActivity.this, FORCE_LOGIN);
				loginInThread.start();
			}else {
				wifiHelper.openWifiByTest();
			}
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
				case OPENNING_WIFI:
					messageText.setText("正在打开wifi...");
					break;
				case OPENNING_WIFI_SUCCEED:
					messageText.setText("成功打开wifi！");
					wifiHelper.scanWifi();
					break;
				case SCAN_WIFI_START:
					messageText.setText("正在扫描可用wifi...");
					break;
				case SCAN_WIFI_OVER:
					break;
				case SCAN_EDU_SUCCEED:
					messageText.setText("成功扫描到CMCC-EDU！");
					break;
				case CONNECT_TO_EDU_START:
					messageText.setText("正在连接CMCC-EDU...");
					break;
				case CONNECT_TO_EDU_SUCCEED:
					messageText.setText("成功连接到CMCC-EDU！");
					autoUpdateEdu();
					break;
				case SCAN_EDU_FAILED:
					messageText.setText("未扫描到CMCC-EDU :-(");
					refreshButton.setText("重新扫描");
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
		editor.putString(SSID_SP, list.get(0));
		editor.putString(WLANACNAME, list.get(1));
		editor.putString(WLANUSERIP, list.get(2));
		editor.commit();
	}

	public static List<String> getPostParasList(Context ctx){
		List<String> list = new ArrayList<String>();
		SharedPreferences sp = ctx.getSharedPreferences(SP_NAME, MODE_PRIVATE);
		list.add(sp.getString(SSID_SP, ""));
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
				if(wifiHelper.isStartLogin()){
					loginInThread = new LoginInThread(myHandler, AutoEduActivity.this, FORCE_LOGIN);
					loginInThread.start();
				}else{
					wifiHelper.openWifiByTest();
				}

			}
		}, 1000 * 60, 1000 * 60);
	}
}