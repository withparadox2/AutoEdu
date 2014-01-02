package com.withparadox2.autoedu;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.*;

/**
 * Created by Administrator on 14-1-2.
 */
public class FileUtil {

	private Context context;
	private static final String TAG = FileUtil.class.getName();

	public FileUtil(Context context){
		this.context = context;
	}

	private File getOutputMediaFile(){
//		File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
//			+ "/Android/data/"
//			+ context.getPackageName()
//			+ "/Files");
//
//		if (! mediaStorageDir.exists()){
//			if (! mediaStorageDir.mkdirs()){
//				return null;
//			}
//		}
//		File mediaFile;
		String mImageName="azimeng.jpg";
//		Log.d(TAG, "========"+mediaStorageDir.getPath() + File.separator + mImageName);
//		mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
//		return mediaFile;
		ContextWrapper cw = new ContextWrapper(context);
		File directory = cw.getDir("media", Context.MODE_PRIVATE);
		return new File(directory.getPath() + File.separator + mImageName);
	}

	public void storeImage(Bitmap image) {
		File pictureFile = getOutputMediaFile();
		if (pictureFile == null) {
			return;
		}
		try {
			FileOutputStream fos = new FileOutputStream(pictureFile);
			image.compress(Bitmap.CompressFormat.JPEG, 90, fos);
			fos.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

	public Bitmap getImage(){
		try {
			return BitmapFactory.decodeStream(new FileInputStream(getOutputMediaFile()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}
