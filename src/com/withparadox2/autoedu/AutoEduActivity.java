package com.withparadox2.autoedu;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.Inflater;

import android.app.Activity;
import android.content.*;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
	public final static int OPENNING_WIFI_FAILED = 13;
	public final static int CONNECT_TO_EDU_FAILED = 14;

	public final static int START_FETCH_JSON = 30;






	public final static int FORCE_LOGIN = 0;
	public final static int NORMAL_LOGIN = 1;

	private final static String SP_NAME = "temp_data";
	public final static String SSID_SP = "ssid";
	public final static String WLANACNAME = "wlanacname";
	public final static String WLANUSERIP = "wlanuserip";
//	public final static String SSID = "\"" + "360-ZS1BD0" + "\"";
//	public final static String SSID_PLAIN = "360-ZS1BD0";
	public final static String SSID = "\"" + "CMCC-EDU" + "\"";
	public final static String SSID_PLAIN = "CMCC-EDU";


	private Context context;

	private TextView messageText;
	private LoginInThread loginInThread;
	private MyHandler myHandler;
	private Button refreshButton;

	private Timer myTimer;
	private WifiHelper wifiHelper;

	private final static String TAG = "AutoEdu";

	private RemoteImage remoteImage;
	private ImageView imageView;


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
//			wifiHelper.openWifiByTest();
			messageText.setText("未连接edu...");
		}else{
			loginInThread = new LoginInThread(myHandler, AutoEduActivity.this, NORMAL_LOGIN);
			loginInThread.start();
		}
		autoUpdateEdu();
		imageView = (ImageView) findViewById(R.id.imageview_background);
		remoteImage = new RemoteImage(this, imageView);
		setBackgroundImage();
	}



	private void setBackgroundImage(){
		Bitmap bitmap = new FileUtil(this).getImage();
		if(bitmap != null){
			imageView.setImageBitmap(bitmap);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(myTimer != null){
			myTimer.cancel();
		}

		wifiHelper.closeWifi();

	}

	private class OnButtonClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(wifiHelper.isStartLogin()){
				loginInThread = new LoginInThread(myHandler, AutoEduActivity.this, FORCE_LOGIN);
				loginInThread.start();
			}else {
//				wifiHelper.openWifiByTest();
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
					messageText.setText(getStr(R.string.openning_wifi));
					break;
				case OPENNING_WIFI_SUCCEED:
					messageText.setText(getStr(R.string.open_wifi_successed));
					wifiHelper.scanWifi();
					break;
				case OPENNING_WIFI_FAILED:
					messageText.setText(getStr(R.string.open_wifi_failed));
					refreshButton.setText(getStr(R.string.reopen_wifi));
					break;
				case SCAN_WIFI_START:
					messageText.setText(getStr(R.string.scanning_useful_wifi));
					break;
				case SCAN_WIFI_OVER:
					break;
				case SCAN_EDU_SUCCEED:
					messageText.setText(getStr(R.string.scan_edu_successed));
					break;
				case CONNECT_TO_EDU_START:
					messageText.setText(getStr(R.string.connecting_edu));
					break;
				case CONNECT_TO_EDU_SUCCEED:
					messageText.setText(getStr(R.string.connect_edu_successed));
					forceLogin();
					break;
				case CONNECT_TO_EDU_FAILED:
					messageText.setText(getStr(R.string.connect_edu_failed));
					refreshButton.setText(getStr(R.string.reconnect));
					break;
				case SCAN_EDU_FAILED:
					messageText.setText(getStr(R.string.scan_edu_failed));
					refreshButton.setText(getStr(R.string.rescan));
					break;
				case START_FETCH_JSON:
					remoteImage.startFetchJson();
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
			myTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					if(wifiHelper.isStartLogin()){
						loginInThread = new LoginInThread(myHandler, AutoEduActivity.this, FORCE_LOGIN);
						loginInThread.start();
					}else{
					}
					sendMyMessage(START_FETCH_JSON);

				}
			}, 1000 * 60, 1000 * 60);
		}
	}

	private void sendMyMessage(int arg){
		Message msg = myHandler.obtainMessage();
		msg.arg1 = arg;
		msg.sendToTarget();
	}

	private void forceLogin(){
		if(wifiHelper.isStartLogin()){
			loginInThread = new LoginInThread(myHandler, AutoEduActivity.this, FORCE_LOGIN);
			loginInThread.start();
		}
	}
}