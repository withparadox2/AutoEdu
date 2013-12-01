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

import android.os.Handler;
import android.os.Message;

public class LoginInThread extends Thread{

	private DefaultHttpClient httpClient;
	private Message msg;
	private Handler myHandler;
	
	public LoginInThread(Handler handler){
		this.myHandler = handler;
		httpClient = new DefaultHttpClient();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		onStart();
	}
	
	private void onStart(){
		sendMyMessage(AutoEduActivity.START_LOGIN);
		Header header = getLocationHeader(); 
		if(header == null){
			sendMyMessage(AutoEduActivity.NET_OK);
		}else{
			postLogin(header);
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

	private ArrayList<String> getLoginParas(String url){
		Pattern pattern = Pattern.compile("=(.+?)&");
        Matcher matcher = pattern.matcher(url);
        ArrayList<String> list = new ArrayList<String>();
        while (matcher.find()) {
                 list.add(matcher.group(1));
        }
		return list; 
	}	
	
	private void postLogin(Header locationHeader){
		if(locationHeader != null){
    		System.out.println("location=" + locationHeader.getValue());
    		String locationHeaderValue = locationHeader.getValue().replace("-EDU", "520");
    		HttpResponse response;
    		ArrayList<String> list = getLoginParas(locationHeaderValue);
    		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("ssid", "CMCC520"));
            nameValuePairs.add(new BasicNameValuePair("wlanacname", list.get(0)));
            nameValuePairs.add(new BasicNameValuePair("wlanuserip", list.get(1)));
            HttpPost httpPost = new HttpPost("http://120.202.164.10:8080/portal/servlets/BusinessLoginServlet");
            try {
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
	            response = httpClient.execute(httpPost);
	            String html = EntityUtils.toString(response.getEntity());
	            if(isLoginSuccess(html)){
	            	sendMyMessage(AutoEduActivity.LOGIN_SUCCESSED);
	            }else{
	            	sendMyMessage(AutoEduActivity.LOGIN_FAILED);
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
		}
	}
	
	private void sendMyMessage(int arg){
		msg = myHandler.obtainMessage();
		msg.arg1 = arg;
		msg.sendToTarget();
	}
	
	private boolean isLoginSuccess(String html){
		return html.indexOf("用户登陆成功") != -1;
	}
}
