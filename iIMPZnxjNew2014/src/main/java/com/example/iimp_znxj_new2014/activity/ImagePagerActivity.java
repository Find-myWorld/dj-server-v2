package com.example.iimp_znxj_new2014.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iimp_znxj_new2014.R;
import com.example.iimp_znxj_new2014.activity.CheckNoteActivity.StatusReceiver;
import com.example.iimp_znxj_new2014.util.Constant;
import com.example.iimp_znxj_new2014.util.Constant.Extra;
import com.example.iimp_znxj_new2014.util.JingMoOrder.Client;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * ViewPager页面显示Activity  当前协查通报使用
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class ImagePagerActivity extends ImageBaseActivity {

	private static final String STATE_POSITION = "STATE_POSITION";

	private static final String TAG = "ImagePagerActivity";

	DisplayImageOptions options;

	ViewPager pager;
	private String[] imageUrls;
	private int count = 0;
	private Handler mHandler;
	
	private String serverIp = null;
	private String serverPort = null;
	private String showTime = null;
	private String intervalTime = null;
	private String pictureNames = null;
	
	private int pagerPosition;
	
	private static final int INTERVAL_VALUE = 1;
	private static final int START_TIMING_VALUE = 2;
	private static final int FINISH_VALUE = 3;
	private static final int MESSAGE_SHOW_TIME_REACHED2 = 4;
	private static final int MESSAGE_SHOW_TIME_START = 5;
	private boolean isFinish = false;
	private StatusReceiver mStatusReceiver;

	private TextView mScrollText;
		
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_image_pager);

//		mScrollText = (TextView) findViewById(R.id.scroll_text_iamge_tv);

		Intent intent = this.getIntent(); 
		Bundle bundle = intent.getExtras(); 
		
		serverIp = bundle.getString(Constant.SLIDE_IP);
		serverPort = bundle.getString(Constant.SLIDE_PORT);
		showTime = bundle.getString(Constant.SLIDE_SHOWTIME);
		pictureNames = bundle.getString(Constant.SLIDE_NAME);
		intervalTime = bundle.getString(Constant.SLIDE_INTERVAL);
		
		String[] pic_split = pictureNames.split(",");
		Log.i("TTTS","22222...serverIp="+serverIp+","+serverPort+","+showTime+","+pictureNames+","+intervalTime+",length="+pic_split.length);
		imageUrls = new String[pic_split.length];
		for(int i = 0;i < pic_split.length;i++){
			imageUrls[i] = "http://"+serverIp+":"+serverPort+"/"+pic_split[i];
		}
		
		Log.i(TAG,"onCreate,pagerPosition="+pagerPosition+",imagerUrls.length="+imageUrls.length);

		// 如果之前有保存用户数据
		if (savedInstanceState != null) {
			pagerPosition = savedInstanceState.getInt(STATE_POSITION);
		}

		//初始化
		imageLoader.init(ImageLoaderConfiguration.createDefault(ImagePagerActivity.this));
		//ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(CheckNoteActivity.this));
		
		options = new DisplayImageOptions.Builder()
			.showImageForEmptyUri(R.drawable.ic_empty)
			.showImageOnFail(R.drawable.ic_error)
			.resetViewBeforeLoading(true)
			.cacheOnDisc(true)
			.imageScaleType(ImageScaleType.EXACTLY)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.displayer(new FadeInBitmapDisplayer(300))
			.build();

		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(new ImagePagerAdapter(imageUrls));
		
		pager.setCurrentItem(0);	// 显示当前位置的View

		new Thread(new MyThread()).start();
		mHandler = new Handler() { 
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case INTERVAL_VALUE:
						Log.i(TAG,"count="+count);
						pager.setCurrentItem(count++);	// 显示当前位置的View
						if(count == imageUrls.length){
							count = 0;
						}
						break;
					case START_TIMING_VALUE:
						Log.i(TAG,"开始计时："+showTime+" 分钟后发送结束命令。");
						int time = Integer.parseInt(showTime) * 60 * 1000;
						sendEmptyMessageDelayed(FINISH_VALUE, time); 
						break;
					case FINISH_VALUE:
						Log.i("TTSS","结束命令");
						ImagePagerActivity.this.finishAndGOtoIndex();	
						break;
						
					case MESSAGE_SHOW_TIME_START:
						int showTime1 = Integer.parseInt(mSubTitleShowTime) * 60 * 1000;
						sendEmptyMessageDelayed(MESSAGE_SHOW_TIME_REACHED2, showTime1);
						Log.i(TAG, "showTime:" + showTime1);
						break;
						
					case MESSAGE_SHOW_TIME_REACHED2:
//						mScrollText.setVisibility(View.INVISIBLE);
					break;
				}
			};
		};
		
		Message msg = new Message();
		msg.what = START_TIMING_VALUE;
		mHandler.sendMessage(msg);
		
		mStatusReceiver = new StatusReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.STOPPLAY_ACTION);
		filter.addAction(Constant.SUBTITLE_ACTION);
		filter.addAction(Constant.STOP_SUBTITLE_ACTION);
		registerReceiver(mStatusReceiver, filter);
	}
		

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// 保存用户数据
		outState.putInt(STATE_POSITION, pager.getCurrentItem());
	}
	
	public class MyThread implements Runnable { // thread
		@Override
		public void run() {
			while (!isFinish) {
				try {
					Message message = new Message();
					message.what = 1;
					mHandler.sendMessage(message);
					Thread.sleep(Integer.parseInt(intervalTime) * 1000); // sleep 1000ms
					Log.i(TAG,"睡眠"+intervalTime+"秒");
				} catch (Exception e) {
				}
			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i("base_key","KeyCode="+keyCode);
		return true;
	}
	private String mSubTitleShowTime;
	public class StatusReceiver extends BroadcastReceiver {
		

		@Override
		public void onReceive(Context context, Intent intent) {
			String actionStr = intent.getAction();
			Log.d("Test","actionStr = "+actionStr);
			 if(Constant.STOPPLAY_ACTION.equals(actionStr)) {
				 ImagePagerActivity.this.finishAndGOtoIndex();
				 Log.i("TTTS","广播命令结束");
			}
			 
//			 if (Constant.SUBTITLE_ACTION.equals(actionStr)) { // 汉字最少两位数就能滚屏
//					Bundle bundle = intent.getExtras(); //
//					String subTitle = bundle.getString(Constant.SUB_TITLE);
//					mScrollText.setVisibility(View.VISIBLE);
//					DisplayMetrics dm = new DisplayMetrics();
//					 getWindowManager().getDefaultDisplay().getMetrics(dm);
//				     int width = dm.widthPixels;    //手机屏幕水平分辨率
//				     int height = dm.heightPixels;  //手机屏幕垂直分辨率
//				    Log.e("MainActivity","屏幕长宽比："+height+"|"+width);
//					if(width==720){
//				    	mScrollText.setText("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + subTitle);
//				    }else if(width==800){
//				    	mScrollText.setText("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + subTitle);
//
//				    }else{
//				    	mScrollText.setText("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + subTitle);
//				    }
//					mSubTitleShowTime = bundle.getString(Constant.SUB_TIME);
//
//					Message msg = new Message();
//					msg.what = MESSAGE_SHOW_TIME_START;
//					mHandler.sendMessage(msg);
//					new Thread(new Client("playSubTitle:true")).start();
//					Log.i(TAG, "接收到字幕,显示时间：" + mSubTitleShowTime);
//				} 
			 
			  if (Constant.STOP_SUBTITLE_ACTION.equals(actionStr)) {
					mScrollText.setVisibility(View.INVISIBLE);
					new Thread(new Client("stopSubTitle:true")).start();
				}
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		isFinish = true;
		//结束时发送继续直播或者流媒体广播
		Intent in=new Intent();
		in.setAction(Constant.VCR_OR_VIDEO_CONTINUE);
		sendBroadcast(in);
	}

	private class ImagePagerAdapter extends PagerAdapter {

		private String[] images;
		private LayoutInflater inflater;

		ImagePagerAdapter(String[] images) {
			this.images = images;
			inflater = getLayoutInflater();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public void finishUpdate(View container) {
		}

		@Override
		public int getCount() {
		//	Log.i(TAG,"Length="+images.length);
			return images.length;
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);
			ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);
			Log.i(TAG,"position="+position);
			imageLoader.displayImage(images[position], imageView, options, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					spinner.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					String message = null;
					switch (failReason.getType()) {		// 获取图片失败类型
						case IO_ERROR:				// 文件I/O错误
							message = "Input/Output error";
							break;
						case DECODING_ERROR:		// 解码错误
							message = "Image can't be decoded";
							break;
						case NETWORK_DENIED:		// 网络延迟
							message = "Downloads are denied";
							break;
						case OUT_OF_MEMORY:		    // 内存不足
							message = "Out Of Memory error";
							break;
						case UNKNOWN:				// 原因不明
							message = "Unknown error";
							break;
					}
					Toast.makeText(ImagePagerActivity.this, message, Toast.LENGTH_SHORT).show();

					spinner.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					spinner.setVisibility(View.GONE);		// 不显示圆形进度条
				}
			});

			((ViewPager) view).addView(imageLayout, 0);		// 将图片增加到ViewPager
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View container) {
		}
	}
	
	

	//结束当前界面跳回主界面
	public void finishAndGOtoIndex() {
		finish();
		Intent intent = new Intent();
		intent.setClass(ImagePagerActivity.this, IndexActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
}