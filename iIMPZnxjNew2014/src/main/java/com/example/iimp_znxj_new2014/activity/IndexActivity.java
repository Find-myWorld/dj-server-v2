package com.example.iimp_znxj_new2014.activity;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iimp_znxj_new2014.DianJiaoApplication;
import com.example.iimp_znxj_new2014.R;
import com.example.iimp_znxj_new2014.selfconsume.BuyActivity2;
import com.example.iimp_znxj_new2014.service.BuildNettyService;
import com.example.iimp_znxj_new2014.service.ReadSerialService;
import com.example.iimp_znxj_new2014.service.UdpService;
import com.example.iimp_znxj_new2014.util.Constant;
import com.example.iimp_znxj_new2014.util.ManagerUtil;
import com.example.iimp_znxj_new2014.util.NetUtil;
import com.example.iimp_znxj_new2014.util.PreferenceUtil;
import com.example.iimp_znxj_new2014.util.UnitaryCodeUtil;
import com.wits.serialport.SerialPort;

/*
 * 串口键盘参数：ttyS4，9600,注意是否开机直接进入直播
 */
public class IndexActivity extends BaseActivity {

	private TextView timeTv, dateTv, showTv, online_whether;
	private SimpleDateFormat tformat, dformat;
	private Handler handler;
	static final int MSG_TIME = 1;
	static final int MSG_DATE = 2;
	static final int MSG_ICON = 3;
	static final int MSG_TOAST = 4;
	static final int MSG_ROLL = 5;
	private String TAG = "IndexActivity";
	private StatusReceiver mStatusReceiver; // 广播

	private String tDate;
	private Intent intent;
	private DianJiaoApplication gv;

	public static SerialPort mSerialPort;
	public static OutputStream mOutputStream;
	public static InputStream mInputStream;
	private boolean isFlag = true;
	private boolean isPause = false;

	private int flag = 1;

	ImageView image[][] = new ImageView[4][4]; // 二维数组定义
	int lrint = 0, udint = 0;
	int[] Rid = new int[] { R.id.main_menu1, R.id.main_menu2, R.id.main_menu3, R.id.main_menu4, R.id.main_menu5, R.id.main_menu6, R.id.main_menu7, R.id.main_menu8 };
	int[] Rdraw = new int[] { R.drawable.image_btn1, R.drawable.image_btn2, R.drawable.image_btn3, R.drawable.image_btn4, R.drawable.image_btn5, R.drawable.image_btn6, R.drawable.image_btn7,
			R.drawable.image_btn8 };
	int[] Rdrawsel = new int[] { R.drawable.image_btn1_h, R.drawable.image_btn2_h, R.drawable.image_btn3_h, R.drawable.image_btn4_h, R.drawable.image_btn5_h, R.drawable.image_btn6_h,
			R.drawable.image_btn7_h, R.drawable.image_btn8_h };

	private CheckOnlineBroadcast checkOnlineReceiver;

	private ImageView setting;
	private DisplayMetrics dm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.index);
	
        //接收串口Service
		Intent service = new Intent();
		if (!ManagerUtil.isServiceRunning(this, "ReadSerialService")) {
			service.setClass(this, ReadSerialService.class);
			startService(service);
		}

		gv = (DianJiaoApplication) getApplication();
		gv.addIndexActivities(this);

		intent = new Intent(this, UdpService.class); // 开启后台Service
		if (!ManagerUtil.isServiceRunning(this, "UdpService")) {
			startService(intent);
		}

		timeTv = (TextView) findViewById(R.id.tvTime);
		dateTv = (TextView) findViewById(R.id.tvDate);
		showTv = (TextView) findViewById(R.id.version_and_ip);
		fillImageButton(); // 初始化ImageView

		showTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				IndexActivity.this.finish();
			}
		});
		Log.v(TAG,"onCreate");
//		Log.i("TTSS", "IndexActivity，获取值班时间段：" + PreferenceUtil.getSharePreference(IndexActivity.this, Constant.DUTYTIME_TIMEARRAY));
		new Thread(new MyThread()).start(); // 主界面时间线程

		handler = new Handler() { // handle
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MSG_TIME:
					String t = tformat.format(new Date());
					timeTv.setText(t);
					break;
				case MSG_DATE:
					tformat = new SimpleDateFormat("HH:mm");
					dformat = new SimpleDateFormat("yyyy-MM-dd");
					String td = dformat.format(new Date()); // 设置首页时间与日期
					dateTv.setText(td);
					break;
				case MSG_ICON:
					fillImageButton();
					Log.i(TAG, "Handler......udint=" + udint + ",lrint=" + lrint);
					image[udint][lrint].setImageResource(Rdrawsel[udint * 4 + lrint]);
					break;
				case MSG_TOAST:
					Toast.makeText(IndexActivity.this, "本功能暂未开通！", Toast.LENGTH_SHORT).show();
					break;
				case MSG_ROLL:
					Toast.makeText(IndexActivity.this, "本功能由后台控制！", Toast.LENGTH_SHORT).show();
					break;
				}
				super.handleMessage(msg);
			}
		};

		tformat = new SimpleDateFormat("HH:mm");
		dformat = new SimpleDateFormat("yyyy-MM-dd");
		tDate = dformat.format(new Date()); // 设置首页时间与日期
		dateTv.setText(tDate);
		showTv.setText("V :" + ManagerUtil.getVersionName(IndexActivity.this) + "\nIP:" + NetUtil.getIp());

		// 注册改变日期的广播
		mStatusReceiver = new StatusReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.example.iimp_znxj_new2014.changedate");
		registerReceiver(mStatusReceiver, filter);

		//开机自动进入直播(有些地方使用 勿删)
//		firstStartGotoTVActivity();
		intent = new Intent(getApplicationContext(), BuildNettyService.class);
		startService(intent);
	}


	/*
	 * 开机自动进入直播
	 */
	public void firstStartGotoTVActivity() {
		if (!Constant.isManualStop) {
			Log.v(TAG, Constant.isManualStop + "");
			final Intent vcrIntent = new Intent(this, TVActivity.class);

			vcrIntent.putExtra("ip", "192.168.0.242");   //当阳：192.168.0.242
			vcrIntent.putExtra("port", "8000");          //阳新：172.16.3.3,172.16.3.2
			vcrIntent.putExtra("channel", "1");
			vcrIntent.putExtra("user", "admin");
			vcrIntent.putExtra("password", "12345");
			vcrIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			new Thread(new Runnable() { // 延迟500ms打开
				public void run() {
					try {
						Thread.sleep(500);
						startActivity(vcrIntent);
						IndexActivity.this.finish();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.i(TAG,"onStart");
		isFlag = true;
		isPause = false;

		setting = (ImageView) findViewById(R.id.setting);
		setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				IndexActivity.this.finish();
				Intent intent = new Intent();
				intent.setClass(IndexActivity.this, SettingActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});

	}

	@Override
	protected void onPostResume() {

		super.onPostResume();
		Log.v(TAG,"onPostResume");
		/*
		 * 判断是否联网
		 */
		online_whether = (TextView) findViewById(R.id.online_whether);

		checkOnlineReceiver = new CheckOnlineBroadcast();
		IntentFilter filter_checkOnline = new IntentFilter();
		filter_checkOnline.addAction("com.example.iimp_znxj_new2014.checkOnline");
		registerReceiver(checkOnlineReceiver, filter_checkOnline);
	}

	public class CheckOnlineBroadcast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String isOnline = intent.getStringExtra("isOnline");
			if ("true".equals(isOnline)) {
				checkOnlineHandler.sendEmptyMessage(0);
			} else if ("false".equals(isOnline)) {
				checkOnlineHandler.sendEmptyMessage(1);
			}
		}

	}

	public Handler checkOnlineHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				online_whether.setTextSize(15);
				online_whether.setTextColor(Color.WHITE);
				online_whether.setText("设备已在线");
				break;
			case 1:
				online_whether.setTextSize(30);
				online_whether.setTextColor(Color.YELLOW);
				online_whether.setText("设备未在线");
				break;
			}
		};
	};

	private void fillImageButton() {
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 4; j++) {
				image[i][j] = (ImageView) this.findViewById(Rid[i * 4 + j]);
				image[i][j].setImageResource(Rdraw[i * 4 + j]);
			}
		}
		image[udint][lrint].setImageResource(Rdrawsel[udint * 4 + lrint]);
		PreferenceUtil.putSharePreference(IndexActivity.this, Constant.TERMINAL_IP, NetUtil.getIp()); // 终端IP写入文件
	}

	private void ProcessKeyFunc(int keyCode) {
		Log.i(TAG,"是否注销了串口接收广播?=>"+isPause+",当前键值:"+keyCode);
		if(!isPause){
			if (gv.getmKey_Right() == keyCode) {
				lrint = lrint + 1;
				if (lrint > 3)
					lrint = 0;

			}
			if (gv.getmKey_Left() == keyCode) {
				lrint = lrint - 1;
				if (lrint < 0)
					lrint = 3;
			}
			if (gv.getmKey_Up() == keyCode) {
				udint = udint + 1;
				udint %= 2;
			}
			if (gv.getmKey_Down() == keyCode) {
				udint = udint + 1;
				udint %= 2;
			}

			Message message = new Message();
			message.what = MSG_ICON;
			message.arg1 = udint;
			message.arg2 = lrint;
			handler.sendMessage(message);

			int menunum = udint * 4 + lrint;
			Log.i(TAG, "menunum=" + menunum);
			if (gv.getmKey_Enter() == keyCode) {
				changePage(menunum);
			}
		}
	}

	/*
	 * 页面跳转
	 */
	public void changePage(int menustr) {
		switch (menustr) {
		case 0:
			this.finish();
			Intent intent = new Intent();
			intent.setClass(IndexActivity.this, BuyActivity2.class);
			startActivity(intent);
			break;
		case 1:
			this.finish();
			Intent intent1 = new Intent();
			intent1.setClass(IndexActivity.this, ThreeGdActivity.class);
			startActivity(intent1);
			break;
		case 2:
			Message message2 = new Message();
			message2.what = MSG_ROLL;
			handler.sendMessage(message2);
			break;
		case 3:
			this.finish();
			Intent intent3 = new Intent();
			intent3.setClass(IndexActivity.this, BillQueryActivity.class);
			startActivity(intent3);
			break;
		case 4:
			this.finish();
			Intent intent4 = new Intent();
			intent4.setClass(IndexActivity.this, LifeActivity.class);
			startActivity(intent4);
			break;
		case 5:
			Message message7 = new Message();
			message7.what = MSG_TOAST;
			handler.sendMessage(message7);
			break;
		case 6:
			Message message6 = new Message();
			message6.what = MSG_TOAST;
			handler.sendMessage(message6);
			break;
		case 7:
			this.finish();
			Intent intent7 = new Intent();
			intent7.setClass(IndexActivity.this, ComplainActivity.class);
			intent7.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent7);
			break;
		default:
			break;
		}

	}

	Boolean isUnregisterReceiver = false;

	@Override
	protected void onPause() {

		Log.v(TAG,"onPause");
		isFlag = false;
		isPause = true;

		super.onPause();
		if (!isUnregisterReceiver) {
			Log.v(TAG,"onPause,注销当前广播");
			this.unregisterReceiver(mStatusReceiver);
			isUnregisterReceiver = true;
		}

		unregisterReceiver(checkOnlineReceiver);

		mSerialPort = null;
	}

	@Override
	protected void onResume() {
		Log.v(TAG,"onResume");
//		isUnregisterReceiver = false;
		isPause = false;
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.v(TAG,"onDestroy");
		stopService(intent);
	}

	public class StatusReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String actionStr = intent.getAction();
			if ("com.example.iimp_znxj_new2014.changedate".equals(actionStr)) {
				Log.i(TAG, "改变日期");
				dformat = new SimpleDateFormat("yyyy-MM-dd");
				tDate = dformat.format(new Date()); // 设置首页时间与日期
				dateTv.setText(tDate);
			}
		}
	}

	public class MyThread implements Runnable {
		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(1000); // sleep 1000ms
					Message message = new Message();
					message.what = MSG_TIME;
					handler.sendMessage(message);
				} catch (Exception e) {
				}
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		return true;
	}

	boolean isCard = false;

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {

		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (event.getKeyCode() == 74) {
				isCard = true;
				this.finish();
				Intent intent = new Intent();
				intent.setClass(IndexActivity.this, BillQueryActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			} else {
				int code = UnitaryCodeUtil.UnitaryCode(event.getKeyCode());
				Log.i("TTSS", "keycode=" + code);
				ProcessKeyFunc(code);
			}

		}
		return false;
	}

	@Override
	public void serial_KeyDo(int keyCode) {
		super.serial_KeyDo(keyCode);
		ProcessKeyFunc(keyCode);
	}

	@Override
	public void serial_CardDo(String cardNum) {
		super.serial_CardDo(cardNum);
		if (ManagerUtil.isThisActivityTop(this, Constant.INDEX_ACTIVITY_NAME)) {
			IndexActivity.this.finish();
			Intent intent = new Intent();
			intent.setClass(IndexActivity.this, BillQueryActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}

	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.v(TAG,"onStop");
	}

}
