package com.example.iimp_znxj_new2014.util;

import android.annotation.SuppressLint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author SONGZHihang
 */
public class DateUtils {
	private static SimpleDateFormat sf = null;

	/**
	 * 获取当前年月日yyyy/MM/dd " 
	 */
	public static String getCurrentDate() {
		Date d = new Date();
		sf = new SimpleDateFormat("yyyy/MM/dd");
		return sf.format(d);
	}

	/**
	 *  时间戳转换字符串，格式为yyyy/MM/dd
	 *   */
	@SuppressLint("SimpleDateFormat")
	public static String getDateToString(long time) {
		Date d = new Date(time);
		sf = new SimpleDateFormat("yyyy/MM/dd");
		return sf.format(d);
	}

	/**
	 * 时间字符串转换时间戳
	 * */
	@SuppressLint("SimpleDateFormat")
	public static long getStringToDate(String time) {
		sf = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();
		try {
			date = sf.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date.getTime()/1000;
	}
	
	/**
	 * 时间戳转换时间字符串
	 * @param time
	 * @return
	 */	
	@SuppressLint("SimpleDateFormat")
	public static String getDatetimeToString(long time) {
		Date d = new Date(time);
		sf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
		return sf.format(d);
	}
	
	/**
	 * 字符串转换时间戳
	 * @param time
	 * @return
	 */	
	@SuppressLint("SimpleDateFormat")
	public static long getStringToDatetime(String time) {
		sf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
		Date date = new Date();
		try {
			date = sf.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date.getTime()/1000;
	}

}