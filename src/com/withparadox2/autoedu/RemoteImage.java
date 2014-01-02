package com.withparadox2.autoedu;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;

/**
 * Created by Administrator on 14-1-2.
 */
public class RemoteImage implements FetchJsonTask.Callback{

	private Activity activity;
	private ImageView imageView;
	private static final String PREFERENCE_NAME = "local_info";
	private static final String KEY_PIC_FLAG = "key_update_flag";
	private static final String KEY_PIC_URL = "key_pic_url";
	private static final String KEY_MAIN_TEXT_COLOR = "key_main_text_color";



	//Json keys
	private static final String JSON_PIC_URL = "picUrl";
	private static final String JSON_UPDATE_FLAG = "updateFlag";
	private static final String JSON_MAIN_TEXT_COLOR = "mainTextColor";



	public RemoteImage(Activity activity, ImageView imageView){
		this.activity = activity;
		this.imageView = imageView;
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

	private void decodeJsonAndProcess(String json){
		try {
			JSONObject jsonObject = new JSONObject(json);
//			if(isReadyToFetchNewPic(jsonObject.getString(JSON_UPDATE_FLAG), getStringInPreference(KEY_PIC_FLAG))){
				setStringInPreference(KEY_PIC_URL, jsonObject.getString(JSON_PIC_URL));
				setStringInPreference(KEY_PIC_FLAG, jsonObject.getString(JSON_UPDATE_FLAG));
				setStringInPreference(KEY_MAIN_TEXT_COLOR, jsonObject.getString(JSON_MAIN_TEXT_COLOR));
				startFetchImage();
//			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void startFetchImage(){
		imageView.setTag(getStringInPreference(KEY_PIC_URL));
		new FetchNewPicTask().execute(imageView);
	}

	public void startFetchJson(){
		new FetchJsonTask(this).execute();
	}


	@Override
	public void onPostExecute(String s) {
		decodeJsonAndProcess(s);
	}
}
