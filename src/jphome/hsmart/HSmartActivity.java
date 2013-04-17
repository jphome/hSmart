package jphome.hsmart;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

// webView = (WebView)findViewById(R.id.mywebview);
// String url = "http://www.google.com";
// String url = "http://10.202.233.107:8081/login?username=admin&password=admin";
// webView.loadUrl(url);

// http://10.202.233.107:8081/cmd_light?status=on
// http://10.202.233.107:8081/cmd_light?status=off

public class HSmartActivity extends Activity {
	// private WebView webView;
	private Button cancelBtn, loginBtn;
	private EditText userEditText, pwdEditText;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_system);
		cancelBtn = (Button) findViewById(R.id.cancelButton);
		loginBtn = (Button) findViewById(R.id.loginButton);

		userEditText = (EditText) findViewById(R.id.userEditText);
		pwdEditText = (EditText) findViewById(R.id.pwdEditText);

		cancelBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		loginBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				// String username = userEditText.getText().toString();
				// String password = pwdEditText.getText().toString();
				// if (username.equals("")) {
				// showDialog("用户名必须填");
				// return;
				// }
				// if (password.equals("")) {
				// showDialog("密码必须填");
				// return;
				// }
				// if (true == cmd.login(username, password)) {
				// Toast.makeText(HSmartActivity.this, "login success",
				// Toast.LENGTH_SHORT).show();
				// }
				
				// Toast.makeText(HSmartActivity.this,
				// FFMpegPlayer.nativeGetStream(), Toast.LENGTH_SHORT).show();
				// Toast.makeText(HSmartActivity.this, "login success",
				// Toast.LENGTH_SHORT).show();
				
				System.out.println("login success");
				Toast toast = Toast.makeText(HSmartActivity.this, "亲，恭喜你，登录成功",
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 20, 69);
				LinearLayout linearLayout = (LinearLayout) toast.getView();
				ImageView imageView = new ImageView(HSmartActivity.this);
				imageView.setImageResource(R.drawable.toast);
				linearLayout.addView(imageView, 1);
				toast.setView(linearLayout);
				toast.show();

				// 后台开启一个独立线程service
				// Intent service = new Intent();
				// service.setClass(HSmartActivity.this, query_service.class);
				// startService(service);
				// System.out.println("service start success");

				Intent intent = new Intent(HSmartActivity.this,
						MainMenuActivity.class);

				// FFMpegPlayer.nativePlay("test", 320, 240); // while(1) for
				// test jni

				startActivity(intent);
				/*
				 * }else{ showDialog("   	    password error"); }
				 */
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		System.out.println("HSmartActivity destroy");
		Intent service = new Intent();
		service.setClass(HSmartActivity.this, query_service.class);
		stopService(service);
		super.onDestroy();
	}

	private void showDialog(String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(msg).setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}
}
