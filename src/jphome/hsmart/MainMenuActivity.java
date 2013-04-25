package jphome.hsmart;

import jphome.app.app_add_Activity;
import jphome.app.app_cmd_Activity;
import jphome.app.app_del_Activity;
import jphome.app.app_query_Activity;
import jphome.app.app_video_Activity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainMenuActivity extends Activity {
	private static final int UPDATE = 1;
	private static final int ABOUT = 2;

	private Button add;
	private Button del;
	private Button query;
	private Button cmd;
	private Button video;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);

		add = (Button) findViewById(R.id.add_Button);
		del = (Button) findViewById(R.id.del_Button);
		query = (Button) findViewById(R.id.query_Button);
		// cmd = (Button)findViewById(R.id.cmd_Button);
		video = (Button) findViewById(R.id.video_Button);

		add.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("to add sensor");
				Intent intent = new Intent(MainMenuActivity.this,
						app_add_Activity.class);
				startActivity(intent);
			}
		});

		del.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("to del sensor");
				Intent intent = new Intent(MainMenuActivity.this,
						app_del_Activity.class);
				startActivity(intent);
			}
		});

		query.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("to query data");
				Intent intent = new Intent(MainMenuActivity.this,
						app_query_Activity.class);
				startActivity(intent);
			}
		});

		// cmd.setOnClickListener(new OnClickListener() {
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// System.out.println("to cmd_send");
		// Intent intent = new Intent(MainMenuActivity.this,
		// app_cmd_Activity.class);
		// startActivity(intent);
		// }
		// });

		video.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("to video_set");
				Intent intent = new Intent(MainMenuActivity.this,
						app_video_Activity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, ABOUT, 2, R.string.hsmart_about);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == ABOUT) {

		}
		return super.onOptionsItemSelected(item);
	}
}
