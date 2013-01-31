package jphome.app;

import jphome.hsmart.R;
import jphome.hsmart.cmd;
import jphome.hsmart.gConfig;
import jphome.sqlite.DBHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class app_add_Activity extends Activity {
	private Button addButton;
	private Button cancelButton;
	private EditText deviceIdEditText;
	private EditText sensorIdEditText;
	private Spinner typeSpinner;
	private EditText nameEditText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_add);
		
		DBHelper dbHelper = new DBHelper(app_add_Activity.this, gConfig.db_Name, 1);
		final SQLiteDatabase dbWriteHandle = dbHelper.getWritableDatabase();
		final ContentValues cv = new ContentValues();
		
		addButton = (Button)findViewById(R.id.addButton);
		cancelButton = (Button)findViewById(R.id.cancelButton_add);
		deviceIdEditText = (EditText)findViewById(R.id.deviceIdEditText_add);
		sensorIdEditText = (EditText)findViewById(R.id.sensorIdEditText_add);
		typeSpinner = (Spinner)findViewById(R.id.typeSpinner);
		nameEditText = (EditText)findViewById(R.id.nameAddEditText);
		
//		String[] str = {"CMD", "DATA"};
//		ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, str);
//		typeSpinner.setAdapter(aa);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
	            this, R.array.sensor_type, android.R.layout.simple_spinner_item);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    typeSpinner.setAdapter(adapter);
		
		cancelButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		addButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String deviceId = deviceIdEditText.getText().toString();
				String sensorId = sensorIdEditText.getText().toString();
				String type = typeSpinner.getSelectedItem().toString();
				String name = nameEditText.getText().toString();
				if (deviceId.equals("") || sensorId.equals("") || name.equals("")) {
					showDialog("   need complete infomation");
				} else {
					cv.clear();
					cv.put("deviceId", deviceId);
					cv.put("sensorId", sensorId);
					cv.put("sensorType", type);
					cv.put("sensorName", name);
					dbWriteHandle.insert(gConfig.sensorTableName, null, cv);
					
					cmd.addDevice(deviceId, sensorId, type);
					Toast.makeText(app_add_Activity.this, "add success", Toast.LENGTH_SHORT).show();					
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
}
















