package com.example.iimp_znxj_new2014.activity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;

import com.example.iimp_znxj_new2014.DianJiaoApplication;
import com.example.iimp_znxj_new2014.R;
import com.example.iimp_znxj_new2014.entity.AgentInfo;
import com.example.iimp_znxj_new2014.entity.AgentInfoList;
import com.example.iimp_znxj_new2014.entity.BedInfo;
import com.example.iimp_znxj_new2014.entity.BedInfoList;
import com.example.iimp_znxj_new2014.entity.DutyInfo;
import com.example.iimp_znxj_new2014.entity.ThreeGdZrAnalyze;
import com.example.iimp_znxj_new2014.util.AlarmUtil;
import com.example.iimp_znxj_new2014.util.Constant;
import com.example.iimp_znxj_new2014.util.PreferenceUtil;
import com.example.iimp_znxj_new2014.util.UnitaryCodeUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.wits.serialport.SerialPort;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/*
 * @title 三固定信息
 */
public class ThreeGdActivity extends BaseActivity {

	DianJiaoApplication gv;
	ViewPager mPager;
	private List<View> listViews; // Tab页面列表
	int i = 0;
	int lrint = 0, udint = 0;
	int[] Rid = new int[] { R.id.three_top_imageView1, R.id.three_top_imageView2, R.id.three_top_imageView3 };
	int[] Rdraw = new int[] { R.drawable.three_pw, R.drawable.three_zb, R.drawable.three_zr };
	int[] Rdrawsel = new int[] { R.drawable.three_pw_h, R.drawable.three_zb_h, R.drawable.three_zr_h };
	ImageView image[] = new ImageView[3]; // 二维数组定义
	private View oldv = null;

	GridView gview = null;
	ListView list;
	ListView list1;

	TextView timestr;// 时间显示
	// Handler handler; //时间的更新
	SimpleDateFormat tformat, dformat, wformat;// 时间设置
	static final int MSG_TIME = 3;

	TextView cellTv; // 显示读取后的数据
	String serverIp;

	private static final String TAG = "ThreeGdActivity";
	// private String URL =
	// "http://192.168.1.253:6700/androidadmin/webservice/ThreeInfoDataXml.aspx";
	private String[] URL = new String[3];
	String[] names = new String[20]; // 姓名 固定20 个
//	String[] numbers = new String[20]; // 编号 固定20 个
//	String[] zbTimes = { "23:00-1:00", "1:00-3:00", "3:00-5:00", "5:00-7:00" };
//	String[] zbRemark = { "无", "无", "无", "无" }; // 值班备注
	ArrayList<String> zb_TimeList = new ArrayList<String>();
	ArrayList<String> zb_PersonList = new ArrayList<String>();
	ArrayList<String> zb_RemarkList = new ArrayList<String>();
	
	ArrayList<String> nameList=new ArrayList<String>();
	
	String[] splitRemark = new String[5];
//	String[] zbPerson = new String[5]; // 值班人，5组人
//	String[] zrItem = new String[5]; // 值日项目
//	String[] zrPerson = new String[5]; // 值日人
//	String[] zrRemark = new String[5]; // 值日备注
	
	ArrayList<String> zrIlist=new ArrayList<String>();
    ArrayList<String> zrPlist=new ArrayList<String>();
    ArrayList<String> zrMlist=new ArrayList<String>();
	int count = 0;

	String getIp, getPort, getCellNumber;

	boolean keyFlag = true; // 屏蔽刷卡操作

	public static SerialPort mSerialPort;
	public static OutputStream mOutputStream;
	public static InputStream mInputStream;
	private boolean isSwingCard = false;
	private boolean isFinished = false;

	private StatusReceiver mStatusReceiver;

	int zbCount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_three_gd);

		gv = (DianJiaoApplication) getApplication();

		getIp = PreferenceUtil.getSharePreference(ThreeGdActivity.this, Constant.CONFIGURE_INFO_SERVERIP);
		getPort = PreferenceUtil.getSharePreference(ThreeGdActivity.this, Constant.CONFIGURE_INFO_PORT);

		serverIp = "http://" + getIp + ":" + getPort + "/";
		String roomId=PreferenceUtil.getSharePreference(ThreeGdActivity.this,Constant.CONFIGURE_INFO_CELLNUMBER);
		Log.e(TAG, "roomId=" + roomId);
		URL = new String[]{serverIp+Constant.SERVER_PART+"getbedinfo.aspx?cid="+roomId,
				serverIp+Constant.SERVER_PART+"getagentinfo.aspx?cid="+roomId,
				serverIp+Constant.SERVER_PART+"getdutyinfo.aspx?cid="+roomId};
		Log.e(TAG, "URL=" + URL[2]);

		fillImageButton();
		image[0].setImageResource(Rdrawsel[0]);

		doNetWork();

		InitViewPager();
		InitPage2();
		InitPage3();

		new Thread(new MyThread()).start(); // 时间更新相关的线程

		mStatusReceiver = new StatusReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.STOPPLAY_ACTION);
		registerReceiver(mStatusReceiver, filter);
	}

	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				int keyCode = msg.arg2;
				Log.i(TAG, "Handler,接收到KeyCode=" + keyCode);
				if (!isFinished && !isPaused) { // 没有结束才执行下一步
					ProcessKeyFunc(keyCode);
				} else {
					Log.i(TAG, "已经结束，本页面以关闭，按键无响应");
				}
				// isSwingCard = true; //刷卡后，屏蔽取消键
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
	public void doNetWork() {
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
//		params.put("requestCommand", choice);
		params.setContentEncoding("UTF-8");
		client.post(URL[lrint], params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, Header[] headers, byte[] responseBody) {
				try {
					String getXmlStr = new String(responseBody, "UTF-8").trim(); // 获取xml
					Log.e("XML 显示","getXmlStr="+getXmlStr);
					InputStream is = new ByteArrayInputStream(getXmlStr.getBytes());// 将xml转为InputStream类型
					showListView(is); // 解析xml
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				Toast.makeText(getApplicationContext(), "网络延时，请稍候......", Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void showListView(InputStream is) {
		if (lrint == 0) {
			showListView1(is);
		} else if (lrint == 1) {
			Log.e("lrint", lrint+"");
			showListView2(is);
		} else if (lrint == 2) {
			Log.e("lrint", lrint+"");
			showListView3(is);
//			Log.e(TAG, "URL=" + URL[lrint]);
		}
	}

	/*
	 * 功能：解析xml
	 */
	public void showListView1(InputStream is) {
//		Log.i("TTSS","showListView1");
//		for(int i = 0;i < names.length; i++){
//			names[i] = null;
//		}
//		BedInfo info = null;
//		ThreeGdPwAnalyze infolist = null;
//		try {
//			if (is != null) {
//				infolist = ThreeGdPwAnalyze.getThreeGdPw(is);
//				is.close();
//			}
//			Log.i("TTSS","长度==>"+infolist.m_list.size());
//			for(int i = 0;i < infolist.m_list.size();i++){
//				info = infolist.m_list.get(i);
//				names[Integer.parseInt(info.m_id) - 1] = info.m_name;
//				Log.i("TTSS","参数："+info.m_name+",ID="+info.m_id);
//			}
//			for(int i=0;i<names.length;i++){
//				if(names[i] == null){
//					Log.i(TAG,"i="+i);
//					names[i]="";
//				}
//			}
//			InitPage1();    //显示铺位表
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} 
		

		Log.i("TTSS","showListView1");
		
		nameList.clear();
		
		BedInfo info = null;
		BedInfoList infolist = null;
		try {
			if (is != null) {
				infolist = BedInfoList.parse(is);
				is.close();
			}
			int maxID=1;
			Log.i("TTSS","长度==>"+infolist.m_list.size());
			for(int i = 0;i < infolist.m_list.size();i++){
				info = infolist.m_list.get(i);
				if(Integer.parseInt(info.m_id)>=maxID){
					maxID=Integer.parseInt(info.m_id);
				}
				
			}
			for(int i = 0;i < maxID;i++){
				nameList.add(i, " ");
				
			}
			
			
			for(int i=0;i<infolist.m_list.size();i++){
				info = infolist.m_list.get(i);
				nameList.set(Integer.parseInt(info.m_id) - 1, info.m_name);
				Log.i("TTSS","参数："+info.m_name+",ID="+info.m_id);
			}
			InitPage1();    //显示铺位表
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
	
	}

	public void showListView2(InputStream is){
		Log.i(TAG,"值班次数："+zbCount);
		String durationTime = "";
		/*for(int i = 0;i < zbTimes.length;i++){
			zbTimes[i] = null;
		}*/
		AgentInfo info;
		AgentInfoList infolist = null;
		
		zb_TimeList.clear();
		zb_PersonList.clear();
		zb_RemarkList.clear();
//		String strTime;
		try {
			if (is != null) {
				infolist = AgentInfoList.parse(is);
				is.close();
			}
			for (int i = 0; i < infolist.m_list.size(); i++) {
				info = infolist.m_list.get(i);
				zb_TimeList.add(info.m_timeStart+"-"+info.m_timeEnd);
				zb_PersonList.add(info.m_pname);
				zb_RemarkList.add("无");
				count++;
				zbCount++;
			}
			for(int i=0;i < zb_PersonList.size();i++){
				durationTime = durationTime + zb_TimeList.get(i)+",";
 			}
			durationTime = durationTime.substring(0, durationTime.length()-1);
			Log.i("TTSS","ThreeGdActivity,duration="+durationTime);
			//清除现有定时，开启新的定时，写入文件
			AlarmUtil.cancelSetAlarm(ThreeGdActivity.this);
			AlarmUtil.tag = 0;
			AlarmUtil.startAlarmClock(durationTime,ThreeGdActivity.this);
			/*if(helper.hasSD()){   
				helper.writeSDFile("durationTime="+durationTime, "dutytime.properties");
			}*/
			
			list.setAdapter(new ListViewAdapter());
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	public void showListView3(InputStream is) {
		ThreeGdZrAnalyze infolist = null;
		DutyInfo info;
		try {
			if (is != null) {
				infolist = ThreeGdZrAnalyze.parse(is);
				is.close();
			}
			zrIlist.clear();
			zrPlist.clear();
			zrMlist.clear();
			
			for (int i = 0; i < infolist.m_list.size(); i++) {
				info = infolist.m_list.get(i);
				zrIlist.add(info.m_item) ;
				zrPlist.add(info.m_name);
				zrMlist.add(info.m_mark);
				count++;
			}
			list1.setAdapter(new ListViewAdapter1());
		} catch (Exception e) {
			e.printStackTrace();
		} 
	
	}

	@Override
	public void onAttachedToWindow() {
		// this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		super.onAttachedToWindow();
	}

	private void fillImageButton() {
		timestr = (TextView) this.findViewById(R.id.three_top_textView3);
		tformat = new SimpleDateFormat("HH:mm"); // 时：分
		dformat = new SimpleDateFormat("yyyy-MM-dd");// 年-月-日 星期
		wformat = new SimpleDateFormat("E");

		cellTv = (TextView) this.findViewById(R.id.three_top_textView2); // 更改监室号
		// String monitorNum = (String) prop.get("cellNumber");
		cellTv.setText("|       监房" + PreferenceUtil.getSharePreference(ThreeGdActivity.this, Constant.CONFIGURE_INFO_CELLNUMBER));

		for (int j = 0; j < 3; j++) {
			image[j] = (ImageView) this.findViewById(Rid[i * 4 + j]);
			image[j].setImageResource(Rdraw[i * 4 + j]);
		}
	}

	private void InitPage3() {
		list1 = (ListView) listViews.get(2).findViewById(R.id.three_item3_listView1);
		Drawable drawable = getResources().getDrawable(R.drawable.message_item_list_sytle);
		list1.setSelector(drawable);
	}

	// 值日
	static class ViewHolder3 {
		private ImageView iv;
		private TextView zrItem;
		private TextView zrPerson;
		private TextView zrRemark;
	}

	// 值日表---生成动态数组，并且转载数据
	public class ListViewAdapter1 extends BaseAdapter {
		public ListViewAdapter1() {

		}
		public int getCount() {
			Log.i("TTSS5","count="+count);
			return zrPlist.size();
		}

		public View getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder3 holder;
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) ThreeGdActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				// 使用View的对象itemView与R.layout.item关联
				convertView = inflater.inflate(R.layout.three_item3_list, null);
				holder = new ViewHolder3();
				// 通过findViewById()方法实例R.layout.item内各组件
				holder.zrItem = (TextView) convertView.findViewById(R.id.three_item3_textView1);
				holder.zrPerson = (TextView) convertView.findViewById(R.id.three_item3_textView2);
				holder.zrRemark = (TextView) convertView.findViewById(R.id.three_item3_textView3);
				holder.iv = (ImageView) convertView.findViewById(R.id.three_item3_imageView1);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder3) convertView.getTag();
			}
			Log.e("position",position+"==>");
			 holder.zrItem.setText(zrIlist.get(position));  
		      holder.zrPerson.setText(zrPlist.get(position));
		      holder.zrRemark.setText(zrMlist.get(position));
			return convertView;
		}

	}

	private void InitPage2() {
		list = (ListView) listViews.get(1).findViewById(R.id.three_item2_listView1);
		// list.setAdapter(new ListViewAdapter());
		Drawable drawable = getResources().getDrawable(R.drawable.message_item_list_sytle);
		list.setSelector(drawable);
	}

	// 值班
	static class ViewHolder2 {
		private ImageView iv;
		private TextView zbTimes;
		private TextView zbPerson;
		private TextView zbRemark;
	}

	// 值班表---生成动态数组，并且转载数据.
	public class ListViewAdapter extends BaseAdapter {
		public ListViewAdapter() {

		}

		public int getCount() {
			// return zbTimes.length;
			return zb_PersonList.size();
		}

		public View getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder2 holder;
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) ThreeGdActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				// 使用View的对象itemView与R.layout.item关联
				convertView = inflater.inflate(R.layout.three_item2_list, null);
				holder = new ViewHolder2();
				Log.e("ListViewAdapter",position+"==>");
				// 通过findViewById()方法实例R.layout.item内各组件
				holder.zbTimes = (TextView) convertView.findViewById(R.id.three_item2_textView1);
				holder.zbPerson = (TextView) convertView.findViewById(R.id.three_item2_textView2);
				holder.zbRemark = (TextView) convertView.findViewById(R.id.three_item2_textView3);
				holder.iv = (ImageView) convertView.findViewById(R.id.three_item2_imageView1);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder2) convertView.getTag();
			}
			holder.zbTimes.setText(zb_TimeList.get(position));
			holder.zbPerson.setText(zb_PersonList.get(position));
			holder.zbRemark.setText(zb_RemarkList.get(position));
			return convertView;
		}
	}

	
	private void InitPage1() {

//		gview = (GridView) listViews.get(0).findViewById(R.id.three_item1_gridView1);
//
//		int n = 20;
//
//		for (int i = 0; i < 20; i++) {
//			// names[i]= names[i]+"张三"+String.valueOf(i+1);
//			names[i] = names[i] + " ";// +numbers[i];
//		}
//		gview.setAdapter(new GridViewAdapter());
//		Drawable drawable = getResources().getDrawable(R.drawable.message_item_list_sytle);
//		gview.setSelector(drawable);
		

		gview = (GridView)listViews.get(0).findViewById(R.id.three_item1_gridView1);
		int n=20;
		
		for (int i = 0; i <nameList.size(); i++) {  
		   // 	names[i]= names[i]+"张三"+String.valueOf(i+1);
			if(nameList.get(i) == null){
				nameList.add(i, "");
			}else{
				nameList.set(i,nameList.get(i)+" ");//+numbers[i];
			}		
		}  
		gview.setAdapter(new GridViewAdapter());
		Drawable drawable = getResources().getDrawable(R.drawable.message_item_list_sytle);  
		gview.setSelector(drawable); 
	
	}

	static class ViewHolder1 {
		private TextView text1;
		private ImageView imgview;
	}

	// 铺位表---GridView实现
	public class GridViewAdapter extends BaseAdapter {

//		public int getCount() {
//			return names.length;
//		}
//
//		public View getItem(int position) {
//			return null;
//		}
//
//		public long getItemId(int position) {
//			return position;
//		}
//
//		public View getView(int position, View convertView, ViewGroup parent) {
//			ViewHolder1 holder;
//			if (convertView == null) {
//				LayoutInflater inflater = (LayoutInflater) ThreeGdActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//				// 使用View的对象itemView与R.layout.item关联
//				convertView = inflater.inflate(R.layout.three_item1_list, null);
//				holder = new ViewHolder1();
//				// 通过findViewById()方法实例R.layout.item内各组件
//				holder.text1 = (TextView) convertView.findViewById(R.id.three_item1_textView1);
//				holder.imgview = (ImageView) convertView.findViewById(R.id.three_item1_imageView1);
//				convertView.setTag(holder);
//			} else {
//				holder = (ViewHolder1) convertView.getTag();
//			}
//			holder.text1.setText(position + 1 + " " + names[position]);
//			return convertView;
//		}
		
		  

        public int getCount() {  
            return nameList.size();  
        }  
  
        public View getItem(int position) {  
            return null;  
        }  
  
        public long getItemId(int position) {  
            return position;  
        }  
  
        public View getView(int position, View convertView, ViewGroup parent) {  
        	ViewHolder1 holder;
            if (convertView == null)  {
               LayoutInflater inflater = (LayoutInflater) ThreeGdActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
            
            // 使用View的对象itemView与R.layout.item关联  
            convertView = inflater.inflate(R.layout.three_item1_list, null);      
            holder=new ViewHolder1();
            // 通过findViewById()方法实例R.layout.item内各组件  
            holder.text1 = (TextView) convertView.findViewById(R.id.three_item1_textView1);  
            holder.imgview  = (ImageView) convertView.findViewById(R.id.three_item1_imageView1);
            convertView.setTag(holder);
            }else
            {
            	holder = (ViewHolder1) convertView.getTag();
            }

            if(nameList.get(position).equals("") || nameList.get(position) == null){
            	holder.text1.setText("");            	
            }else{
            	holder.text1.setText((position+1)+" "+nameList.get(position));	
            }
            return convertView; 
		}  
    
	}

	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.three_top_vPager);
		listViews = new ArrayList<View>();
		LayoutInflater mInflater = getLayoutInflater();
		listViews.add(mInflater.inflate(R.layout.three_viewpage_item_lay1, null));
		listViews.add(mInflater.inflate(R.layout.three_viewpage_item_lay2, null));
		listViews.add(mInflater.inflate(R.layout.three_viewpage_item_lay3, null));
		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setCurrentItem(0);
	}

	/**
	 * ViewPager适配器
	 */
	public class MyPagerAdapter extends PagerAdapter {
		public List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			return mListViews.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}

	private String[] ThreeInfoTable = { "ThreeInfoPw", "ThreeInfoZb", "ThreeInfoZr" };
	private boolean cardstart;

	private KeyEvent Keyevent_Up =new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP);
	private KeyEvent Keyevent_Down =new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN);
	
	@SuppressLint("InlinedApi")
	private void ProcessKeyFunc(int keyCode) {
		
		if(keyCode==74){
			cardstart=true;
		}
		
		if(keyCode==69&& cardstart ){
			cardstart=false;
		}
		
		if(cardstart){
			Log.e("keyCode1", "ProcessKeyFunc：" + keyCode);
			return;
			
		}
		Log.e("keyCode2", "ProcessKeyFunc：" + keyCode);
		fillImageButton();
		Log.i(TAG, "keyFlag==true");
		if (gv.getmKey_Right() == keyCode) {
			count = 0;
			lrint = lrint + 1;

			if (lrint >2) {
				lrint = 0;
			}
			
			if(lrint==0){
				doNetWork();
			}else if(lrint==1){
				doNetWork();
			}else if(lrint==2){
				doNetWork();
			}
		}

		if (gv.getmKey_Up()==keyCode)
		{
			if(gview!=null &&lrint==0){
				gview.onKeyDown(KeyEvent.KEYCODE_PAGE_UP,Keyevent_Up);
				
			}
		}
		if (gv.getmKey_Down()==keyCode)
		{
//			gview.fullScroll(View.FOCUS_DOWN);KeyEvent.KEYCODE_PAGE_DOWN,KeyEvent.KEYCODE_DPAD_DOWN,KeyEvent.KEYCODE_PAGE_UP
			if(gview!=null  && lrint==0){
				gview.onKeyDown( KeyEvent.KEYCODE_PAGE_DOWN,Keyevent_Down);
				
			}
		};
		
		if (gv.getmKey_Left() == keyCode) {
			count = 0;
			lrint = lrint - 1;
			if (lrint < 0) {
				lrint = 2;
			}
			
			if(lrint==0){
				doNetWork();
			}else if(lrint==1){
				doNetWork();
			}else if(lrint==2){
				doNetWork();
			}
			
		}
		if (gv.getmKey_Esc() == keyCode) {
			changePage(0);
		}
		Log.i("REASON", "左右切换lrint值(0,1,2)：" + lrint);
		mPager.setCurrentItem(lrint);
//		list.requestFocusFromTouch();
		image[lrint].setImageResource(Rdrawsel[lrint]);
	}

	public class StatusReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String actionStr = intent.getAction();
			Log.d(TAG, "actionStr = " + actionStr);
			if (Constant.STOPPLAY_ACTION.equals(actionStr)) {
				Log.v(TAG, "stopPlay广播");
				changePage(0);
			}
		}
	}

	/*
	 * 页面跳转
	 */
	public void changePage(int menustr) {
		Log.i(TAG, "changePage");
		Intent intent = new Intent();// 新建一个activity
		intent.setClass(ThreeGdActivity.this, IndexActivity.class);// 从本类的activity跳转到目标activity。
		startActivityForResult(intent, 0);// 执行目标activity
		// //
		// overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
		// getWindow().setWindowAnimations(2000);
		// overridePendingTransition(android.R.anim.slide_in_left,
		// android.R.anim.slide_out_right);
		this.finish();
	}

	@Override
	protected void onDestroy() {
		Log.v(TAG, "onDestroy");
		isFinished = true;
		mSerialPort = null;
		unregisterReceiver(mStatusReceiver);// 可能报错
		super.onDestroy();
	}

	/*
	 * USB按键处理
	 */

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
//		Keyevent=event;
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			int code = UnitaryCodeUtil.UnitaryCode(event.getKeyCode());
			try{
				ProcessKeyFunc(code);
			}catch(Exception e){
				Log.i("ERROR","ERROR："+e.getMessage());
				e.printStackTrace();
			}
		}
		return false;
	}

	private boolean isPaused = false;

	@Override
	protected void onPause() {
		isPaused = true;
		super.onPause();
	}

	@Override
	public void serial_KeyDo(int keyCode) {
		super.serial_KeyDo(keyCode);
		Message msg = new Message();
		msg.what = 1;
		msg.arg2 = keyCode;
		myHandler.sendMessage(msg);
	}

	@Override
	public void serial_CardDo(String cardNum) {

		super.serial_CardDo(cardNum);
	}

}
