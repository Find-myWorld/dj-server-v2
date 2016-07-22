package com.example.iimp_znxj_new2014.util;
/**
 *@author 作者 E-mail
 *@version 创建时间:2015-5-5上午10:50:42
 */
import java.io.IOException;
import java.io.OutputStream;

import android.util.Log;
public class SuUtil {

	private static final String TAG = "SuUtil";
	private static Process process;

	/**
	 * 结束进程,执行操作调用即可
	 */
	public static void kill(String packageName) {
		Log.i(TAG,"SuUtil...kill");
		initProcess();
		killProcess(packageName);
		close();
	}

	/**
	 * 初始化进程
	 */
	private static void initProcess() {
		Log.i(TAG,"SuUtil...initProcess");
		if (process == null)
			try {
				process = Runtime.getRuntime().exec("su");
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	/**
	 * 结束进程
	 */
	private static void killProcess(String packageName) {
		Log.i(TAG,"SuUtil...killProcess");
		OutputStream out = process.getOutputStream();
		String cmd = "am force-stop " + packageName + " \n";
		try {
			out.write(cmd.getBytes());
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭输出流
	 */
	private static void close() {
		Log.i(TAG,"SuUtil...close");
		if (process != null)
			try {
				process.getOutputStream().close();
				process = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
}

