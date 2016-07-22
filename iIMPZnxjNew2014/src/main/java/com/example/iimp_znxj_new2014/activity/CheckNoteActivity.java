package com.example.iimp_znxj_new2014.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilePermission;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.EmptyStackException;

import javax.xml.validation.Schema;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SimpleAdapter;

import com.example.iimp_znxj_new2014.R;
import com.example.iimp_znxj_new2014.activity.PlayLocalVideoActivity.StatusReceiver;
import com.example.iimp_znxj_new2014.util.BitmapUtil;
import com.example.iimp_znxj_new2014.util.Constant;
import com.example.iimp_znxj_new2014.util.FileUtils;
import com.example.iimp_znxj_new2014.util.JingMoOrder;
import com.example.iimp_znxj_new2014.util.JingMoOrder.Client;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions.Builder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.download.ImageDownloader.Scheme;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/*
 * 协查通报
 */
public class CheckNoteActivity extends BaseActivity {
	private static final int MESSAGE_CHECK_NOTE_PIC = 1;
	private static final int MESSAGE_SHOW_TIME_REACHED = 2;
	private ImageView mCheckNoteImageView;
	private String mCheckNoteShowTime;
//	private String mUrl;///
	private PicThread mPicThread;
	private StatusReceiver mStatusReceiver;
	private String checkNoteFileName;
	private String imagePath;
	
	private static final String TAG = "CheckNoteActivity";
	private static final String STOPPLAY_ACTION = "com.example.iimp_znxj_new2014.stopPlay";

	private boolean flag = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.check_note);
		Intent intent = this.getIntent(); 
		Bundle bundle = intent.getExtras(); 
		String checkNoteIp = bundle.getString(Constant.CHECK_NOTE_SERVER_IP);
	    checkNoteFileName = bundle
				.getString(Constant.CHECK_NOTE_PIC_NAME);
		String port = bundle.getString(Constant.CHECK_NOTE_PORT);
		mCheckNoteShowTime = bundle.getString(Constant.CHECK_NOTE_SHOW_TIME);
		
		mCheckNoteImageView = (ImageView) findViewById(R.id.check_note_pic);
		
		Log.i(TAG,"imagePath:"+checkNoteIp+"|"+port+"|"+checkNoteFileName+"|"+mCheckNoteShowTime);
		
		//初始化，创建默认的ImageLoader配置参数
		ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(CheckNoteActivity.this));
		
		if(JingMoOrder.fileIsExists("/mnt/extsd/Download/"+checkNoteFileName)){ //本地存在该路径
			imagePath = "/mnt/extsd/Download/"+checkNoteFileName;
			new Thread(new Client("checknote:true")).start();
			//创建默认的ImageLoader配置参数
		//	ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
		//	ImageLoader.getInstance().init(configuration);
			@SuppressWarnings("deprecation")
			DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageOnFail(R.drawable.ic_error)
				.cacheInMemory(true)
				.cacheOnDisc(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.build();
			String imageUrl = Scheme.FILE.wrap(imagePath);
			ImageLoader.getInstance().displayImage(imageUrl, mCheckNoteImageView, options); 
		}else if(!checkNoteIp.equals("") && !port.equals("")){                   //加载网络路径图片
			 imagePath = "http://"+checkNoteIp+":"+port+"/"+checkNoteFileName;
			 Log.i(TAG,"imagePath2:"+imagePath);
			 
			 ImageLoader.getInstance().loadImage(imagePath, new SimpleImageLoadingListener(){
				@Override
				public void onLoadingComplete(String imageUri, View view,
						Bitmap loadedImage) {
					// TODO Auto-generated method stub
					Log.i(TAG,"onLoadingComplete");
					new Thread(new Client("checknote:true")).start();
					super.onLoadingComplete(imageUri, view, loadedImage);
					mCheckNoteImageView.setImageBitmap(loadedImage);
   				}

				@Override
				public void onLoadingFailed(String imageUri, View view,
						FailReason failReason) {
					// TODO Auto-generated method stub
					new Thread(new Client("checknote:false,no such file")).start();
					Log.i(TAG,"onLoadingComplete");
					super.onLoadingFailed(imageUri, view, failReason);
					CheckNoteActivity.this.finish();
					Intent intent2 = new Intent(CheckNoteActivity.this,IndexActivity.class);
					startActivity(intent2);
				}
			 });  
			 Log.i(TAG,"imagePath2");
		}else{
			Log.i(TAG,"imagePath3");
			flag = false;
			new Thread(new Client("checknote:false,no such file")).start();
			CheckNoteActivity.this.finish();
			Intent intent2 = new Intent(CheckNoteActivity.this,IndexActivity.class);
			startActivity(intent2);
		}
		
		if(flag){
			Message msg = new Message();
			msg.what = MESSAGE_CHECK_NOTE_PIC;
			mCheckNoteShowHandler.sendMessage(msg);
			
			mStatusReceiver = new StatusReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(STOPPLAY_ACTION);
			registerReceiver(mStatusReceiver, filter);
		}
		
	}

	class PicThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
			//	Bitmap bitmap = BitmapUtil.getImage(CheckNoteActivity.this,  ///
			//			mUrl);///
				String path = "/mnt/extsd/Download/"+checkNoteFileName;
				if(!fileIsExists(path)){
					Log.i(TAG,"文件不存在，结束退回组界面");
					new Thread(new Client("checknote:false,no such file")).start();
					CheckNoteActivity.this.finish();
					Intent intent2 = new Intent(CheckNoteActivity.this,IndexActivity.class);
					startActivity(intent2);
				}else{
					Message msg = new Message();
					msg.what = MESSAGE_CHECK_NOTE_PIC;
					mCheckNoteShowHandler.sendMessage(msg);
				}
			} catch (Exception e) {
				Log.i("TS","Error:"+e.getMessage());
				new Thread(new Client("checknote:false")).start();
				e.printStackTrace();
			}
			super.run();
		}
	}
	
	/*
	 * 缩放的算法
	 */
	@SuppressLint("NewApi")
	public void setZoom(int height,int width){
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int windowHeight = size.y;  //720
		int windowWidth = size.x;   //1280
		Log.i(TAG,"屏幕长宽："+windowHeight+"|"+windowWidth);
		
		float value1 = (float)windowHeight/height;
		float value2 = (float)windowWidth/width;
		
		if(windowHeight/height > 1 && windowWidth/width > 1){
			//不做任何操作
			Log.i(TAG,"不做操作");
		}else{
			boolean flag = value1 < value2;
			Log.i(TAG,"比值："+value1+","+value2);
			if(flag){
				height = (int)(value1 * height);
				width = (int)(value1 * width);
				Log.i(TAG,"当前长宽1："+height+"|"+width);
			}else{
				height = (int)(value2 * height);
				width = (int)(value2 * width);
				Log.i(TAG,"当前长宽2："+height+"|"+width);
			}
		}
	}

	//自定义函数 显示打开的照片在ImageView1中
	public void ShowPhotoByImageView() {
	 //   Uri imageFileUri = data.getData();
	    DisplayMetrics dm = new DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(dm);
	    String pathName = "/mnt/extsd/Download/"+checkNoteFileName;
	    int width = dm.widthPixels;    //手机屏幕水平分辨率
	    int height = dm.heightPixels;  //手机屏幕垂直分辨率
	    Log.v("height", ""+height );
	    Log.v("width", ""+width);
	    try {
	        // Load up the image's dimensions not the image itself
	        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
	        bmpFactoryOptions.inJustDecodeBounds = true;
	        Bitmap bmp = BitmapFactory.decodeFile(pathName,bmpFactoryOptions);//(getContentResolver().openInputStream(imageFileUri), null, bmpFactoryOptions);
	        int heightRatio = (int)Math.ceil(bmpFactoryOptions.outHeight/(float)height);
	        int widthRatio = (int)Math.ceil(bmpFactoryOptions.outWidth/(float)width);
	        Log.v("bmpheight", ""+bmpFactoryOptions.outHeight);
	        Log.v("bmpheight", ""+bmpFactoryOptions.outWidth);
	        if(heightRatio>1&&widthRatio>1) {
	            if(heightRatio>widthRatio) {
	                bmpFactoryOptions.inSampleSize = heightRatio*2;   //缩小倍数
	            }
	            else {
	                bmpFactoryOptions.inSampleSize = widthRatio*2;
	            }
	        }
	         //图像真正解码   
	        bmpFactoryOptions.inJustDecodeBounds = false;   
	        bmp = BitmapFactory.decodeFile(pathName,bmpFactoryOptions);
	     //   bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFileUri), null, bmpFactoryOptions);  
	        mCheckNoteImageView.setImageBitmap(bmp); //将剪裁后照片显示出来  
	     //   textview1.setVisibility(View.VISIBLE);
	    } catch(Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public void ShowPhotoByImageView2() {
	//	Matrix m = new Matrix();///
	//	m.postScale(0.5f, 0.5f);///
		
		DisplayMetrics dm = new DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(dm);
	    int width = dm.widthPixels;    //手机屏幕水平分辨率
	    int height = dm.heightPixels;  //手机屏幕垂直分辨率
	    Log.i(TAG,"屏幕长宽比："+height+"|"+width);
		String pathName = "/mnt/extsd/Download/"+checkNoteFileName;
	
	    try {
	        // Load up the image's dimensions not the image itself
	        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
	        bmpFactoryOptions.inJustDecodeBounds = true;//
	        BitmapFactory.decodeFile(pathName, bmpFactoryOptions);
	        bmpFactoryOptions.inSampleSize = calculateInSampleSize(bmpFactoryOptions, width, height);//
	     //   Log.i(TAG,"当前缩小倍数："+calculateInSampleSize(bmpFactoryOptions, width, height));
	        bmpFactoryOptions.inJustDecodeBounds = false;
	        //    getSmallBitmap(pathName);
	        Bitmap bitmap = BitmapFactory.decodeFile(pathName,bmpFactoryOptions);
	      //  Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),bitmap.getHeight(),m, true); // 
	        Log.i(TAG,"当前长宽比："+bitmap.getHeight()+"|"+bitmap.getWidth());
	        
	    //    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	     //   bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
	        mCheckNoteImageView.setImageBitmap(bitmap);   
	    } catch(Exception e) {
	        e.printStackTrace();
	    }  
	}
	
	//计算图片的缩放值
	public static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth, int reqHeight) {
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    Log.i(TAG,"当前图片长宽比："+options.outHeight+"|"+options.outWidth);
	    int inSampleSize = 1;

	    if (height > reqHeight || width > reqWidth) {
	             final int heightRatio = Math.round((float) height/ (float) reqHeight);
	             final int widthRatio = Math.round((float) width / (float) reqWidth);
	             inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }
	    
	    return inSampleSize;
	}
	
	// 根据路径获得图片并压缩，返回bitmap用于显示
	public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
	     // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, 720, 1280);
	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(filePath, options);
	}
	
	//把bitmap转换成String
	public static String bitmapToString(String filePath) {

	    Bitmap bm = getSmallBitmap(filePath);
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    bm.compress(Bitmap.CompressFormat.JPEG, 40, baos);
	    byte[] b = baos.toByteArray();
	    return Base64.encodeToString(b, Base64.DEFAULT);
	}
	
	private Handler mCheckNoteShowHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MESSAGE_SHOW_TIME_REACHED: 
				CheckNoteActivity.this.finish();
				Log.d("jiayy", "startIndex in CheckNote");
				Intent intent = new Intent(CheckNoteActivity.this,
						IndexActivity.class);
				startActivity(intent);
				break;
			case MESSAGE_CHECK_NOTE_PIC:
			/*	Bitmap bitmap = (Bitmap) msg.obj;
				BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
				mCheckNoteImageView.setBackgroundDrawable(bitmapDrawable);  */
				int showTime = Integer.parseInt(mCheckNoteShowTime) * 60 * 1000;
				sendEmptyMessageDelayed(MESSAGE_SHOW_TIME_REACHED, showTime); 
				break;
			}
		};
	};

	public class StatusReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String actionStr = intent.getAction();
			Log.d("Test","actionStr = "+actionStr);
			 if("com.example.iimp_znxj_new2014.stopPlay".equals(actionStr)) {
			//	mScrollText.setVisibility(View.INVISIBLE);
			//	 new Thread(new Client("stopPlay:true")).start();
				 CheckNoteActivity.this.finish();
				 Log.d("jiayy", "startIndex in CheckNote");
				 Intent intent2 = new Intent(CheckNoteActivity.this,IndexActivity.class);
				 startActivity(intent2);
				 Log.i("TS","接收到关闭协查通知的命令");
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mCheckNoteShowHandler.hasMessages(MESSAGE_CHECK_NOTE_PIC)) {
			mCheckNoteShowHandler.removeMessages(MESSAGE_CHECK_NOTE_PIC);
		}
		if (mCheckNoteShowHandler.hasMessages(MESSAGE_SHOW_TIME_REACHED)) {
			mCheckNoteShowHandler.removeMessages(MESSAGE_SHOW_TIME_REACHED);
		}
		mCheckNoteShowHandler.removeCallbacks(mPicThread);
		unregisterReceiver(mStatusReceiver);
	}
	
	//文件名解码
	public static String decode(String value){
		try {
			return URLDecoder.decode(value, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Log.i("Error","Error:"+e.getMessage());
			e.printStackTrace();
		}
		return value;
	}
	
	//判断该文件是否存在
	public boolean fileIsExists(String path){
		try{
			File f = new File(path);
			if(!f.exists()){
				return false;
			}
		}catch(Exception e){
			return false;
		}
		return true;
	}
}
