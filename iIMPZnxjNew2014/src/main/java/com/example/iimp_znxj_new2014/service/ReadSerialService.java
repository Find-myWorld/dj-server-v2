package com.example.iimp_znxj_new2014.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.Arrays;

import com.example.iimp_znxj_new2014.Config;
import com.example.iimp_znxj_new2014.util.Constant;
import com.wits.serialport.SerialPort;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;

public class ReadSerialService extends Service {

	private static final String TAG = "ReadSerialService";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		initSerialPort();
		super.onCreate();
	}

	/*
	 * 串口操作
	 */
	public static SerialPort mSerialPort;
	public static OutputStream mOutputStream;
	public static InputStream mInputStream;

	public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
		mSerialPort = new SerialPort(new File(Config.DEVICE_SERIALPORT), 9600, 0);
		return mSerialPort;
	}

	public void closeSerialPort() {
		isFlag = false;
		if (mSerialPort != null) {
			Log.i(TAG, "closeSerialPort");
			mSerialPort.close();
			mSerialPort = null;
		}
	}

	/*
	 * 串口线程的初始化
	 */
	public void initSerialPort() {
		isFlag = true;
		Log.i(TAG, "ReadThreadSerial启动");
		try {
			mSerialPort = getSerialPort();
			
			//上一步报错的话(即硬件不支持串口)，则不执行以下语句
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();
			new Thread(new ReadThreadSerial()).start(); // 读串口的线程
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 读取串口的线程
	 */
	private boolean isFlag = false;

	public class ReadThreadSerial extends Thread {
		@Override
		public void run() {
			super.run();
			while (isFlag) {
				int size;
				String ss = null;
				try {
					byte[] buffer = new byte[20];
					if (mInputStream == null) {
						break;
					}

					size = mInputStream.read(buffer);

					for (int i = 0; i < size; i++) {
						ss = ss + ":" + Integer.toString(buffer[i] & 0xff);
					}

					if (size > 10 ) {
						String cardNum = null;
						StringBuilder cardIdSb = new StringBuilder();
						int cardArray[] = new int[10];

						for (int i = 2; i <= 11; i++) {
							cardArray[i - 2] = buffer[i] & 0xff;
						}

						for (int x : cardArray) {
							cardIdSb.append(x - 48);
						}

						cardNum = cardIdSb.toString();
						serial_Card(cardNum);
						Log.i(TAG, "ReadThreadSerial,卡号为：" + cardNum );
					} else {
						int keyCode = buffer[2] & 0xff;
						serial_Key(keyCode);
						Log.i(TAG, "ReadThreadSerial,键盘值为：" + keyCode);
					}

					Arrays.fill(buffer, (byte) 0);

				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}

	public void serial_Key(int keyCode) {
		Intent intent = new Intent();
		intent.putExtra("keyCode", keyCode);
		Log.i(TAG, "ReadSerialService,keyCode:" + keyCode);
		intent.setAction(Constant.SERIAL_ACTION_KEYCODE);
		sendBroadcast(intent);
	}

	public void serial_Card(String cardNum) {
		Intent intent = new Intent();
		intent.putExtra("cardNum", cardNum);
		Log.i(TAG, "ReadSerialService,cardNum:" + cardNum);
		intent.setAction(Constant.SERIAL_ACTION_CARDNUM);
		sendBroadcast(intent);
	}

	@Override
	public void onDestroy() {
		closeSerialPort();
		super.onDestroy();
	}

}
