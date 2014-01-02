package com.withparadox2.autoedu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 14-1-2.
 */
public class FetchNewPicTask extends AsyncTask<ImageView, Void, Bitmap>{

	private ImageView imageView;
	private Context context;

	public FetchNewPicTask(Context context){
		this.context = context;

	}

	@Override
	protected Bitmap doInBackground(ImageView... params) {
		this.imageView = params[0];
		return downLoadImageByUrl((String)imageView.getTag());
	}

	@Override
	protected void onPostExecute(Bitmap bitmap) {
		if (bitmap != null){
			imageView.setImageBitmap(bitmap);
			new FileUtil(context).storeImage(bitmap);
		}
	}

	private Bitmap downLoadImageByUrl(String str){
		InputStream inputStream = null;
		Bitmap bitmap = null;
		try {
			URL url = new URL(str);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			inputStream = connection.getInputStream();
			bitmap = BitmapFactory.decodeStream(inputStream);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null){
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return bitmap;
	}
}
