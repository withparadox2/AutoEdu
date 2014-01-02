package com.withparadox2.autoedu;

import android.os.AsyncTask;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by Administrator on 14-1-2.
 */
public class FetchJsonTask extends AsyncTask<Void, Void, String>{
	interface Callback{
		public void onPostExecute(String s);
	}

	Callback callback;

	public FetchJsonTask(Callback callback){
		this.callback = callback;
	}

	@Override
	protected String doInBackground(Void... params) {
		HttpGet httpGet = new HttpGet("http://autoedu.sinaapp.com");
		DefaultHttpClient httpClient = new DefaultHttpClient();
		String json = null;
		try {
			HttpResponse response = httpClient.execute(httpGet);
			json = EntityUtils.toString(response.getEntity());
			System.out.println(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return json;
	}

	@Override
	protected void onPostExecute(String s) {
		if(s != null){
			callback.onPostExecute(s);
		}
	}
}
