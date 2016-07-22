package com.example.iimp_znxj_new2014.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Locale;

import com.example.iimp_znxj_new2014.R;
import com.example.iimp_znxj_new2014.activity.AutoScrollView.ScrollEnd;
import com.example.iimp_znxj_new2014.util.Constant;
import com.example.iimp_znxj_new2014.util.ManagerUtil;
import com.example.iimp_znxj_new2014.util.JingMoOrder.Client;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

/**
 * link: blog.csdn.net/t12x3456
 * 
 * @author Tony
 * 
 */
public class FullScrollSubtitleActivity extends Activity implements OnInitListener ,ScrollEnd{

	private static final String TAG = "FullScrollSubtitleActivity";
	private String URL = "http://192.168.1.26:89/test.txt"; // scenic.txt
	private String showSpeed = null;
	private String showTime = null;
	private String getResponse;

	private AutoScrollView scrollView;
	private TextView contentTv1;
	private static final int ONE_MINUTE = 60 * 1000;

	private StatusReceiver mStatusReceiver;
	private Handler thandler;

	private TextToSpeech tts;
	private PowerManager.WakeLock wakeLock;
	private String volume;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scrollsubtitle);

		initControl();

		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		// 判断是否为空 不为空得到volume的值
		if (getIntent().getExtras().getString("volume") != null) {
			volume = getIntent().getExtras().getString("volume");
		}
		// TTS初始化
		tts = new TextToSpeech(FullScrollSubtitleActivity.this, FullScrollSubtitleActivity.this);
		tts.setSpeechRate(1);

		URL = bundle.getString(Constant.SCROLL_URL);
		// showSpeed = bundle.getString(Constant.SCROLL_SPEED);
		if (volume != null) {
			try {
				ManagerUtil.SoundCtrl(FullScrollSubtitleActivity.this,Integer.parseInt(volume));
			} catch (Exception e) {
			}
			
		}
		showTime = bundle.getString(Constant.SCROLL_SHWOTIME);

		Log.v(TAG, "onCreate,url=" + URL + ",Speed:" + showSpeed + ",showTime=" + showTime);

		doGetNetWork();
		//
		handler.sendEmptyMessage(0);

		mStatusReceiver = new StatusReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.STOPPLAY_ACTION);
		registerReceiver(mStatusReceiver, filter);

		scrollView.registerScrollListener(this);
		thandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1: // 回复成功滚动
					new Thread(new Client("fullSubTitle:true")).start();
					break;

				case 2: // 回复失败滚动
					new Thread(new Client("fullSubTitle:false")).start();
					FullScrollSubtitleActivity.this.finishAndGOtoIndex();
					break;

				case 3: // 定时关闭
					FullScrollSubtitleActivity.this.finishAndGOtoIndex();
					Log.i("NetWork", "5s连一次");
					break;
				}
			}
		};

		if ("-1".equals(showTime)) {
			Log.i("SubTitlePlan", "showTime=" + showTime+"等于-1");
			
		}else{
			Log.i("SubTitlePlan", "showTime=" + showTime);
			
			Message msg = new Message();
			msg.what = 3;
			thandler.sendMessageDelayed(msg, Integer.parseInt(showTime) * ONE_MINUTE);
		}

		Message message = handler.obtainMessage(1);
		handler.sendMessageDelayed(message, 1000);
	}

	private void initControl() {
		scrollView = (AutoScrollView) findViewById(R.id.auto_scrollview);
		contentTv1 = (TextView) findViewById(R.id.autoscroll_tv1);
	}

	public class StatusReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String actionStr = intent.getAction();
			Log.d("Test", "actionStr = " + actionStr);
			if (Constant.STOPPLAY_ACTION.equals(actionStr)) {
				FullScrollSubtitleActivity.this.finishAndGOtoIndex();
				Log.d("jiayy", "startIndex in CheckNote");
			}
		}
	}

	private void doGetNetWork() { // final String userName,final String userPass
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.setContentEncoding("UTF-8"); // 要加上，不然乱码

		if(URL==null){
			return;
		}
		
		// 执行post方法
		client.get(URL, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, org.apache.http.Header[] headers, byte[] responseBody) {
				try {
					getResponse = new String(responseBody, "UTF-8").trim(); // trim()方法，消除左右两边的空格
					contentTv1.setText(getResponse);
					Log.i(TAG, "Success：" + getResponse);

					if (!scrollView.isScrolled()) {
						Log.i(TAG, "isScrolled1");
						scrollView.setScrolled(true);
					}
					Message msg = new Message();
					msg.what = 1;
					thandler.sendMessage(msg);
					Log.i(TAG, "end");

				} catch (Exception e) {
					Log.i(TAG, "Error:" + e.getMessage());
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
				error.printStackTrace();
				Message msg = new Message();
				msg.what = 2;
				thandler.sendMessage(msg);
				Log.i(TAG, "doGetNetWork,Error:" + error.getMessage());
			}
		});
	}

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			// 设置屏幕的
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");// /
			wakeLock.acquire();

			Log.v("7.16", "开始语音");
			tts.speak(getResponse, TextToSpeech.QUEUE_FLUSH, null);
		};
	};

	@Override
	protected void onStop() {
		Log.v(TAG, "onStop");
		if (scrollView.isScrolled()) {
			scrollView.setScrolled(false);
		}
		unregisterReceiver(mStatusReceiver);

		wakeLock.release();
		super.onDestroy();

		if (tts != null) {
			tts.shutdown();
		}
		
		//结束时发送继续直播或者流媒体广播
		Intent in=new Intent();
		in.setAction(Constant.VCR_OR_VIDEO_CONTINUE);
		sendBroadcast(in);

		super.onStop();
	}

	public String encode(String value) {
		try {
			return URLEncoder.encode(value, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Log.i("Error", "Error:" + e.getMessage());
			e.printStackTrace();
		}
		return value;
	}

	public String decode(String value) {
		try {
			return URLDecoder.decode(value, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Log.i("Error", "Error:" + e.getMessage());
			e.printStackTrace();
		}
		return value;
	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			int result = tts.setLanguage(Locale.CHINA);
			if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Toast.makeText(this, "请先安装TTS软件", Toast.LENGTH_SHORT).show();
				new Thread(new Client("fullSubTitle:false,reason:please install tts first")).start();
			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if(keyCode==4){
			Intent intent = new Intent();
			intent.setAction("com.example.iimp_znxj_new2014.stopPlay");
			sendBroadcast(intent);
		}
		
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void isEnd() {
		Message message = handler.obtainMessage(1);
		handler.sendMessageDelayed(message, 1000);
	}

	
	//结束当前界面跳回主界面
		public void finishAndGOtoIndex() {
			finish();
			Intent intent = new Intent();
			intent.setClass(FullScrollSubtitleActivity.this, IndexActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
}
