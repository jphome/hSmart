package jphome.app;

import jphome.hsmart.R;
import jphome.hsmart.cmd;
import jphome.hsmart.gConfig;
import jphome.utils.HttpUtil;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebView;

/*
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" >
<html xmlns="http://www.w3.org/1999/xhtml" >
<head>
	<title>%s</title>
	<link rel="stylesheet" type="text/css" href="http://192.168.1.168/css/demo.css" />
</head>

<body>
	<h1>Cmd_global</h1>
	<a onclick="window.demo.clickOnAndroid(1)" >
		<img src="light_on.png" alt="a ha" />
	</a>
	<a onclick="window.demo.clickOnAndroid(0)" >
		<img src="light_off.png" alt=\"a ha" />
	</a>
</body>
</html>
*/
public class app_cmd_Activity extends Activity {
	private WebView webView;
	private Handler mHandler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_cmd);

		webView = (WebView)findViewById(R.id.webview_cmd);
//		webView.setBackgroundColor(0);
		webView.getSettings().setJavaScriptEnabled(true);
		
		// 将一个java对象绑定到javascript中 用window.demo.clickOnAndroid()调用
		webView.addJavascriptInterface(new Object() {
			public void light(final byte status) {
				mHandler.post(new Runnable() {
					public void run() {
						System.out.println("cmd_light");
						cmd.light(status, (byte)0);
					}
				});
			}
		}, "cmd");
		
/*		http://192.168.1.168:8081/cmd_global
*/		String url = gConfig.URL.BASE_URL + "cmd_global";

		String html_base = HttpUtil.queryStringForGet(url);
		webView.loadDataWithBaseURL(gConfig.baseURL_Resource, html_base, "text/html", "utf-8", null);
	}
	
	private void webHtml(String url) {
		try {
			webView.loadUrl(url);
		} catch (Exception ex) {
			// TODO: handle exception
			ex.printStackTrace();
		}
	}
	
	private void webImage(String url) {
		try {
			webView.loadUrl(url);
		} catch (Exception ex) {
			// TODO: handle exception
			ex.printStackTrace();
		}
	}
	
	private void localImage(String url) {
		try {
			webView.loadUrl(url);
		} catch (Exception ex) {
			// TODO: handle exception
			ex.printStackTrace();
		}
	}
}











