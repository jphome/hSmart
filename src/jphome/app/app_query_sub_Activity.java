package jphome.app;

import java.io.StringReader;

import javax.xml.parsers.SAXParserFactory;

import jphome.hsmart.R;
import jphome.hsmart.cmd;
import jphome.hsmart.gConfig;
import jphome.utils.DBUtil;
import jphome.utils.HttpUtil;
import jphome.xml.ContentHandler_switch_status;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.widget.Toast;

public class app_query_sub_Activity extends Activity {
	private WebView webView;
	private Handler mHandler = new Handler();
	static public String html_content = null;

	DBUtil dbHelper;
	static public SQLiteDatabase dbReadHandle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_query_sub);

		dbHelper = new DBUtil(app_query_sub_Activity.this, gConfig.db_Name, 1);
		dbReadHandle = dbHelper.getReadableDatabase();

		webView = (WebView) findViewById(R.id.webview_query_sub);
		/// webView.setBackgroundColor(0);
		webView.getSettings().setJavaScriptEnabled(true);
		/// 将图片下载阻塞
		webView.getSettings().setBlockNetworkImage(true);
		/// 提高渲染的优先级
		webView.getSettings().setRenderPriority(RenderPriority.HIGH);
		
		webView.addJavascriptInterface(new Object() {
			public void light(final String deviceId, final String sensorId,
					final byte status) {
				mHandler.post(new Runnable() {
					public void run() {
						System.out.println("cmd_light");
						cmd.light(deviceId, sensorId, status);

						// 重新发送查询命令，重新生成页面
						webView.clearAnimation();
						// webView.clearCache(true);
						// webView.clearHistory();
						// webView.clearView();

						System.out.println("webView.clear");
						genShowPage();
						System.out.println("genShowPage");
					}
				});
			}
		}, "cmd");

		genShowPage();

		// webView.loadData(html_content, "text/html", "utf-8");
		// webView.loadDataWithBaseURL(gConfig.baseURL_Resource, html_base,
		// "text/html", "utf-8", null);
		
	}

	protected void genShowPage() {
		Intent intent = getIntent();
		String url_base = intent.getStringExtra("url_base");
		String url = gConfig.URL.BASE_URL + url_base;
		System.out.println("url: " + url);
		String xml_base = HttpUtil.queryStringForGet(url);
		html_content = null;

		if (xml_base.equals("error")) {
			Toast.makeText(app_query_sub_Activity.this, "网络异常",
					Toast.LENGTH_SHORT).show();
		} else {
			try {
				// 创建一个SAXParserFactory
				SAXParserFactory factory = SAXParserFactory.newInstance();
				XMLReader reader = factory.newSAXParser().getXMLReader();
				// 将XMLReader设置内容处理器
				reader.setContentHandler(new ContentHandler_switch_status());
				// 开始解析文件
				reader.parse(new InputSource(new StringReader(xml_base)));
			} catch (Exception e) {
				System.out.println("xml parse error");
				e.printStackTrace();
			}
		}

		String html_complete = gConfig.html_head + html_content
				+ gConfig.html_tail;

		webView.loadDataWithBaseURL(gConfig.baseURL_Resource, html_complete,
				"text/html", "utf-8", null);
	}
}
