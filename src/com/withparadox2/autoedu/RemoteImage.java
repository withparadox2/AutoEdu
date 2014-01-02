package com.withparadox2.autoedu;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Created by Administrator on 14-1-2.
 */
public class RemoteImage {

	private Activity activity;
	private static final String PREFERENCE_NAME = "local_info";
	private static final String KEY_PIC_DATE = "key_pic_date";


	public RemoteImage(Activity activity){
		this.activity = activity;
	}
	public void setNewPicDate(String date){
		setStringInPreference(KEY_PIC_DATE, date);
	}

	private void setStringInPreference(String key, String val){
		SharedPreferences sharedPreferences = activity.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key, val);
		editor.commit();
	}

	private String getStringInPreference(String key){
		SharedPreferences sharedPreferences = activity.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		return sharedPreferences.getString(key, "20132014");
	}

	private boolean isReadyToFetchNewPic(String strFromTxTRemote, String strFromSPLocal){
		return !TextUtils.equals(strFromSPLocal, strFromTxTRemote);
	}
}
