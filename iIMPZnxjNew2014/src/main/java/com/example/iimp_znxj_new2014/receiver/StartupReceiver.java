package com.example.iimp_znxj_new2014.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.example.iimp_znxj_new2014.activity.IndexActivity;
import com.example.iimp_znxj_new2014.service.UdpService;
import com.example.iimp_znxj_new2014.util.Constant;
import com.example.iimp_znxj_new2014.util.JingMoOrder;
import com.example.iimp_znxj_new2014.util.PreferenceUtil;
import com.example.iimp_znxj_new2014.util.JingMoOrder.Client;

/*
 * 应用程序安装、删除、替换过程中的广播消息接收
 */
public class StartupReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		
		try {
			
		
		PackageManager manager = context.getPackageManager();
	/*	if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
			String packageName = intent.getData().getSchemeSpecificPart();
			if (!BootCompletedReceiver.isServiceWorked()) {
				Intent serviceIntent = new Intent(context, UdpService.class);
				context.startService(serviceIntent);
			}
			Intent mIntent = new Intent(context, IndexActivity.class);
			mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mIntent.setAction("android.intent.action.MAIN");
			mIntent.addCategory("android.intent.category.LAUNCHER");
			context.startActivity(mIntent);
			String adjustTimeServerIp = PreferenceUtil.getSharePreference(
					context, Constant.ADJUST_SERVER_URL);
			JingMoOrder.adjustServerTime(context, adjustTimeServerIp);
		} */
		
		if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)){
		//	Log.i(TAG,""+OPEN_ACTION);
			String packageName = intent.getDataString().substring(8);
			Intent newIntent = new Intent();
			newIntent.setClassName(packageName, packageName+".IndexActivity");
			newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			newIntent.setAction("android.intent.action.MAIN");
			newIntent.addCategory("android.intent.category.LAUNCHER");
			context.startActivity(newIntent);
		}
		
		if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
			String packageName = intent.getData().getSchemeSpecificPart();
		}
		if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
			String packageName = intent.getData().getSchemeSpecificPart();
			if (!BootCompletedReceiver.isServiceWorked()) {
				new Thread(new Client("update:true")).start();
				Intent serviceIntent = new Intent(context, UdpService.class);
				context.startService(serviceIntent);
				Intent mIntent = new Intent(context, IndexActivity.class);
				mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mIntent.setAction("android.intent.action.MAIN");
				mIntent.addCategory("android.intent.category.LAUNCHER");
				context.startActivity(mIntent);
				
				/*
				 * 开机自动校时
				 */
				String adjustTimeServerIp = PreferenceUtil.getSharePreference(
						context, Constant.ADJUST_SERVER_URL);
				PreferenceUtil.putSharePreference(context,
						Constant.ADJUST_SERVER_FLAG, "false");
				JingMoOrder.adjustServerTime(context, adjustTimeServerIp);
			}
		}
		
		}catch (Exception e) {
			
		}
	}
		
		
	
}
