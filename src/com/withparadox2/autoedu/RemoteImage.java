package com.withparadox2.autoedu;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 14-1-2.
 */
public class RemoteImage implements FetchJsonTask.Callback{

	private Activity activity;
	private ImageView imageView;
	private TextView textView;
	public static final String PREFERENCE_NAME = "local_info";
	public static final String KEY_PIC_FLAG = "key_update_flag";
	public static final String KEY_PIC_URL = "key_pic_url";
	public static final String KEY_MAIN_TEXT_COLOR = "key_main_text_color";
	public static final String KEY_AZIMENG_TEXT = "key_azimeng_text";
	public static final String KEY_AZIMENG_TEXT_SIZE = "key_azimeng_text_size";
	public static final String KEY_AZIMENG_TEXT_COLOR = "key_azimeng_text_color";
	public static final String KEY_AZIMENG_TEXT_LOCATION_X = "key_azimeng_text_location_x";
	public static final String KEY_AZIMENG_TEXT_LOCATION_Y = "key_azimeng_text_location_y";



	//Json keys
	private static final String JSON_PIC_URL = "picUrl";
	private static final String JSON_UPDATE_FLAG = "updateFlag";
	private static final String JSON_MAIN_TEXT_COLOR = "mainTextColor";
	private static final String JSON_AZIMENG_TEXT = "azimengText";
	private static final String JSON_AZIMENG_TEXT_COLOR = "azimengTextColor";
	private static final String JSON_AZIMENG_TEXT_SIZE = "azimengTextSize";
	private static final String JSON_AZIMENG_TEXT_LOCATION_X = "azimengTextLocationX";
	private static final String JSON_AZIMENG_TEXT_LOCATION_Y = "azimengTextLocationY";






	public RemoteImage(Activity activity, ImageView imageView, TextView textView){
		this.activity = activity;
		this.imageView = imageView;
		this.textView = textView;
	}

	public void setAzimengText(){
		if(!TextUtils.isEmpty(getStringInPreference(KEY_AZIMENG_TEXT))){
			textView.setText(getStringInPreference(KEY_AZIMENG_TEXT));
			textView.setTextColor(Color.parseColor(getStringInPreference(RemoteImage.KEY_AZIMENG_TEXT_COLOR)));
			textView.setTextSize(Util.dpToPx(activity, Integer.parseInt(getStringInPreference(RemoteImage.KEY_AZIMENG_TEXT_SIZE))));
			FrameLayout.LayoutParams params =
					new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.NO_GRAVITY);
			params.leftMargin = Util.dpToPx(activity, Integer.parseInt(getStringInPreference(RemoteImage.KEY_AZIMENG_TEXT_LOCATION_X)));
			params.topMargin  = Util.dpToPx(activity, Integer.parseInt(getStringInPreference(RemoteImage.KEY_AZIMENG_TEXT_LOCATION_Y)));
			textView.setLayoutParams(params);
		}else {
			textView.setText("");
		}
	}

	private void setStringInPreference(String key, String val){
		SharedPreferences sharedPreferences = activity.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key, val);
		editor.commit();
	}

	public String getStringInPreference(String key){
		return getStringInPreferenceByDef(key, "");
	}

	public String getStringInPreferenceByDef(String key, String def){
		SharedPreferences sharedPreferences = activity.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		return sharedPreferences.getString(key, def);
	}

	private boolean isReadyToFetchNewPic(String strFromTxTRemote, String strFromSPLocal){
		return !TextUtils.equals(strFromSPLocal, strFromTxTRemote);
	}

	private void decodeJsonAndProcess(String json){
		try {
			JSONObject jsonObject = new JSONObject(json);
			if(isReadyToFetchNewPic(jsonObject.getString(JSON_UPDATE_FLAG), getStringInPreference(KEY_PIC_FLAG))){
				setStringInPreference(KEY_PIC_URL, jsonObject.getString(JSON_PIC_URL));
				setStringInPreference(KEY_PIC_FLAG, jsonObject.getString(JSON_UPDATE_FLAG));
				setStringInPreference(KEY_MAIN_TEXT_COLOR, jsonObject.getString(JSON_MAIN_TEXT_COLOR));
				setStringInPreference(KEY_AZIMENG_TEXT, jsonObject.getString(JSON_AZIMENG_TEXT));
				setStringInPreference(KEY_AZIMENG_TEXT_COLOR, jsonObject.getString(JSON_AZIMENG_TEXT_COLOR));
				setStringInPreference(KEY_AZIMENG_TEXT_SIZE, jsonObject.getString(JSON_AZIMENG_TEXT_SIZE));
				setStringInPreference(KEY_AZIMENG_TEXT_LOCATION_X,jsonObject.getString(JSON_AZIMENG_TEXT_LOCATION_X));
				setStringInPreference(KEY_AZIMENG_TEXT_LOCATION_Y,jsonObject.getString(JSON_AZIMENG_TEXT_LOCATION_Y));
				startFetchImage();
				setAzimengText();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void startFetchImage(){
		imageView.setTag(getStringInPreference(KEY_PIC_URL));
		new FetchNewPicTask(activity).execute(imageView);
	}

	public void startFetchJson(){
		new FetchJsonTask(this).execute();
	}


	@Override
	public void onPostExecute(String s) {
		decodeJsonAndProcess(s);
	}
}
