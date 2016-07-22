package com.example.iimp_znxj_new2014.dao;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.wits.serialport.SerialPort;

/*
 * @title 指纹操作方法
 * @description 
 * (1)指纹比对  0xA1->0x0F->0x0D->0x50
 * (2)指纹录入  0xA1->0x07->0x28
 */
public class FingerPrintUtil{

	public static SerialPort mSerialPort;
	public static OutputStream mOutputStream;
	public static InputStream mInputStream;
	public static ReadThreadSerial mReadThread2;
	private static String TAG = "FingerPrintUtil";
	
	private Context mContext;
	private static FingerPrintUtil instance;
	private ParseListener mParseListener;
	public String results;

/*	private TextView txtMsg = null;
//	private TextView txtMsg2 = null;
	
	private EditText etxtTimes = null;
	private EditText etxtQuery = null;*/
	public boolean tFlag = true;
	public boolean isTrue = true;
	private Timer timer;
	private int GetAllNum=0;
	private Long onTimes;
	
	private FpDao fDao;    //指纹相关操作
	private int count = 0; //比对次数
	
	public FingerPrintUtil(Context context) {
		try {
			mContext = context;
			fDao = new FpDao(context);
			
			Log.v(TAG, "FingerPrintUtil");
			
			mSerialPort = getSerialPort();
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();
			
			
			mReadThread2 = new ReadThreadSerial();   //开启串口线程
			mReadThread2.start();
		/*	myThread2 = new clientRead();
			myThread2.start();*/
			Log.i(TAG, "-----------------mReadThread.start");
		} catch (SecurityException e) {
			Log.e(TAG, "-----------------SecurityException");
		} catch (IOException e) {
			Log.e(TAG, "-----------------IOException");
		} catch (InvalidParameterException e) {
			Log.e(TAG, "-----------------InvalidParameterException");
		}
	}
	
	//设备握手(0xa1)
	public void resetModule() {
		// TODO Auto-generated method stub
		try {
			byte[] result = new byte[16];
			result[0] = (byte) 0xaa;
			result[1] = (byte) 0x01;
			result[2] = (byte) 0x01;
			result[3] = (byte) 0xa1;  //第4位，有效指令
			result[4] = (byte) 0;
			result[5] = (byte) 0;
			result[6] = (byte) 0;
			result[7] = (byte) 0;
			result[8] = (byte) 0;
			result[9] = (byte) 0;
			result[10] = (byte) 0;
			result[11] = (byte) 0;
			result[12] = (byte) 0;
			result[13] = (byte) 0;
			result[14] = (byte) 0x4d;
			result[15] = (byte) 0x01;
			
			if (mOutputStream != null) {
				mOutputStream.write(result);
			} else {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//自动感应(0x0f)
	public void setTouchSensing() {
		try {
			byte[] result = new byte[16];
			result[0] = (byte) 0xaa;
			result[1] = (byte) 0x01;
			result[2] = (byte) 0x01;
			result[3] = (byte) 0x0f;   //第4位，有效指令
			result[4] = (byte) 1;
			result[5] = (byte) 0;
			result[6] = (byte) 0;
			result[7] = (byte) 0;
			result[8] = (byte) 0;
			result[9] = (byte) 0;
			result[10] = (byte) 0;
			result[11] = (byte) 0;
			result[12] = (byte) 0;
			result[13] = (byte) 0;
			result[14] = (byte) 0xbc;
			result[15] = (byte) 0;
			if (mOutputStream != null) {
				mOutputStream.write(result);
			} else {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//自动背光(0x0d)
	public void setSledCtrl() {
		// TODO Auto-generated method stub
		try {
			byte[] result = new byte[16];
			/*result[0] = (byte) 0xaa;
			result[1] = (byte) 0x01;
			result[2] = (byte) 0x01;
			result[3] = (byte) 0x0d;   //第4位，有效指令
			result[4] = (byte) 2;
			result[5] = (byte) 0;
			result[6] = (byte) 0;
			result[7] = (byte) 0;
			result[8] = (byte) 0;
			result[9] = (byte) 0;
			result[10] = (byte) 0;
			result[11] = (byte) 0;
			result[12] = (byte) 0;
			result[13] = (byte) 0;
			result[14] = (byte) 0xbb;
			result[15] = (byte) 0;*/
			result[0] = (byte)170;  //byte->16进制
			result[1] = (byte) 1;
			result[2] = (byte) 1;
			result[3] = (byte) 13;   //第4位，有效指令
			result[4] = (byte) 2;
			result[5] = (byte) 0;
			result[6] = (byte) 0;
			result[7] = (byte) 0;
			result[8] = (byte) 0;
			result[9] = (byte) 0;
			result[10] = (byte) 0;
			result[11] = (byte) 0;
			result[12] = (byte) 0;
			result[13] = (byte) 0;
			result[14] = (byte) 187;
			result[15] = (byte) 0;
			
			if (mOutputStream != null) {
				mOutputStream.write(result);
			} else {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//自动识别(0x50)
	public void setAutoIdentify() {
		try {
			byte[] result = new byte[16];
			result[0] = (byte) 0xAA;   //指令包识别码
			result[1] = (byte) 0x01;   //源设备地址
			result[2] = (byte) 0x01;   //目标设备地址
			result[3] = (byte) 0x50;   //第4位，有效指令
			result[4] = (byte) 1;      //数据内容
			result[5] = (byte) 0;
			result[6] = (byte) 0;
			result[7] = (byte) 0;
			result[8] = (byte) 0;
			result[9] = (byte) 0;
			result[10] = (byte) 0;
			result[11] = (byte) 0;
			result[12] = (byte) 0;
			result[13] = (byte) 0;
			result[14] = (byte) 0xfd;   //校验位
			result[15] = (byte) 0;
			if (mOutputStream != null) {
				mOutputStream.write(result);
			} else {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//取消校验(0x07)
	public void setDupCheck(){
		try {
			byte[] result = new byte[16];
			result[0] = (byte) 0xaa;   //指令包识别码
			result[1] = (byte) 0x01;   //源设备地址
			result[2] = (byte) 0x01;   //目标设备地址
			result[3] = (byte) 0x07;   //第4位，有效指令
			result[4] = (byte) 0;      //数据内容
			result[5] = (byte) 0;
			result[6] = (byte) 0;
			result[7] = (byte) 0;
			result[8] = (byte) 0;   //"0xA9"
			result[9] = (byte) 0;
			result[10] = (byte) 0;
			result[11] = (byte) 0;
			result[12] = (byte) 0;
			result[13] = (byte) 0;
			result[14] = (byte) 0xB3;   //校验位
			result[15] = (byte) 0;
			if (mOutputStream != null) {
				mOutputStream.write(result);
				Log.v(TAG,"setDupCheck");
			} else {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//写入指纹(0x28)
	public void writeFPcmd() {
		try {
			byte[] result = new byte[16];
			result[0] = (byte) 0xaa;   //指令包识别码
			result[1] = (byte) 0x01;   //源设备地址
			result[2] = (byte) 0x01;   //目标设备地址
			result[3] = (byte) 0x28;   //第4位，有效指令
			result[4] = (byte) 0;      //数据内容
			result[5] = (byte) 2;
			result[6] = (byte) 0;
			result[7] = (byte) 0;
			result[8] = (byte) 0;   //"0xA9"
			result[9] = (byte) 0;
			result[10] = (byte) 0;
			result[11] = (byte) 0;
			result[12] = (byte) 0;
			result[13] = (byte) 0;
			result[14] = (byte) 0xD6;   //校验位
			result[15] = (byte) 0;
			if (mOutputStream != null) {
				Log.v(TAG,"writeFPcmd");
				mOutputStream.write(result);
			} else {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//写入指纹数据(0x28_528bit)
	public void writeFPdata(byte[] array){ 
		try{
			byte[] result = new byte[528];
			result = array;
			for(int i = 0;i < result.length;i++){
				Log.i(TAG,"wirteCmdData,i="+i+"--->"+result[i]);
			}
			if (mOutputStream != null) {
				mOutputStream.write(result);
				Log.v(TAG,"wirteCmdData");
			} else {
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				Log.i(TAG, "mReadThread exit!");
				break;
			case 2:
				byte[] date1=new byte[16];
//				txtMsg.setText("收到字节"+msg.arg2);
				date1=msg.getData().getByteArray("getinfo2");
				for(int i = 0;i<date1.length;i++){
					Log.i("TEST","2222.date1["+i+"]:"+date1[i]);
				}
				
				/*
				 * date[5]是对应的指纹编号(如果有编号的话)
				 * date[4] = 33 表示同上一个指纹？
				 */
				if (date1[0]==0x55 && date1[3]==0x50)                  // 判断识别返回的信息
				{   
//					Log.i(TAG,"mParseListener:"+mParseListener);
//					Log.i(TAG,"date1[4]="+date1[4]+",date1[5]="+date1[5]+",mParseListener="+mParseListener+",instance="+instance);
					if (date1[4]==0x00){  
//						setResult("true,"+fDao.getRybh((date1[5]&0xff)+""));
						Log.i("TS","匹配成功：对应编号为："+fDao.getRybh((date1[5]&0xff)+"")+",指纹编号："+(date1[5]&0xff));
						if (mParseListener != null) {
							mParseListener.onParseFinished(fDao.getRybh((date1[5]&0xff)+""));
						}
					}
					if (date1[4]==0x12){
//						setResult("false");
//						Log.i("TS","匹配失败");
						if (mParseListener != null) {
							mParseListener.onParseFinished("false");
						}
					}
					if (date1[4]==0x33){
//						setResult("same");
//						Log.i("TS","重复的指纹数据");
						if (mParseListener != null) {
							mParseListener.onParseFinished("same");
						}
					}
				}
//				etxtTimes.setText(byte2HexStr(date1));   //显示内容
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	/*
	 * 字节数组转化
	 */
	public String byte2HexStr(byte[] b)    
	{    
	    String stmp="";    
	    StringBuilder sb = new StringBuilder("");    
	    for (int n=0;n<b.length;n++)    
	    {    
	        stmp = Integer.toHexString(b[n] & 0xFF);    
	        sb.append((stmp.length()==1)? "0"+stmp : stmp);    
	        sb.append(" ");    
	    }    
	    return sb.toString().toUpperCase().trim();    
	} 
	
	/*
	 * 串口读取
	 */
	public class ReadThreadSerial extends Thread {
		@Override
		
		public void run() {
			super.run();
			byte[] buffer = new byte[16];
			byte[] Sendbuffer = new byte[16];
			boolean  getpacket=false;
			while (tFlag) {
				int size;
				String ss=null;
				try {
					byte[] tempbuffer = new byte[16];
					if (mInputStream == null) {
						break;
					}
					size = mInputStream.read(tempbuffer);
//					Log.v(TAG,"ReadThreadSerial串口操作");
					/*Message msg1 = new Message();
					msg1.what = 1;
					myHandler.sendMessage(msg1);*/
					
					if (tempbuffer[0]==0x55){
						getpacket=true; // 找到通讯包头后开始计算
					}
			        if (getpacket)
					{//
			        	Log.i("Error","tempbuffer="+tempbuffer.length+",buffer="+buffer.length+",GetAllNum="+GetAllNum+",size="+size);
			        	if((GetAllNum+size) <= buffer.length){
			        		Log.i("Error","未超出数组界限");
			        		System.arraycopy(tempbuffer, 0, buffer, GetAllNum, size);
							GetAllNum=GetAllNum+size;
			        	}else{
			        		Log.i("Error","超出数组界限");
			        	}
						if (GetAllNum>=16){
//							Log.i(TAG,"Data Info==============="+byte2HexStr(buffer));
							Message msg = new Message();
							msg.what = 2;
							msg.arg2=GetAllNum;
		                    Bundle bundle = new Bundle();  
		                  //  bundle.putString("getinfo1","i love u");
		                    System.arraycopy(buffer, 0, Sendbuffer, 0, GetAllNum);
							bundle.putByteArray("getinfo2", Sendbuffer);
							msg.setData(bundle);
							myHandler.sendMessage(msg);
							Arrays.fill(buffer, (byte) 0);
							GetAllNum=0;
							getpacket=false;
						}
						Arrays.fill(tempbuffer, (byte) 0);
					}
				}catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}

	class WriteTask extends TimerTask {
		public void run() {

			writeOnTimeToMC(1, onTimes);
		}
	}
	
	//将指定byte数组以16进制的形式打印到控制台   
	public void printHexString( byte[] b) {     
	   for (int i = 0; i < b.length; i++) {    
	     String hex = Integer.toHexString(b[i] & 0xFF);    
	     if (hex.length() == 1) {    
	       hex = '0' + hex;    
	     }    
	     Log.i(TAG,"byte数组以16进制显示："+hex.toUpperCase());
	     System.out.print(hex.toUpperCase() );    
	   }    
	}
	
	public SerialPort getSerialPort() throws SecurityException, IOException,
			InvalidParameterException {
		mSerialPort = new SerialPort(new File("/dev/s3c2410_serial2"), 38400, 0);
		return mSerialPort;
	}
	
	public void closeSerialPort() {
		Log.v(TAG, "closeSerialPort");
		if (mSerialPort != null) {
			mSerialPort.close();
			mSerialPort = null;
		}
	}
	
	public void setResult(String result){
		this.results = result;
	}

	public String getResult(){
		return results;
	}
	
	public void writeOnTimeToMC(int flags, long times) {

		try {
			byte[] mBuffer = longToByteArray(flags, times);
		//	Log.e(TAG, "writeOnTimeToMC--------time=" + times);
			int i;
			for (i = 0; i < mBuffer.length; i++)
	//			Log.i(TAG, "BUFFER-----HASHCODE=" + mBuffer[i]);
	//		mReadThread = new ReadThread();
	//		mReadThread.start();
	//		ReadThreadSerial
			mReadThread2 = new ReadThreadSerial();   //开启线程
			mReadThread2.start();
			
			if (mOutputStream != null) {
				mOutputStream.write(mBuffer);
			} else {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	//鍚戝崟鐗囨満鍙戦�鐨勬暟鎹�涓猙yte
	public byte[] longToByteArray(int flags, long times) {
		byte[] result = new byte[9];
		result[0] = (byte) 0x00;
		result[1] = (byte) 0xaa;
		result[2] = (byte) 0xff;
		result[3] = (byte) 0x55;

		result[4] = (byte) (flags);

		result[5] = (byte) ((times >> 16) & 0xFF);
		result[6] = (byte) ((times >> 8) & 0xFF);
		result[7] = (byte) (times & 0xFF);

		result[8] = (byte) 0x55;

		return result;
	}
	
	public void close() {
		/*if(Socket != null){
			Log.e(TAG,"onDestroy0");
			Socket.close();
		}*/
		closeSerialPort();
		fDao.close();
		mSerialPort = null;
		Log.v(TAG,"close");
	}
	
	public static FingerPrintUtil getInstance(Context context) {
		Log.v(TAG,"getInstance");
		if (instance == null) {
			instance = new FingerPrintUtil(context);
		}
		return instance;
	}
	
	public void registerParseListener(ParseListener listener) {
		Log.v(TAG,"registerParseListener");
		mParseListener = listener;
	}
	
	public interface ParseListener {
		void onParseFinished(String result);
	}
}
