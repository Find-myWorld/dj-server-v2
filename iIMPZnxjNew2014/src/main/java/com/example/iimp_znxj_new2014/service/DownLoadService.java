package com.example.iimp_znxj_new2014.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.example.iimp_znxj_new2014.util.Constant;
import com.example.iimp_znxj_new2014.util.DownPlanDao;
import com.example.iimp_znxj_new2014.util.JingMoOrder;
import com.example.iimp_znxj_new2014.util.PreferenceUtil;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;

/*
 * 定时下载
 * <p>因为程序没有使用AlarmManagerUtil开启定时任务</p>
 */
public class DownLoadService extends Service {

	private DownPlanDao dao;
	private static final String TAG = "DownLoadService";
	private static final String DOWN_TABLE = "alarm_t";
	@Override
	public void onCreate() {   //第一次启动Service会执行OnCreate
		// TODO Auto-generated method stub
		super.onCreate();
		Log.i(TAG,"onCreate");
		dao = new DownPlanDao(getApplicationContext());
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {  //每次启动都会执行OnStartCommand
		Log.i(TAG,""+getCurrentTime());
		Cursor cs = dao.idQuery(getCurrentTime(),DOWN_TABLE);
		String url = null;
		String filename = null;
		while(cs.moveToNext()){
			url = cs.getString(0);
			filename = cs.getString(1);
		}
		cs.close();
		Log.i(TAG,"url+filename="+url+","+filename);
		PreferenceUtil.putSharePreference(DownLoadService.this,
				Constant.DOWNLOAD_FLAG, "false");
		JingMoOrder.fileDownload(getApplicationContext(), url, filename);
		
		
		dao.delete(getCurrentTime(),DOWN_TABLE);  //删除已经启动的定时数据
		return super.onStartCommand(intent, flags, startId);
	}

	/*
	 * 获取当前时间
	 * @return tValue int
	 */
	public int getCurrentTime(){
		Calendar  current = Calendar.getInstance();
		//SimpleDateFormat sdf = new SimpleDateFormat(1+"MMddHHmm");
		SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmmss");
		String timeStr = sdf.format(current.getTime());
		Log.i(TAG,"Service中当前时间:"+timeStr);
		
	//	SimpleDateFormat sdfId = new SimpleDateFormat(1+"MMddHHmm");
		return Integer.parseInt(timeStr);
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		Log.i(TAG,"onBind");
		return null;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.i(TAG,"onDestroy");
		super.onDestroy();
	}

}
