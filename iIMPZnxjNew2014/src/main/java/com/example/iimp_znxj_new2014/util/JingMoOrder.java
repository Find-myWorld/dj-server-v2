package com.example.iimp_znxj_new2014.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EncodingUtils;

import com.example.iimp_znxj_new2014.DianJiaoApplication;
import com.example.iimp_znxj_new2014.entity.Room;
import com.example.iimp_znxj_new2014.entity.RoomAnalyze;
import com.example.iimp_znxj_new2014.receiver.BootCompletedReceiver;
import com.example.iimp_znxj_new2014.service.UdpService;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

@SuppressLint("SimpleDateFormat")
public class JingMoOrder {

	// 设置新的IP
	public static boolean modifyClientIp(String ip, String netmask) {
		String cmd = "/system/bin/ifconfig eth0 " + ip + " netmask " + netmask
				+ " up";
		new Thread(new Client("modifyClientIp:true","normal")).start();
		return upgradeRootPermission(cmd);
	}

	private static String updataPath = Environment
			.getExternalStorageDirectory() + "/";
	// private static String downloadPath = "/mnt/extsd/Download/";
	private static String downloadPath = updataPath + "/Download/";
	private static String appName = "NewApp.apk";
	private static String fname = "file";
	private static int size = 0;
	public static UdpService uservice = new UdpService();
	private static int compare = 0;
	private static boolean flag = true;

	/*
	 * //没有使用到 private static final int MESSAGE_DOWNLOAD_FINISHED = 1; private
	 * static Handler mHandler = new Handler() { public void
	 * handleMessage(android.os.Message msg) { switch(msg.what) { case
	 * MESSAGE_DOWNLOAD_FINISHED: autoInstallApp(); // runRootCommand(); break;
	 * } }; };
	 */

	/*
	 * 获取当前活动的Actiivty（如：VideoShowActivity）
	 */
	public static boolean isVideoShowActivityTop(final Context context,
			String activityName) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = am.getRunningTasks(1);
		if (!tasks.isEmpty()) {
			ComponentName topActivity = tasks.get(0).topActivity;
			if (topActivity.getClassName().equals(activityName)) {// �˴��İ������Ϊactivity���ڵ�Ŀ¼
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	/*
	 * 校时线程
	 */
	public static final int LOOPTIME = 10; // 循环次数

	static class AdjustThread extends Thread {
		String url;
		Context context;

		public AdjustThread(Context mContext, String serverUrl) {
			url = serverUrl;
			context = mContext;
		}

		int count = 0;

		public void run() {
			super.run();
			/*
			 * 如果设备不在线自动重启 如果已经发过校时时间 如果没有得到正确的时间 判断三次
			 */

			int x = 0;
			int failureTimes = 0;
			while (x < LOOPTIME) {
				x++;

				String ss = downloadXML(context, url);
				Log.e("adujustTime", ss);
				if (ss == null || ss == "") {
					failureTimes++;
					Log.e("adjustTime", "-----failureTimes=" + failureTimes
							+ "");
				} else {
					Log.e("adjustTime", "-----failureTimes=" + failureTimes
							+ "");
				}

				if (x == LOOPTIME) {
					if (failureTimes != LOOPTIME) {
						PreferenceUtil.putIntegerSharePreference(context,
								"rebootTime", 10);
						doAdjust(context, ss); //
						failureTimes = 0;
					} else {
						String flag = PreferenceUtil.getSharePreference(
								context, "haveAdjusted");
						Log.v("reboot", "-------flag=" + flag);
						if ("true".equalsIgnoreCase(flag)) {
							count = PreferenceUtil.getIntegerSharePreference(
									context, "rebootTime");
							PreferenceUtil.putIntegerSharePreference(context,
									"rebootTime", count + 1);//三次以后count为13
							Log.v("reboot", "-------count=" + count);
							if (count < 13) {//重启3次后不再重启
								Log.v("reboot", "-------开始重启");
								JingMoOrder.reboot();
							} else {
								PreferenceUtil.putIntegerSharePreference(
										context, "rebootTime", 10);
							}
						}
					}
				}

				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {

					e.printStackTrace();
				}

			}

		}
	}

	public static Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.arg1) {
			case 1:
				int number = (Integer) msg.obj;
				int percent = (int) ((number) / (float) size * 100);

				if (flag && percent == 0) {
					flag = false;
					new Thread(new Client("download:" + fname + ",0%","normal")).start();
				}
				if (percent % 10 == 0 && compare != percent) {
					compare = percent;
					new Thread(new Client("download:" + decode(fname) + ","
							+ percent + "%","normal")).start();
				}
				if (percent == 100) {
					new Thread(new Client("download:true","normal")).start();
				}
				break;
			default:
				break;
			}
		}
	};

	/*
	 * 返回下载进度
	 */
	private static void reProgress(int num) {
		// int number = (Integer)msg.obj;
		int percent = (int) ((num) / (float) size * 100);

		if (flag && percent == 0) {
			flag = false;
			new Thread(new Client("download:" + fname + ",0%","normal")).start();
		}
		if (percent % 10 == 0 && compare != percent) {
			compare = percent;
			new Thread(new Client("download:" + decode(fname) + "," + percent
					+ "%","normal")).start();
		}
		if (percent == 100) {
			new Thread(new Client("download:true","normal")).start();
		}
	}

	private static boolean doAdjust(Context context, String time) {
		Process process = null;
		DataOutputStream os = null;
		String[] tempt = null;
		tempt = time.split("-");
		String tstr = tempt[0] + "-" + tempt[1] + "-" + tempt[2] + " "
				+ tempt[3] + ":" + tempt[4] + ":" + tempt[5];
		try {
			SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			long l = s.parse(tstr).getTime();
			String cmd = "date " + (l / 1000);
			process = Runtime.getRuntime().exec("su"); // �л���root�ʺ�
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(cmd + "\n");
			os.writeBytes("exit\n");
			os.flush();

			// 判断标志，只有发送定时命令后才回复状态。开机、新安装时不发送状态
			String flag = PreferenceUtil.getSharePreference(context,
					"haveAdjusted");
			if (flag.equals("true")) {
				new Thread(new Client("adjustDate:true","normal")).start();
			}
			process.waitFor();

			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			intent.setAction("com.example.iimp_znxj_new2014.changedate");
			intent.putExtras(bundle);
			context.sendBroadcast(intent);

		} catch (Exception e) {
			new Thread(new Client("adjustDate:false","normal")).start();
			Intent udpIntent = new Intent(context, UdpService.class);
			udpIntent.setAction(Constant.ORDER_RESULT_ACTION);
			udpIntent.putExtra(Constant.ORDER_RESULT_EXTRA,
					Constant.ORDER_ACTION + "play,"
							+ Constant.ORDER_RESULT_FAILER);
			return false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				if (process != null) {
					process.destroy();
				}
			} catch (Exception e) {
				Intent udpIntent = new Intent(context, UdpService.class);
				udpIntent.setAction(Constant.ORDER_RESULT_ACTION);
				udpIntent.putExtra(Constant.ORDER_RESULT_EXTRA,
						Constant.ORDER_ACTION + "play,"
								+ Constant.ORDER_RESULT_FAILER);
				return false;
			}
		}
		return true;
	}

	@SuppressLint("SimpleDateFormat")
	public static void adjustServerTime(Context context, String serverUrlStr) {
		new AdjustThread(context, serverUrlStr).start();

	}

	@SuppressWarnings("deprecation")
	private void GetandSaveCurrentImage(Activity currentActivity) // ��Ļ��ͼ
	{
		WindowManager windowManager = currentActivity.getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		int w = display.getWidth();
		int h = display.getHeight();

		Bitmap Bmp = Bitmap.createBitmap(w, h, Config.ARGB_8888);

		View decorview = currentActivity.getWindow().getDecorView();
		decorview.setDrawingCacheEnabled(true);
		Bmp = decorview.getDrawingCache();

		try {
			File sdCard = Environment.getExternalStorageDirectory();
			File directory = new File(sdCard.getAbsolutePath() + "/ScreenPath");
			if (!directory.exists()) {
				directory.mkdirs();
			}
			if (!directory.exists()) {
				directory.createNewFile();
			}

			FileOutputStream fos = null;
			fos = new FileOutputStream(sdCard);
			if (null != fos) {
				Bmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
				fos.flush();
				fos.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * linux命令行操作
	 */
	private static boolean upgradeRootPermission(String cmd) {
		Process process = null;
		DataOutputStream os = null;
		String[] tempt = null;
		try {
			process = Runtime.getRuntime().exec("su"); // �л���root�ʺ�
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(cmd + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				if (process != null) {
					process.destroy();
				}
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	/*
	 * 从服务器获得xml格式的时间
	 */
	public static String downloadXML(Context context, String urlStr) {
		// String urlStr = "http://192.168.1.253/Androidservice/GetCurTime.aspx";
		Log.i("Msg", "downloadXML");
		StringBuffer sb = new StringBuffer();
		String line = null;
		BufferedReader buffer = null;
		try {
			URL url = new URL(urlStr);
			HttpURLConnection urlConn = (HttpURLConnection) url
					.openConnection();
			InputStream inputStream = urlConn.getInputStream();
			buffer = new BufferedReader(new InputStreamReader(inputStream));
			while ((line = buffer.readLine()) != null) {
				sb.append(line);
			}
//	
		} catch (Exception e) {
			e.printStackTrace();
			Intent udpIntent = new Intent(context, UdpService.class);
			udpIntent.setAction(Constant.ORDER_RESULT_ACTION);
			udpIntent.putExtra(Constant.ORDER_RESULT_EXTRA,
					Constant.ORDER_ACTION + "play,"
							+ Constant.ORDER_RESULT_FAILER);
		} finally {
			try {
				buffer.close();
			} catch (Exception e2) {
				e2.printStackTrace();
				Intent udpIntent = new Intent(context, UdpService.class);
				udpIntent.setAction(Constant.ORDER_RESULT_ACTION);
				udpIntent.putExtra(Constant.ORDER_RESULT_EXTRA,
						Constant.ORDER_ACTION + "play,"
								+ Constant.ORDER_RESULT_FAILER);
			}
		}
		return sb.toString();
	}


	/*
	 * 重启
	 */
	public static void reboot() {
		new Thread(new Client("reboot:true","normal")).start();
		upgradeRootPermission("/system/bin/reboot");
	}

	/*
	 * 静默升级 <p>下载部分</p>
	 */
	public static void jingMoInstall(final Context context, String appUrl,
			final String appfName) {
		final String url = appUrl;
		// appName = appfName;
		Log.i("UpdateVersion", "url=" + url);
		new Thread() {
			@SuppressWarnings("deprecation")
			public void run() {
				File file = new File(updataPath);
				// Log.d("jiayy", "context = " + context + "downloadPath = "
				// + downloadPath + " url = " + url);
				if (!file.exists())
					file.mkdir();
				HttpGet httpGet = new HttpGet(url);
				HttpResponse httpResponse;
				try {
					httpResponse = new DefaultHttpClient().execute(httpGet);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						InputStream is = httpResponse.getEntity().getContent();
						FileOutputStream fos = new FileOutputStream( // apk存储在本地
								updataPath + appName);
						Log.i("UpdateVersion", "jingmoInstall_1.5:"
								+ updataPath + appName);
						byte[] buffer = new byte[8192];
						int count = 0;
						while ((count = is.read(buffer)) != -1) {
							fos.write(buffer, 0, count);
						}
						new Thread(new Client("update:download...","normal")).start();
						fos.close();
						is.close();
						Log.i("UpdateVersion", "jingMoInstall_2");
						autoInstallApp();
					}
				} catch (ClientProtocolException e) {
					Log.e("dd", "dd");
					e.printStackTrace();
				} catch (IOException e) {
					Log.e("ee", "ee");
					e.printStackTrace();
				} finally {

				}

			};
		}.start();
	}

	/*
	 * 获取下载文件大小
	 */
	public static void getFileSize(final String url) {
		new Thread() {
			public void run() {
				try {
					URL u = new URL(url);
					HttpURLConnection urlcon = (HttpURLConnection) u
							.openConnection();
					int fileLength = urlcon.getContentLength();
					if (fileLength != 0) {
						size = fileLength;
						Log.i("TS", "size=" + size);
					}
					if (size <= 1163) {
						new Thread(new Client("download:false,no such file","normal"))
								.start();
					}
				} catch (IOException e) {
					Log.i("Error", "大小为：" + size + "|" + e.getMessage());
					e.printStackTrace();
				}
			}
		}.start();
	}

	/*
	 * 文件下载
	 */
	@SuppressLint("InlinedApi")
	public static void fileDownload(final Context context, String furl,
			String filename) {
		fname = filename;
		final String url = furl;// "http://" + serverIp + ":" + serverPort + "/"
								// + fname;
		getFileSize(url);
		Log.i("Send", "4." + url + "|" + fname + "|" + filename + "|"
				+ encode(filename));

		StorageManager sm = (StorageManager) context
				.getSystemService(Context.STORAGE_SERVICE);
		new Thread() {
			@SuppressWarnings("deprecation")
			public void run() {
				File file = new File(downloadPath); // /
				if (!file.exists())
					file.mkdir();
				HttpGet httpGet = new HttpGet(url);
				HttpResponse httpResponse;
				try {
					httpResponse = new DefaultHttpClient().execute(httpGet);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						InputStream is = httpResponse.getEntity().getContent();
						FileOutputStream fos = new FileOutputStream(
								downloadPath + decode(fname));
						byte[] buffer = new byte[8192];
						int count = 0;
						int currentLength = 0;
						String downFlag = PreferenceUtil.getSharePreference(
								DianJiaoApplication.getContextObject(),
								Constant.DOWNLOAD_FLAG);
						while ((count = is.read(buffer)) != -1) {
							fos.write(buffer, 0, count);
							currentLength += count;
							if (downFlag.equals("true")) {
								reProgress(currentLength); // 返回进度
							}
						}
						fos.close();
						is.close();
						// new Thread(new Client("download:true")).start();
					}
				} catch (Exception e) {
					Log.i("Erro", "error=" + e.getMessage());
					new Thread(new Client("download:false","normal")).start();
					e.printStackTrace();
				} finally {

				}
			};
		}.start();

	}

	/*
	 * 静默升级 <p>安装部分</p>
	 */
	private static void autoInstallApp() { // (final Context mContext)
		Log.i("UpdateVersion", "autoInstallApp_3:" + appName);
		new Thread() {
			public void run() {
				Process process = null;
				OutputStream out = null;
				InputStream in = null;
				try {
					// Context context = mContext;
					process = Runtime.getRuntime().exec("su");
					out = process.getOutputStream();
					// 以前安装不成功，主要没加：adb shell
					out.write(("adb shell " + "\n" + " pm install -r "
							+ updataPath + appName + "\n").getBytes());
					in = process.getInputStream();
					int len = 0;
					byte[] bs = new byte[256];
					new Thread(new Client("update:install...","normal")).start();
					while (-1 != (len = in.read(bs))) {
						String state = new String(bs, 0, len);
						Log.i("Msg", "....state");
						if (state.equals("Success\n")) {
							Log.i("UpdateVersion",
									"install finish"
											+ "BootCompletedReceiver.isServiceWorked() = "
											+ BootCompletedReceiver
													.isServiceWorked());
						}
					}
					Log.i("UpdateVersion", "autoInstallApp_4:" + appName);
					// new Thread(new Client("update:wait...")).start();
				} catch (IOException e) {
					new Thread(new Client("update:false","error")).start();
					e.printStackTrace();
				} catch (Exception e) {
					new Thread(new Client("update:false","error")).start();
					e.printStackTrace();
				} finally {
					try {
						if (out != null) {
							out.flush();
							out.close();
						}
						if (in != null) {
							in.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	/**
	 * 请求ROOT权限后执行命令（最好开启一个线程）
	 * 
	 * @param cmd
	 *            (pm install -r *.apk)
	 * @return
	 */
	public static void runRootCommand() {
		Process process = null;
		DataOutputStream os = null;
		BufferedReader br = null;
		StringBuilder sb = null;
		try {
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes("pm install -r " + updataPath + appName + " \n");
			Log.i("Msg", "1...:" + updataPath + appName);
			os.writeBytes("exit\n");
			br = new BufferedReader(new InputStreamReader(
					process.getInputStream()));

			sb = new StringBuilder();
			String temp = null;
			while ((temp = br.readLine()) != null) {
				sb.append(temp + "\n");
				Log.i("Msg", "sb=" + sb);
				if ("Success".equalsIgnoreCase(temp)) {
					Log.i("Msg", "2...");
				}
			}
			process.waitFor();
		} catch (Exception e) {
			Log.i("Msg", "Error...2:" + e.getMessage());
			// LogUtils.logE("异常："+e.getMessage());
		}

		finally {
			try {
				if (os != null) {
					os.flush();
					os.close();
				}
				if (br != null) {
					br.close();
				}
				process.destroy();
			} catch (Exception e) {
				Log.i("Msg", "Error" + e.getMessage());
			}
		}
	}

	/*
	 * 查找当前文件夹下目录
	 */
	public static void search(File fileold) {
		String filestr;
		filestr = "";
		Log.i("TIME", "search1");
		try {
			File[] files = fileold.listFiles();
			Log.i("TIME", "search2");
			if (files.length > 0) {
				for (int j = 0; j < files.length; j++) {
					if (!files[j].isDirectory()) {
						filestr = filestr + files[j].getName() + ",";
					} else {

					}
				}
				new Thread(new Client("getfilelist:" + filestr,"normal")).start();
			} else {
				new Thread(new Client("getfilelist:Empty folder!","error")).start();
			}
		} catch (Exception e) {
			new Thread(new Client("getfilelist:No such folder!","error")).start();
		}
	}

	/*
	 * 删除指定文件
	 */
	public static void deleteFile(String filename) {
		File f = new File("/mnt/extsd/Download/" + filename);
		if (f.exists()) {
			if (f.isFile()) {
				f.delete(); //
				new Thread(new Client("delfile:true","normal")).start();
			}
		} else {
			new Thread(new Client("delfile:false","error")).start();
		}
	}

	// 上传错误日志（上传地址为：交互终端后台）
	public static void postErrorLog3(Context context, String msg, String type) {
		String serverIpAndPort = PreferenceUtil.getSharePreference(context,Constant.CONFIGURE_INFO_SERVERIP)+ ":"
				+ PreferenceUtil.getSharePreference(context,Constant.CONFIGURE_INFO_PORT);
		String url = "http://" + serverIpAndPort + "/"+Constant.SERVER_PART+"/LogRecord.aspx?deviceIp="
				+ getIp() + "&logMessage=" + msg + "&logType=" + type;
		Log.i("TS", "url=" + url);
		// 发送请求到服务器
		AsyncHttpClient client = new AsyncHttpClient();
		// 创建请求参数
		RequestParams params = new RequestParams();
		client.post(url, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				
				Log.i("TS", "success");
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				
				Log.i("TS", "error" + arg3.getMessage());
			}
		});
	}

	// 上传错误日志
	/*public static void postErrorLog(String serverIpAndPort, String localIp,
			String msg) {
		String url = "http://" + serverIpAndPort + "/LogAsp.aspx?deviceIp='"
				+ localIp + "'&msg='" + msg + "'";
		Log.i("TS", "url=" + url);
		// 发送请求到服务器
		AsyncHttpClient client = new AsyncHttpClient();
		// 创建请求参数
		RequestParams params = new RequestParams();
		client.post(url, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				
				Log.i("TS", "success");
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				
				Log.i("TS", "error" + arg3.getMessage());
			}
		});
	}*/

	//获取监室号和监室名称
    public static void getRoomIDAndName(final Context context) {
    	String requestUrl = Constant.SERVER_URL + Constant.AUTO_GET_ROOMID;
    	AsyncHttpClient client = new AsyncHttpClient();
    	Log.i("TTSS","请求地址："+requestUrl);
    	client.get(requestUrl, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] responseBody) {
				
				try {
					String getXmlStr = new String(responseBody, "UTF-8");
					InputStream is = new ByteArrayInputStream(getXmlStr.getBytes());// 将xml转为InputStream类型
					Log.i("TTSS","getXmlStr=\n"+getXmlStr+"\nInputSream=>"+is);
					showListView(is,context);
				} catch (Exception e) {
					Log.i("TTSS","error=>"+e.getMessage());
					e.printStackTrace();
				} // 获取xml
			}
			
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				
				Log.i("TTSS","error1=>"+arg3.getMessage());
			}
		});
	} 

    /*
	 * 功能：解析xml
	 */
	public static void showListView(InputStream is,Context context){
		Room info = null;
		try {
			if (is != null) {
				info = RoomAnalyze.parse(is);
				Log.i("TTSS","info=>"+info);
				is.close();
			}
			String monitorNumStr = info.getRoomID();
			String cellNum = info.getRoomName();
			String durationStr=info.getRollCallTime();
			Log.i("TTSS","showListView1,cellNum="+cellNum);
//			String content = "serverUrl="+Constant.SERVER_URL+"\n"+"monitoringNumber="+monitorNumStr+"\n"+"cellNumber="+cellNum;
			
//			serverIp = path.split(":")[0];
//			port = path.split(":")[1];

//			cellNumber = editRoom.getText().toString();
//			duration = rollcalltime.getText().toString();

//			PreferenceUtil.putSharePreference(SettingActivity.this, Constant.CONFIGURE_INFO_SERVERIP, serverIp);
//			PreferenceUtil.putSharePreference(SettingActivity.this, Constant.CONFIGURE_INFO_PORT, port);
			PreferenceUtil.putSharePreference(context, Constant.CONFIGURE_INFO_CELLNUMBER, monitorNumStr);
			PreferenceUtil.putSharePreference(context, Constant.CONFIGURE_INFO_DURATION, durationStr);
			
			
			
		} catch (Exception e) {
			Log.e("TTSS","Error2=>"+e.getMessage());
			e.printStackTrace();
		} 
	}
	
	/*
	 * 删除指定文件
	 */
	public static void deleteFilePath(String path) {
		File f = new File(path);
		if (f.exists()) {
			if (f.isFile()) {
				f.delete(); //
			}
		} else {
		}
	}

	/*
	 * 回复消息至服务器端
	 */
	@SuppressLint("NewApi")
	public static class Client implements Runnable {
		String result = "";
		DatagramSocket sendSocket;
		String serverIp = "";
		String serverPort = "";
		String type="normal";

		public Client(String resultForServer,String type) {
			result = resultForServer;
			this.type=type;
			Log.i("TS", "result=" + result);
		}
		public Client(String resultForServer) {
			result = resultForServer;
			Log.i("TS", "result=" + result);
		}
		
		@Override
		public void run() {
			try {
				
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				
				e1.printStackTrace();
			}	
//			ScreenShot.postErrorLog(uservice, result, type);//上传日志到新综合管理平台的日志文件中
			try {
				if (TextUtils.isEmpty(serverIp)) {
					serverIp = PreferenceUtil.getSharePreference(
							DianJiaoApplication.getContextObject(),
							Constant.SERVER_IP);
				}
				InetAddress serverAddr = InetAddress.getByName(serverIp);
				sendSocket = new DatagramSocket();
				byte[] buf = null;
				int serverPortInt = 0;
				if (!TextUtils.isEmpty(result)) {
					buf = result.getBytes();
					if (TextUtils.isEmpty(serverPort)) {
						serverPort = PreferenceUtil.getSharePreference(
								DianJiaoApplication.getContextObject(),
								Constant.SERVER_PORT);
					}
					if (!TextUtils.isEmpty(serverPort)) {
						serverPortInt = Integer.valueOf(serverPort);
						DatagramPacket packet = new DatagramPacket(buf,
								buf.length, serverAddr, serverPortInt);
						sendSocket.send(packet);
						sendSocket.close();
					}
				}
			} catch (Exception e) {
				Log.i("Error", "Error:" + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	
	@SuppressLint("NewApi")
	public static class Clientdiff implements Runnable {
		String result = "";
		DatagramSocket sendSocket;
		String serverIp = "";
		String serverPort = "9000";

		public Clientdiff(String resultForServer,String port) {
			result = resultForServer;
			serverPort=port;
			Log.i("TS", "result=" + result);
		}
		

		public Clientdiff(String resultForServer) {
			result = resultForServer;
			Log.i("TS", "result=" + result);
		}

		@Override
		public void run() {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				if (TextUtils.isEmpty(serverIp)) {
					serverIp = PreferenceUtil.getSharePreference(
							DianJiaoApplication.getContextObject(),
							Constant.SERVER_IP);
				}
				InetAddress serverAddr = InetAddress.getByName(serverIp);
				int port=Integer.parseInt(serverPort);
				sendSocket = new DatagramSocket();
				byte[] buf = null;
				if (!TextUtils.isEmpty(result)) {
					buf = result.getBytes();
						
						DatagramPacket packet = new DatagramPacket(buf,
								buf.length, serverAddr, port);
						sendSocket.send(packet);
						sendSocket.close();
					
				}
			} catch (Exception e) {
				Log.i("Error", "Error:" + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	// 文件名转码
	public static String encode(String value) {
		try {
			return URLEncoder.encode(value, "GB2312");
		} catch (UnsupportedEncodingException e) {
			
			Log.i("Error", "Error:" + e.getMessage());
			e.printStackTrace();
		}
		return value;
	}

	// 文件名解码
	public static String decode(String value) {
		try {
			return URLDecoder.decode(value, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Log.i("Error", "Error:" + e.getMessage());
			e.printStackTrace();
		}
		return value;
	}

	// 判断该文件是否存在
	public static boolean fileIsExists(String path) { // 这里只能判断SD卡下的路径
		try {
			File f = new File(path);
			if (!f.exists()) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	// 将字符串写入到文本文件中
	public static void WriteTxtFile(String strcontent, String strFilePath) {
		// 每次写入时，都换行写
		String strContent = strcontent + "\n";
		try {
			File file = new File(strFilePath);
			if (!file.exists()) {
				Log.d("TestFile", "Create the file:" + strFilePath);
				file.createNewFile();
			}
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			raf.seek(file.length());
			raf.write(strContent.getBytes());
			raf.close();
		} catch (Exception e) {
			Log.e("TestFile", "Error on write File.");
		}
	}

	// 上传错误日志
	/*
	 * public static void postErrorLog2(Context context,String msg,String type)
	 * { String url =
	 * "http://"+PreferenceUtil.getSharePreference(context,Constant
	 * .CONFIGURE_INFO_SERVERIP)+":"+PreferenceUtil.getSharePreference(context,
	 * Constant
	 * .CONFIGURE_INFO_PORT)+"/"+Constant.SERVER_PART+"LogRecord.aspx";//
	 * ?ip='"+localIp+"'&msg='"+msg+"'"; SyncHttpClient client = new
	 * SyncHttpClient(); RequestParams params = new
	 * RequestParams();//PreferenceUtil
	 * .getSharePreference(context,"SERVER_IP_PORT")
	 * params.put("deviceIp",getIp()); params.put("logMessage","1234");
	 * params.put("logType",type); // normal\error
	 * 
	 * params.setContentEncoding("UTF-8");
	 * Log.i("TEST","URL地址：1."+type+"|"+url+"\n2."
	 * +PreferenceUtil.getSharePreference(context,Constant.TERMINAL_IP)
	 * +"\n3."+msg); // Log.i("TEST","getIp="+getIp()); client.post(url,
	 * params,new AsyncHttpResponseHandler() {
	 * 
	 * @Override public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
	 *  Log.i("TEST","success,"+arg2); }
	 * 
	 * @Override public void onFailure(int arg0, Header[] arg1, byte[] arg2,
	 * Throwable arg3) { 
	 * Log.i("TEST","error,"+arg3.getMessage()); } }); }
	 */

	/*
	 * 获取Android设备的IP地址(针对有线联网方式)
	 */
	public static String getIp() {
		String ipaddress = null;
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				if (intf.getName().toLowerCase().equals("eth0")
						|| intf.getName().toLowerCase().equals("wlan0")) {
					for (Enumeration<InetAddress> enumIpAddr = intf
							.getInetAddresses(); enumIpAddr.hasMoreElements();) {
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
