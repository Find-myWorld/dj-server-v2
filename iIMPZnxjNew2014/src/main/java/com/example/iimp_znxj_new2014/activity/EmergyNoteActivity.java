package com.example.iimp_znxj_new2014.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.iimp_znxj_new2014.R;
import com.example.iimp_znxj_new2014.activity.CheckNoteActivity.StatusReceiver;
import com.example.iimp_znxj_new2014.util.BitmapUtil;
import com.example.iimp_znxj_new2014.util.Constant;
import com.example.iimp_znxj_new2014.util.JingMoOrder.Client;

/*
 * 紧急通知
 */
public class EmergyNoteActivity extends BaseActivity {
	private static final int MESSAGE_EMERGY_NOTE_PIC = 1;
	private static final int MESSAGE_SHOW_TIME_REACHED = 2;
	private static final int MESSAGE_EMERGY_NO_PIC = 3;
	private ImageView mEmergyNoteImageView;
	private String mEmergyNoteShowTime;
	private String mEmergyNoteContent;
	private String mUrl;
	private TextView mContentTextView;
	private AlertDialog myDialog = null;
	private PicThread mPicThread;

	private StatusReceiver mStatusReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.emergy_note);
		/*
		 * AlertDialog.Builder builder = new AlertDialog.Builder(this); View
		 * view = LayoutInflater.from(this).inflate(R.layout.dialog, null);
		 * builder.setView(view); myDialog = builder.create(); // myDialog = new
		 * AlertDialog.Builder(this).create(); //
		 * myDialog.getWindow().setContentView(R.layout.dialog);
		 * myDialog.show();
		 */
		Intent intent = this.getIntent(); 
		Bundle bundle = intent.getExtras(); 
		if (bundle != null) {
			String checkNoteIp = bundle
					.getString(Constant.EMERG_NOTE_SERVER_IP);
			String checkNoteFileName = bundle
					.getString(Constant.EMERG_NOTE_PIC_NAME);
			String port = bundle.getString(Constant.EMERG_NOTE_PORT);
			mEmergyNoteShowTime = bundle
					.getString(Constant.EMERG_NOTE_SHOW_TIME);
			
			mEmergyNoteContent = bundle.getString(Constant.EMERG_NOTE_CONTENT);
			mContentTextView = (TextView) findViewById(R.id.emergy_note_text);
			mEmergyNoteImageView = (ImageView) findViewById(R.id.emergy_note_pic);
			
			if (!TextUtils.isEmpty(checkNoteFileName)) {
				mEmergyNoteImageView.setVisibility(View.VISIBLE);
				mUrl = "http://" + checkNoteIp + ":" + port + "/"
						+ checkNoteFileName;
				mPicThread = new PicThread();
				mPicThread.start();
			} else {
				mEmergyNoteImageView.setVisibility(View.GONE);
				mContentTextView.setText(mEmergyNoteContent);
				
				Message msg = new Message();
				msg.what = MESSAGE_EMERGY_NO_PIC;
				mCheckNoteShowHandler.sendMessage(msg);
			}
		}
		
		mStatusReceiver = new StatusReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.STOPPLAY_ACTION);
		registerReceiver(mStatusReceiver, filter);
	}

	class PicThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				Bitmap bitmap = BitmapUtil.getImage(EmergyNoteActivity.this,
						mUrl);
				Message msg = new Message();
				msg.what = MESSAGE_EMERGY_NOTE_PIC;
				msg.obj = bitmap;
				mCheckNoteShowHandler.sendMessage(msg);

			} catch (Exception e) {
				new Thread(new Client("emergNote:false")).start();
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			super.run();
		}
	}

	private Handler mCheckNoteShowHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int showTime = Integer.parseInt(mEmergyNoteShowTime) * 60 * 1000;
			switch (msg.what) {
			case MESSAGE_SHOW_TIME_REACHED:
				EmergyNoteActivity.this.finish();
				Log.d("jiayy", "startIndex in EmergyNoteActivity");
				Intent intent = new Intent(EmergyNoteActivity.this,
						IndexActivity.class);
				startActivity(intent);
				break;
			case MESSAGE_EMERGY_NOTE_PIC:
				Bitmap bitmap = (Bitmap) msg.obj;
				if (!TextUtils.isEmpty(mEmergyNoteContent)) {
					mContentTextView.setVisibility(View.VISIBLE);
					mContentTextView.setText(mEmergyNoteContent);
				} else {
					mContentTextView.setVisibility(View.GONE);
				}
				new Thread(new Client("emergNote:true")).start();
				BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
				mEmergyNoteImageView.setBackgroundDrawable(bitmapDrawable);
				sendEmptyMessageDelayed(MESSAGE_SHOW_TIME_REACHED, showTime);
				break;
			case MESSAGE_EMERGY_NO_PIC:
				new Thread(new Client("emergNote:true")).start();
				sendEmptyMessageDelayed(MESSAGE_EMERGY_NO_PIC, showTime);
			}
		};
	};

	public class StatusReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String actionStr = intent.getAction();
			Log.d("Test","actionStr = "+actionStr);
			 if(Constant.STOPPLAY_ACTION.equals(actionStr)) {
				 EmergyNoteActivity.this.finish();
				 Intent intent2 = new Intent(EmergyNoteActivity.this,IndexActivity.class);
				 startActivity(intent2);
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d("jiayy", "mCheckNoteShowHandler.removeCallbacks in emergynoet");
		if (mCheckNoteShowHandler.hasMessages(MESSAGE_EMERGY_NOTE_PIC)) {
			mCheckNoteShowHandler.removeMessages(MESSAGE_EMERGY_NOTE_PIC);
		}
		if (mCheckNoteShowHandler.hasMessages(MESSAGE_SHOW_TIME_REACHED)) {
			mCheckNoteShowHandler.removeMessages(MESSAGE_SHOW_TIME_REACHED);
		}
		if (mCheckNoteShowHandler.hasMessages(MESSAGE_EMERGY_NO_PIC)) {
			mCheckNoteShowHandler.removeMessages(MESSAGE_EMERGY_NO_PIC);
		}
		mCheckNoteShowHandler.removeCallbacks(mPicThread);
	}

}
