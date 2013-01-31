package jphome.app;

import jphome.hsmart.R;
import jphome.hsmart.gConfig;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebView;

public class app_query_Activity extends Activity {
	final byte QUERY_TEMPERATURE = 0;
	final byte QUERY_LIGHT_STATUS = 1;

	private WebView webView;
	private Handler mHandler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_query);
		
		webView = (WebView)findViewById(R.id.webview_query);
//		webView.setBackgroundColor(0);
		webView.getSettings().setJavaScriptEnabled(true);
		
		// 将一个java对象绑定到javascript中 用window.demo.clickOnAndroid()调用
		webView.addJavascriptInterface(new Object() {
			public void entity(final byte type) {
				mHandler.post(new Runnable() {
					public void run() {
						String url_base = null;
						switch(type) {
						case QUERY_TEMPERATURE:
							/* http://www.monitor.com:8081/query_temperature */
							url_base = "query_temperature";
							break;
						case QUERY_LIGHT_STATUS:
							/* http://www.monitor.com:8081/query_light_status */
							url_base = "query_switch_status";
							break;
						default:
							return ;
						}
						
						Intent intent = new Intent(app_query_Activity.this, app_query_sub_Activity.class);
						intent.putExtra("url_base", url_base);
						startActivity(intent);
					}
				});
			}
		}, "query");

		webView.loadUrl("file:///android_asset/html/query.html");
	}
}












