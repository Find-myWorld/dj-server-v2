package com.example.iimp_znxj_new2014.activity;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.cyberplayer.core.BVideoView;
import com.baidu.cyberplayer.core.BVideoView.OnCompletionWithParamListener;
import com.baidu.cyberplayer.core.BVideoView.OnErrorListener;
import com.baidu.cyberplayer.core.BVideoView.OnInfoListener;
import com.baidu.cyberplayer.core.BVideoView.OnPlayingBufferCacheListener;
import com.baidu.cyberplayer.core.BVideoView.OnPreparedListener;
import com.baidu.cyberplayer.subtitle.SubtitleManager;
import com.example.iimp_znxj_new2014.R;
import com.example.iimp_znxj_new2014.util.Constant;
import com.example.iimp_znxj_new2014.util.JingMoOrder;
import com.example.iimp_znxj_new2014.util.JingMoOrder.Client;
import com.example.iimp_znxj_new2014.util.SubtitleSettingPopupWindow;

/*
 * 百度公司提供的视频播放方案,硬解码（已过期，不使用）
 * <p>这里有个退出的问题，每次切换视频文件或播放结束，都会调用onCompletion()方法。退出是加了一个判断位：isTrue（在onPrepared()方法中设置）</p>
 */
public class VideoMediaPlayingActivity extends Activity implements OnPreparedListener, 
							OnErrorListener, 
							OnInfoListener,
							OnPlayingBufferCacheListener,
							OnCompletionWithParamListener
							{
	private final String TAG = "VideoViewPlayingActivity";
	
	private String mVideoSource = null;      //当前播放路径
	private String mCompareAnther = null;
	private boolean isTrue = true;           //判断标志，如果是播放另一视频，isTrue = false;防止info = 702 退出 
	
	private BVideoView mVV = null;
	private RelativeLayout mViewHolder = null;
//	private LinearLayout mControllerHolder = null;
	private TextView mScrollText;            //字幕
	private StatusReceiver mStatusReceiver;  //广播
	
	private boolean mType = true;        //为true时回复消息，反之不回复消息
	
	private EventHandler mEventHandler;
	private HandlerThread mHandlerThread;
	
	private boolean mIsHwDecode = true;
	private String fileType;
	
	private final Object SYNC_Playing = new Object();
		
	private final int EVENT_PLAY = 0;
	private int size;
	private int VOLUME_VALUE = 10;
	
	private WakeLock mWakeLock = null;
	private static final String POWER_LOCK = "VideoViewPlayingActivity";
	private static final String PAUSE_ACTION = "com.example.iimp_znxj_new2014.pause";
	private static final String CONTINUE_PLAY_ACTION = "com.example.iimp_znxj_new2014.continueplay";
	private static final String SUBTITLE_ACTION = "com.example.iimp_znxj_new2014.subtitle";
	private static final String STOPPLAY_ACTION = "com.example.iimp_znxj_new2014.stopPlay";
	private static final String VOLUME_ACTION = "com.example.iimp_znxj_new2014.volume";
	private static final String GET_PROGRESS = "com.example.iimp_znxj_new2014.getprogress";
	private static final String GET_PERCENT = "com.example.iimp_znxj_new2014.getpercent";
	
	public static final String BASE_PATH = Environment
			.getExternalStorageDirectory().getAbsolutePath();
	private String curTime;
	
	/**
	 * 播放状态
	 */
	private  enum PLAYER_STATUS {
		PLAYER_IDLE, PLAYER_PREPARING, PLAYER_PREPARED,     //闲置的、准备的、准备的
	}
	
	private PLAYER_STATUS mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
	
	/**
	 * 记录播放位置
	 */
	private int mLastPos = 0;
	
	//add for subtitle
    private Button mSubtitleButton;
    private SubtitleSettingPopupWindow mSubtitleSettingWindow;
    private RelativeLayout mRoot;
    private SubtitleManager mSubtitleManager;
    
    private static final int MESSAGE_SHOW_TIME_START = 1;
	private static final int MESSAGE_SHOW_TIME_REACHED = 2;
	private String mSubTitleShowTime;
	
	class EventHandler extends Handler {
		public EventHandler(final Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(final Message msg) {
			switch (msg.what) {
			case EVENT_PLAY:
				/**
				 * 如果已经播放了，等待上一次播放结束
				 */
		  		if (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE) {
					synchronized (SYNC_Playing) {  //控制对类成员变量的访问：每个类实例对应一把锁
						try {
							SYNC_Playing.wait();
						//	Log.v("onCompletion", "5.wait player status to idle");
						} catch (final Exception e) {
							// TODO Auto-generated catch block
							Log.v("onCompletion", "Error:"+e.getMessage());
							e.printStackTrace();
						}
					}
				}  
				/**
				 * 设置播放url
				 */
				mVV.setVideoPath(mVideoSource);
				mVV.showCacheInfo(true);
				//开始播放
				mVV.start();
				mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARING;
				break;
			default:
				break;
			}
		}
	}
			
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
						
		setContentView(R.layout.controllerplaying);
		
		final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, POWER_LOCK);
		
		mIsHwDecode = getIntent().getBooleanExtra("isHW", false);
		final Uri uriPath = getIntent().getData();
		if (null != uriPath) {
			final String scheme = uriPath.getScheme();
			if (null != scheme) {
				mVideoSource = uriPath.toString();
			} else {
				mVideoSource = uriPath.getPath();
			}
		}
		
		mType = getIntent().getBooleanExtra("flag", true);   //判断是定时播，还是在线播
		
		initUI();
		/**
		 * 开启后台事件处理线程
		 */
		mHandlerThread = new HandlerThread("event handler thread",
				Process.THREAD_PRIORITY_BACKGROUND);
		mHandlerThread.start();
		mEventHandler = new EventHandler(mHandlerThread.getLooper());
		
		mStatusReceiver = new StatusReceiver();
		final IntentFilter filter = new IntentFilter();
	//	player.registerPlayListener(this);   //结束动作监听
		filter.addAction(PAUSE_ACTION);
		filter.addAction(CONTINUE_PLAY_ACTION);
		filter.addAction(SUBTITLE_ACTION);
		filter.addAction(STOPPLAY_ACTION);
		filter.addAction(Constant.STOP_SUBTITLE_ACTION);
		filter.addAction(Constant.ANOTHELOCALVIDEO_ACTION);
		filter.addAction(Constant.ANOTHERPLAY_ACTION);
		filter.addAction(VOLUME_ACTION);
		filter.addAction(GET_PROGRESS);
		filter.addAction(GET_PERCENT);
		filter.addAction(Constant.MESSAGE_TYPE_ACTION);
		filter.setPriority(Integer.MAX_VALUE);
		registerReceiver(mStatusReceiver, filter);
		
		final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		final Date curDate = new Date(System.currentTimeMillis());
		curTime = formatter.format(curDate);
	//	Log.i(TAG,"路径+时间："+curTime+"|"+BASE_PATH);
		JingMoOrder.deleteFilePath(BASE_PATH+"/base_log.txt");
		JingMoOrder.WriteTxtFile(curTime+"|"+mVideoSource, BASE_PATH+"/base_log.txt");
	}
	
	/**
	 * 初始化界面
	 */
	private void initUI() {		
		mViewHolder = (RelativeLayout)findViewById(R.id.view_holder);
//		mControllerHolder = (LinearLayout )findViewById(R.id.controller_holder);
		mScrollText = (TextView)findViewById(R.id.local_scroll_text_baiduvideo);
		
		/**
		 * 设置ak（全部）  和  sk的前16位
		 */
		BVideoView.setAKSK("SQzgOQCjyARFMjVxBNc91f65", "kG1so9A4GcrWrmiu");
		/**
		 *创建BVideoView和BMediaController
		 */
		mVV = new BVideoView(this);
		mViewHolder.addView(mVV);
		/**
		 * 注册listener
		 */
		mVV.setOnPreparedListener(this);
		mVV.setOnCompletionWithParamListener(this);
		mVV.setOnErrorListener(this);
		mVV.setOnInfoListener(this);
		mVV.setOnPlayingBufferCacheListener(this);
		/**
		 * 关联BMediaController
		 */
	//	mVV.setMediaController(mVVCtl);
		/**
		 * 设置解码模式
		 */
	//	
		Log.i(TAG,"mIsHwDecode:"+isHwDecode(mVideoSource));
	   	if(isHwDecode(mVideoSource)){
	   		mVV.setDecodeMode(BVideoView.DECODE_HW); //硬解,仅支持mp4，流畅
		}else{
			mVV.setDecodeMode(BVideoView.DECODE_SW); //软解,全格式，卡顿
		}
	}
	
	public class StatusReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(final Context context, final Intent intent) {
			final String actionStr = intent.getAction();
			if ("com.example.iimp_znxj_new2014.pause".equals(actionStr)) {
			//	Log.i(TAG,"pause");
				if(mVV.isPlaying()){
					mVV.pause();
				//	mLastPos = mVV.getCurrentPosition();
				}
				new Thread(new Client("pause:true")).start();
			} else if ("com.example.iimp_znxj_new2014.continueplay"
					.equals(actionStr)) {
			//	Log.i(TAG,"continueplay...");
				if(!mVV.isPlaying()){
					mVV.resume();  //恢复播放
				}
				new Thread(new Client("continuePlay:true")).start();
			} else if ("com.example.iimp_znxj_new2014.subtitle"
					.equals(actionStr)) {
			//	Log.i(TAG,"subtitle");
				mScrollText.setVisibility(View.VISIBLE);
				final Bundle bundle = intent.getExtras(); // 
				final String subTitle = bundle.getString(Constant.SUB_TITLE);
				mSubTitleShowTime = bundle.getString(Constant.SUB_TIME);
				Log.i(TAG,"subTime:"+mSubTitleShowTime);   
				
				final Message msg = new Message();
				msg.what = MESSAGE_SHOW_TIME_START;
				mCheckNoteShowHandler.sendMessage(msg);  //字幕定时关闭
				
				mScrollText.setText("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t"+subTitle);
				new Thread(new Client("playSubTitle:true")).start();
			} else if(Constant.STOP_SUBTITLE_ACTION.equals(actionStr)) {
				mScrollText.setVisibility(View.INVISIBLE);
				new Thread(new Client("stopSubTitle:true")).start();
			} else if ("com.example.iimp_znxj_new2014.stopPlay"
					.equals(actionStr)) {
				Log.i(TAG,"stopPlay");
				if (mVV != null) {
					mVV.stopPlayback();  //结束播放,回到首页
				}
				VideoMediaPlayingActivity.this.finish();
				final Intent stopIntent = new Intent(VideoMediaPlayingActivity.this,
						IndexActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(stopIntent);
			}else if (Constant.ANOTHELOCALVIDEO_ACTION
					.equals(actionStr)) {
				final Bundle bundle = intent.getExtras();
				final String newfilename = bundle.getString(Constant.PLAY_LOCAL_VIDEO_FILE_NAME);
				Log.i(TAG,"新接收到的播放本地："+newfilename);
				mCompareAnther = newfilename;
				mType = true;   //实时播放类型
			    playAnother(mCompareAnther);
			}else if (Constant.ANOTHERPLAY_ACTION
					.equals(actionStr)) {
				final Bundle bundle = intent.getExtras();
			    final String url = bundle.getString(Constant.PLAY_LOCAL_VIDEO_FILE_NAME);
				Log.i(TAG,"新接收到的在线路径："+url);
				mCompareAnther = url;
				mType = true;   //实时播放类型
		
				if(isHwDecode(mCompareAnther)){
					Log.i(TAG,"播放另一个硬解码");
				//	mVV.setDecodeMode(BVideoView.DECODE_HW);
					if(mVV.isPlaying()){
						mVV.stopPlayback();
					}
					isTrue = false;
					mVV.setVideoPath(mCompareAnther);
					//开始播放
					mVV.start();
				}else{
					Log.i(TAG,"播放另一个软解码");
					new Thread(new Client("playVideo:false")).start();
				    VideoMediaPlayingActivity.this.finish();
				//	isTrue = false;
				//	mVV.setDecodeMode(BVideoView.DECODE_SW); //软解,全格式，卡顿
				//	playAnotherVideo(mCompareAnther);
				}
			}else if (VOLUME_ACTION.equals(actionStr)){
				final Bundle bundle = intent.getExtras();
			    final String value = bundle.getString(Constant.VOLUME_VALUE);
			    Log.i("VOLUME","接收到音量调节的命令,value="+value);
			    
			    if(value.equals("up")){
			    	VOLUME_VALUE++;
			    }else if(value.equals("down")){
			    	VOLUME_VALUE--;
			    }
			    Log.i("VOLUME","当前音量值："+VOLUME_VALUE); 
			    SoundCtrl(VOLUME_VALUE);
			}else if(GET_PROGRESS.equals(actionStr)){
				Log.i("GetProgressBar","接收到返回当前播放进度的广播， 80%");
				getPercent();
				new Thread(new Client("progressBar:"+getPercent()+"%")).start();
			}else if(Constant.MESSAGE_TYPE_ACTION.equals(actionStr)){   //来自定时的播放
			    mType = false;
			    Log.i(TAG, "定时类型");
			}else if(GET_PERCENT.equals(actionStr)){
				final Bundle bundle = intent.getExtras();
			    final int getPercent = Integer.parseInt(bundle.getString(Constant.PERCENT_VALUE));
			    Log.i(TAG,"获取的百分比为："+getPercent);
			//    mVV.seekTo(getPercent);
			    seekTo(getPercent);  
			}
		}
	}
	
	/*
	 * 将传过来的进度转换为秒数
	 */
	private void seekTo(final int percent){
	    double number = mVV.getDuration();
		double sTime = 0;
		sTime = (double)percent/100 * number;
		mVV.seekTo(sTime);
		new Thread(new Client("jumpTo:true")).start();
	}
	
	/*
	 * 获取当前播放进度
	 * @return  百分比
	 */
	public int getPercent(){
		final int number = mVV.getCurrentPosition();
    	final int percent = (int)((number)/(float)mVV.getDuration()*100);
		return percent;
	} 
	
	/*
	 * 音量调节
	 */
	private void SoundCtrl(int val)
	{
		final AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);    
		//最大音量    
		final int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);    
		if(val > maxVolume){
			val = maxVolume;
		}else if(val < 0){
			val = 0;
		}
		//当前音量    
		final int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC); 
		Log.i("VOLUME", "maxVolume="+maxVolume+":"+"currentVolume = " + currentVolume + ",设置的音量："+val);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, val, 0); //tempVolume:音量绝对值    
	}
	
	/*
	 * 定时关闭字幕
	 */
	private final Handler mCheckNoteShowHandler = new Handler() {
		public void handleMessage(final android.os.Message msg) {
			switch (msg.what) {
			case MESSAGE_SHOW_TIME_REACHED:
				mScrollText.setVisibility(View.INVISIBLE);
				break;
			case MESSAGE_SHOW_TIME_START:
				final int showTime = Integer.parseInt(mSubTitleShowTime) * 60 * 1000;
				sendEmptyMessageDelayed(MESSAGE_SHOW_TIME_REACHED, showTime); 
				Log.i(TAG,"showTime:"+showTime);
				break;
			}
		};
	};
	/*
	 * 播放另一个文件
	 */
	public void playAnother(final String afilename){
		mVideoSource = "/mnt/extsd/Download/"+afilename;
	//	Log.i(TAG,"playAnother,本地路径为："+mVideoSource);
		/**
		 * 如果已经开发播放，先停止播放
		 */
		if(mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE){
			mVV.stopPlayback();
		}
					
		if(mEventHandler.hasMessages(EVENT_PLAY))
			mEventHandler.removeMessages(EVENT_PLAY);
		mEventHandler.sendEmptyMessage(EVENT_PLAY);
	}
	
	/*
	 * 播放另一个流媒体文件
	 */
	public void playAnotherVideo(final String url){
		mVideoSource = url;
	//	isTrue = true;
		/**
		 * 如果已经开发播放，先停止播放
		 */
		if(mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE){
			Log.i("onCompletion","1.结束当前");
			mVV.stopPlayback();
		}
		if(mEventHandler.hasMessages(EVENT_PLAY))
			mEventHandler.removeMessages(EVENT_PLAY);
		mEventHandler.sendEmptyMessage(EVENT_PLAY);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.v(TAG, "onPause");
		/**
		 * 在停止播放前 你可以先记录当前播放的位置,以便以后可以续播
		 */
		if (mPlayerStatus == PLAYER_STATUS.PLAYER_PREPARED) {
			mLastPos = mVV.getCurrentPosition();
			mVV.stopPlayback();
		}
	}

	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.v("STATUS", "onResume");
		if (null != mWakeLock && (!mWakeLock.isHeld())) {
			mWakeLock.acquire();
		}
		/**
		 * 发起一次播放任务,当然您不一定要在这发起
		 */
		mEventHandler.sendEmptyMessage(EVENT_PLAY);	
	}
	
	@Override
	protected void onStop(){
		super.onStop();
		Log.v(TAG, "onStop");
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		Log.v(TAG, "onDestroy");
		/**
		 * 结束后台事件处理线程
		 */
		mHandlerThread.quit();
		if (mCheckNoteShowHandler.hasMessages(MESSAGE_SHOW_TIME_START)) {
			mCheckNoteShowHandler.removeMessages(MESSAGE_SHOW_TIME_START);
		}
		if (mCheckNoteShowHandler.hasMessages(MESSAGE_SHOW_TIME_REACHED)) {
			mCheckNoteShowHandler.removeMessages(MESSAGE_SHOW_TIME_REACHED);
		}
	//	mCheckNoteShowHandler.removeCallbacks(mPicThread);
	}
    
	@Override
	public boolean onInfo(final int what, final int extra) {     
		JingMoOrder.WriteTxtFile(what+","+extra+" | ", BASE_PATH+"/base_log.txt");
		Log.i(TAG,"onInfo:"+what+"|"+extra);
		switch(what){
		//开始缓冲
		case BVideoView.MEDIA_INFO_BUFFERING_START:
			JingMoOrder.WriteTxtFile("开始缓冲...", BASE_PATH+"/base_log.txt");
			Log.i(TAG,"start cahing..."+what+"|"+extra);
			mVV.pause();
			new Thread(new Runnable(){   
				public void run(){
					try{
						Thread.sleep(1500);
						mVV.resume();
						JingMoOrder.WriteTxtFile("pause...continue", BASE_PATH+"/base_log.txt");
					} catch (final InterruptedException e){
						e.printStackTrace();
					}
				}
			}).start();
			break;
		// 结束缓冲
		case BVideoView.MEDIA_INFO_BUFFERING_END:
			JingMoOrder.WriteTxtFile("MEDIA_INFO_BUFFERING_END", BASE_PATH+"/base_log.txt");
			Log.i(TAG,"stop cahing..."+what+"|"+extra);
			if(what == 702){
				if(!isTrue){
					new Thread(new Client("playVideo:false")).start();
				}
				Log.i(TAG,"播放已结束，退出");//硬解码播放结束标志
				this.finish();
			}  
			break;
		}
		isTrue = true;
		Log.i(TAG,"istrue2="+isTrue);
		return false;
	}
	
	/**
	 * 当前缓冲的百分比， 可以配合onInfo中的开始缓冲和结束缓冲来显示百分比到界面
	 */
	@Override
	public void onPlayingBufferCache(final int percent) {
		// TODO Auto-generated method stub
		Log.i(TAG,"onPlayingBufferCache:"+percent);
	}	

	/**
	 * 播放出错
	 */
	@Override
	public boolean onError(final int what, final int extra) {
		// TODO Auto-generated method stub
		Log.v(TAG, "onError");
		synchronized (SYNC_Playing) {
			SYNC_Playing.notify();
		}
		new Thread(new Client("playVideo:false")).start();
		this.finish();
		
		mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
		return true;
	}
	
	/**
	 * 播放完成,有返回参数
	 * <p>这个方法很强大，每次切换视频、播放视频、结束播放，它都会判断当前文件是否存在</p>
	 */
	@Override
	public void OnCompletionWithParam(final int arg0) {
		// TODO Auto-generated method stub
		Log.v(TAG, "OnCompletionWithParam:"+arg0);
		//302:文件不存在？
		//307:播放结束？
		if(arg0 == 307){
			Log.i(TAG,"播放已结束，退出");
			this.finish();
		}else if(arg0 == 302){
			Log.i(TAG,"没有该文件，退出");
			new Thread(new Client("playVideo:no such file!")).start();
			this.finish();
		}else if(arg0 == 304){
			Log.i(TAG,"未知错误，退出");
			new Thread(new Client("playVideo:false")).start();
			this.finish();
		}
		synchronized (SYNC_Playing) {
			SYNC_Playing.notify();
		}
		mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
	}

	/**
	 * 播放准备就绪
	 */
	@Override
	public void onPrepared() {
		// TODO Auto-generated method stub
		Log.v(TAG, "onPrepared");				
		mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARED;
	//	isTrue = true;
		if(mType){
			new Thread(new Client("playVideo:true")).start();
		}
	}
	
	/*
	 * 获取下载文件大小
	 */
	public  void getFileSize(final String url){
		new Thread(){
			public void run(){
				try {
					final URL u = new URL(url);
					final HttpURLConnection urlcon = (HttpURLConnection)u.openConnection();
					final int fileLength = urlcon.getContentLength();
					if(fileLength != 0){
						size = fileLength;
					}
					if(size <= 1163){
						size = 0;
					}
				} catch (final IOException e) {
					size = -1;
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	
	/**
	 * 根据播放文件名决定采用硬解码，还是软解码
	 * @param filePath 播放路径
	 * @return true:硬解码，false：软解码
	 */
	public boolean isHwDecode(String filePath){
		String[] split = filePath.split("\\.");
		Log.i(TAG,"split长度："+split.length);
		if(split.length > 1){
			Log.i(TAG,"后缀："+split[split.length - 1]);
			if(!split[split.length-1].equals("mp4")){
				return false;
			}
		}
		return true;
	}
	
}
