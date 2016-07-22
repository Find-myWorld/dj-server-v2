package com.example.iimp_znxj_new2014.activity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.Header;

import com.example.iimp_znxj_new2014.Config;
import com.example.iimp_znxj_new2014.DianJiaoApplication;
import com.example.iimp_znxj_new2014.R;
import com.example.iimp_znxj_new2014.entity.GoodsQuery;
import com.example.iimp_znxj_new2014.entity.GoodsQueryAnalyze;
import com.example.iimp_znxj_new2014.util.Constant;
import com.example.iimp_znxj_new2014.util.PreferenceUtil;
import com.example.iimp_znxj_new2014.util.UnitaryCodeUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/*
 * 账单查询
 */
public class BillQueryActivity extends BaseActivity {

	DianJiaoApplication gv;
	int lrint = 0, udint = 0;
	/*	private Dialog dialog = null;
		private int Confirm_BeSure=1;
		private int Confirm_DoNo=2;
		private int Confirm_DoYes=3;*/

	// private String URL =
	// "http://192.168.1.253:6700/androidadmin/webservice/ConsumeQuery.aspx";

	private String URL = null;
	private boolean flag = false;
	private boolean mark = false;
	private final static String TAG = "BillQueryActivity";
	private StringBuilder cardIdSb = new StringBuilder(); // 数组转化为字符串；卡号信息
	private StringBuilder goodsSb = new StringBuilder(); // 数组转化为字符串；商品id +
															// 商品num
	private int[] idCode = new int[12]; // 记住卡的信息Code
	private int[] idNumber = new int[10]; // 读卡的内容(Code转换成 0~9 编号)
	private int num = 0;
	private String sIdstr = null; // 编号(字符串)[发送]，格式：0006723410
	private boolean isSwingCard = false; // 判断标志：规定只有在弹出对话框页面才能做刷卡操作

	private int count = 0;
	private int[] ids = new int[64];
	private String[] times = new String[64];
	private String[] contents = new String[64];
	private String[] prices = new String[64];
	private String[] dates = new String[64];

	private TextView cardTv, balanceTv;
	private ListView list;
	private View oldv = null;

	String serverIp;
	String serverPort;

	private Handler handler;
	private String balance = null;

	private Toast myToast;
	private boolean isShowDig = false;

	private boolean isNoBody = false;

	private TextView tipTextView;

	private Dialog progressDialog;// 缓冲时显示

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.love_checkconsume_info);

		gv = (DianJiaoApplication) getApplication();

		serverIp = PreferenceUtil.getSharePreference(BillQueryActivity.this, Constant.CONFIGURE_INFO_SERVERIP);
		serverPort = PreferenceUtil.getSharePreference(BillQueryActivity.this, Constant.CONFIGURE_INFO_PORT);

		URL = "http://" + serverIp + ":" + serverPort +"/"+Constant.SERVER_PART+ "ConsumeQuery.aspx";
		cardTv = (TextView) findViewById(R.id.consume_sure_item_textView2);
		balanceTv = (TextView) findViewById(R.id.consume_balance_textView3);

		list = (ListView) findViewById(R.id.consume_info_listView);

		list.setDivider(null);
		list.setSelected(true);
		list.setCacheColorHint(0);
		list.setOnItemSelectedListener(new myitemselect()); // 为ListView子项添加选中事件
		Drawable drawable = getResources().getDrawable(R.drawable.message_item_list_sytle);
		list.setSelector(drawable);

		tipTextView = (TextView) findViewById(R.id.consume_sure_item_textView1);

	}

	@Override
	protected void onStart() {
		handler = new Handler() { // handle
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					balanceTv.setText("余额:" + msg.obj);
					break;
				case 2:
					isShowDig = true;
					Log.i(TAG, "2s后重置判断标志");
					break;
				case 3:
					int remaindTime = (Integer) msg.obj;
					tipTextView.setText("剩余" + remaindTime + "秒后返回首页");
					break;
				case 4:
					tipTextView.setText("请在当前页面刷卡！");
					break;
				}
				super.handleMessage(msg);
			}
		};

		noBodyGotoIndex();
		handler.sendEmptyMessageDelayed(2, 2000);
		super.onStart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.love_info, menu);
		return true;
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public class myitemselect implements OnItemSelectedListener { // ListView子项选择事件-----选中就触发（如使用方向键改变选择对象）
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			if (oldv == null) {
				oldv = arg1;
				arg1.setBackgroundResource(R.drawable.itemsel5); // 为选中子项加高亮
			} else {
				Log.i(TAG, "接收到item变化");
				oldv.setBackgroundColor(Color.TRANSPARENT);
				arg1.setBackgroundResource(R.drawable.itemsel5); // 为选中子项加高亮
				oldv = arg1;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

			arg0.setBackgroundColor(Color.TRANSPARENT);
		}
	}

	/*
	 * 键盘监听函数，左右键没用，上下键可用
	 */
	private void ProcessKeyFunc(int keyCode) {

		countTime = Config.NOBODY_GOTOINDEX_TIME; // 有操作跳转时间重置

		if (gv.getmKey_Right() == keyCode) {
			lrint = lrint + 1;
			if (lrint >= 3)
				lrint = 3;
		}

		if (gv.getmKey_Left() == keyCode) {
			lrint = lrint - 1;
			if (lrint <= 0)
				lrint = 0;
		}

		if (gv.getmKey_Up() == keyCode) {
			udint = udint - 1;
			if (udint <= 0)
				if (list != null) {
					try {
						udint = list.getAdapter().getCount();
					} catch (Exception e) {
						return;
					}
				}

		}
		if (gv.getmKey_Down() == keyCode) {
			udint = udint + 1;
			if (udint >= count)
				udint = 1;
		}

		if (gv.getmKey_Enter() == keyCode) {

		}

		Message msg = new Message();
		msg.obj = udint;
		changeSelectedHandler.sendMessage(msg);

		if (gv.getmKey_Esc() == keyCode) {
			changePage(0);
		}

	}

	Handler changeSelectedHandler = new Handler() {
		public void handleMessage(Message msg) {

			Log.i(TAG, "改变Select");
			int position = (Integer) msg.obj;
			Log.i(TAG, list.getSelectedItemPosition() + "");
			list.requestFocusFromTouch();
			list.setSelection(position - 1);

		};
	};

	static class ViewHolder {
		private TextView numbers;
		private TextView time;
		private TextView contents;
		private TextView prices;
	}

	// 生成动态数组，并且转载数据
	public class ListViewAdapter extends BaseAdapter {

		public ListViewAdapter() {

		}

		public int getCount() {
			// Log.i(TAG,"ListViewAdapter_Count="+count);
			return count - 1; // 减掉的那个是余额
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
				LayoutInflater inflater = (LayoutInflater) BillQueryActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				// 使用View的对象itemView与R.layout.item关联
				convertView = inflater.inflate(R.layout.love_checkconsume_list, null);
				holder = new ViewHolder();
				// 通过findViewById()方法实例R.layout.item内各组件
				holder.numbers = (TextView) convertView.findViewById(R.id.love_checkconsume_list_tv1);
				holder.time = (TextView) convertView.findViewById(R.id.love_checkconsume_list_tv2);
				holder.contents = (TextView) convertView.findViewById(R.id.love_checkconsume_list_tv3);
				holder.prices = (TextView) convertView.findViewById(R.id.love_checkconsume_list_tv4);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			// Log.i(TAG,"position&&count:"+position+"|"+count);
			if (position != count) {
				holder.numbers.setText(position + 1 + ".");
				holder.time.setText(dates[position + 1]);
				holder.contents.setText(contents[position + 1]);
				holder.prices.setText(prices[position + 1] + "￥");
			}
			return convertView;
		}
	}

	private boolean useStartsWithout0 = false;// 是否使用不已0开头的卡号
	// 联网操作(发送卡号信息给服务器)；只有发送，没有接收

	public void doNetWorkSendMsg(final String cardsMsg) {
		countTime = Config.NOBODY_GOTOINDEX_TIME; // 有操作跳转时间重置

		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("cardNumber", cardsMsg);
		params.put("type", "1");
		params.setContentEncoding("UTF-8");
		Log.i(TAG, URL);
		client.post(URL, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, Header[] headers, byte[] responseBody) {
				try {
					String getXmlStr = new String(responseBody, "UTF-8").trim(); // 获取xml
					InputStream is = new ByteArrayInputStream(getXmlStr.getBytes());// 将xml转为InputStream类型
					showListView(is); // 解析xml
					Log.i(TAG, "解析完成");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				if (useStartsWithout0) {
					String cardsMsg_replaced = UnitaryCodeUtil.removeStarts0(cardsMsg);
					doNetWorkSendMsg(cardsMsg_replaced);
					Log.v(TAG, "去掉开头的0后卡号：" + cardsMsg_replaced);
					useStartsWithout0 = false;
				} else {
					Toast.makeText(getApplicationContext(), "网络延时，与服务器连接失败...", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	public void showListView(InputStream is) {
		GoodsQueryAnalyze service = new GoodsQueryAnalyze();
		try {
			List<GoodsQuery> newslist = service.getGoodsQuery(is);
			count = 0;
			for (GoodsQuery news : newslist) {
				ids[count] = news.getId();
				contents[count] = news.getContent();
				prices[count] = news.getPrice();
				balance = prices[0];
				dates[count] = news.getDate();
				count++;
			}

			Message msg = new Message();
			msg.what = 1;
			msg.obj = balance;
			handler.sendMessage(msg);

			if (count == 1) {
				Toast.makeText(BillQueryActivity.this, "暂无此卡消费记录！", Toast.LENGTH_LONG).show();
			}

			list.setAdapter(new ListViewAdapter()); // 在listview中显示数据（xml）
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* 
	 * 页面跳转
	 */
	public void changePage(int menustr) {
		this.finish();
		isShowDig = false;
		Intent intent = new Intent();// 新建一个activity
		intent.setClass(BillQueryActivity.this, IndexActivity.class);// 从本类的activity跳转到目标activity。
		startActivityForResult(intent, 0);// 执行目标activity
		setResult(1, intent);
		getWindow().setWindowAnimations(2000);
		overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
	}

	public void changePage() {
		isShowDig = false;
		this.finish();
		Intent intent = new Intent();// 新建一个activity
		intent.setClass(BillQueryActivity.this, IndexActivity.class);
		startActivity(intent);
		setResult(1, intent);
		getWindow().setWindowAnimations(2000);
		overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

	}

	/*
	 * 自定义Toast
	 * <p>解决Toast长时间轮流显示问题</p>
	 */
	public void DisplayToast(String str) {
		if (myToast != null) {
			myToast.setText(str);
		} else {
			myToast = Toast.makeText(BillQueryActivity.this, str, Toast.LENGTH_SHORT);
		}
		myToast.show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private boolean isFinished = false;

	/*
	 * 处理串口线程发送的keycode
	 */
	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:

				int keyCode = msg.arg2;
				Log.i(TAG, "Handler,接收到KeyCode=" + keyCode);

				if (!isFinished) { // 没有结束才执行下一步
					ProcessKeyFunc(keyCode);
					Log.i(TAG, "按键操作进行..");
				} else {
					Log.i(TAG, "已经结束，本页面以关闭，按键无响应");
				}
				break;
			case 2:
				sIdstr = msg.obj.toString();
				Log.i(TAG, "myHandler....卡号为：" + sIdstr);
				useStartsWithout0 = true;
				doNetWorkSendMsg(sIdstr);
				// 刷卡操作
				break;
			}
			super.handleMessage(msg);
		}
	};

	/*
	 * USB按键处理
	 */

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {

		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			int keyCode = event.getKeyCode();

			int code = UnitaryCodeUtil.UnitaryCode(keyCode);
			
			if(!startGetCardNum){
				ProcessKeyFunc(code);
			}
			String cardNum = getUSBCardNum(event);
			if (!cardNum.equals("")) {
				useStartsWithout0 = true;
				doNetWorkSendMsg(cardNum);
			}
		}
		return false;
	}

	StringBuilder cardNumSB = new StringBuilder();
	boolean startGetCardNum = false;

	// 接收USB卡号
	public String getUSBCardNum(KeyEvent event) {
		int code = event.getKeyCode();
		if (code == 74) {
			startGetCardNum = true;
			return "";
		}
		if (code == 59) {
			String cardNum = cardNumSB.toString();
			cardNumSB.delete(0, cardNumSB.length());
			startGetCardNum = false;
			Log.i(TAG, cardNum);
			return cardNum;
		}
		if (startGetCardNum) {
			cardNumSB.append(code - 7);
		}
		return "";
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == 4) {
			changePage();
		}
		return true;
	}

	/*
	 * 两分钟无操作自动调回主界面
	 */
	private int countTime; // 返回主界面的时间
	private boolean backHomeFlag = false;

	public void noBodyGotoIndex() {
		countTime = Config.NOBODY_GOTOINDEX_TIME;
		backHomeFlag = true;
		new Thread() {
			public void run() {
				while (backHomeFlag) {
					try {
						if (countTime <= 0) {
							BillQueryActivity.this.finish();
							Intent intent = new Intent();
							intent.setClass(BillQueryActivity.this, IndexActivity.class);
							startActivity(intent);
						} else if (countTime < 60) {
							Message msg = new Message();
							msg.what = 3;
							msg.obj = countTime;
							handler.sendMessage(msg);
						} else {
							Message msg = new Message();
							msg.what = 4;
							handler.sendMessage(msg);
						}
						countTime--;
						Log.i(TAG, countTime + "秒后回到主界面");
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			};

		}.start();
	}

	private boolean isPaused = false;

	@Override
	protected void onPause() {
		myHandler.removeMessages(1);
		myHandler.removeMessages(2);
		isPaused = true;
		backHomeFlag = false;
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

}
