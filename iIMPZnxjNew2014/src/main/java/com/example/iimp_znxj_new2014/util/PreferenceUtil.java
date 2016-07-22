package com.example.iimp_znxj_new2014.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtil {
	public static void putSharePreference(Context context, String key,
			String value) {
		SharedPreferences settings = context.getSharedPreferences("serverInfo",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor localEditor = settings.edit();
		localEditor.putString(key, value);
		localEditor.commit();
	}

	public static String getSharePreference(Context context,String key) {
		SharedPreferences settings = context.getSharedPreferences("serverInfo",
				Context.MODE_PRIVATE);
		return settings.getString(key, "");
	}
	
	
	public static void putIntegerSharePreference(Context context, String key,
			int value) {
		SharedPreferences settings = context.getSharedPreferences("serverInfo",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor localEditor = settings.edit();
	//	localEditor.putString(key, value);
		localEditor.putInt(key, value);
		localEditor.commit();
	}

	//每次开机自动清零重启次数
	public static int getIntegerSharePreference(Context context,String key) {
		SharedPreferences settings = context.getSharedPreferences("serverInfo",
				Context.MODE_PRIVATE);
	//	return settings.getInt(key, 10);
		return settings.getInt(key, 10);
	}
	
	/**
	 * 
	 * @param context 
	 * @param key
	 * @param defaultValue 缺省值
	 * @return
	 */
	public static String getSharePreference(Context context, String key,String defaultValue) {
		SharedPreferences settings = context.getSharedPreferences("serverInfo", Context.MODE_PRIVATE);
		return settings.getString(key, defaultValue);
	}
}
