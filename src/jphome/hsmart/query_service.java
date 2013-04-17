package jphome.hsmart;

import java.util.ArrayList;
import java.util.HashMap;

import jphome.utils.DBUtil;
import jphome.utils.HttpUtil;
import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;

public class query_service extends Service {
	DBUtil dbHelper;
	SQLiteDatabase dbWriteHandle;
	Thread thread;
	private Boolean runflag = true;

	static ArrayList<HashMap<String, String>> temperatureList;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		System.out.println("service create");
		dbHelper = new DBUtil(query_service.this, gConfig.db_Name, 1);
		dbWriteHandle = dbHelper.getWritableDatabase();
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		System.out.println("service start");
		runflag = true;
		queryThread queryThread = new queryThread();
		// 启动新线程
		thread = new Thread(queryThread);
		thread.start();

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		System.out.println("service destroy");
		System.out.println("thread stop");
		runflag = false;
		super.onDestroy();
	}

	class queryThread implements Runnable {
		public void run() {
			while (runflag) {
				System.out.println("thread running");

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
