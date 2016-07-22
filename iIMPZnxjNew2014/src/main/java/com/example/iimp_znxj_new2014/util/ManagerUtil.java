package com.example.iimp_znxj_new2014.util;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.util.Log;

public class ManagerUtil {
	private static final String TAG = "ManagerUtil";

	/**
	 * ActivityManager
	 * @param context 
	 * @param activityName Activity的类名
	 * @return
	 */
	public static boolean isThisActivityTop(Context context, String activityName) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

		// 得到当前活动的task内的所有集合
		List<RunningTaskInfo> tasks = am.getRunningTasks(1);

		if (!tasks.isEmpty()) {
			ComponentName topActivity = tasks.get(0).topActivity;
			Log.d("jiayy11", "topActivity.getClassName() = " + topActivity.getClassName() + "  topActivity.getClassName().equals(activityName)  = " + topActivity.getClassName().equals(activityName)
					+ " activityName = " + activityName);
			if (topActivity.getClassName().equals(activityName)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * AudioManager 
	 * @param context
	 * @param val 音量大小
	 */
	public static void SoundCtrl(Context context, int val) {
		final AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		// 最大音量
		final int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		if (val > maxVolume) {
			val = maxVolume;
		} else if (val < 0) {
			val = 0;
		}
		PreferenceUtil.putIntegerSharePreference(context, "VOLUME_NUM_VALUE", val);
		// 当前音量
		final int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, val, 0); // tempVolume:音量绝对值
		Log.i(TAG, "UdpSErvice_调整后的音量：" + val);
	}

	/**
	 * PackageManager
	 * @param context
	 * @return
	 */
	public static String getVersionName(Context context) {
		String version = null;
		try {
			PackageManager packageManager = context.getPackageManager();
			// getPackageName()是你当前类的包名，0代表是获取版本信息
			PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			version = packInfo.versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 获取packagemanager的实例
		return version;
	}

	/**
	 * ActivityManager 通过获得顶端的Activity来获得App的包名来判断当前运行的App
	 * @param context
	 * @return
	 */
	public static boolean isApplicationBroughtToBackground(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = am.getRunningTasks(1);
		if (!tasks.isEmpty()) {
			ComponentName topActivity = tasks.get(0).topActivity;
			String activityPackageName = topActivity.getPackageName();
			Log.d("jiayy00", "activityPackageName = " + activityPackageName // 每30秒执行一次
					+ " context.getPackageName() = " + context.getPackageName());
			if (activityPackageName.equals(context.getPackageName())) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
	
	/**
	 * 查看该Service是否正在运行
	 * @param mContext
	 * @param className
	 * @return
	 */
	public static boolean isServiceRunning(Context mContext, String className) {

		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(30);

		if (!(serviceList.size() > 0)) {
			return false;
		}

		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}
}
