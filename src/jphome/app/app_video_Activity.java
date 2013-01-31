package jphome.app;

import jphome.hsmart.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class app_video_Activity extends Activity {
	private Button startVideo;
	private EditText editIP;
	private EditText editPort;
	String videoIP, videoPort;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_video);
		
		startVideo = (Button)findViewById(R.id.startVideo);
		editIP     = (EditText)findViewById(R.id.editVideoIP);
		editPort   = (EditText)findViewById(R.id.editVideoPort);
		
		startVideo.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				System.out.println("===> to start video");
				videoIP = editIP.getText().toString();
				videoPort = editPort.getText().toString();
				
				Intent intent = new Intent();
				intent.putExtra("videoIP", videoIP);
				intent.putExtra("videoPort", videoPort);
				intent.setClass(app_video_Activity.this, app_video_out_Activity.class);
//				Intent intent = new Intent(app_video_Activity.this, app_video_out_Activity.class);
				startActivity(intent);
			}
		});
	}
}

















