package com.example.iimp_znxj_new2014.selfconsume;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

/*
 * 购物模块,串口键盘，Application参数也要改，dimmen字体要改
 * 截止到：2015-11-18
 */
public class BuyActivity extends BaseActivity {
	DianJiaoApplication gv;

	
	
	/**
	 * 0表示可以刷卡，
	 * 1表示屏蔽刷卡
	 */
	private String isShowDialog="1";
	
	/**
	 * true 做清空操作
	 * false 不做清空
	 */
	private boolean isclear=false;
	int[] ids = new int[199];
	String[] names = new String[199];
	String[] explains = new String[199];
	String[] prices = new String[199];
	String[] picture = new String[199];
	boolean count_flag = true; // 只能接收一次getCount的值
	boolean add_flag = true;

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

	// private int[] idCode = new int[12]; // 记住卡的信息Code
	// private int[] idNumber = new int[10]; // 读卡的内容(Code转换成 0~9 编号)
	// private String sIdstr = null; // 编号(字符串)[发送]，格式：0006723410
	private String sGoodstr = null; // 商品信息[发送] 格式：3,2,1,3,5,9,12,4
	// private int number = 0;

//	private String isOk = null; // 服务器返回true/false
	private String balanceTv = null; // 服务器返回余额
	private String tatalTv = null; // 服务器返回本次总花费

	Bitmap bitmap = null;
	Bitmap[] bitmaps = new Bitmap[199];
	Bitmap dBitmap = null; // 创建一个默认的bitmap对象
	int num = 0;

	boolean flag = false;
	boolean mark = false;
	boolean keyFlag = true;
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

	// ImageView iv;
	// BuyInfo[] buys = null;
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

	private boolean isSwingCard = false;
	private boolean isFinished = false;
	private boolean isShowProcess = true;
	private boolean isOnCreate = true;

	private StatusReceiver mStatusReceiver;
	private Toast myToast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buy);

		dao = new GoodsDao(BuyActivity.this);

		Log.v(TAG, "onCreate");

		gv = (DianJiaoApplication) getApplication();
		getIp = PreferenceUtil.getSharePreference(BuyActivity.this, Constant.CONFIGURE_INFO_SERVERIP);
		getPort = PreferenceUtil.getSharePreference(BuyActivity.this, Constant.CONFIGURE_INFO_PORT);
		getCellNumber = PreferenceUtil.getSharePreference(BuyActivity.this, Constant.CONFIGURE_INFO_CELLNUMBER);
		serverIp = "http://" + getIp + ":" + getPort + "/";
		// serverIp = "http://192.168.1.253:6700/";
		Log.i(TAG, "配置信息：" + getIp + "|" + getPort + "|" + getCellNumber + "|" + serverIp);

		URL = serverIp + Constant.SERVER_PART+"ShoppingDataXml.aspx";
		msgURL = serverIp + Constant.SERVER_PART+"ProductManage.aspx";
		picURL = serverIp;

//		doNetWork("ShoppingDaily");

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
					if (!cardNum.equals("") && isSwingCard) {
						Message msg = new Message();
						msg.what = 2;
						Log.i(TAG, "卡号:" + cardNum);
						msg.obj = cardNum;
						myHandler.sendMessage(msg);
					}

					if (!startGetCardNum && !cardNum.startsWith("69") && count == 0) {
						int code = UnitaryCodeUtil.UnitaryCode(event.getKeyCode());
						ProcessKeyFunc(code);
						Log.i(TAG, "按键Code:" + code);
					}

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
//					 value=0;
					 isclear=true;
					 showSqlData();
//					 isShowDialog="1";
					 dialog.dismiss();
					 isSwingCard = false;
//					 handler.sendEmptyMessageDelayed(4, 1000);
//					 new ListViewAdapter().notifyDataSetChanged();
//					 doNetWork(commandArray[lrint]);  //刷新界面 
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
				public void onSuccess(int arg0, Header[] headers, byte[] responseBody) {
					try {
						String getXmlStr = new String(responseBody, "UTF-8").trim(); // 获取xml
						InputStream is = new ByteArrayInputStream(getXmlStr.getBytes());// 将xml转为InputStream类型
						Log.i(TAG, "doNetWork:" + is);
						showListView(is); // 解析xml
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
	 * 图片入库
	 * <p>根据图片的路径获取图片然后以二进制格式储存到数据库中</p>
	 * @params url 图片的路径
	 * @return null
	 */
	public void getImg(String url) {
		// Log.i(TAG,"getImg_url:"+url);
		// Log.i(TAG, "getImg=" + url);
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(url, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statuCode, Header[] headers, byte[] responseBody) {
				// TODO Auto-generated method stub
				if (statuCode == 200) {
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					BitmapFactory factory = new BitmapFactory();
					Bitmap bitmap = factory.decodeByteArray(responseBody, 0, responseBody.length);
					if (getBitmapSize(bitmap) < 1000000) {
						bitmap.compress(Bitmap.CompressFormat.PNG, 50, os);
					} else {
						dBitmap.compress(Bitmap.CompressFormat.PNG, 50, os);
					}
					dao.updateDataPic(os.toByteArray(), ids[countt], tableArray[lrint]);// 根据id更新图片（添加图片）
					countt++;
					if (countt == count) {
						showSqlData(); // 查询数据库中数据在listview中
						closeProcessDialog();
					}
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				countt++;
				if (countt == count) {
					showSqlData(); // 查询数据库中数据在listview中
					closeProcessDialog();
				}
				Log.i(TAG, "加载默认图片........");
			}
		});
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
		/*Cursor css = dao.idQueryCount(countArray[lrint]);
		while (css.moveToNext()) {
			sqlCount = css.getInt(0); // 取出数据库中count值，用于判断是读取数据库还是直接显示
		}
		css.close();*/
		isclear=false;
		sqlCount = 0;
		if (lrint == 0) {
			showListView1(is);
		} else if (lrint == 1) {
			showListView2(is);
		} else if (lrint == 2) {
			showListView3(is);
		}
	}

	/*
	 * 解析服务器返回的xml文件
	 */
	public void showListView1(InputStream is) {
		BuyDailyAnalyze service = new BuyDailyAnalyze();
		try {
			List<BuyShopping> newslist = service.getBuyDaily(is);
			if(newslist.size() == 1){
				closeProcessDialog();
				DisplayToast("  无商品数据！  ");
			}
			for (BuyShopping news : newslist) {
				if (count_flag) { // 标志位，用于判断
					getCount = news.getCount();
					if (getCount == null) {
						continue;
					}
					int intCount = Integer.parseInt(getCount);
					// Log.i(TAG, "intCount=" + intCount + " ,sqlCount="
					// + sqlCount + " ," + tableArray[lrint]);
					if (intCount > sqlCount) { // 如果大于，表示服务器数据更新了。需重新入库，读库操作
						dao.deleteTable(tableArray[lrint]);
						// dao.createTable(tableArray[lrint]);
						dao.updateCount(countArray[lrint], getCount);
						count_flag = false; // 更改标志位，下次直接执行：else if内容
					} else { // 表示服务器数据未更新，直接读取数据库数据就好
						addDataToArray();
						showSqlData();
						add_flag = false;
					}

				} else if (add_flag) {
					ids[count] = news.getId();
					prices[news.getId()] = news.getPrice();
					getImg(picURL + news.getPicture()); // 调用图片入库方法
					data = new Goods(news.getId(), news.getName(), news.getExplain(), news.getPrice());
					dao.addData(tableArray[lrint], data); // 商品信息存入数据库（不含图片信息）
					count++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 将数据库转到存数组中
	 */
	public void addDataToArray() {
		int i = 0;
		Cursor cs = dao.queryTable(tableArray[lrint]);// listview显示内容一样，光标可以下移
		
		while (cs.moveToNext()) {
			
			ids[i] = cs.getInt(0);//
			prices[cs.getInt(0)] = cs.getString(3); // 不知道为什么是3，不是1
			Log.v(TAG, prices[cs.getInt(0)]);
			i++;
		}
		cs.close();///
	}

	/*
	 * 解析服务器返回的xml文件
	 */
	public void showListView2(InputStream is) {
		BuyLaborAnalyze service = new BuyLaborAnalyze();
		try {
			List<BuyShopping> newslist = service.getBuyLabor(is);
			if(newslist.size() == 1){
				closeProcessDialog();
				DisplayToast("  无商品数据！  ");
			}
			for (BuyShopping news : newslist) {
				if (count_flag) {
					getCount = news.getCount(); // getCount应该有误？？？？？左右按键切换，获取不到值
					int intCount = Integer.parseInt(getCount); //
					if (intCount > sqlCount) { // 如果大于，表示服务器数据更新了。需重新入库，读库操作
						dao.deleteTable(tableArray[lrint]);
						// dao.createTable(tableArray[lrint]);
						dao.updateCount(countArray[lrint], getCount);// 将新的count值存入数据库
						count_flag = false;
					} else { // 表示服务器数据未更新，直接读取数据库数据就好
						addDataToArray();
						showSqlData(); // 显示内容在屏幕上
						add_flag = false;
					}

				} else if (add_flag) {
					ids[count] = news.getId();// 保留，购物信息需要
					prices[news.getId()] = news.getPrice();// 保留，购物信息需要
					getImg(picURL + news.getPicture()); // 调用图片入库方法
					data = new Goods(news.getId(), news.getName(), news.getExplain(), news.getPrice());// 数据入库操作，不含图片入库
					dao.addData(tableArray[lrint], data);
					count++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 解析服务器返回的xml文件
	 */
	public void showListView3(InputStream is) {
		BuyFoodAnalyze service = new BuyFoodAnalyze();
		try {
			List<BuyShopping> newslist = service.getBuyFood(is);
			if(newslist.size() == 1){
				closeProcessDialog();
				DisplayToast("  无商品数据！  ");
			}
			for (BuyShopping news : newslist) {
				Log.v(TAG, news.getName()+"  "+news.getPrice());
				if (count_flag) {
					getCount = news.getCount(); // getCount应该有误？？？？？左右按键切换，获取不到值
					int intCount = Integer.parseInt(getCount); //
					if (intCount > sqlCount) { // 如果大于，表示服务器数据更新了。需重新入库，读库操作
						dao.deleteTable(tableArray[lrint]);
						// dao.createTable(tableArray[lrint]);
						dao.updateCount(countArray[lrint], getCount);// 将新的count值存入数据库
						count_flag = false;
					} else { // 表示服务器数据未更新，直接读取数据库数据就好
						Log.v(TAG, "服务器未更新");
						addDataToArray();
						showSqlData();
						add_flag = false;
					}

				} else if (add_flag) {
					ids[count] = news.getId();// 保留，购物信息需要
					prices[news.getId()] = news.getPrice();// 保留，购物信息需要
					getImg(picURL + news.getPicture()); // 调用图片入库方法
					data = new Goods(news.getId(), news.getName(), news.getExplain(), news.getPrice());// 数据入库操作，不含图片入库
					dao.addData(tableArray[lrint], data);
					count++;
				}
			}
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
	 * 调用加载方法
	 */
	public void showSqlData() {
		list.setAdapter(new ListViewAdapter());
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
			Log.i("TTSS", "ListViewAdapter,getCount=" + tableArray[lrint] + "," + dao.getTableCount(tableArray[lrint]));
			return dao.getTableCount(tableArray[lrint]); // 显示当前表中数据的条数
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
				LayoutInflater inflater = (LayoutInflater) BuyActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
			Cursor cs = dao.idQueryTable(ids[position] + "", tableArray[lrint]);
			while (cs.moveToNext()) {
				String name = cs.getString(0);//
				holder.text1.setText(name);// /
				String explain = cs.getString(2);
				holder.text3.setText(explain);
				String price = cs.getString(1);// /
				holder.text2.setText("单价￥" + price);
				
				if (isclear) {
					Log.e("isclear", isclear+"");
					holder.text5.setText("0");
					
				}
				byte[] in = cs.getBlob(3);
				try {
					if (in != null) {
						Bitmap bit = BitmapFactory.decodeByteArray(in, 0, in.length);
						if (bit != null) {
							holder.imgview.setImageBitmap(bit);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			cs.close();///
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

		dBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.buy_produres_pic);
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
		
		Log.i("ABCD", "ProcessKeyFunc:" + keyCode + ",isSwingCard=" + isSwingCard);
		int counter = dao.getTableCount(tableArray[lrint]);
		fillImageButton();

		if (gv.getmKey_Right() == keyCode && !isSwingCard) {
			if(isShowProcess){
				showProcessDialog();
			}
			count = countt = udint = 0;
			count_flag = add_flag = isShowProcess =true;
			lrint = lrint + 1;
			if (lrint > 2) {
				lrint = 0;
			} else if (lrint == 1) {
			}
			doNetWork(commandArray[lrint]);
		}
		if (gv.getmKey_Left() == keyCode && !isSwingCard) {
			if(isShowProcess){
				showProcessDialog();
			}
			count = countt = udint = 0;
			count_flag = add_flag = isShowProcess =true;
			lrint = lrint - 1;
			if (lrint < 0) {
				lrint = tableArray.length - 1;
			} else if (lrint == 1) {
				
			}
			doNetWork(commandArray[lrint]);
		}
		if (gv.getmKey_Up() == keyCode && !isSwingCard) {
			udint = udint - 1;
			if (udint <= 0)
				udint = 0;
		}
		if (gv.getmKey_Down() == keyCode && !isSwingCard) {
			udint = udint + 1;
			if (udint >= counter)
				udint = counter;
		}

		if (counter != 0 && keyCode >= gv.getmKey_0() && keyCode <= gv.getmKey_9() && !isSwingCard) { // 输入0-9才做数据操作
			// Log.i(TAG,"当前counter="+counter);
			value = keyCode - 48;
			Log.i(TAG, "当前value=" + value);

			if (et == null) {
				return;
			}
			int currentNum = Integer.parseInt(et.getText().toString());

			if (currentNum <= 9 && currentNum > 0) {
				value = currentNum * 10 + keyCode - 48;
				Log.i("TS", "第二位数：" + value);
			}
			if (String.valueOf(value).length() == 1) {
				//
				Log.i("TS", "一位数");
			}
			if (String.valueOf(value).length() == 2) {
				// Log.i("TS","二位数");
			}
			et.setText("" + value);
			setShuzu(ids[curSelectID], value);
			// Log.i("TS","ids,value"+curSelectID+"|"+ids[curSelectID]+"|"+value);
		}

		list.requestFocusFromTouch();
		list.setSelection(udint);
		image[lrint].setImageResource(Rdrawsel[lrint]);

		if (gv.getmKey_Enter() == keyCode) // F1键 进入确认界面（刷卡页面）
		{
			
			if (getGoodsSum() == 0 && !isCard) {
				Toast.makeText(getApplicationContext(), "您还未选中任何商品，无法进入支付页面！", Toast.LENGTH_LONG).show();
			} else {
				getGoodsStr(); // 获取商品信息
				isShowDialog="0";
				showDialogWin(CONFIRE_FLAG_VALUE);
				isSwingCard = true;
			}
		}

		if (gv.getmKey_Esc() == keyCode) {
			if (isSwingCard) {
				Log.i(TAG, "取消对话框。。。");
				isSwingCard = false;
				dialog.dismiss();
			} else {
				Log.i(TAG, "退出程序。。。。");
				// this.finish();
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
		for (int i = 1; i < 200; i++) {
			for (int j = 1; j < 200; j++) {
				spcart[i][j] = 0;
			}
		}
	}

	/*
	 *  购买操作
	 *  <p>数组记录购买信息</>
	 */
	public void setShuzu(int i, int j) { // 购买操作 (1<=i<=200,0
											// (0<=j<200),商品编号1~200，商品数量0~199
		if (j == 0) { // 置零操作
			for (int y = 1; y < 200; y++) {
				spcart[i][y] = 0;
			}
		} else {
			for (int y = 1; y < 200; y++) { // 判断id(商品)对应"数量"是否有值，有值的话，先置0，再做数量操作
				if (spcart[i][y] == 1) {
					spcart[i][y] = 0;
				}
			}
			spcart[i][j] = 1;
		}
	}

	/*
	 *  获取已购买的商品信息
	 *  @return strGoods 格式：1,2,8,3,4,2,
	 */
	public String getGoodsStr() {
		for (int i = 1; i < 200; i++) {
			for (int j = 1; j < 200; j++) {
				if (spcart[i][j] == 1) {
					goodsSb.append(i + "," + j + ",");
				}
			}
		}
		sGoodstr = goodsSb.toString();
		String strGoods = goodsSb.toString().trim();
		goodsSb.delete(0, goodsSb.length()); // 清空StringBuilder，否则累加
		return strGoods;
	}

	/*
	 *  获取所选商品的数量/种类
	 */
	public int getGoodsSum() { // 统计商品种类，不应超过20种 或者 商品数量，不超过20件
		int sum = 0;
		for (int i = 1; i < 200; i++) {
			for (int j = 1; j < 200; j++) {
				if (spcart[i][j] == 1) {
					sum += j;
				}
			}
		}
		return sum;
	}

	/*
	 *  获取所选商品的总价
	 */
	public BigDecimal getGoodsPrice() {
		double price = 0;
		for (int i = 1; i < 200; i++) {
			for (int j = 1; j < 200; j++) {
				if (spcart[i][j] == 1) {
					// price += Double.parseDouble(prices[i-1]);
					// //此处price是double类型。【i-1是因为商品id是从0开始的】
					price = price + j * Double.parseDouble(prices[i]); // 价格 =
																		// 单价 *
																		// 数量；
					Log.i(TAG, "选中的id+num:" + i + "," + j);
				}
			}
		}
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
		intent.setClass(BuyActivity.this, IndexActivity.class);// 从本类的activity跳转到目标activity。
		startActivityForResult(intent, 0);// 执行目标activity
		this.finish();
	}
	
	
	/**
	 * 获取所有列表中的商品数量框，并进行重置操作
	 * */
	
	public class ListViewAdapter1 extends BaseAdapter {
		View[] itemViews;

		public ListViewAdapter1() {
		}

		public int getCount() {
			Log.i("TTSS", "ListViewAdapter,getCount=" + tableArray[lrint] + "," + dao.getTableCount(tableArray[lrint]));
			return dao.getTableCount(tableArray[lrint]); // 显示当前表中数据的条数
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
				LayoutInflater inflater = (LayoutInflater) BuyActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
			for (int i = 0; i < 3; i++) {
				Cursor cs = dao.idQueryTable(ids[position] + "", tableArray[i]);
				while (cs.moveToNext()) {
					holder.text5.setText("");
				}
				cs.close();
			}
			///
			return convertView;
		}
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
				String cardNumber = msg.obj.toString();
				if (!isFinished && !isPaused) { // 没有结束才执行下一步
					Log.i(TAG, "handler接收到卡号:" + cardNumber);
					useStartsWithout0 = true;
					doNetWorkSendMsg(getGoodsStr(), cardNumber,1); // 发送信息给服务器
					initShuzu(); // 成功付款一次后初始化购物车数组（不然关闭刷卡窗口后，购物车数组里还有数据）
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

			isShowDialog="0";
			Log.e("执行顺序", "执行顺序");
			String cardNum = getUSBCardNum(event);
			if (!cardNum.equals("") && isSwingCard) {
				Message msg = new Message();
				msg.what = 2;
				Log.i(TAG, "卡号:" + cardNum);
				msg.obj = cardNum;
				myHandler.sendMessage(msg);
				isCard = true;
			}

			if (!startGetCardNum) {
				if("1".equals(isShowDialog)){
					return false;
				}else{
					int code = UnitaryCodeUtil.UnitaryCode(event.getKeyCode());
					ProcessKeyFunc(code);
					Log.e(TAG, "按键Code:" + code);
				}
				Log.e(isShowDialog, "isShowDialog" + isShowDialog);
				
				}
				

		}
		return true;
	}

	StringBuilder cardNumSB = new StringBuilder();
	boolean startGetCardNum = false;



	private boolean cardnumstart;

	// 接收USB卡号
	public String getUSBCardNum(KeyEvent event) {
		
//		if("1".equals(isShowDialog)){
//			return "";
//		}
		int code = event.getKeyCode();
		Log.e("getUSBCardNum", code + "");
		if (code == 74) {
			cardnumstart=true;
			startGetCardNum = true;
			return "";
		}
		if (code == 59) {
			String cardNum = cardNumSB.toString();
			cardNumSB.delete(0, cardNumSB.length());
//			startGetCardNum = false;
			cardnumstart=false;
			isShowDialog="1";
			Log.i(TAG, cardNum);
			Log.e("isShowDialog", isShowDialog);
			Log.e("cardNum", cardNum);
			return cardNum;
		}
		
		if (code == 76) {
			
			
		}
		if(code ==66 &&startGetCardNum){
			startGetCardNum = false;
			isShowDialog="1";
//			Log.e("cardNum", cardNum);
		}
		
		if (cardnumstart) {
			cardNumSB.append(code - 7);
		}
		count--;
		Log.v(TAG, count + "");
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
		msg.what = 1;
		msg.arg2 = keyCode;
		myHandler.sendMessage(msg);

	}

	@Override
	public void serial_CardDo(String cardNum) {

		super.serial_CardDo(cardNum);
		Message msg = new Message();
		msg.what = 2;
		msg.obj = cardNum;
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
			myToast = Toast.makeText(BuyActivity.this,str,Toast.LENGTH_SHORT);
		}
		myToast.show();
	}
	
	public void cancelToast(){
		if(myToast != null){
			myToast.cancel();
		}
	}

}
