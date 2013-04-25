package jphome.app;

import java.io.StringReader;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
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
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class app_query_temperature_Activity extends Activity {
	private Timer timer = new Timer();
	private TimerTask task;
	private Handler handler;
	private String title = "温度变化曲线";
	// private XYSeries series;
	private TimeSeries series;

	private XYMultipleSeriesDataset mDataset;
	// private GraphicalView chart;
	private GraphicalView mChartView;
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
		// series = new XYSeries(title);
		series = new TimeSeries(title);

		// 创建一个数据集的实例，这个数据集将被用来创建图表
		mDataset = new XYMultipleSeriesDataset();

		// 将点集添加到这个数据集中
		mDataset.addSeries(series);

		// 以下都是曲线的样式和属性等等的设置，renderer相当于一个用来给图表做渲染的句柄
		int color = Color.GREEN;
		PointStyle style = PointStyle.TRIANGLE;
		// 设置好图表的样式
		renderer = buildRenderer(color, style, true);
		setChartSettings(renderer);

		// 生成图表
		// chart = ChartFactory.getLineChartView(context, mDataset, renderer);
		mChartView = ChartFactory.getTimeChartView(context, mDataset, renderer,
				"M/d HH:mm");

		// 折线被点击回调
		// mChartView.setOnTouchListener(chartViewOnTouchListener);
		// mChartView.setId(0);

		layout.removeAllViews(); // 先remove再add可以实现统计图更新
		// 将图表添加到布局中去
		layout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT,
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
		timer.schedule(task, 1, 5 * 1000);
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

	protected void setChartSettings(XYMultipleSeriesRenderer renderer) {
		double xMin, xMax, yMin = 5, yMax = 40;

		// 有关对图表的渲染可参看api文档
		renderer.setAxisTitleTextSize(16);
		renderer.setChartTitleTextSize(16);
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(15);

		renderer.setXTitle("时间 (月/日 时:分)");
		renderer.setYTitle("温度 (摄氏度)");
		renderer.setChartTitle(title);

		// XY轴的最大/最小值
		// renderer.setXAxisMin(xMin);
		// renderer.setXAxisMax(xMax);
		renderer.setYAxisMin(yMin);
		renderer.setYAxisMax(yMax);

		// 坐标轴颜色
		renderer.setAxesColor(Color.RED);
		// 标签(title、单位)颜色
		renderer.setLabelsColor(Color.WHITE);
		// 网格属性
		renderer.setShowGrid(true);
		renderer.setGridColor(Color.BLACK);
		// 背景色 不起作用
		// renderer.setBackgroundColor(Color.WHITE);

		// 横纵坐标格数
		renderer.setXLabels(5);
		renderer.setYLabels((int) ((yMax - yMin) / 5));
		// Y轴的刻度到Y轴的右边
		renderer.setYLabelsAlign(Align.RIGHT);
		// 折线转折点大小
		renderer.setPointSize((float) 7);
		// 是否显示图例
		renderer.setShowLegend(false);
		// 显示折线转折点值
		// renderer.setDisplayChartValues(true);
	}

	private void updateChart() {
		String url = gConfig.URL.BASE_URL + "query_temperature";
		System.out.println("url: " + url);
		String xml_base = HttpUtil.queryStringForGet(url);
		System.out.println("HttpGet end");
		System.out.println("get xml: " + xml_base);
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
		java.util.Date date = new Date(0);
		Calendar cal = Calendar.getInstance();
		cal.get(Calendar.HOUR_OF_DAY);// 得到24小时机制的
		// date.get(Calendar.HOUR);// 得到12小时机制的

		while (cursor.moveToNext()) {			
			String time_s = cursor.getString(cursor.getColumnIndex("time"));
			int time_i = Integer.parseInt(time_s); // 413,1352
			System.out.println("time_i: " + time_i);

			System.out.println("time: " + "2013 " + (time_i / 1000000 + 1)
					+ "." + time_i % 1000000 / 10000 + " " + time_i % 10000
					/ 100 + ":" + time_i % 100);
			cal.set(Calendar.YEAR, 2013);
			cal.set(Calendar.MONTH, time_i / 1000000);
			cal.set(Calendar.DATE, time_i % 1000000 / 10000);
			cal.set(Calendar.HOUR_OF_DAY, time_i % 10000 / 100);
			cal.set(Calendar.MINUTE, time_i % 100);

			date = cal.getTime();

			String yValue = cursor.getString(cursor.getColumnIndex("value"));
			// System.out.println("time: " + time + " value: " + value);
			// series.add(cursor.getPosition() * 10,
			// Double.parseDouble(yValue));
			series.add(date, Double.parseDouble(yValue));
			System.out.println("value_double: " + Double.parseDouble(yValue));
		}
		// 移除数据集中旧的点集
		mDataset.removeSeries(series);
		// 在数据集中添加新的点集
		mDataset.addSeries(series);
		// 视图更新，没有这一步，曲线不会呈现动态
		// 如果在非UI主线程中，需要调用postInvalidate()，具体参考api
		// chart.invalidate();
		mChartView.invalidate();

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

	private LinearLayout containerbody;
	private int mEventEndX;
	private int mEventEndY;
	private int mEventStartX;
	private int mScreenOffsetX = 0;
	private final int CHART_MARGINS_LEFT = 20;
	private final int CHART_MARGINS_TOP = 30;
	private final int CHART_MARGINS_RIGHT = 20;
	// private final int CHART_MARGINS_BOTTOM = 0;
	// private int mScreenOffsetY = 0;
	private PopupWindow mPopupWindow;
	private PopupWindow mPopupWindow1;
	private PopupWindow popWin;
	private View mPopupView;
	private View mPopupView1;
	private GraphicalView mLineChartView;
	private int chart_margins_bottom;
	private final int CHART_X_LABELS = 9;
	private final int CHART_Y_LABELS = 6;
	private final int CHART_X_AXISMAX = CHART_X_LABELS + 1;
	private final int CHART_Y_AXISMAX = CHART_Y_LABELS * 10;
	private int panXLimit = 30;
	private int lineEndX = 30;
	private List<Map<String, String>> mDataMapList = new ArrayList<Map<String, String>>();
	private Map<String, String> mDataMap = new HashMap<String, String>();
	private int touchPoint;

	private TextView mPopTxt1;
	private TextView mPopTxt2;
	private TextView mPopTxt3;
	private TextView mPopTxt4;

	private int POPWIN_WIDTH;
	private int POPWIN_HEIGHT;
	private int POPWIN_WIDTH1;
	private int POPWIN_HEIGHT1;

	private void dismissPopupWindow() {
		if (mPopupWindow != null) {
			if (mPopupWindow.isShowing()) {
				mPopupWindow.dismiss();
			}
			mPopupWindow = null;
		}
		if (mPopupWindow1 != null) {
			if (mPopupWindow1.isShowing()) {
				mPopupWindow1.dismiss();
			}
			mPopupWindow1 = null;
		}

		if (popWin != null) {
			if (popWin.isShowing()) {
				popWin.dismiss();
			}
			popWin = null;
		}
	}

	public int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	private OnTouchListener chartViewOnTouchListener = new OnTouchListener() {
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			dismissPopupWindow();

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mEventStartX = (int) event.getX();
				// mEventStartY = (int) event.getY();
				break;
			case MotionEvent.ACTION_UP:
				// *图表点击坐标
				mEventEndX = (int) event.getX();
				mEventEndY = (int) event.getY();
				System.out.println("1.------------------------------");
				// 屏幕是否位移
				mScreenOffsetX += mEventEndX - mEventStartX == 0 ? 0
						: mEventEndX - mEventStartX;
				// 是否超出X轴原点（是，归0）
				mScreenOffsetX = mScreenOffsetX > 0 ? 0 : mScreenOffsetX;
				System.out.println("############ mOffsetX = " + mScreenOffsetX);
				if (event.getX() < CHART_MARGINS_LEFT
						|| event.getX() > containerbody.getRight()
								- CHART_MARGINS_LEFT
						|| event.getY() < CHART_MARGINS_TOP
						|| event.getY() > containerbody.getBottom()
								- CHART_MARGINS_TOP) {
					// out of the chartView, do nothing.
				} else {
					// 取屏幕宽、高
					int screenWidth = getWindowManager().getDefaultDisplay()
							.getWidth();
					int screenHeight = getWindowManager().getDefaultDisplay()
							.getHeight();
					// 取图表区域宽、高
					int chartViewWidth = mLineChartView.getWidth();
					int chartViewHeight = mLineChartView.getHeight();
					// 求图表单元格宽、高
					int chartCellWidth = (chartViewWidth - CHART_MARGINS_LEFT * 2)
							/ CHART_X_LABELS;
					int chartCellHeight = (chartViewHeight - CHART_MARGINS_TOP - chart_margins_bottom)
							/ CHART_Y_LABELS;
					// 是否超出X轴上限（是，取上限值）
					mScreenOffsetX = mScreenOffsetX < -chartCellWidth
							* (panXLimit - CHART_X_AXISMAX) ? -chartCellWidth
							* (panXLimit - CHART_X_AXISMAX) : mScreenOffsetX;
					System.out.println("-chartCellWidth * panXLimit = "
							+ -chartCellWidth * (panXLimit - CHART_X_AXISMAX));
					System.out
							.println("@@@@mScreenOffsetX = " + mScreenOffsetX);
					// *求图表像素坐标 换算成图表单位坐标
					int chartEventX = 1 + Math
							.round((mEventEndX - CHART_MARGINS_LEFT)
									/ chartCellWidth);
					int chartEventY = CHART_Y_AXISMAX
							- Math.round((mEventEndY - CHART_MARGINS_TOP)
									/ chartCellHeight * 10);
					System.out.println("chartEventX = " + chartEventX);
					System.out.println("chartEventY = " + chartEventY);
					// 求位移单元格
					int chartOffsetX = Math.round(mScreenOffsetX
							/ chartCellWidth);
					System.out.println("############ chartOffsetX = "
							+ chartOffsetX);
					System.out.println("2.================================");
					// 求图表像素坐标 换算成屏幕像素坐标
					int screenEventX = mEventEndX
							+ (screenWidth - chartViewWidth) / 2;
					int screenEventY = mEventEndY
							+ (screenHeight - chartViewHeight) / 2;
					// System.out.println("chartEventX = " + event.getX());
					// System.out.println("chartEventY = " + event.getY());
					// System.out.println("screenEventX = " + screenEventX);
					// System.out.println("screenEventY = " + screenEventY);

					boolean mType = true;
					isShowPopWin: for (int i = 0; i < lineEndX; i++) {
						int pxSeriesX = ((int) (series.getX(i)) - 1)
								* chartCellWidth + CHART_MARGINS_LEFT;
						int pxSeriesY = (CHART_Y_AXISMAX - (int) (series
								.getY(i)))
								* chartCellHeight
								/ 10
								+ CHART_MARGINS_TOP;
						System.out
								.println("3.**********************************");
						// System.out.println(" mEventEndX = " + mEventEndX);
						// System.out.println(" pxSeriesX + mScreenOffsetX - chartCellWidth / 2 = "
						// + (pxSeriesX + mScreenOffsetX - chartCellWidth / 2));
						// System.out.println(" pxSeriesX + mScreenOffsetX + chartCellWidth / 2 = "
						// + (pxSeriesX + mScreenOffsetX + chartCellWidth / 2));
						// System.out.println(" mEventEndY = " + mEventEndY);
						// System.out.println(" pxSeriesY - chartCellHeight / 2 = "
						// + (pxSeriesY - chartCellHeight / 2));
						// System.out.println(" pxSeriesY + chartCellHeight / 2 = "
						// + (pxSeriesY + chartCellHeight / 2));

						if (mEventEndX > pxSeriesX + mScreenOffsetX
								- chartCellWidth / 2
								&& mEventEndX < pxSeriesX + mScreenOffsetX
										+ chartCellWidth / 2
								&& mEventEndY > pxSeriesY - chartCellHeight / 2
								&& mEventEndY < pxSeriesY + chartCellHeight / 2) {
							System.out.println(" series.getX(i) = "
									+ series.getX(i));
							System.out.println(" pxSeriesX = " + pxSeriesX);

							mDataMap = mDataMapList.get(i);
							String bustotal = mDataMap.get("BUSTOTAL")
									.toString();
							String busunsuccess = mDataMap.get("BUSUNSUCCESS")
									.toString();
							touchPoint = i;
							// 根据type，判断窗口绿or蓝
							if ("0".equals(mDataMap.get("type").toString())) {
								mType = false;
								mPopTxt1.setText("预约" + bustotal + "条");
								mPopTxt2.setText(busunsuccess + "条");
							} else {
								mType = true;
								mPopTxt4.setText("已办理" + bustotal + "条");
							}
							if (screenWidth - screenEventX < POPWIN_WIDTH) {
								mPopupView.setBackgroundDrawable(getResources()
										.getDrawable(R.drawable.switch_on));
								mPopupView
										.setPadding(
												0,
												dip2px(app_query_temperature_Activity.this,
														30),
												dip2px(app_query_temperature_Activity.this,
														23), 0);
								mPopTxt1.setGravity(Gravity.RIGHT);
								mPopTxt2.setGravity(Gravity.RIGHT);
								mPopTxt3.setGravity(Gravity.RIGHT);

								mPopupView1
										.setBackgroundDrawable(getResources()
												.getDrawable(
														R.drawable.switch_on));
								mPopupView1
										.setPadding(
												0,
												dip2px(app_query_temperature_Activity.this,
														30),
												dip2px(app_query_temperature_Activity.this,
														23), 0);
								mPopTxt4.setGravity(Gravity.RIGHT);

								screenEventX -= POPWIN_WIDTH;
							} else {
								mPopupView.setBackgroundDrawable(getResources()
										.getDrawable(R.drawable.switch_off));
								mPopupView
										.setPadding(
												dip2px(app_query_temperature_Activity.this,
														23),
												dip2px(app_query_temperature_Activity.this,
														30), 0, 0);
								mPopTxt1.setGravity(Gravity.LEFT);
								mPopTxt2.setGravity(Gravity.LEFT);
								mPopTxt3.setGravity(Gravity.LEFT);

								mPopupView1
										.setBackgroundDrawable(getResources()
												.getDrawable(
														R.drawable.switch_off));
								mPopupView1
										.setPadding(
												dip2px(app_query_temperature_Activity.this,
														20),
												dip2px(app_query_temperature_Activity.this,
														30), 0, 0);
								mPopTxt4.setGravity(Gravity.LEFT);
							}

							mPopupWindow = new PopupWindow(mPopupView,
									POPWIN_WIDTH, POPWIN_HEIGHT);
							mPopupWindow1 = new PopupWindow(mPopupView1,
									POPWIN_WIDTH1, POPWIN_HEIGHT1);
							if (mType) {
								if (mPopupWindow1.isShowing()) {
									mPopupWindow1.update(screenEventX,
											screenEventY, POPWIN_WIDTH,
											POPWIN_HEIGHT);
								} else {
									mPopupWindow1.showAtLocation(
											mLineChartView, Gravity.NO_GRAVITY,
											screenEventX, screenEventY);
								}
							} else {// + POPWIN_HEIGHT / 2
								if (mPopupWindow.isShowing()) {
									mPopupWindow.update(screenEventX,
											screenEventY, POPWIN_WIDTH,
											POPWIN_HEIGHT);
								} else {
									mPopupWindow.showAtLocation(mLineChartView,
											Gravity.NO_GRAVITY, screenEventX,
											screenEventY);
								}
							}

							break isShowPopWin;
						}
					}
				}
				break;
			default:
				break;
			}
			return false;
		}
	};
}
