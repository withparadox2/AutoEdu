package com.withparadox2.autoedu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Message;

import android.os.Handler;
import android.util.Log;

import java.util.List;

/**
 * Created by withparadox2 on 13-12-19.
 */
public class WifiHelper {
	private WifiManager wifiManager;
	private Context context;
	private Handler myHandler;

	public  WifiHelper(Context context, Handler myHandler){
		wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		this.context = context;
		this.myHandler = myHandler;
	}

	public void openWifiByTest(){
		if(!wifiManager.isWifiEnabled()){
			sendMyMessage(AutoEduActivity.OPENNING_WIFI);
			openWifi();
		}else{
			sendMyMessage(AutoEduActivity.OPENNING_WIFI_SUCCEED);
		}
	}

	public void scanWifi(){
		sendMyMessage(AutoEduActivity.SCAN_WIFI_START);
		wifiManager.startScan();
		context.registerReceiver(new WifiReceiver(wifiManager), new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	}

	private void openWifi(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				wifiManager.setWifiEnabled(true);
				while (wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED){
					Thread.currentThread();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				sendMyMessage(AutoEduActivity.OPENNING_WIFI_SUCCEED);
			}
		});
	}



	private class WifiReceiver extends BroadcastReceiver {
		private WifiManager wifiManager;

		public WifiReceiver(WifiManager wifiManager){
			this.wifiManager = wifiManager;
		}
		@Override
		public void onReceive(Context context, Intent intent) {
			if(isConnectToEdu()){
				sendMyMessage(AutoEduActivity.CONNECT_TO_EDU_SUCCEED);
			}else{
				doAfterScanOver();
			}
		}
	}

	private void doAfterScanOver(){
		sendMyMessage(AutoEduActivity.SCAN_WIFI_OVER);
		List<ScanResult> scanResults = wifiManager.getScanResults();
		if(scanResults != null && scanResults.size() > 0){
			for (ScanResult sItem : scanResults){
				if(sItem.SSID.equals(AutoEduActivity.SSID)){
					sendMyMessage(AutoEduActivity.SCAN_EDU_SUCCEED);
					int netID = wifiManager.addNetwork(getConfiguration());
					wifiManager.enableNetwork(netID, false);
					sendMyMessage(AutoEduActivity.CONNECT_TO_EDU_START);
					wifiManager.reconnect();
					while (!isConnectToEdu()){
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					sendMyMessage(AutoEduActivity.CONNECT_TO_EDU_SUCCEED);
					return;
				}
				sendMyMessage(AutoEduActivity.SCAN_EDU_FAILED);
			}
		}
	}

	private boolean isConnectToEdu(){
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		if(wifiInfo != null && wifiInfo.getSSID().equals(AutoEduActivity.SSID)){
			return true;
		}else{
			return false;
		}
	}

	private boolean isWifiConnected(Context context) {
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

	public boolean isStartLogin(){
		if(isWifiConnected(context) && isConnectToEdu()){
			return true;
		}else{
			return false;
		}
	}

	private WifiConfiguration getConfiguration(){
		if(getExistConfigurationOrNull(AutoEduActivity.SSID) != null){
			return getExistConfigurationOrNull(AutoEduActivity.SSID);
		}else {
			WifiConfiguration config = new WifiConfiguration();
			config.SSID = AutoEduActivity.SSID;
			config.preSharedKey = "\"" + "12345678" + "\"";
			return config;
		}
	}


	private WifiConfiguration getExistConfigurationOrNull(String SSID){
		List<WifiConfiguration> configurations = wifiManager.getConfiguredNetworks();
		for (WifiConfiguration configuration : configurations){
			if(configuration.SSID.equals(SSID)){
				return  configuration;
			}
		}
		return null;
	}

	private void sendMyMessage(int arg){
		Message msg = myHandler.obtainMessage();
		msg.arg1 = arg;
		msg.sendToTarget();
	}
}
