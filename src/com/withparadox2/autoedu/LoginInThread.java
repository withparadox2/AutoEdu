package com.withparadox2.autoedu;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class LoginInThread extends Thread{

	private DefaultHttpClient httpClient;
	private Message msg;
	private Handler myHandler;
	private Context ctx;
	private int whichKindFlag;

	public LoginInThread(Handler handler, Context ctx, int whichKindFlag){
		this.myHandler = handler;
		httpClient = new DefaultHttpClient();
		this.ctx = ctx;
		this.whichKindFlag = whichKindFlag;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		switch (whichKindFlag) {
		case AutoEduActivity.NORMAL_LOGIN:
			normalLogin();
			break;
		case AutoEduActivity.FORCE_LOGIN:
			forceLogin();
			break;
		}
	}


	private void normalLogin(){
		sendMyMessage(AutoEduActivity.START_LOGIN);
		Header header = getLocationHeader(); 
		if(header == null){
			sendMyMessage(AutoEduActivity.NET_OK);
		}else{
			String locationHeaderValue = header.getValue().replace("-EDU", "520");
			postLogin(getLoginParasList(locationHeaderValue));
		}
	}


	public static Header getLocationHeader(){
		HttpResponse response = null;
		Header locationHeader = null;
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet("http://www.baidu.com");
		HttpParams httpParams = new BasicHttpParams();
		httpParams.setParameter("http.protocol.handle-redirects", false); 
		httpGet.setParams(httpParams);
		try {
			response = httpClient.execute(httpGet);
			locationHeader = response.getFirstHeader("Location");
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return locationHeader;
	}

	/**
	 * 返回一个包含三个post参数的列表，SSID，用户ip，用户名
	 * @param url 通过访问baidu跳转得到的网址
	 */
	private ArrayList<String> getLoginParasList(String url){
		Pattern pattern = Pattern.compile("=(.+?)&");
		Matcher matcher = pattern.matcher(url);
		ArrayList<String> list = new ArrayList<String>();
		list.add("CMCC520");
		while (matcher.find()) {
			list.add(matcher.group(1));
		}
		AutoEduActivity.saveValueInSp(ctx, list);
		return list; 
	}	

	/**
	 *如果强制刷新失败，可能是用户ip变化了，因此需要通过<br>
	 *normalLogin()来更新ip，重新提交一遍
	 */
	private void forceLogin(){
		List<String> list  = AutoEduActivity.getPostParasList(ctx);
		sendMyMessage(AutoEduActivity.START_LOGIN);
		if(!postLogin(list)){
			normalLogin();
		}
	}

	private boolean postLogin(List<String> list){
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);

		nameValuePairs.add(new BasicNameValuePair("ssid", list.get(0)));
		nameValuePairs.add(new BasicNameValuePair("wlanacname", list.get(1)));
		nameValuePairs.add(new BasicNameValuePair("wlanuserip", list.get(2)));
		HttpPost httpPost = new HttpPost("http://120.202.164.10:8080/portal/servlets/BusinessLoginServlet");
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			HttpResponse response = httpClient.execute(httpPost);
			String html = EntityUtils.toString(response.getEntity());
//			System.out.println(html);
			if(isLoginSuccess(html)){
				sendMyMessage(AutoEduActivity.LOGIN_SUCCESSED);
				return true;
			}else{
				sendMyMessage(AutoEduActivity.LOGIN_FAILED);
				return false;
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}



	private void sendMyMessage(int arg){
		msg = myHandler.obtainMessage();
		msg.arg1 = arg;
		msg.sendToTarget();
	}

	private boolean isLoginSuccess(String html){
		return html.contains("用户登录成功")||html.contains("返回码: 3022");
	}
}
