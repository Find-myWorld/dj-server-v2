package com.example.iimp_znxj_new2014.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.iimp_znxj_new2014.DianJiaoApplication;
import com.example.iimp_znxj_new2014.R;
import com.example.iimp_znxj_new2014.util.Constant;

public class BaseActivity extends Activity {
	private ReadSerialBroadCastReceiver receiver;
	private boolean haveUnregister = false;
	
	public LayoutInflater layoutInflater;
	public View myLoginView;
	public Dialog alertDialog;

	private static final String TAG = "BaseActivity";
	// protected ImageLoader imageLoader = ImageLoader.getInstance();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 隐藏应用程序的标题栏，即当前activity的标题栏
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		DianJiaoApplication.getInstance().addActivity(this);
		
		layoutInflater = LayoutInflater.from(this);
		myLoginView = layoutInflater.inflate(R.layout.process, null);
		alertDialog = new AlertDialog.Builder(this).setView(myLoginView)
				.create();

		Log.i(TAG,"onCreate");
		try {
			IntentFilter filter = new IntentFilter();
			filter.addAction(Constant.SERIAL_ACTION_CARDNUM);
			filter.addAction(Constant.SERIAL_ACTION_KEYCODE);
			receiver = new ReadSerialBroadCastReceiver();
			registerReceiver(receiver, filter);
		} catch (Exception e) {
			
		}
		
	}
	
	public void showProcessDialog() {
		alertDialog.show();
		WindowManager.LayoutParams lp = alertDialog.getWindow().getAttributes();
		lp.width = 250;
		alertDialog.getWindow().setAttributes(lp);
	}

	public void closeProcessDialog() {
		if (alertDialog.isShowing())
			alertDialog.dismiss();
	}

	public class ReadSerialBroadCastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Constant.SERIAL_ACTION_KEYCODE.equals(action)) {
				Log.i(TAG,"ReadSerialBroadCastReceiver=>SERIAL_ACTION_KEYCODE");
				serial_KeyDo(intent.getExtras().getInt("keyCode"));
			} else if (Constant.SERIAL_ACTION_CARDNUM.equals(action)) {
				Log.i(TAG,"ReadSerialBroadCastReceiver=>SERIAL_ACTION_CARDNUM");
				serial_CardDo(intent.getExtras().getString("cardNum"));
			}
		}

	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		return true;
	}

	public void serial_KeyDo(int keyCode) {

	}
	
	public void serial_KeyAndEventDo(int keyCode,KeyEvent event) {

	}

	public void serial_CardDo(String cardNum) {

	}

	@Override
	protected void onPause() {

		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		Log.i(TAG,"onDestroy");
	}

	@Override
	protected void onStop() {

		super.onStop();
		Log.i(TAG,"BaseActivity.onStop");
		/*if (!haveUnregister) {
			Log.i(TAG,"onStop,unregisterReceiver");
			unregisterReceiver(receiver);
			haveUnregister = true;
		}*/
	}
	
}
