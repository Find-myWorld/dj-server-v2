package com.example.iimp_znxj_new2014.selfconsume;

import java.io.InputStream;
import java.util.Properties;

import android.content.Context;
import android.util.Log;

/*
 * ��ȡassets�ļ����µ�.properties�ļ�
 */
public class MyProperUtil {
	private static Properties urlProps;
	private static final String TAG = "MyProperUtil";
	public static Properties getProperties(Context c){
		Properties props = new Properties();
		try{
			InputStream in = c.getAssets().open("appConfig.properties");
			props.load(in);
		}catch(Exception e){
			e.printStackTrace();
		}
		urlProps = props;
		Log.i(TAG,"��ȡ�ļ��е�ֵ��"+urlProps.getProperty("serverUrl"));
		
		return urlProps;
		
	}
}
