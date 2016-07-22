package com.example.iimp_znxj_new2014.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import com.example.iimp_znxj_new2014.DianJiaoApplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetUtil {
	/*
	 * 判断是否联网
	 */
	@SuppressLint("InlinedApi")
	public static boolean isNetworkConnected() {
		boolean wifiConnected = false;
		boolean mobileConnected = false;
		boolean lanContected = false;

		ConnectivityManager connMgr = (ConnectivityManager) DianJiaoApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();

		if (activeInfo != null && activeInfo.isConnected()) {
			wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
			mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
			lanContected = activeInfo.getType() == ConnectivityManager.TYPE_ETHERNET;
		}
		return wifiConnected || mobileConnected || lanContected;
	}
	
	
	/**
	 * 获取Android设备的IP地址(针对有线联网方式)
	 * @return
	 */
	public static String getIp() {
		String ipaddress = null;
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				if (intf.getName().toLowerCase().equals("eth0") || intf.getName().toLowerCase().equals("wlan0")) {
					for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
						InetAddress inetAddress = enumIpAddr.nextElement();
						if (!inetAddress.isLoopbackAddress()) {
							ipaddress = inetAddress.getHostAddress().toString();
							if (!ipaddress.contains("::")) {// ipV6的地址
								return ipaddress;
							}
						}
					}
				} else {
					continue;
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return ipaddress;
	}
	
	
}
