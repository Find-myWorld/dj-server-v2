package com.example.iimp_znxj_new2014.activity;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;

import org.webrtc.videoengine.ViERenderer;

import com.example.iimp_znxj_new2014.R;
import com.example.iimp_znxj_new2014.util.Constant;
import com.example.iimp_znxj_new2014.util.FileHelper;
import com.example.iimp_znxj_new2014.util.JingMoOrder;
import com.example.iimp_znxj_new2014.util.JingMoOrder.Client;
import com.example.iimp_znxj_new2014.util.PreferenceUtil;
import com.kedacom.platform2mc.ntv.IPhoenixSDKConstantVal;
import com.kedacom.platform2mc.ntv.IPhoenixSDK_Android;
import com.kedacom.platform2mc.struct.DevChn;
import com.kedacom.platform2mc.struct.DeviceGroupInfo;
import com.kedacom.platform2mc.struct.DeviceID;
import com.kedacom.platform2mc.struct.DeviceInfo;
import com.kedacom.platform2mc.struct.EventInfo;
import com.kedacom.platform2mc.struct.GroupID;
import com.kedacom.platform2mc.struct.StreamParam;
import com.kedacom.platform2mc.struct.SubsDevices;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class KeDaVCRActivity extends BaseActivity implements UncaughtExceptionHandler{
	public IPhoenixSDK_Android mCuSdk = new IPhoenixSDK_Android();
	private SurfaceView  mSurfaceView;
	private int mPlayID;
	private List<DeviceInfo> DeviceInfoList = new ArrayList<DeviceInfo>();
	private List<DeviceGroupInfo> DeviceGroupList = new ArrayList<DeviceGroupInfo>();
	int Platform = 0; // 1 mean platformOne;2 mean platformTwo
	private boolean m_LoginSucc = false;
	private boolean IsPlaying = false;
    /**
    *  实时浏览TCP断链回调
    */
	public static final int START_STREAM_TCP_SHUTDOWN=5;
	/**
	 * TCp断链
	 */
//	public static final int START_STREAM_TCP_SHUTDOWN=23;
//	public static final int START_STREAM_TCP_SHUTDOWN=7081;
	
	private static final String TAG = "KeDaVCRActivity";
	private String ip, admin, password, channel;

	private ArrayList<String> deviceIdArray = new ArrayList<String>();
	private boolean isFirst = true;

	private TextView mScrollText;

	private String errorServiceIP = null;
	private String errorServicePort = null;
	private BroadcastReceiver broadcastReceiver;

	/**
	 * 参数channel 为空则为true 自动获取通道
	 * channel不为空 则为false 播放指定通道
	 */
	private boolean autoSelectedDev = true;

	//弃用由于科达通道号无意义
	private int deviceNum;
	/**
	 * 科达专用channel传参为设备名称
	 */
	private String deviceName;
	
	private String deviceChannel;
	private Thread MyThread;

	
	private Handler mhandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
//			case START_STREAM_TCP_SHUTDOWN:
//				Toast.makeText(KeDaVCRActivity.this, "捕捉到回调！", Toast.LENGTH_SHORT).show();
//				StopPlay();
//				break;
			case START_STREAM_TCP_SHUTDOWN:
				Toast.makeText(KeDaVCRActivity.this, "捕捉到回调!回调值 --mEventInfo.getM_emWork()："+msg.arg1, Toast.LENGTH_SHORT).show();
				if(msg.arg1 != 0)
				{
					StopPlay();
				}
				
				break;
			case STOP_PLAY:
				IsPlaying=false;
				if(helper.hasSD()){
					helper.writeSDFile("callback==>do,play()==>do", "LogCode.txt");
				}else{
				//	Toast.makeText(getApplicationContext(),"没有SD卡", Toast.LENGTH_LONG).show();
				}
				play();
				
				break;
				
			default:
				break;
			}
			
		}
		
	};
	private FileHelper helper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_kedavcr);
		helper = new FileHelper(KeDaVCRActivity.this); 

		mScrollText = (TextView) findViewById(R.id.scroll_text_vcr_tv);

		mSurfaceView = ViERenderer.CreateRenderer(this.getApplicationContext(), true);
		LayoutParams laParamsSurfaceView = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		FrameLayout main = (FrameLayout) this.findViewById(R.id.main);

		main.addView(mSurfaceView, -1, laParamsSurfaceView);

		

//		mCuSdk.Init();
		mCuSdk.InitV2(KeDaVCRActivity.this);
		if(helper.hasSD()){
			helper.writeSDFile("onCreate==>mCuSdk.Init():Success", "LogCode.txt");
		}else{
		//	Toast.makeText(getApplicationContext(),"没有SD卡", Toast.LENGTH_LONG).show();
		}
		mCuSdk.SetMainCtx();
		String logPath = Environment.getExternalStorageDirectory() + "/mcusdk_log_new";
        mCuSdk.SetSaveLogFile(0, logPath);//1有日志，0不打日志 
        mCuSdk.SetScreenShowLog(1);
		DeviceGroupInfo DevGroupInfo = new DeviceGroupInfo();
		DevGroupInfo.groupID = "";
		DeviceGroupList.add(DevGroupInfo);

		IntentFilter filter = new IntentFilter();
		broadcastReceiver = new TVBroadcastReceiver();
		filter.addAction(Constant.STOPPLAY_ACTION);
		filter.addAction(Constant.SUBTITLE_ACTION);
		filter.addAction(Constant.STOP_SUBTITLE_ACTION);
		filter.addAction(Constant.CHECKONLINE_ACTION);
		registerReceiver(broadcastReceiver, filter);

		errorServicePort = PreferenceUtil.getSharePreference(KeDaVCRActivity.this, Constant.SERVER_IP_PORT);
		errorServiceIP = PreferenceUtil.getSharePreference(KeDaVCRActivity.this, Constant.TERMINAL_IP);

		admin = getIntent().getStringExtra("user");
		password = getIntent().getStringExtra("password");
		ip = getIntent().getStringExtra("ip");
		channel = getIntent().getStringExtra("channel");
		deviceChannel=getIntent().getStringExtra("deviceChannel");
		if (channel == null || "".equals(channel) ) {
			autoSelectedDev = true;
		} else {
			autoSelectedDev = false;
			deviceName=channel;

		}
		
		login();
		
		new Thread(new Runnable() {
				
				@SuppressWarnings("static-access")
				@Override
				public void run() {
					
					try {
						int[] useless =new int [255] ;
						EventInfo mEventInfo=new EventInfo();
						while(true){
							boolean callback=mCuSdk.JSetMsgEventCB(useless,mEventInfo);
							if(callback ){
								if(helper.hasSD()){
									helper.writeSDFile("callback==>do"+"mEventInfo.getM_emWork()==>"+mEventInfo.getM_emWork(), "LogCode.txt");
								}else{
								//	Toast.makeText(getApplicationContext(),"没有SD卡", Toast.LENGTH_LONG).show();
								}
								Message msg=Message.obtain();
//								msg.what=mEventInfo.m_dwErrorCode;
								msg.arg1 = mEventInfo.getM_dwErrorCode();
								msg.what=mEventInfo.getM_emWork();
								mhandler.sendMessage(msg);
								
							}else{
							     try {
									Thread.sleep(100);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					} catch (Exception e) {
						
						new Thread(new Client("callback:"+e.toString())).start();
					}
					
					
				}
			}).start();
		JingMoOrder.postErrorLog3(KeDaVCRActivity.this, "科达直播参数:admin="+admin+",password="+password+",ip="+ip+",channel="+channel, Constant.NORMAL_MSG);
	}

	public void login() {
		try {
			if(helper.hasSD()){
				helper.writeSDFile("login()==>do", "LogCode.txt");
			}else{
			//	Toast.makeText(getApplicationContext(),"没有SD卡", Toast.LENGTH_LONG).show();
			}
			int errorCode[] = { 0 };
			if (TextUtils.isEmpty(admin) || TextUtils.isEmpty(password) || TextUtils.isEmpty(ip)) {
				Toast.makeText(KeDaVCRActivity.this, "填写内容不能为空！", Toast.LENGTH_SHORT).show();
				JingMoOrder.postErrorLog3(KeDaVCRActivity.this, "账号，密码，ip不能为空", Constant.ERROR_MSG);//发送错误日志
				new Thread(new Client("playVCR:false,error:empty login message")).start();
				this.finishAndGOtoIndex();
			} else {
				Platform = mCuSdk.PlatTypeDetect(ip, errorCode);
				mCuSdk.ModualSelect(Platform, 3, 1);
				Log.e("McuSdkAndroidDemo", "mButton_Login enter");
				if (Platform == 1) {
					m_LoginSucc = mCuSdk.LogIn(admin, password, ip, "ANDROID_PHONE", errorCode);
				} else if (Platform == 2) {
					m_LoginSucc = mCuSdk.LogIn(admin, password, ip, "ANDROID_PHONE", errorCode);
				}

				if (m_LoginSucc) {
					if(helper.hasSD()){
						helper.writeSDFile(" login()==>mCuSdk.LogIn:success", "LogCode.txt");
					}else{
					//	Toast.makeText(getApplicationContext(),"没有SD卡", Toast.LENGTH_LONG).show();
					}
					
					
					Toast.makeText(KeDaVCRActivity.this, "登陆平台成功！", Toast.LENGTH_SHORT).show();
					if (autoSelectedDev) {
						//自动获取设备列表中能够播放的通道并且循环播放
						play_AutoSelectDev();
					} else {
						//播放指定通道号中的信号
						play();
					}
				} else {
					new Thread(new Client("playVCR:false,error:login platform failure")).start();
					JingMoOrder.postErrorLog3(KeDaVCRActivity.this, "登陆失败:直播错误码" + errorCode[0], Constant.ERROR_MSG);
					this.finishAndGOtoIndex();
					Toast.makeText(KeDaVCRActivity.this, "登陆平台失败！", Toast.LENGTH_SHORT).show();
				}
			}
		} catch (Exception e) {
			new Thread(new Client("play():"+e.toString())).start();
		}
		
	}

	public void getDevice() {

		DeviceInfoList.clear();
		DeviceGroupList.clear();
		DeviceGroupInfo DevGroupInfo = new DeviceGroupInfo();
		DevGroupInfo.groupID = "";
		DeviceGroupList.add(DevGroupInfo);

		int errorCode[] = { 0 };
		int GroupIdx = 0;
		for (GroupIdx = 0; GroupIdx < DeviceGroupList.size(); GroupIdx++) {
			GroupID temGroupId = new GroupID();
			temGroupId.setSzID(DeviceGroupList.get(GroupIdx).groupID);
			int TskID = mCuSdk.GetGroupByGroup(temGroupId, errorCode);
			if (TskID > 0) {
				boolean IsGetGroupInfo = true;
				while (IsGetGroupInfo) {
					DeviceGroupInfo groupInfo = new DeviceGroupInfo();
					IsGetGroupInfo = mCuSdk.GetGroupNext(TskID, groupInfo, errorCode);
					if (IsGetGroupInfo) {
						DeviceGroupList.add(groupInfo);
						Log.e("McuSdkAndroidDemo", "mButton_GetDev with DeviceGroupList.add groupInfo.szGroupName:=" + groupInfo.szGroupName);
					} else if ((!IsGetGroupInfo) && (errorCode[0] == 0)) {
						Log.e("McuSdkAndroidDemo", "mButton_GetDev Exit with GetGroupNext ERR");
						break;
					}
				}
			} else {
				Log.e("McuSdkAndroidDemo", "mButton_GetDev Exit with GetGroupByGroup fail errorCode:" + errorCode[0]);
				return;
			}
		}

		for (GroupIdx = 0; GroupIdx < DeviceGroupList.size(); GroupIdx++) {
			GroupID temGroupId = new GroupID();
			temGroupId.setSzID(DeviceGroupList.get(GroupIdx).groupID);
			int TskID = mCuSdk.GetDeviceByGroup(temGroupId, errorCode);
			if (TskID > 0) {
				boolean IsGetDevInfo = true;
				while (IsGetDevInfo) {
					DeviceInfo DevicesInfo = new DeviceInfo();
					IsGetDevInfo = mCuSdk.GetDeviceNext(TskID, DevicesInfo, errorCode);
					if (IsGetDevInfo) {
						DeviceInfoList.add(DevicesInfo);
						Log.v("test", DevicesInfo.deviceID);
						// 定阅设备消息
						SubsDevices mSubsDevices = new SubsDevices();
						DeviceID dev = new DeviceID();

						dev.szID = DevicesInfo.deviceID;

						if (isFirst) {
							deviceIdArray.add(dev.szID);
						}
						Log.i("TTSS", "设备ID:" + dev.szID);

						mSubsDevices.vctDevID[0] = dev;
						mSubsDevices.bySubsDevNum = 1;
						mCuSdk.SetSubscriptDeviceStatus(mSubsDevices, IPhoenixSDKConstantVal.SDK_SUB_SCRIPTION_TYPE_ONLINE, errorCode);// 订阅设备上下线
						mCuSdk.SetSubscriptDeviceStatus(mSubsDevices, IPhoenixSDKConstantVal.SDK_SUB_SCRIPTION_TYPE_CONFIG, errorCode);// 订阅通道上下张
						mCuSdk.SetSubscriptDeviceStatus(mSubsDevices, IPhoenixSDKConstantVal.SDK_SUB_SRCIPTION_TYPE_GPS, errorCode);// 订阅设备gps信息
						Log.e("McuSdkAndroidDemo", "mButton_GetDev with GroupName:=" + DeviceGroupList.get(GroupIdx).szGroupName + "下的设备 DevName:=" + DevicesInfo.szDevSrcAlias);
					} else if ((!IsGetDevInfo) && (errorCode[0] == 0)) {
						isFirst = false;
						Log.e("McuSdkAndroidDemo", "mButton_GetDev Exit with GetDeviceNext ERR");
						Toast.makeText(KeDaVCRActivity.this, "获取设备信息完成!", Toast.LENGTH_SHORT).show();
						break;
					} else if ((!IsGetDevInfo) && (errorCode[0] != 0)) {
						isFirst = false;
						Log.e("McuSdkAndroidDemo", "mButton_GetDev Exit with GetDeviceNext ERR");
						Toast.makeText(KeDaVCRActivity.this, "获取设备组下的设备信息失败!", Toast.LENGTH_SHORT).show();
					}
				}
			} else if ((TskID == 0) && (errorCode[0] == 0)) {
				Log.e("McuSdkAndroidDemo", "mButton_GetDev Exit 根目录下设备不获取");
				continue;
			} else {
				Log.e("McuSdkAndroidDemo", "mButton_GetDev Exit with GetDeviceNext fail errorCode:" + errorCode[0]);
				return;
			}
		}
	}

	public void play_AutoSelectDev() {

		if (false == m_LoginSucc) {
			JingMoOrder.postErrorLog3(KeDaVCRActivity.this, "登陆失败，所以播放失败!", Constant.ERROR_MSG);
			this.finishAndGOtoIndex();
			Toast.makeText(KeDaVCRActivity.this, "登陆失败，所以播放失败!", Toast.LENGTH_LONG).show();
			return;
		}

		if (IsPlaying) {
			int errorCode[] = { 0 };
			boolean StopRe = mCuSdk.StopRealPlay(mPlayID, errorCode);
			if (!StopRe) {
				Log.v("test", "停止正在播放的设备失败");
			}
			IsPlaying = false;
		} else {
			int errorCode[] = { 0 };
			DevChn mDevChn = new DevChn();
			getDevice();

			for (int i = 0; i < DeviceInfoList.size(); i++) {

				mDevChn.deviceID = DeviceInfoList.get(i).getDeviceID();
				mDevChn.domainID = DeviceInfoList.get(i).getDomainID();
				mDevChn.nChn = (short) 0;
				mDevChn.nSrc = (short) 0;

				StreamParam mStreamParam = new StreamParam();
				mStreamParam.m_szManufactor = "kedacom";
				mStreamParam.m_szServerIp = "114.215.175.1";
				mCuSdk.SetHighDefinitionValue(0);
				mCuSdk.SetFeatureCode("MacAddress");

				//播放的参数
				mPlayID = mCuSdk.StartRealPlay(mDevChn, mStreamParam, mSurfaceView, errorCode);
				if ((mPlayID >= 0) && (mPlayID <= 3)) {
					mCuSdk.SetAudioEnable(mPlayID);
					new Thread(new Client("playVCR:true")).start();
					Toast.makeText(KeDaVCRActivity.this, "请求码流播放成功！", Toast.LENGTH_SHORT).show();
					IsPlaying = true;
					break;
				} else if (i == DeviceInfoList.size() - 1) {
					Log.d("McuSdkAndroidDemo", "mCuSdk.StartRealPlay return false ");
					JingMoOrder.postErrorLog3(KeDaVCRActivity.this, "直播错误码" + errorCode[0] + "没有有效的设备", Constant.ERROR_MSG);
					this.finishAndGOtoIndex();
					new Thread(new Client("playVCR:false,error:code=" + errorCode[0])).start();
					Toast.makeText(KeDaVCRActivity.this, "请求码流播放失败,错误码:" + errorCode[0], Toast.LENGTH_LONG).show();
				}
			}

		}
	}
	
	
	int connectTimes=0;
	public void play() {
		if(helper.hasSD()){
			helper.writeSDFile(" play()==>do", "LogCode.txt");
		}else{
		//	Toast.makeText(getApplicationContext(),"没有SD卡", Toast.LENGTH_LONG).show();
		}
		
		
		if (IsPlaying) {
			int errorCode[] = { 0 };
			
			try {
				
				mCuSdk.StopRealPlay(mPlayID, errorCode);
			
			} catch (Exception e) {
				new Thread(new Client("mCuSdk.StopRealPlay:"+e.toString())).start();
			}
			
			
			if(helper.hasSD()){
				helper.writeSDFile(" play()==>do===>isplaying=false", "LogCode.txt");
			}else{
			//	Toast.makeText(getApplicationContext(),"没有SD卡", Toast.LENGTH_LONG).show();
			}
			IsPlaying = false;
//			//新增
//			Message msg = new Message();
//			msg.what = MESSAGE_START;
//			mHandler.sendMessage(msg);
		}
		try {
			int errorCode[] = { 0 };
			StreamParam mStreamParam=new StreamParam();
		    DevChn mDevChn =new DevChn();
		    
			getDevice();

			int getdevice=0;
			int getchannel=0;
//			if (checkNumValid(deviceNum)) {
			for (int i = 0; i < DeviceInfoList.size(); i++) {
				
				if((deviceName+"@kedacom").equals(DeviceInfoList.get(i).getDeviceID())){
					mDevChn.deviceID =DeviceInfoList.get(i).getDeviceID();
					mDevChn.domainID = DeviceInfoList.get(i).getDomainID();
					getdevice++;
					String device1Name="";
					String deviceChannel1 = "";
					for (int j = 0; j < DeviceInfoList.get(i).getnDevSrcNum(); j++) {
						
						device1Name=device1Name+DeviceInfoList.get(i).getaDevSrcChn()[j].getSzSrcName()+",";
						deviceChannel1 =deviceChannel1+ (short) DeviceInfoList.get(i).getaDevSrcChn()[j].getnSn()+",";
//							mDevChn.nSrc = (short) DeviceInfoList.get(i).getaDevSrcChn()[j].getnSn();
//							getchannel++;
					}
//					Toast.makeText(getApplicationContext(), "设备长度:"+DeviceInfoList.get(i).getnDevSrcNum()+",所有设备名称:"+deviceName+"设备通道："+deviceChannel1+",deviceChannel"+deviceChannel, Toast.LENGTH_SHORT).show();
					if(deviceChannel==null || "".equals(deviceChannel)){
						mDevChn.nChn=(short)0;
						mDevChn.nSrc=(short)0;
						getchannel=1;
					}else{
						if("1001".equals(deviceChannel)){
							mDevChn.nChn=(short)0;
							mDevChn.nSrc=(short)0;
							getchannel=1;
							}
						
						if(deviceChannel.equals("1")){
							mDevChn.nChn=(short)0;
							mDevChn.nSrc=(short)0;
							getchannel=1;
						}
						

						if(deviceChannel.equals("2")){
							mDevChn.nChn=(short)1;
							mDevChn.nSrc=(short)1;
							getchannel=1;
						}
						if(deviceChannel.equals("3")){
							mDevChn.nChn=(short)2;
							mDevChn.nSrc=(short)2;
							getchannel=1;
						}
						if(deviceChannel.equals("4")){
							mDevChn.nChn=(short)3;
							mDevChn.nSrc=(short)3;
							getchannel=1;
						}
//							else{
//						Toast.makeText(KeDaVCRActivity.this, deviceChannel, Toast.LENGTH_SHORT).show();
//							for (int j = 0; j < DeviceInfoList.get(i).getaDevSrcChn().length; j++) {
//								if("deviceChannel".equals((short)DeviceInfoList.get(i).getaDevSrcChn()[j].getnSn()+"")){
//									mDevChn.nChn = (short) DeviceInfoList.get(i).getaDevSrcChn()[j].getnSn();
//									mDevChn.nSrc = (short) DeviceInfoList.get(i).getaDevSrcChn()[j].getnSn();
//									getchannel++;
//								}
								
//							}

//						}
					}
					
				}
				
			}
			
			if(getdevice==0){
				JingMoOrder.postErrorLog3(KeDaVCRActivity.this,  "找不到对应的设备！播放失败", Constant.ERROR_MSG);
				this.finishAndGOtoIndex();
				new Thread(new Client("playVCR:false,error找不到对应的设备！播放失败:code=" + errorCode[0])).start();
				Toast.makeText(KeDaVCRActivity.this, "找不到对应的设备！播放失败" , Toast.LENGTH_LONG).show();
			}
			
			if(getchannel==0){
				JingMoOrder.postErrorLog3(KeDaVCRActivity.this,  "找不到对应的通道！播放失败", Constant.ERROR_MSG);
				this.finishAndGOtoIndex();
				new Thread(new Client("playVCR:false,error找不到对应的通道！播放失败:code=" + errorCode[0])).start();
				Toast.makeText(KeDaVCRActivity.this, "找不到对应的 通道！播放失败" , Toast.LENGTH_LONG).show();
			}
							

			    mStreamParam = new StreamParam();
				mStreamParam.m_szManufactor = "kedacom";
				mStreamParam.m_szServerIp = "114.215.175.1";
				mCuSdk.SetHighDefinitionValue(0);
				mCuSdk.SetFeatureCode("MacAddress");
				
               
				mPlayID = mCuSdk.StartRealPlay(mDevChn, mStreamParam, mSurfaceView, errorCode);
				if(helper.hasSD()){
					helper.writeSDFile(" play()==>mCuSdk.StartRealPlay:do", "LogCode.txt");
				}else{
					
				}
//                new Thread(new Runnable() {
//					
//					@Override
//					public void run() {
//						while(connectTimes<5 && mPlayID ==65535){
//							 try {
//				   					Thread.sleep(3000);
//								} catch (InterruptedException e) {
//									e.printStackTrace();
//								}
//							    connectTimes++;
//								Toast.makeText(KeDaVCRActivity.this, "第"+(connectTimes+2)+"次请求", Toast.LENGTH_SHORT).show();
//			   				    mPlayID = mCuSdk.StartRealPlay(mDevChn, mStreamParam, mSurfaceView, errorCode);
////								MyThread.start();
//			   				
//							}
//						
//					}
//				}).start();
	         
				
				for (int i = 0; i < 14; i++) {
					if(mPlayID==65535){
						try {
							Thread.sleep((long) (8000+Math.random()*8000));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						Toast.makeText(KeDaVCRActivity.this, "第"+(i+2)+"次请求", Toast.LENGTH_SHORT).show();
						
							if(helper.hasSD()){
								helper.writeSDFile(" play()==>mCuSdk.StartRealPlay:do", "LogCode.txt");
							}else{
							//	Toast.makeText(getApplicationContext(),"没有SD卡", Toast.LENGTH_LONG).show();
							}
						mPlayID = mCuSdk.StartRealPlay(mDevChn, mStreamParam, mSurfaceView, errorCode);
					}
				}
				 
//			} else {
//				mPlayID = -Integer.MAX_VALUE;
//			}

			if (mPlayID!= 65535) {
			
					if(helper.hasSD()){
						helper.writeSDFile(" play()==>mCuSdk.StartRealPlay:success", "LogCode.txt");
					}else{
					//	Toast.makeText(getApplicationContext(),"没有SD卡", Toast.LENGTH_LONG).show();
					}
				Log.v("test", "成功播放的设备ID:" + mDevChn.deviceID);
				mCuSdk.SetAudioEnable(mPlayID);
				Toast.makeText(KeDaVCRActivity.this, "请求码流播放成功！", Toast.LENGTH_SHORT).show();
				IsPlaying = true; 
			} else {
				JingMoOrder.postErrorLog3(KeDaVCRActivity.this, "直播错误码" + errorCode[0] + "没有有效的设备", Constant.ERROR_MSG);
				this.finishAndGOtoIndex();
				Toast.makeText(KeDaVCRActivity.this, "请求码流播放失败,错误码:" + errorCode[0] + "没有有效的设备", Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			new Thread(new Client("StartRealPlay:"+e.toString())).start();
		}
		
			
		}

	

	
	private boolean checkNumValid(int deviceNum) {
		if (deviceNum > DeviceInfoList.size()) {
			return false;
		}
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == 4) {
			this.finishAndGOtoIndex();
		}
		return true;
	}

	private boolean haveUnregisted = false;

	@Override
	protected void onStop() {
		super.onStop();
		if (!haveUnregisted) {
			this.unregisterReceiver(broadcastReceiver);
		}
	

		
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		try {
			Thread.interrupted();
//			if(IsPlaying){
				int errorCode[] = { 0 };
				mCuSdk.StopRealPlay(mPlayID, errorCode);
				if(helper.hasSD()){
					helper.writeSDFile("onPause()==> mCuSdk.StopRealPlay):do", "LogCode.txt");
				}
				if(helper.hasSD()){
					helper.writeSDFile("onPause():do===>isplaying=false", "LogCode.txt");
				}
				IsPlaying=false;

				mCuSdk.Logout();
				if(helper.hasSD()){
					helper.writeSDFile("onPause()==>mCuSdk.Logout():do", "LogCode.txt");
				}
				m_LoginSucc=false;
			
			mCuSdk.Cleanup();
			if(helper.hasSD()){
				helper.writeSDFile("onPause()==>mCuSdk.Cleanup():do", "LogCode.txt");
			}
			
		} catch (Exception e) {
			new Thread(new Client("Onpause:"+e.toString())).start();
			
		}
		
		
	}

	public String actionStr = "";

	public class TVBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			actionStr = intent.getAction();
			if (Constant.STOPPLAY_ACTION.contentEquals(actionStr)) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Constant.isManualStop = true;
//				Toast.makeText(getApplicationContext(), "111111", Toast.LENGTH_LONG).show();
				KeDaVCRActivity.this.finishAndGOtoIndex();
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
			    }else if(width==800){
			    	mScrollText.setText("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + subTitle);

			    }else{
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
			} else if (Constant.CHECKONLINE_ACTION.equals(actionStr)) {
				String isOnline = intent.getStringExtra("isOnline");
				if ("false".equals(isOnline)) {
					JingMoOrder.postErrorLog3(KeDaVCRActivity.this, "直播中断退回主界面", Constant.ERROR_MSG);
					KeDaVCRActivity.this.finishAndGOtoIndex();
					Toast.makeText(KeDaVCRActivity.this, "网络中断退回主界面", Toast.LENGTH_SHORT).show();
				}
			}
//			else if(Constant.PAUSE_ACTION1.equals(actionStr)){//新增
//				if(IsPlaying){
//					int errorCode[] = { 0 };
//					Log.i("pause", "1");
//					mCuSdk.StopRealPlay(mPlayID, errorCode);
//					Log.i("pause", "2");
//					IsPlaying=false;
//				}
//			}
		}
	}

	private static final int MESSAGE_SHOW_TIME_START = 1;
	private static final int MESSAGE_SHOW_TIME_REACHED = 2;
//	private static final int MESSAGE_START=3;//新增
	private String mSubTitleShowTime;
	
	Handler mHandler = new Handler() {
		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_SHOW_TIME_START:
				int showTime = Integer.parseInt(mSubTitleShowTime) * 60 * 1000;
				sendEmptyMessageDelayed(MESSAGE_SHOW_TIME_REACHED, showTime);
				Log.i(TAG, "showTime:" + showTime);
				break;
			case MESSAGE_SHOW_TIME_REACHED:
				mScrollText.setVisibility(View.INVISIBLE);
				break;
				//新增
//			case MESSAGE_START:
//				play();
//				break;
			}
		}
	};

	public void finishAndGOtoIndex() {
		finish();
		Intent intent = new Intent();
		intent.setClass(this, IndexActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		Constant.isManualStop = true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
	}
	
	private static final int STOP_PLAY=0x20;
	
	private void StopPlay(){
		
		try {
			if(helper.hasSD()){
				helper.writeSDFile(" StopPlay:do", "LogCode.txt");
			}
			
			int errorCode[] = { 0 };
			mCuSdk.StopRealPlay(mPlayID, errorCode);
			if(helper.hasSD()){
				helper.writeSDFile(" StopPlay==>mCuSdk.StopRealPlay:do", "LogCode.txt");
			}
			Toast.makeText(KeDaVCRActivity.this, "正在停止播放！", Toast.LENGTH_SHORT).show();
			mhandler.sendEmptyMessageDelayed(STOP_PLAY, 2000);
						
		} catch (Exception e) {
			new Thread(new Client("stopPlay:"+e.toString())).start();
		}
		
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		new Thread(new Client("uncaughtException:"+e.toString())).start();
	}

	
	
	
	
	
}
