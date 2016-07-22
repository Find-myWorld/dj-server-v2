package com.example.iimp_znxj_new2014.selfconsume;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.http.Header;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iimp_znxj_new2014.DianJiaoApplication;
import com.example.iimp_znxj_new2014.R;
import com.example.iimp_znxj_new2014.activity.BaseActivity;
import com.example.iimp_znxj_new2014.activity.IndexActivity;
import com.example.iimp_znxj_new2014.util.Constant;
import com.example.iimp_znxj_new2014.util.PreferenceUtil;
import com.example.iimp_znxj_new2014.util.UnitaryCodeUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/*
 * Date:2015-11-18
 * Description:去掉了许多不必要的判断说明(多是判断是否刷卡的判断)
 * 自助消费
 */
public class BuyActivity2 extends BaseActivity {
	DianJiaoApplication gv;
	/**
	 * 0表示可以刷卡，
	 * 1表示屏蔽刷卡
	 */
//	private String isShowDialog="1";
	
	/**
	 * true 做清空操作
	 * false 不做清空
	 */
	private ArrayList<Integer> idList = new ArrayList<Integer>();
	private ArrayList<String> nameList = new ArrayList<String>();
	private ArrayList<String> explainList = new ArrayList<String>();
	private ArrayList<String> priceList = new ArrayList<String>();
	private ArrayList<String> picList = new ArrayList<String>();
	private ArrayList<Integer> countList = new ArrayList<Integer>();
	
	private ArrayList<ShoppingCart> cartList = new ArrayList<ShoppingCart>();
	
	
//	boolean count_flag = true; // 只能接收一次getCount的值
//	boolean add_flag = true;

	public GoodsDao dao; // 数据库操作类声明
	public Goods data; // 物品类声明
	public ByteArrayOutputStream os;
	public ByteArrayOutputStream bos;// test
	byte[] bt = new byte[199];

	private static final String DBNAME = "commodity.db";
	private String[] tableArray = { "daily_t", "labor_t", "food_t" }; // 三张表的数组
	private String[] countArray = { "dailyCount", "laborCount", "foodCount" };// count_t表的三个字段
	private String[] commandArray = { "ShoppingDaily", "ShoppingLabor", "ShoppingFood" }; // 请求服务器名利数组

	String getCount = null; // 解析xml获取的count值
	int sqlCount; // 存在数据库中的一个值，如果getCount >
					// sqlCount,则解析xml并入库，然后再读数据库。如果getCount <=
					// sqlCount,直接读数据库显示数据
	int[][] spcart = new int[200][200]; // 二维数组，【id】【数量】 = 1

//	boolean flag = false;
//	boolean mark = false;
//	boolean keyFlag = true;
	StringBuilder cardIdSb = new StringBuilder(); // 数组转化为字符串；卡号信息
	StringBuilder goodsSb = new StringBuilder(); // 数组转化为字符串；商品id + 商品num

	private final static String TAG = "BuyActivity";
	private String URL = null;
	private String msgURL = null;
	private String picURL = null;
	int count = 0;
	int countt = 0;
	int testCount = 0;
	int reclen = 5; // 5秒倒计时关闭窗口

	ListView list;
	TextView et = null;
	int i = 0;
	int n = 32;
	int lrint = 0, udint = 0;
	int curSelectID = 0;
	int[] Rid = new int[] { R.id.buy_top_imageView1, R.id.buy_top_imageView2, R.id.buy_top_imageView3 };
	int[] Rdraw = new int[] { R.drawable.buy_ryp, R.drawable.buy_lbyp, R.drawable.buy_sp };
	int[] Rdrawsel = new int[] { R.drawable.buy_ryp_h, R.drawable.buy_lbyp_h, R.drawable.buy_sp_h };
	ImageView image[] = new ImageView[4]; // 二维数组定义
	private View oldv = null;
	private Dialog dialog = null;
	private String isOk = null;      //服务器返回true/false
	
	private String contentTv = null; //服务器返回内容
	private final static int SUCCESS_FLAG_VALUE = 1;
	private final static int FAILURE_FLAG_VALUE = 2;
	private final static int CONFIRE_FLAG_VALUE = 3;
	private final static int OTHER_FLAG_VALUE = 4;
	
	static final int MSG_TIME_VALUE = 1;
	static final int DO_NETWORK_VALUE = 2;
	static final int CLOSE_DIALOG_VALUE = 3;
	
	private final static int SWING_CARD = 2;
	private final static int SERIAL_KEY_DOWN = 1;

	int[] pic = new int[] { R.drawable.buy_ryp_h, R.drawable.buy_lbyp_h, R.drawable.buy_sp_h };

	TextView timestr;// 时间显示
	Handler handler; // 时间的更新
	SimpleDateFormat tformat, dformat, wformat;// 时间设置
	static final int MSG_TIME = 1;

	TextView cellTv; // 显示读取后的数据
	Properties prop;
	FileHelper helper;
	String serverIp = "192.168.0.26";
	String port = "8002";
	String cellNumber;
	int value = 0;
	String getIp, getPort, getCellNumber;

	private boolean isSwingCard = false;//isSwingCard=false,按键响应;isSwingCard=true,屏蔽按键
	private boolean isFinished = false;
	private boolean isShowProcess = true;
	private boolean isOnCreate = true;

	private StatusReceiver mStatusReceiver;
	private Toast myToast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buy);

		dao = new GoodsDao(BuyActivity2.this);

		Log.v(TAG, "onCreate");

		gv = (DianJiaoApplication) getApplication();
		getIp = PreferenceUtil.getSharePreference(BuyActivity2.this, Constant.CONFIGURE_INFO_SERVERIP);
		getPort = PreferenceUtil.getSharePreference(BuyActivity2.this, Constant.CONFIGURE_INFO_PORT);
		getCellNumber = PreferenceUtil.getSharePreference(BuyActivity2.this, Constant.CONFIGURE_INFO_CELLNUMBER);
		serverIp = "http://" + getIp + ":" + getPort + "/";
		// serverIp = "http://192.168.1.253:6700/";
		Log.i(TAG, "配置信息：" + getIp + "|" + getPort + "|" + getCellNumber + "|" + serverIp);

		URL = serverIp + Constant.SERVER_PART+"ShoppingDataXml.aspx";
		msgURL = serverIp + Constant.SERVER_PART+"ProductManage.aspx";
		picURL = serverIp;
		
		// 初始化，创建默认的ImageLoader配置参数
		ImageLoader.getInstance().init(
				ImageLoaderConfiguration.createDefault(BuyActivity2.this));

		initShuzu();

		fillImageButton();
		image[0].setImageResource(Rdrawsel[0]);
		dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					String cardNum = getUSBCardNum(event);
					if (event.getKeyCode() == 59) {
						count = 3;
					}
					if (!cardNum.equals("")) {
						Message msg = new Message();
						msg.what = 2;
						Log.i(TAG, "onKey,卡号:" + cardNum);
						msg.obj = cardNum;
						myHandler.sendMessage(msg);
					}
					/*if (!startGetCardNum && !cardNum.startsWith("69") && count == 0) {
						int code = UnitaryCodeUtil.UnitaryCode(event.getKeyCode());
						ProcessKeyFunc(code);
						Log.i(TAG, "按键Code:" + code);
					}*/
				}
				return false;
			}
		});

		list = (ListView) findViewById(R.id.buy_info_listView1);
		list.setDivider(null);

		list.setSelected(true);
		list.setCacheColorHint(0);
		list.setOnItemSelectedListener(new myitemselect());// 按键左右切换时未调用该方法，容易出现“空指针异常的问题”

		Drawable drawable = getResources().getDrawable(R.drawable.message_item_list_sytle);
		list.setSelector(drawable);

		new Thread(new MyThread()).start(); // 时间更新相关的线程

		handler = new Handler() {                  // 时间更新
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MSG_TIME_VALUE:
					String d = dformat.format(new Date()); // 设置首页时间与日期
					String t = tformat.format(new Date());
					String w = wformat.format(new Date());
					timestr.setText(d+" "+t+" "+w);
					break;
				case DO_NETWORK_VALUE:
					 doNetWork("ShoppingDaily");
					 Log.i("TTSS","延迟请求？...ShoppingDaily");
					 break;
				case CLOSE_DIALOG_VALUE:
					 Log.i("TTSS","延迟5s请求+关闭窗口");
					 if (dialog.isShowing()) {
						 dialog.dismiss();
					 }
					 list.setAdapter(new ListViewAdapter());
					 break;
				}
				
				super.handleMessage(msg);
			}
	   };
	   handler.sendEmptyMessageDelayed(DO_NETWORK_VALUE, 300);

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
					handler.sendMessage(message);
				} catch (Exception e) {
				}
			}
		}
	}

	/*
	 * 发送post命令到服务器，接收返回信息
	 * <p></p>
	 * @params choice post命令的内容字符串
	 * @return null
	 * @throws UnsupportedEncodingException
	 */
	public void doNetWork(final String choice) {
		if(isOnCreate){
			showProcessDialog();
			isOnCreate = false;
		}
		
		try {
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
						InputStream is = new ByteArrayInputStream(getXmlStr
								.getBytes());// 将xml转为InputStream类型
						Log.i("TTSS", "doNetWork,is=>" + getXmlStr);
						showListView(is); // 解析xml
						// list.setSelection(curSelectID);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
					closeProcessDialog();
					Toast.makeText(getApplicationContext(), "网络延时，请稍候......", Toast.LENGTH_SHORT).show();
				}
			});
		} catch (IllegalArgumentException e) {
			Toast.makeText(getApplicationContext(), "发生错误", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}

	}

	/*
	 * 通过图片的Bitmap获取图片的大小
	 */
	@SuppressLint("NewApi")
	public int getBitmapSize(Bitmap bp) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			return bp.getAllocationByteCount();
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
			return bp.getByteCount();
		}
		return bp.getRowBytes() * bp.getHeight();
	}

	/*
	 * 调用不同的方法解析xml文件
	 * <p>根据当前的lrint值，调用相应的方法解析xml文件</p>
	 */
	public void showListView(InputStream is) {
		clearArrayList();
		if (lrint == 0) {
			showListView1(is);
		} else if (lrint == 1) {
			showListView2(is);
		} else if (lrint == 2) {
			showListView3(is);
		}
	}
	
	/*
	 * 清空ArrayList
	 */
	private void clearArrayList(){
		idList.clear();
		nameList.clear();
		explainList.clear();
		priceList.clear();
		picList.clear();
		countList.clear();
	}

	/*
	 * 解析服务器返回的xml文件
	 */
	public void showListView1(InputStream is) {
		BuyDailyAnalyze service = new BuyDailyAnalyze();
		boolean isFirst = true;
		try {
			List<BuyShopping> newslist = service.getBuyDaily(is);
			if (newslist.size() == 1) {
				closeProcessDialog();
				DisplayToast("  无商品数据！  ");
			}
			for (BuyShopping news : newslist) {
				if (isFirst) {
					isFirst = false; // xml第一个为Count,非商品信息
				} else {
					idList.add(news.getId());
					nameList.add(news.getName());
					priceList.add(news.getPrice());
					explainList.add(news.getExplain());
					picList.add(picURL + news.getPicture());
					countList.add(0);
				}
			}
			list.setAdapter(new ListViewAdapter());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 解析服务器返回的xml文件
	 */
	public void showListView2(InputStream is) {
		BuyLaborAnalyze service = new BuyLaborAnalyze();
		boolean isFirst = true;
		try {
			List<BuyShopping> newslist = service.getBuyLabor(is);
			if (newslist.size() == 1) {
				closeProcessDialog();
				DisplayToast("  无商品数据！  ");
			}
			for (BuyShopping news : newslist) {
				if (isFirst) {
					isFirst = false; // xml第一个为Count,非商品信息
				} else {
					idList.add(news.getId());
					nameList.add(news.getName());
					priceList.add(news.getPrice());
					explainList.add(news.getExplain());
					picList.add(picURL + news.getPicture());
					countList.add(0);
				}
			}
			list.setAdapter(new ListViewAdapter());
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
	}

	/*
	 * 解析服务器返回的xml文件
	 */
	public void showListView3(InputStream is) {
		BuyFoodAnalyze service = new BuyFoodAnalyze();
		boolean isFirst = true;
		try {
			List<BuyShopping> newslist = service.getBuyFood(is);
			if (newslist.size() == 1) {
				closeProcessDialog();
				DisplayToast("  无商品数据！  ");
			}
			for (BuyShopping news : newslist) {
				if (isFirst) {
					isFirst = false; // xml第一个为Count,非商品信息
				} else {
					idList.add(news.getId());
					nameList.add(news.getName());
					priceList.add(news.getPrice());
					explainList.add(news.getExplain());
					picList.add(picURL + news.getPicture());
					countList.add(0);
				}
			}
			list.setAdapter(new ListViewAdapter());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static class ViewHolder {
		private TextView text1;
		private TextView text2;
		private TextView text3;
		private TextView text4;
		private TextView text5;
		private ImageView imgview;
	}

	static class Counter {
		private int id;
		private int count;
	}

	/*
	 *  加载数据库信息到界面上
	 *  <p>只负责加载数据库中内容，数据库中有什么信息，就加载什么</p>
	 */
	public class ListViewAdapter extends BaseAdapter {
		View[] itemViews;

		public ListViewAdapter() {
		}

		public int getCount() {
			if (idList.size() > 0) {
				closeProcessDialog();
			}
			return idList.size(); // 显示当前表中数据的条数
		}

		public View getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) BuyActivity2.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				// 使用View的对象itemView与R.layout.item关联
				convertView = inflater.inflate(R.layout.buy_list, null);
				holder = new ViewHolder();
				// 通过findViewById()方法实例R.layout.item内各组件
				holder.text1 = (TextView) convertView.findViewById(R.id.buy_item_textView1); // 名称
				holder.text2 = (TextView) convertView.findViewById(R.id.buy_item_textView2); // 单价
				holder.text3 = (TextView) convertView.findViewById(R.id.buy_item_textView3); // 简介
				holder.text4 = (TextView) convertView.findViewById(R.id.buy_item_textView4);
				holder.text5 = (TextView) convertView.findViewById(R.id.buy_item_textView5);
				holder.imgview = (ImageView) convertView.findViewById(R.id.buy_item_imageView1);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.text1.setText(nameList.get(position));
			holder.text2.setText("单价￥" + priceList.get(position));
			holder.text3.setText(explainList.get(position));
			holder.text5.setText("0");

			// 加载图片
			DisplayImageOptions options = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.ic_stub)
					.showImageForEmptyUri(R.drawable.buy_produres_pic)
					.showImageOnFail(R.drawable.buy_produres_pic).cacheInMemory(true)  //图片缓存在内存
					.cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565)
					.build();
			ImageLoader.getInstance().displayImage(picList.get(position), holder.imgview,
					options);
			return convertView;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.v(TAG, "onCreateOptionMenu");
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.buy, menu);
		return true;
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	/*
	 *  初始化
	 */
	private void fillImageButton() {
		timestr = (TextView) this.findViewById(R.id.buy_top_textView3);
		tformat = new SimpleDateFormat("HH:mm"); // 时：分
		dformat = new SimpleDateFormat("yyyy-MM-dd");// 年-月-日 星期
		wformat = new SimpleDateFormat("E");

		cellTv = (TextView) this.findViewById(R.id.buy_top_textView2); // 更改监室号
		// String monitorNum = (String) prop.get("cellNumber");
		cellTv.setText("|       监房" + getCellNumber);

		for (int j = 0; j < 3; j++) {
			image[j] = (ImageView) this.findViewById(Rid[i * 4 + j]);
			image[j].setImageResource(Rdraw[i * 4 + j]);
		}

	}

	/*
	 * 发送商品信息和卡号信息到服务器（一次购买操作）
	 * @params goosMsg 购物车数据
	 * @params cardsMsg 卡号信息
	 * @return null
	 * @throws UnsupportedEncodingException
	 */
	private boolean useStartsWithout0 = false;// 是否使用不已0开头的卡号

	public void doNetWorkSendMsg(final String goodsMsg, final String cardsMsg,int type) {
		Log.i(TAG, "doNetWorkSendMsg:" + goodsMsg + "," + cardsMsg);
		try {
			AsyncHttpClient client = new AsyncHttpClient();
			RequestParams params = new RequestParams();
			if(type == 1){
				params.put("goodsNumber", goodsMsg);
				params.put("cardNumber", cardsMsg);
				params.put("type", "1");
			}else{
				params.put("goodsNumber", goodsMsg);
				params.put("rybh", cardsMsg);
				params.put("type", "2");
			}
			Log.i("TEST","1."+msgURL+",2."+goodsMsg+",3."+cardsMsg);
			params.setContentEncoding("UTF-8");
			client.post(msgURL, params, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int arg0, Header[] headers, byte[] responseBody) {
					try {
						String getXmlStr = new String(responseBody, "UTF-8").trim(); // 获取xml
						Log.i(TAG, "doNetWorkSendMsg,返回消息为：" + getXmlStr);
						String[] split = getXmlStr.split("#");     
						Log.i(TAG,"split长度为总为3？："+split.length);
						if(split.length >= 2){
							isOk = split[0];
							contentTv = split[1];
							Log.i("ABCD","split长度为总为3？："+split.length+",1="+isOk+",2="+contentTv);
							if(isOk.equals("true")){
								showDialogWin(SUCCESS_FLAG_VALUE);
							}else{
								showDialogWin(FAILURE_FLAG_VALUE);
							}
						}else{
							showDialogWin(OTHER_FLAG_VALUE);
						}
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
					if (useStartsWithout0) {
						String cardsMsg_replaced = UnitaryCodeUtil.removeStarts0(cardsMsg);
						doNetWorkSendMsg(goodsMsg, cardsMsg_replaced,1);
						Log.v(TAG, "去掉开头的0后卡号：" + cardsMsg_replaced);
						useStartsWithout0 = false;
					} else {
						Toast.makeText(getApplicationContext(), "网络延时，与服务器连接失败...", Toast.LENGTH_SHORT).show();
					}
				}
			});
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	/*
	 * 按键响应函数
	 * <p>响应键盘的：上下左右键+数字键+功能键</p>
	 */
	private void ProcessKeyFunc(int keyCode) {
		
		int counter = idList.size();
		fillImageButton();
		Log.i("TEST","keyCode="+keyCode+",counter="+counter);
		
		if(keyCode == 74){    //开始刷卡标志 ——屏蔽键盘
			isSwingCard = true;
		}
		
		
		if(!dialog.isShowing()){
			Log.i(TAG,"");
			if (gv.getmKey_Right() == keyCode) {
				udint = 0;
				if(isShowProcess){
					showProcessDialog();
				}
				isShowProcess =true;
				lrint = lrint + 1;
				if (lrint > 2) {
					lrint = 0;
				} else if (lrint == 1) {
				}
				doNetWork(commandArray[lrint]);
			}
			if (gv.getmKey_Left() == keyCode) {
				udint = 0;
				if(isShowProcess){
					showProcessDialog();
				}
				isShowProcess =true;
				lrint = lrint - 1;
				if (lrint < 0) {
					lrint = tableArray.length - 1;
				} else if (lrint == 1) {
					
				}
				doNetWork(commandArray[lrint]);
			}
			if (gv.getmKey_Up() == keyCode) {
				udint = udint - 1;
				if (udint <= 0){
					udint = 0;
				}
			}
			if (gv.getmKey_Down() == keyCode) {
				udint = udint + 1;
				if (udint >= counter){
					udint = counter;
				}
			}

			if (counter != 0 && keyCode >= gv.getmKey_0() && keyCode <= gv.getmKey_9()) { // 输入0-9才做数据操作
				value = keyCode - 48;
				if (et == null) {
					return;
				}
				int currentNum = Integer.parseInt(et.getText().toString());
				if (currentNum <= 9 && currentNum > 0) {
					value = currentNum * 10 + keyCode - 48;
				}
				et.setText("" + value);
				//传入商品ID，对应的数量。    2-3,4-8,1-2
				//购物车
				ShoppingCart sCart = new ShoppingCart();
				sCart.setgId(idList.get(curSelectID));   //ID   => idList.get(curSelectID)
				sCart.setgPrice(priceList.get(curSelectID));
				sCart.setgCount(value);                  //数量   => value
				
				boolean isHas = false;
				for(int i = 0;i < cartList.size();i++){
					if(cartList.get(i).getgId() == idList.get(curSelectID)){
						isHas = true;
						cartList.set(i, sCart);
						break;
					}
				}
				if(!isHas){
					cartList.add(sCart);
				}
			}
			
			if (gv.getmKey_Enter() == keyCode){
				if (getGoodsSum() == 0 && !isCard) {
					Toast.makeText(getApplicationContext(), "您还未选中任何商品，无法进入支付页面！", Toast.LENGTH_LONG).show();
				} else {
					getGoodsStr(); // 获取商品信息
					showDialogWin(CONFIRE_FLAG_VALUE);
				}
			}
		}
		if(keyCode == 69){    //刷卡结束标志——按键响应
			isSwingCard = false;
		}
		
		list.requestFocusFromTouch();
		list.setSelection(udint);
		image[lrint].setImageResource(Rdrawsel[lrint]);

		if (gv.getmKey_Esc() == keyCode){
			if (dialog.isShowing()) {
				dialog.dismiss();
			} else {
				changePage(0);
			}
		}
	}

	/*
	 *  用户购买后刷卡后弹出提示窗口
	 *  <p>提示用户的信息有6种情况：(1)提示购买信息，当前买了几件商品，总价格
	 *  (2)购买成功(3)购买失败，卡内余额不足(4)购买失败，XX商品数量不足(5)购买失败，该卡尚未注册(6)购买失败，未知错误</p>
	 */
	public void showDialogWin(int flag) {
		isShowProcess = false;
		LayoutInflater li;
		View v = null;
		li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		switch (flag) {
			case CONFIRE_FLAG_VALUE:       //购买前提示；提示消费商品信息、提示消费xx元
				v = li.inflate(R.layout.love_checkconsume2_info, null);
				TextView ed1,ed2,ed3;
				ed1 = (TextView) v.findViewById(R.id.buy_sure_item_textView1);
				ed1.setText("" + getGoodsSum());
				ed2 = (TextView) v.findViewById(R.id.buy_sure_item_textView2);
				ed2.setText("本次共选中  " + getGoodsSum() + " 件物品");
				ed3 = (TextView) v.findViewById(R.id.buy_sure_item_TextView3);
				ed3.setText("共计消费  " + getGoodsPrice() + " 元");
				
				break;
			case SUCCESS_FLAG_VALUE:      //购买成功；提示扣除xx元、提示余额xx元
				Log.i("ABCD","here,"+contentTv);
				v = li.inflate(R.layout.buy_post_ok, null);
			    TextView okEd1,okEd2;
				okEd1 = (TextView)v.findViewById(R.id.buy_ok_cost_tv);
//				okEd2 = (TextView)v.findViewById(R.id.buy_ok_time_tv);
				okEd1.setText(contentTv);
				
				handler.sendEmptyMessageDelayed(CLOSE_DIALOG_VALUE, 5000);
				
				break;
				
			case FAILURE_FLAG_VALUE:     //购买失败；出现未知错误
				v = li.inflate(R.layout.buy_post_no, null);
				TextView fTv1,fTv2;
				fTv1 = (TextView)v.findViewById(R.id.buy_no_cost_tv);
//				errTv2 = (TextView)v.findViewById(R.id.buy_no_time_tv);
				fTv1.setText(contentTv);
				
				handler.sendEmptyMessageDelayed(CLOSE_DIALOG_VALUE, 5000);
				
				break;
				
			case OTHER_FLAG_VALUE:     //购买失败；出现未知错误
				v = li.inflate(R.layout.buy_post_no, null);
				TextView errTv1,errTv2;
				errTv1 = (TextView)v.findViewById(R.id.buy_no_cost_tv);
				errTv2 = (TextView)v.findViewById(R.id.buy_no_time_tv);
				errTv1.setText(" \t  购买操作失败  \n\t 出现未知错误！");
				
				handler.sendEmptyMessageDelayed(CLOSE_DIALOG_VALUE, 5000);
				
				break;
				
			default:
				break;
		}

		dialog.setContentView(v);
		// 获得Window对象
		Window dialogWindow = dialog.getWindow();
		dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
		// 获得WindowManager.LayoutParams对象
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.width = 680; // 宽度
		lp.height = 520; // 高度
		lp.alpha = 0.9f; // 透明度
		dialogWindow.setAttributes(lp);
		dialog.show();
	}

	/*
	 *  初始化购物车数组(数组置0操作)
	 */
	public void initShuzu() {
		cartList.clear();
	}

	/*
	 *  获取已购买的商品信息
	 *  @return strGoods 格式：1,2,8,3,4,2,
	 */
	public String getGoodsStr() {
		String sGoodstr = "";
		StringBuilder goodsSb = new StringBuilder();
		for(int i = 0;i < cartList.size();i++){
			if(cartList.get(i).getgCount() != 0){
				goodsSb.append(cartList.get(i).getgId()+","+cartList.get(i).getgCount()+",");
			}
		}
		sGoodstr = goodsSb.toString();
		goodsSb.delete(0, goodsSb.length()); // 清空StringBuilder，否则累加
		Log.i(TAG,"商品参数："+sGoodstr);
		return sGoodstr;
	}

	/*
	 *  获取所选商品的数量/种类
	 */
	public int getGoodsSum() { // 统计商品种类，不应超过20种 或者 商品数量，不超过20件
		int sum = 0;
		for(int i = 0;i < cartList.size();i++){
			if(cartList.get(i).getgCount() != 0){
				sum += cartList.get(i).getgCount();
			}
		}
		Log.i(TAG,"商品数量："+sum);
		return sum;
	}

	/*
	 *  获取所选商品的总价
	 */
	public BigDecimal getGoodsPrice() {
		double price = 0;
		for(int i = 0;i < cartList.size();i++){
			if(cartList.get(i).getgCount() != 0){
				price = price + Double.parseDouble(cartList.get(i).getgPrice()) * cartList.get(i).getgCount();
			}
		}
		Log.i(TAG,"商品总价："+price);
		BigDecimal bd = new BigDecimal(price); // 此处price还是double类型
		bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP); // 保留后面两位小数，否则double型后面小数位太多
		return bd;
	}

	public class StatusReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String actionStr = intent.getAction();
			Log.d("Test", "actionStr = " + actionStr);
			if (Constant.STOPPLAY_ACTION.equals(actionStr)) {
				Log.i(TAG, "stopPlay广播");
				changePage(0);
			}
		}
	}

	@Override
	protected void onDestroy() {
		Log.v(TAG, "onDestroy");
		isFinished = true;

		unregisterReceiver(mStatusReceiver);// 可能报错

		super.onDestroy();
	}

	// 返回上一个界面
	public void changePage(int menustr) {
		Log.v(TAG, "changePage");
		Intent intent = new Intent();// 新建一个activity
		intent.setClass(BuyActivity2.this, IndexActivity.class);// 从本类的activity跳转到目标activity。
		startActivityForResult(intent, 0);// 执行目标activity
		this.finish();
	}
		
	// ListView事件监听
	public class myitemselect implements OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			// TODO Auto-generated method stub
			if (oldv == null)
				;
			else
				oldv.setBackgroundColor(Color.TRANSPARENT);
			if (arg1 != null) {// 解决：容易发生 “空指针” 错误，即 arg1 = null
				// Log.i(TAG, "OnItemSelectedListener,arg3=" + arg3);
				arg1.setBackgroundResource(R.drawable.itemsel5);
				et = (TextView) arg1.findViewById(R.id.buy_item_textView5);
				curSelectID = (int) arg3;
				oldv = arg1;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			Log.i(TAG, "失去焦点");
		}
	}

	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SERIAL_KEY_DOWN:
				int keyCode = msg.arg2;
				Log.i(TAG, "Handler,接收到KeyCode=" + keyCode);
				if (!isFinished && !isPaused ) { // 没有结束才执行下一步
					ProcessKeyFunc(keyCode);
				} else {
					Log.i(TAG, "已经结束，本页面以关闭，按键无响应");
				}
				 isSwingCard = true; //刷卡后，屏蔽取消键
				break;
			case SWING_CARD:
				if(dialog.isShowing()){
					String cardNumber = msg.obj.toString();
					if (!isFinished && !isPaused) { // 没有结束才执行下一步
						Log.i(TAG, "handler接收到卡号:" + cardNumber);
//						useStartsWithout0 = true;
						doNetWorkSendMsg(getGoodsStr(), cardNumber,1); // 发送信息给服务器
						initShuzu(); // 成功付款一次后初始化购物车数组（不然关闭刷卡窗口后，购物车数组里还有数据）
					}
				}
				break;
			}
			super.handleMessage(msg);
		}
	};

	/*
	 * USB按键处理
	 */
	boolean isCard = false;
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if(dialog.isShowing()){
				String cardNum = getUSBCardNum(event);
				if (!TextUtils.isEmpty(cardNum)) {
					Log.i(TAG, "dispatchKeyEvent,卡号:" + cardNum);
					Message msg = new Message();
					msg.what = SWING_CARD;
					msg.obj = cardNum;
					myHandler.sendMessage(msg);
					isCard = true;
				}
				if (gv.getmKey_Esc() == event.getKeyCode()) {
					if (dialog.isShowing()) {
//						isSwingCard = false;
						dialog.dismiss();
					} else {
						changePage(0);
					}
				}
			}else{
				int code = UnitaryCodeUtil.UnitaryCode(event.getKeyCode());
				Log.i(TAG, "dispatchKeyEvent,键值:" + code);
				ProcessKeyFunc(code);
			}
		}
		return true;
	}

	StringBuilder cardNumSB = new StringBuilder();
	boolean startGetCardNum = false;

	private boolean cardnumstart;
	// 接收USB卡号
	public String getUSBCardNum(KeyEvent event) {
		if(!dialog.isShowing()){
			return "";
		}
		int code = event.getKeyCode();
		if (code == 74) {
			cardnumstart=true;
			startGetCardNum = true;
			return "";
		}
		if (code == 59) {
			String cardNum = cardNumSB.toString();
			cardNumSB.delete(0, cardNumSB.length());
			startGetCardNum = false;
			cardnumstart=false;
			Log.i(TAG, cardNum);
			Log.e("cardNum", cardNum);
			return cardNum;
		}
		if (code == 76) {
		}
		if(code ==66 &&startGetCardNum){
			startGetCardNum = false;
		}
		if (cardnumstart) {
			cardNumSB.append(code - 7);
		}
		count--;
		if (count <= 0) {
			count = 0;
		}
		return "";
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
		msg.what = SERIAL_KEY_DOWN;
		msg.arg2 = keyCode;
		Log.i(TAG,"serial_KeyDo,keyCode="+keyCode);
		myHandler.sendMessage(msg);
	}

	@Override
	public void serial_CardDo(String cardNum) {
		super.serial_CardDo(cardNum);
		Message msg = new Message();
		msg.what = SWING_CARD;
		msg.obj = cardNum;
		Log.i(TAG,"serial_CardDo,cardNum="+cardNum);
		myHandler.sendMessage(msg);
	}
	
	/*
	 * 自定义Toast
	 * <p>解决Toast长时间轮流显示问题</p>
	 */
	public void DisplayToast(String str){
		if(myToast != null){
			myToast.setText(str);
		}else{
			myToast = Toast.makeText(BuyActivity2.this,str,Toast.LENGTH_SHORT);
		}
		myToast.show();
	}
	
	public void cancelToast(){
		if(myToast != null){
			myToast.cancel();
		}
	}

}
