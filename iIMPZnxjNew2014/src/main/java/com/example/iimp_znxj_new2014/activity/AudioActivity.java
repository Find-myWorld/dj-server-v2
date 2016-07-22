package com.example.iimp_znxj_new2014.activity;

import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout.LayoutParams;

import com.example.iimp_znxj_new2014.R;
import com.example.iimp_znxj_new2014.activity.PlayLocalVideoActivity.StatusReceiver;
import com.example.iimp_znxj_new2014.util.Constant;
import com.example.iimp_znxj_new2014.util.JingMoOrder.Client;

/*
 * 音频播放
 */
public class AudioActivity extends BaseActivity implements OnCompletionListener{

	private static final String TAG = "AudioActivity";
	private String fileUrl = null;
	private MediaPlayer mediaPlayer = new MediaPlayer();
	private StatusReceiver mStatusReceiver;
	private static final String STOPPLAY_ACTION = "com.example.iimp_znxj_new2014.stopPlay";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audio);
		
		mediaPlayer.setOnCompletionListener(this);
		
		Log.i(TAG,"onCreate");
		Uri uriPath = getIntent().getData();
		if (null != uriPath) {
			String scheme = uriPath.getScheme();
			if (null != scheme) {
				fileUrl = uriPath.toString();
			} else {
				fileUrl = uriPath.getPath();
			}
		}

		Log.i(TAG,"文件路径："+fileUrl);
		
		if(mediaPlayer.isPlaying()){
			mediaPlayer.stop();
			mediaPlayer.reset();
		}
		try{
			mediaPlayer.setDataSource(fileUrl);
			mediaPlayer.prepare();
			mediaPlayer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		mStatusReceiver = new StatusReceiver();
	//	player = new VideoPlayer(surfaceView, skbProgress);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(STOPPLAY_ACTION);
		filter.addAction(Constant.STOP_SUBTITLE_ACTION);
		filter.addAction(Constant.ANOTHELOCALVIDEO_ACTION);
		filter.setPriority(Integer.MAX_VALUE);
		registerReceiver(mStatusReceiver, filter);
	}

	public class StatusReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String actionStr = intent.getAction();
			Log.d("Test","actionStr = "+actionStr);
		    if ("com.example.iimp_znxj_new2014.stopPlay"
					.equals(actionStr)) {
				Log.i(TAG,"stopPlay");
				if (mediaPlayer != null) {
					mediaPlayer.stop();
				}
				AudioActivity.this.finish();
			/*	Intent stopIntent = new Intent(AudioActivity.this,
						IndexActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(stopIntent);*/
				new Thread(new Client("stopPlay:true")).start();
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		Log.i(TAG,"onDestroy");
		mediaPlayer.release();
		super.onDestroy();
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		// TODO Auto-generated method stub
		Log.i(TAG,"onCompletion");
		this.finish();
	}
}
