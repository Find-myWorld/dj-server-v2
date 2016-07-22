package com.example.iimp_znxj_new2014.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.example.iimp_znxj_new2014.R;
import com.example.iimp_znxj_new2014.util.Constant;
import com.example.iimp_znxj_new2014.util.DownPlanDao;
import com.example.iimp_znxj_new2014.util.JingMoOrder;
import com.example.iimp_znxj_new2014.util.JingMoOrder.Client;
import com.example.iimp_znxj_new2014.util.VideoPlayer;

/*
 * 定时播放本地文件（音频+视频）
 */
public class PlayPlanLocalVideoActivity extends BaseActivity implements SurfaceHolder.Callback {
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private MediaPlayer mplayer;
	private VideoPlayer player;
	private StatusReceiver mStatusReceiver;
	private TextView mScrollText;
	private String filename;
	private SeekBar skbProgress;
	private Timer mTimer = new Timer();
	
	private Handler handler;
	
	private static final String PAUSE_ACTION = "com.example.iimp_znxj_new2014.pause";
	private static final String CONTINUE_PLAY_ACTION = "com.example.iimp_znxj_new2014.continueplay";
	private static final String SUBTITLE_ACTION = "com.example.iimp_znxj_new2014.subtitle";
	private static final String STOPPLAY_ACTION = "com.example.iimp_znxj_new2014.stopPlay";
	private static final String LOCAL_VIDEO_ACTIVITY_BNAME = "com.example.iimp_znxj_new2014.activity.VideoViewPlayingActivity";
	
	private DownPlanDao dao;
	private static final String PLAY_PLAN_TABLE = "playplan_t";
	private static final String TAG = "PlayPlanLocalVideoActivity";
	private String url;
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_localvideo);
		
		dao = new DownPlanDao(getApplicationContext());
		Cursor cs = dao.idQuery(getCurrentTime(),PLAY_PLAN_TABLE);
		while(cs.moveToNext()){
			url = cs.getString(0);
			filename = cs.getString(1);
			Log.i(TAG,"url="+url+"|"+filename);
		}
		cs.close();
		Log.i("End","定时计划1111,PlayPlanLocalVideoActivity，onCreate,"+url+",filename="+filename);
	//	JingMoOrder.fileDownload(getApplicationContext(), url, filename);
	//	dao.delete(getCurrentTime(),PLAY_PLAN_TABLE);  //删除已经启动的定时数据
	/*	surfaceView = (SurfaceView) this.findViewById(R.id.local_surface_view);
		surfaceView.setOnClickListener(new vidclick());   
		mScrollText = (TextView)findViewById(R.id.local_scroll_text_video);
		
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	
		mplayer = new MediaPlayer();

		skbProgress = (SeekBar) this.findViewById(R.id.local_seek_bar);
		skbProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());
		
		mStatusReceiver = new StatusReceiver();*/
	//	player = new VideoPlayer(surfaceView, skbProgress);
		/*
		if(JingMoOrder.isVideoShowActivityTop(PlayPlanLocalVideoActivity.this, LOCAL_VIDEO_ACTIVITY_BNAME)){
			Log.i(TAG,"当前正在播放，应发送播放另一个广播");
			
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString(Constant.PLAY_LOCAL_VIDEO_FILE_NAME, url);
			intent.setAction(Constant.ANOTHELOCALVIDEO_ACTION);
			intent.putExtras(bundle);
			sendBroadcast(intent);
			this.finish();
		} */
		
		
		IntentFilter filter = new IntentFilter();
	//	player.registerPlayListener(this);   //结束动作监听
		filter.addAction(PAUSE_ACTION);
		filter.addAction(CONTINUE_PLAY_ACTION);
		filter.addAction(SUBTITLE_ACTION);
		filter.addAction(STOPPLAY_ACTION);
		filter.addAction(Constant.STOP_SUBTITLE_ACTION);
		filter.addAction(Constant.ANOTHELOCALVIDEO_ACTION);
		filter.setPriority(Integer.MAX_VALUE);
//		registerReceiver(mStatusReceiver, filter); 
		
		//根据视频格式判断使用何种方式播放
		if(filename != null && !filename.equals("")){
			String fileSuffix[] = filename.split("\\.");
			Log.i("TTSS","Length:"+fileSuffix.length+"|"+fileSuffix[0]+"|"+fileSuffix[1]);
			
			if(fileSuffix[1].equals("mp3") || fileSuffix[1].equals("wav") || fileSuffix[1].equals("wma")){
				Log.i(TAG,"音频格式文件");
				Intent itto = new Intent(this, AudioActivity.class);
			//	itto.setData(Uri.parse("/mnt/extsd/Download/"+filename));
				itto.setData(Uri.parse(url));
				startActivity(itto);
				this.finish();
			}else{  //使用百度公司提供的视频播放库   
				final Intent itto = new Intent(this, VideoShowActivity.class);
				itto.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				itto.setData(Uri.parse(url));
				itto.putExtra("flag", false);
				startActivity(itto);
				this.finish();
			}
		}   
	}
	
	TimerTask mTimerTask = new TimerTask() {
		@Override
		public void run() {
			if (mplayer == null)
				return;
			if (mplayer.isPlaying() && skbProgress.isPressed() == false) {
				handleProgress.sendEmptyMessage(0);
			}
		}
	};
	
	Handler handleProgress = new Handler() {
		public void handleMessage(Message msg) {

			int position = mplayer.getCurrentPosition();
			int duration = mplayer.getDuration();

			if (duration > 0) {
				long pos = skbProgress.getMax() * position / duration;
				skbProgress.setProgress((int) pos);
			//	Log.i("TTS","=="+pos);
			}
		};
	};
	
	public void play(String filename){
		try {
		//	if(mplayer.isPlaying()){
		//		Log.i("Msg","stop");
		//		mplayer.stop();
			//	mplayer.release();
			//	mplayer.reset();
		//	}
			File file = new File("/mnt/extsd/Download/"+filename);
		//	File file = new File("/mnt/usbhost2/Download/"+filename);
			@SuppressWarnings("resource")
			FileInputStream fis = new FileInputStream(file);
			mplayer.setDataSource(fis.getFD());  
			mplayer.setDisplay(surfaceHolder);   //这里容易报错
			mplayer.prepare();
			mplayer.start();
			mTimer.schedule(mTimerTask, 0, 1000);
			Log.i("Msg","长度："+mplayer.getDuration());
			
			new Thread(new Client("playLocalVideo:true")).start();
		} catch (IllegalArgumentException e) {
			new Thread(new Client("playLocalVideo:false")).start();
			finish();
			e.printStackTrace();
		} catch (SecurityException e) {
			new Thread(new Client("playLocalVideo:false")).start();
			finish();
			e.printStackTrace();
		} catch (IllegalStateException e) {
			new Thread(new Client("playLocalVideo:false")).start();
			finish();
			e.printStackTrace();
		} catch (IOException e) {
			Log.i("TS","Error4:"+e.getMessage());
			new Thread(new Client("playLocalVideo:false,No such File")).start();
			finish();
			e.printStackTrace();
		}
	}
	
	public class SeekBarChangeEvent implements OnSeekBarChangeListener {
		private int progress;
		private int max = mplayer.getDuration();
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			this.progress = progress * mplayer.getDuration()
					/ seekBar.getMax();
			Log.i("TTS","1=="+progress+"|"+max+"|"+mplayer.getDuration()+"|"+seekBar.getMax());
			if(progress >= seekBar.getMax()-1){
				Log.i("TS","finish");
				finish();
			}
		}
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			Log.i("TTS","2=="+progress);

		}
		@Override
		public void onStopTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub
			Log.i("TS","onStopTrackingTouch");
			Log.i("TTS","3=="+progress);
			mplayer.seekTo(progress);
		}
	}
	
	public class vidclick implements OnClickListener {
		@Override
		public void onClick(View v) {
			Log.i("TS","vidclick");
		}
	}
	
	public class StatusReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String actionStr = intent.getAction();
			Log.d("Test","actionStr = "+actionStr);
			if ("com.example.iimp_znxj_new2014.pause".equals(actionStr)) {
				Log.i("TS","pause");
				if(mplayer.isPlaying()){
					mplayer.pause();
				}
				Log.i("TS","当前时长："+mplayer.getCurrentPosition());
				LayoutParams lp = (LayoutParams) surfaceView.getLayoutParams();
				lp.width = 1280;
				lp.height = 720;
				surfaceView.setLayoutParams(lp);
				new Thread(new Client("pause:true")).start();
			} else if ("com.example.iimp_znxj_new2014.continueplay"
					.equals(actionStr)) {
				Log.i("TS","continueplay");
				if(!mplayer.isPlaying()){
					mplayer.start();
				}
				Log.i("TS","当前时长："+mplayer.getCurrentPosition());
			//    play(filename);  //播放
				new Thread(new Client("continuePlay:true")).start();
			} else if ("com.example.iimp_znxj_new2014.subtitle"
					.equals(actionStr)) {
				Log.i("TS","subtitle");
				mScrollText.setVisibility(View.VISIBLE);
				Bundle bundle = intent.getExtras(); // 
				String subTitle = bundle.getString(Constant.SUB_TITLE);
				mScrollText.setText("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t"+subTitle);
				new Thread(new Client("playSubTitle:true")).start();
			} else if(Constant.STOP_SUBTITLE_ACTION.equals(actionStr)) {
				mScrollText.setVisibility(View.INVISIBLE);
				new Thread(new Client("stopSubTitle:true")).start();
			//	Log.i("TS","接收到关闭字幕的命令");
			} else if ("com.example.iimp_znxj_new2014.stopPlay"
					.equals(actionStr)) {
				Log.i("TS","stopPlay");
				if (mplayer != null) {
					mplayer.stop();
				}
				PlayPlanLocalVideoActivity.this.finish();
				Log.d("jiayy","startIndex in VideoShowActivity stopPlay");
				Intent stopIntent = new Intent(PlayPlanLocalVideoActivity.this,
						IndexActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(stopIntent);
				new Thread(new Client("stopPlay:true")).start();
			}else if (Constant.ANOTHELOCALVIDEO_ACTION
					.equals(actionStr)) {
				Bundle bundle = intent.getExtras();
				final String newfilename = bundle.getString(Constant.PLAY_LOCAL_VIDEO_FILE_NAME);
				if(mplayer != null){
					Log.i("Msg","广播，结束当前，重启新的");
					new Thread(new Runnable(){   //必须做延迟操作，给初始化更多的时间，不然报错
						public void run(){
							try{
								Thread.sleep(500);
								playAnother(newfilename);
							} catch (InterruptedException e){
								e.printStackTrace();
							}
						}
					}).start();
				}
				
			}
		}
	}
    
	public void playAnother(String afilename){
		if(mplayer != null){
			mplayer.stop();
			mplayer.setDisplay(null);
			
			mplayer.reset();
		//	mplayer.setDisplay(surfaceHolder);
			
			File file = new File("/mnt/extsd/Download/"+afilename);
			@SuppressWarnings("resource")
			FileInputStream fis;
			try {
				fis = new FileInputStream(file);
				mplayer.setDataSource(fis.getFD());  
				mplayer.setDisplay(surfaceHolder);   //这里容易报错
				mplayer.prepare();
				mplayer.start();
				mTimer.schedule(mTimerTask, 0, 1000);
				Log.i("Msg","长度："+mplayer.getDuration());
				
				new Thread(new Client("playLocalVideo:true")).start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.i("Error",""+e.getMessage());
				e.printStackTrace();
			}
			
			
		}
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i("End","PlayPlanActivity,onDestroy");
//		mplayer.stop();
	//	mplayer.release();  //Activity销毁时停止播放，释放资源（不做这个操作，退出后还能听到声音）
//		unregisterReceiver(mStatusReceiver);
	}  
	
	/*
	 * 获取当前时间
	 * @return tValue int
	 */
	public int getCurrentTime(){
		Calendar  current = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(1+"MMddHHmm");
		String timeStr = sdf.format(current.getTime());
		Log.i("Msg","Service中当前时间:"+timeStr);
		
		SimpleDateFormat sdfId = new SimpleDateFormat(1+"MMddHHmm");
		return Integer.parseInt(timeStr);
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
	//	mplayer = new MediaPlayer();
	//	mplayer.setDisplay(surfaceHolder);
		Log.i("Msg","Created");
	} 
	@Override
	public void surfaceChanged(SurfaceHolder surfaceHolder, int arg1, int w,
			int h) {
		Log.i("Msg","Changed");
		mplayer.setDisplay(surfaceHolder);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
