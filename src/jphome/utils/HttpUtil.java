package jphome.utils;

import java.io.IOException;

import jphome.hsmart.AppConstant;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class HttpUtil {
	// 声明Base URL 常量
	static int rc;
	
	// 通过URL获得HttpGet对象
	public static HttpGet getHttpGet(String url){
		HttpGet request = new HttpGet(url);
		 return request;
	}
	// 通过URL获得HttpPost对象
	public static HttpPost getHttpPost(String url){
		 HttpPost request = new HttpPost(url);
		 return request;
	}
	// 通过HttpGet获得HttpResponse对象
	public static HttpResponse getHttpResponse(HttpGet request) throws ClientProtocolException, IOException{
		HttpResponse response = new DefaultHttpClient().execute(request);
		return response;
	}
	// 通过HttpPost获得HttpResponse对象	
	public static HttpResponse getHttpResponse(HttpPost request) throws ClientProtocolException, IOException{
		HttpResponse response = new DefaultHttpClient().execute(request);
		return response;
	}
	
	// 通过URL发送post请求，返回请求结果
	public static String queryStringForPost(String url){
		// 获得HttpPost实例
		HttpPost request = HttpUtil.getHttpPost(url);
		String result = null;
		try {
			// 获得HttpResponse实例
			HttpResponse response = HttpUtil.getHttpResponse(request);
			// 判断是否请求成功
			rc = response.getStatusLine().getStatusCode();
			if(AppConstant.HS_OK == rc) {
				// 获得返回结果
				result = EntityUtils.toString(response.getEntity());
				return result;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			result = "网络异常！";
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			result = "网络异常！";
			return result;
		}
        return null;
    }
	
	// 通过URL发送get请求，返回请求结果
	public static  String queryStringForGet(String url){
		// 获得HttpGet实例
		HttpGet request = HttpUtil.getHttpGet(url);
		String result = null;
		try {
			// 获得HttpResponse实例
			HttpResponse response = HttpUtil.getHttpResponse(request);
			// 判断是否请求成功
			if(AppConstant.HS_OK == response.getStatusLine().getStatusCode()){
				// 获得返回结果
				result = EntityUtils.toString(response.getEntity());
				return result;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			result = "网络异常！";
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			result = "网络异常！";
			return result;
		}
        return null;
    }
	
	// 通过HttpPost发送post请求，返回请求结果
	public static String queryStringForPost(HttpPost request) {
		String result = null;
		try {
			// 获得HttpResponse实例
			HttpResponse response = HttpUtil.getHttpResponse(request);
			// 判断是否请求成功
			if(AppConstant.HS_OK == response.getStatusLine().getStatusCode()) {
				// 获得返回结果
				result = EntityUtils.toString(response.getEntity());
				return result;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			result = "网络异常！";
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			result = "网络异常！";
			return result;
		}
        return null;
    }
	
	@SuppressWarnings("finally")
	public static boolean cmdForGet(String url) {
		boolean result = false;
		// 获得HttpGet实例
		HttpGet request = HttpUtil.getHttpGet(url);
		try {
			// 获得HttpResponse实例
			HttpResponse response = HttpUtil.getHttpResponse(request);			
			// 判断是否请求成功
			rc = response.getStatusLine().getStatusCode();
			if(AppConstant.HS_OK == rc) {
				// 获得返回结果
				result = true;
			} else {
				System.out.println("GET return " + rc);				
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			System.out.println("网络异常！");
			result = false;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("网络异常！");
			result = false;
		} finally {
			return result;
		}
	}
}


