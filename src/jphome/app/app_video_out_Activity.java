package jphome.app;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import jphome.hsmart.R;
import jphome.hsmart.gConfig;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class app_video_out_Activity extends Activity {
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private Thread thread;
	private boolean thread_flag = false;

	private Canvas canvas;
	private Paint paintVideo;
	private Paint paintDetector;
	private int screenWidth, screenHeight;
	private int imageWidth, imageHeight;
	URL videoUrl;
	private String urlVideo;
	private String videoIP, videoPort;
	HttpURLConnection conn;
	Bitmap bmp;

	private FaceDetector myFaceDetect;
	private FaceDetector.Face[] myFace;
	float myEyesDistance;
	private int numberOfFaceDetected;
	private int numberOfFace = 5;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video);

		System.out.println("===> app_video_out_Activity");

		surfaceView = (SurfaceView) findViewById(R.id.videoSrufaceView);
		surfaceHolder = surfaceView.getHolder();
		// surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceHolder.addCallback(surfaceCallback);

		Intent intent = getIntent();
		videoIP = intent.getStringExtra("videoIP");
		videoPort = intent.getStringExtra("videoPort");
		if (videoIP.equals("") || videoPort.equals("")) {
			// http://192.168.1.1:8080/?action=snapshot
			urlVideo = gConfig.URL.VIDEO_URL;
			try {
				videoUrl = new URL(urlVideo);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("use default videoUrl");
		} else {
			try {
				/* http://192.168.1.1:8080/?action=snapshot */
				videoUrl = new URL("http", videoIP,
						Integer.parseInt(videoPort), "/?action=snapshot");
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// urlVideo = videoIP + ":" + videoPort + "/?action=snapshot";
			System.out.println("use videoUrl from editText");
		}

		// videoUrl = new URL("http://192.168.1.100/images/light_off.png");

		paintVideo = new Paint();
		paintVideo.setAntiAlias(true);
		paintVideo.setColor(Color.RED);
		paintDetector = new Paint();
		paintDetector.setColor(Color.RED);
		paintDetector.setStyle(Paint.Style.STROKE);
		paintDetector.setStrokeWidth(3);
		myFace = new FaceDetector.Face[numberOfFace];
		myFaceDetect = new FaceDetector(320, 240, numberOfFace);

		surfaceView.setKeepScreenOn(true);

		thread = new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				while (!thread_flag) {
					draw();
					System.out.println("===> thread running");
				}
			}
		};
	}

	private void draw() {
		try {
			InputStream inputstream = null;

			conn = (HttpURLConnection) videoUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			inputstream = conn.getInputStream();
			bmp = BitmapFactory.decodeStream(inputstream);

			canvas = surfaceHolder.lockCanvas();
			canvas.drawColor(Color.WHITE); // background color
			canvas.drawBitmap(bmp, 0, 0, paintVideo);

			// faceDetect();

			surfaceHolder.unlockCanvasAndPost(canvas);
			conn.disconnect();
			if (bmp.isRecycled() == false) { // 如果没有回收
				bmp.recycle();
			}
		} catch (Exception ex) {
			System.out.println("===> thread_draw Exception");
			thread_flag = true;
		} finally {
			// if (canvas != null)
			// sfh.unlockCanvasAndPost(canvas);
		}
	}

	private void faceDetect() {
		// imageWidth = bmp.getWidth();
		// imageHeight = bmp.getHeight();
		// System.out.println("imageWidth: " + imageWidth + "   imageHeight: " +
		// imageHeight);

		numberOfFaceDetected = myFaceDetect.findFaces(bmp, myFace);
		System.out.println("find face: " + numberOfFaceDetected);

		for (int i = 0; i < numberOfFaceDetected; i++) {
			Face face = myFace[i];
			PointF myMidPoint = new PointF();
			face.getMidPoint(myMidPoint);
			myEyesDistance = face.eyesDistance();
			canvas.drawCircle(myMidPoint.x, myMidPoint.y, myEyesDistance,
					paintDetector);
			/*
			 * canvas.drawRect( (int)(myMidPoint.x - myEyesDistance),
			 * (int)(myMidPoint.y - myEyesDistance), (int)(myMidPoint.x +
			 * myEyesDistance), (int)(myMidPoint.y + myEyesDistance),
			 * paintDetector);
			 */
		}
	}

	SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
		public void surfaceCreated(SurfaceHolder arg0) {
			// TODO Auto-generated method stub
			screenWidth = surfaceView.getWidth();
			screenHeight = surfaceView.getHeight();
			System.out.println("ScreenW:" + screenWidth + "\n" + "ScreenH:"
					+ screenHeight);
			thread.start();
		}

		public void surfaceDestroyed(SurfaceHolder arg0) {
			// TODO Auto-generated method stub
			thread_flag = true;
			System.out.println("===> surfaceDestroyed");
		}

		public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
		}
	};
}
