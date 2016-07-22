package com.example.iimp_znxj_new2014.activity;

import java.util.Locale;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iimp_znxj_new2014.R;

/*
 * 实现功能：弹出临时通知对话框、对话框弹出时间随内容字数而变
 * 亮屏息屏操作（如果屏幕是暗的，接收到广播后会点亮屏幕，对话框关闭后屏幕熄灭；如果屏幕是亮的，接收到广播，对话框关闭后，屏幕还是亮的）
 */
public class PopActivity extends BaseActivity implements OnInitListener{
	private int reclen = 8;   //设置倒计时的秒数
	private int compare = 0;  //用做比较
	private TextView tvPop;
	private TextView tvTime;
	private TextView tvTitle;
	private PowerManager.WakeLock wakeLock;
	private static final String TAG = "PopActivity";
	
	private TextToSpeech tts;
	private String getContent;
	private int getValue;
	private String[] titleArray = {"临时播报","值班提醒"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pop);															

		tvPop = (TextView)findViewById(R.id.text_pop);
		tvTime = (TextView)findViewById(R.id.text_time);
		tvTitle = (TextView)findViewById(R.id.text_show);
		tts = new TextToSpeech(this, this);
		
		Log.i(TAG,"onCreate1");
		
		getContent = this.getIntent().getStringExtra("content");  //获取service传过来的数据
		getValue = this.getIntent().getIntExtra("pop_value", 0);
		
		Log.i("TTSS","getValue1="+getValue);
		int i = getValue > 0 ? 1:0;
		Log.i("TTSS","getValue2="+getValue);
		
		tvTitle.setText(titleArray[getValue]);
		
		
		tvPop.setText("\t\t"+getContent);                   //在窗口中显示内容
		reclen = ( getContent.length() / 4 + 3 ) * 2;       //计算对话框的显示时间
		compare = reclen - 1;
		
		Message message = handler.obtainMessage(1);
		handler.sendMessageDelayed(message, 1000);
		
		Log.i(TAG,"onCreate2");
		
	}
	
	@Override
	protected void onStart() {  //点亮屏幕
		PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);   
		wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP|PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");///
		wakeLock.acquire();
		super.onStart();
	}

	final Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 1:
				reclen--;
				tvTime.setText(reclen+" 后窗口自动关闭");
				if(reclen == compare){
					tts.speak(getContent+","+getContent, TextToSpeech.QUEUE_FLUSH, null);
				}
				if(reclen > 0){
					Message message = handler.obtainMessage(1);
					handler.sendMessageDelayed(message, 1000);
				}else{
					finish();
				}
			}
			super.handleMessage(msg);
		}
	};
	@Override
	protected void onDestroy() {
		wakeLock.release();
		super.onDestroy();
		if(tts != null){
			tts.shutdown();
		}
	}

	/*
	 * 按键盘上‘Esc键’，弹出的窗口不会退出
	 */
	/*@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(gv.getmKey_Esc() == keyCode){
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}*/

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS)
		{
			int result = tts.setLanguage(Locale.CHINA);
			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED)
			{
				Toast.makeText(PopActivity.this,"Language is not available.",Toast.LENGTH_LONG).show();
			}
		}
	}
	
}
	


