package com.kedacom.platform2mc.ntv;		//package, import行可根据实际项目修改

//import android.util.Log;

import com.kedacom.platform2mc.struct.AppVersionUrl;
import com.kedacom.platform2mc.struct.ChnTranData;
import com.kedacom.platform2mc.struct.DevAllChnIdName;
import com.kedacom.platform2mc.struct.DevChn;
import com.kedacom.platform2mc.struct.DevSrcSta;
import com.kedacom.platform2mc.struct.DeviceGroupInfo;
import com.kedacom.platform2mc.struct.DeviceID;
import com.kedacom.platform2mc.struct.DeviceInfo;
import com.kedacom.platform2mc.struct.DeviceStatus;
import com.kedacom.platform2mc.struct.EncoderParam;
import com.kedacom.platform2mc.struct.EncoderParamTotal;
import com.kedacom.platform2mc.struct.EventInfo;
import com.kedacom.platform2mc.struct.GroupID;
import com.kedacom.platform2mc.struct.PointMoveCmd;
import com.kedacom.platform2mc.struct.PtzCmd;
import com.kedacom.platform2mc.struct.RecPlyCtlInfo;
import com.kedacom.platform2mc.struct.SearchDvcRspInfo;
import com.kedacom.platform2mc.struct.StreamParam;
import com.kedacom.platform2mc.struct.StreamUrlList;
import com.kedacom.platform2mc.struct.SubsDevices;
import com.kedacom.platform2mc.struct.TPeriod;
import com.kedacom.platform2mc.struct.TRecordDownloadInfo;
import com.kedacom.platform2mc.struct.TRecordInfo;
import com.kedacom.platform2mc.struct.VidOSD;



public class IPhoenixSDK {

    //java层向jni层设置设备状态信息（设备上下线及告警）回调函数接口
    public static native boolean MsgSetEnv();

	//java层向jni层获取设备状态信息值（设备上下线及告警）的接口
	//具体是设备上下线的信息还是告警信息回调要解析结构体DeviceStatus的值
    public static native boolean JSetMsg(int useless[], DeviceID mDeviceID, DeviceStatus mDeviceStatus);

	//java层向jni层设置设备事件回调（请求码流播放，抓拍回调，以及关键桢回调）函数接口
    public static native boolean MsgSetEnvEventCB();

	//java层向jni层获取设备事件回调的值（请求码流播放，抓拍回调，以及关键桢回调）的接口
	//具体是哪种事件的回调需要解析结构体EventInfo的值
    public static native boolean JSetMsgEventCB(int useless[], EventInfo mEventInfo);

	public native int	PlatTypeDetect(String server,int errorCode[]);

	//形参BusinessMode值为1,表示初始化平台1.0的环境;形参BusinessMode值为2,表示初始化平台2.0的环境
	//形参StreamMode值为3时，表示SDK用的是g900接收码流
	//形参DecoderMode的值为1时，表示启用SDK的解码
	public native boolean ModualSelect(int BusinessMode,int StreamMode,int DecoderMode);
	//SDK初始化函数
 	public native boolean Init();
	//SDK初始化函数第二版本，需要传入Context，其他相同，以支持新的Android Media音频播放方案
    public native boolean InitV2(Object context);

	//SDK销毁函数
    public native boolean Cleanup();
		
	//获取SDK版本信息（SDK编译的时间和日期）
    public native boolean GetAppVersion(AppVersionUrl mAppVersionUrl);
    
    //客户端登陆平台的函数
    public native boolean LogIn(String userName, String passWord, String server, String clientType, int errorCode[]);	//native 方法不可修改

	//客户端登出（注销）平台的函数
    public native boolean Logout();
        
	//获取设备分组信息
    public native int GetGroupByGroup(GroupID groupid, int errorCode[]);

	//其中输入参数taskID为函数GetGroupByGroup的返回值
    public native boolean GetGroupNext(int taskID, DeviceGroupInfo groupInfo, int errorCode[]);
		
	//获取设备信息
    public native int GetDeviceByGroup(GroupID groupid, int errorCode[]);
    
    //其中输入参数taskID为函数GetDeviceByGroup的返回值
    public native boolean GetDeviceNext(int taskID, DeviceInfo deviceInfo, int errorCode[]);
    
    //ptz操作接口（上，下，左,右）
    public native boolean SetPtzControl(DevChn tDevChn, PtzCmd tPtzCmd, int errorCode[]);

	//ptz操作接口（只针对长按居中）
    public native boolean SetPointMoveControl(DevChn tDevChn, PointMoveCmd tPointMoveCmd, int errorCode[]);
	
	//ptz接口(载入预置位)	
	public native boolean SetPtzPreSet(DevChn tDevChn, int tPreSetPostion, int errorCode[]);
	
	//ptz接口(保存预置位)
	public native boolean SavePtzPreSet(DevChn tDevChn, int tPreSetPostion, int errorCode[]);

    //获取设备的国标ID接口
    public native boolean GetDeviceGBID(DevChn tDevChn, DevChn tDevGbID);

    //开始实时码流播放接口
    public native int StartRealPlay(DevChn tDevChn, StreamParam tStreamParam, Object surfaceView, int errorCode[]);

	//停止实时码流播放接口
    public native boolean StopRealPlay(int dwPlayID, int errorCode[]);
    
    //开始本地录像接口
    //其中输入参数fileType为1时，表示本地录像文件保存的格式为em3gp
    //其中输入参数fileType为非1时，无效(暂时不支持)
    public native boolean StartLocalRecord(int dwPlayID, String strFileName, int fileType);
    
    //停止本地录像接口
    public native boolean StopLocalRecord(int dwPlayID);
    
    //抓拍函数接口
    //其中输入参数fileType为1时，表示抓拍图片的保存格式为emJpeg.
    //其中输入参数fileType非1时，无效(暂时不支持)
    public native boolean SaveSnapshot(int dwPlayID, String strFileName, int fileType);

	//设置播放视频高清与流畅接口
	//输入参数resolutionType为1时为高清，输入参数为0时为流畅
    public native boolean SetHighDefinitionValue(int resolutionType);

	//java应用层设置要订阅的设备，一次最多可以订阅20个设备
	//其中传入参数subscriptType为1时表示订阅设备上下线信息
	//其中传入参数subscriptType为2时表示订阅设备告警信息
	//其中传入参数subscriptType为3时表示订阅设备上下线和告警信息
    public native boolean SetSubscriptDeviceStatus(SubsDevices mSubsDevices, int subscriptType, int errorCode[]);
    
    //java应用层设置要取消订阅的设备，只支持单个设备逐个订阅
    //其中传入参数subscriptType的含义同上
    public native boolean SetUnSubscriptDeviceStatus(SubsDevices mSubsDevices, int subscriptType, int errorCode[]);
    

	//向G900请求码流时发送手机的UUID用来标识手机设备，建议在SDK初始化成功以后，调用一次此接口
    public native boolean SetFeatureCode(String temFeatureCode);


	//创建回放录像的taskID，返回值就是taskID，大于0的值，返回-1表示出错
    public native int CreateRecordTask(DevChn temDevChn ,int errorCode[]);
	//查询一个时间段的录像
    public native boolean PreLoadRecord(int dwTaskId, TPeriod PreLoadTime, int recordType, int errorCode[]);
	//再收到查询录像的事件回调以后调用此接口获取record内容
    public native boolean GetRecordNext(int dwTaskId, int dwSeekTime, TRecordInfo dwRecordInfo, int errorCode[]);
	//开始回放录像
    public native int StartRecordplay(int dwTaskId, int dwPlayTime, int playType,Object surfaceView, int errorCode[]);

	//录像回放的VCR控制
	public native boolean RecordPlayCtrl(RecPlyCtlInfo stRecPlyCtlInfo,int errorCode[]);

	//停止回放录像
    public native boolean StopRecordplay(int playId,int errorCode[]);
	//销毁之前创建的tasdkID
    public native boolean DestroyRecordTask(int dwTaskId);

	public native boolean SetVideoOSD(DevChn tDevChn,VidOSD tVidOsd,int errorCode[]);

	//开启平台录像
	public native boolean StartPlatRec(DevChn tDevChn, int errorCode[]);
	//停止平台录像
	public native boolean StopPlatRec(DevChn tDevChn, int errorCode[]);
	//设置声音有效
    public native boolean SetAudioEnable(int jplayId);
	//设置无声
    public native boolean SetAudioDisable(int jplayId);
	//搜索设备
    public native boolean SearchDvc(String jSearchDvcName,int SearchResLimt,int errorCode[]);
	//获取搜索结果
	public native boolean GetSearchDvcRspInfo( SearchDvcRspInfo jSearchDvcInfo,int errorCode[]);
	//获取父结点的信息
	public native boolean GetGroupInfo(GroupID jGroupId,DeviceGroupInfo jGroupInfo);
	//传入参数jdwLogFlag为1时表示开启保存log日志文件，0表示关闭保存log日志文件 保存的路径在“mnt/sdcrad/log/”文件夹
	public native boolean SetSaveLogFile(int jdwLogFlag,String jSaveLogFileDirectName);
	//传入参数jdwShowLogLev为1表示开启屏幕打印，0表示关闭屏幕打印
	public native boolean SetScreenShowLog(int jdwShowLogLev);
	
	public native boolean GetDevSrcStatus(DevChn tDevChn,DevSrcSta tDevSrcSta,int errorCode[]);
	
	public native boolean SendKeyFrame(DevChn tDevChn,int errorCode[]);
	
	public native boolean SendTransData(DevChn tDevChn,ChnTranData tChnTranData,int errorCode[]);
	
	public native boolean GetPuEncoderParam(DevChn tDevChn,EncoderParamTotal tEncParaTotal,int errorCode[]);
	
	public native boolean SetPuEncoderParam(DevChn tDevChn,EncoderParam tEncParam,int errorCode[]);
	
	public native boolean GetDevChnIdName(DevChn tDevChn,DevAllChnIdName tDevChnIdName,int errorCode[]);
	
	//录像下载
	public native int StartRecordDownLoad(int dwTaskID, TRecordDownloadInfo tDownloadInfo, int errorCode[]);
	public native boolean StopRecordDownLoad(int dwPlayID, int errorCode[]);
	//设置码流类型streamPattern:0表示TCP码流, :1表示UDP码流
	public native boolean SetStreamPattern(int streamPattern, int errorCode[]);
	//修改密码
	public native boolean ChangePassword(String oldPwd, String newPwd, int errorCode[]);
	//开始url播放
    public native int StartPlay(String utf8_url, StreamParam tStreamParam, Object surfaceView, int errorCode[]);
	//停止url播放
    public native boolean StopPlay(int dwPlayID, int errorCode[]);
	
	public void SetMainCtx() {	
		//函数名可修改，函数内部实现不可修改，用于初始化设备状态回调函数和设备事件回调函数
		MsgSetEnv();
    	MsgSetEnvEventCB();
	}
	
	public static void myCallbackFunc() {		
    	//设备状态（上下线，告警等）回调函数，函数名不可修改，函数内部实现可修改
	}

    public static void myEventCallBackFunc() {
    	//设备事件（请求播放码流，抓拍是否成功）回调函数，函数名不可修改，函数内部实现可修改
	}

	public static int StreamUrlCallBackFunc(StreamUrlList temUrlList)
	{
		//SDK的URL回调
		//应用层可以实现这个函数，也可以不实现这个函数。
		//如果应用层不实现这个函数，返回值请返回65535,
		//应用层如果实现这个函数，返回值应该小于m_byUrlNum
		//Log.d("xql", "temUrlList.m_byUrlNum="+temUrlList.m_byUrlNum);
		for(int UrlIdx = 0;UrlIdx < temUrlList.m_byUrlNum;UrlIdx++)
		{
			//Log.d("xql", "tempUrlInfo.m_szUrl="+temUrlList.m_StreamUrl[0].m_szUrl+"  tempUrlInfo.m_dwWidth="+temUrlList.m_StreamUrl[0].m_dwWidth+"  tempUrlInfo.m_dwHeight="+temUrlList.m_StreamUrl[0].m_dwHeight);
		}
		int RtnUrlIndx = IPhoenixSDKConstantVal.SDK_VIDEO_PLAY_INDEX_INVAILED;
		
		return RtnUrlIndx;
	}
}
