package com.example.iimp_znxj_new2014.receiver;

import java.util.ArrayList;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.example.iimp_znxj_new2014.DianJiaoApplication;
import com.example.iimp_znxj_new2014.activity.IndexActivity;
import com.example.iimp_znxj_new2014.activity.PopActivity;
import com.example.iimp_znxj_new2014.service.UdpService;
import com.example.iimp_znxj_new2014.util.AlarmUtil;
import com.example.iimp_znxj_new2014.util.Constant;
import com.example.iimp_znxj_new2014.util.JingMoOrder;
import com.example.iimp_znxj_new2014.util.PreferenceUtil;

public class BootCompletedReceiver extends BroadcastReceiver {

	private static final String DUTY_REMINDER = "android.intent.action.DUTY_REMINDER";
	private static final String CANCEL_REMINDER = "android.intent.action.CANCEL_REMINDER";
	
	/*
	 * 接收到开机广播；1.校时   2.配置IP
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("jiayy", "action = " + intent.getAction());
		if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
			if (!isServiceWorked()) {
				Intent serviceIntent = new Intent(context, UdpService.class);
				context.startService(serviceIntent);
			}
			Log.e("jiayy","startIndex in bootcomplete");
			Intent mainIntent = new Intent(context, IndexActivity.class);
			mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(mainIntent);
			String adjustTimeServerIp = PreferenceUtil.getSharePreference(
					context, Constant.ADJUST_SERVER_URL);
			PreferenceUtil.putSharePreference(context,
					Constant.ADJUST_SERVER_FLAG, "false");
			JingMoOrder.adjustServerTime(context, adjustTimeServerIp);
			String clientIp = PreferenceUtil.getSharePreference(context,
					Constant.MODIFY_CLIENT_IP);
			String subnetMask = PreferenceUtil.getSharePreference(context,
					Constant.MODIFY_CLIENT_SUBNETMASK);
			Log.d("jiayy", "clientIp = " + clientIp + " subnetMask = "
					+ subnetMask);
			if (!TextUtils.isEmpty(clientIp) && !TextUtils.isEmpty(subnetMask)) {
				JingMoOrder.modifyClientIp(clientIp, subnetMask);
			}
		}
		if (intent.getAction().equals(DUTY_REMINDER)){
			Log.i("TTSS","接收到广播");
//			Toast.makeText(context, "15s提醒一次！！！", Toast.LENGTH_LONG).show();  
			
			Intent mIntent = new Intent(context,PopActivity.class);
//			mIntent.putExtra("content", "值班期间,保持纪律,发现问题,及时上报");
			mIntent.putExtra("content", "值班提醒");
			mIntent.putExtra("pop_value", 1);//, value)Extra("content", testStr);
			mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(mIntent);
			
		}
		
		if (intent.getAction().equals(CANCEL_REMINDER)){
			Log.i("TTSS","接收到清空广播");
			AlarmUtil.clearBroadCastAlarm(context);
		}
		
	}

	/*
	 * Android判断一个Service是否运行
	 */
	public static boolean isServiceWorked() {
		ActivityManager myManager = (ActivityManager) DianJiaoApplication
				.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
		ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager
				.getRunningServices(30);
		for (int i = 0; i < runningService.size(); i++) {
			if (runningService.get(i).service.getClassName().toString()
					.equals("com.example.iimp_znxj_new2014.service.UdpService")) {
				return true;
			}
		}
		return false;
	}
}
