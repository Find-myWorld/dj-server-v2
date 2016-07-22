package com.example.iimp_znxj_new2014.activity;

import java.util.Locale;

/*
 * @description 旧版直播
 */

import java.util.Random;

import org.MediaPlayer.PlayM4.Player;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.test.PerformanceTestCase.Intermediates;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iimp_znxj_new2014.DianJiaoApplication;
import com.example.iimp_znxj_new2014.R;
import com.example.iimp_znxj_new2014.util.Constant;
import com.example.iimp_znxj_new2014.util.JingMoOrder;
import com.example.iimp_znxj_new2014.util.JingMoOrder.Client;
import com.example.iimp_znxj_new2014.util.ManagerUtil;
import com.hikvision.netsdk.ExceptionCallBack;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_CLIENTINFO;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.hikvision.netsdk.NET_DVR_IPPARACFG_V40;
import com.hikvision.netsdk.RealPlayCallBack;

public class TVActivity extends BaseActivity implements Callback ,OnInitListener{

	private TextView mScrollText;
	private Player m_oPlayerSDK = null;
	private HCNetSDK m_oHCNetSDK = null;
	private static final int MESSAGE_SHOW_TIME_START = 1;
	private static final int MESSAGE_SHOW_TIME_REACHED = 2;
	private static SurfaceView m_osurfaceView = null;

	private NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30 = null;

	private int m_iLogID = -1; // return by NET_DVR_Login_v30
	private int m_iPlayID = -1; // return by NET_DVR_RealPlay_V30
	private int m_iPlaybackID = -1; // return by NET_DVR_PlayBackByTime
	// private byte m_byGetFlag = 1; // 1-get net cfg, 0-set net cfg
	private int m_iPort = -1; // play port
	// private NET_DVR_NETCFG_V30 NetCfg = new NET_DVR_NETCFG_V30(); //netcfg
	// struct

	private final String TAG = "TVActivity";

	/*  tcp
	private Socket socket;
	private SocketChannel socketChannel;
	private Selector selector;
	*/

	private Handler mHandler;

	// final static int ConnentOver = 1;
	// final static int SendOver = 2;
	final static int DataComeIn = 3;
	final static int TimeOver = 4;
	final static int Pause = 5;
	final static int Resume = 6;

	// private String remoteip;
	byte[] udp_message = new byte[1024];

	private boolean timecount; 
	private int channel = 33;

	private String strIP = "192.168.0.144";
	private int nPort = 8000;
	private String strUser = "admin";
	private String strPsd = "12345";
	private TVBroadcastReceiver broadcastReceiver;
	private boolean isKeyAction = true; // 清零卡住计数的flag
	private String mSubTitleShowTime;
	private TextToSpeech tts;
	private String volume;

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_tv);
		// SDPATH = Environment.getExternalStorageDirectory().getPath() +
		// "//TVInfo.cfg";
		tts = new TextToSpeech(TVActivity.this, TVActivity.this);
		tts.setSpeechRate(1);
		
		
		mScrollText = (TextView) findViewById(R.id.scroll_text_vcr_tv);

		if (!initeSdk()) {
			this.finishAndGotoIndex();
			return;
		}
		if (!initeActivity()) {
			this.finishAndGotoIndex();
			return;
		}

		mHandler = new Handler() {
			@SuppressLint("HandlerLeak")
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case Pause:
					DoStop();
					break;
				case Resume:
					Doplay(channel);
					break;
				case MESSAGE_SHOW_TIME_REACHED:
					mScrollText.setVisibility(View.INVISIBLE);
					break;
				case MESSAGE_SHOW_TIME_START:
					int showTime = Integer.parseInt(mSubTitleShowTime) * 60 * 1000;
					sendEmptyMessageDelayed(MESSAGE_SHOW_TIME_REACHED, showTime);
					Log.i(TAG, "showTime:" + showTime);
					break;
				}
			}
		};

		strIP = getIntent().getStringExtra("ip"); // ipAddress
		nPort = Integer.parseInt(getIntent().getStringExtra("port"));// port
		strUser = getIntent().getStringExtra("user"); // UserName
		strPsd = getIntent().getStringExtra("password"); // PassWord
		
		channel = Integer.parseInt(getIntent().getStringExtra("channel"));
		// 判断是否为空 不为空得到volume的值
		if (getIntent().getStringExtra("volume") != null) {
			volume = getIntent().getStringExtra("volume");
		}
		JingMoOrder.postErrorLog3(TVActivity.this, "海康直播参数:admin="+strUser+",password="+strPsd+",ip="+strIP+",port="+nPort+",channel="+channel, Constant.NORMAL_MSG);
		Log.v("7.20", "，strIP=" + strIP + "，nPort=" + nPort + "，strUser=" + strUser + "，strPsd=" + strPsd+",channel="+channel);

		DologinIn();
		Doplay(channel);

		if (volume != null) {
			try {
				ManagerUtil.SoundCtrl(TVActivity.this,Integer.parseInt(volume));
			} catch (Exception e) {
			}
			
		}
		
		broadcastReceiver = new TVBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.example.iimp_znxj_new2014.stopPlay");
		intentFilter.addAction(Constant.SUBTITLE_ACTION);
		intentFilter.addAction(Constant.STOP_SUBTITLE_ACTION);
		intentFilter.addAction("com.example.iimp_znxj_new2014.setRealTimeContentVCR");
		registerReceiver(broadcastReceiver, intentFilter);

		//考虑到有的地方嫌吵，会吓到心脏病的人
//		Message message = handler.obtainMessage(1);
//		handler.sendMessageDelayed(message, 0);
		// 清零计数
		new Thread() {
			public void run() {
				while (isKeyAction) {
					try {
						Thread.sleep(20000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					count = 0;
				}
			};
		}.start();
	}

	String actionStr = "";

	

	Handler handler = new Handler() {

		@SuppressWarnings("deprecation")
		public void handleMessage(Message msg) {

			Log.e("7.16", "开始语音");
			tts.speak("接下来是直播时间！", TextToSpeech.QUEUE_FLUSH, null);
		};
	};
	public class TVBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			actionStr = intent.getAction(); // action

			if ("com.example.iimp_znxj_new2014.stopPlay".equals(actionStr)) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Constant.isManualStop = true;
				finishAndGotoIndex();

			}
			//回复信息的
			if("com.example.iimp_znxj_new2014.setRealTimeContentVCR".equals(actionStr)){
				String result= Constant.ORDER_ACTION + "getRealTimeContent" + Constant.ORDER_TYPE+"0"+ 
				Constant.ORDER_FILEPATH+ "null"+Constant.ORDER_STATE+"play"+Constant.ORDER_PROGREEBAR+"null"+Constant.ORDER_CHANNEL+channel ;
			
				new Thread(new Client(result)).start();
				
				
			}
			if (Constant.SUBTITLE_ACTION.equals(actionStr)) { // 汉字最少两位数就能滚屏
				Bundle bundle = intent.getExtras(); //
				String subTitle = bundle.getString(Constant.SUB_TITLE);
				mScrollText.setVisibility(View.VISIBLE);
				DisplayMetrics dm = new DisplayMetrics();
				 getWindowManager().getDefaultDisplay().getMetrics(dm);
			     int width = dm.widthPixels;    //手机屏幕水平分辨率
			     int height = dm.heightPixels;  //手机屏幕垂直分辨率
			    Log.e("MainActivity","屏幕长宽比："+height+"|"+width);
			    if(width==720){
			    	mScrollText.setText("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + subTitle);
//			    	mScrollText.setText(subTitle);
			    }else{
			    	mScrollText.setText(subTitle);
			    	mScrollText.setText("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + subTitle);
			    }
				mSubTitleShowTime = bundle.getString(Constant.SUB_TIME);

				Message msg = new Message();
				msg.what = MESSAGE_SHOW_TIME_START;
				mHandler.sendMessage(msg);
				new Thread(new Client("playSubTitle:true")).start();

				Log.i(TAG, "接收到字幕,显示时间：" + mSubTitleShowTime);
			} else if (Constant.STOP_SUBTITLE_ACTION.equals(actionStr)) {
				mScrollText.setVisibility(View.INVISIBLE);
				new Thread(new Client("stopSubTitle:true")).start();
				// Log.i("TS","接收到关闭字幕的命令");
			}

		}
	}

	private Boolean isUnregisterReceiver = false;

	
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onStart() {
		super.onStart();
		

	}

	@Override
	protected void onPause() {

		isKeyAction = false;

		super.onPause();
		if (isUnregisterReceiver == false) {
			unregisterReceiver(broadcastReceiver);
			isUnregisterReceiver = true;
		}
		DoStop();
		DologinOut();
		

		if (tts != null) {
			tts.shutdown();
		}
	}

	private boolean initeActivity() {
		m_osurfaceView = (SurfaceView) findViewById(R.id.Sur_Player);
		m_osurfaceView.getHolder().addCallback(this);
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tv, menu);
		return true;
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.i(TAG, "surface is created" + m_iPort);
		if (-1 == m_iPort) {
			return;
		}
		Surface surface = holder.getSurface();
		if (null != m_oPlayerSDK && true == surface.isValid()) {
			if (false == m_oPlayerSDK.setVideoWindow(m_iPort, 0, holder)) {
				Log.e(TAG, "Player setVideoWindow failed!");
			}
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i(TAG, "Player setVideoWindow release!" + m_iPort);
		if (-1 == m_iPort) {
			return;
		}
		if (null != m_oPlayerSDK && true == holder.getSurface().isValid()) {
			if (false == m_oPlayerSDK.setVideoWindow(m_iPort, 0, null)) {
				Log.e(TAG, "Player setVideoWindow failed!");
			}
		}
	}

	/**
	 * @fn initeSdk
	 * @author huyf
	 * @brief SDK init
	 * @param NULL
	 *            [in]
	 * @param NULL
	 *            [out]
	 * @return true - success;false - fail
	 */
	private boolean initeSdk() {
		// get an instance and init net sdk
		m_oHCNetSDK = new HCNetSDK();
		if (null == m_oHCNetSDK) {
			Log.e(TAG, "m_oHCNetSDK new is failed!");
			return false;
		}

		if (!m_oHCNetSDK.NET_DVR_Init()) {
			Log.e(TAG, "HCNetSDK init is failed!");
			return false;
		}

		// init player
		m_oPlayerSDK = Player.getInstance();

		m_oPlayerSDK.setDisplayBuf(nPort, m_oPlayerSDK.getDisplayBuf(nPort) * 1000); // 缓存区

		if (m_oPlayerSDK == null) {
			Log.e(TAG, "PlayCtrl getInstance failed!");
			return false;
		}
		Log.e(TAG, "m_oHCNetSDK init Succes!");
		return true;
	}

	private void DoStop() {
		stopPlay();
	}

	private void Doplay(int channlenum) {
		try {
			if (m_iLogID < 0) {
				Log.e(TAG, "please login on device first");
				return;
			}
			if (m_iPlayID < 0) {
				if (m_iPlaybackID >= 0) // ���ڻط�
				{
					Log.i(TAG, "Please stop palyback first");
					return;
				}
				RealPlayCallBack fRealDataCallBack = getRealPlayerCbf();

				if (fRealDataCallBack == null) {
					Log.e(TAG, "fRealDataCallBack object is failed!");
					return;
				}

				NET_DVR_IPPARACFG_V40 struIPPara = new NET_DVR_IPPARACFG_V40();
				m_oHCNetSDK.NET_DVR_GetDVRConfig(m_iLogID, HCNetSDK.NET_DVR_GET_IPPARACFG_V40, 0, struIPPara);

				int iFirstChannelNo = channlenum;// get start channel no
				/*	if(struIPPara.dwAChanNum > 0)
					{
						iFirstChannelNo = 1;
					}
					else
					{
						iFirstChannelNo = struIPPara.dwStartDChan;
					}
					*/
				Log.i(TAG, "iFirstChannelNo:" + iFirstChannelNo);

				NET_DVR_CLIENTINFO ClientInfo = new NET_DVR_CLIENTINFO();
				ClientInfo.lChannel = iFirstChannelNo; // start channel no +
//				2 147 483 648 		1073741824		// preview channel
//				ClientInfo.lLinkMode = 1<<31	;  	 // bit 31 -- 0,main
				// stream;1,sub stream
				ClientInfo.lLinkMode = 0;
				// ClientInfo.lLinkMode = 1; // bit 31 -- 0,main stream;1,sub
				// stream
				// bit 0~30 -- link type,0-TCP;1-UDP;2-multicast;3-RTP
				ClientInfo.sMultiCastIP = null;

				// net sdk start preview

				m_iPlayID = m_oHCNetSDK.NET_DVR_RealPlay_V30(m_iLogID, ClientInfo, fRealDataCallBack, true);

				if (m_iPlayID < 0) {
					Log.e(TAG, "NET_DVR_RealPlay is failed!Err:" + m_oHCNetSDK.NET_DVR_GetLastError());
					this.finishAndGotoIndex();
					return;
				}

				Log.i(TAG, "NetSdk Play sucess ***********************3***************************");
				new Thread(new Client("playVCR:true")).start();
			} else {

			}
		} catch (Exception err) {
			Log.e(TAG, "error: " + err.toString());
			new Thread(new Client("playVCR:"+ err.toString())).start();
			this.finishAndGotoIndex();
		}
	}

	private void DologinOut() {
		if (m_iLogID > 0) {
			if (!m_oHCNetSDK.NET_DVR_Logout_V30(m_iLogID)) {
				Log.e(TAG, " NET_DVR_Logout is failed!");
				return;
			}
			m_iLogID = -1;
		}
	}

	private void DologinIn() {
		try {
			if (m_iLogID < 0) {
				// login on the device
				m_iLogID = loginDevice();
				if (m_iLogID < 0) {
					Log.e(TAG, "This device logins failed!");
					this.finishAndGotoIndex();
					new Thread(new Client("playVCR:This device logins failed!")).start();
					return;
				}
				// get instance of exception callback and set
				ExceptionCallBack oexceptionCbf = getExceptiongCbf();
				if (oexceptionCbf == null) {
					Log.e(TAG, "ExceptionCallBack object is failed!");
					new Thread(new Client("playVCR:ExceptionCallBack object is failed!")).start();
					this.finishAndGotoIndex();
					return;
				}

				if (!m_oHCNetSDK.NET_DVR_SetExceptionCallBack(oexceptionCbf)) {
					Log.e(TAG, "NET_DVR_SetExceptionCallBack is failed!");
					new Thread(new Client("playVCR:NET_DVR_SetExceptionCallBack is failed!")).start();
					this.finishAndGotoIndex();
					return;
				}

				Log.i(TAG, "Login sucess ****************************1***************************");
			} else {
				// whether we have logout
			}
		} catch (Exception err) {
			Log.e(TAG, "error: " + err.toString());
		}
	}

	/**
	 * @fn loginDevice
	 * @author huyf
	 * @brief login on device
	 * @param NULL
	 *            [in]
	 * @param NULL
	 *            [out]
	 * @return login ID
	 */
	private int loginDevice() {
		// get instance
		m_oNetDvrDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();
		if (null == m_oNetDvrDeviceInfoV30) {
			new Thread(new Client("playVCR:false")).start();
			this.finishAndGotoIndex();
			Log.e(TAG, "HKNetDvrDeviceInfoV30 new is failed!");
			return -1;
		}

		// call NET_DVR_Login_v30 to login on, port 8000 as default
		int iLogID = m_oHCNetSDK.NET_DVR_Login_V30(strIP, nPort, strUser, strPsd, m_oNetDvrDeviceInfoV30);
		if (iLogID < 0) {
			Log.e(TAG, "NET_DVR_Login is failed!Err:" + m_oHCNetSDK.NET_DVR_GetLastError());
			return -1;
		}

		Log.i(TAG, "NET_DVR_Login is Successful!");

		return iLogID;
	}

	/**
	 * @fn getExceptiongCbf
	 * @author huyf
	 * @brief process exception
	 * @param NULL
	 *            [in]
	 * @param NULL
	 *            [out]
	 * @return exception instance
	 */
	private int exceptionCount = 0;

	private ExceptionCallBack getExceptiongCbf() {
		ExceptionCallBack oExceptionCbf = new ExceptionCallBack() {
			public void fExceptionCallBack(int iType, int iUserID, int iHandle) {
				// you can add process here
				Log.v(TAG, "---iType=" + iType + "----iUserID=" + iUserID + "----iHandle=" + iHandle);

				// 如果得不到直播数据超过一分钟回到主界面
				if (iType == 32773) {
					exceptionCount++;
					Log.v(TAG, exceptionCount + "");
				} else {
					exceptionCount = 0;
				}
				if (exceptionCount > 6) {
					TVActivity.this.finishAndGotoIndex();
					new Thread(new Client("can not get TV data")).start();
				}
			}
		};
		return oExceptionCbf;
	}

	/**
	 * @fn stopPlay
	 * @author huyf
	 * @brief stop preview
	 * @param NULL
	 *            [in]
	 * @param NULL
	 *            [out]
	 * @return NULL
	 */
	private void stopPlay() {

		if (m_iPlayID < 0) {
			Log.e(TAG, "m_iPlayID < 0");
			return;
		}

		// net sdk stop preview
		if (!m_oHCNetSDK.NET_DVR_StopRealPlay(m_iPlayID)) {
			Log.e(TAG, "StopRealPlay is failed!Err:" + m_oHCNetSDK.NET_DVR_GetLastError());
			return;
		}

		// player stop play
		if (!m_oPlayerSDK.stop(m_iPort)) {
			Log.e(TAG, "stop is failed!");
			return;
		}

		m_oPlayerSDK.stopSound();

		if (!m_oPlayerSDK.closeStream(m_iPort)) {
			Log.e(TAG, "closeStream is failed!");
			return;
		}
		if (!m_oPlayerSDK.freePort(m_iPort)) {
			Log.e(TAG, "freePort is failed!" + m_iPort);
			return;
		}
		m_iPort = -1;
		// set id invalid
		m_iPlayID = -1;
	}

	/**
	 * @fn getRealPlayerCbf
	 * @author huyf
	 * @brief get realplay callback instance
	 * @param NULL
	 *            [in]
	 * @param NULL
	 *            [out]
	 * @return callback instance
	 */

	private RealPlayCallBack getRealPlayerCbf() {
		RealPlayCallBack cbf = new RealPlayCallBack() {
			public void fRealDataCallBack(int iRealHandle, int iDataType, byte[] pDataBuffer, int iDataSize) {
				// player channel 1
				// Log.v(TAG,"-----iDataType"+iDataType+"----pDataBuffer.length"+pDataBuffer.length+"-----iDataSize"+iDataSize);
				TVActivity.this.processRealData(1, iDataType, pDataBuffer, iDataSize, Player.STREAM_REALTIME);
			}
		};
		return cbf;
	}

	/**
	 * @fn processRealData
	 * @author huyf
	 * @brief process real data
	 * @param iPlayViewNo
	 *            - player channel [in]
	 * @param iDataType
	 *            - data type [in]
	 * @param pDataBuffer
	 *            - data buffer [in]
	 * @param iDataSize
	 *            - data size [in]
	 * @param iStreamMode
	 *            - stream mode [in]
	 * @param NULL
	 *            [out]
	 * @return NULL
	 */
	private int count = 0;
	private Random random = new Random();

	public void processRealData(int iPlayViewNo, int iDataType, byte[] pDataBuffer, int iDataSize, int iStreamMode) {
		// Log.i(TAG, "iPlayViewNo:" + iPlayViewNo + "iDataType:" + iDataType +
		// "iDataSize:" + iDataSize);
		try {
			switch (iDataType) {
			case HCNetSDK.NET_DVR_SYSHEAD:
				if (m_iPort >= 0) {
					break;
				}
				m_iPort = m_oPlayerSDK.getPort();
				if (m_iPort == -1) {
					Log.e(TAG, "getPort is failed!");
					break;
				}
				if (iDataSize > 0) {

					if (!m_oPlayerSDK.setStreamOpenMode(m_iPort, iStreamMode)) // set
																				// stream
																				// mode
					{
						Log.e(TAG, "setStreamOpenMode failed");
						break;
					}
					if (!m_oPlayerSDK.setSecretKey(m_iPort, 1, "ge_security_3477".getBytes(), 128)) {
						Log.e(TAG, "setSecretKey failed");
						break;
					}
					if (!m_oPlayerSDK.openStream(m_iPort, pDataBuffer, iDataSize, 3 * 1024 * 1024)) // open
																									// stream
					{
						Log.e(TAG, "openStream failed");
						break;
					}
					if (!m_oPlayerSDK.play(m_iPort, m_osurfaceView.getHolder())) {
						Log.e(TAG, "play failed");
						break;
					}
					m_oPlayerSDK.playSound(m_iPort);
				}
				break;
			case HCNetSDK.NET_DVR_STREAMDATA:
			case HCNetSDK.NET_DVR_STD_AUDIODATA:
			case HCNetSDK.NET_DVR_STD_VIDEODATA:
				if (!m_oPlayerSDK.inputData(m_iPort, pDataBuffer, iDataSize)) {

					Log.e(TAG, "inputData failed with: " + m_oPlayerSDK.getLastError(m_iPort));

					count++;
					Log.v("7.21", count + "");

					// 当卡住时重新开启画面
					if (count > 56) {
						this.finishAndGotoIndex();
						// 防止多个设备同一时间请求
						Thread.sleep(100 + random.nextInt(500));


						/*mHandler.sendEmptyMessage(Pause);
						
						//防止多个设备同一时间请求
						Thread.sleep(100+random.nextInt(600));
						
						mHandler.sendEmptyMessage(Resume);*/
					}
				} else {

				}
				break;

			default:
				/*	if (!m_oPlayerSDK.inputData(m_iPort, pDataBuffer, iDataSize))
					{
						Log.e(TAG, "inputData failed with: " + m_oPlayerSDK.getLastError(m_iPort));
					}	  */
				break;
			}
		} catch (Exception e) {
			Log.e(TAG, "processRealData Exception!err:" + e.toString());
		}
	}

	public void Restart() {
		
		
		
		SharedPreferences sp = DianJiaoApplication.getInstance().getSharedPreferences("TvShow", Context.MODE_PRIVATE);
		String ip = sp.getString("ip", "192.168.0.144");
		String port = sp.getString("port", "8000");
		String user = sp.getString("user", "admin");
		String password = sp.getString("password", "12345");
		String channel = sp.getString("channel", "33");

		Intent intent = new Intent(this, TVActivity.class);
		intent.putExtra("ip", ip);
		intent.putExtra("port", port);
		intent.putExtra("user", user);
		intent.putExtra("password", password);
		intent.putExtra("channel", channel);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.v(TAG, "onKeyDown" + keyCode);
		if (keyCode == 4) {
			this.finishAndGotoIndex();
		}
		return true;
	}

	public void finishAndGotoIndex() {
		finish();
		Intent intent = new Intent();
		intent.setClass(this, IndexActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		Constant.isManualStop = true;
	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			int result = tts.setLanguage(Locale.CHINA);
			if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Toast.makeText(this, "请先安装TTS软件", Toast.LENGTH_SHORT).show();
			}
		}
	}

}
