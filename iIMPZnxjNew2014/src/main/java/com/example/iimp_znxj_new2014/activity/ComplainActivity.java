package com.example.iimp_znxj_new2014.activity;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.http.Header;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iimp_znxj_new2014.DianJiaoApplication;
import com.example.iimp_znxj_new2014.R;
import com.example.iimp_znxj_new2014.dao.FileHelper;
import com.example.iimp_znxj_new2014.dao.FingerPrintUtil;
import com.example.iimp_znxj_new2014.dao.FingerPrintUtil.ParseListener;
import com.example.iimp_znxj_new2014.util.Constant;
import com.example.iimp_znxj_new2014.util.PreferenceUtil;
import com.example.iimp_znxj_new2014.util.UnitaryCodeUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ComplainActivity extends BaseActivity implements ParseListener {

	DianJiaoApplication gv;
	int lrint = 0, udint = 0;
	int[] Rid = new int[] { R.id.complain_top_imageView1, R.id.complain_top_imageView2, R.id.complain_top_imageView3 };
	int[] Rdraw = new int[] { R.drawable.complain_gj, R.drawable.complain_sz, R.drawable.complain_jcg };
	int[] Rdrawsel = new int[] { R.drawable.complain_gj_h, R.drawable.complain_sz_h, R.drawable.complain_jcg_h };
	ImageView image[] = new ImageView[3];

	// private String URL =
	// "http://192.168.1.253:6700/androidadmin/webservice/Appointment.aspx";
	private String URL = null;
	private final static String TAG = "ComplainActivity";
	private Dialog dialog = null;
	private int Confirm_BeSure = 1;
	private int Confirm_DoNo = 2;
	private int Confirm_DoYes = 3;
	private String[] appointmentArry = { "管教", "所长", "检查官" };
	private String[] appointmentCode = { "1", "2", "3" };

	private boolean flag = false;
	private boolean mark = false;
	private StringBuilder cardIdSb = new StringBuilder();
	private StringBuilder goodsSb = new StringBuilder();

	private int[] idCode = new int[12];
	private int[] idNumber = new int[10];
	private int num = 0;
	private String sIdstr = null;

	TextView timestr;
	Handler handler;
	SimpleDateFormat tformat, dformat, wformat;
	static final int MSG_TIME = 1;

	TextView cellTv;
	Properties prop;
	FileHelper helper;
	String serverIp;
	String serverPort;
	boolean keyFlag = true;

	private FingerPrintUtil parseProcessor;
	private Toast myToast;
	private boolean isShowDig = false;
	private boolean iscard;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_complain);

		helper = new FileHelper(getApplicationContext());
		prop = helper.loadConfig(getApplicationContext(), "/mnt/sdcard/config.properties");
		gv = (DianJiaoApplication) getApplication();
		serverIp = PreferenceUtil.getSharePreference(ComplainActivity.this, Constant.CONFIGURE_INFO_SERVERIP);
		serverPort = PreferenceUtil.getSharePreference(ComplainActivity.this, Constant.CONFIGURE_INFO_PORT);

		URL = "http://" + serverIp + ":" + serverPort +"/"+ Constant.SERVER_PART+"Appointment.aspx";

		Log.e("URL", URL);
		parseProcessor = FingerPrintUtil.getInstance(ComplainActivity.this);
		parseProcessor.registerParseListener(ComplainActivity.this);
		parseProcessor.setAutoIdentify();

		fillImageButton();
		image[0].setImageResource(Rdrawsel[0]);

		dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setOnKeyListener(new dialogKeyListener());

		new Thread(new MyThread()).start();
		// changePage();
		handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MSG_TIME:
					String d = dformat.format(new Date());
					String t = tformat.format(new Date());
					String w = wformat.format(new Date());
					timestr.setText(d + " " + t + " " + w);
				}
				super.handleMessage(msg);
			}
		};
	}

	public class dialogKeyListener implements OnKeyListener {
		@Override
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) { 

			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				
				ProcessSwingCard(keyCode);
				Log.v(TAG, "digon keyDown"+"  keyCode="+keyCode);
			}
			if(keyCode==111){
				isShowDig=false;
			}
			return false;
		}
	}
	

	public class MyThread implements Runnable { // thread
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.complain, menu);
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		keyCode = UnitaryCodeUtil.UnitaryCode(keyCode);
		Log.v(TAG, "onkeyDown");
		ProcessKeyFunc(keyCode);
		return false;
	}

	
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	private void fillImageButton() {
		timestr = (TextView) this.findViewById(R.id.complain_top_textView3);
		tformat = new SimpleDateFormat("HH:mm");
		dformat = new SimpleDateFormat("yyyy-MM-dd");
		wformat = new SimpleDateFormat("E");

		cellTv = (TextView) this.findViewById(R.id.complain_top_textView2);
		String monitorNum = PreferenceUtil.getSharePreference(ComplainActivity.this, Constant.CONFIGURE_INFO_CELLNUMBER);
		cellTv.setText("|       监房" + monitorNum);

		for (int j = 0; j < 3; j++) {
			image[j] = (ImageView) this.findViewById(Rid[j]);
			image[j].setImageResource(Rdraw[j]);
		}
	}

	private void ProcessSwingCard(int keyCode) {
		if (keyCode == 74) // start
		{
			flag = true;
		}
		if (keyCode == 76) // end
		{
			flag = false;
			num = 0;
		}
		if (flag) {
			idCode[num] = keyCode;
			Log.i(TAG, num + "-->idCode[" + num + "]=" + keyCode);
			num++;
		}
		if (num == 11) {
			for (int i = 1; i <= 10; i++) {
				idNumber[i - 1] = idCode[i] - 7;// idNumber.length
												// =10[0~9];idCode.length=12[0~11];
			}
			for (int x : idNumber) {
				cardIdSb.append(x);
			}
			sIdstr = cardIdSb.toString();
			doNetWorkSendMsg(appointmentCode[lrint], sIdstr);
			cardIdSb.delete(0, cardIdSb.length());

			new Thread(new Runnable() {
				public void run() {
					try {
						Thread.sleep(2000);
						if(dialog.isShowing()){
							dialog.dismiss();
							isShowDig=false;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}).start();

			num = 0;
		}
		;
	}

	public void doNetWorkSendMsg(String orderPerson, String cardsMsg) {

		Log.e("预约", orderPerson+cardsMsg);
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		Log.v(TAG, "orderPerson=" + orderPerson + " cardsMsg=" + cardsMsg + " URL=" + URL);
		params.put("appointMent", orderPerson);
		params.put("cardId", cardsMsg);
		params.put("type", "1");
		params.setContentEncoding("UTF-8");
		client.post(URL, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, Header[] headers, byte[] responseBody) {
				try {
					String getXmlStr = new String(responseBody, "UTF-8").trim();
					Toast.makeText(getApplicationContext(), " " + getXmlStr + "," + "本页面2s后将自动关闭！", Toast.LENGTH_LONG)
							.show();
					getXmlStr = null;
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				Toast.makeText(getApplicationContext(), "网络延时，预约失败...", Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void ProcessKeyFunc(int keyCode) {
		Log.v(TAG, keyCode+"");
		Log.v(TAG, dialog.isShowing()+"");
		fillImageButton();

		iscard=false;
		if (keyCode == 74) {
			keyFlag = false;
		}
		if (keyCode == 69 && ! keyFlag ) {
			keyFlag = true;
			iscard=true;
		}
		if (keyFlag && !iscard) {
			if (gv.getmKey_Right() == keyCode) {
				lrint = lrint + 1;
				if (lrint > 2)
					lrint = 0;
			}
			if (gv.getmKey_Left() == keyCode) {
				lrint = lrint - 1;
				if (lrint < 0)
					lrint = 2;
			}
			if (gv.getmKey_Up() == keyCode) {

			}
			if (gv.getmKey_Down() == keyCode) {

			}
			if (gv.getmKey_Enter() == keyCode) {
				showDialogWin(Confirm_BeSure);
				isShowDig = true;
			}
		}

		image[lrint].setImageResource(Rdrawsel[lrint]);
		if (gv.getmKey_Esc() == keyCode) {
			if (isShowDig) {
				dialog.dismiss();
				isShowDig = false;
			} else {
				changePage(0);
			}
		}
	}

	public void showDialogWin(int flag) {

		LayoutInflater li;
		View v = null;
		li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

		switch (flag) {
		case 1:
			v = li.inflate(R.layout.complain_confirm_besure, null);
			TextView ed1, ed2, ed3;
			ed2 = (TextView) v.findViewById(R.id.complain_sure_item_textView2);

			ed3 = (TextView) v.findViewById(R.id.complain_sure_item_TextView3);
			ed3.setText(appointmentArry[lrint]);
			break;
		default:
			break;

		}
		dialog.setContentView(v);

		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CENTER);
		lp.x = 45;
		lp.y = 50;
		lp.width = 680;
		lp.height = 408;
		lp.alpha = 0.9f;
		dialogWindow.setAttributes(lp);
		dialog.show();
	}

	public void changePage(int menustr) {
		this.finish();
		isShowDig = false;
		Intent intent = new Intent();
		intent.setClass(ComplainActivity.this, IndexActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);

		getWindow().setWindowAnimations(2000);
		overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
	}

	public void DisplayToast(String str) {
		if (myToast != null) {
			myToast.setText(str);
		} else {
			myToast = Toast.makeText(ComplainActivity.this, str, Toast.LENGTH_SHORT);
		}
		myToast.show();
	}

	@Override
	public void onParseFinished(String result) {

		if (result != null && isShowDig) {
			if (result.equals("false")) {
				DisplayToast("比对失败!");
			} else if (result.equals("same")) {
				DisplayToast("重复的指纹信息,请松开重按!");
			} else if (isShowDig) {
				DisplayToast("比对成功!");
				doNetWorkSendMsg(appointmentCode[lrint], result);
				new Thread(new Runnable() {
					public void run() {
						try {
							Thread.sleep(2000);
							dialog.dismiss();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
		}
	}

	@Override
	public void serial_KeyDo(int keyCode) {

		ProcessKeyFunc(keyCode);
	}

	@Override
	public void serial_CardDo(String cardNum) {
		doNetWorkSendMsg(appointmentCode[lrint], cardNum);
		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(2000);
					if (isShowDig) {
						dialog.dismiss();
						isShowDig=false;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

}
