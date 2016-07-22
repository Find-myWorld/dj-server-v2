package com.example.iimp_znxj_new2014.activity;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.http.Header;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iimp_znxj_new2014.Config;
import com.example.iimp_znxj_new2014.DianJiaoApplication;
import com.example.iimp_znxj_new2014.R;
import com.example.iimp_znxj_new2014.entity.LifeMenu;
import com.example.iimp_znxj_new2014.entity.LifeMenuAnalyze;
import com.example.iimp_znxj_new2014.selfconsume.FileHelper;
import com.example.iimp_znxj_new2014.util.Constant;
import com.example.iimp_znxj_new2014.util.PreferenceUtil;
import com.example.iimp_znxj_new2014.util.UnitaryCodeUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.wits.serialport.SerialPort;

/*
 * ViewPager实现
 * 沿用交互终端的“生活指南模块”，只显示每日菜谱
 */
public class LifeActivity extends BaseActivity {

	DianJiaoApplication gv;
	int lrint = 0, udint = 0;
	int itemId = 0;
	int i, i2 = 0;
	ImageView iv;
	// ////////////////////////////////////////////////////////////////
	private Context context = LifeActivity.this;
	private final static String TAG = "LifeActivity";
	private ViewPager viewPager;
	// private ImageView image1,image2,image3,image4;
	private TextView tvPage1, tvPage2, tvPage3, tvPage4, tvPage5, tvPage6;
	private ArrayList<View> views;
	
	String URL = null;
	String[] titles = new String[32];
	String[] time = new String[32];
	String[] foodmenus = new String[9];
	ArrayList<String> todaymenu=new ArrayList<String>();
	ArrayList<String> tomorrowmenu=new ArrayList<String>();
	int count = 0;

	TextView timestr;// 时间显示
	Handler handler; // 时间的更新
	SimpleDateFormat tformat, dformat, wformat;// 时间设置
	static final int MSG_TIME = 3;

	TextView cellTv; // 显示读取后的数据
	Properties prop;
	FileHelper helper;
	String serverIp, getIp, getPort, getCellNumber;;

	boolean keyFlag = true; // 屏蔽刷卡操作

	public static SerialPort mSerialPort;
	public static OutputStream mOutputStream;
	public static InputStream mInputStream;
	private StatusReceiver mStatusReceiver;
	private List<LifeMenu> newslist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_life);

		gv = (DianJiaoApplication) getApplication();

		getIp = PreferenceUtil.getSharePreference(LifeActivity.this,
				Constant.CONFIGURE_INFO_SERVERIP);
		getPort = PreferenceUtil.getSharePreference(LifeActivity.this,
				Constant.CONFIGURE_INFO_PORT);

		Log.e("getIp", getIp+getPort);
		
		serverIp = "http://" + getIp + ":" + getPort + "/";
		Log.e("serverIp", serverIp);
		URL = serverIp + Constant.SERVER_PART+"ForLiveDataXml.aspx";

		Log.e("URL", URL);
		doNetWork("foodmenu");

		fillImageButton(); // 初始化标题

		initViewPager(); // 初始化ViewPager [同时初始化了listview1，listview2]
		initPagerAdapter(); // 初始化PagerAdapter

		new Thread(new MyThread()).start(); // 时间更新相关的线程

		mStatusReceiver = new StatusReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.STOPPLAY_ACTION);
		registerReceiver(mStatusReceiver, filter);
	}

	public class MyThread implements Runnable { // thread
		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(1000); // sleep 1000ms
					Message message = new Message();
					message.what = MSG_TIME;
					myHandler.sendMessage(message);
				} catch (Exception e) {
				}
			}
		}
	}

	

	/*
	 * 功能：联网获取数据
	 */
	public void doNetWork(final String choice) {
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("requestCommand", choice);
		params.setContentEncoding("UTF-8");
		client.post(URL, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, Header[] headers,
					byte[] responseBody) {
				try {
					String getXmlStr = new String(responseBody, "UTF-8").trim(); // 获取xml
					Log.e("getXmlStr", "生活指南xml：" + getXmlStr);
					InputStream is = new ByteArrayInputStream(getXmlStr
							.getBytes());// 将xml转为InputStream类型
					showListView3(is); // 解析xml
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "连接服务器超时...",
						Toast.LENGTH_SHORT).show();
			}
		});

	}

	// 每日菜谱
	private void showListView3(InputStream is) {
		LifeMenuAnalyze service = new LifeMenuAnalyze();
		try {
			newslist = service.getLifeMenu(is);
			Log.e("newList", newslist.size()+"");
			
			for(LifeMenu news:newslist){
				foodmenus[0+count] = news.getTodayBreak();
				foodmenus[1+count] = news.getTodaylunch();
				foodmenus[2+count] = news.getTodayDiner();
				foodmenus[3+count] = news.getTomorBreak();
				foodmenus[4+count] = news.getTomorlunch();
				foodmenus[5+count] = news.getTomorDiner();
			//	Log.i("TS","count="+count+"|"+news.getTodayBreak()+"|"+news.getTodaylunch()+"|"+news.getTodayDiner()+"|"+news.getTomorBreak()+"|"+news.getTomorlunch()+"|"+news.getTomorDiner());
				count = 3;
			}
//			for (LifeMenu news : newslist) {
//				Log.e("TTSS", "菜单：" +count);
//				todaymenu.add(news.getTodayBreak());
//				todaymenu.add(news.getTodaylunch());
//				todaymenu.add(news.getTodayDiner());
//				tomorrowmenu.add( news.getTomorBreak());
//				tomorrowmenu.add(news.getTomorlunch());
//				tomorrowmenu.add(news.getTomorDiner());
//				
//			    Log.i("TTSS","count="+count+"|"+news.getTodayBreak()+"|"+news.getTodaylunch()+"|"+news.getTodayDiner()+"|"+news.getTomorBreak()+"|"+news.getTomorlunch()+"|"+news.getTomorDiner());
//				count = 3;// 0,1,2,3,4,5,   3,4,5,6,7,8
//			}
//			
//			for (int i = 0; i < tomorrowmenu.size(); i++) {
//				 Log.e("tomorrowmenu", "长度：" + tomorrowmenu.get(i+count));
//			}
			
			Log.e("todaymenu", "长度：" + todaymenu.size());
		    Log.e("tomorrowmenu", "长度：" + tomorrowmenu.size());
			
			viewPage3(); // 加载viewpage3
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	private void fillImageButton() {
		timestr = (TextView) this.findViewById(R.id.life_top_textView3);
		tformat = new SimpleDateFormat("HH:mm"); // 时：分
		dformat = new SimpleDateFormat("yyyy-MM-dd");// 年-月-日 星期
		wformat = new SimpleDateFormat("E");

		cellTv = (TextView) this.findViewById(R.id.life_top_textView2); // 更改监室号
		// String monitorNum = (String) prop.get("cellNumber");
		cellTv.setText("|       监房"
				+ PreferenceUtil.getSharePreference(LifeActivity.this,
						Constant.CONFIGURE_INFO_CELLNUMBER));

		int m = 0;
	}
	
	
	private void viewPage3(){
		if(newslist.size()==2){
			if(newslist.get(0).getTodayBreak() !=null){
				String[] str1 =newslist.get(0).getTodayBreak().trim().split("，");   //获取不了？？
				for(int i = 0;i < str1.length;i++){
			    	Log.i(TAG,"append........");
			     	tvPage1.append(str1[i]+"\n");	//此处为append，布局xml中不能有数据	   
			    }
			}
			if(newslist.get(0).getTodaylunch() != null){
				String[] str2 = newslist.get(0).getTodaylunch().trim().split("，");
				for(int i = 0;i < str2.length;i++){
			    	tvPage2.append(str2[i]+"\n");		   
			    }
			}
			if(newslist.get(0).getTodayDiner()!= null){
				String[] str3 = newslist.get(0).getTodayDiner().trim().split("，");
				for(int i = 0;i < str3.length;i++){
				    tvPage3.append(str3[i]+"\n");		   
				}		
			}
			if(newslist.get(1).getTomorBreak() != null){
				String[] str4 = newslist.get(1).getTomorBreak().trim().split("，");
			    for(int i = 0;i < str4.length;i++){
			    	tvPage4.append(str4[i]+"\n");		   
			    }
			}
			if(newslist.get(1).getTomorlunch()!= null){
				String[] str5 = newslist.get(1).getTomorlunch().trim().split("，");
				for(int i = 0;i < str5.length;i++){
			    	tvPage5.append(str5[i]+"\n");		   
			    }
			}
			if(newslist.get(1).getTomorDiner() != null){
				String[] str6 =newslist.get(1).getTomorDiner().trim().split("，");
				for(int i = 0;i < str6.length;i++){
			    	tvPage6.append(str6[i]+"\n");		   
			    }
			}
		}else{
			if(newslist.get(0).getTodayBreak() !=null){
				String[] str1 =newslist.get(0).getTodayBreak().trim().split("，");   //获取不了？？
				for(int i = 0;i < str1.length;i++){
			    	Log.i(TAG,"append........");
			     	tvPage1.append(str1[i]+"\n");	//此处为append，布局xml中不能有数据	   
			    }
			}
			if(newslist.get(0).getTodaylunch() != null){
				String[] str2 = newslist.get(0).getTodaylunch().trim().split("，");
				for(int i = 0;i < str2.length;i++){
			    	tvPage2.append(str2[i]+"\n");		   
			    }
			}
			if(newslist.get(0).getTodayDiner()!= null){
				String[] str3 = newslist.get(0).getTodayDiner().trim().split("，");
				for(int i = 0;i < str3.length;i++){
				    tvPage3.append(str3[i]+"\n");		   
				}		
			}
			if(newslist.get(0).getTomorBreak() != null){
				String[] str4 = newslist.get(0).getTomorBreak().trim().split("，");
			    for(int i = 0;i < str4.length;i++){
			    	tvPage4.append(str4[i]+"\n");		   
			    }
			}
			if(newslist.get(0).getTomorlunch()!= null){
				String[] str5 = newslist.get(0).getTomorlunch().trim().split("，");
				for(int i = 0;i < str5.length;i++){
			    	tvPage5.append(str5[i]+"\n");		   
			    }
			}
			if(newslist.get(0).getTomorDiner() != null){
				String[] str6 =newslist.get(0).getTomorDiner().trim().split("，");
				for(int i = 0;i < str6.length;i++){
			    	tvPage6.append(str6[i]+"\n");		   
			    }
			}
		}
	}

	/*
	 * view_page_3 分隔符逗号为中文逗号“，”
	 * 
	 * 
	 * 
	 */
//	private void viewPage3() {
//		Log.i("TTSS", "viewPage3:" + foodmenus[0]);
//		if (todaymenu.get(0) != null) {
//			String[] str1 = todaymenu.get(0).trim().split("，"); // 获取不了？？
//			for (int i = 0; i < str1.length; i++) {
//				Log.i(TAG, "append........");
//				tvPage1.append(str1[i] + "\n"); // 此处为append，布局xml中不能有数据
//			}
//		}
////		tvPage1.append("馒头"+"\n"+"炒芹菜");
//		if (todaymenu.get(1) != null) {
//			String[] str2 = todaymenu.get(1).trim().split("，");
//			for (int i = 0; i < str2.length; i++) {
//				tvPage2.append(str2[i] + "\n");
//			}
//		}
////		tvPage2.append("馒头2"+"\n"+"炒芹1菜"+"\n"+"小青菜");
//		if (todaymenu.get(2) != null) {
//			String[] str3 = todaymenu.get(2) .trim().split("，");
//			for (int i = 0; i < str3.length; i++) {
//				tvPage3.append(str3[i] + "\n");
//			}
//		}
////		tvPage3.append("馒头"+"\n"+"炒芹菜");
//		if (tomorrowmenu.get((0+count))  != null) {
//			String[] str4 = tomorrowmenu.get((0+count)).split("，");
//			for (int i = 0; i < str4.length; i++) {
//				tvPage4.append(str4[i] + "\n");
//			}
//		}
////		tvPage4.append("馒头"+"\n"+"炒芹菜");
//		if (tomorrowmenu.get((1+count))!= null) {
//			String[] str5 = tomorrowmenu.get((1+count)).trim().split("，");
//			for (int i = 0; i < str5.length; i++) {
//				tvPage5.append(str5[i] + "\n");
//			}
//		}
////		tvPage5.append("馒头"+"\n"+"炒芹菜");
//		if (tomorrowmenu.get((2+count)) != null) {
//			String[] str6 = tomorrowmenu.get((2+count)).trim().split("，");
//			for (int i = 0; i < str6.length; i++) {
//				tvPage6.append(str6[i] + "\n");
//			}
//		}
////		tvPage6.append("馒2头"+"\n"+"炒芹6菜");
//	}

	private void setPage3Empty() {
		tvPage1.setText("");
		tvPage2.setText("");
		tvPage3.setText("");
		tvPage4.setText("");
		tvPage5.setText("");
		tvPage6.setText("");
	}

	static class ViewHolder {
		private TextView points;
		private TextView titles;
		private ImageView images;
		private TextView texts;

		private TextView day;
		private TextView week;
		private ImageView img;
		private TextView wea;
		private TextView tmp;
	}

	private void initPagerAdapter() {
		PagerAdapter myPagerAdpter = new PagerAdapter() {
			// 判断是否由对象生成界面
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			// 获取当前窗体界面数
			@Override
			public int getCount() {
				return views.size();
			}

			// 销毁position位置的界面
			@Override
			public void destroyItem(View container, int position, Object object) {
				// TODO Auto-generated method stub
				((ViewPager) container).removeView(views.get(position));
			}

			// 初始化position位置的界面
			@Override
			public Object instantiateItem(ViewGroup container, int position) {
			
				((ViewPager) container).addView(views.get(position));
				return views.get(position);
			}
		};
		viewPager.setAdapter(myPagerAdpter);
		viewPager.setCurrentItem(0); // 设置当前界面（0,1,2,3）
	}

	/*
	 * 初始化ViewPager
	 */
	private void initViewPager() {
		viewPager = (ViewPager) findViewById(R.id.view_page);

		LayoutInflater inft = LayoutInflater.from(context);

		View view3 = inft.inflate(R.layout.view_page_3, null);
		tvPage1 = (TextView) view3.findViewById(R.id.view_page3_text1);
		tvPage2 = (TextView) view3.findViewById(R.id.view_page3_text2);
		tvPage3 = (TextView) view3.findViewById(R.id.view_page3_text3);
		tvPage4 = (TextView) view3.findViewById(R.id.view_page3_text4);
		tvPage5 = (TextView) view3.findViewById(R.id.view_page3_text5);
		tvPage6 = (TextView) view3.findViewById(R.id.view_page3_text6);

		views = new ArrayList<View>();
		views.add(view3);
	}
	
	public class StatusReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String actionStr = intent.getAction();
			Log.d(TAG,"actionStr = "+actionStr);
		    if (Constant.STOPPLAY_ACTION.equals(actionStr)) {
				Log.v(TAG,"stopPlay广播");
				changePage(0);
			}
		}
	}

	/*
	 * 页面跳转
	 */
	public void changePage(int menustr) {
		Intent intent = new Intent();// 新建一个activity
		intent.setClass(LifeActivity.this, IndexActivity.class);// 从本类的activity跳转到目标activity。
		startActivityForResult(intent, 0);// 执行目标activity
		this.finish();
	}
	
	@Override
	protected void onDestroy() {
		Log.v(TAG,"onDestroy");
		unregisterReceiver(mStatusReceiver);//可能报错
		super.onDestroy();
	}
	
	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				int keyCode = msg.arg2;
				Log.i(TAG, "Handler,接收到KeyCode=" + keyCode);
				if (keyCode == gv.getmKey_Esc()) {
					changePage(0);
				}
				break;
			case 2:
				// 刷卡操作
				break;
			case MSG_TIME:
				String d = dformat.format(new Date()); // 设置首页时间与日期
				String t = tformat.format(new Date());
				String w = wformat.format(new Date());
				timestr.setText(d + " " + t + " " + w);
			}
			super.handleMessage(msg);
		}
	};
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		final int key = event.getKeyCode();
		Log.i("TTS", "onKeyDown---->" + key);
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			int code = UnitaryCodeUtil.UnitaryCode(key);
			if (gv.getmKey_Esc() == code) {
				changePage(0);
			}
		}
		return true;
	}

	@Override
	public void serial_KeyDo(int keyCode) {
		
		super.serial_KeyDo(keyCode);
		Message msg = new Message();
		msg.what = 1;
		msg.arg2 =keyCode;
		myHandler.sendMessage(msg);
	}

	@Override
	public void serial_CardDo(String cardNum) {
		
		super.serial_CardDo(cardNum);
	}
	
	
	
}
