package com.example.iimp_znxj_new2014.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.example.iimp_znxj_new2014.R;
import com.example.iimp_znxj_new2014.service.UdpService;
import com.example.iimp_znxj_new2014.util.Constant;
import com.example.iimp_znxj_new2014.util.VideoPlayer;
import com.example.iimp_znxj_new2014.util.VideoPlayer.PlayListener;

/*
 * 播放计划（未采用）
 */
public class VideoShowPlanActivity extends BaseActivity implements PlayListener {
	private SurfaceView surfaceView;
	private Button btnPause, btnPlayUrl, btnStop;
	private SeekBar skbProgress;
	private VideoPlayer player;
	private int curPos = 0;
	private String playVideoBeginTime;
	private String playVideoEndTime;
	private static final int MESSAGE_CHECK_VIDEO_PLAN_VALUE = 1;
	private static final int CHECK_DAILY_LIFE_TIME = 30 * 1000;
	private static final String DESTROY_ACTION = "com.example.iimp_znxj_new2014.destroy";
	private String url;
	private StatusReceiver mStatusReceiver;
	private static final String PAUSE_ACTION = "com.example.iimp_znxj_new2014.pause";
	private static final String CONTINUE_PLAY_ACTION = "com.example.iimp_znxj_new2014.continueplay";
	private static final String SUBTITLE_ACTION = "com.example.iimp_znxj_new2014.subtitle";
	private static final String STOPPLAY_ACTION = "com.example.iimp_znxj_new2014.stopPlay";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = this.getIntent(); // ��ȡ���е�intent����
		Bundle bundle = intent.getExtras(); // ��ȡintent�����bundle����
		String playIp = bundle.getString(Constant.PLAY_VIDEO_PLAN_IP);
		playVideoBeginTime = bundle
				.getString(Constant.PLAY_VIDEO_PLAN_BEGIN_TIME);
		String playFileName = bundle
				.getString(Constant.PLAY_VIDEO_PLAN_FILE_NAME);
		String playPort = bundle.getString(Constant.PLAY_VIDEO_PLAN_PORT);
		url = "http://" + playIp + ":" + playPort + "/" + playFileName;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video);
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		surfaceView = (SurfaceView) this.findViewById(R.id.surface_view);
		surfaceView.setOnClickListener(new vidclick());
		skbProgress = (SeekBar) this.findViewById(R.id.seek_bar);
		skbProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());
		Log.d("jiayy", "play plan url = " + url);
		player = new VideoPlayer(surfaceView, skbProgress, url,true);
		mStatusReceiver = new StatusReceiver();
		player.registerPlayListener(this);
		IntentFilter filter = new IntentFilter();
		filter.addAction(DESTROY_ACTION);
		filter.addAction(PAUSE_ACTION);
		filter.addAction(CONTINUE_PLAY_ACTION);
		filter.addAction(SUBTITLE_ACTION);
		filter.addAction(STOPPLAY_ACTION);
		filter.addAction(Constant.STOP_SUBTITLE_ACTION);
		filter.setPriority(Integer.MAX_VALUE);
		registerReceiver(mStatusReceiver, filter);
		// player.playUrl(url);

	}

	public class vidclick implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

		}

	}

	public class SeekBarChangeEvent implements OnSeekBarChangeListener {

		private int progress;

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			this.progress = progress * player.mediaPlayer.getDuration()
					/ seekBar.getMax();
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			player.mediaPlayer.seekTo(progress);
		}

	}

	public class ClickEvent implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v == btnPause) {
				curPos = player.mediaPlayer.getCurrentPosition();
				player.pause();
				LayoutParams lp = (LayoutParams) surfaceView.getLayoutParams();
				lp.width = 1280;
				lp.height = 720;
				surfaceView.setLayoutParams(lp);

			} else if (v == btnPlayUrl) {
				String url = "http://192.168.1.253/1.mp4";
				player.playUrl(url);

			} else if (v == btnStop) {
				player.play();
				player.mediaPlayer.seekTo(curPos);

			}
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (player != null) {
			player.stop();
		}
		super.onDestroy();
	}

	public void surfaceChanged(SurfaceHolder surfaceHolder, int arg1, int w,
			int h) {
		player.mediaPlayer.setDisplay(surfaceHolder);
	}

	@Override
	public void onPlayFinished(String result) {
		// TODO Auto-generated method stub
		if (Constant.PLAY_COMPLETE.equals(result)) {
			if (player != null) {
				player.stop();
			}
			this.finish();
		} else {
			Intent udpIntent = new Intent(this, UdpService.class);
			udpIntent.setAction(Constant.ORDER_RESULT_ACTION);
			udpIntent.putExtra(Constant.ORDER_RESULT_EXTRA, result);
		}
	}

	public class StatusReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String actionStr = intent.getAction();
			if ("com.example.iimp_znxj_new2014.destroy".equals(actionStr)) {
				VideoShowPlanActivity.this.finish();
				Intent indexIntent = new Intent(context,
						VideoShowPlanActivity.class);
				context.startActivity(indexIntent);
			}
			if ("com.example.iimp_znxj_new2014.pause".equals(actionStr)) {
				curPos = player.mediaPlayer.getCurrentPosition();
				player.pause();
				LayoutParams lp = (LayoutParams) surfaceView.getLayoutParams();
				lp.width = 1280;
				lp.height = 720;
				surfaceView.setLayoutParams(lp);
			} else if ("com.example.iimp_znxj_new2014.continueplay"
					.equals(actionStr)) {
				player.play();
				player.mediaPlayer.seekTo(curPos);
			} else if ("com.example.iimp_znxj_new2014.stopPlay"
					.equals(actionStr)) {
				if (player != null) {
					player.stop();
				}
				VideoShowPlanActivity.this.finish();
				Log.d("jiayy","startIndex in VideoShowPlanActivity");
				Intent stopIntent = new Intent(VideoShowPlanActivity.this,
						IndexActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(stopIntent);
			}
		}
	}
}
