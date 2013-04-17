package jphome.app;

import java.io.StringReader;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.SAXParserFactory;

import jphome.hsmart.R;
import jphome.hsmart.gConfig;
import jphome.utils.DBUtil;
import jphome.utils.HttpUtil;
import jphome.xml.ContentHandler_temperature;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.Toast;

public class app_query_temperature_Activity extends Activity {
	private Timer timer = new Timer();
	private TimerTask task;
	private Handler handler;
	private String title = "Temperature";
	private XYSeries series;
	private XYMultipleSeriesDataset mDataset;
	private GraphicalView chart;
	private XYMultipleSeriesRenderer renderer;
	private Context context;
	private int addX = -1, addY;

	DBUtil dbHelper;
	static public SQLiteDatabase dbWriteHandle;
	SQLiteDatabase dbReadHandle;

	int[] xv = new int[100];
	int[] yv = new int[100];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_temperature);

		dbHelper = new DBUtil(app_query_temperature_Activity.this,
				gConfig.db_Name, 1);
		dbWriteHandle = dbHelper.getWritableDatabase();
		dbReadHandle = dbHelper.getReadableDatabase();

		context = getApplicationContext();
		// 这里获得main界面上的布局，下面会把图表画在这个布局里面
		LinearLayout layout = (LinearLayout) findViewById(R.id.query_chart);

		// 这个类用来放置曲线上的所有点，是一个点的集合，根据这些点画出曲线
		series = new XYSeries(title);

		// 创建一个数据集的实例，这个数据集将被用来创建图表
		mDataset = new XYMultipleSeriesDataset();

		// 将点集添加到这个数据集中
		mDataset.addSeries(series);

		// 以下都是曲线的样式和属性等等的设置，renderer相当于一个用来给图表做渲染的句柄
		int color = Color.GREEN;
		PointStyle style = PointStyle.CIRCLE;
		renderer = buildRenderer(color, style, true);

		// 设置好图表的样式
		setChartSettings(renderer, "X", "Y", 0, 100, 0, 50, Color.WHITE,
				Color.WHITE);

		// 生成图表
		chart = ChartFactory.getLineChartView(context, mDataset, renderer);

		// 将图表添加到布局中去
		layout.addView(chart, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));

		// 这里的Handler实例将配合下面的Timer实例，完成定时更新图表的功能
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// 刷新图表
				updateChart();
				super.handleMessage(msg);
			}
		};
		task = new TimerTask() {
			public void run() {
				Message message = new Message();
				message.what = 1;
				handler.sendMessage(message);
			}
		};
		// 1秒后开始进行定时器调度，5秒为循环周期
		timer.schedule(task, 1, 5*1000);
	}

	public void onDestroy() {
		// 当结束程序时关掉Timer
		timer.cancel();
		super.onDestroy();
	}

	protected XYMultipleSeriesRenderer buildRenderer(int color,
			PointStyle style, boolean fill) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

		// 设置图表中曲线本身的样式，包括颜色、点的大小以及线的粗细等
		XYSeriesRenderer r = new XYSeriesRenderer();
		r.setColor(color);
		r.setPointStyle(style);
		r.setFillPoints(fill);
		r.setLineWidth(3);
		renderer.addSeriesRenderer(r);

		return renderer;
	}

	protected void setChartSettings(XYMultipleSeriesRenderer renderer,
			String xTitle, String yTitle, double xMin, double xMax,
			double yMin, double yMax, int axesColor, int labelsColor) {
		// 有关对图表的渲染可参看api文档
		renderer.setChartTitle(title);
		renderer.setXTitle(xTitle);
		renderer.setYTitle(yTitle);
		renderer.setXAxisMin(xMin);
		renderer.setXAxisMax(xMax);
		renderer.setYAxisMin(yMin);
		renderer.setYAxisMax(yMax);
		renderer.setAxesColor(axesColor);
		renderer.setLabelsColor(labelsColor);
		renderer.setShowGrid(true);
		renderer.setGridColor(Color.GREEN);
		renderer.setXLabels(20);
		renderer.setYLabels(10);
		renderer.setXTitle("时间/h");
		renderer.setYTitle("温度/c");
		renderer.setYLabelsAlign(Align.RIGHT);
		renderer.setPointSize((float) 2);
		renderer.setShowLegend(false);
	}

	private void updateChart() {
		String url = gConfig.URL.BASE_URL + "query_temperature";
		String xml_base = HttpUtil.queryStringForGet(url);
		System.out.println(xml_base);
		if (xml_base.equals("error")) {
			Toast.makeText(app_query_temperature_Activity.this, "网络异常",
					Toast.LENGTH_SHORT).show();
		} else {
			try {
				// 创建一个SAXParserFactory
				SAXParserFactory factory = SAXParserFactory.newInstance();
				XMLReader reader = factory.newSAXParser().getXMLReader();
				// 将XMLReader设置内容处理器
				reader.setContentHandler(new ContentHandler_temperature());
				// 开始解析文件
				reader.parse(new InputSource(new StringReader(xml_base)));
			} catch (Exception e) {
				System.out.println("xml parse error");
				e.printStackTrace();
			}
		}

		series.clear();
		Cursor cursor = dbReadHandle.query(gConfig.testTableName, new String[] {
				"time", "value" }, null, null, null, null, null);
		while (cursor.moveToNext()) {
			String time = cursor.getString(cursor.getColumnIndex("time"));
			String value = cursor.getString(cursor.getColumnIndex("value"));
//			System.out.println("time: " + time + " value: " + value);

			series.add(cursor.getPosition() * 10, Double.parseDouble(value));
			System.out.println("value_double: " + Double.parseDouble(value));
		}
		// 移除数据集中旧的点集
		mDataset.removeSeries(series);
		// 在数据集中添加新的点集
		mDataset.addSeries(series);
		// 视图更新，没有这一步，曲线不会呈现动态
		// 如果在非UI主线程中，需要调用postInvalidate()，具体参考api
		chart.invalidate();
		
		//
		// // 设置好下一个需要增加的节点
		// addX = 0;
		// // addY = (int)(Math.random() * 90);
		// addY = (int) (Math.random() * 50);
		// // 移除数据集中旧的点集
		// mDataset.removeSeries(series);
		// // 判断当前点集中到底有多少点，因为屏幕总共只能容纳100个，所以当点数超过100时，长度永远是100
		// int length = series.getItemCount();
		// if (length > 100) {
		// length = 100;
		// }
		// // 将旧的点集中x和y的数值取出来放入backup中，并且将x的值加1，造成曲线向右平移的效果
		// for (int i = 0; i < length; i++) {
		// xv[i] = (int) series.getX(i) + 1;
		// yv[i] = (int) series.getY(i);
		// }
		// // 点集先清空，为了做成新的点集而准备
		// series.clear();
		// // 将新产生的点首先加入到点集中，然后在循环体中将坐标变换后的一系列点都重新加入到点集中
		// // 这里可以试验一下把顺序颠倒过来是什么效果，即先运行循环体，再添加新产生的点
		// series.add(addX, addY);
		// for (int k = 0; k < length; k++) {
		// series.add(xv[k], yv[k]);
		// }
		// // 在数据集中添加新的点集
		// mDataset.addSeries(series);
		//
		// // 视图更新，没有这一步，曲线不会呈现动态
		// // 如果在非UI主线程中，需要调用postInvalidate()，具体参考api
		// chart.invalidate();
	}

}
