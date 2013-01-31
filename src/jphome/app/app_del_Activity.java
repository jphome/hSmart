package jphome.app;

import jphome.hsmart.R;
import jphome.hsmart.cmd;
import jphome.hsmart.gConfig;
import jphome.sqlite.DBHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class app_del_Activity extends Activity {
	private Button delButton;
	private Button cancelButton;
	private EditText deviceIdEditText;
	private EditText sensorIdEditText;
	
	private SQLiteDatabase dbWriteHandle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_del);
		
		DBHelper dbHelper = new DBHelper(app_del_Activity.this, gConfig.db_Name, 1);
		dbWriteHandle = dbHelper.getWritableDatabase();
		
		delButton = (Button)findViewById(R.id.delButton);
		cancelButton = (Button)findViewById(R.id.cancelButton_del);
		deviceIdEditText = (EditText)findViewById(R.id.deviceIdEditText_del);
		sensorIdEditText = (EditText)findViewById(R.id.sensorIdEditText_del);
		
		cancelButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		
		delButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String deviceId = deviceIdEditText.getText().toString();
				String sensorId = sensorIdEditText.getText().toString();
				if (deviceId.equals("")) {
					showDialog("         need deviceId at least");
					return;
				} else if (sensorId.equals("")) {
					// To del the whole node
					warnDelNode(deviceId);					
				} else {
					// To del the exact sensor
					String[] whereArgs = {deviceId, sensorId};
					dbWriteHandle.delete(gConfig.sensorTableName, "deviceId=? and sensorId=?", whereArgs);
					cmd.delDevice(deviceId, sensorId);
					System.out.println("del sensor: <" + deviceId + "><" + sensorId + ">");
					Toast.makeText(app_del_Activity.this, "del sensor success", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
    private void showDialog(String msg){
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(msg)
    	       .setCancelable(false)
    	       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {}
    	       });
    	AlertDialog alert = builder.create();
    	alert.show();
    }
    
    private void warnDelNode(final String deviceId){
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage("Is to del the whole node <"+ deviceId + "> ?")
    	       .setPositiveButton("YES", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	        	   // del deviceId
    	        	   String[] whereArgs = {deviceId};
    	        	   dbWriteHandle.delete(gConfig.sensorTableName, "deviceId=?", whereArgs);
    	        	   cmd.delNode(deviceId);
    	        	   System.out.println("del node: <" + deviceId + ">");
    	        	   Toast.makeText(app_del_Activity.this, "del node success", Toast.LENGTH_SHORT).show();
    	           }
    	       })
    	       .setNegativeButton("NO", new DialogInterface.OnClickListener() {
    	    	   public void onClick(DialogInterface dialog, int which) {}
    	       });
    	AlertDialog alert = builder.create();
    	alert.show();
    }
}











