package com.example.iimp_znxj_new2014.util;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.example.iimp_znxj_new2014.activity.VideoShowActivity;
import com.example.iimp_znxj_new2014.activity.VideoViewPlayingActivity;
import com.example.iimp_znxj_new2014.processor.ParseProcessor.ParseListener;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.SeekBar;

public class VideoPlayer implements OnBufferingUpdateListener,
		OnCompletionListener, MediaPlayer.OnPreparedListener,
		SurfaceHolder.Callback {
	private int videoWidth;
	private int videoHeight;
	public MediaPlayer mediaPlayer;
	private SurfaceHolder surfaceHolder;
	private SeekBar skbProgress;
	private Timer mTimer = new Timer();
	private String url;
	private PlayListener mPlayListener;
	private Boolean isAlarm = true;

	public VideoPlayer(SurfaceView surfaceView, SeekBar skbProgress,
			String playUrl,boolean flag) {
		this.skbProgress = skbProgress;
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		// surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		url = playUrl;
		mTimer.schedule(mTimerTask, 0, 1000);
		
		isAlarm = flag;  //记录是否定时播
	}
	
	public VideoPlayer(SurfaceView surfaceView,SeekBar skbProgress) {
		this.skbProgress = skbProgress;
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		mTimer.schedule(mTimerTask, 0, 1000);
	} 

	/*******************************************************
	 * ͨ��ʱ����Handler�����½����
	 ******************************************************/
	TimerTask mTimerTask = new TimerTask() {
		@Override
		public void run() {
			if (mediaPlayer == null)
				return;
			if (mediaPlayer.isPlaying() && skbProgress.isPressed() == false) {
				handleProgress.sendEmptyMessage(0);
			}
		}
	};

	Handler handleProgress = new Handler() {
		public void handleMessage(Message msg) {

			int position = mediaPlayer.getCurrentPosition();
			int duration = mediaPlayer.getDuration();

			if (duration > 0) {
				long pos = skbProgress.getMax() * position / duration;
				skbProgress.setProgress((int) pos);
				Log.i("TTS","=="+pos);
			}
		};
	};

	// *****************************************************

	public void play() {
		mediaPlayer.start();
	}

	public void playUrl(String videoUrl) {
		try {
			Log.d("jiayy","playUrl");
			mediaPlayer.reset();
			mediaPlayer.setDataSource(videoUrl);
			//mediaPlayer.prepare();// prepare֮���Զ�����
			mediaPlayer.prepare();
//			mediaPlayer.setLooping(true);
			// mediaPlayer.start();
			if(isAlarm){
				mPlayListener.onPlayFinished(Constant.ORDER_PLAYVIDEO_OK);
			}
		} catch(Exception e){
			e.printStackTrace();
			mPlayListener.onPlayFinished(Constant.ORDER_PLAYVIDEO_FAILER);
		} /*catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mPlayListener.onPlayFinished(Constant.ORDER_PLAYVIDEO_FAILER);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mPlayListener.onPlayFinished(Constant.ORDER_PLAYVIDEO_FAILER);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mPlayListener.onPlayFinished(Constant.ORDER_PLAYVIDEO_FAILER);
		}  */
	}

	public void playAnotherUrl(String videoUrl) {
		try {
			mediaPlayer.reset();
			mediaPlayer.setDisplay(surfaceHolder);
			mediaPlayer.setDataSource(videoUrl);
			mediaPlayer.prepare();// prepare֮���Զ�����
//			mediaPlayer.setLooping(true);
			// mediaPlayer.start();
			if(isAlarm){
				mPlayListener.onPlayFinished(Constant.ORDER_PLAYVIDEO_OK);
			}
		//	mPlayListener.onPlayFinished(Constant.ORDER_ACTION + "play,"
		//			+ Constant.ORDER_RESULT_OK);
		} catch(Exception e){
			mPlayListener.onPlayFinished(Constant.ORDER_PLAYVIDEO_FAILER);
			e.printStackTrace();
		}
		/*catch (IllegalArgumentException e) {
		}
			// TODO Auto-generated catch block
			e.printStackTrace();
			mPlayListener.onPlayFinished(Constant.ORDER_PLAYVIDEO_FAILER);
		//	mPlayListener.onPlayFinished(Constant.ORDER_ACTION + "play,"
		//			+ Constant.ORDER_RESULT_FAILER);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mPlayListener.onPlayFinished(Constant.ORDER_PLAYVIDEO_FAILER);
		//	mPlayListener.onPlayFinished(Constant.ORDER_ACTION + "play,"
		//			+ Constant.ORDER_RESULT_FAILER);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mPlayListener.onPlayFinished(Constant.ORDER_PLAYVIDEO_FAILER);
		//	mPlayListener.onPlayFinished(Constant.ORDER_ACTION + "play,"
		//			+ Constant.ORDER_RESULT_FAILER);
		} */
	}
	public void pause() {
		mediaPlayer.pause();
	}

	public void resume() {

	}

	public void stop() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	public void playAnother(String url) {
		Log.d("jiayy11","media play another play000000 url = "+url);
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.setDisplay(null);
			playAnotherUrl(url);
		}   
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		Log.e("mediaPlayer", "surface changed");
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		try {
			Log.d("jiayy", "surfaceCreate");
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setDisplay(surfaceHolder);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnBufferingUpdateListener(this);
			mediaPlayer.setOnPreparedListener(this);
			mediaPlayer.setOnCompletionListener(this);
			playUrl(url);
		} catch (Exception e) {
			Log.e("mediaPlayer", "error", e);
			mPlayListener.onPlayFinished(Constant.ORDER_PLAYVIDEO_FAILER);
		}
		Log.e("mediaPlayer", "surface created");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		Log.e("mediaPlayer", "surface destroyed");
	}

	@Override
	/**
	 * ͨ��onPrepared����
	 */
	public void onPrepared(MediaPlayer arg0) {
		videoWidth = mediaPlayer.getVideoWidth();
		videoHeight = mediaPlayer.getVideoHeight();
		if (videoHeight != 0 && videoWidth != 0) {
			arg0.start();
		}
		Log.e("mediaPlayer", "onPrepared");
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		// TODO Auto-generated method stub
		Log.d("jiayy", "onCompletion");
		mPlayListener.onPlayFinished(Constant.PLAY_COMPLETE);
	}

	@Override
	public void onBufferingUpdate(MediaPlayer arg0, int bufferingProgress) {
		skbProgress.setSecondaryProgress(bufferingProgress);
		int currentProgress = skbProgress.getMax()
				* mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
		Log.e(currentProgress + "% play", bufferingProgress + "% buffer");

	}

	public interface PlayListener {
		void onPlayFinished(String result);
	}

	public void registerPlayListener(PlayListener listener) {
		mPlayListener = listener;
		Log.i("TS","registerPlayListener");
	}
}
