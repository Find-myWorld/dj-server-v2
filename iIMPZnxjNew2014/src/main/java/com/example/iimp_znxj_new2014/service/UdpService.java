package com.example.iimp_znxj_new2014.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidParameterException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.iimp_znxj_new2014.DianJiaoApplication;
import com.example.iimp_znxj_new2014.activity.CheckNoteActivity;
import com.example.iimp_znxj_new2014.activity.EmergyNoteActivity;
import com.example.iimp_znxj_new2014.activity.FullScrollSubtitleActivity;
import com.example.iimp_znxj_new2014.activity.ImagePagerActivity;
import com.example.iimp_znxj_new2014.activity.IndexActivity;
import com.example.iimp_znxj_new2014.activity.KeDaVCRActivity;
import com.example.iimp_znxj_new2014.activity.PlayLocalVideoActivity;
import com.example.iimp_znxj_new2014.activity.RollCallActivity;
import com.example.iimp_znxj_new2014.activity.TVActivity;
import com.example.iimp_znxj_new2014.activity.VideoShowActivity;
import com.example.iimp_znxj_new2014.activity.VideoViewPlayingActivity;
import com.example.iimp_znxj_new2014.entity.AdjustDate;
import com.example.iimp_znxj_new2014.entity.CheckNote;
import com.example.iimp_znxj_new2014.entity.ConfigureInfo;
import com.example.iimp_znxj_new2014.entity.ContinuePlay;
import com.example.iimp_znxj_new2014.entity.DailyLife;
import com.example.iimp_znxj_new2014.entity.DailyLifeList;
import com.example.iimp_znxj_new2014.entity.Delete;
import com.example.iimp_znxj_new2014.entity.DeleteFile;
import com.example.iimp_znxj_new2014.entity.DownLoadPlan;
import com.example.iimp_znxj_new2014.entity.DownLoadPlanList;
import com.example.iimp_znxj_new2014.entity.DownloadFile;
import com.example.iimp_znxj_new2014.entity.DutyTime;
import com.example.iimp_znxj_new2014.entity.EmergNote;
import com.example.iimp_znxj_new2014.entity.FullSubTitlePlan;
import com.example.iimp_znxj_new2014.entity.GetCurrentState;
import com.example.iimp_znxj_new2014.entity.GetFolderList;
import com.example.iimp_znxj_new2014.entity.GetProgressBar;
import com.example.iimp_znxj_new2014.entity.GetRealTimeContent;
import com.example.iimp_znxj_new2014.entity.ImageSlide;
import com.example.iimp_znxj_new2014.entity.JumpPercentage;
import com.example.iimp_znxj_new2014.entity.ModifyClientIp;
import com.example.iimp_znxj_new2014.entity.OnDuty;
import com.example.iimp_znxj_new2014.entity.OnLine;
import com.example.iimp_znxj_new2014.entity.OnOff;
import com.example.iimp_znxj_new2014.entity.OnOffPlanList;
import com.example.iimp_znxj_new2014.entity.Pause;
import com.example.iimp_znxj_new2014.entity.PlayLocal;
import com.example.iimp_znxj_new2014.entity.PlayPlan;
import com.example.iimp_znxj_new2014.entity.PlayPlanList;
import com.example.iimp_znxj_new2014.entity.PlaySubTitle;
import com.example.iimp_znxj_new2014.entity.PlayVCR;
import com.example.iimp_znxj_new2014.entity.PlayVideo;
import com.example.iimp_znxj_new2014.entity.Reboot;
import com.example.iimp_znxj_new2014.entity.RollCall;
import com.example.iimp_znxj_new2014.entity.RollSubTitle;
import com.example.iimp_znxj_new2014.entity.SaveScreen;
import com.example.iimp_znxj_new2014.entity.SearchDevices;
import com.example.iimp_znxj_new2014.entity.ServerIpPort;
import com.example.iimp_znxj_new2014.entity.SetEmpty;
import com.example.iimp_znxj_new2014.entity.SetVcrType;
import com.example.iimp_znxj_new2014.entity.StopPlay;
import com.example.iimp_znxj_new2014.entity.StopPlaySubTitle;
import com.example.iimp_znxj_new2014.entity.TVShow;
import com.example.iimp_znxj_new2014.entity.TimingLiving;
import com.example.iimp_znxj_new2014.entity.TimingLivingList;
import com.example.iimp_znxj_new2014.entity.TimingPlan;
import com.example.iimp_znxj_new2014.entity.TimingPlanList;
import com.example.iimp_znxj_new2014.entity.UpdateVersion;
import com.example.iimp_znxj_new2014.entity.Volume;
import com.example.iimp_znxj_new2014.event.JsonEvent;
import com.example.iimp_znxj_new2014.processor.ParseProcessor;
import com.example.iimp_znxj_new2014.processor.ParseProcessor.ParseListener;
import com.example.iimp_znxj_new2014.selfconsume.GoodsDao;
import com.example.iimp_znxj_new2014.util.AlarmManagerUtil;
import com.example.iimp_znxj_new2014.util.AlarmUtil;
import com.example.iimp_znxj_new2014.util.Constant;
import com.example.iimp_znxj_new2014.util.DateUtils;
import com.example.iimp_znxj_new2014.util.DownPlanDao;
import com.example.iimp_znxj_new2014.util.FileUtils;
import com.example.iimp_znxj_new2014.util.JingMoOrder;
import com.example.iimp_znxj_new2014.util.ManagerUtil;
import com.example.iimp_znxj_new2014.util.NetUtil;
import com.example.iimp_znxj_new2014.util.PreferenceUtil;
import com.wits.serialport.SerialPort;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class UdpService extends Service implements ParseListener {
	private int port = 7000;
	private DatagramSocket readSocket;
	private DatagramSocket sendSocket;
	private ClientReadThread mThread;
	private final String TAG = "UdpService";

	private static final int MESSAGE_CHECK_ALIVE = 1;
	private static final int MESSAGE_CHECK_DAILY_LIFE_VALUE = 2;
	private static final int MESSAGE_CHECK_TIMING_LIVING_VALUE = 3;
	private static final int MESSAGE_CHECK_PLAY_PLAN_VALUE = 4;
	private static final int MESSAGE_CHECK_OFF_VALUE = 5;
	private static final int MESSAGE_INTERUPT_READTHREAD = 6;
	private static final int MESSAGE_CHECK_TIMING_PLAN_VALUE = 7;
	private static final int MESSAGE_CHECK_FULLSUBTITLE_PLAN = 8;

	private static String serverIp = "";
	private static String serverPort = "";
	private static String serverStr = "";

	private static List<DailyLife> dailyLifeList = new ArrayList<DailyLife>();
	private static List<PlayPlan> playPlanList = new ArrayList<PlayPlan>();
	private static List<DownLoadPlan> downLoadList = new ArrayList<DownLoadPlan>();
	private static List<TimingPlan> timingPlanList = new ArrayList<TimingPlan>(); // 定时计划
	private static List<TimingLiving> timingLivingList = new ArrayList<TimingLiving>();
	private static OnOffPlanList onOffPlanList = new OnOffPlanList();
	private static FullSubTitlePlan fullSubTitlePlanList = new FullSubTitlePlan();

	private static final int IS_APP_ALIVE_TIME = 30 * 1000;
	private static final int ONE_MINUTE = 60 * 1000;

	private ParseProcessor parseProcessor;
	private String offTimeServerStr = "";
	private String onTimeServerStr = "";
	private MediaPlayer mMediaPlayer = new MediaPlayer();
	private static final int DAY_PER_SECOND = 24 * 60 * 60;
	public static SerialPort mSerialPort;
	public static OutputStream mOutputStream;
	public static InputStream mInputStream;
	public static ReadThread mReadThread;
	private boolean mFlag;
	private Timer timer;

	private DownPlanDao dao;
	private GoodsDao gDao;
	private static final String DOWN_TABLE = "alarm_t";
	private static final String PLAY_PLAN_TABLE = "playplan_t";

	private boolean flag = true;
	private boolean isSend = false; // 是否发送关闭广播
	private int VOLUME_VALUE = 10;

	public int isNetOn = 0;
	private Timer checkOnlineTimer;
	private StatusReceiver mStatusReceiver;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		dao = new DownPlanDao(getApplicationContext());// 使用到增加数据的功能
		gDao = new GoodsDao(getApplicationContext());

		Log.v(TAG, "onCreate service");
		if (null == mSerialPort) {
			try {
				mSerialPort = getSerialPort();
				mOutputStream = mSerialPort.getOutputStream();
				mInputStream = mSerialPort.getInputStream();
				mFlag = true;
				Log.i(TAG, "-----------------mReadThread.start");
			} catch (SecurityException e) {
			} catch (IOException e) {
			} catch (InvalidParameterException e) {
			}
		}

		/*
		 * 延时之后每隔固定时间发一次校时，不在线则重启
		 */

		/*
		 * new Timer().schedule(new TimerTask() {
		 * 
		 * @Override public void run() { Log.v("adjustTime", "定时Timer计划");
		 * 
		 * String adjustTimeServerIp =
		 * PreferenceUtil.getSharePreference(UdpService.this,
		 * Constant.ADJUST_SERVER_URL); String adjustTimeFlag =
		 * PreferenceUtil.getSharePreference(UdpService.this, "haveAdjusted");
		 * if ("true".equals(adjustTimeFlag)) {
		 * JingMoOrder.adjustServerTime(UdpService.this, adjustTimeServerIp); }
		 * } }, 5 * 60 * 1000, 1000*60*15);
		 */

		/* 延时之后每隔固定判断是否在线，不在线则重启 */

		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				boolean isNetworkOn = NetUtil.isNetworkConnected();
				Log.e("netWork", "----onLine=" + isNetworkOn + "----isNetOn="
						+ isNetOn);
				if (!isNetworkOn) {
					isNetOn++;
					if (isNetOn > 10) {
						try {
							rebootHandler.sendEmptyMessage(0);
						} catch (Exception e) {
							Log.e(TAG, e.getMessage());
						}
					}
				} else {
					isNetOn = 0;
				}
			}
		}, 1000 * 60 * 30, 1000 * 60);

		/*
		 * 判断是否在线
		 */
		checkOnlineTimer = new Timer();
		checkOnlineTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				Log.i("test", "发送广播");
				if (NetUtil.isNetworkConnected()) {
					Intent intent = new Intent();
					intent.putExtra("isOnline", "true");
					intent.setAction("com.example.iimp_znxj_new2014.checkOnline");
					sendBroadcast(intent);
				} else {
					Intent intent = new Intent();
					intent.putExtra("isOnline", "false");
					intent.setAction("com.example.iimp_znxj_new2014.checkOnline");
					sendBroadcast(intent);
				}
			}
		}, 0, 1000 * 10);

		mStatusReceiver = new StatusReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.VCR_OR_VIDEO_CONTINUE);
		filter.addAction("reboot");// 重启广播
		registerReceiver(mStatusReceiver, filter);

		EventBus.getDefault().register(this);
	}
	/*接收来自操作的数据，解析*/
	@Subscribe
	public void onEventMainThread(JsonEvent event){
		System.err.println("onEventMainThread:" + event.getJsonstr());
		parseProcessor = ParseProcessor
				.getInstance(UdpService.this);
		parseProcessor.registerParseListener(UdpService.this);
		parseProcessor.parseData(event.getJsonstr()); // 解析接收到的json格式数据

	}

	public class StatusReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String actionStr = intent.getAction();
			// 接受直播和流媒体继续广播
			if (Constant.VCR_OR_VIDEO_CONTINUE.equals(actionStr)) {
				boolean hasPlan = false;
				try {
					hasPlan = getCurrentTime() >= parseStrTimeToms(Constant.VCR_START_TIME)
							&& getCurrentTime() <= parseStrTimeToms(Constant.VCR_END_TIME);
				} catch (Exception e) {
					// if(helper.hasSD()){
					// helper.writeSDFile("直播本地计划时间有误！", "LogCode.txt");
					// }

				}
				if (hasPlan) {
					realTime = Constant.VCR_END_TIME;
					SharedPreferences sp = DianJiaoApplication.getInstance()
							.getSharedPreferences("TvShow",
									Context.MODE_PRIVATE);
					String livingIp = sp.getString("ip", "");
					String livingPort = sp.getString("port", "");
					String livingUser = sp.getString("user", "");
					String livingPass = sp.getString("passWord", "");
					String livingChannel = sp.getString("channel", "");

					Intent alarmVcr = null;
					String VCRType = PreferenceUtil.getSharePreference(
							UdpService.this,
							Constant.SHAREDPREFERENCE_VCR_TYPE, 1 + "");
					if ("1".equals(VCRType)) {
						alarmVcr = new Intent(UdpService.this, TVActivity.class);
					} else if ("2".equals(VCRType)) {
						alarmVcr = new Intent(UdpService.this,
								KeDaVCRActivity.class);
					}

					alarmVcr.putExtra("ip", livingIp);
					alarmVcr.putExtra("port", livingPort);
					alarmVcr.putExtra("user", livingUser);
					alarmVcr.putExtra("password", livingPass);
					alarmVcr.putExtra("channel", livingChannel);
					alarmVcr.putExtra("volume", PreferenceUtil
							.getSharePreference(getApplicationContext(),
									Constant.VCR_VOLUME, "13"));
					alarmVcr.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(alarmVcr);

				} else {
					try {
						hasPlan = getCurrentTime() >= parseStrTimeToms(Constant.VIDEO_START_TIME)
								&& getCurrentTime() <= parseStrTimeToms(Constant.VIDEO_END_TIME);
					} catch (Exception e) {
						// if(helper.hasSD()){
						// helper.writeSDFile("流媒体本地计划时间有误！", "LogCode.txt");
						// }

					}
					if (hasPlan) {
						realTime = Constant.VIDEO_END_TIME;
						String jsonStr = PreferenceUtil.getSharePreference(
								UdpService.this,
								Constant.CURRENT_VIDEO_PERCENT, "");
						if (!TextUtils.isEmpty(jsonStr)) {
							Log.e("jsonStr", jsonStr);
							JSONObject jsonObject;
							try {
								jsonObject = new JSONObject(jsonStr);

								JSONObject dataObject = jsonObject
										.getJSONObject("data"); // 每次只需解析一个数据
								if (dataObject.has("loop")) {
									palyVideoLoop = dataObject
											.getString("loop");
									isLoopNull = false;
								} else {
									isLoopNull = true;
								}

								String playVideoServerIp = (dataObject
										.getString("serverIp"));
								String playVideoserverPort = dataObject
										.getString("port");
								String playVideoFileName = encode(dataObject
										.getString("fileName")); // UTF-8编码

								int curPercent = 0;
								try {
									curPercent = Integer.parseInt(dataObject
											.getString("curPercent"));
								} catch (Exception e) {
									curPercent = 0;
								}
								String url = "http://" + playVideoServerIp
										+ ":" + playVideoserverPort + "/"
										+ playVideoFileName;
								PreferenceUtil.putSharePreference(
										UdpService.this,
										Constant.SERVER_IP_PORT,
										playVideoServerIp + ":"
												+ playVideoserverPort); // 写入文件
								PreferenceUtil.putSharePreference(
										UdpService.this, Constant.VIDEO_IP,
										playVideoServerIp); // 写入文件
								PreferenceUtil.putSharePreference(
										UdpService.this, Constant.VIDEO_PORT,
										playVideoserverPort); // 写入文件

								if (!ManagerUtil.isThisActivityTop(
										UdpService.this,
										Constant.INDEX_ACTIVITY_NAME)) {// 如果当前Activity不在主界面，则退回到主界面
									DianJiaoApplication.getInstance().exit();
									Log.i("ALARM", "先回到主界面1");
									//
									Intent intent1 = new Intent(
											UdpService.this,
											IndexActivity.class);
									intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									startActivity(intent1);
								}

								Log.d("PlayVideo", "new play:" + url);
								final Intent itto = new Intent(UdpService.this,
										VideoShowActivity.class);
								Bundle bundle = new Bundle();
								if (isLoopNull == false) {
									bundle.putString(Constant.PLAY_VIDEO_LOOP,
											palyVideoLoop);
								}
								bundle.putInt("curPercent", curPercent);
								bundle.putString("port", playVideoserverPort);
								bundle.putString("host", playVideoServerIp);
								bundle.putString("volume", PreferenceUtil
										.getSharePreference(
												getApplicationContext(),
												Constant.VIDEO_VOLUME, "8"));
								itto.putExtras(bundle);
								itto.setData(Uri.parse(url));
								// itto.putExtra("mIsHwDecode", true);
								itto.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(itto);
							} catch (JSONException e) {
								e.printStackTrace();
							}

							// if(helper.hasSD()){
							// helper.writeSDFile("ContinuePlay:true==>本地缓存为："+jsonStr,
							// "LogCode.txt");
							// }
							new Thread(new Client("ContinuePlay:true==>本地缓存为："
									+ jsonStr));
						} else {
							// if(helper.hasSD()){
							// helper.writeSDFile("ContinuePlay:true==>本地没有缓存记录！",
							// "LogCode.txt");
							// }
							new Thread(new Client(
									"ContinuePlay:false==>本地没有缓存记录！"));
						}
					}

				}

			}

			if ("reboot".equals(actionStr)) {
				JingMoOrder.reboot();// 重启
			}

		}

	}

	private Handler rebootHandler = new Handler() {
		public void handleMessage(Message msg) {
			JingMoOrder.reboot();
		};
	};

	/*
	 * 判断app是否处于活动状态，不是就回首页(防止退出程序)
	 */
	@SuppressLint("HandlerLeak")
	private Handler mAppAliveHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MESSAGE_CHECK_ALIVE:
				if (!ManagerUtil
						.isApplicationBroughtToBackground(getApplicationContext())) {
					// start application
					DianJiaoApplication.getInstance().exit();
					Intent intent = new Intent(UdpService.this,
							IndexActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				}
				sendEmptyMessageDelayed(MESSAGE_CHECK_ALIVE, IS_APP_ALIVE_TIME);
				break;

			}
		};
	};

	/*
	 * 刷新定时计划（每日计划/一日计划）
	 */
	private Handler mCheckDailyLifeHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MESSAGE_CHECK_DAILY_LIFE_VALUE:
				try {
					checkDailyLife(); // 每分钟执行一次
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					sendEmptyMessageDelayed(MESSAGE_CHECK_DAILY_LIFE_VALUE,
							ONE_MINUTE);
				}
				break;
			case MESSAGE_CHECK_TIMING_PLAN_VALUE:
				try {
					checkTimmingPlan();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					Log.i("z2", "60循环");
					sendEmptyMessageDelayed(MESSAGE_CHECK_TIMING_PLAN_VALUE,
							ONE_MINUTE);
				}
				break;
			case MESSAGE_CHECK_TIMING_LIVING_VALUE:
				try {
					Log.i("TTTT", "MESSAGE_CHECK_TIMING_LIVING_VALUE.....3");
					checkTimmingLiving();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					sendEmptyMessageDelayed(MESSAGE_CHECK_TIMING_LIVING_VALUE,
							ONE_MINUTE);
				}
				break;
			case MESSAGE_CHECK_PLAY_PLAN_VALUE:
				try {
					palayStopPlayPlan();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					sendEmptyMessageDelayed(MESSAGE_CHECK_PLAY_PLAN_VALUE,
							ONE_MINUTE);
				}
				break;
			case MESSAGE_CHECK_OFF_VALUE:
				try {
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					sendEmptyMessageDelayed(MESSAGE_CHECK_PLAY_PLAN_VALUE,
							ONE_MINUTE);
				}
				break;
			case MESSAGE_INTERUPT_READTHREAD:
				if (mReadThread != null)
					mReadThread.interrupt();
				break;
			case MESSAGE_CHECK_FULLSUBTITLE_PLAN:
				try {
					checkFullSubTitlePlan();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					sendEmptyMessageDelayed(MESSAGE_CHECK_FULLSUBTITLE_PLAN,
							ONE_MINUTE);
				}
				break;
			}
		};
	};

	public void writeOnTimeToMC(int flags, long times) {
		try {
			byte[] mBuffer = longToByteArray(flags, times);
			Log.e(TAG, "writeOnTimeToMC--------time=" + times);
			int i;
			for (i = 0; i < mBuffer.length; i++)
				Log.i(TAG, "BUFFER-----HASHCODE=" + mBuffer[i]);
			mReadThread = new ReadThread();
			mReadThread.start();

			if (mOutputStream != null) {
				mOutputStream.write(mBuffer);
				Log.e(TAG, "send data 9 byte to serialport.ok");
			} else {
				Log.e(TAG, "mOutputStream:--------null");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static byte[] longToByteArray(int flags, long times) {
		byte[] result = new byte[9];
		result[0] = (byte) 0x00;
		result[1] = (byte) 0xaa;
		result[2] = (byte) 0xff;
		result[3] = (byte) 0x55;

		result[4] = (byte) (flags);

		result[5] = (byte) ((times >> 16) & 0xFF);
		result[6] = (byte) ((times >> 8) & 0xFF);
		result[7] = (byte) (times & 0xFF);

		result[8] = (byte) 0x55;
		return result;
	}

	public SerialPort getSerialPort() throws SecurityException, IOException,
			InvalidParameterException {
		mSerialPort = new SerialPort(new File("/dev/ttyS5"), 9600, 0);
		return mSerialPort;
	}

	public void closeSerialPort() {
		Log.i(TAG, "closeSerialPort");
		if (mSerialPort != null) {
			mSerialPort.close();
			mSerialPort = null;
		}
	}

	class WriteTask extends TimerTask {
		public void run() {
			writeOnTimeToMC(1, mOffsetSecondTimes);
		}
	}

	public class ReadThread extends Thread {
		@Override
		public void run() {
			super.run();
			while (mFlag) {
				int size;
				try {
					byte[] buffer = new byte[1];
					if (mInputStream == null) {
						break;
					}
					size = mInputStream.read(buffer);

					if (size > 0) {
						for (int i = 0; i < size; i++) {
							if (buffer[i] != 0x55) {
								if (timer == null) {
									timer = new Timer();
									timer.schedule(new WriteTask(), 2000, 1000);
								}
								Log.i("8.3",
										"Data error app will resent data==============="
												+ +buffer[i]);
							} else if (buffer[i] == 0x55) {
								Log.i(TAG, " Set boot time ok!" + buffer[i]);
								Log.i("8.3", " Set boot time ok!" + buffer[i]);

								// 发送关机广播
								Intent intentSetOff = new Intent();
								intentSetOff
										.setAction("wits.com.simahuan.shutdown");
								sendBroadcast(intentSetOff);

								size = 0;
								mFlag = false;
								if (timer != null) {
									timer.cancel();
									timer = null;
									Log.i(TAG, "timer cancel!");
								}
								Message msg = new Message();
								msg.what = MESSAGE_INTERUPT_READTHREAD;
								mCheckDailyLifeHandler.sendMessage(msg);
							}
						}
						// flush_buffer
						Arrays.fill(buffer, (byte) 0);
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}

	/*
	 * 每分钟验证一日生活制度
	 */
	private void checkDailyLife() {
		if (dailyLifeList != null && !dailyLifeList.isEmpty()) {
			// Log.d("checkDailyLife", "palayStopDailyLife()0");
			palayStopDailyLife(); // 每分钟执行一次
		} else {
			// 读dailylife文件内容
			String jsonStr = FileUtils.getStringCache(
					Constant.DAILY_LIFE_FOLDER, Constant.DAILY_LIFE_FILE_NAME,
					this);

			if (!TextUtils.isEmpty(jsonStr)) { // 读取内容相当于又接收了一遍json指令，下面作解析操作
				parseProcessor = ParseProcessor.getInstance(UdpService.this);
				parseProcessor.registerParseListener(UdpService.this);
				parseProcessor.parseData(jsonStr);
			}
		}
	}

	/*
	 * 验证流媒体定时计划
	 */
	private void checkTimmingPlan() {
		if (timingPlanList != null && !timingPlanList.isEmpty()
				&& !Constant.isLiveTime) {
			startTimingPlan(); // 每分钟执行一次
		} else {
			// 读TimingPlan文件内容
			String jsonStr = FileUtils.getStringCache(
					Constant.TIMING_PLAN_FOLDER_NAME,
					Constant.TIMING_PLAN_FILE_NAME, this);

			if (!TextUtils.isEmpty(jsonStr)) { // 读取内容相当于又接收了一遍json指令，下面作解析操作
				parseProcessor = ParseProcessor.getInstance(UdpService.this);
				parseProcessor.registerParseListener(UdpService.this);
				parseProcessor.parseData(jsonStr);
			}
		}
	}

	/*
	 * 开始定时计划
	 */
	private void startShutPlan() {
		String jsonStr = FileUtils.getStringCache(
				Constant.SHUT_PLAN_FOLDER_NAME, Constant.SHUT_PLAN_FILE_NAME,
				this);
		if (!TextUtils.isEmpty(jsonStr)) { // 读取内容相当于又接收了一遍json指令，下面作解析操作
			parseProcessor = ParseProcessor.getInstance(UdpService.this);
			parseProcessor.registerParseListener(UdpService.this);
			parseProcessor.parseData(jsonStr);
		}
	}

	/*
	 * 验证直播计划
	 */
	private void checkTimmingLiving() {
		Log.i("TTTT", "checkTimmingLiving_......4");
		if (timingLivingList != null && !timingLivingList.isEmpty()) {
			startTimingLiving(); // 每分钟执行一次
		} else {
			String jsonStr = FileUtils.getStringCache(
					// 读TimingPlan文件内容
					Constant.TIMING_LIVING_FOLDER,
					Constant.TIMING_LIVING_FILE_NAME, this);
			if (!TextUtils.isEmpty(jsonStr)) { // 读取内容相当于又接收了一遍json指令，下面作解析操作
				parseProcessor = ParseProcessor.getInstance(UdpService.this);
				parseProcessor.registerParseListener(UdpService.this);
				parseProcessor.parseData(jsonStr);
			}
		}
	}

	/*
	 * 验证全屏滚动计划
	 */
	private void checkFullSubTitlePlan() {
		Log.i("TTTT", "checkFullSubTitlePlan......");
		if (fullSubTitlePlanList != null && !(fullSubTitlePlanList.size() == 0)) {
			// 每分钟执行一次
			Log.i("SubTitlePlan", "判断是否到达滚动时间");
			startSubTitlePlan();
		} else {
			String jsonStr = FileUtils.getStringCache(
					// 读FullSubTitlePlan文件内容
					Constant.FULLSUBTITLE_PLAN_FOLDER_NAME,
					Constant.FULLSUBTITLE_PLAN_FILE_NAME, this);
			if (!TextUtils.isEmpty(jsonStr)) { // 读取内容相当于又接收了一遍json指令，下面作解析操作
				if (parseProcessor == null) {
					parseProcessor = ParseProcessor
							.getInstance(UdpService.this);
					parseProcessor.registerParseListener(UdpService.this);
				}
				parseProcessor.parseData(jsonStr);
			}
		}
	}

	String minute;

	private int parseStrTimeToms(String dateStr) {

		if (dateStr == null) {
			return -1;
		}

		// Log.v("7.13.1", dateStr);

		if (dateStr.length() > 10) {
			return -1;
		}

		String[] strarray = dateStr.split(":");
		// Log.v("7.13.1", strarray.length + "");
		String hour = strarray[0];
		// 没测试不知道有没有问题
		if (strarray.length > 1) {
			minute = strarray[1];
		} else {
			minute = "0";
		}
		int hourInt = 0;
		int minuteInt = 0;
		if (!TextUtils.isEmpty(hour) && !TextUtils.isEmpty(minute)) {
			hourInt = Integer.parseInt(hour);
			minuteInt = Integer.parseInt(minute);
		}
		return hourInt * 60 + minuteInt;
	}

	/*
	 * 验证滚动播放计划
	 */
	String subTitlePlanNowTime;
	String subTitlePlanStartTime;
	String subTitlePlanEndTime;

	private void startSubTitlePlan() {
		Log.e("1111111", "开始定时");
		for (int i = 0; i < fullSubTitlePlanList.size(); i++) {
			RollSubTitle rollSubTitle = fullSubTitlePlanList.get(i);

			// String planWeek = rollSubTitle.getWeek();
			if ("0".startsWith(rollSubTitle.getWeek())) {

			} else {
				String planWeek = getWeek(rollSubTitle.getStartTime());// 获取当前日期

				Calendar calendar = Calendar.getInstance();
				// 得到本日星期
				String nowWeek = String.valueOf(calendar
						.get(Calendar.DAY_OF_WEEK));

				if (!(nowWeek.equals(planWeek))) {
					continue;
				}
			}

			// 得到当前开始时间与结束时间

			subTitlePlanStartTime = rollSubTitle.getStartTime();
			subTitlePlanEndTime = rollSubTitle.getEndTime();
			// if(helper.hasSD()){
			// helper.writeSDFile("滚动计划==>StartTime:"+subTitlePlanStartTime,
			// "LogCode.txt");
			// helper.writeSDFile("滚动计划==>EndTime  :"+subTitlePlanEndTime,
			// "LogCode.txt");
			// helper.writeSDFile("滚动计划==>FileName :"+rollSubTitle.getFileName(),
			// "LogCode.txt");
			//
			// }
			if (subTitlePlanStartTime.length() > 10) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
				Date date = new Date();
				subTitlePlanNowTime = sdf.format(date);
				subTitlePlanEndTime = subTitlePlanEndTime.split(" ")[1];
			} else {
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
				Date date = new Date();
				subTitlePlanNowTime = sdf.format(date);
				Log.i("SubTitlePlan", "未超过10个字符" + subTitlePlanStartTime);
			}
			// if(helper.hasSD()){
			// helper.writeSDFile("subTitlePlanNowTime:"+subTitlePlanNowTime,
			// "LogCode.txt");
			// }

			if (subTitlePlanNowTime.equals(subTitlePlanStartTime)) {
				// Log.i("SubTitlePlan", "滚动开始时间" + subTitlePlanStartTime);
				realTime = subTitlePlanEndTime;
				if (!ManagerUtil.isThisActivityTop(UdpService.this,
						Constant.INDEX_ACTIVITY_NAME)) {// 如果当前Activity不在主界面，则退回到主界面
					DianJiaoApplication.getInstance().exit();
					Log.i("ALARM", "先回到主界面1");
					//
					Intent intent1 = new Intent(UdpService.this,
							IndexActivity.class);
					intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent1);
				}
				Log.i("SubTitlePlan", "滚动结束时间" + realTime);
				// 先发个结束广播结束当前播放的任务
				Intent intent = new Intent();
				intent.setAction("com.example.iimp_znxj_new2014.stopPlay");
				sendBroadcast(intent);

				String serverIp = rollSubTitle.getServerIp();
				String port = rollSubTitle.getPort();
				String fileName = rollSubTitle.getFileName();
				String rollSpeed = rollSubTitle.getRollSpeed();
				String showTime = rollSubTitle.getShowTime();

				String url = "http://" + serverIp + ":" + port + "/" + fileName;
				if (rollSpeed.equals("1")) {
					Constant.SCROLL_SPEED = 200;
				} else if (rollSpeed.equals("2")) {
					Constant.SCROLL_SPEED = 25;
				} else {
					Constant.SCROLL_SPEED = 5;
				}
				final Intent itto = new Intent(UdpService.this,
						FullScrollSubtitleActivity.class); // 滚动字幕
				itto.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				Bundle bundle = new Bundle();
				bundle.putString(Constant.SCROLL_URL, url);
				bundle.putString(Constant.SCROLL_SHWOTIME, showTime);
				itto.putExtras(bundle);
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							Thread.sleep(500);
						} catch (Exception e) {
						}
						startActivity(itto);
					}
				}).start();
				// if(helper.hasSD()){
				// helper.writeSDFile("开始进入滚动页面==》url："+url, "LogCode.txt");
				// }
				// 推送的定时滚动计划的开始时间
				// JingMoOrder.postErrorLog(PreferenceUtil.getSharePreference(UdpService.this,
				// Constant.SERVER_IP_PORT),
				// PreferenceUtil.getSharePreference(UdpService.this,
				// Constant.TERMINAL_IP),
				// "滚动开始时间----" + nowTime + "-----播放文件:" + fileName);

				JingMoOrder.postErrorLog3(UdpService.this, "滚动开始时间----"
						+ nowTime + "-----播放文件:" + fileName,
						Constant.NORMAL_MSG);

			} else if (subTitlePlanNowTime.equals(realTime)) {
				Log.i("SubTitlePlan", "滚动结束" + realTime);
				// 推送的定时滚动计划的开始时间
				// JingMoOrder.postErrorLog(PreferenceUtil.getSharePreference(UdpService.this,
				// Constant.SERVER_IP_PORT),
				// PreferenceUtil.getSharePreference(UdpService.this,
				// Constant.TERMINAL_IP),
				// "滚动结束时间----" + subTitlePlanEndTime);

				JingMoOrder.postErrorLog3(UdpService.this, "滚动结束时间----"
						+ subTitlePlanEndTime, Constant.NORMAL_MSG);

				Intent intent = new Intent();
				intent.setAction("com.example.iimp_znxj_new2014.stopPlay");
				sendBroadcast(intent);
			}
		}
	}

	/*
	 * 每天都有（每日计划）
	 */
	private void palayStopDailyLife() {
		for (DailyLife dailyLife : dailyLifeList) {
			Log.i("checkDailyLife", "palayStopDailyLife");
			String dailyLifeServerIp = dailyLife.getServerIp();
			String dailyLifeFileName = dailyLife.getFileName();
			String dailyLifeBeginTime = dailyLife.getBeginTime();
			String dailyLifeEndTime = dailyLife.getEndTime();
			String dailyLifePort = dailyLife.getPort();
			String path = "http://" + dailyLifeServerIp + ":" + dailyLifePort
					+ "/" + dailyLifeFileName;
			String[] type = dailyLifeFileName.trim().split("\\.");// 用“.”作为分隔的话，必须是如下写法：String.split("\\."),这样才能正确的分隔开

			if (getCurrentTime() == parseStrTimeToms(dailyLifeBeginTime)) { // 开始时间则开始播放
				try {
					Log.i("Msg", "Currect_Songs:" + dailyLifeFileName);
					Log.i("Msg", "length=" + type.length + "|" + type[0] + "|"
							+ type[1]);
					if (type[1].equals("mp3") || type[1].equals("wav")
							|| type[1].equals("wma")) {
						if (mMediaPlayer.isPlaying()) {
							mMediaPlayer.stop();
							mMediaPlayer.reset();
						}
						// mMediaPlayer.setDataSource(url); //网络路径
						mMediaPlayer.setDataSource(path); // 本地路径
						mMediaPlayer.prepare();
						mMediaPlayer.start();
						mMediaPlayer.setLooping(true); // 循环播放
						Intent udpIntent = new Intent(this, UdpService.class);
						udpIntent.setAction(Constant.ORDER_RESULT_ACTION);
						udpIntent.putExtra(Constant.ORDER_RESULT_EXTRA,
								Constant.ORDER_ACTION + "play,"
										+ Constant.ORDER_RESULT_OK);
						startService(udpIntent);
					} else {
						// Intent itto = new Intent(this,
						// VideoViewPlayingActivity.class);
						Intent itto = new Intent(this, VideoShowActivity.class);
						itto.setData(Uri.parse(path));
						itto.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(itto);
					}

				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					Intent udpIntent = new Intent(this, UdpService.class);
					udpIntent.setAction(Constant.ORDER_RESULT_ACTION);
					udpIntent.putExtra(Constant.ORDER_RESULT_EXTRA,
							Constant.ORDER_ACTION + "play,"
									+ Constant.ORDER_RESULT_FAILER);
					startService(udpIntent);
				} catch (SecurityException e) {
					e.printStackTrace();
					Intent udpIntent = new Intent(this, UdpService.class);
					udpIntent.setAction(Constant.ORDER_RESULT_ACTION);
					udpIntent.putExtra(Constant.ORDER_RESULT_EXTRA,
							Constant.ORDER_ACTION + "play,"
									+ Constant.ORDER_RESULT_FAILER);
					startService(udpIntent);
				} catch (IllegalStateException e) {
					e.printStackTrace();
					Intent udpIntent = new Intent(this, UdpService.class);
					udpIntent.setAction(Constant.ORDER_RESULT_ACTION);
					udpIntent.putExtra(Constant.ORDER_RESULT_EXTRA,
							Constant.ORDER_ACTION + "play,"
									+ Constant.ORDER_RESULT_FAILER);
					startService(udpIntent);
				} catch (IOException e) {
					e.printStackTrace();
					Intent udpIntent = new Intent(this, UdpService.class);
					udpIntent.setAction(Constant.ORDER_RESULT_ACTION);
					udpIntent.putExtra(Constant.ORDER_RESULT_EXTRA,
							Constant.ORDER_ACTION + "play,"
									+ Constant.ORDER_RESULT_FAILER);
					startService(udpIntent);
				}
			} else if (getCurrentTime() == parseStrTimeToms(dailyLifeEndTime)) { // 结束时间则结束播放
				Log.i("Msg", "End_Songs:" + dailyLifeFileName);
				try {
					if (type[1].equals("mp3") || type[1].equals("wav")
							|| type[1].equals("wma")) {
						Log.i("DailyLife", "DailyLife音频结束");
						if (mMediaPlayer.isPlaying()) {
							mMediaPlayer.stop();
							mMediaPlayer.reset();
							Intent udpIntent = new Intent(this,
									UdpService.class);
							udpIntent.setAction(Constant.ORDER_RESULT_ACTION);
							udpIntent.putExtra(Constant.ORDER_RESULT_EXTRA,
									Constant.ORDER_ACTION + "play,"
											+ Constant.ORDER_RESULT_OK);
							startService(udpIntent);
						}
					} else {
						Log.i("DailyLife", "DailyLife视频结束");
						// String stopPlay = ((StopPlay) data).getStopPlay();
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putString(Constant.STOP_PLAY, "stop");
						intent.setAction(Constant.STOPPLAY_ACTION);
						intent.putExtras(bundle);
						sendBroadcast(intent);
					}

				} catch (Exception e) {
					e.printStackTrace();
					Intent udpIntent = new Intent(this, UdpService.class);
					udpIntent.setAction(Constant.ORDER_RESULT_ACTION);
					udpIntent.putExtra(Constant.ORDER_RESULT_EXTRA,
							Constant.ORDER_ACTION + "play,"
									+ Constant.ORDER_RESULT_FAILER);
					startService(udpIntent);
				}
			}
		}
	}

	/*
	 * 播放计划（一次）
	 */
	private void palayStopPlayPlan() {
		for (PlayPlan playPlan : playPlanList) {
			String playPlanServerIp = playPlan.getServerIp();
			String playPlanBeginTime = playPlan.getPlayTime(); //
			String playPlanFileName = playPlan.getPlayFileName();//
			String playPlanPort = playPlan.getPort();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date begindate = null;

			Log.i("setPlayPlan", "List:" + playPlanBeginTime + "|"
					+ playPlanFileName);
			if (!TextUtils.isEmpty(playPlanBeginTime)) {
				Log.i("setPlayPlan", "开始时间不为空");
				try {
					begindate = sdf.parse(playPlanBeginTime);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				long beginTime = begindate.getTime(); // 执行到此处中止
														// (begindate.getTime有错)
				Date date = new Date();
				String time = sdf.format(date);
				Date nowdate = null;
				try {
					nowdate = sdf.parse(time);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				Long currentTime = nowdate.getTime();
				Log.i("setPlayPlan", "cuurrentTime = " + time + " beginTime = "
						+ sdf.format(begindate));
				if (!TextUtils.isEmpty(String.valueOf(currentTime))) {
					if (currentTime.equals(beginTime)) {
						startPlay(playPlanServerIp, playPlanBeginTime,
								playPlanFileName, playPlanPort);
						Log.i("setPlayPlan", "起始时间相等");
						// startPlay(playPlanBeginTime,
						// playPlanFileName);
					}
				} else {
					Log.i("setPlayPlan", "起始不时间相等");
				}
			} else {
				Log.i("setPlayPlan", "时间为空");
			}
		}
	}

	/*
	 * 开启定时下载计划
	 */
	@SuppressWarnings("deprecation")
	private void startDownLoadAlarmManager() {
		Log.i("Msg", "start1");
		for (DownLoadPlan downloadPlan : downLoadList) {
			Log.i("Msg", "start2");
			String downLoadServerIp = downloadPlan.getServerIp();
			String downLoadBeginTime = downloadPlan.getBeginTime();
			String downLoadFileName = encode(downloadPlan.getFileName().trim());
			String downLoadPort = downloadPlan.getPort();

			String url = "http://" + downLoadServerIp + ":" + downLoadPort
					+ "/" + downLoadFileName;
			// String url = "etest";
			Log.i("Msg", "url=" + url);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// SimpleDateFormat sdfId = new SimpleDateFormat(1+"MMddHHmm");
			SimpleDateFormat sdfId = new SimpleDateFormat("MMddHHmmss");

			java.util.Date sdate = null;
			try {
				sdate = sdf.parse(downLoadBeginTime);
			} catch (ParseException e1) {

				e1.printStackTrace();
			}
			Calendar c = Calendar.getInstance();
			c.setTime(sdate);

			int requestCode = Integer.parseInt(sdfId.format(sdate).trim());
			Log.i("CheckNoteActivity", "定时下载的Id：" + requestCode);

			// int num = 1228183056;

			if (dao.checkId(requestCode, DOWN_TABLE)) {
				// Log.i("CheckNoteActivity","新建表后再添加");
				dao.addData(requestCode, url, downLoadFileName, DOWN_TABLE);

			} else {
				// Log.i("CheckNoteActivity","删除表后再新建");
				dao.delete(requestCode, DOWN_TABLE); // 先删除传过来的Id对应的数据，再新增
				dao.addData(requestCode, url, downLoadFileName, DOWN_TABLE);
			}
			AlarmManagerUtil
					.setDownLoadAlarm(getApplicationContext(),
							c.get(Calendar.YEAR), c.get(Calendar.MONTH),
							c.get(Calendar.DAY_OF_MONTH),
							c.get(Calendar.HOUR_OF_DAY),
							c.get(Calendar.MINUTE), c.get(Calendar.SECOND),
							requestCode);
		}
	}

	/*
	 * 每天都有（定时直播）
	 */
	Boolean isRepeatLiving;
	Boolean isEveryLiving;
	Boolean isLivingOne;
	String realLivingEnd = "";
	String realTime = "00:00"; // 真正结束时间
	String livingWeekDay;
	String playTime = "";

	private void startTimingLiving() {
		cc = Calendar.getInstance(); // 当前日历对象
		week = cc.get(Calendar.DAY_OF_WEEK) + "";

		isRepeatLiving = false;
		isEveryLiving = false;
		for (TimingLiving timingLive : timingLivingList) {
			Log.i("TTTT", "MESSAGE_CHECK_TIMING_LIVING_VALUE.......5");
			String livingIp = timingLive.getServerIp();
			String livingPort = timingLive.getPort();
			String livingChannel = timingLive.getChannel();
			String livingUser = timingLive.getUserName();
			String livingPass = timingLive.getPassWord();
			String livingStart = timingLive.getStartTime();
			String livingEnd = timingLive.getEndTime();
			// if(helper.hasSD()){
			// helper.writeSDFile("直播计划==>StartTime:"+livingStart,
			// "LogCode.txt");
			// helper.writeSDFile("直播计划==>EndTime  :"+livingEnd, "LogCode.txt");
			// helper.writeSDFile("直播计划==>Channel  :"+livingChannel,
			// "LogCode.txt");
			//
			// }
			// 如果播放时间和结束时间相等那么直接跳出循环
			if (livingStart.equalsIgnoreCase(livingEnd)) {
				continue;
			}

			if (timingLive.getWeekDay() != null) {
				livingWeekDay = timingLive.getWeekDay();

				Log.v("test", "今天" + week + "定时星期：" + livingWeekDay + "开始时间"
						+ livingStart);
				// 如果是每日任务 判断星期几播 还是每日都播
				if ("0".equalsIgnoreCase(livingWeekDay)) {
					isEveryLiving = true;
					Log.v("week", "--------今日星期:" + week + "-----预定日期"
							+ livingWeekDay);
					Log.v("test", "是每日直播任务");
				} else {
					isEveryLiving = false;
					Log.v("week", "--------今日星期:" + week + "-----预定日期"
							+ livingWeekDay);
				}

				// 每周几播 星期不对不给播
				if (week.equalsIgnoreCase(livingWeekDay) == false
						&& !isEveryLiving) {
					Log.v("7.30", "--------今日星期:" + week + "-----预定日期"
							+ livingWeekDay + "星期不对");
					continue;
				} else {
					// Log.v("7.30", "--------else 今日星期:" + week + "-----预定日期"
					// + livingWeekDay);
				}
			}
			String volume = timingLive.getVolume();
			Date now = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
			String nowTime = sdf.format(now);
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/M/d HH:mm");
			String nowTime2 = sdf2.format(now);
			SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy/MM/d HH:mm");
			String nowTime3 = sdf3.format(now);
			SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy/M/dd HH:mm");
			String nowTime4 = sdf4.format(now);
			// 判断是否是播放一次 是否到了播放时间
			if (nowTime.equalsIgnoreCase(livingStart)
					|| nowTime2.equalsIgnoreCase(livingStart)
					|| nowTime3.equalsIgnoreCase(livingStart)
					|| nowTime4.equalsIgnoreCase(livingStart)) {
				isLivingOne = true;
				Log.v("startTimingLiving", "-----nowTime-true=" + nowTime);
			} else {
				isLivingOne = false;
				Log.v("startTimingLiving", "-----nowTime-false=" + nowTime);
			}

			if (isLivingOne
					|| getCurrentTime() == parseStrTimeToms(livingStart)) { // 开始时间则开始播放
				playTime = livingStart;
				realTime = livingEnd;

				if (livingStart.length() > 10) {
					Constant.VCR_START_TIME = livingStart.split(" ")[1];
					Constant.VCR_END_TIME = livingEnd.split(" ")[1];
				} else {
					Constant.VCR_START_TIME = livingStart;
					Constant.VCR_END_TIME = livingEnd;

				}
				Constant.VIDEO_END_TIME = "00:00";
				// if(helper.hasSD()){
				// helper.writeSDFile("Constant.VCR_START_TIME:"+Constant.VCR_START_TIME,
				// "LogCode.txt");
				// helper.writeSDFile("Constant.VCR_END_TIME:"+Constant.VCR_END_TIME,
				// "LogCode.txt");
				// }
				Log.v("7.30", "--------开始时间:" + livingStart + "--------结束时间:"
						+ livingEnd);
				Log.v("7.30", "--------今日星期:" + week + "-----预定日期"
						+ livingWeekDay);
				Log.i("TTTT", "等于设定开始时间.......6");
				if (!ManagerUtil.isThisActivityTop(this,
						Constant.INDEX_ACTIVITY_NAME)) {// 如果当前Activity不在主界面，则退回到主界面
					DianJiaoApplication.getInstance().exit();
					Log.i("TTTT", "先回到主界面1");
					Intent intent = new Intent(this, IndexActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				}

				Intent intent = new Intent();
				intent.setAction("com.example.iimp_znxj_new2014.stopPlay");
				sendBroadcast(intent);
				// 推送的定时计划的开始时间
				// JingMoOrder.postErrorLog(PreferenceUtil.getSharePreference(UdpService.this,
				// Constant.SERVER_IP_PORT),
				// PreferenceUtil.getSharePreference(UdpService.this,
				// Constant.TERMINAL_IP),
				// "直播开始时间----" + now.getHours() + ":" + now.getMinutes() + ":"
				// + now.getSeconds() + "-----通道号:" + livingChannel);
				//
				// JingMoOrder.postErrorLog3(UdpService.this,
				// arg1.getMessage(),Constant.NORMAL_MSG);
				new Thread(new Client("---直播开始时间" + now.getHours() + ":"
						+ now.getMinutes() + ":" + now.getSeconds())
						+ "-----通道号:" + livingChannel).start();

				// 如果该计划执行将播放的信息写入文件
				SharedPreferences sp = DianJiaoApplication.getInstance()
						.getSharedPreferences("TvShow", Context.MODE_PRIVATE);

				SharedPreferences.Editor editor = sp.edit();
				editor.putString("ip", livingIp);
				editor.putString("port", livingPort);
				editor.putString("user", livingUser);
				editor.putString("passWord", livingPass);
				editor.putString("channel", livingChannel);
				editor.putString("volume", volume);
				editor.commit();
				Intent alarmVcr = null;
				String VCRType = PreferenceUtil.getSharePreference(this,
						Constant.SHAREDPREFERENCE_VCR_TYPE, 1 + "");
				if ("1".equals(VCRType)) {
					alarmVcr = new Intent(this, TVActivity.class);
				} else if ("2".equals(VCRType)) {
					alarmVcr = new Intent(this, KeDaVCRActivity.class);
				}

				alarmVcr.putExtra("ip", livingIp);
				alarmVcr.putExtra("port", livingPort);
				alarmVcr.putExtra("user", livingUser);
				alarmVcr.putExtra("password", livingPass);
				alarmVcr.putExtra("channel", livingChannel);
				alarmVcr.putExtra("volume", volume);
				alarmVcr.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				final Intent VcrIntent = alarmVcr;
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							Thread.sleep(500);
						} catch (Exception e) {
						}
						startActivity(VcrIntent);
						Constant.isLiveTime = true;
					}
				}).start();

			} else if (realTime != null
					&& getCurrentTime() == parseStrTimeToms(realTime)
					|| nowTime.equalsIgnoreCase(realTime)
					|| nowTime2.equalsIgnoreCase(realTime)
					|| nowTime3.equalsIgnoreCase(realTime)
					|| nowTime4.equalsIgnoreCase(realTime)) { // 结束时间则结束播放

				// 推送的定时计划的结束时间
				// JingMoOrder.postErrorLog(PreferenceUtil.getSharePreference(UdpService.this,
				// Constant.SERVER_IP_PORT),
				// PreferenceUtil.getSharePreference(UdpService.this,
				// Constant.TERMINAL_IP),
				// "------直播结束时间" + now.getHours() + ":" + now.getMinutes() +
				// ":" + now.getSeconds() + "-----通道号:" + livingChannel);
				Log.v("7.30", "--------结束时间:" + realTime);
				Constant.isLiveTime = false;
				new Thread(new Client("直播结束时间-----" + now.getHours() + ":"
						+ now.getMinutes() + ":" + now.getSeconds())
						+ "-----通道号:" + livingChannel).start();

				Intent intent = new Intent();
				intent.setAction("com.example.iimp_znxj_new2014.stopPlay");
				sendBroadcast(intent);
			}
		}
	}

	/*
	 * TimingPlan分析处理
	 */
	ArrayList<String> tmPanPalyTimes = new ArrayList<String>(); // 存储时间的集合
	Boolean isRepeat; // 是否重复
	Calendar cc; // 当前日历对象
	String week; // 今天星期几
	Boolean isEvery; // 是否每日都播

	Boolean isOne; // 是否是只播放一次
	String timingPlanLoop;
	String volume;

	private void startTimingPlan() {

		isRepeat = false;
		cc = Calendar.getInstance();
		isEvery = false;
		week = cc.get(Calendar.DAY_OF_WEEK) + "";
		for (TimingPlan timingPlan : timingPlanList) {
			String tmPlanLocal = timingPlan.getLocal();
			String tmPlanServerIp = timingPlan.getServerIp();
			String tmPlanPort = timingPlan.getPort();
			String tmPlanPlayTime = timingPlan.getPlayTime();
			String tmPlanFileName = timingPlan.getFileName();
			String tmPlanEndTime = timingPlan.getEndTime();
			String url = null;
			// if(helper.hasSD()){
			// helper.writeSDFile("流媒体计划==>StartTime:"+tmPlanPlayTime,
			// "LogCode.txt");
			// helper.writeSDFile("流媒体计划==>EndTime  :"+tmPlanEndTime,
			// "LogCode.txt");
			// helper.writeSDFile("流媒体计划==>FileName :"+tmPlanEndTime,
			// "LogCode.txt");
			//
			// }
			//
			// 是否循环
			Log.e("tmPlanEndTime", tmPlanEndTime);
			if (timingPlan.getLoop() != null) {
				timingPlanLoop = timingPlan.getLoop();
				Log.v("7.23", "loop不为空" + timingPlanLoop);
			} else {
				timingPlanLoop = null;
			}

			// 多媒体声音
			String volume = timingPlan.getVolume();

			// 如果播放时间和结束时间相等那么直接跳出循环
			if (tmPlanPlayTime.equalsIgnoreCase(tmPlanEndTime)) {
				continue;
			}

			// 判断每周是否为空
			if (timingPlan.getWeekDay() != null) {
				String tmPlanWeek = timingPlan.getWeekDay();
				Log.v("test", "定时时间：" + tmPlanPlayTime + "星期几：" + tmPlanWeek
						+ "今日星期" + week);
				// Log.i("TimingPlanList","1.基本信息："+tmPlanLocal+"|"+tmPlanServerIp+"|"+tmPlanPort+"|"+tmPlanFileName);

				// 判断星期几播 还是每日都播
				if ("0".equalsIgnoreCase(tmPlanWeek)
						&& tmPlanPlayTime.length() < 10) {
					isEvery = true;
					// Log.v("week",
					// "--------今日星期:"+week+"-----预定日期"+tmPlanWeek);
					Log.v("test", "每日都播");
				} else {
					isEvery = false;
				}

				// 如果是周几播 判断星期几是否与今天相同
				if (tmPlanPlayTime.length() < 10
						&& week.equalsIgnoreCase(tmPlanWeek) == false
						&& !isEvery) {
					// Log.v("week",
					// "--------今日星期:"+week+"-----预定日期"+tmPlanWeek);
					Log.v("test", "星期不对");
					continue;
				}
			}

			// 将文件名称URLdEncode格式化
			try {
				tmPlanFileName = URLEncoder.encode(tmPlanFileName, "utf-8"); // utf-8格式
			} catch (UnsupportedEncodingException e2) {
				e2.printStackTrace();
			}

			if (tmPlanLocal.equals("true")) { // 定时播放本地
				url = "/mnt/extsd/Download/" + tmPlanFileName;
			} else {
				url = "http://" + tmPlanServerIp + ":" + tmPlanPort + "/"
						+ tmPlanFileName;
				// Log.v("test",url);
				Log.v("test", "将会有视频播放");
			}

			Log.i("TimingPlanList", "2.播放路径：" + url);

			// 各种格式的时间
			Date now = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
			String nowTime = sdf.format(now);
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/M/d HH:mm");
			String nowTime2 = sdf2.format(now);
			SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy/MM/d HH:mm");
			String nowTime3 = sdf3.format(now);
			SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy/M/dd HH:mm");
			String nowTime4 = sdf4.format(now);
			Log.v("7.14", nowTime + nowTime2 + nowTime3 + nowTime4
					+ tmPlanPlayTime);
			// 判断是否是播放一次且 是否到了播放时间
			if (nowTime.equalsIgnoreCase(tmPlanPlayTime)
					|| nowTime2.equalsIgnoreCase(tmPlanPlayTime)
					|| nowTime3.equalsIgnoreCase(tmPlanPlayTime)
					|| nowTime4.equalsIgnoreCase(tmPlanPlayTime)) {
				isOne = true;
				Log.v("7.14", nowTime + nowTime2 + nowTime3 + nowTime4
						+ tmPlanPlayTime + isOne);
			} else {
				isOne = false;
				Log.v("7.14", nowTime + nowTime2 + nowTime3 + nowTime4
						+ tmPlanPlayTime + isOne);
			}

			String[] type = tmPlanFileName.trim().split("\\.");

			// 当前时间是开始时间

			if (isOne || getCurrentTime() == parseStrTimeToms(tmPlanPlayTime)) {
				if (tmPlanPlayTime.length() > 10) {
					Constant.VIDEO_START_TIME = tmPlanPlayTime.split(" ")[1];
					Constant.VIDEO_END_TIME = tmPlanEndTime.split(" ")[1];
				} else {
					Constant.VIDEO_START_TIME = tmPlanPlayTime;
					Constant.VIDEO_END_TIME = tmPlanEndTime;
				}
				Constant.VCR_END_TIME = "00:00";
				// if(helper.hasSD()){
				// helper.writeSDFile("Constant.VIDEO_START_TIME:"+Constant.VIDEO_START_TIME,
				// "LogCode.txt");
				// helper.writeSDFile("Constant.VIDEO_END_TIME:"+Constant.VIDEO_END_TIME,
				// "LogCode.txt");
				// }
				//
				realTime = tmPlanEndTime; // 只有定时计划播放才会有结束时间
				Log.v("test", "结束时间1：" + realTime);
				if (!ManagerUtil.isThisActivityTop(this,
						Constant.INDEX_ACTIVITY_NAME)) {// 如果当前Activity不在主界面，则退回到主界面
					DianJiaoApplication.getInstance().exit();
					Log.i("ALARM", "先回到主界面1");
					Intent intent = new Intent(this, IndexActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				}
				// Log.i("TimingPlanList","开始时间");
				try {

					Log.i("ALARM", "定时播流媒体2" + url);

					// 推送的定时计划的开始时间
					// JingMoOrder.postErrorLog(PreferenceUtil.getSharePreference(UdpService.this,
					// Constant.SERVER_IP_PORT),
					// PreferenceUtil.getSharePreference(UdpService.this,
					// Constant.TERMINAL_IP),
					// "流媒体开始时间:" + now.getHours() + ":" + now.getMinutes() +
					// ":" + now.getSeconds());

					JingMoOrder
							.postErrorLog3(
									UdpService.this,
									"流媒体开始时间:" + now.getHours() + ":"
											+ now.getMinutes() + ":"
											+ now.getSeconds(),
									Constant.ERROR_MSG);

					// 先发个结束广播 专门针对不能正常结束的rmvb格式其他格式不加也能正常播放
					Intent intent = new Intent();
					intent.setAction("com.example.iimp_znxj_new2014.stopPlay");
					sendBroadcast(intent);

					final Intent itto = new Intent(this,
							VideoShowActivity.class);

					Bundle bundle = new Bundle();
					itto.setData(Uri.parse(url));
					bundle.putString("host", tmPlanServerIp);
					bundle.putString("port", tmPlanPort);
					PreferenceUtil.putSharePreference(UdpService.this,
							Constant.VIDEO_IP, tmPlanServerIp); // 写入文件
					PreferenceUtil.putSharePreference(UdpService.this,
							Constant.VIDEO_PORT, tmPlanPort); // 写入文件
					if (volume != null) {
						bundle.putString("volume", volume);
					}

					if (timingPlanLoop != null) {
						bundle.putString(Constant.PLAY_VIDEO_LOOP,
								timingPlanLoop);
					}

					itto.putExtras(bundle);
					itto.putExtra("flag", false);
					itto.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					new Thread(new Runnable() { // 延迟1s打开协查通报,等待发送广播和响应广播完成

								public void run() {
									try {
										Thread.sleep(500);
										startActivity(itto);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
							}).start();

				} catch (Exception e) {
					Log.i(TAG, "Error:" + e.getMessage());
					e.printStackTrace();
				}
			}
			// 当前时间是结束时间
			else if (realTime != null
					&& getCurrentTime() == parseStrTimeToms(realTime)
					|| nowTime.equalsIgnoreCase(realTime)
					|| nowTime2.equalsIgnoreCase(realTime)
					|| nowTime3.equalsIgnoreCase(realTime)
					|| nowTime4.equalsIgnoreCase(realTime)) {
				Log.v("test", "结束时间2：" + realTime);
				// 推送的定时计划的结束时间
				// JingMoOrder.postErrorLog(PreferenceUtil.getSharePreference(UdpService.this,
				// Constant.SERVER_IP_PORT),
				// PreferenceUtil.getSharePreference(UdpService.this,
				// Constant.TERMINAL_IP),
				// "流媒体结束时间:" + now.getHours() + ":" + now.getMinutes() + ":" +
				// now.getSeconds());
				JingMoOrder.postErrorLog3(UdpService.this,
						"流媒体结束时间:" + now.getHours() + ":" + now.getMinutes()
								+ ":" + now.getSeconds(), Constant.ERROR_MSG);
				try {
					if (mMediaPlayer.isPlaying()) {
						Log.i("ALARM", "结束时if");
						mMediaPlayer.stop();
						mMediaPlayer.reset();
						Intent udpIntent = new Intent(this, UdpService.class);
						udpIntent.setAction(Constant.ORDER_RESULT_ACTION);
						udpIntent.putExtra(Constant.ORDER_RESULT_EXTRA,
								Constant.ORDER_ACTION + "play,"
										+ Constant.ORDER_RESULT_OK);
						startService(udpIntent);
					} else {
						Log.i("ALARM", "结束时else");
						Intent intent = new Intent();
						intent.setAction("com.example.iimp_znxj_new2014.stopPlay");
						sendBroadcast(intent);
					}
				} catch (Exception e) {
					Log.i(TAG, "Error:" + e.getMessage());
					e.printStackTrace();
				}
			}

		}
		isSend = false;
	}

	/*
	 * 发送关闭广播，针对有年月日的
	 */
	private void startEndBroadCast(String endTime) {
		Log.i("EndTime", "结束时间=" + endTime);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		SimpleDateFormat sdfId = new SimpleDateFormat(1 + "MMddHHmm");

		java.util.Date sdate = null;
		try {
			sdate = sdf.parse(endTime);
		} catch (ParseException e1) {

			e1.printStackTrace();
		}
		Calendar c = Calendar.getInstance();
		c.setTime(sdate);

		int requestCode = Integer.parseInt(sdfId.format(sdate).trim());

		AlarmManagerUtil.sendEndBroadCast(UdpService.this,
				c.get(Calendar.YEAR), c.get(Calendar.MONTH),
				c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR_OF_DAY),
				c.get(Calendar.MINUTE), requestCode);

	}

	/*
	 * 播放计划
	 */
	@SuppressWarnings("deprecation")
	private void startPlayPlanAlarmManager() {
		Log.i("Msg", "start1");
		for (PlayPlan playPlan : playPlanList) {
			Log.i("Msg", "start2");
			String playPlanServerIp = playPlan.getServerIp();
			String playPlanPort = playPlan.getPort();
			String downLoadBeginTime = playPlan.getPlayTime();
			String downLoadFileName = encode(playPlan.getPlayFileName().trim());

			// String url = "pointless"; //没有SD卡
			String url = "http://" + playPlanServerIp + ":" + playPlanPort
					+ "/" + downLoadFileName;
			Log.i("playPlan", "url=" + url);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
			SimpleDateFormat sdfId = new SimpleDateFormat(1 + "MMddHHmm");

			java.util.Date sdate = null;
			try {
				sdate = sdf.parse(downLoadBeginTime);
			} catch (ParseException e1) {

				e1.printStackTrace();
			}
			Calendar c = Calendar.getInstance();
			c.setTime(sdate);

			int requestCode = Integer.parseInt(sdfId.format(sdate).trim());

			Log.i("Msg", "每日计划,日期信息：" + requestCode + "|" + sdate.getYear()
					+ "|" + sdate.getMonth() + "|" + sdate.getDay() + "|"
					+ sdate.getHours() + "|" + sdate.getMinutes());
			if (dao.checkId(requestCode, PLAY_PLAN_TABLE)) {
				dao.addData(requestCode, url, downLoadFileName, PLAY_PLAN_TABLE);
				Log.i("Msg", "true");
			} else {
				dao.delete(requestCode, PLAY_PLAN_TABLE);
				dao.addData(requestCode, url, downLoadFileName, PLAY_PLAN_TABLE);
				Log.i("Msg", "false,先删除再添加");
			}
			Log.i("Msg", "开启闹钟");
			// Log.i("Msg","startDownLoadAlarmManager:"+downLoadServerIp+"|"+downLoadBeginTime+"|"+downLoadFileName+"|"+downLoadPort+"|"+requestCode);
			AlarmManagerUtil.setPlayPlan(getApplicationContext(),
					c.get(Calendar.YEAR), c.get(Calendar.MONTH),
					c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR_OF_DAY),
					c.get(Calendar.MINUTE), requestCode);
		}
	}

	/*
	 * 获取时间后开启计划任务(天天有)
	 */
	private void startPlay(String playPlanServerIp, String playPlanBeginTime,
			String playPlanFileName, String playPlanPort) {
		Log.i("Msg", "startPlay");
		// Intent playPlanIntent = new Intent(this,
		// VideoShowPlanActivity.class);
		Intent playPlanIntent = new Intent(this, PlayLocalVideoActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(Constant.PLAY_VIDEO_PLAN_IP, playPlanServerIp); // /
		bundle.putString(Constant.PLAY_VIDEO_PLAN_BEGIN_TIME, playPlanBeginTime);
		bundle.putString(Constant.PLAY_VIDEO_PLAN_FILE_NAME, playPlanFileName);
		bundle.putString(Constant.PLAY_VIDEO_PLAN_PORT, playPlanPort); // /
		playPlanIntent.putExtras(bundle);
		playPlanIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(playPlanIntent);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		Log.v(TAG, "onBind");
		return null;
	}

	Boolean ischecked = false; // 是否已经发送
	Boolean isCheckedShutDownPlan = true;

	// 服务启动自动调用该方法
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(TAG, "onStartCommand");
		Log.v("7.31", "------service start");
		Log.d("jiayy11", "onStartCommand start" + " mSerialPort = "
				+ mSerialPort);
		flags = START_STICKY;
		Date date = new Date();
		final int startMinute = date.getMinutes();
		new Thread() {
			public void run() {
				while (!ischecked) {
					Date date = new Date();
					int nowMinute = date.getMinutes();
					if (nowMinute == startMinute + 1) {
						// 判断app是否活动
						mAppAliveHandler.sendEmptyMessage(MESSAGE_CHECK_ALIVE);

						Log.d("jiayy",
								"dailyLifeList != null && !dailyLifeList.isEmpty() onCreate = "
										+ (dailyLifeList != null && !dailyLifeList
												.isEmpty()));
						int sec = secondsTo60() * 1000;

						if (!mCheckDailyLifeHandler
								.hasMessages(MESSAGE_CHECK_TIMING_LIVING_VALUE)) {
							Log.v(TAG, "onStartCommand...直播命令");
							mCheckDailyLifeHandler.sendEmptyMessageDelayed(
									MESSAGE_CHECK_TIMING_LIVING_VALUE, sec); //
						}

						if (!mCheckDailyLifeHandler
								.hasMessages(MESSAGE_CHECK_TIMING_PLAN_VALUE)) {
							Log.v(TAG, "onStartCommand...定时命令");
							Log.i("z2", "定时计划开启");
							mCheckDailyLifeHandler.sendEmptyMessageDelayed(
									MESSAGE_CHECK_TIMING_PLAN_VALUE, sec); //
						}

						if (!mCheckDailyLifeHandler
								.hasMessages(MESSAGE_CHECK_DAILY_LIFE_VALUE)
								&& (dailyLifeList != null && dailyLifeList
										.size() != 0)) {
							Log.v(TAG, "checkdailylife in onStartCommand");
							mCheckDailyLifeHandler.sendEmptyMessageDelayed(
									MESSAGE_CHECK_DAILY_LIFE_VALUE, sec);
						}
						if (!mCheckDailyLifeHandler
								.hasMessages(MESSAGE_CHECK_PLAY_PLAN_VALUE)
								&& (playPlanList != null && playPlanList.size() != 0)) {
							Log.v(TAG, "checkPlayPlan in onStartCommand");
							mCheckDailyLifeHandler.sendEmptyMessageDelayed(
									MESSAGE_CHECK_PLAY_PLAN_VALUE, sec);
						}
						if (!mCheckDailyLifeHandler
								.hasMessages(MESSAGE_CHECK_OFF_VALUE)) {
							Log.v(TAG, "onStartCommand...定时开关机命令");
							mCheckDailyLifeHandler.sendEmptyMessageDelayed(
									MESSAGE_CHECK_OFF_VALUE, sec);

						}
						if (!mCheckDailyLifeHandler
								.hasMessages(MESSAGE_CHECK_FULLSUBTITLE_PLAN)) {
							Log.v(TAG, "onStartCommand...定时滚动计划");
							mCheckDailyLifeHandler.sendEmptyMessageDelayed(
									MESSAGE_CHECK_FULLSUBTITLE_PLAN, sec);
						}
						if (isCheckedShutDownPlan) {
							startShutPlan();
							isCheckedShutDownPlan = false;
						}

						ischecked = true;
						Log.v("7.22", "定时启动");
					}
				}
			}
		}.start();

		// 读取音量
		VOLUME_VALUE = PreferenceUtil.getIntegerSharePreference(
				UdpService.this, "VOLUME_NUM_VALUE");
		Log.v("7.30", "-----VOLUME_VALUE" + VOLUME_VALUE);

		mThread = new ClientReadThread();
		// 线程
		if (!mThread.isAlive()) {
			mThread.start();
		}

		if (intent != null) {
			String actionStr = intent.getAction();
			String orderResult = intent
					.getStringExtra(Constant.ORDER_RESULT_EXTRA);
			if (!TextUtils.isEmpty(actionStr)) {
				if (Constant.ORDER_RESULT_ACTION.equals(actionStr)) {
					new Thread(new Client(orderResult)).start();
				}
			}
		}
		// new Thread(new Client()).start();
		return super.onStartCommand(intent, flags, startId);
	}

	private int getCurrentTime() {
		Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		Log.d("jiayy", "hour = " + hour + " minute = " + minute);
		return hour * 60 + minute;
	}

	public void onDestroy() {
		Log.v(TAG, "service ondestroy");
		super.onDestroy();
		if (mAppAliveHandler.hasMessages(MESSAGE_CHECK_ALIVE)) {
			mAppAliveHandler.removeMessages(MESSAGE_CHECK_ALIVE);
		}
		if (mCheckDailyLifeHandler.hasMessages(MESSAGE_CHECK_DAILY_LIFE_VALUE)) {
			mCheckDailyLifeHandler
					.removeMessages(MESSAGE_CHECK_DAILY_LIFE_VALUE);
		}
		if (mCheckDailyLifeHandler.hasMessages(MESSAGE_CHECK_PLAY_PLAN_VALUE)) {
			mCheckDailyLifeHandler
					.removeMessages(MESSAGE_CHECK_PLAY_PLAN_VALUE);
		}
		if (mCheckDailyLifeHandler.hasMessages(MESSAGE_CHECK_OFF_VALUE)) {
			mCheckDailyLifeHandler.removeMessages(MESSAGE_CHECK_OFF_VALUE);
		}
		if (mCheckDailyLifeHandler.hasMessages(MESSAGE_INTERUPT_READTHREAD)) {
			mCheckDailyLifeHandler.removeMessages(MESSAGE_INTERUPT_READTHREAD);
		}
		if (mCheckDailyLifeHandler.hasMessages(MESSAGE_CHECK_FULLSUBTITLE_PLAN)) {
			mCheckDailyLifeHandler
					.removeMessages(MESSAGE_CHECK_FULLSUBTITLE_PLAN);
		}

		if (mReadThread != null)
			mReadThread.interrupt();
		closeSerialPort();
		mSerialPort = null;
		EventBus.getDefault().unregister(this);
	}

	public void onManualDestroy() {
		Log.v(TAG, "onManualDestroy");
		super.onDestroy();
		if (mAppAliveHandler.hasMessages(MESSAGE_CHECK_ALIVE)) {
			mAppAliveHandler.removeMessages(MESSAGE_CHECK_ALIVE);
		}
		if (mCheckDailyLifeHandler.hasMessages(MESSAGE_CHECK_DAILY_LIFE_VALUE)) {
			mCheckDailyLifeHandler
					.removeMessages(MESSAGE_CHECK_DAILY_LIFE_VALUE);
		}
		if (mCheckDailyLifeHandler.hasMessages(MESSAGE_CHECK_PLAY_PLAN_VALUE)) {
			mCheckDailyLifeHandler
					.removeMessages(MESSAGE_CHECK_PLAY_PLAN_VALUE);
		}
		if (mCheckDailyLifeHandler.hasMessages(MESSAGE_CHECK_OFF_VALUE)) {
			mCheckDailyLifeHandler.removeMessages(MESSAGE_CHECK_OFF_VALUE);
		}
		if (mCheckDailyLifeHandler.hasMessages(MESSAGE_INTERUPT_READTHREAD)) {
			mCheckDailyLifeHandler.removeMessages(MESSAGE_INTERUPT_READTHREAD);
		}
		if (mCheckDailyLifeHandler.hasMessages(MESSAGE_CHECK_FULLSUBTITLE_PLAN)) {
			mCheckDailyLifeHandler
					.removeMessages(MESSAGE_CHECK_FULLSUBTITLE_PLAN);
		}

		if (mReadThread != null)
			mReadThread.interrupt();
		closeSerialPort();
		mSerialPort = null;
	}

	// 该线程用于实时接收服务器端json格式命令
	public class ClientReadThread extends Thread {
		int count = 0;

		public void run() {
			try {
				// InetAddress serverAddr = InetAddress.getByName(remoteip);
				Log.d("jiayy", "readSocket = " + readSocket);
				if (readSocket == null) {
					readSocket = new DatagramSocket(port);
				}

				while (true) {
					DatagramPacket dPacket;
					byte[] udp_message = new byte[4096];
					dPacket = new DatagramPacket(udp_message,
							udp_message.length);
					readSocket.receive(dPacket);
					Log.d(TAG, "socket address"
							+ dPacket.getAddress().getHostAddress());
					serverIp = dPacket.getAddress().getHostAddress();
					// 偏好设置
					PreferenceUtil.putSharePreference(UdpService.this,
							Constant.SERVER_IP, serverIp);
					count = count + dPacket.getLength();
					// serverStr = new String(dPacket.getData(),
					// "gb2312");//串口调试助手可用

					serverStr = new String(dPacket.getData(), "utf-8");//
					Log.i("File", "serverStr = " + serverStr);
					if (TextUtils.isEmpty(serverStr)) {

					} else { // 接收到命令，实例化。。。
						parseProcessor = ParseProcessor
								.getInstance(UdpService.this);
						parseProcessor.registerParseListener(UdpService.this);
						parseProcessor.parseData(serverStr); // 解析接收到的json格式数据
						if (serverStr.equals("dianming")) {
							Log.i("TTS", "开启点名");
						}
					}
					Log.d(TAG, "str = " + serverStr);
					Intent intent = new Intent();
					intent.setAction("com.example.show.service"); // 这条广播好像没有接收的对象？
					intent.putExtra("testStr", serverStr);
					sendBroadcast(intent);
					if (dPacket.getLength() < 512)
						Log.e(TAG, "dPacket.getLength() < 512------" + count);
				}
			} catch (IOException e) {

				Log.d("jiayy", "IOException");
				e.printStackTrace();
			}
		}
	}

	/*
	 * 回复信息至服务器端
	 */
	private int count = 0;// 播放3次
	public DatagramSocket sendSocket1;

	@SuppressLint("NewApi")
	public class Client implements Runnable {
		String result = "";

		public Client(String resultForServer) {
			result = resultForServer;
			count = 0;
		}

		@Override
		public void run() {
			while (count < 3) {
				count++;
				Log.v("7.13", count + "");
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				try {
					if (TextUtils.isEmpty(serverIp)) {
						serverIp = PreferenceUtil.getSharePreference(
								UdpService.this, Constant.SERVER_IP);
					}
					InetAddress serverAddr = InetAddress.getByName(serverIp);
					sendSocket = new DatagramSocket();
					byte[] buf = null;
					int serverPortInt = 0;
					if (!TextUtils.isEmpty(result)) {
						buf = result.getBytes();
						if (TextUtils.isEmpty(serverPort)) {
							serverPort = PreferenceUtil.getSharePreference(
									UdpService.this, Constant.SERVER_PORT);
						}
						if (!TextUtils.isEmpty(serverPort)) {
							serverPort = PreferenceUtil.getSharePreference(
									UdpService.this, Constant.SERVER_PORT);
							serverPortInt = Integer.valueOf(serverPort);
							DatagramPacket packet = new DatagramPacket(buf,
									buf.length, serverAddr, serverPortInt);

							sendSocket.send(packet);
							sendSocket.close();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 仅回复实时查看调用
	@SuppressLint("NewApi")
	public class Client1 implements Runnable {
		String result1 = "";

		public Client1(String resultForServer) {
			result1 = resultForServer;
		}

		@Override
		public void run() {

			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			try {
				if (TextUtils.isEmpty(serverIp)) {
					serverIp = PreferenceUtil.getSharePreference(
							UdpService.this, Constant.SERVER_IP);
				}
				InetAddress serverAddr = InetAddress.getByName(serverIp);
				sendSocket1 = new DatagramSocket();
				byte[] buf = null;
				int serverPortInt = 0;
				if (!TextUtils.isEmpty(result1)) {
					buf = result1.getBytes();
					if (TextUtils.isEmpty(serverPort)) {
						serverPort = PreferenceUtil.getSharePreference(
								UdpService.this, Constant.SERVER_PORT);
					}
					if (!TextUtils.isEmpty(serverPort)) {
						serverPort = PreferenceUtil.getSharePreference(
								UdpService.this, Constant.SERVER_PORT);
						serverPortInt = Integer.valueOf(serverPort);
						DatagramPacket packet = new DatagramPacket(buf,
								buf.length, serverAddr, serverPortInt);
						sendSocket1.send(packet);
						sendSocket1.close();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private int secondsTo60() {
		Calendar c = Calendar.getInstance();
		int sec = c.get(Calendar.SECOND);
		Log.i("Msg", "sec=" + sec);
		return 60 - sec;
	}

	public static String getServerStr() {
		return serverStr;
	}

	/*
	 * 实现了接口ParseListener解析完调用该方法
	 * 
	 * @see
	 * com.example.iimp_znxj_new2014.processor.ParseProcessor.ParseListener#
	 * onParseFinished(java.lang.Object)
	 */
	Boolean isLoopNull; // 为了兼容之前命令做个判断是否为空
	String palyVideoLoop; // loop的值
	private String playVideoFileName;

	@Override
	public void onParseFinished(Object data) {
		Log.v(TAG, "onParseFinished");
		if (data instanceof PlayVCR) {
			if (ManagerUtil.isThisActivityTop(this,
					Constant.SELFCONSUME_BUYACTIVITY_NAME)
					|| ManagerUtil.isThisActivityTop(this,
							Constant.VIDEO_SHOW_ACTIVITY_NAME)
					|| ManagerUtil.isThisActivityTop(this,
							Constant.AUDIO_ACTIVITY_NAME)
					|| ManagerUtil.isThisActivityTop(this,
							Constant.CHECK_NOTE_ACTIVITY_NAME)
					|| ManagerUtil.isThisActivityTop(this,
							Constant.EMERGY_NOTE_ACTIVITY_NAME)
					|| ManagerUtil.isThisActivityTop(this,
							Constant.THREE_GD_ACTIVITY_NAME)
					|| ManagerUtil.isThisActivityTop(this,
							Constant.LIFE_FOODMENU_ACTIVITY_NAME)) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();// VIDEO_SHOW_ACTIVITY_NAME,LOCAL_VIDEO_ACTIVITY_NAME
				intent.setAction("com.example.iimp_znxj_new2014.stopPlay");
				intent.putExtras(bundle);
				sendBroadcast(intent);
				Log.i("TTTS", "关闭正在播放的视频Activity");
			}
			String playIp = ((PlayVCR) data).getServerIp();
			String playPort = ((PlayVCR) data).getPort();
			String channel = ((PlayVCR) data).getChannel();
			String userName = ((PlayVCR) data).getUserName();
			String passWord = ((PlayVCR) data).getPassWord();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			String time = sdf.format(date);
			Date nowdate = null;
			try {
				nowdate = sdf.parse(time);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			long currentTime = nowdate.getTime();
			String currentTimeStr = String.valueOf(currentTime);
			String prePlayVideoTimeStr = PreferenceUtil.getSharePreference(
					this, Constant.PRE_PLAY_VCR_TIME);
			long prePlayVideoTime = 0;
			if (!TextUtils.isEmpty(prePlayVideoTimeStr)
					&& ManagerUtil.isThisActivityTop(this,
							Constant.VCR_SHOW_ACTIVITY_NAME)) {
				prePlayVideoTime = Long.parseLong(prePlayVideoTimeStr);
				Log.d("PlayVCR", " (currentTime - prePlayVideoTime) / 1000 = "
						+ (currentTime - prePlayVideoTime) / 1000);
				/*
				 * if ((currentTime - prePlayVideoTime) / 1000 < 10) { return; }
				 * else { Log.d("PlayVCR", "play vcr > 10"); Intent intent = new
				 * Intent(); intent.setAction(Constant.ANOTHEVCRRPLAY_ACTION);
				 * Bundle bundle = new Bundle();
				 * bundle.putString(Constant.PLAY_VCR_IP, playIp);
				 * bundle.putString(Constant.PLAY_VCR_PORT, playPort);
				 * bundle.putString(Constant.PLAY_VCR_CHANNEL, channel);
				 * bundle.putString(Constant.PLAY_VCR_USER_NAME, userName);
				 * bundle.putString(Constant.PLAY_VCR_PASSWORD, passWord);
				 * intent.putExtras(bundle); sendBroadcast(intent); return; }
				 */
			}
			PreferenceUtil.putSharePreference(this, Constant.PRE_PLAY_VCR_TIME,
					currentTimeStr);

			realTime = "-1"; // 清除定时计划的结束时间
			final Intent vcrIntent = new Intent(this, TVActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString(Constant.PLAY_VCR_IP, playIp);
			bundle.putString(Constant.PLAY_VCR_PORT, playPort);
			bundle.putString(Constant.PLAY_VCR_CHANNEL, channel);
			bundle.putString(Constant.PLAY_VCR_USER_NAME, userName);
			bundle.putString(Constant.PLAY_VCR_PASSWORD, passWord);
			vcrIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			vcrIntent.putExtras(bundle);
			Log.d("PlayVCR", "startActivity(vcrIntent)" + playIp + "|"
					+ playPort + "|" + channel + "|" + userName + "|"
					+ passWord);

			new Thread(new Runnable() { // 延迟200ms打开
						public void run() {
							try {
								Thread.sleep(800);
								startActivity(vcrIntent);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}).start();

		} else if (data instanceof PlayLocal) { // 播放本地视频
			String filename = ((PlayLocal) data).getFileName();
			String url = "/mnt/extsd/Download/" + filename;
			String[] type = filename.trim().split("\\.");// 用“.”作为分隔的话，必须是如下写法：String.split("\\."),这样才能正确的分隔开

			if (mMediaPlayer.isPlaying()
					|| ManagerUtil.isThisActivityTop(this,
							Constant.CHECK_NOTE_ACTIVITY_NAME)) {
				mMediaPlayer.stop();
				mMediaPlayer.reset();

				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				intent.setAction(Constant.STOPPLAY_ACTION);
				intent.putExtras(bundle);
				sendBroadcast(intent);
				Log.i("PlayLocal", "关闭正在播放的音乐或关闭协查通报");
			}
			if (!JingMoOrder.fileIsExists(url)) {
				Log.i("PlayLocal", "文件不存在");
				new Thread(new Client("playLocalVideo:false,no such file"))
						.start();
			} else {
				if (type.length > 1 && type[1].equals("mp3")
						|| type[1].equals("wav") || type[1].equals("wma")) { // 开始时间则开始播放
					Log.i("PlayLocal", "播放本地音乐（非定时计划）：" + filename);
					if (ManagerUtil.isThisActivityTop(this,
							Constant.LOCAL_VIDEO_ACTIVITY_NAME)
							|| ManagerUtil.isThisActivityTop(this,
									Constant.LOCAL_VIDEO_ACTIVITY_PLANNAME)
							|| ManagerUtil.isThisActivityTop(this,
									Constant.LOCAL_VIDEO_ACTIVITY_BNAME)
							|| ManagerUtil.isThisActivityTop(this,
									Constant.SELFCONSUME_BUYACTIVITY_NAME)
							|| ManagerUtil.isThisActivityTop(this,
									Constant.THREE_GD_ACTIVITY_NAME)
							|| ManagerUtil.isThisActivityTop(this,
									Constant.LIFE_FOODMENU_ACTIVITY_NAME)) {
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						intent.setAction("com.example.iimp_znxj_new2014.stopPlay");
						intent.putExtras(bundle);
						sendBroadcast(intent);
						Log.i("PlayLocal", "关闭正在播放的视频Activity");
					}
					try {
						Log.i("PlayLocal", "1111:" + filename);
						if (isHasFile(filename)) {
							mMediaPlayer.setDataSource("/mnt/extsd/Download/"
									+ filename); // 本地路径
							mMediaPlayer.prepare();
							mMediaPlayer.start();
							new Thread(new Client("playLocalMusic:true"))
									.start();
						} else {
							new Thread(new Client(
									"playLocalMusic:false,no such file"))
									.start();
						}
					} catch (Exception e) {
						Log.i("PlayLocal", "Error:" + e.getMessage());
						new Thread(new Client("playLocalMusic:false")).start();
						e.printStackTrace();
					}
				} else if (ManagerUtil.isThisActivityTop(this,
						Constant.LOCAL_VIDEO_ACTIVITY_NAME)
						|| ManagerUtil.isThisActivityTop(this,
								Constant.LOCAL_VIDEO_ACTIVITY_PLANNAME)
						|| ManagerUtil.isThisActivityTop(this,
								Constant.LOCAL_VIDEO_ACTIVITY_BNAME)) {
					Log.i("PlayLocal", "当前Activity为活动状态，发广播即可：" + filename);
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putString(Constant.PLAY_LOCAL_VIDEO_FILE_NAME,
							filename);
					intent.setAction(Constant.ANOTHELOCALVIDEO_ACTION);
					intent.putExtras(bundle);
					sendBroadcast(intent);
					// new Thread(new Client("playLocalVideo:true")).start();
				} else {
					Log.i("PlayLocal", "执行这个命令：" + url);
					final Intent itto = new Intent(this,
							VideoViewPlayingActivity.class);
					itto.setData(Uri.parse(url));
					// itto.setAction(Constant.MESSAGE_TYPE_ACTION);
					itto.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

					new Thread(new Runnable() { // 延迟200ms打开
								public void run() {
									try {
										Thread.sleep(200);
										startActivity(itto);
										// sendBroadcast(itto);
										// startActivity(localIntent);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
							}).start();
					// new Thread(new Client("playLocalVideo:true")).start();
				}
			}
		} else if (data instanceof AdjustDate) {
			String adjustServerUrl = ((AdjustDate) data).getAdjustUrl();
			PreferenceUtil.putSharePreference(UdpService.this,
					Constant.ADJUST_SERVER_URL, adjustServerUrl);
			PreferenceUtil.putSharePreference(UdpService.this,
					Constant.ADJUST_SERVER_FLAG, "true");
			PreferenceUtil.putSharePreference(UdpService.this, "haveAdjusted",
					"true");
			Log.v("reboot", PreferenceUtil.getSharePreference(UdpService.this,
					"haveAdjusted"));
			JingMoOrder.adjustServerTime(this, adjustServerUrl);

			Date date = new Date();

			new Thread(new Client("nowtime" + date.getHours() + ":"
					+ date.getMinutes())).start();
		} else if (data instanceof PlayVideo) { // 播放流媒体
			if (ManagerUtil.isThisActivityTop(this,
					Constant.VCR_SHOW_ACTIVITY_NAME)
					|| mMediaPlayer.isPlaying()
					|| ManagerUtil.isThisActivityTop(this,
							Constant.LOCAL_VIDEO_ACTIVITY_NAME)
					|| ManagerUtil.isThisActivityTop(this,
							Constant.LOCAL_VIDEO_ACTIVITY_PLANNAME)
					|| ManagerUtil.isThisActivityTop(this,
							Constant.SELFCONSUME_BUYACTIVITY_NAME)
					|| ManagerUtil.isThisActivityTop(this,
							Constant.THREE_GD_ACTIVITY_NAME)
					|| ManagerUtil.isThisActivityTop(this,
							Constant.LIFE_FOODMENU_ACTIVITY_NAME)
					|| ManagerUtil.isThisActivityTop(this,
							Constant.VIDEO_SHOW_ACTIVITY_NAME)) {
				Log.i("PlayVideo", "结束其他");
				// 结束正在播放的Activity
				Intent intent = new Intent();
				intent.setAction(Constant.STOPPLAY_VCR_ACTION);
				intent.setAction(Constant.STOPPLAY_ACTION);
				sendBroadcast(intent);
				// 结束正在播放的Music
				mMediaPlayer.stop();
				mMediaPlayer.reset();
			}

			Constant.VCR_END_TIME = "00:00";
			Constant.VIDEO_END_TIME = "00:00";

			int curPercent = 0;
			try {
				curPercent = Integer.parseInt(((PlayVideo) data)
						.getCurPercent());
			} catch (Exception e) {
				curPercent = 0;
			}
			String playVideoServerIp = ((PlayVideo) data).getSeverIp();
			String playVideoserverPort = ((PlayVideo) data).getPort();
			playVideoFileName = encode(((PlayVideo) data).getFileName()); // UTF-8编码
			String playVideoLocal = ((PlayVideo) data).getLocal();

			String url = "http://" + playVideoServerIp + ":"
					+ playVideoserverPort + "/" + playVideoFileName;
			PreferenceUtil.putSharePreference(UdpService.this,
					Constant.SERVER_IP_PORT, playVideoServerIp + ":"
							+ playVideoserverPort); // 写入文件
			PreferenceUtil.putSharePreference(UdpService.this,
					Constant.VIDEO_IP, playVideoServerIp); // 写入文件
			PreferenceUtil.putSharePreference(UdpService.this,
					Constant.VIDEO_PORT, playVideoserverPort); // 写入文件
			if (((PlayVideo) data).getLoop() != null) {
				palyVideoLoop = ((PlayVideo) data).getLoop();
				isLoopNull = false;
			} else {
				isLoopNull = true;
			}
			String volume = ((PlayVideo) data).getVolume();

			if (!ManagerUtil.isThisActivityTop(this,
					Constant.INDEX_ACTIVITY_NAME)) {// 如果当前Activity不在主界面，则退回到主界面
				DianJiaoApplication.getInstance().exit();
				Log.i("ALARM", "先回到主界面1");
				//
				Intent intent = new Intent(this, IndexActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}

			Log.d("PlayVideo", "new play:" + url);
			realTime = "-1"; // 清空计划的结束时间
			final Intent itto = new Intent(this, VideoShowActivity.class);
			Bundle bundle = new Bundle();
			if (isLoopNull == false) {
				bundle.putString(Constant.PLAY_VIDEO_LOOP, palyVideoLoop);
			}
			bundle.putInt("curPercent", curPercent);
			bundle.putString("volume", volume);
			bundle.putString("port", playVideoserverPort);
			bundle.putString("host", playVideoServerIp);
			itto.putExtras(bundle);
			itto.setData(Uri.parse(url));
			// itto.putExtra("mIsHwDecode", true);
			itto.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			new Thread(new Runnable() { // 延迟1s打开
						public void run() {
							try {
								Thread.sleep(1000);
								startActivity(itto);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}).start();

		} else if (data instanceof CheckNote) {
			if (ManagerUtil.isThisActivityTop(this,
					Constant.VCR_SHOW_ACTIVITY_NAME)) {
				Intent intent = new Intent();
				intent.setAction(Constant.STOPPLAY_VCR_ACTION);
				sendBroadcast(intent);
				Log.i("Error", "1");

			} else if (ManagerUtil.isThisActivityTop(this,
					Constant.LOCAL_VIDEO_ACTIVITY_NAME)) {
				Intent intent = new Intent();
				intent.setAction(Constant.STOPPLAY_ACTION);
				sendBroadcast(intent);
			} else {
				Intent intent = new Intent();
				intent.setAction(Constant.STOPPLAY_ACTION);
				sendBroadcast(intent);
			}
			String checkNoteIp = ((CheckNote) data).getServerIp();
			String checkNoteShowTime = ((CheckNote) data).getShowTime();
			String checkNotePicName = ((CheckNote) data).getPicName();
			String checkNotePort = ((CheckNote) data).getPort();
			final Intent checkNoteIntent = new Intent(this,
					CheckNoteActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString(Constant.CHECK_NOTE_SERVER_IP, checkNoteIp);
			bundle.putString(Constant.CHECK_NOTE_PIC_NAME, checkNotePicName);
			bundle.putString(Constant.CHECK_NOTE_SHOW_TIME, checkNoteShowTime);
			bundle.putString(Constant.CHECK_NOTE_PORT, checkNotePort);
			checkNoteIntent.putExtras(bundle);
			checkNoteIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			new Thread(new Runnable() { // 延迟1s打开协查通报,等待发送广播和响应广播完成
						public void run() {
							try {
								Thread.sleep(500);
								startActivity(checkNoteIntent);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}).start();

		} else if (data instanceof ContinuePlay) {
			String str = ((ContinuePlay) data).getContinuePlay();
			if ("local".equals(str)) {
				// 当前为流媒体则无响应
				if (ManagerUtil.isThisActivityTop(this,
						Constant.VIDEO_SHOW_ACTIVITY_NAME)) {
					return;
				}
				// Intent intent = new Intent();
				// intent.setAction(Constant.STOPPLAY_ACTION);
				// sendBroadcast(intent);
				// try {
				// Thread.sleep(3000);
				// } catch (InterruptedException e) {
				// e.printStackTrace();
				// }
				String jsonStr = PreferenceUtil.getSharePreference(
						UdpService.this, Constant.CURRENT_VIDEO_PERCENT, "");
				if (!TextUtils.isEmpty(jsonStr)) { // 读取内容相当于又接收了一遍json指令，下面作解析操作
					parseProcessor = ParseProcessor
							.getInstance(UdpService.this);
					parseProcessor.registerParseListener(UdpService.this);
					parseProcessor.parseData(jsonStr);
					Log.e("jsonStr", jsonStr);
					new Thread(new Client("ContinuePlay:true==>本地缓存为："
							+ jsonStr));
				} else {
					new Thread(new Client("ContinuePlay:false==>本地没有缓存记录！"));
				}
			} else {
				Intent intent = new Intent();
				intent.setAction(Constant.CONTINUE_PLAY_ACTION);
				sendBroadcast(intent);
			}
		} else if (data instanceof DailyLifeList) { // 一日生活制度
			if (ManagerUtil.isThisActivityTop(this,
					Constant.VCR_SHOW_ACTIVITY_NAME)) { // 如果当前Activity为VideoShow，则先结束它
				Intent intent = new Intent();
				intent.setAction(Constant.STOPPLAY_VCR_ACTION);
				sendBroadcast(intent);
			} else if (!ManagerUtil.isThisActivityTop(this,
					Constant.INDEX_ACTIVITY_NAME)) {// 如果当前Activity不在主界面，则退回到主界面
				DianJiaoApplication.getInstance().exit();
				Log.d("jiayy", "startIndex in DailyLifeList in service");
				Intent intent = new Intent(this, IndexActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
			dailyLifeList = ((DailyLifeList) data).getDailyLifeList();
			Log.i("DailyLife", "daylife=" + dailyLifeList);
			if (dailyLifeList != null && !dailyLifeList.isEmpty()) {
				int sec = secondsTo60() * 1000;
				if (!mCheckDailyLifeHandler
						.hasMessages(MESSAGE_CHECK_DAILY_LIFE_VALUE)) {
					mCheckDailyLifeHandler.sendEmptyMessageDelayed(
							MESSAGE_CHECK_DAILY_LIFE_VALUE, sec);
				}
			}
			Log.i("DailyLifeList", "重启总是执行？");
			new Thread(new Client("dailyLife:true")).start();
			palayStopDailyLife();
		} else if (data instanceof TimingLivingList) {
			Log.i("TTTT", "TimingLivingList.......1");
			timingLivingList.clear();
			Constant.VCR_END_TIME = "00:00";
			timingLivingList = ((TimingLivingList) data).getTimingLivingList();
			if (timingLivingList != null && !timingLivingList.isEmpty()) {
				int sec = secondsTo60() * 1000;
				if (!mCheckDailyLifeHandler
						.hasMessages(MESSAGE_CHECK_TIMING_LIVING_VALUE)) {
					Log.i("TTTT", "接收到命令....2");
					mCheckDailyLifeHandler.sendEmptyMessageDelayed(
							MESSAGE_CHECK_TIMING_LIVING_VALUE, sec);

				}
			}
			new Thread(new Client("alarmVCR:true")).start();
			startTimingLiving();
		} else if (data instanceof EmergNote) {
			if (ManagerUtil.isThisActivityTop(this,
					Constant.VCR_SHOW_ACTIVITY_NAME)) {
				Intent intent = new Intent();
				intent.setAction(Constant.STOPPLAY_VCR_ACTION);
				sendBroadcast(intent);
			} else {
				DianJiaoApplication.getInstance().exit();
			}
			String emergNoteServerIp = ((EmergNote) data).getServerIp();
			String emergNotePicName = ((EmergNote) data).getPicName();
			String emergNoteContent = ((EmergNote) data).getNoteContent();
			String emergNoteShowTime = ((EmergNote) data).getShowTime();
			String port = ((EmergNote) data).getPort();
			Intent emergyIntent = new Intent(this, EmergyNoteActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString(Constant.EMERG_NOTE_SERVER_IP, emergNoteServerIp);
			bundle.putString(Constant.EMERG_NOTE_PIC_NAME, emergNotePicName);
			bundle.putString(Constant.EMERG_NOTE_CONTENT, emergNoteContent);
			bundle.putString(Constant.EMERG_NOTE_SHOW_TIME, emergNoteShowTime);
			bundle.putString(Constant.EMERG_NOTE_PORT, port);
			emergyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			emergyIntent.putExtras(bundle);
			startActivity(emergyIntent);
		} else if (data instanceof Pause) {
			String pause = ((Pause) data).getPause();
			Intent intent = new Intent();
			intent.setAction("com.example.iimp_znxj_new2014.pause");
			sendBroadcast(intent);
		} else if (data instanceof PlayPlanList) { // 每日计划
			if (ManagerUtil.isThisActivityTop(this,
					Constant.VCR_SHOW_ACTIVITY_NAME)) {
				Intent intent = new Intent();
				intent.setAction(Constant.STOPPLAY_VCR_ACTION);
				sendBroadcast(intent);
			} else if (!ManagerUtil.isThisActivityTop(this,
					Constant.INDEX_ACTIVITY_NAME)) {
				DianJiaoApplication.getInstance().exit();
				Log.d("jiayy", "startIndex in playplan in service");
				Intent intent = new Intent(this, IndexActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
			playPlanList = ((PlayPlanList) data).getPlayPlanList();
			Log.i("TTSS", "PlayPlanList1:" + playPlanList);
			if (playPlanList != null && !playPlanList.isEmpty()) {
				Log.i("TTSS", "PlayPlanList2");
				playPlanList = ((PlayPlanList) data).getPlayPlanList();

				callPlayPlanAlarm(); // 接收到每日播放的计划都要清空以前的播放计划
				dao.deleteTable(PLAY_PLAN_TABLE); // 接收到每日播放的计划都清空以前的数据
				dao.createTable(PLAY_PLAN_TABLE);

				startPlayPlanAlarmManager();
			}
			new Thread(new Client("playPlan:true")).start();
		} else if (data instanceof DownLoadPlanList) {
			downLoadList = ((DownLoadPlanList) data).getDownLoadList();
			startDownLoadAlarmManager();
			new Thread(new Client("downLoadPlan:true")).start();
		} else if (data instanceof TimingPlanList) {
			timingPlanList.clear();
			Constant.VIDEO_END_TIME = "00:00";
			timingPlanList = ((TimingPlanList) data).getTimingPlanList();
			isSend = true;
			if (timingPlanList != null && !timingPlanList.isEmpty()) {
				int sec = secondsTo60() * 1000;
				if (!mCheckDailyLifeHandler
						.hasMessages(MESSAGE_CHECK_TIMING_PLAN_VALUE)) {
					Log.i("TimingPlanList", "接收到信息，首次发送");
					mCheckDailyLifeHandler.sendEmptyMessageDelayed(
							MESSAGE_CHECK_TIMING_PLAN_VALUE, sec);
					timingPlanList.clear(); // 当重新发送时清空时间集合
				}

				timingPlanList = ((TimingPlanList) data).getTimingPlanList();
				callPlayPlanAlarm(); // 接收到每日播放的计划都要清空以前的播放计划
				dao.deleteTable(PLAY_PLAN_TABLE); // 接收到每日播放的计划都清空以前的数据
				dao.createTable(PLAY_PLAN_TABLE);
			}
			Log.i("FLAG",
					"TimingPlan_Flag="
							+ PreferenceUtil.getSharePreference(this,
									Constant.ADJUST_SERVER_FLAG));
			new Thread(new Client("timingPlan:true")).start();
			Log.i("test1", "判断时间");
			// 计划一发送被解析完 或者 服务第一次启动获得定时时间时 立刻进行一次判断
			if (!Constant.isLiveTime) {
				startTimingPlan();
			}

		} else if (data instanceof PlaySubTitle) {
			String subTitleStr = ((PlaySubTitle) data).getSubTitleStr();
			String subShowTime = ((PlaySubTitle) data).getShowTime();
			Log.i("Send", "" + subTitleStr);
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString(Constant.SUB_TITLE, subTitleStr);
			bundle.putString(Constant.SUB_TIME, subShowTime);
			intent.setAction(Constant.SUBTITLE_ACTION);

			intent.putExtras(bundle);
			sendBroadcast(intent);
		} else if (data instanceof StopPlaySubTitle) {
			String stopPlayTitle = ((StopPlaySubTitle) data)
					.getStopPlaySubTitle();
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString(Constant.STOP_PLAY_SUB_TITLE, stopPlayTitle);
			intent.setAction("com.example.iimp_znxj_new2014.stopsubtitle");
			intent.putExtras(bundle);
			sendBroadcast(intent);
		} else if (data instanceof SaveScreen) {
			// String serverIp = ((SaveScreen) data).getServerIp();
		} else if (data instanceof SearchDevices) {
			serverPort = ((SearchDevices) data).getServerPort();
			PreferenceUtil.putSharePreference(UdpService.this,
					Constant.SERVER_PORT, serverPort);
		} else if (data instanceof UpdateVersion) { // 静默升级

			String serverIp = ((UpdateVersion) data).getServerIp();
			String fileName = ((UpdateVersion) data).getFileName();
			String port = ((UpdateVersion) data).getPort();
			String appUrl = "http://" + serverIp + ":" + port + "/" + fileName;
			Log.i("UpdateVersion", "appUrl=" + appUrl);
			JingMoOrder.jingMoInstall(this, appUrl, fileName);
		} else if (data instanceof DownloadFile) {
			String serverIp = ((DownloadFile) data).getServerIp();
			String fileName = encode(((DownloadFile) data).getFileName().trim());
			String port = ((DownloadFile) data).getPort();

			if (isHasFile(fileName)) {
				new Thread(new Client("download:exists")).start();
			} else {
				String furl = "http://" + serverIp + ":" + port + "/"
						+ fileName;// java.net.URLEncoder.encode(encode(fileName),"UTF-8");
				Log.i("Send", "Furl=" + furl);
				PreferenceUtil.putSharePreference(UdpService.this,
						Constant.DOWNLOAD_FLAG, "true");
				JingMoOrder.fileDownload(this, furl, fileName);
			}
		} else if (data instanceof GetFolderList) {
			File sfile = new File("/mnt/extsd/Download/");
			JingMoOrder.search(sfile);
			// new Thread(new Client("getfilelist:true")).start();
		} else if (data instanceof DeleteFile) {
			String fileName = ((DeleteFile) data).getFilename();
			JingMoOrder.deleteFile(fileName);
			// new Thread(new Client("delfile:true")).start();
		} else if (data instanceof ModifyClientIp) {
			String clientIp = ((ModifyClientIp) data).getClientIp();
			String subnetmask = ((ModifyClientIp) data).getSubnetmask();
			PreferenceUtil.putSharePreference(UdpService.this,
					Constant.MODIFY_CLIENT_IP, clientIp);
			PreferenceUtil.putSharePreference(UdpService.this,
					Constant.MODIFY_CLIENT_SUBNETMASK, subnetmask);
			JingMoOrder.modifyClientIp(clientIp, subnetmask);
		} else if (data instanceof OnLine) {
			Log.i("TS", "online");
			String port = ((OnLine) data).getPort();
			serverPort = port;
			PreferenceUtil.putSharePreference(UdpService.this,
					Constant.SERVER_PORT, port);
			// String result = Constant.ORDER_ACTION + "online"
			// + Constant.ORDER_RESULT_OK + Constant.ORDER_RESULT_VERSION; /
			// ",version:v"
			String result = Constant.ORDER_ACTION + "online"
					+ Constant.ORDER_RESULT_OK + ",nowtime:"
					+ DateUtils.getDatetimeToString(System.currentTimeMillis())
					+ ",version:v"
					+ ManagerUtil.getVersionName(UdpService.this);
			new Thread(new Client(result)).start();

		} else if (data instanceof GetRealTimeContent) {
			// 实时查看指令
			if (ManagerUtil.isThisActivityTop(this,
					Constant.VIDEO_SHOW_ACTIVITY_NAME)) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				intent.setAction(Constant.MESSAGE_SETREALTIMECONTENT);
				intent.putExtras(bundle);
				sendBroadcast(intent);

			} else if (ManagerUtil.isThisActivityTop(this,
					Constant.TVACTIVITY_NAME)) {
				// String result= Constant.ORDER_ACTION + "getRealTimeContent" +
				// Constant.ORDER_TYPE+"0"+
				// Constant.ORDER_FILEPATH+
				// "null"+Constant.ORDER_STATE+"play"+Constant.ORDER_PROGREEBAR+"null"+Constant.ORDER_CHANNEL+"null"
				// ;
				//
				// new Thread(new Client(result)).start();
				Intent intent = new Intent();
				Bundle bundle = new Bundle();

				intent.setAction("com.example.iimp_znxj_new2014.setRealTimeContentVCR");
				intent.putExtras(bundle);
				sendBroadcast(intent);

			} else {

				String result = Constant.ORDER_ACTION + "getRealTimeContent"
						+ Constant.ORDER_TYPE + "-1" + Constant.ORDER_FILEPATH
						+ "null" + Constant.ORDER_STATE + ""
						+ Constant.ORDER_PROGREEBAR + "null"
						+ Constant.ORDER_CHANNEL + "null";
				new Thread(new Client1(result)).start();
			}

		} else if (data instanceof OnOff) {
			onTimeServerStr = ((OnOff) data).getOnTime();
			offTimeServerStr = ((OnOff) data).getOffTime();
			PreferenceUtil.putSharePreference(UdpService.this,
					Constant.OFF_ON_TIME_ON_TIME, onTimeServerStr);
			PreferenceUtil.putSharePreference(UdpService.this,
					Constant.OFF_ON_TIME_OFF_TIME, offTimeServerStr);
			int sec = secondsTo60() * 1000;
			Log.d("jiayy", "second = " + sec);
			if (!mCheckDailyLifeHandler.hasMessages(MESSAGE_CHECK_OFF_VALUE)) {
				mCheckDailyLifeHandler.sendEmptyMessageDelayed(
						MESSAGE_CHECK_OFF_VALUE, sec);
				new Thread(new Client("onoff:true")).start(); // 发回数据表示成功发送
			}
		} else if (data instanceof StopPlay) {
			Log.i(TAG, "StopPlay");
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
				mMediaPlayer.reset();
				new Thread(new Client("stopPlay:true")).start();
			}

			String stopPlay = ((StopPlay) data).getStopPlay();
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString(Constant.STOP_PLAY, stopPlay);
			intent.setAction("com.example.iimp_znxj_new2014.stopPlay");
			intent.putExtras(bundle);
			sendBroadcast(intent);
			new Thread(new Client("stopPlay:true")).start();
		} else if (data instanceof Reboot) {
			String reboot = ((Reboot) data).getReboot();
			JingMoOrder.reboot();
		} else if (data instanceof GetCurrentState) {
			if (ManagerUtil.isThisActivityTop(this,
					Constant.VCR_SHOW_ACTIVITY_NAME)) { // VCR
				new Thread(new Client("getCurrentState:playVCR")).start();
			} else if (ManagerUtil.isThisActivityTop(this,
					Constant.LOCAL_VIDEO_ACTIVITY_BNAME)) { // 流媒体、播本地
				new Thread(new Client("getCurrentState:playLocalVideo"))
						.start();
			} else if (ManagerUtil.isThisActivityTop(this,
					Constant.VIDEO_SHOW_ACTIVITY_NAME)) { // 流媒体
				new Thread(new Client("getCurrentState:playLocalVideo"))
						.start();
			} else if (ManagerUtil.isThisActivityTop(this,
					Constant.AUDIO_ACTIVITY_NAME) || mMediaPlayer.isPlaying()) { // 音乐
				new Thread(new Client("getCurrentState:playLocalMusic"))
						.start();
			} else if (ManagerUtil.isThisActivityTop(this,
					Constant.INDEX_ACTIVITY_NAME)) { // 首页
				new Thread(new Client("getCurrentState:standby")).start();
			} else if (ManagerUtil.isThisActivityTop(this,
					Constant.CHECK_NOTE_ACTIVITY_NAME)) { // 协查通报
				new Thread(new Client("getCurrentState:checknote")).start();
			} else if (ManagerUtil.isThisActivityTop(this,
					Constant.SELFCONSUME_BUYACTIVITY_NAME)) {
				new Thread(new Client("getCurrentState:selfConsume")).start();
			} else {
				new Thread(new Client("getCurrentState:other")).start();
			}
			// getCurrentState:playVCR/playLocalVideo/playLocalMusic/standby/checknote/other
			// => 直播、流媒体、音乐、首页、协查通报、其他
		} else if (data instanceof Volume) {
			String getValue = ((Volume) data).getMovement();
			if (getValue.equals("up") && VOLUME_VALUE < 15) {
				VOLUME_VALUE++;
			} else if (getValue.equals("down") && VOLUME_VALUE > 0) {
				VOLUME_VALUE--;
			}
			Log.i(TAG, "UdpService_当前音量值：" + VOLUME_VALUE);
			ManagerUtil.SoundCtrl(UdpService.this, VOLUME_VALUE);
			Log.v("7.30", "-----VO0LUME_VALUE had changed:" + VOLUME_VALUE);
			new Thread(new Client("VOLUME_VALUE:" + VOLUME_VALUE)).start();
		} else if (data instanceof GetProgressBar) {
			Log.i("TTSS", "进度%%");
			String getPercent = ((GetProgressBar) data).getPercent();
			Intent intent = new Intent();
			Bundle bundle = new Bundle();

			if (((GetProgressBar) data).getPercent() == null) {
				intent.setAction("com.example.iimp_znxj_new2014.getcurpercent");
				PreferenceUtil.putSharePreference(UdpService.this,
						Constant.CHANGE_BACK_PORT, "true");
			} else {
				intent.setAction("com.example.iimp_znxj_new2014.getprogress");
			}
			intent.putExtras(bundle);
			sendBroadcast(intent);
		} else if (data instanceof JumpPercentage) {
			String getPercent = ((JumpPercentage) data).getPercent();
			Log.i("JumpPercentage", "JumpPercentage:" + getPercent);
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString(Constant.PERCENT_VALUE, getPercent);
			intent.setAction("com.example.iimp_znxj_new2014.getpercent");
			intent.putExtras(bundle);
			sendBroadcast(intent);
		} else if (data instanceof ConfigureInfo) {
			String serverIp = ((ConfigureInfo) data).getServerIp();
			String port = ((ConfigureInfo) data).getPort();
			String cellNumber = ((ConfigureInfo) data).getCellNumber();
			String duration = ((ConfigureInfo) data).getDuration();
			Log.i(TAG, "ConfigureInfo,serverIp=" + serverIp + "," + port + ","
					+ cellNumber);
			PreferenceUtil.putSharePreference(UdpService.this,
					Constant.CONFIGURE_INFO_SERVERIP, serverIp);
			PreferenceUtil.putSharePreference(UdpService.this,
					Constant.CONFIGURE_INFO_PORT, port);
			PreferenceUtil.putSharePreference(UdpService.this,
					Constant.CONFIGURE_INFO_CELLNUMBER, cellNumber);
			PreferenceUtil.putSharePreference(UdpService.this,
					Constant.CONFIGURE_INFO_DURATION, duration);
			new Thread(new Client("configureInfo:true")).start();
		} else if (data instanceof SetEmpty) {
			Log.i(TAG, "接收到清空购物数据的命令");
			gDao.setEmpty();
			new Thread(new Client("setEmpty:true")).start();
		} else if (data instanceof RollCall) {
			Log.i("TTS", "UdpService");
			int duration;
			String port = ((RollCall) data).getResponsePort();

			String getAciton = ((RollCall) data).getDowhat();
			Log.i("TEST", "发送点名结束广播,getAciton=" + getAciton);
			if (getAciton.equals("end")) {
				Log.i("TEST", "发送点名结束广播");
				Intent intent = new Intent();
				intent.setAction("com.example.name.end");
				sendBroadcast(intent);
			} else {
				PreferenceUtil.putSharePreference(UdpService.this,
						Constant.SERVER_PORT, port);
				if (PreferenceUtil.getSharePreference(UdpService.this,
						Constant.CONFIGURE_INFO_DURATION) == null
						|| PreferenceUtil.getSharePreference(UdpService.this,
								Constant.CONFIGURE_INFO_DURATION).equals("")) {
					duration = 1 * 60;
				} else {
					duration = Integer.parseInt(PreferenceUtil
							.getSharePreference(UdpService.this,
									Constant.CONFIGURE_INFO_DURATION)) * 60;
				}
				String rollCallSecondssss = null;
				if (ManagerUtil.isThisActivityTop(UdpService.this,
						Constant.ROLLCALL_ACTIVITY_NAME)) {
					rollCallSecondssss = "{'rollcallseconds':'" + duration
							+ "','rollcall':'false'}";
				} else {
					DianJiaoApplication.getInstance().finishIndexActivities();
					Intent intent = new Intent(UdpService.this,
							RollCallActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
					rollCallSecondssss = "{'rollcallseconds':'" + duration
							+ "','rollcall':'true'}";
				}
				rollCallSecondssss = rollCallSecondssss.replace("\'", "\"");
				new Thread(new Client(rollCallSecondssss)).start();
			}

		} else if (data instanceof ImageSlide) {

			if (!ManagerUtil.isThisActivityTop(this,
					Constant.INDEX_ACTIVITY_NAME)) {
				DianJiaoApplication.getInstance().exit();
				Intent intent = new Intent(this, IndexActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
			// 先发个结束广播结束当前播放的任务
			Intent intentStop = new Intent();
			intentStop.setAction("com.example.iimp_znxj_new2014.stopPlay");
			sendBroadcast(intentStop);
			String serverIp = ((ImageSlide) data).getServerIp();
			String port = ((ImageSlide) data).getPort();
			String showTime = ((ImageSlide) data).getShowTime();
			String picName = ((ImageSlide) data).getPicName();
			String interval = ((ImageSlide) data).getInterval();

			Log.i("TTTS", "1111....serverIp=" + serverIp + "," + port + ","
					+ showTime + "," + picName + "," + interval);
			final Intent intent = new Intent(UdpService.this,
					ImagePagerActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString(Constant.SLIDE_IP, serverIp);
			bundle.putString(Constant.SLIDE_PORT, port);
			bundle.putString(Constant.SLIDE_SHOWTIME, showTime);
			bundle.putString(Constant.SLIDE_NAME, picName);
			bundle.putString(Constant.SLIDE_INTERVAL, interval);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtras(bundle);
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					startActivity(intent);
				}
			}).start();

			new Thread(new Client("imageSlide:true")).start();
		} else if (data instanceof Delete) {
			Constant.VCR_END_TIME = "00:00";
			Constant.VIDEO_END_TIME = "00:00";
			String getType = ((Delete) data).getDelete();
			String filePath = null;
			if (getType.equals("timingPlan")) {
				/*
				 * //定时计划分钟集合清空 最好直接得到要删的计划的分钟 tmPanPalyTimes.clear();
				 * Log.i(TAG,"删除定时流媒体文件");
				 */
				filePath = getFilesDir() + "/"
						+ Constant.TIMING_PLAN_FOLDER_NAME + "/"
						+ Constant.TIMING_PLAN_FILE_NAME;
				timingPlanList.clear();
			} else if (getType.equals("timingLivingPlan")) {
				timingLivingList.clear();
				filePath = getFilesDir() + "/" + Constant.TIMING_LIVING_FOLDER
						+ "/" + Constant.TIMING_LIVING_FILE_NAME;
				Log.i(TAG, "删除定时直播文件");
			} else if (getType.equals("fullSubTitlePlan")) {
				fullSubTitlePlanList.clear();
				filePath = getFilesDir() + "/"
						+ Constant.FULLSUBTITLE_PLAN_FOLDER_NAME + "/"
						+ Constant.FULLSUBTITLE_PLAN_FILE_NAME;
			}
			File file = new File(filePath);
			FileUtils.deleteFolder(UdpService.this, file);
			new Thread(new Client("delete:true")).start();
		} else if (data instanceof RollSubTitle) {

			if (!ManagerUtil.isThisActivityTop(this,
					Constant.INDEX_ACTIVITY_NAME)) {
				DianJiaoApplication.getInstance().exit();
				Intent intent = new Intent(this, IndexActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
			// 先发个结束广播结束当前播放的任务
			Intent intentStop = new Intent();
			intentStop.setAction("com.example.iimp_znxj_new2014.stopPlay");
			sendBroadcast(intentStop);

			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			String serverIp = ((RollSubTitle) data).getServerIp();
			String port = ((RollSubTitle) data).getPort();
			String fileName = ((RollSubTitle) data).getFileName();
			String rollSpeed = ((RollSubTitle) data).getRollSpeed();
			String showTime = ((RollSubTitle) data).getShowTime();
			String volume = ((RollSubTitle) data).getVolume();
			String url = "http://" + serverIp + ":" + port + "/" + fileName;
			if (rollSpeed.equals("1")) {
				Constant.SCROLL_SPEED = 200;
			} else if (rollSpeed.equals("2")) {
				Constant.SCROLL_SPEED = 30;
			} else {
				Constant.SCROLL_SPEED = 5;
			}

			final Intent itto = new Intent(this,
					FullScrollSubtitleActivity.class); // 滚动字幕
			itto.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Bundle bundle = new Bundle();
			bundle.putString(Constant.SCROLL_URL, url);
			// bundle.putString(Constant.SCROLL_SPEED,rollSpeed);
			bundle.putString(Constant.SCROLL_SHWOTIME, showTime);
			bundle.putString("volume", volume);
			itto.putExtras(bundle);

			new Thread(new Runnable() {
				public void run() {
					try {
						Thread.sleep(1000);
						startActivity(itto);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}).start();
			new Thread(new Client("RollSubTitle:true")).start();
		} else if (data instanceof TVShow) {

			if (!ManagerUtil.isThisActivityTop(this,
					Constant.INDEX_ACTIVITY_NAME)) {
				DianJiaoApplication.getInstance().exit();
				Intent intent = new Intent(this, IndexActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
			Intent stopIn = new Intent();
			stopIn.setAction("com.example.iimp_znxj_new2014.stopPlay");
			sendBroadcast(stopIn);
			Constant.VCR_END_TIME = "00:00";
			Constant.VIDEO_END_TIME = "00:00";

			String Ip = ((TVShow) data).getIp();
			String port = ((TVShow) data).getPort();
			String channel = ((TVShow) data).getChannel();
			String user = ((TVShow) data).getUser();
			String password = ((TVShow) data).getPassword();
			String volume = ((TVShow) data).getVolume();

			String deviceChannel = "";
			if (((TVShow) data).getDeviceChannel() != null) {
				deviceChannel = ((TVShow) data).getDeviceChannel();

			}

			Intent intent = null;

			String VCRType = PreferenceUtil.getSharePreference(this,
					Constant.SHAREDPREFERENCE_VCR_TYPE, 1 + "");
			if ("1".equals(VCRType)) {
				intent = new Intent(this, TVActivity.class);
			} else if ("2".equals(VCRType)) {

				intent = new Intent(this, KeDaVCRActivity.class);
				intent.putExtra("deviceChannel", deviceChannel);
			}
			realTime = "-1";
			intent.putExtra("ip", Ip);
			intent.putExtra("port", port);
			intent.putExtra("user", user);
			intent.putExtra("password", password);
			intent.putExtra("channel", channel);
			intent.putExtra("volume", volume);
			// intent.putExtra("devicechannel", channel);
			final Intent VcrIntent = intent;

			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
					}
					startActivity(VcrIntent);
				}
			}).start();

		} else if (data instanceof OnOffPlanList) {
			shutDownFlag = false;
			onOffPlanList.getOnOffPlanList().clear();
			try {
				Thread.sleep(shutDownSleepTime + 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Log.v("8.1", "service接收到数据");
			onOffPlanList = (OnOffPlanList) data;
			if (!(onOffPlanList.size() < 0)) {
				shutDownFlag = true;
				shutDownPlans();
				new Thread(new Client("shutDownPlans:true")).start();
			}
		} else if (data instanceof FullSubTitlePlan) {
			fullSubTitlePlanList.clear();

			if (mCheckDailyLifeHandler
					.hasMessages(MESSAGE_CHECK_FULLSUBTITLE_PLAN)) {
				mCheckDailyLifeHandler
						.removeMessages(MESSAGE_CHECK_FULLSUBTITLE_PLAN);
			}

			fullSubTitlePlanList = (FullSubTitlePlan) data;

			int sec = 10 * 1000;

			if (!mCheckDailyLifeHandler
					.hasMessages(MESSAGE_CHECK_FULLSUBTITLE_PLAN)) {
				Log.i("FullSubTitlePlan", "接收到信息，首次发送");
				mCheckDailyLifeHandler.sendEmptyMessageDelayed(
						MESSAGE_CHECK_FULLSUBTITLE_PLAN, sec);
			}

			new Thread(new Client("fullSubTitlePlanList:true")).start();
		} else if (data instanceof OnDuty) {

			String beginhour = ((OnDuty) data).getBeginHour();
			String beginminute = ((OnDuty) data).getBeginMinute();
			String intervalStr = ((OnDuty) data).getInterval();
			String cyclesStr = ((OnDuty) data).getCycles();

			// 根据获取的时间，计算提前5分钟的时间节点
			if (Integer.parseInt(beginminute) < 5
					&& Integer.parseInt(beginminute) > 0) {
				if (beginhour.equals("00")) {
					beginhour = 23 + "";
					beginminute = (60 - Integer.parseInt(beginminute)) + "";
				} else {
					beginhour = Integer.parseInt(beginhour) - 1 + "";
					beginminute = (60 - Integer.parseInt(beginminute)) + "";
				}
			} else if (Integer.parseInt(beginminute) == 0) {
				if (beginhour.equals("00")) {
					beginhour = 23 + "";
					beginminute = 55 + "";
				} else {
					beginhour = Integer.parseInt(beginhour) - 1 + "";
					beginminute = 55 + "";
				}
			} else {
				beginminute = (Integer.parseInt(beginminute) - 5) + "";
			}

			Log.i("TEST", "Hour=" + beginhour + ",Minute=" + beginminute);

			String content = "beginhour=" + beginhour + "\n" + "beginminute="
					+ beginminute + "\n" + "interval=" + intervalStr + "\n"
					+ "cycles=" + cyclesStr;
			// if(helper.hasSD()){ //接收到新的交接班命令：(1)先取消上次的定时(2)新命令写入文件(3)重置判断标志位
			// AlarmUtil.cancelAlarm(UdpService.this);
			// helper.writeSDFile(content, "onduty.properties");
			// dao.updateCount("countNum", 0+"");

			AlarmUtil.startAlarm(UdpService.this,
					Integer.parseInt(beginhour), // 开启定时
					Integer.parseInt(beginminute),
					Integer.parseInt(intervalStr));

			Log.i("TS", "写入交接班时间");

			new Thread(new Client("onduty:true")).start();

		} else if (data instanceof DutyTime) {
			String durationTime = ((DutyTime) data).getTimeArray();
			Log.i("TTSS", "durationTime=" + durationTime);

			AlarmUtil.cancelSetAlarm(UdpService.this);
			AlarmUtil.clearBroadCastAlarm(UdpService.this);
			AlarmUtil.tag = 0;
			AlarmUtil.startAlarmClock(durationTime, UdpService.this);
			PreferenceUtil.putSharePreference(UdpService.this,
					Constant.DUTYTIME_TIMEARRAY, durationTime);
			new Thread(new Client("dutytime:true")).start();

		} else if (data instanceof ServerIpPort) {

			String serverIpStr = ((ServerIpPort) data).getServerIp();
			Constant.SERVER_URL = "http://" + serverIpStr + "/";
			Log.i("TTSS", "后台IP+PORT：" + serverIpStr);

			String[] split = serverIpStr.split(":");
			if (split.length == 2) {
				PreferenceUtil.putSharePreference(UdpService.this,
						Constant.CONFIGURE_INFO_SERVERIP, split[0]);
				PreferenceUtil.putSharePreference(UdpService.this,
						Constant.CONFIGURE_INFO_PORT, split[1]);
				new Thread(new Client("autoGetNum:true")).start();
			}
			JingMoOrder.getRoomIDAndName(UdpService.this);
		} else if (data instanceof SetVcrType) {
			new Thread(new Client("setVCRType:true")).start();
		}
	}

	/*
	 * 定时开关机
	 */
	private String nowTime;
	private String planOffTime;
	private String planOnTIme;
	private boolean shutDownFlag = true;
	Date dateOff;
	Date dateOn;
	long mOffsetSecondTimes;
	long shutDownSleepTime = 3000;
	Date nowDate;
	SimpleDateFormat sdf;
	long offTime;
	long onTime;

	public void shutDownPlans() {
		new Thread() {
			@Override
			public void run() {
				while (shutDownFlag) {

					// Log.v("8.2", "nowTime" + nowTime);
					for (int i = 0; i < onOffPlanList.size(); i++) {
						String planWeek = onOffPlanList.get(i).getWeekDay();

						Calendar calendar = Calendar.getInstance();
						// 得到本日星期
						String nowWeek = String.valueOf(calendar
								.get(Calendar.DAY_OF_WEEK));
						if ("0".equals(planWeek)) {// 每天

						} else if ("".equals(planWeek)) {// 指定当天
							String getWeek = getWeek(onOffPlanList.get(i)
									.getOffTime());// 获取当前日期
							if (!(nowWeek.equals(getWeek))) {
								continue;
							}
						} else {// 每周那一天
							if (!(nowWeek.equals(planWeek))) {
								continue;
							}
						}
						planOffTime = onOffPlanList.get(i).getOffTime();
						planOnTIme = onOffPlanList.get(i).getOnTime();

						if (planOffTime.length() > 10) {
							nowDate = new Date();
							sdf = new SimpleDateFormat("HH:mm");
							nowTime = sdf.format(nowDate);
							planOffTime = planOffTime.split(" ")[1];
							planOnTIme = planOnTIme.split(" ")[1];
						} else {
							nowDate = new Date();
							sdf = new SimpleDateFormat("HH:mm");
							nowTime = sdf.format(nowDate);

						}
						Log.e("nowTime", "" + nowTime);
						Log.e("planOffTime", "" + planOffTime);
						Log.e("planOnTIme", "" + planOnTIme);
						if (nowTime.equalsIgnoreCase(planOffTime)) {
							Log.v("8.2", "planOffTime=" + planOffTime);
							shutDownFlag = false;

							Log.v("8.2", "planOnTIme=" + planOnTIme);
							try {
								dateOff = sdf.parse(planOffTime);
								dateOn = sdf.parse(planOnTIme);
								offTime = dateOff.getTime();
								onTime = dateOn.getTime();
								Log.v("8.2", "offTime=" + offTime + "onTime"
										+ onTime);
								mOffsetSecondTimes = (onTime - offTime) / 1000 + 5;
								if (mOffsetSecondTimes < 0) {
									mOffsetSecondTimes = mOffsetSecondTimes
											+ DAY_PER_SECOND;
								}
								Log.v("8.2", mOffsetSecondTimes + "s后重启");
								// 重启的秒数
								mFlag = true;
								writeOnTimeToMC(1, mOffsetSecondTimes);
								break;
							} catch (ParseException e1) {
								e1.printStackTrace();
							}
						}
					}
					try {
						Thread.sleep(shutDownSleepTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				super.run();
			}
		}.start();
	}

	/*
	 * 新来数据后务必取消掉上次的定时设置
	 */
	private void callPlayPlanAlarm() {
		// dao = new DownPlanDao(getApplicationContext());
		int requestCode;
		Cursor cs = dao.queryTable(PLAY_PLAN_TABLE);
		while (cs.moveToNext()) {
			requestCode = cs.getInt(0);
			// Log.i("playPlan","取消闹钟，id="+requestCode);
			AlarmManagerUtil.cancelPlayPlan(getApplicationContext(),
					requestCode);
		}
		cs.close();
	}

	public class vidclick implements OnClickListener {
		@Override
		public void onClick(View v) {

		}
	}

	// 文件名转码
	public String encode(String value) {
		try {
			return URLEncoder.encode(value, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Log.i("Error", "Error:" + e.getMessage());
			e.printStackTrace();
		}
		return value;
	}

	// 文件名解码
	public String decode(String value, String code) {
		try {
			return URLDecoder.decode(value, code);
		} catch (UnsupportedEncodingException e) {
			Log.i("Error", "Error:" + e.getMessage());
			e.printStackTrace();
		}
		return value;
	}

	// 判断SD卡上文件是否存在
	public boolean isHasFile(String fileName) {
		File file = new File("/mnt/extsd/Download/" + fileName);
		if (file == null || !file.exists() || file.isDirectory())
			return false;
		return true;
	}

	private String getWeek(String pTime) {

		String Week = "";

		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Calendar c = Calendar.getInstance();
		try {

			c.setTime(format.parse(pTime));

		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 1) {
			Week += "1";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 2) {
			Week += "2";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 3) {
			Week += "3";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 4) {
			Week += "4";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 5) {
			Week += "5";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 6) {
			Week += "6";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 7) {
			Week += "7";
		}

		return Week;
	}

}
