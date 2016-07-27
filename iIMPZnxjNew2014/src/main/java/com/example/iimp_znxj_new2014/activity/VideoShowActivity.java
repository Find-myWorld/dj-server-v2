package com.example.iimp_znxj_new2014.activity;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnErrorListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iimp_znxj_new2014.R;
import com.example.iimp_znxj_new2014.util.Constant;
import com.example.iimp_znxj_new2014.util.JingMoOrder;
import com.example.iimp_znxj_new2014.util.JingMoOrder.Client;
import com.example.iimp_znxj_new2014.util.JingMoOrder.Clientdiff;
import com.example.iimp_znxj_new2014.util.NetUtil;
import com.example.iimp_znxj_new2014.util.PreferenceUtil;
import com.example.iimp_znxj_new2014.util.ViewFactoryUtil;

/*
 * 流媒体播放（现采用）
 * time:2015.6.30
 * author:zhang wenfa
 * update:支持全格式
 */
public class VideoShowActivity extends BaseActivity implements OnCompletionListener, SurfaceHolder.Callback {
	private SurfaceView surfaceView;
	private Button btnPause, btnPlayUrl, btnStop;
	private SeekBar skbProgress;
	private MediaPlayer player;
	
	private String allFileNames;
	private int  currentPercent;
	// private int curPos = 0;
	private long curPos = 0; // 当前位置
	private StatusReceiver mStatusReceiver;
	private TextView mScrollText;
	private static final String PAUSE_ACTION = "com.example.iimp_znxj_new2014.pause";
	private static final String CONTINUE_PLAY_ACTION = "com.example.iimp_znxj_new2014.continueplay";
	private static final String SUBTITLE_ACTION = "com.example.iimp_znxj_new2014.subtitle";
	private static final String STOPPLAY_ACTION = "com.example.iimp_znxj_new2014.stopPlay";

	private static final String VOLUME_ACTION = "com.example.iimp_znxj_new2014.volume";
	private static final String GET_PROGRESS = "com.example.iimp_znxj_new2014.getprogress";
	private static final String GET_PERCENT = "com.example.iimp_znxj_new2014.getpercent";
	
	private static final String GET_CURPERCENT = "com.example.iimp_znxj_new2014.getcurpercent";

	private static final int SHOW_ERRORDIALOG = 0;
	private static final int SHOW_PROGRESSDIALOG = 1;
	private static final int HIDE_RGRESSDIALOG = 2;

	private int VOLUME_VALUE = 10;

	private static final String TAG = "VideoShowActivity";

	private String mSubTitleShowTime; // 字幕显示时间
	private static final int MESSAGE_SHOW_TIME_START = 1;
	private static final int MESSAGE_SHOW_TIME_REACHED = 2;

	private boolean mType = true; // 为true时回复消息，反之不回复消息
	private String fileName = null; // 记录当前播放文件名
	private String url = null;

	private SurfaceHolder holder;
	private ImageView iv; // 音频用背景图
	private String loop = "-1"; // 判断是否循环播放
	private Random random = new Random(); // 随机数
	Uri uriPath;
	private String testUrl; // 视频播放路径
	private long previousPosition = -1;
	private int countRepeat = 0;
	private long position; // 当前位置
	private long duration;
	private String volume;
	private int curPercent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video);

		//Vitamio.initialize(this);
		// 判断动态链接库(so)是否已经成功加载完成。
		if (!LibsChecker.checkVitamioLibs(this)) {
			this.finishAndGOtoIndex();
			Log.e(TAG, "播放器初始化失败");
			return;
		}

		iv = (ImageView) findViewById(R.id.iv);
		uriPath = getIntent().getData();

		if (null != uriPath) {
			final String scheme = uriPath.getScheme();
			if (null != scheme) {
				url = uriPath.toString();
			} else {
				url = uriPath.getPath();
			}
		}

		curPercent=getIntent().getIntExtra("curPercent", 0);
		
		// 判断是否为空 不为空得到loop的值
		if (getIntent().getExtras().getString(Constant.PLAY_VIDEO_LOOP) != null) {
			loop = getIntent().getExtras().getString(Constant.PLAY_VIDEO_LOOP);
			Log.v("7.10", loop);
		}

		// 判断是否为空 不为空得到volume的值
		if (getIntent().getExtras().getString("volume") != null) {
			volume = getIntent().getExtras().getString("volume");
		}

		// playUrl = getPlayUrl();

		mType = getIntent().getBooleanExtra("flag", true); // 判断是定时播，还是在线播
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		surfaceView = (SurfaceView) this.findViewById(R.id.surface_view);
		player = new MediaPlayer(this);

		// holder的初始化与添加到MediaPlayer上
		holder = surfaceView.getHolder();
		holder.addCallback(this);
		holder.setFormat(PixelFormat.RGBA_8888); // 不加不能正确显示
		player.setDisplay(holder); // 设置显示的surfaceView

		mScrollText = (TextView) findViewById(R.id.scroll_text_video);

		Log.i(TAG, "startPlay");

		mStatusReceiver = new StatusReceiver();

		IntentFilter filter = new IntentFilter();
		filter.addAction(PAUSE_ACTION);
		filter.addAction(CONTINUE_PLAY_ACTION);
		filter.addAction(SUBTITLE_ACTION);
		filter.addAction(STOPPLAY_ACTION);
		filter.addAction(Constant.STOP_SUBTITLE_ACTION);
		filter.addAction(Constant.ANOTHERPLAY_ACTION);
		filter.addAction(VOLUME_ACTION);
		filter.addAction(GET_PROGRESS);
		filter.addAction(GET_PERCENT);
		filter.addAction(GET_CURPERCENT);
		filter.addAction(Constant.CHECKONLINE_ACTION);
		filter.addAction(Constant.MESSAGE_TYPE_ACTION);
		filter.addAction(Constant.MESSAGE_SETREALTIMECONTENT);
		filter.setPriority(Integer.MAX_VALUE);
		registerReceiver(mStatusReceiver, filter);

		// 得到播放路径
		testUrl = getPlayURL();
		// 监听器的添加
		addAllListeners();
		// 判断是否是音频来判断是否显示背景图片
		ThreadHandler.sendEmptyMessage(0);
		changeVolume();
		play();
		new Thread(new Client("playVideo:true")).start();
		
		JingMoOrder.postErrorLog3(VideoShowActivity.this,"流媒体播放路径:"+url, Constant.NORMAL_MSG);
		
		
		
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				while(NetUtil.isNetworkConnected()){
//					try {
//						Thread.sleep(5000);
//					} catch (Exception e) {
//						
//					}
//					String filenames="";
//					for (int i = nums; i <=(fileNums.length)-1; i++) {
//						filenames=filenames+fileNums[i]+",";
//					}
//					Log.e("filenames", filenames);
//					allFileNames=filenames;
//					currentPercent=getPercent();
//				}
//				
//			}
//		}).start();
		
		
	}

	private int volume_system = 10;

	// 使用自身的音量播放
	public void changeVolume() {
		volume_system = PreferenceUtil.getIntegerSharePreference(VideoShowActivity.this, Constant.VOLUME_NUM_VALUE);
		if (volume != null) {
			Log.v("7.30", volume);
			SoundCtrl(Integer.parseInt(volume));
		}
	}

	// 恢复原来声音
	public void recoverVolume() {
		SoundCtrl(volume_system);
	}

	private boolean needResume;
	boolean isJudge = false; // 是否卡死线程的标志

	public void addAllListeners() {

		// 缓冲监听器
		player.setOnInfoListener(new OnInfoListener() {

			public boolean onInfo(MediaPlayer arg0, int arg1, int arg2) {
 				switch (arg1) {
				case MediaPlayer.MEDIA_INFO_BUFFERING_START:
					// 开始缓存，暂停播放
					if (player.isPlaying()) {
						player.stop();
						needResume = true;
						Log.i(TAG, "开始缓存，暂停播放");
						Message msg = new Message();
						msg.what = SHOW_PROGRESSDIALOG;
						msg.obj = "缓冲中...";
						viewShowHandler.sendMessage(msg);
					}
					break;
				case MediaPlayer.MEDIA_INFO_BUFFERING_END:
					// 缓存完成，继续播放
					if (needResume)
						player.start();
					playNomal=true;
					Log.i(TAG, "缓存完成，继续播放");
					viewShowHandler.sendEmptyMessage(HIDE_RGRESSDIALOG);
					break;
				case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
					// 显示 下载速度
					// Log.e(TAG, arg2+"");
					break;
				}
				return true;
			}
		});

		// 缓冲完成监听器
		player.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				player.start();
				playNomal=true;
				Log.i(TAG, "onPrepared,player.start()");

				// 当视频卡住超过预设时间时直接播放下一个视频 但是要判断是否是暂停
				isJudge = true;
				duration = player.getDuration();
				countRepeat = 0;
				new Thread() {

					public void run() {
						while (isJudge && null != player) {
							if (player != null) {
								position = player.getCurrentPosition();
								if (position != previousPosition) {
									previousPosition = position;
									countRepeat = 0;
								} else {
									countRepeat++;

									Log.v(TAG, "position:-----" + position);
									if (countRepeat > 2) {

										/*
										 * 当卡住时player.isBuffering()为true(但有的播到中途卡住isBuffing也是false)
										 * 暂停时player.isBuffering()为false
										 * 当缓冲时优先让视频重新播放
										 * 不能重新播放直接播放下一个(目前直接跳到下一个)
										 */

										if (player.isBuffering()) {
											// player.start();
											playNext();
											if (player.isPlaying()) {
											}
										}

//										if (!NetUtil.isNetworkConnected()) {
//
//											Log.e(TAG, "网络中断");
//											Message msg = new Message();
//											msg.what = SHOW_ERRORDIALOG;
//											msg.obj = " 网 络 出 现 异 常 ！";
//											viewShowHandler.sendMessage(msg);
//											playNext();
//										}
									}
								}
							}

							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

						}
					}
				}.start();

				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				// 判断是否是音频来判断是否显示背景图片
				ThreadHandler.sendEmptyMessage(0);

				new Thread(new Client("playVideo:true")).start();
			}
		});

		// 完成的监听
		player.setOnCompletionListener(this);

		player.setOnErrorListener(new OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {

				String errMsg = null;
				if (what == 1 && extra == -5) {
					errMsg = "文件不存在或文件名有误";
				} else {
					errMsg = "错误的后缀名或其他问题";
				}
//				JingMoOrder.postErrorLog(PreferenceUtil.getSharePreference(VideoShowActivity.this, Constant.SERVER_IP_PORT),
//						PreferenceUtil.getSharePreference(VideoShowActivity.this, Constant.TERMINAL_IP), errMsg + ",当前播放路径:" + url);
				JingMoOrder.postErrorLog3(VideoShowActivity.this,errMsg+",接收播放路径为:" + url,Constant.ERROR_MSG);
				Log.e(TAG, "Error:" + errMsg);
				Toast.makeText(VideoShowActivity.this, errMsg, Toast.LENGTH_LONG).show();
				new Thread(new Client(errMsg)).start();
				return false;
			}
		});
	}

	/*
	 * 线程中改变View
	 */
	Dialog progressDialog;
	@SuppressLint("HandlerLeak")
	Handler viewShowHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_ERRORDIALOG:
				String errorMsg = (String) msg.obj;
				Toast.makeText(VideoShowActivity.this, errorMsg, Toast.LENGTH_LONG).show();
				break;
			case SHOW_PROGRESSDIALOG:
				String showString = (String) msg.obj;
				progressDialog = ViewFactoryUtil.createLoadingDialog(VideoShowActivity.this, showString);
				progressDialog.show();
				break;
			case HIDE_RGRESSDIALOG:
				if (progressDialog != null) {
					progressDialog.hide();
				}
				break;
			}

		};
	};

	int hostNum = 0; // 主机号
	int fileNum = 0; // 文件在该主机中的号
	int hostTotalNum = 0; // 总共的主机数
	int fileTotalNum = 0; // 该主机总共的文件数
	ArrayList<ArrayList<String>> hostList = new ArrayList<ArrayList<String>>();
	ArrayList<FileNum> allFileList = new ArrayList<FileNum>();

	// 存放所有播放文件的主机号数 与文件号数
	class FileNum {
		private int hostNum;
		private int fileNum;

		public FileNum(int hostNum, int fileNum) {
			this.hostNum = hostNum;
			this.fileNum = fileNum;
		}

		public int getHostNum() {
			return hostNum;
		}

		public void setHostNum(int hostNum) {
			this.hostNum = hostNum;
		}

		public int getFileNum() {
			return fileNum;
		}

		public void setFileNum(int fileNum) {
			this.fileNum = fileNum;
		}
	}

	String playurl; // 播放的路径
	Boolean isGetAllFileList = false; // 是否已经得到全部列表
	Boolean isFirstRandom = false; // 是否播放是第一次且是随机
	Boolean isFirstPlay = true; // 第一次播放初始化
	String[] hosts = null; // 所有主机号
	String port = null;
	String[] ports = null; // 所有端口号

	// 带&参数能够不同服务器对应不同文件

	public String getPlayURL() {
		// 得到主机数组
		if (isFirstPlay) {
			getAllList();
			isFirstPlay = false;
		}

		// 进行一次全部文件遍历
		if (isGetAllFileList == false) {
			getAllFileList();
			isGetAllFileList = true;
		}

		// 得到当前主机号所有文件数量
		fileTotalNum = hostList.get(hostNum % (hostTotalNum)).size();
		Log.v("15", "fileTotalNum=" + fileTotalNum);

		Log.v("15", "fileNum=" + fileNum + "hostNum=" + hostNum);

		// 第一次播放并且是随机时直接从列表中随机一个
		if (isFirstRandom == false && "random".equalsIgnoreCase(loop)) {
			int i = random.nextInt(allFileList.size());
			hostNum = allFileList.get(i).getHostNum();
			fileNum = allFileList.get(i).getFileNum();
			Log.v("7.23", "----hostNum:" + hostNum + "-----fileNum:" + fileNum + "-----i=" + i + "----size" + allFileList.size());
			allFileList.remove(i);
			isFirstRandom = true;
		}

//		playurl = "http://" + hosts[hostNum % (hostTotalNum)] + ":" + ports[hostNum % (hostTotalNum)] + "/" + hostList.get(hostNum % (hostTotalNum)).get(fileNum);
		playurl = "http://" + hosts[hostNum % (hostTotalNum)] + ":" + ports[hostNum % (hostTotalNum)] + "/" + fileNums[nums];
		Log.v("15", "playurl=" + playurl);

		return playurl;
	}
	String [] fileNums = null;
	int nums=0;
	// 得到所有的主机 端口 文件的集合
	public void getAllList() {
		String host = uriPath.getHost();
		// String host =getIntent().getStringExtra("host");
		hosts = host.split(",");
		hostTotalNum = hosts.length;

		// 得到端口号

		port = getIntent().getStringExtra("port");
		Log.v("z215", port);
		ports = port.split(",");
		for (String string : ports) {
			Log.v("z215", "端口" + string);
		}

		// 得到文件名称数组
		String split[] = url.split("/");
		fileName = split[split.length - 1];
		
		fileNums=fileName.split("%2C");
		
		String[] onefileNames = fileName.split("%26"); // 不同主机的文件列表

		for (int i = 0; i < hostTotalNum; i++) {
			hostList.add(new ArrayList<String>());
		}
		Log.v("15", "hostTotalNum=" + hostTotalNum);

		// 将所有文件存入集合
		for (int i = 0; i < onefileNames.length; i++) {
			String oneListAllFile[] = onefileNames[i].split("%2C");
			for (int j = 0; j < oneListAllFile.length; j++) {
				hostList.get(i).add(oneListAllFile[j]);
				// Log.v("15", oneListAllFile[j]);
			}
		}
	}

	// 循环出所有可能的文件
	public void getAllFileList() {
		for (int i = 0; i < hostList.size(); i++) {
			int size = hostList.get(i).size();
			for (int j = 0; j < size; j++) {
				// Log.v("15",hostList.get(i).get(j)+":"+i+"-----");
				allFileList.add(new FileNum(i, j));
			}
		}
	}

	/*
	 * 判断是否是音频文件
	 */
	Handler ThreadHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (testUrl.toString().endsWith(".mp3") || playurl.toString().endsWith(".wav") || playurl.toString().endsWith(".wma")) {
				iv.setVisibility(View.VISIBLE);
			} else {
				iv.setVisibility(View.INVISIBLE);
			}
		};
	};
	public boolean playNomal=true;

	public class StatusReceiver extends BroadcastReceiver {
		private String result;

		@Override
		public void onReceive(Context context, Intent intent) {
			String actionStr = intent.getAction();
			Log.d("jiayy11", "actionStr = " + actionStr);
			if (Constant.PAUSE_ACTION.equals(actionStr)) {
				curPos = player.getCurrentPosition();
				player.pause();
				LayoutParams lp = (LayoutParams) surfaceView.getLayoutParams();
				lp.width = 1280;
				lp.height = 720;
				surfaceView.setLayoutParams(lp);
				new Thread(new Client("pause:true")).start();
				playNomal=false;
				
			}else if(Constant.MESSAGE_SETREALTIMECONTENT.equals(actionStr)){
				
				if(playNomal){
				 result= Constant.ORDER_ACTION + "getRealTimeContent" + Constant.ORDER_TYPE+"1"+ 
						Constant.ORDER_FILEPATH+  testUrl+Constant.ORDER_STATE+"play"
						+Constant.ORDER_PROGREEBAR+getNumber()+Constant.ORDER_CHANNEL+"null" ;
				}else{
					 result= Constant.ORDER_ACTION + "getRealTimeContent" + Constant.ORDER_TYPE+"1"+ 
							Constant.ORDER_FILEPATH+ testUrl+Constant.ORDER_STATE+"pause"
							+Constant.ORDER_PROGREEBAR+getNumber()+Constant.ORDER_CHANNEL+"null" ;
				}
				new Thread(new Client(result)).start();
			
			} else if (Constant.CONTINUE_PLAY_ACTION.equals(actionStr)) {
				if (player.isPlaying() == false) {
					player.start();
					playNomal=true;
				}
				new Thread(new Client("continuePlay:true")).start();
			} else if (Constant.SUBTITLE_ACTION.equals(actionStr)) {
				mScrollText.setVisibility(View.VISIBLE);
				final Bundle bundle = intent.getExtras(); //
				final String subTitle = bundle.getString(Constant.SUB_TITLE);
				mSubTitleShowTime = bundle.getString(Constant.SUB_TIME);
				Log.i(TAG, "subTime:" + mSubTitleShowTime);

				final Message msg = new Message();
				msg.what = MESSAGE_SHOW_TIME_START;
				mCheckNoteShowHandler.sendMessage(msg); // 字幕定时关闭

				DisplayMetrics dm = new DisplayMetrics();
				 getWindowManager().getDefaultDisplay().getMetrics(dm);
			     int width = dm.widthPixels;    //手机屏幕水平分辨率
			     int height = dm.heightPixels;  //手机屏幕垂直分辨率
			    Log.e("MainActivity","屏幕长宽比："+height+"|"+width);
			    if(width==720){
			    	mScrollText.setText("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + subTitle);
			    }else{
			    	mScrollText.setText("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + subTitle);
			    }
				new Thread(new Client("playSubTitle:true")).start();
			} else if (Constant.STOP_SUBTITLE_ACTION.equals(actionStr)) {
				mScrollText.setVisibility(View.INVISIBLE);
				new Thread(new Client("stopSubTitle:true")).start();
			} else if (STOPPLAY_ACTION.equals(actionStr)) {
				Log.i("TEST", "video_topPlay");
				isJudge = false;
				if (VideoShowActivity.this.isFinishing() == false) {
					VideoShowActivity.this.finish();
				}

				if (player != null) {
					player.stop();
				}
			} else if (Constant.MESSAGE_TYPE_ACTION.equals(actionStr)) { // 来自定时的播放
				mType = false;
				PreferenceUtil.putSharePreference(VideoShowActivity.this, Constant.MESSEGE_COMEFROM_ALARM, "true");
				Log.i(TAG, "定时类型");
			}
			// another_action 需要初始化变量太多不使用了
			else if (Constant.ANOTHERPLAY_ACTION.equals(actionStr)) {
			} else if (VOLUME_ACTION.equals(actionStr)) {
				String value = intent.getStringExtra("VOLUME_ACTION");
				Log.i("VOLUME", "接收到音量调节的命令,value=" + value);

				if (value.equals("up") && VOLUME_VALUE < 15) {
					VOLUME_VALUE++;
				} else if (value.equals("down") && VOLUME_VALUE > 0) {
					VOLUME_VALUE--;
				}
				Log.i(TAG, "当前音量值：" + VOLUME_VALUE);
				// 音量调节
				SoundCtrl(VOLUME_VALUE);
			} else if (GET_PROGRESS.equals(actionStr)) {
				Log.i(TAG, "中英文文件名解码：" + decode(fileName, "UTF-8"));
				new Thread(new Client("progressBar:" + getPercent() + "%,currentFile:" + decode(fileName, "UTF-8"))).start();
			} else if (Constant.MESSAGE_TYPE_ACTION.equals(actionStr)) { // 来自定时的播放
				mType = false;
				Log.i(TAG, "定时类型");
			} else if (GET_PERCENT.equals(actionStr)) {
				final Bundle bundle = intent.getExtras();
				final int getPercent = Integer.parseInt(bundle.getString(Constant.PERCENT_VALUE));
				Log.i(TAG, "获取的百分比为：" + getPercent);
				seekTo(getPercent);
			} else if (Constant.CHECKONLINE_ACTION.equals(actionStr)) {
				
				String isOnline = intent.getStringExtra("isOnline");
				if ("false".equals(isOnline)) {
//					JingMoOrder.postErrorLog(PreferenceUtil.getSharePreference(VideoShowActivity.this, Constant.SERVER_IP_PORT),
//							PreferenceUtil.getSharePreference(VideoShowActivity.this, Constant.TERMINAL_IP), "流媒体中断退回主界面");
					hasNet=false;
					if(coming==0){
						
						JingMoOrder.postErrorLog3(VideoShowActivity.this,"流媒体中断退回主界面",Constant.ERROR_MSG);
//						VideoShowActivity.this.finishAndGOtoIndex();
						Toast.makeText(VideoShowActivity.this, "网络中断,请尽快联网", Toast.LENGTH_SHORT).show();
						player.pause();
						coming=1;
					}
		
				}else{
					
					coming=0;
					
					if (player.isPlaying() == false && !hasNet) {
						player.start();
						hasNet=true;
						playNomal=true;
						
					}
				}
			}else if (GET_CURPERCENT.equals(actionStr)) {
				Log.i(TAG, "中英文文件名解码：" + decode(fileName, "UTF-8"));
				String filenames="";
				for (int i = nums; i <=(fileNums.length)-1; i++) {
					filenames=filenames+fileNums[i]+",";
				}
				Log.e("filenames", filenames);
				new Thread(new Clientdiff("progressBar:" + getPercent() + ",currentFile:" + decode(filenames, "UTF-8"))).start();
				Log.e("backStr", "progressBar:" + getPercent() + ",currentFile:" + decode(filenames, "UTF-8"));
			}
		}
	}

	private boolean hasNet=true;
	private int coming=0;
 
	/*
	 * 获取当前播放进度
	 * 
	 * @return 百分比
	 */
	public int getPercent() {
		final long number = player.getCurrentPosition();
		final int percent = (int) ((number) / (float) player.getDuration() * 100);
		return percent;
	}

	public long	getNumber(){
		final long number = player.getCurrentPosition();
		return number;
		
	}
	/*
	 * 将传过来的进度转换为秒数
	 */
	private void seekTo(final int percent) {
		double number = player.getDuration();
		double sTime = 0;
		sTime = (double) percent / 100 * number;
		player.seekTo((int) sTime);
		if(curPercent ==0){
			new Thread(new Client("jumpTo:true")).start();
		}		
		curPercent=0;
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
				Log.i(TAG, "showTime:" + showTime);
				break;
			}
		};
	};

	/*
	 * 音量调节
	 */
	private void SoundCtrl(int val) {
		final AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		// 最大音量
		final int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		if (val > maxVolume) {
			val = maxVolume;
		} else if (val < 0) {
			val = 0;
		}

		PreferenceUtil.putIntegerSharePreference(VideoShowActivity.this, Constant.VOLUME_NUM_VALUE, val);

		// 当前音量
		final int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, val, 0); // tempVolume:音量绝对值
		Log.i(TAG, "SoundCtrl_调整后的音量：" + val);
	}

	/*
	 * @Override public boolean onKeyDown(int keyCode, KeyEvent event) { // TODO
	 * Auto-generated method stub Log.i("key","KeyCode="+keyCode); return true;
	 * }
	 */
	Boolean isUnregisterReceiver = false;

	@Override
	protected void onPause() {
		super.onPause();
//		recoverVolume();
		String filenames="";
		for (int i = nums; i <=(fileNums.length)-1; i++) {
			if(i==(fileNums.length)-1){
				filenames=filenames+fileNums[i];
			}else{
				filenames=filenames+fileNums[i]+",";
			}
		}
		
		String str="{\"action\":\"playVideo\",\"data\":{\"serverIp\":\""+PreferenceUtil.getSharePreference(VideoShowActivity.this, Constant.VIDEO_IP)+"\",\"fileName\":\""+filenames+"\",\"port\":\""+PreferenceUtil.getSharePreference(VideoShowActivity.this, Constant.VIDEO_PORT)+"\",\"curPercent\":\""+getPercent()+"\"}}";
		Log.e("message", str);
//		if(helper.hasSD()){
//			helper.writeSDFile(str, "LogCode.txt");
//		}
		PreferenceUtil.putSharePreference(VideoShowActivity.this, Constant.CURRENT_VIDEO_PERCENT, str);
		
		isJudge = false;
		try {
			if (isUnregisterReceiver == false) {
				unregisterReceiver(mStatusReceiver);
				isUnregisterReceiver = true;
			}
		} catch (Exception e) {
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 停止播放
		if (player != null) {
			player.stop();
		}

		Log.v("z2", "退出");
		// unregisterReceiver(mStatusReceiver);
		/**
		 * 结束后台事件处理线程
		 */

		if (mCheckNoteShowHandler.hasMessages(MESSAGE_SHOW_TIME_START)) {
			mCheckNoteShowHandler.removeMessages(MESSAGE_SHOW_TIME_START);
		}
		if (mCheckNoteShowHandler.hasMessages(MESSAGE_SHOW_TIME_REACHED)) {
			mCheckNoteShowHandler.removeMessages(MESSAGE_SHOW_TIME_REACHED);
		}

		if (viewShowHandler.hasMessages(0)) {
			viewShowHandler.removeMessages(0);
		}
		if (viewShowHandler.hasMessages(1)) {
			viewShowHandler.removeMessages(1);
		}
		if (viewShowHandler.hasMessages(2)) {
			viewShowHandler.removeMessages(2);
		}
	}

	// 中文文件名解码
	public String decode(String value, String code) {
		try {
			return URLDecoder.decode(value, code);
		} catch (UnsupportedEncodingException e) {
			Log.i("Error", "Error:" + e.getMessage());
			e.printStackTrace();
		}
		return value;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (player != null) {
			player.release();
			player = null;
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		holder.setFixedSize(width, height); // 设置显示区域固定
	}

	private void play() {
		Log.e("FileNum", fileNums[nums]);
		// isJudge = true;
		Log.v(TAG, "play(),url=" + url);
		if (player != null) {
			player.reset(); 
			try {
				testUrl.replace("%2F", "/");
				Log.v("tets", "最终播放url:"+testUrl);
				player.setDataSource(testUrl);
				player.prepareAsync(); // 准备
				Log.e("curPercent", curPercent+"");
				if(0<curPercent && curPercent<100 ){
					new Thread(new  Runnable() {
						public void run() {
							try {
								Thread.sleep(5000);
							} catch (InterruptedException e) {
								
								e.printStackTrace();
							}
							seekTo(curPercent);	
							Log.e("curPercent", "走了"+curPercent);
						}
					}).start();					
					
				}
			} catch (IllegalArgumentException e1) {
				e1.printStackTrace();
			} catch (SecurityException e1) {
				e1.printStackTrace();
			} catch (IllegalStateException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		Log.v(TAG, "onCompletion");
		isJudge = false;
		if (player != null) {
			player.stop();
			Log.v("test", "视频结束");
		}
		nums++;
		if(nums>=fileNums.length){
			finishAndGOtoIndex();
		}else{
			playNext();
		}
		
	}

	// 播放下一个文件
	private void playNext() {
		isJudge = false;
		// 文件数增加
		fileNum++;
		// 有loop参数：1.当为true时循环 2.为random时随机播放一遍列表，并且不重复 3.为false时顺序播放一遍
		// 无loop参数：顺序播放一遍
		if ("true".equalsIgnoreCase(loop)) {
			if (fileNum == (hostList.get(hostNum % hostTotalNum).size())) {
				hostNum++;
				fileNum = 0;
			}
			testUrl = getPlayURL();
			play();
		} else if ("random".equalsIgnoreCase(loop)) {
			if (allFileList.isEmpty()) {
				VideoShowActivity.this.finish();
			} else {
				int i = random.nextInt(allFileList.size());
				hostNum = allFileList.get(i).getHostNum();
				fileNum = allFileList.get(i).getFileNum();
				Log.v("7.23", "----hostNum:" + hostNum + "-----fileNum:" + fileNum + "-----i=" + i + "----size" + allFileList.size());
				allFileList.remove(i);
				testUrl = getPlayURL();
				play();
			}

		} else if ("false".equalsIgnoreCase(loop)) {
			if (VideoShowActivity.this.isFinishing() == false && hostNum == (hostTotalNum - 1) && fileNum == (hostList.get(hostNum % hostTotalNum).size())) {
				VideoShowActivity.this.finish();
			} else {
				if (fileNum == (hostList.get(hostNum % hostTotalNum).size())) {
					hostNum++;
					fileNum = 0;
				}
				testUrl = getPlayURL();
				play();
			}
		} else {
			if (VideoShowActivity.this.isFinishing() == false && hostNum == (hostTotalNum - 1) && fileNum == (hostList.get(hostNum % hostTotalNum).size())) {
				VideoShowActivity.this.finish();
			} else {
				if (fileNum == (hostList.get(hostNum % hostTotalNum).size())) {
					hostNum++;
					fileNum = 0;
				}
				testUrl = getPlayURL();
				play();
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.v(TAG, "onKeyDown" + keyCode);
		if (keyCode == 4) {
			isJudge = false;
			VideoShowActivity.this.finishAndGOtoIndex();
		}
		return true;
	}

	//结束当前界面跳回主界面
	public void finishAndGOtoIndex() {
		finish();
		Intent intent = new Intent();
		intent.setClass(this, IndexActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		Constant.isManualStop = true;
	}
	

}
