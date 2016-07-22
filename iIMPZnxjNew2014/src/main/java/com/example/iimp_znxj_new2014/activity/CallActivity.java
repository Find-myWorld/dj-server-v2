package com.example.iimp_znxj_new2014.activity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.apache.http.Header;

import com.example.iimp_znxj_new2014.DianJiaoApplication;
import com.example.iimp_znxj_new2014.R;
import com.example.iimp_znxj_new2014.dao.FingerPrintUtil;
import com.example.iimp_znxj_new2014.entity.AgentInfo;
import com.example.iimp_znxj_new2014.entity.AgentInfoList;
import com.example.iimp_znxj_new2014.util.AlarmUtil;
import com.example.iimp_znxj_new2014.util.Constant;
import com.example.iimp_znxj_new2014.util.PreferenceUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;



/* * 交接班提醒
 * @title CallActivity.java
 * @description 交接班提醒，5个时间段，每次提前5分钟提醒，5分钟内刷卡签到有效
 * <p>5分钟之内不能息屏，否则会回到主页</p>配合新后台使用会在后台配置是获取{"action":"onduty","data":{"beginhour":"16","beginminute":"11","interval":"2","cycles":"3"}}
 * 在页面切换时获取时间 
 */
public class CallActivity extends BaseActivity implements OnInitListener {

	private static int LONG_TIME = 0;
	private static final String TAG = "CallActivity";

	private boolean flag = false;
	private boolean mark = false;
	private int num = 0;
	private StringBuilder cardIdSb = new StringBuilder(); // 数组转化为字符串；卡号信息
	// private StringBuilder goodsSb = new StringBuilder(); //数组转化为字符串；商品id +
	// 商品num
	private int[] idCode = new int[12]; // 记住卡的信息Code
	private int[] idNumber = new int[10]; // Code转化为Number(编码转数字)
	private String sIdstr = null;

	private String URL = null;
	private String nameURL = null;
	private String numberURL = null;
	private String singerURL = null;
	// private GoodsDao dao;
	private AlarmUtil alarmUtil;
	private String[] nameArray = new String[2]; // 记录卡号的数组

	private String[] zbTimes = new String[5];
	private String[] zbPerson = new String[5]; // 值班人，5组人
	private String[] zbRybh = new String[5]; // 值班人，5组人
	private int count = 0;
	private int zbCount = 0;

	private String[] reminderTime;// = new
									// String[4];//{"20:55","22:55","00:55","02:55","04:55"};
	private String[] durationTime;// = new
									// String[4];//{"21:00-23:00","23:00-1:00","1:00-3:00","3:00-5:00","5:00-7:00"};

	private ArrayList<String> timeList = new ArrayList<String>();
	private ArrayList<String> personList = new ArrayList<String>();
	private ArrayList<String> rybhList = new ArrayList<String>();

	private TextView callTimeTv;
	private TextView callContentTv;
	private TextView timestr; // 时间显示
	private Handler handler;
	private SimpleDateFormat tformat, dformat, wformat;
	private static final int MSG_TIME = 1;
	private TextView cellTv; // 显示当前监房号

	private String serverIp;

	private int starthour; // 值班开始小时
	private int startminute; // 值班开始分钟
	private int interval; // 值班间隔
	private int cycles; // 循环次数

	private PowerManager.WakeLock wakeLock;
	private TextToSpeech tts;

	private FingerPrintUtil parseProcessor;
	private Toast myToast;
	private boolean isShowDig = true;

	private int curDuration = 0; // 值班阶段：1、2、3...
	private String durTime = null; // 时间段：08:00-09:00,10:00-11:00,12:00-13:00

	private String getIp, getPort, getCellNumber;
	private boolean isAdvanceFinish = false;
	private DianJiaoApplication djApplicate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		DianJiaoApplication.getInstance().finishIndexActivities();

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call);

		tts = new TextToSpeech(this, this);
		djApplicate = (DianJiaoApplication) getApplication();

		Intent gIntent = getIntent();
//		curDuration = gIntent.getIntExtra("current_stage", -1);
		curDuration = gIntent.getIntExtra("current_stage", 1);

		getIp = PreferenceUtil.getSharePreference(CallActivity.this,
				Constant.CONFIGURE_INFO_SERVERIP);
		getPort = PreferenceUtil.getSharePreference(CallActivity.this,
				Constant.CONFIGURE_INFO_PORT);

		serverIp = "http://" + getIp + ":" + getPort + "/";

		init();
		String roomId = PreferenceUtil.getSharePreference(CallActivity.this,
				Constant.CONFIGURE_INFO_CELLNUMBER);
		URL = serverIp  + Constant.SERVER_PART + "BackToName.aspx";
		nameURL = serverIp + Constant.SERVER_PART + "getagentinfo.aspx?cid="
				+ roomId;
		numberURL = serverIp + Constant.SERVER_PART + "OnDutyCount.aspx";
		singerURL = serverIp + Constant.SERVER_PART + "SingerCount.aspx";

		Log.i(TAG, "onCreate=>传值为：" + curDuration + " 请求地址:" + nameURL);

		doNetWork("ThreeInfoZb");

		LONG_TIME = 300; // 定义当前Activity定时关闭的时间(秒)||300s
		Message message = handlerTime.obtainMessage(1); // 启动5分钟倒计时
		handlerTime.sendMessageDelayed(message, 300000);

		new Thread(new MyThread()).start(); // 时间更新相关的线程
		// changePage();
		handler = new Handler() { // 时间更新
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MSG_TIME:
					String d = dformat.format(new Date()); // 设置首页时间与日期
					String t = tformat.format(new Date());
					String w = wformat.format(new Date());
					timestr.setText(d + " " + t + " " + w);
				}

				super.handleMessage(msg);
			}
		};
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
	protected void onDestroy() {
		if (wakeLock != null) {

			wakeLock.release();
		}
		super.onDestroy();
		if (tts != null) {
			tts.shutdown();
		}
	}

	/*
	 * 初始化
	 */

	public void init() {
		callTimeTv = (TextView) findViewById(R.id.call_time_tv1);
		callContentTv = (TextView) findViewById(R.id.call_content_tv2);

		timestr = (TextView) this.findViewById(R.id.call_top_textView3);
		timestr.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				isAdvanceFinish = true;
				gotoIndex();
			}
		});
		tformat = new SimpleDateFormat("HH:mm"); // 时：分
		dformat = new SimpleDateFormat("yyyy-MM-dd");// 年-月-日 星期
		wformat = new SimpleDateFormat("E");

		cellTv = (TextView) this.findViewById(R.id.call_top_textView2); // 更改监室号
		String monitorNum = PreferenceUtil.getSharePreference(
				CallActivity.this, Constant.CONFIGURE_INFO_CELLNUMBER);
		cellTv.setText("|       监房" + monitorNum);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	/*
	 * 显示提示信息在界面上
	 * 
	 * @param moment 当前的阶段（1、2、3、4、5）
	 */
	public void show(int moment) {
		Log.i("End", "show，，" + durationTime[0] + "|" + durationTime[2]
				+ "|moment=" + moment + "|" + durationTime[moment - 1]);
		// callTimeTv.setText(reminderTime[moment-1]); //数组从0开始，moment[1~5]-1
		callContentTv.setText("距离下一值班还有5分钟\n值班人：" + zbPerson[moment - 1]
				+ "\n值班时间：" + durationTime[moment - 1]);
		tts.speak("距离下一值班还有5分钟,\n值班人：" + zbPerson[moment - 1] + "\n ,值班时间："
				+ durationTime[moment - 1], TextToSpeech.QUEUE_FLUSH, null); // 刷卡后读出人名
	}

	/*
	 * 新的提示方法
	 */
	public void showContent(int moment) {

		
		String dutyPaltrl=PreferenceUtil.getSharePreference(CallActivity.this, Constant.DUTY_PALTRL,"1");
		if("0".equals(dutyPaltrl)){
			this.finish();
		}
		Log.i("TEST", "curDuration=" + curDuration);
		Log.i("TEST", "值班人=" + personList.get(moment - 1));
		Log.i("TEST", "时间段=" + timeList.get(moment - 1));

		callTimeTv.setText(getBeforFive(timeList.get(curDuration - 1))); // 数组从0开始，moment[1~5]-1
		callContentTv.setText("距离下一值班还有5分钟\n值班人：" + personList.get(moment - 1)
				+ "\n值班时间：" + timeList.get(moment - 1));
		tts.speak("距离下一值班还有5分钟,\n值班人：" + personList.get(moment - 1)
				+ "\n ,值班时间：" + timeList.get(moment - 1),
				TextToSpeech.QUEUE_FLUSH, null); // 刷卡后读出人名
	}

	private String getBeforFive(String time) {
		Log.i(TAG, "getBeforFive.time=" + time);
		String hour = null;
		String minute = null;
		String[] t_split = time.split("-");
		String[] split1 = null;

		if (t_split.length == 2) {

			split1 = t_split[0].split(":");

			if (split1.length == 2) {
				hour = split1[0];
				minute = split1[1];
				// 根据获取的时间，计算提前5分钟的时间节点
				if (hour.equals("00") || hour.equals("0")) {
					hour = "23";
				} else {
					hour = Integer.parseInt(hour) - 1 + "";
				}
				if (Integer.parseInt(minute) < 5
						&& Integer.parseInt(minute) >= 0) {
					minute = 55 + Integer.parseInt(minute) + "";
				} else if (minute.equals("05") || minute.equals("5")) {
					hour = split1[0];
					minute = "00";
				} else {
					hour = split1[0];
					minute = Integer.parseInt(minute) - 5 + "";
				}
			}
			if (minute.length() == 1) {
				return hour + ":0" + minute;
			} else {
				return hour + ":" + minute;
			}

		} else {
			return null;
		}

	}

	// 刷卡键盘监听
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
			doNetWorkSendMsg(sIdstr,1); // 发送卡号给服务器
			cardIdSb.delete(0, cardIdSb.length()); // 清空卡号信息StringBuilder，否则累加
			num = 0;
		}
		;
	}

	@Override
	protected void onStart() {
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
				| PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");// /
		wakeLock.acquire();
		super.onStart();
	}

	/*
	 * 联服务器获取今日值班人员信息
	 */
	public void doNetWork(String choice) {
		Log.v(TAG, "doNetWork");
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("requestCommand", choice);
		params.setContentEncoding("UTF-8");
		Log.v(TAG, nameURL);
		client.post(nameURL, params, new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(int arg0, org.apache.http.Header[] arg1,
					byte[] arg2, Throwable arg3) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "获取值班人员名单失败",
						Toast.LENGTH_SHORT).show();
				isAdvanceFinish = true;
				gotoIndex();
			}

			@Override
			public void onSuccess(int arg0, org.apache.http.Header[] arg1,
					byte[] responseBody) {
				// TODO Auto-generated method stub
				try {
					String getXmlStr = new String(responseBody, "UTF-8").trim(); // 获取xml
					Log.i("End", "getXmlStr=" + getXmlStr);
					InputStream is = new ByteArrayInputStream(getXmlStr
							.getBytes());// 将xml转为InputStream类型
					showListView1(is); // 解析xml
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/*
	 * 解析服务器返回的人员信息
	 */
	public void showListView1(InputStream is) {
		Log.v(TAG, "showListView1...1");
		AgentInfo info = null;
		AgentInfoList infolist = null;
		try {
			if (is != null) {
				Log.v(TAG, "showListView1...2");
				infolist = AgentInfoList.parse(is);
				is.close();
			}
			for (int i = 0; i < infolist.m_list.size(); i++) {
				Log.v(TAG, "showListView1...3");
				info = infolist.m_list.get(i);
				timeList.add(info.m_timeStart + "-" + info.m_timeEnd);
				personList.add(info.m_pname);
				rybhList.add(info.m_pid);
				/*zbTimes[count] = info.m_timeStart + "-" + info.m_timeEnd;
				zbPerson[count] = info.m_pname;
				zbRybh[count] = info.m_pid;*/
				Log.i("TTSS", count + "==>" + info.m_timeStart + ","
						+ info.m_pname + "," + info.m_pid);
				count++;
			}
			Log.i(TAG, "showListView1...4,curDuration:" + curDuration
					+ ",count=" + count);
			if (timeList.size() <= 0 || timeList.size() < curDuration) {
				Log.v(TAG, "showListView1...5");
				Toast.makeText(CallActivity.this, "值班人员或时间未配置,退出当前值班 ！",
						Toast.LENGTH_LONG).show();
				isAdvanceFinish = true;
//				Log.i(TAG, "Error1:值班人员或时间未配置,退出当前界面!");
//				this.finish();
				gotoIndex();
			}
			Log.v(TAG, "showListView1...6");
			showContent(curDuration);
		} catch (Exception e) {
			/*Log.e(TAG, "Error2:" + e.getMessage());
			e.printStackTrace();
			Toast.makeText(CallActivity.this, "值班人员或时间未配置,退出当前值班 ！",
					Toast.LENGTH_LONG).show();*/
			isAdvanceFinish = true;
			e.printStackTrace();
			this.finish();
		}
	}

	/*
	 * 解析服务器返回的人员信息
	 */
	/*public void showListView2(InputStream is) {
		ThreeGdZbAnalyze service = new ThreeGdZbAnalyze();
		try {
			List<ThreeGdZb> newslist = service.getThreeGdZb(is);
			for (ThreeGdZb news : newslist) {
				timeList.add(news.getTime());
				personList.add(news.getPerson());
				zbTimes[count] = news.getTime();
				zbPerson[count] = news.getPerson();
				count++;
				Log.i("TEST", "count=" + count + "," + news.getTime() + ","
						+ news.getPerson());
			}
			Log.i(TAG, "值班时间：" + timeList.get(curDuration - 1));
			if (curDuration > count || timeList.get(curDuration - 1).equals("")) {
				Toast.makeText(CallActivity.this, "值班人员或时间未配置,退出当前界面!",
						Toast.LENGTH_LONG).show();
				isAdvanceFinish = true;
				gotoIndex();
			}
			for (String str : timeList) {
				Log.i(TAG, "值班时间：" + str + ",Length=" + timeList.size());
			}

			showContent(curDuration);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	/*
	 * 联网操作,刷卡获取人名
	 * 
	 * @param cardsMsg 刷卡卡号
	 */
	public void doNetWorkSendMsg(String cardsMsg, int type) {
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		if (type == 1) {
			params.put("cardNumber", cardsMsg);
			params.put("type", "1");
		} else {
			params.put("rybh", cardsMsg);
			params.put("type", "2");
		}
		params.setContentEncoding("UTF-8");
		client.post(URL, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, Header[] headers,
					byte[] responseBody) {
				try {
					String getXmlStr = new String(responseBody, "UTF-8").trim(); // 获取xml
					Log.i("TTSS", "getXmlStr=" + getXmlStr);
					if (getXmlStr != null && !getXmlStr.equals("")) {
						matchToArray(getXmlStr, personList.get(curDuration - 1));
						// matchToArray(getXmlStr,personList.get(curDuration-1));
						// //匹配成功的卡号入库
					} else {
						Toast.makeText(getApplicationContext(), "服务器返回信息为空",
								Toast.LENGTH_LONG).show();
					}

				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "连接服务器失败...",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	/*
	 * 联网操作,刷卡获取人名
	 * 
	 * @param cardsMsg 刷卡卡号
	 */
	public void sendTimeAndCard(String cardsMsg) {
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("cardNumber", cardsMsg);
		Log.i(TAG, "sendTimeAndCard.carsMsg=" + cardsMsg);
		params.setContentEncoding("UTF-8");
		client.post(singerURL, params, new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(int arg0, org.apache.http.Header[] arg1,
					byte[] arg2, Throwable arg3) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "连接服务器失败...",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSuccess(int arg0, org.apache.http.Header[] arg1,
					byte[] responseBody) {
				// TODO Auto-generated method stub
			}
		});
	}

	/*
	 * 联网操作(最后发送全部的卡号信息给服务器）
	 * 
	 * @param cardsContent 全部卡号的信息,格式为：001,002,|003,|004,|005,006,|007,008,
	 * 
	 * public void doNetWorkLast(String cardsContent){ AsyncHttpClient client =
	 * new AsyncHttpClient(); RequestParams params = new RequestParams();
	 * params.put("cardNumber", cardsContent);
	 * params.setContentEncoding("UTF-8"); client.post(numberURL, params, new
	 * AsyncHttpResponseHandler() {
	 * 
	 * @Override public void onFailure(int arg0, org.apache.http.Header[] arg1,
	 * byte[] arg2, Throwable arg3) { // TODO Auto-generated method stub }
	 * 
	 * @Override public void onSuccess(int arg0, org.apache.http.Header[] arg1,
	 * byte[] arg2) { // TODO Auto-generated method stub } }); }
	 */

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == djApplicate.getmKey_Esc()) {

		} else {
			ProcessSwingCard(keyCode);
		}
		return true;
	}

	/*
	 * 倒计时设置，时长5分钟
	 */

	final Handler handlerTime = new Handler() {
		@SuppressWarnings("static-access")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				LONG_TIME--;
//				if (LONG_TIME > 0) {
//					Message message = handlerTime.obtainMessage(1);
//					handlerTime.sendMessageDelayed(message, 1000);
//				} else {
					Log.i(TAG, "点名时间结束退出，发送信息到后台");
					if (!isAdvanceFinish) {
						sendTimeAndCard(getFinalMsg());
					}
					gotoIndex();
					String dutyRemind=PreferenceUtil.getSharePreference(CallActivity.this, Constant.DUTY_REMIND, "0");
					if("1".equals(dutyRemind)){
						AlarmUtil.startBroadCastAlarm(CallActivity.this);
					}
				}
//			}
			super.handleMessage(msg);
		}
	};

	/*
	 * 签到人匹配 <p>当前刷卡人名和当前时间段应值班人的姓名比较，姓名匹配的话，签到成功，数据入库；否则失败<p>
	 * 
	 * @param cardName 当前刷卡人姓名
	 * 
	 * @param arrayName 当前时间段应值班的两人
	 * 
	 * @attention 函数逻辑没有问题，有个问题是网络传输问题，在快速刷正确的卡情况下，容易重复录入信息
	 */
	public void matchToArray(String cardName, String arrayName) {

		// Log.i(TAG,"matchToArray,cardName="+cardName+",arrayName="+arrayName+",sIdstr="+sIdstr);

		if (arrayName != null && !arrayName.equals("")) {
			String[] split = arrayName.split(",");
			if (split.length == 2 && split[0] != null && split[1] != null) {
				if (cardName.equals(split[0]) || cardName.equals(split[1])) {
					Log.i(TAG, "刷卡卡号：" + sIdstr);
					Log.i(TAG, "卡号数组[0]：" + nameArray[0]);
					Log.i(TAG, "卡号数组[1]：" + nameArray[1]);

					if (sIdstr.equals(nameArray[0])
							|| sIdstr.equals(nameArray[1])) {
						Log.i(TAG, "matchToArray...111111");
						tts.speak("你已签到成功，请勿重复签到！！！", TextToSpeech.QUEUE_FLUSH,
								null);
						Toast.makeText(CallActivity.this, "您已签到成功，请勿重复签到！！！",
								Toast.LENGTH_LONG).show();
					} else if (!sIdstr.equals(nameArray[0])
							&& !sIdstr.equals(nameArray[1])) {
						Log.i(TAG, "matchToArray...333333,nameArray[0]="
								+ nameArray[0] + ",nameArray[1]="
								+ nameArray[1]);
						tts.speak(cardName + "签到成功", TextToSpeech.QUEUE_FLUSH,
								null); // 刷卡后读出人名

						if (nameArray[0] != null && !nameArray[0].equals("")) {
							nameArray[1] = sIdstr;

							Log.i(TAG, "End。。。信息：" + getFinalMsg());

							sendTimeAndCard(getFinalMsg());
							Toast.makeText(CallActivity.this,
									"值班人都已签到成功,5秒后退出!", Toast.LENGTH_LONG)
									.show();

							new Thread(new Runnable() {
								@Override
								public void run() {
									try {
										Thread.sleep(5000);
										
										String dutyRemind=PreferenceUtil.getSharePreference(CallActivity.this, Constant.DUTY_REMIND, "0");
										if("1".equals(dutyRemind)){
											AlarmUtil.startBroadCastAlarm(CallActivity.this);
										}
										isAdvanceFinish = true;
										gotoIndex();
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}).start();

						} else {
							nameArray[0] = sIdstr;
						}

						Toast.makeText(CallActivity.this, "签到成功！！！",
								Toast.LENGTH_LONG).show();

					} else {
						Log.i(TAG, "matchToArray...55555");
						tts.speak("你已签到成功，请勿重复签到！！！", TextToSpeech.QUEUE_FLUSH,
								null); // 刷卡后读出人名
						Toast.makeText(CallActivity.this, "你已签到成功，请勿重复签到！！！",
								Toast.LENGTH_LONG).show();
					}

					Log.i(TAG, "卡号数组[0]...end：" + nameArray[0]);
					Log.i(TAG, "卡号数组[1]...end：" + nameArray[1]);

				} else {
					tts.speak("非当前值班人！！！", TextToSpeech.QUEUE_FLUSH, null); // 刷卡后读出人名
					Toast.makeText(CallActivity.this, "非当前值班人！！！",
							Toast.LENGTH_SHORT).show();
				}
			}
		} else {
			Toast.makeText(CallActivity.this, "服务器数据错误！", Toast.LENGTH_LONG)
					.show();
		}

	}

	public String getFinalMsg() {
		Log.v(TAG, "getFinalMsg");

		for (String str : timeList) {
			Log.v(TAG, "getFinalMsg.时间段：" + str);
		}

		String msg = "";
		for (String str : nameArray) {
			if (str != null && !str.equals("")) {
				msg = msg + str + ",";
			}
		}
		Log.i("TEST", "curDuration=" + curDuration);
		msg = timeList.get(curDuration - 1) + "|" + msg;
		Log.i("TEST", "当前时间：" + msg + ",Length=" + timeList.size());
		if (msg.endsWith(",")) {
			msg = msg.substring(0, msg.length() - 1);
		}
		Log.i("TEST", "getFinalMsg=" + msg);
		return msg;
	}

	@Override
	public void onInit(int status) { // 语音引擎初始化
		if (status == TextToSpeech.SUCCESS) {
			int result = tts.setLanguage(Locale.CHINA);
			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				DisplayToast("检测到语音不可用!");
			}
		}
	}

	/*
	 * 自定义Toast <p>解决Toast长时间轮流显示问题</p>
	 */
	public void DisplayToast(String str) {
		if (myToast != null) {
			myToast.setText(str);
		} else {
			myToast = Toast
					.makeText(CallActivity.this, str, Toast.LENGTH_SHORT);
		}
		myToast.show();
	}

	@Override
	public void serial_KeyDo(int keyCode) {

		super.serial_KeyDo(keyCode);
	}

	@Override
	public void serial_CardDo(String cardNum) {

		super.serial_CardDo(cardNum);
		sIdstr = cardNum;
		// Log.i("END","卡号："+cardNum);
		doNetWorkSendMsg(cardNum,1); // 发送卡号给服务器
	}

	public void gotoIndex() {
		this.finish();
		Intent intent = new Intent();
		intent.setClass(this, IndexActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);

	}

}
