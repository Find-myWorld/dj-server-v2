package com.example.iimp_znxj_new2014.activity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.http.Header;

import com.example.iimp_znxj_new2014.DianJiaoApplication;
import com.example.iimp_znxj_new2014.R;
import com.example.iimp_znxj_new2014.entity.PersonBaseInfo;
import com.example.iimp_znxj_new2014.entity.PersonBaseInfoList;
import com.example.iimp_znxj_new2014.util.Constant;
import com.example.iimp_znxj_new2014.util.PreferenceUtil;
import com.example.iimp_znxj_new2014.util.UnitaryCodeUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.wits.serialport.SerialPort;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/*
 * 点名
 */
public class RollCallActivity extends BaseActivity implements OnInitListener {

	private ArrayList<Map<String, Object>> cardlist = new ArrayList<Map<String, Object>>();

	DianJiaoApplication gv;
	GridView gview;
	String[] names = new String[20];

	TextView timeTv;
	String URL = null;
	String startURL = null;
	String endURL = null;

	/**
	 * 0表示本地比对，1表示本地比对+刷卡
	 */
	private String type = "0";

	int n = 20;
	// int duration = 20;//300s = 5min,距离结束还有 35 秒
	String TAG = "RollCallActivity";

	boolean flag = false;
	boolean mark = false;
	int[] idCode = new int[12];
	int[] idNumber = new int[10];
	StringBuilder cardIdSb = new StringBuilder(); // 数组转化为字符串；卡号信息
	StringBuilder goodsSb = new StringBuilder();
	int num = 0;
	String sIdstr = null;

	String split1;
	String split2;
	String split3;
	Dialog dialog = null;
	String getNames = "";
	int getNumber = 0;
	String getNumberStr = "00";
	String[] numbers = new String[20];
	private TextToSpeech tts;

	int count = 0;
	boolean isTime = true;

	/**
	 * 是否提前结束点名 true表示是 false表是否
	 */
	private boolean isEnd = false;

//	Properties prop;
//	Properties timeprop;
//	FileHelper helper;
	String serverIp, getIp, getPort;
	double duration = 1;
	double compareNum;

	private PowerManager.WakeLock wakeLock;

	private PersonBaseInfo info = null;
	private PersonBaseInfoList infolist = null;

	private boolean isShowDig = false;
	public static SerialPort mSerialPort;
	public static OutputStream mOutputStream;
	public static InputStream mInputStream;

	private StatusReceiver mStatusReceiver;

	private Handler mHandler;
	private boolean isFinish = true;

	private Map<String, Object> map;
	private boolean hasDianMing = false;

	public class StatusReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String actionStr = intent.getAction();
			Log.d(TAG, "actionStr = " + actionStr);
			if (Constant.STOPPLAY_ACTION.equals(actionStr)) {
				Log.v(TAG, "stopPlay广播");
				changePage(0);
			}
			if ("com.example.name.end".equals(actionStr)) {
				isEnd = true;
				Log.v(TAG, "提前结束");
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_roll_call);

		type = PreferenceUtil.getSharePreference(RollCallActivity.this,
				Constant.ROLL_CALL_TYPE);
		String roomId = PreferenceUtil.getSharePreference(
				RollCallActivity.this, Constant.CONFIGURE_INFO_CELLNUMBER);
		Log.i("TEST", "onCreate=" + type);

		gv = (DianJiaoApplication) getApplication();
//		helper = new FileHelper(getApplicationContext());
		
		getIp = PreferenceUtil.getSharePreference(RollCallActivity.this,
				Constant.CONFIGURE_INFO_SERVERIP);
		getPort = PreferenceUtil.getSharePreference(RollCallActivity.this,
				Constant.CONFIGURE_INFO_PORT);

		if (PreferenceUtil.getSharePreference(RollCallActivity.this,
				Constant.CONFIGURE_INFO_DURATION) == null
				|| PreferenceUtil.getSharePreference(RollCallActivity.this,
						Constant.CONFIGURE_INFO_DURATION).equals("")) {
			duration = 1 * 60;
		} else {
			duration = Integer.parseInt(PreferenceUtil.getSharePreference(
					RollCallActivity.this, Constant.CONFIGURE_INFO_DURATION)) * 60;
		}

		sendRollCallSeconds(duration);

		compareNum = duration - 2;

		serverIp = "http://" + getIp + ":" + getPort + "/";

		URL = serverIp + Constant.SERVER_PART + "getpersonsbyroom.aspx?roomid="
				+ roomId;
		startURL = serverIp + Constant.SERVER_PART + "RollCallStart.aspx";
		endURL = serverIp + Constant.SERVER_PART + "RollCallEnd.aspx";

		Log.i("TEST", "链接：" + URL+",roomId="+roomId);

		doNetWork("ThreeInfoPw");

		gview = (GridView) findViewById(R.id.RollCall_gridView1);
		timeTv = (TextView) findViewById(R.id.RollCall_textView1);
		tts = new TextToSpeech(this, this);

		dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		Message message = handler.obtainMessage(1);
		handler.sendMessageDelayed(message, 1000);
		message.what = 1;

		/*
		 * 广播注册
		 */
		mStatusReceiver = new StatusReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.STOPPLAY_ACTION);
		filter.addAction("com.example.name.end");
		registerReceiver(mStatusReceiver, filter);

		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {

				switch (msg.what) {
				case 2:
					if (isFinish) {
						Toast.makeText(getApplicationContext(),
								"网络连接异常,点名统计失败!", Toast.LENGTH_SHORT).show();
						changePage(0);
					}
					break;
				default:
					break;
				}
				super.handleMessage(msg);
			}
		};
	}

	/*
	 * 将点名时间发送服务端
	 */
	public void sendRollCallSeconds(double rollCallSeconds) {

		String rollCallSecondssss = "{'rollCallSeconds':'" + rollCallSeconds
				+ "'}";
		Log.v("test", rollCallSeconds + "");
		rollCallSecondssss = rollCallSecondssss.replace("\'", "\"");
	}

	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case 2:
				if (!isPaused) {
//					sIdstr = msg.obj.toString();
//					Log.i(TAG, "myHandler....卡号为：" + sIdstr);
//					useStartsWithout0 = true;
//					doNetWorkSendMsg(sIdstr);
//					// 刷卡操作
				

					sIdstr = msg.obj.toString();
					sIdstr = removeStarts0(sIdstr);
					Log.e("mammama", sIdstr);
					Log.i(TAG, "myHandler....卡号为：" + sIdstr);
					useStartsWithout0 = true;
					if ("1".equals(type)) {
						doNetWorkNow(sIdstr);
						Log.e("mammama", sIdstr);
					}

					// Map<String, Object> map;
					hasDianMing = false;

					for (int i = 0; i < cardlist.size(); i++) {

						if (cardlist.get(i).get("cardId").equals(sIdstr)) {
							hasDianMing = true;
						}

					}
					if (hasDianMing) {
						tts.speak("您已签到！请勿重复操作", TextToSpeech.QUEUE_FLUSH, null);
					} else {
						compare(sIdstr);

					}
					// 刷卡操作
				
			
			}
				break;
			}
			super.handleMessage(msg);
		}
	};

	private boolean useStartsWithout0 = false;

	// 验证该卡号与当前监室是否匹配，返回值为true或false
	public void doNetWorkSendMsg(final String cardsMsg) {

		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("cardNumber", cardsMsg);
		params.setContentEncoding("UTF-8");
		client.post(startURL, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, Header[] headers,
					byte[] responseBody) {
				try {
					String getStr = new String(responseBody, "UTF-8").trim(); // 获取xml
					Log.i(TAG, "刷卡后返回的内容为：" + getStr); // 返回内容的格式为：人名，true
														// 如：张三，true 或
														// false，false表示查无此人

					String[] split = getStr.split(",");
					split1 = split[0];
					tts.speak(split1, TextToSpeech.QUEUE_FLUSH, null);// 语音播报
					
					for (int i = 0; i < nameList.size(); i++) {
						if ((split1 + " ").equals(nameList.get(i))) {
							// Log.e(TAG,names[i]);
							// Log.e(TAG,names[i]);
							// if("" != names[i])
							// {
							nameList.set(i, "");

							getNumber++;
							//
							// }
							// else{
							//
							// tts.speak("已刷卡请勿重复操作", TextToSpeech.QUEUE_FLUSH,
							// null);//语音播报
							//
							// }

						}
					}
					split2 = split[1];
					split3 = split[2];
					getNumberStr = getNumberStr + "," + split3;
					Log.i(TAG, "三段内容分别为：" + split1 + "|" + split2 + "|"
							+ split3 + ";" + getNumberStr);
					complite(split1);

				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				if (useStartsWithout0) {
					String cardsMsg_replaced = UnitaryCodeUtil
							.removeStarts0(cardsMsg);
					doNetWorkSendMsg(cardsMsg_replaced);
					Log.v(TAG, "去掉开头的0后卡号：" + cardsMsg_replaced);
					useStartsWithout0 = false;
				} else {
					Toast.makeText(getApplicationContext(), "网络延时，与服务器连接失败...",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	protected void onStart() { // 点亮屏幕
		Log.i(TAG, "ActivityPop_onStart");
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
				| PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");// /
		wakeLock.acquire();
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		Log.v(TAG, "onDestroy");
		unregisterReceiver(mStatusReceiver);// 可能报错
		super.onDestroy();
		if (tts != null) {
			tts.shutdown();
		}
	}

	private boolean isPaused = false;

	@Override
	protected void onPause() {
		isPaused = true;
		try {
			wakeLock.release();
		} catch (Throwable th) {
		}
		if (handler.hasMessages(1)) {
			handler.removeMessages(1);
		}
		super.onPause();
	}

	// 点名结束之后返回未点名人员的名单
	public void doNetWorkGetName(String numbers) {
		Log.e(TAG, numbers);
		Log.e(TAG, endURL);
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("callEnd", numbers);// /
		params.setContentEncoding("UTF-8");
		client.post(endURL, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, Header[] headers,
					byte[] responseBody) {
				Log.e(TAG, "访问服务器成功");
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {

				Log.i(TAG, "访问服务器失败");
				isFinish = false;
				Toast.makeText(getApplicationContext(), "链接服务器失败！请稍后...",
						Toast.LENGTH_SHORT).show();
				changePage(0);
			}
		});
	}

	// 发起实时点名，即实时返回请求ID；
	public void doNetWorkNow(String numbers) {
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("cardNumber", numbers);
		params.put("type", "1");
		// params.put("callEnd", numbers);///
		params.setContentEncoding("UTF-8");
		Log.i("TTSS", "发起请求doNetWorkGetName");

		client.post(startURL, params, new AsyncHttpResponseHandler() {
			@SuppressWarnings("unused")
			@Override
			public void onSuccess(int arg0, Header[] headers,
					byte[] responseBody) {
				isFinish = false;
				Log.i("TTSS", "访问服务器成功");
				try {
					String getXmlName = new String(responseBody, "UTF-8")
							.trim(); // 获取xml
					Log.i("TTSS", "getXmlName=" + getXmlName);
					if (getXmlName.equals("")) {
						Log.i(TAG, "empty");
					}
					if (getXmlName == null) {
						Log.i(TAG, "null");
					}
					String[] split = getXmlName.split(",");
					if (split.length >= 2) {
						// getNames = split[0];
						// getNumber = split[1];
					} else {
						isFinish = true;
						changePage(0);
						Toast.makeText(RollCallActivity.this, "返回结果出错,退出点名!",
								Toast.LENGTH_SHORT).show();
					}
					Log.i(TAG, "点名结束之后返回的内容为：" + getXmlName + ",getNames="
							+ getNames + ",getNumber=" + getNumber); // 返回内容的格式为：人名，true
																		// 如：张三，true
																		// 或
																		// false，false表示查无此人
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {

				Log.i("TTSS", "访问服务器失败");
				isFinish = false;
				Toast.makeText(getApplicationContext(), "链接服务器失败！请稍后...",
						Toast.LENGTH_SHORT).show();
				changePage(0);

			}

		});

	}

	// 弹出对话框
	public void showDialogWin(int flag) {
		// setContentView可以设置为一个View也可以简单地指定资源ID
		LayoutInflater li;
		View v = null;
		li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

		switch (flag) {
		case 1:
			v = li.inflate(R.layout.rollcall_end_tip, null);
			TextView rolled1,
			rolled2;
			int n = 0;

			n = count - getNumber;
			final TextView rolled3;
			rolled1 = (TextView) v.findViewById(R.id.rollcall_item_textView1);
			rolled2 = (TextView) v.findViewById(R.id.rollcall_item_textView2);
			rolled3 = (TextView) v.findViewById(R.id.rollcall_item_textView3);

			Log.i(TAG, "count=" + count + ",n=" + n);
			rolled1.setText("" + count);
			rolled2.setText("" + getNumber);
			Log.i(TAG, "getNmae1=" + getNames);

			for (int i = 0; i < nameList.size(); i++) {
				if (nameList.get(i) != "") {
					getNames = getNames + nameList.get(i) + ",";
				}
			}
			rolled3.setText(getNames);

			new Thread(new Runnable() { // 弹出对话框5s后关闭
				public void run() {
					try {
						Thread.sleep(5000);
						dialog.dismiss(); // 关闭对话框后，退出点名界面
						// Thread.sleep(2000);
						changePage(0);

					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}).start();
			break;
		default:
			break;
		}

		dialog.setContentView(v);
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL
				| Gravity.CENTER_VERTICAL);
		// lp.x = 20; // 新位置X坐标
		// lp.y = 100; // 新位置Y坐标
		lp.width = 680; // 宽度
		lp.height = 408; // 高度
		lp.alpha = 0.9f; // 透明度
		dialogWindow.setAttributes(lp);
		dialog.show();
	}

	// 刷卡后获取该卡号对应的人名，找到这个人对应的铺位号，然后做‘勾选’操作
	private void complite(String name) {
		int j=0;
		for (int i = 0; i < nameList.size(); i++) {
			if (nameList.get(i) != null && name.equals(nameList.get(i).trim())) { // 必须加“.trim()”方法，否则判断不出来
				j = i;
			}
		}
		if (j < nameList.size()) {
			ImageView iview = (ImageView) gview.getChildAt(j).findViewById(
					R.id.rollcall_item_imageView1);
			gview.getChildAt(j).setBackgroundResource(R.drawable.itemsel5);
			iview.setImageResource(R.drawable.man1);
		}
	}

	// 获取当前监室的铺位表
	public void doNetWork(final String choice) {
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("requestCommand", choice);
		params.setContentEncoding("UTF-8");
		client.post(URL, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, Header[] headers,
					byte[] responseBody) {
				try {
					String getXmlStr = new String(responseBody, "UTF-8").trim(); // 获取xml
					InputStream is = new ByteArrayInputStream(getXmlStr
							.getBytes());// 将xml转为InputStream类型
					showListView(is); // 解析xml
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {

				Toast.makeText(getApplicationContext(), "网络延时，请稍候......",
						Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	ArrayList<String>  nameList=new ArrayList<String>();
	
	public void showListView(InputStream is) {
//		Log.i("TTSS", "showListView1");
//		for (int i = 0; i < names.length; i++) {
//			names[i] = null;
//		}
//		// BedInfo info = null;
//		// BedInfoList infolist = null;
//		try {
//			if (is != null) {
//				infolist = PersonBaseInfoList.parse(is);
//				is.close();
//			}
//			Log.i("TTSS", "长度==>" + infolist.m_list.size());
//			for (int i = 0; i < infolist.m_list.size(); i++) {
//				info = infolist.m_list.get(i);
//				names[i] = info.m_name;
//				Log.i("TTSS", "参数：" + info.m_name + ",ID=" + info.m_cardId);
//			}
//			InitPage1(); // 显示铺位表
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		

		Log.i("TTSS", "showListView1");
		nameList.clear();
		// BedInfo info = null;
		// BedInfoList infolist = null;
		try {
			if (is != null) {
				infolist = PersonBaseInfoList.parse(is);
				is.close();
			}
			Log.i("TTSS", "长度==>" + infolist.m_list.size());
			for (int i = 0; i < infolist.m_list.size(); i++) {
				info = infolist.m_list.get(i);
				nameList.add(i,info.m_name) ;
				Log.i("TTSS", "参数：" + info.m_name + ",ID=" + info.m_cardId);
			}
			
			
			InitPage1(); // 显示铺位表

		} catch (Exception e) {
			Log.e("Error", e.toString());
			e.printStackTrace();
		}
	
	}

	// 统计当前监房的人数
	private void InitPage1() {
		/*
		 * // int n=20; for (int i = 0; i < 20; i++) { // names[i]=
		 * names[i]+" ";//+numbers[i]; if (names[i] != null) { count++; } }
		 */
//		for (int i = 0; i < 20; i++) {
//			// names[i]= names[i]+"张三"+String.valueOf(i+1);
//			if (names[i] == null || names[i].equals("")) {
//				Log.i(TAG, "???,i=" + i);
//				names[i] = "";
//			} else {
//				Log.i("TTSS", "count=" + count + ",i=" + i);
//				count++;
//				names[i] = names[i] + " ";// +numbers[i];
//			}
//		}
//		Log.i(TAG, "该监房人数，count=" + count);
//		gview.setAdapter(new GridViewAdapter());
//		Drawable drawable = getResources().getDrawable(
//				R.drawable.message_item_list_sytle);
//		gview.setSelector(drawable);
		

		/*
		 * gview =
		 * (GridView)listViews.get(0).findViewById(R.id.three_item1_gridView1);
		 * int n=20;
		 */

		for (int i = 0; i < nameList.size(); i++) {
			// names[i]= names[i]+"张三"+String.valueOf(i+1);
			if (nameList.get(i) == null || nameList.get(i).equals("")) {
				nameList.add(i, "");
			} else {
				count++;
				nameList.set(i, nameList.get(i)+ " ")  ;// +numbers[i];
			}
		}
		// names=names;
		gview.setAdapter(new GridViewAdapter());
		Drawable drawable = getResources().getDrawable(
				R.drawable.message_item_list_sytle);
		gview.setSelector(drawable);
		
//		jumpToFingerPrint(RollCallActivity.this, true);//
//		Intent intent = new Intent(RollCallActivity.this,HD_MainActivity.class);
//		intent.putExtra(Constant.COMPARISON_METHOD,true);
//    	intent.putExtra(Constant.IS_ROLLCALL_ACTIVITY_VALUE, true);   //
//		startActivityForResult(intent, Constant.FP_INTENT_JUMP_VALUE);
	
	}

	/*
	 * 功能：实时显示剩余的点名时间，当时间为0时，弹出对话框，显示未点到人员信息
	 * 注：这里有一个请求服务器操作（有延时），必须提前请求服务器，不然弹出的对话框显示的信息不全
	 */

	final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				duration--;
				if (isTime) {
					timeTv.setText("距离结束还有 " + duration + " 秒");
				}
				if (duration == compareNum) {
					tts.speak("在押人员点名开始:", TextToSpeech.QUEUE_FLUSH, null);
				}
				if (isTime && duration == 0) {
					timeTv.setText("点名结束，稍候将统计点名情况...");
					mHandler.sendEmptyMessageDelayed(2, 6000);

					Log.i("END", "最后的结果：" + getNumberStr);
					isTime = false;
				}
				if (duration > -2 && !isEnd) {
					Message message = handler.obtainMessage(1);
					message.what = 1;
					handler.sendMessageDelayed(message, 1000);
				} else {
					Log.i(TAG, "发送命令前，getNumberStr = " + getNumberStr);
					// doNetWorkGetName(getNumberStr);
					// //弹出窗口时发送命令给服务器，获取未点到名的人员名单
					isShowDig = false;
					isFinish = false;
					timeTv.setText("点名结束，稍候将统计点名情况...");
					// doNetWorkGetName(getNumberStr);
					// //弹出窗口时发送命令给服务器，获取未点到名的人员名单
					if (!isFinish) {
						showDialogWin(1);
						doNetWorkGetName(getNumberStr);
					}
				}
			}
			super.handleMessage(msg);
		}
	};

	static class ViewHolder {
		private TextView text1;
		private ImageView imgview;
	}

	public class GridViewAdapter extends BaseAdapter {

//		public int getCount() {
//			return names.length;
//		}
//
//		public View getItem(int position) {
//			return null;
//		}
//
//		public long getItemId(int position) {
//			return position;
//		}
//
//		public View getView(int position, View convertView, ViewGroup parent) {
//			ViewHolder holder;
//			if (convertView == null) {
//				LayoutInflater inflater = (LayoutInflater) RollCallActivity.this
//						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//				// 使用View的对象itemView与R.layout.item关联
//				convertView = inflater.inflate(R.layout.rollcall_item_list,
//						null);
//				holder = new ViewHolder();
//				// 通过findViewById()方法实例R.layout.item内各组件
//				holder.text1 = (TextView) convertView
//						.findViewById(R.id.rollcall_item_textView1);
//				holder.imgview = (ImageView) convertView
//						.findViewById(R.id.rollcall_item_imageView1);
//				convertView.setTag(holder);
//			} else {
//				holder = (ViewHolder) convertView.getTag();
//			}
//			holder.text1.setText(position + 1 + " " + names[position]);
//			return convertView;
//		}
		


		public int getCount() {
			return nameList.size();
		}

		public View getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) RollCallActivity.this
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				// 使用View的对象itemView与R.layout.item关联
				convertView = inflater.inflate(R.layout.rollcall_item_list,
						null);
				holder = new ViewHolder();
				// 通过findViewById()方法实例R.layout.item内各组件
				holder.text1 = (TextView) convertView
						.findViewById(R.id.rollcall_item_textView1);
				holder.imgview = (ImageView) convertView
						.findViewById(R.id.rollcall_item_imageView1);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.text1.setText(position + 1 + " " + nameList.get(position));
			return convertView;
		}
	
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	// 对刷卡的人做“打勾”操作
	private void setMark(int KeyCode) { // i{0-19}
		int i = KeyCode - 7;
		ImageView iview = (ImageView) gview.getChildAt(i).findViewById(
				R.id.rollcall_item_imageView1);
		gview.getChildAt(i).setBackgroundResource(R.drawable.itemsel5);
		iview.setImageResource(R.drawable.man1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.roll_call, menu);
		return true;
	}

	@Override
	public void onInit(int status) { // 语音引擎初始化
		Log.i(TAG, "onInit");
		if (status == TextToSpeech.SUCCESS) {
			int result = tts.setLanguage(Locale.CHINA);
			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Toast.makeText(RollCallActivity.this,
						"Language is not available.", Toast.LENGTH_LONG).show();
			}
		}
	}

	/*
	 * 页面跳转
	 */
	public void changePage(int menustr) {
		Log.i(TAG, "changePage");
		RollCallActivity.this.finish();
		Intent intent = new Intent();
		intent.setClass(this, IndexActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	/*
	 * USB按键处理
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			getUSBCardNum(event);
		}
		return false;
	}

	public static String removeStarts0(String cardNum) {
		return cardNum.replaceFirst("^0+", "");
	}

	StringBuilder cardNumSB = new StringBuilder();
	boolean startGetCardNum = false;

	public void getUSBCardNum(KeyEvent event) {
		int code = event.getKeyCode();
		if (code == 74) // start
		{
			flag = true;
		}
		if (code == 76) // end
		{
			flag = false;
			num = 0;
		}
		if (flag) {
			idCode[num] = code;
			Log.i(TAG, num + "-->idCode[" + num + "]=" + code);
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

			sIdstr = removeStarts0(sIdstr);
			// cardTv.setText("当前卡号为:"+sIdstr); //显示当前卡号信息
			Log.i(TAG, "卡号为：" + sIdstr + ",num=" + num);

			if ("1".equals(type)) {
				doNetWorkNow(sIdstr);
				Log.e("mammama", sIdstr);
			}

			// Map<String, Object> map;
			hasDianMing = false;

			for (int i = 0; i < cardlist.size(); i++) {

				if (cardlist.get(i).get("cardId").equals(sIdstr)) {
					hasDianMing = true;
				}

			}
			if (hasDianMing) {
				tts.speak("您已签到！请勿重复操作", TextToSpeech.QUEUE_FLUSH, null);
			} else {
				compare(sIdstr);

			}

			// 不通过后台请求比对人员信息。直接前台比对。
			// doNetWorkSendMsg(sIdstr,1); //发送卡号给服务器
			cardIdSb.delete(0, cardIdSb.length()); // 清空卡号信息StringBuilder，否则累加
			num = 0;
		}

	}

	private void compare(String sIdstr) {
//		PersonBaseInfo infoP = null;
//		boolean isExist = false;
//		if (infolist != null) {
//			for (int i = 0; i < infolist.m_list.size(); i++) {
//				infoP = infolist.m_list.get(i);
//				if (sIdstr.equals(infoP.m_cardId)) {
//					isExist = true;
//					complite(infoP.m_name);
//					names[i] = "";
//					getNumberStr = getNumberStr + infoP.m_id + ",";
//					tts.speak(infoP.m_name, TextToSpeech.QUEUE_FLUSH, null);
//				}
//
//			}
//			if (!isExist) {
//				tts.speak("查无此卡！", TextToSpeech.QUEUE_FLUSH, null);
//			} else {
//
//				getNumber++;
//
//			}
//		}
		

		PersonBaseInfo infoP = null;
		boolean isExist = false;
		for (int i = 0; i < infolist.m_list.size(); i++) {

			infoP = infolist.m_list.get(i);
			if (sIdstr.equals(infoP.m_cardId)) {
				isExist = true;
				complite(infoP.m_name);
				nameList.set(i, "") ;
				getNumberStr = getNumberStr + infoP.m_id + ",";
				tts.speak(infoP.m_name, TextToSpeech.QUEUE_FLUSH, null);

				map = new HashMap();
				map.put("cardId", sIdstr);
				cardlist.add(map);
			}

		}
		if (!isExist) {
			tts.speak("查无此卡！", TextToSpeech.QUEUE_FLUSH, null);
		} else {

			getNumber++;

		}

	
	}

	@Override
	public void serial_KeyDo(int keyCode) {

		super.serial_KeyDo(keyCode);
	}

	@Override
	public void serial_CardDo(String cardNum) {

		super.serial_CardDo(cardNum);
		Message msg = new Message();
		msg.what = 2;
		msg.obj = cardNum;
		myHandler.sendMessage(msg);
	}

}
