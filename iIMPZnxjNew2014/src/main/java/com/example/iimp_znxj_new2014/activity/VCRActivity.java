package com.example.iimp_znxj_new2014.activity;

import org.MediaPlayer.PlayM4.Player;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iimp_znxj_new2014.R;
import com.example.iimp_znxj_new2014.util.Constant;
import com.example.iimp_znxj_new2014.util.JingMoOrder;
import com.example.iimp_znxj_new2014.util.PreferenceUtil;
import com.example.iimp_znxj_new2014.util.JingMoOrder.Client;
import com.hikvision.netsdk.ExceptionCallBack;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_CLIENTINFO;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.hikvision.netsdk.NET_DVR_IPPARACFG_V40;
import com.hikvision.netsdk.RealPlayCallBack;

//电视直播
public class VCRActivity extends BaseActivity implements Callback {

	/* �����Դ�ĺ�����ϵ */
	private Player m_oPlayerSDK = null;
	private HCNetSDK m_oHCNetSDK = null;
	private NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30 = null;
	private int m_iLogID = -1; // return by NET_DVR_Login_v30
	private int m_iPlayID = -1; // return by NET_DVR_RealPlay_V30
	private int m_iPlaybackID = -1; // return by NET_DVR_PlayBackByTime
	private int m_iPort = -1; // play port
	private final String TAG = "TVActivity";
	private String vidip = "192.168.1.90";
	private int vidport = 8000;
	private String vidusr = "admin";
	private String vidpass = "12345";
	private int vidchannle = 2;
	/* ����ؼ� */
	private static SurfaceView m_osurfaceView = null;
	/* �̴߳��� */
	private Handler mHandler;

	final static int ConnentOver = 1;
	final static int SendOver = 2;
	final static int DataComeIn = 3;
	final static int TimeOver = 4;
	final static int PlayTV = 5;
	private TextView mScrollText;
	private StatusReceiver mStatusReceiver;
	final static int DelayTime = 2000;
	private static final String SUBTITLE_STRING = "com.example.iimp_znxj_new2014.subtitle";
//	private JingMoOrder jmo = null;
	private String mSubTitleShowTime;
	private static final int MESSAGE_SHOW_TIME_START = 1;
	private static final int MESSAGE_SHOW_TIME_REACHED = 2;
	
	private boolean mType = true;
	private int VOLUME_VALUE = 10;
	private static final int m_playHD = 0; // 高清
	private static final int m_playSD = 1; // 标清

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tvshow);
	//	jmo = new JingMoOrder();
		Intent intent = this.getIntent(); // ��ȡ���е�intent����
		Bundle bundle = intent.getExtras(); // ��ȡintent�����bundle����
//		String playIp = bundle.getString(Constant.PLAY_VCR_IP);
//		String playPort = bundle.getString(Constant.PLAY_VCR_PORT);
//		String channel = bundle.getString(Constant.PLAY_VCR_CHANNEL);
//		String userName = bundle.getString(Constant.PLAY_VCR_USER_NAME);
//		String passWord = bundle.getString(Constant.PLAY_VCR_PASSWORD);
		
		String playIp = getIntent().getStringExtra("ip"); // ipAddress
		String playPort = getIntent().getStringExtra("port");// port
		String userName = getIntent().getStringExtra("user"); // UserName
		String passWord= getIntent().getStringExtra("password"); // PassWord
		String channel = getIntent().getStringExtra("channel");

//		JingMoOrder.postErrorLog3(TVActivity.this, "海康直播参数:admin="+strUser+",password="+strPsd+",ip="+strIP+",port="+nPort+",channel="+channel, Constant.NORMAL_MSG);
//		Log.v("7.20", "strIP" + strIP + "nPort" + nPort + "strUser" + strUser + "strPsd" + strPsd);
		mType = getIntent().getBooleanExtra("flag", true);   //判断是定时播，还是在线播
		Log.i(TAG,"mType="+mType+"(true在线播放，false是定时播放)");
		vidip = playIp;
//		Log.i(TAG,"onCreate:"+playIp+"|"+playPort+"|"+channel+"|"+userName+"|"+passWord);
		
		if (!TextUtils.isEmpty(playPort)){
			vidport = Integer.parseInt(playPort);
		}
		vidusr = userName;
		vidpass = passWord;
		if (!TextUtils.isEmpty(channel)){
			vidchannle = Integer.parseInt(channel);
		}
		if (!initSDK()) {      //没有初始化成功会直接结束
			this.finish();
			return;
		}  
		Log.i(TAG,"onCreate5");
		if (!initeActivity()) {//没有初始化成功会直接结束
			this.finish();
			return;
		}
		new Thread(new DelayPlayThread()).start();
		mHandler = new Handler() {
			@SuppressLint("HandlerLeak")
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case PlayTV: // ��ʱ�Զ�����
					TV_Login();    //登录成功后才会播放
					TV_Play(vidchannle);
					TV_Full();
					break;
				}
			}
		};

		mScrollText = (TextView) findViewById(R.id.scroll_text_vcr);
		mScrollText.setVisibility(View.INVISIBLE);
		mStatusReceiver = new StatusReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.SUBTITLE_ACTION);
		filter.addAction(Constant.STOP_SUBTITLE_ACTION);  //关闭字幕的广播
		filter.addAction(Constant.STOPPLAY_ACTION);
		filter.addAction(Constant.STOPPLAY_VCR_ACTION);
		filter.addAction(Constant.ANOTHEVCRRPLAY_ACTION);
		registerReceiver(mStatusReceiver, filter);
		
	}

	public void TV_Full() {
		LayoutParams lp = (LayoutParams) m_osurfaceView.getLayoutParams();
		/*
		 * lp.leftMargin=0; lp.topMargin=0; lp.width = 1280; lp.height =720;
		 */
		m_osurfaceView.setLayoutParams(lp);
	}

	public void MessageBox(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	
	public class DelayPlayThread implements Runnable { // thread
		@Override
		public void run() {

			try {
				Thread.sleep(DelayTime); // sleep 1000ms
				Message message = new Message();
				message.what = PlayTV;
				mHandler.sendMessage(message);
			} catch (Exception e) {
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tvshow, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	private boolean initeActivity() {
		m_osurfaceView = (SurfaceView) findViewById(R.id.HK_Sur_Player);
		m_osurfaceView.getHolder().addCallback(this);
		return true;
	}

	private void TV_Play(int channlenum) {
		try {
			if (m_iLogID < 0) {
				Log.e(TAG, "please login on device first");
				return;
			}
			if (m_iPlayID < 0) {
				if (m_iPlaybackID >= 0) {
					Log.i(TAG, "Please stop palyback first");
					return;
				} // ���ڻط�
				RealPlayCallBack fRealDataCallBack = getRealPlayerCbf();
				if (fRealDataCallBack == null) {
					Log.e(TAG, "fRealDataCallBack object is failed!");
					return;
				}
				NET_DVR_IPPARACFG_V40 struIPPara = new NET_DVR_IPPARACFG_V40();
				m_oHCNetSDK.NET_DVR_GetDVRConfig(m_iLogID,
						HCNetSDK.NET_DVR_GET_IPPARACFG_V40, 0, struIPPara);
				int iFirstChannelNo = channlenum;// get start channel no

				NET_DVR_CLIENTINFO ClientInfo = new NET_DVR_CLIENTINFO();
				ClientInfo.lChannel = iFirstChannelNo; // start channel no +
														// preview channel
//				ClientInfo.lLinkMode = 0; // bit 31 -- 0,main stream;1,sub
											// stream ; bit 0~30 -- link
											// type,0-TCP;1-UDP;2-multicast;3-RTP
				
				ClientInfo.lLinkMode = m_playSD << 31 + 3;

				
				
				ClientInfo.sMultiCastIP = null;
				m_iPlayID = m_oHCNetSDK.NET_DVR_RealPlay_V30(m_iLogID,
						ClientInfo, fRealDataCallBack, true);
				// m_oHCNetSDK.NET_DVR_StopRealPlay(m_iPlayID);
				if (m_iPlayID < 0) {
					Log.e(TAG,
							"NET_DVR_RealPlay is failed!Err:"
									+ m_oHCNetSDK.NET_DVR_GetLastError());
					return;
				}
				if(mType){
					new Thread(new Client("playVCR:true")).start();   //回复成功的消息给中心服务
				}
				Log.i(TAG, "NetSdk Play sucess *****3*******");
			} else {
			}
		} catch (Exception err) {
			if(mType){
				Constant.isManualStop = true;
				new Thread(new Client("playVCR:false")).start();   //回复成功的消息给中心服务
			}
			Log.e(TAG, "error: " + err.toString());
		}
	}

	private void TV_LogOut() {
		if (m_iLogID > 0) {
			if (!m_oHCNetSDK.NET_DVR_Logout_V30(m_iLogID)) {
				Log.e(TAG, " NET_DVR_Logout is failed!");
				return;
			}
			m_iLogID = -1;
		}
	}

	private void TV_Login() {
		try {
			if (m_iLogID < 0) {
				m_iLogID = loginDevice(); // login on the device
				if (m_iLogID < 0) {
					Log.e(TAG, "This device logins failed!");
					if(mType){
						Constant.isManualStop = true;
						new Thread(new Client("playVCR:false")).start();   //回复成功的消息给中心服务
					}
					exitToMain();
					return;
				}
				// get instance of exception callback and set
				ExceptionCallBack oexceptionCbf = getExceptiongCbf();
				if (oexceptionCbf == null) {
					Log.e(TAG, "ExceptionCallBack object is failed!");
					return;
				}
				if (!m_oHCNetSDK.NET_DVR_SetExceptionCallBack(oexceptionCbf)) {
					Log.e(TAG, "NET_DVR_SetExceptionCallBack is failed!");
					return;
				}
			//	new Thread(new Client("playVCR:true")).start();   //登录成功，回复消息给中心服务
			//	Log.i(TAG,
			//			"Login sucess ****************************1***************************"); // m_oLoginBtn.setText("Logout");

			} else {
				// whether we have logout �Ѿ���½����
			}
		} catch (Exception err) {
			Log.e(TAG, "error: " + err.toString());
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
	private boolean initSDK() {
		// get an instance and init net sdk
		Log.i(TAG,"initSDK1");
		m_oHCNetSDK = new HCNetSDK();
		Log.i(TAG,"initSDK2");   //移动libs文件夹下.so文件即可
		if (null == m_oHCNetSDK) {
			Log.e(TAG, "m_oHCNetSDK new is failed!");
			Constant.isManualStop = true;
			return false;
		}
		if (!m_oHCNetSDK.NET_DVR_Init()) {
			Log.e(TAG, "HCNetSDK init is failed!");
			Constant.isManualStop = true;
			return false;
		}
		m_oPlayerSDK = Player.getInstance();  //报错
		if (m_oPlayerSDK == null) {
			Log.e(TAG, "PlayCtrl getInstance failed!");
			Constant.isManualStop = true;
			return false;
		}
		Log.e(TAG, "m_oHCNetSDK init Succes!");
		return true;
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
		m_oNetDvrDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();
		if (null == m_oNetDvrDeviceInfoV30) {
			Log.e(TAG, "HKNetDvrDeviceInfoV30 new is failed!");
			return -1;
		}
		int iLogID = m_oHCNetSDK.NET_DVR_Login_V30(vidip, vidport, vidusr,
				vidpass, m_oNetDvrDeviceInfoV30);
		if (iLogID < 0) {
			Log.e(TAG,
					"NET_DVR_Login is failed!Err:"
							+ m_oHCNetSDK.NET_DVR_GetLastError());   
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
	private ExceptionCallBack getExceptiongCbf() {
		ExceptionCallBack oExceptionCbf = new ExceptionCallBack() {
			public void fExceptionCallBack(int iType, int iUserID, int iHandle) {
				;// you can add process here
			}
		};
		return oExceptionCbf;
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
			public void fRealDataCallBack(int iRealHandle, int iDataType,
					byte[] pDataBuffer, int iDataSize) {
				// player channel 1
				VCRActivity.this.processRealData(1, iDataType, pDataBuffer,
						iDataSize, Player.STREAM_REALTIME);
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
	public void processRealData(int iPlayViewNo, int iDataType,
			byte[] pDataBuffer, int iDataSize, int iStreamMode) {
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
					if (!m_oPlayerSDK.setStreamOpenMode(m_iPort, iStreamMode)) {
						Log.e(TAG, "setStreamOpenMode failed");
						break;
					}
					if (!m_oPlayerSDK.setSecretKey(m_iPort, 1,
							"ge_security_3477".getBytes(), 128)) {
						Log.e(TAG, "setSecretKey failed");
						break;
					}
					if (!m_oPlayerSDK.openStream(m_iPort, pDataBuffer,
							iDataSize, 2 * 1024 * 1024)) {
						Log.e(TAG, "openStream failed");
						break;
					}
					if (!m_oPlayerSDK.play(m_iPort, m_osurfaceView.getHolder())) {
						Log.e(TAG, "play failed");
						break;
					}
				m_oPlayerSDK.playSound(m_iPort);
//					m_oPlayerSDK.playSound(0);
				}
				break;
			case HCNetSDK.NET_DVR_STREAMDATA:
			case HCNetSDK.NET_DVR_STD_AUDIODATA:
			case HCNetSDK.NET_DVR_STD_VIDEODATA:
				if (!m_oPlayerSDK.inputData(m_iPort, pDataBuffer, iDataSize)) {
					Log.e(TAG,
							"inputData failed with: "
									+ m_oPlayerSDK.getLastError(m_iPort));
					break;
				}
			default:
				/*
				 * if (!m_oPlayerSDK.inputData(m_iPort, pDataBuffer, iDataSize))
				 * { Log.e(TAG, "inputData failed with: " +
				 * m_oPlayerSDK.getLastError(m_iPort)); }
				 */
				break;

			}
		} catch (Exception e) {
			Log.e(TAG, "processRealData Exception!err:" + e.toString());
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
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

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		playStop();
		TV_LogOut();
		unregisterReceiver(mStatusReceiver);
	}

	/*
	 * 播放VCR时接收到广播处理事件：
	 * 1.暂停  2.继续播放 3.退出 4.其他
	 */
	public class StatusReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String actionStr = intent.getAction();
			if (Constant.SUBTITLE_ACTION.equals(actionStr)) {   //汉字最少两位数就能滚屏
				Bundle bundle = intent.getExtras(); //
				String subTitle = bundle.getString(Constant.SUB_TITLE);
				mScrollText.setVisibility(View.VISIBLE);
				mScrollText.setText("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t"+subTitle);
				mSubTitleShowTime = bundle.getString(Constant.SUB_TIME);
				
				Message msg = new Message();
				msg.what = MESSAGE_SHOW_TIME_START;
				mCheckNoteShowHandler.sendMessage(msg);
				new Thread(new Client("playSubTitle:true")).start();
				
				Log.i(TAG,"接收到字幕,显示时间："+mSubTitleShowTime);
			} else if(Constant.STOP_SUBTITLE_ACTION.equals(actionStr)) {
				mScrollText.setVisibility(View.INVISIBLE);
				new Thread(new Client("stopSubTitle:true")).start();
			//	Log.i("TS","接收到关闭字幕的命令");
			}else if ("com.example.iimp_znxj_new2014.stopPlay"
					.equals(actionStr)) {
				playStop();
				TV_LogOut();
				Log.i("TTTS","退出VCR1");
				Constant.isManualStop = true;
			//	new Thread(new Client("stopPlay:true")).start();
				finish();
				Log.d("TTTS","startIndex in VCRActivity");
				Intent stopIntent = new Intent(VCRActivity.this,
						IndexActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(stopIntent);
			} else if (Constant.STOPPLAY_VCR_ACTION.equals(actionStr)) {
				playStop();
				TV_LogOut();
				Log.i("PlayVCR","应用接收到结束广播");
				finish();
			}else if(Constant.ANOTHEVCRRPLAY_ACTION.equals(actionStr)){
				mType = true;
				playStop();
				TV_LogOut();
				Bundle bundle = intent.getExtras(); // ��ȡintent�����bundle����
				String playIp = bundle.getString(Constant.PLAY_VCR_IP);
				String playPort = bundle.getString(Constant.PLAY_VCR_PORT);
				String channel = bundle.getString(Constant.PLAY_VCR_CHANNEL);
				String userName = bundle.getString(Constant.PLAY_VCR_USER_NAME);
				String passWord = bundle.getString(Constant.PLAY_VCR_PASSWORD);
				vidip = playIp;
				if (!TextUtils.isEmpty(playPort))
					vidport = Integer.parseInt(playPort);
				vidusr = userName;
				vidpass = passWord;
				if (!TextUtils.isEmpty(channel))
					vidchannle = Integer.parseInt(channel);
				if (!initSDK()) {
					VCRActivity.this.finish();
					return;
				}
				if (!initeActivity()) {
					VCRActivity.this.finish();
					return;
				}
				new Thread(new DelayPlayThread()).start();
			}
		}
	}

	/*
	 * 定时关闭字幕
	 */
	private Handler mCheckNoteShowHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MESSAGE_SHOW_TIME_REACHED:
				mScrollText.setVisibility(View.INVISIBLE);
				break;
			case MESSAGE_SHOW_TIME_START:
				int showTime = Integer.parseInt(mSubTitleShowTime) * 60 * 1000;
				sendEmptyMessageDelayed(MESSAGE_SHOW_TIME_REACHED, showTime); 
				Log.i(TAG,"showTime:"+showTime);
				break;
			}
		};
	};
	
	private void playStop() {
		if (m_iPlayID < 0) {
			Log.e(TAG, "m_iPlayID < 0");
			return;
		}
		// net sdk stop preview
		if (!m_oHCNetSDK.NET_DVR_StopRealPlay(m_iPlayID)) {
			Log.e(TAG,
					"StopRealPlay is failed!Err:"
							+ m_oHCNetSDK.NET_DVR_GetLastError());
			return;
		}
		// player stop play
		if (!m_oPlayerSDK.stop(m_iPort)) {
			Log.e(TAG, "stop is failed!");
			return;
		}
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
	
	private void exitToMain(){
		this.finish();
		Log.d("jiayy","startIndex in VideoShowActivity stopPlay");
		Intent stopIntent = new Intent(VCRActivity.this,
				IndexActivity.class);
		stopIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(stopIntent);
	}
}
