package com.kedacom.platform2mc.ntv;		//package, import行可根据实际项目修改

public class IPhoenixSDK_Android extends IPhoenixSDK{
    private static final String TAG = "[IPhoenixSDK_Android]";

    static {																				  
    	//不可修改，这二个库的存放路径应放在工程目录下的/libs/armeabi/下
//    	System.loadLibrary("kdvideoplayer");
//    	System.loadLibrary("kdaudiodecoder");
//        System.loadLibrary("kdaudioplayer");
//        System.loadLibrary("kd_codec_sdk_armv7");
//        System.loadLibrary("McuSDK_AndroidJni");
    																				  
        	//不可修改，这二个库的存放路径应放在工程目录下的/libs/armeabi/下
        	System.loadLibrary("osp");
        	System.loadLibrary("uniplay");
          System.loadLibrary("McuSDK_AndroidJni");
        
    }
}




