package com.example.iimp_znxj_new2014.util;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.Header;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
   
/*
 * 截屏
 */
public class ScreenShot {  
	public static Activity a;
    public static void takeScreenShot(Activity activity){  
    //	activity1 = activity;
        View view = activity.getWindow().getDecorView();  
        view.setDrawingCacheEnabled(true);  
        view.buildDrawingCache();  
        Bitmap b1 = view.getDrawingCache();  

        Rect frame = new Rect();    
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);    
        int statusBarHeight = frame.top;    
        System.out.println(statusBarHeight);  
           
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();    
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();    

        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight); ///
    //    Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height); //�ᱨ��
        view.destroyDrawingCache();  
        Log.i("Test","ScreenShot:"+b);
        
        savePic(b, "mnt/sdcard/current.png");
    }  
       
    private static void savePic(Bitmap b,String strFileName){  
    	Log.i("Test","savePic");
        FileOutputStream fos = null;  
        try {  
            fos = new FileOutputStream(strFileName);
            if (null != fos)  
            {  
                b.compress(Bitmap.CompressFormat.PNG, 90, fos);  
                fos.flush();  
                fos.close();
            //    Toast.makeText(activity1, "save " + strFileName.toString(), Toast.LENGTH_SHORT).show();
            }  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }
    }  
       
    public static void uploadFile(String path, String url) throws FileNotFoundException{
		File file = new File(path);
		if (file.exists() && file.length() > 0) {
			SyncHttpClient client = new SyncHttpClient();
			RequestParams params = new RequestParams();
			params.put("Filedata", file);
			Log.i("Test","uploadFile4");
			client.post(url, params, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						byte[] responseBody) {
					try {
						Log.i("Test","11"+ new String(responseBody,"UTF-8").trim());
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					Log.i("Test","�ϴ�ʧ��"+error.getMessage());
				}
				@Override
				public void onProgress(int bytesWritten, int totalSize) {
					// TODO Auto-generated method stub
					super.onProgress(bytesWritten, totalSize);
					int count = (int) ((bytesWritten * 1.0 / totalSize) * 100);
					Log.e("Test", bytesWritten + " / " + totalSize);
				}
				@Override
				public void onRetry(int retryNo) {
					super.onRetry(retryNo);
				}
			});
		} else {
		//	Log.i("Test","Error");
		}
	}
    
  //上传错误日志
    /**
     * 
     * @param context
     * @param msg
     * @param type  
     */
    public static void postErrorLog(Context context,String msg,String type) {
    	String url = "http://"+PreferenceUtil.getSharePreference(context,Constant.SERVER_IP_PORT)+"/"+Constant.SERVER_PART+"LogRecord.aspx";//?ip='"+localIp+"'&msg='"+msg+"'";
    	SyncHttpClient client = new SyncHttpClient();
		RequestParams params = new RequestParams();//PreferenceUtil.getSharePreference(context,"SERVER_IP_PORT")
		params.put("deviceIp",JingMoOrder.getIp());
		params.put("logMessage",msg);
		params.put("logType",type); //   normal\error
		params.setContentEncoding("UTF-8");
		Log.i("TEST","URL地址：1."+url+"\n2."+PreferenceUtil.getSharePreference(context,Constant.TERMINAL_IP)
				+"\n3."+msg+"\n4."+type);
		Log.i("TEST","getIp="+JingMoOrder.getIp());
		client.post(url, params,new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				Log.i("TEST","success,"+arg2);
			}
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				// TODO Auto-generated method stub
				Log.i("TEST","error"+arg3.getMessage());
			}
		});
	}
}  
