package com.kedacom.platform2mc.ntv;

public class IPhoenixSDKConstantVal {

	// ptz value
	public final static int SDK_PTZ_LEFT = 0;		//ptz        
	public final static int SDK_PTZ_RIGHT = 1;		//ptz        
	public final static int SDK_PTZ_UP = 2;			//ptz        
	public final static int SDK_PTZ_DOWN = 3;		//ptz        
	public final static int SDK_PTZ_LEFT_UP =4;		//ptz        
	public final static int SDK_PTZ_LEFT_DOWN = 5;	//ptz        
	public final static int SDK_PTZ_RIGHT_UP = 6;	//ptz         
	public final static int SDK_PTZ_RIGHT_DOWN = 7;	//ptz        
	public final static int SDK_PTZ_STOP = 8;		//ptz    ֹͣ
	public final static int SDK_PTZ_ZOOMIN = 9;		//ptz     Ŵ 
	public final static int SDK_PTZ_ZOOMOUT = 10;	//ptz      С
	public final static int SDK_PTZ_ZOOMSTOP = 11;	//ptz        ֹͣ
	public final static int SDK_PTZ_HOME = 12;

	// ƽ̨    
	public final static int SDK_PLATFORM_TYPE_INVAILED = 0; // Ƿ ƽ̨    
	public final static int SDK_PLATFORM_TYPE_1 = 1;		//ƽ̨1.0
	public final static int SDK_PLATFORM_TYPE_2 = 2;		//ƽ̨2.0

	//             
	public final static int SDK_STREAM_MODE_TYPE_INVAILED = 0;	// Ƿ ƽ̨    
	public final static int SDK_STREAM_MODE_TYPE_PLAT_1 = 1;	//ƽ̨1.0
	public final static int SDK_STREAM_MODE_TYPE_PLAT_2 = 2;	//ƽ̨2.0
	public final static int SDK_STREAM_MODE_TYPE_G900 = 3;		//G900

	//         
	public final static int SDK_DECODER_MODE_INVAILED = 0; //SDK      
	public final static int SDK_DECODER_MODE_BASE_DEC = 1; //SDK          

	//   ӡ    
	public final static int SDK_SCREEN_PRINT_LOG_LEVER_FORBIDDEN = 0;//  ֹ  ӡ
	public final static int SDK_SCREEN_PRINT_LOG_LEVEL_NOMAL = 1;//  ӡȫ    ־  Ϣ
	public final static int SDK_SCREEN_PRINT_LOG_LEVER_KEY = 2;//  ӡ ؼ   ־  Ϣ
	public final static int SDK_SCREEN_PRINT_LOG_LEVER_SUCC = 3;//ֻ  ӡ ɹ   ־  Ϣ
	public final static int SDK_SCREEN_PRINT_LOG_LEVER_FAIL = 4;//ֻ  ӡʧ    ־  Ϣ


	//             
	public final static int SDK_VIDEO_PLAY_INVAILED = -1;// Ƿ     Ƶ         
	public final static int SDK_VIDEO_PLAY_RECORD = 0;//¼  طŵ   Ƶ   
	public final static int SDK_VIDEO_PLAY_REAL = 1;//ʵʱ        Ƶ   
	// ¼      
	public final static int SDK_RECORD_TYPE_INVAILED = -1;//¼   ѯʱ  ¼  ط ʱ Ƿ ¼      
	public final static int SDK_RECORD_TYPE_PLATFORM = 0;//¼   ѯʱ  ¼  ط ʱƽ̨¼      
	public final static int SDK_RECORD_TYPE_PU = 1;		//¼   ѯʱ  ¼  ط ʱǰ  ¼      

	//       Ϣ    
	public final static int SDK_SUB_SCRIPTION_TYPE_ONLINE = 1;//ֻ     豸        Ϣ
	public final static int SDK_SUB_SCRIPTION_TYPE_ALARM = 2;//ֻ     豸 澯  Ϣ
	public final static int SDK_SUB_SCRIPTION_TYPE_CONFIG = 4;//ֻ    NVRͨ        Ϣ
	public final static int SDK_SUB_SCRIPTION_TYPE_DEVSTATE = 7;// 豸     ߣ  澯  Ϣ  NVRͨ    Ϣȫ    
	public final static int SDK_SUB_SRCIPTION_TYPE_GPS = 8;//ֻ     豸GPS  Ϣ

	// 豸      Ϣ ص  ¼     
	public final static int SDK_SUB_SCRIP_CALL_BACK_TYPE_ONLINE = 0;// 豸        Ϣ ص     
	public final static int SDK_SUB_SCRIP_CALL_BACK_TYPE_ALARM = 1;// 豸 澯  Ϣ ص     
	public final static int SDK_SBU_SCRIP_CALL_BACK_TYPE_CONFIG = 2;// 豸ͨ    Ϣ ص     
	public final static int SDK_SBU_SCRIP_CALL_BACK_TYPE_GPS = 3;// 豸GPS  Ϣ ص 
	
	//¼  طŵ CLT ¼     
	public final static int SDK_RECORD_PLAY_CLT_TYPE_PLAY = 0;//¼  ط   ͣ  ط 
	public final static int SDK_RECORD_PLAY_CLT_TYPE_PAUSE = 1;//¼  طŵ   ͣ
	public final static int SDK_RECORD_PLAY_CLT_TYPE_SEEK = 2;//¼  طŵ  ϶     
	public final static int SDK_RECORD_PLAY_CLT_TYPE_SCALE = 3;//¼  ط ʱ Ŀ ź    
	
	//      Ƶ              
	public final static int SDK_VIDEO_PLAY_FLUENT = 0;		//ʵʱ   ͼ       ģʽ
	public final static int SDK_VIDEO_PLAY_HIGHDEFINITION = 1;//ʵʱ   ͼ       ģʽ
	public final static int SDK_VIDEO_PLAY_INDEX_INVAILED = 0;//ʵʱ   ͼ  URL ķǷ index,   Ӧ ò㲻ʵ  StreamUrlCallBackFunc ӿڣ  򷵻ظ ֵ
	
	// 澯    
	public final static int SDK_ALARM_TYPE_INVAILED = 0;
	public final static int SDK_ALARM_TYPE_MOVE = 1;
	public final static int SDK_ALARM_TYPE_INPUT = 2;
	public final static int SDK_ALARM_TYPE_DISK_FULL = 3;
	public final static int SDK_ALARM_TYPE_VIDEO_LOST = 4;
	public final static int SDK_ALARM_TYPE_INTE_LLIGENT = 5;
	public final static int SDK_ALARM_TYPE_VIDEO = 6;
		
	//  豸    
	public final static int SDK_DEVICE_CAP_TYPE_BULLET = 0;		//ǰ    ǹ  	
	public final static int SDK_DEVICE_CAP_TYPE_DOME = 1;		//ǰ       
	public final static int SDK_DEVICE_CAP_TYPE_NULL = 2;		//    ǹ  Ҳ       

	//  豸    
	public final static int SDK_DEVICE_TYPE_UNKNOWN = 0;
	public final static int SDK_DEVICE_TYPE_ENCODER = 1;
	public final static int SDK_DEVICE_TYPE_DECODER = 2;
	public final static int SDK_DEVICE_TYPE_CODECER = 3;
	public final static int SDK_DEVICE_TYPE_TVWALL = 4;
	public final static int SDK_DEVICE_TYPE_NVR = 5;
	public final static int SDK_DEVICE_TYPE_SVR = 6;
	public final static int SDK_DEVICE_TYPE_ALARM_HOST = 7;
	
	//ץ  ͼƬ  ʽ
	public final static int SDK_SNAP_SHOT_TYPE_BMP_32 = 0;
	public final static int SDK_SANP_SHOT_TYPE_JPG_100 = 1;
	public final static int SDK_SNAP_SHOT_TYPE_JPG_70 = 2;
	public final static int SDK_SNAP_SHOT_TYPE_JPG_50 = 3;
	public final static int SDK_SNAP_SHOT_TYPE_JPG_30 = 4;
	public final static int SDK_SANP_SHOT_TYPE_JPG_10 = 5;
	public final static int SDK_SNAP_SHOT_TYPE_BMP_24 = 6;
	
	//    ¼ 񱣴     
	public final static int SDK_LOCAL_REC_TYPE_MP4 = 0;		//
	public final static int SDK_LOCAL_REC_TYPE_3GP = 1;
	public final static int SDK_LOCAL_REC_TYPE_ASF = 2;
	
	//  Ƶ        
	public final static int SDK_VIDEO_FORMAT_INVAILED = 0;
	public final static int SDK_VIDEO_FORMAT_SN4 =1;
	public final static int SDK_VIDEO_FORMAT_MPEG4 = 2;
	public final static int SDK_VIDEO_FORMAT_H261 = 3;
	public final static int SDK_VIDEO_FORMAT_H263 = 4;
	public final static int SDK_VIDEO_FORMAT_H264 = 5;
	
	//  Ƶ           
	public final static int SDK_VIDEO_RESOLUTION_VR_INVAILED = 0;
	public final static int SDK_VIDEO_RESOLUTION_VR_AUTO =1;
	public final static int SDK_VIDEO_RESOLUTION_VR_QCIF = 2;
	public final static int SDK_VIDEO_RESOLUTION_VR_CIF = 4;
	public final static int SDK_VIDEO_RESOLUTION_VR_2CIF = 8;
	public final static int SDK_VIDEO_RESOLUTION_VR_4CIF = 16;
	public final static int SDK_VIDEO_RESOLUTION_VR_QQCIF = 32;
	public final static int SDK_VIDEO_RESOLUTION_VR_QVGA = 64;
	public final static int SDK_VIDEO_RESOLUTION_VR_VGA = 128;
	public final static int SDK_VIDEO_RESOLUTION_VR_720P = 256;
	public final static int SDK_VIDEO_RESOLUTION_VR_1080P = 512;
	public final static int SDK_VIDEO_RESOLUTION_VR_QXGA = 1024;
	
	//  Ƶ     ȼ 
	public final static int SDK_VIDEO_QUALITY_INVAILED  = 0;
	public final static int SDK_VIDEO_QUALITY_NOMAL  	= 1;
	public final static int SDK_VIDEO_QUALITY_SPEED     = 2;
	
	//  ͻ       
	public final static String SDK_CLIENT_TYPE_ANDROID_PHONE = "ANDROID_PHONE";
	public final static String SDK_CLIENT_TYPE_ANDROID_PAD = "ANDROID_PAD";
	public final static String SDK_CLIENT_TYPE_IOS_PHONE = "IOS_PHONE";
	public final static String SDK_CLIENT_TYPE_IOS_PAD = "IOS_PAD";
	public final static String SDK_CLIENT_TYPE_LINUX_CLIENT = "LINUX_CLIENT";
	public final static String SDK_CLIENT_TYPE_LINUX_SERVER = "LINUX_SERVER";
	public final static String SDK_CLIENT_TYPE_WIN_CLIENT = "WIN_CLIENT";
	public final static String SDK_CLIENT_TYPE_WIN_JNI_CLIENT = "WIN_JNI_CLIENT";
	public final static String SDK_CLIENT_TYPE_WIN_JNI_SERVER = "WIN_JNI_SERVER";

	//SDK ص  ¼     
	public final static int SDK_EVENT_CALLBACK_WORKTYPE_LOGIN = 1;					//  ½ ¼  ص 
	public final static int SDK_EVENT_CALLBACK_WORKTYPE_LOGOUT = 2;					//ע   ¼  ص 
	public final static int SDK_EVENT_CALLBACK_WORKTYPE_GETGROUP = 3;		  		//  ȡ 豸   ¼  ص 
	public final static int SDK_EVENT_CALLBACK_WORKTYPE_GETDEVICE = 4;				//  ȡ 豸 ¼  ص 
	public final static int SDK_EVENT_CALLBACK_WORKTYPE_STARTSTREAM = 5;			//           ţ     ʵʱ         ¼        Ƿ ɹ  ¼  ص 
	public final static int SDK_EVENT_CALLBACK_WORKTYPE_RECVKEYFRAME = 6;			//SDK յ      ؼ ֡ ص 
	public final static int SDK_EVENT_CALLBACK_WORKTYPE_STOPSTREAM = 7;				//ֹͣ         ¼  ص 
	public final static int SDK_EVENT_CALLBACK_WORKTYPE_PTZ = 8;					//ptz    ʱ  ص 
	public final static int SDK_EVENT_CALLBACK_WORKTYPE_SUBSCRIPT = 9;				//  Ϣ     ¼  ص 
	public final static int SDK_EVENT_CALLBACK_WORKTYPE_LOCALREC = 10;				//    ¼   ¼  ص 
	public final static int SDK_EVENT_CALLBACK_WORKTYPE_SNAPSHOT = 11;				//    ץ   ¼  ص 
	public final static int SDK_EVENT_CALLBACK_WORKTYPE_SEARCHREC = 12;		    	//    ¼   ¼  ص 
	public final static int SDK_EVENT_CALLBACK_WORKTYPE_STARTPLAYREC = 13;			//    ¼ 񲥷  ¼  ص 
	public final static int SDK_EVENT_CALLBACK_WORKTYPE_STOPPLAYREC = 14;			//    ¼  ֹͣ     ¼  ص 
	public final static int SDK_EVENT_CALLBACK_WORKTYPE_VCRCTRL = 15;				//¼  ط VCR        ¼  ص 
	public final static int SDK_EVENT_CALLBACK_WORKTYPE_HeartbeatErr = 16;			//  ƽ̨      ʧ   ¼  ص 
	public final static int SDK_EVENT_CALLBACK_WORKTYPE_SEARCHDVC = 17;				//SDK     豸 ¼  ص 
	public final static int SDK_EVENT_CALLBACK_WORKTYPE_RECORD_DOWNLOAD = 18;		//    ¼      ʱ   ص 
	public final static int SDK_EVENT_CALLBACK_WORKTYPE_RECORD_DOWNLOAD_PACE = 19;	//¼     ؽ   ¼  ص 
	public final static int SDK_EVENT_CALLBACK_WORKTYPE_UNKNOWN = 255;				// Ƿ    ͵  ¼  ص 

	// SDK      
	public final static int SDK_ERRCODE_MODUAL_INVALID = 60001;		//   Чģ  
	public final static int SDK_ERRCODE_TASK_INVALID = 60002;		//   Ч    
	public final static int SDK_ERRCODE_TASK_CREATE_ERROR = 60003;//         ʧ  
	public final static int SDK_ERRCODE_INPUT_ERROR = 60004;//        
	public final static int SDK_ERRCODE_GET_DATA_ERROR = 60005;//   ȡ  ݴ   
	public final static int SDK_ERRCODE_NET_ERROR = 60006;//        
	public final static int SDK_ERRCODE_SNAPSHOT_ERROR = 60007;	//ץ  ͼƬʧ  
	public final static int SDK_ERRCODE_DGROUP_ROOT_INFO_ERROR = 60008; //  ȡ  Ŀ¼ µ  豸  Ϣ    
	//        ŵ ͬ        
	public final static int SDK_ERRCODE_PLAYER_ERR_MVC_CONNECT_MVS_FAILED = 10120;//MVC    MVSʱTCP  ·    ʧ  
	public final static int SDK_ERRCODE_PLAYER_ERR_STREAM_GET_IDLE_STREAM = 61000;//    ģ        ʧ  
	public final static int SDK_ERRCODE_PLAYER_ERR_DECODER_GET_IDLE_DECODER = 61001;//    ģ        ʧ  
	public final static int SDK_ERRCODE_PLAYER_ERR_DECODER_CREATE = 61002;//          ʧ  
	public final static int SDK_ERRCODE_PLAYER_ERR_DECODER_START_PLAY_STREAM = 61003;//        ģ  ʧ  
	public final static int SDK_ERRCODE_PLAYER_ERR_DECODER_START_PLAY_WND = 61004;//  ʾģ   ʼ  ʧ  
	public final static int SDK_ERRCODE_PLAYER_ERR_CONVERT_GB_DEVICED_ID = 61005;//devicedIDת   IDʧ  
	public final static int SDK_ERRCODE_PLAYER_ERR_G900_INIT_FAIL = 61006;//G900ģ   ʼ  ʧ  
	public final static int SDK_ERRCODE_PLAYER_ERR_FROM_G900_GET_URL = 61007;//  G900  ȡURLʧ  
	public final static int SDK_ERRCODE_PLAYER_ERR_G900_START_REQ_FAIL = 61008;//g900ģ 鷢         ʧ  
	public final static int SDK_ERRCODE_MCU_PLAYER_ERR_DEVICES_OFFLINE = 61009;// 豸      
	public final static int SDK_ERRCODE_PLAYER_ERR_G900_URL_NOT_SUPPORT_ALL = 61010;//g900ģ 鷢         ʧ  
	//   Ƶ       ŵ  첽      
	public final static int SDK_ERRCODE_PLAYER_ERR_NO_KEY_FRAME_COME = 61100;//     ؼ   û й   
	public final static int SDK_ERRCODE_PLAYER_ERR_CONNECT_MVC_FAIL = 61102;//mvc    mvsʧ  
	public final static int SDK_ERRCODE_PLAYER_ERR_DISCONNECT_MVC_NTF = 61105;// յ mvs    ֪ͨ
	public final static int SDK_ERRCODE_PLAYER_ERR_CONNECT_MVC_TIMEOUT = 61106;//mvc    mvs  ʱ
	public final static int SDK_ERRCODE_PLAYER_ERR_G900_ERR_FAIL = 61901;//G900    
	public final static int SDK_ERRCODE_PLAYER_ERR_G900_ERR_UNINIT = 61902;//G900δ  ʼ  
	public final static int SDK_ERRCODE_PLAYER_ERR_G900_ERR_UNCONNECT = 61903;//δ    G900
	public final static int SDK_ERRCODE_PLAYER_ERR_G900_ERR_PARAM = 61904;//G900        
	public final static int SDK_ERRCODE_PLAYER_ERR_G900_ERR_INVALID_PLAYEID = 61905;//G900 playid  Ч
	public final static int SDK_ERRCODE_PLAYER_ERR_G900_TIMEOUT = 61906;//G900     ʱ
	// ¼   ѯ      
	public final static int SDK_ERRCODE_QUERY_RECORD_THREAD_NOT_NULL = 62000;//  ѯ¼   ļ  ߳  Ѵ   
	public final static int SDK_ERRCODE_QUERY_RECORD_TASKID_NOT_EXITS = 62001;//  ѯƽ̨¼   taskID      
	public final static int SDK_ERRCODE_QUERY_RECORD_MANAGER_NULL = 62002;//  ѯƽ̨¼  ʱ  ݹ   ģ  ΪNULL
	public final static int SDK_ERRCODE_QUERY_RECORD_MANAGER_GET_DATA_NULL = 62003;//  ѯƽ̨¼  ʱ  ݹ   ģ   ȡ   ΪNULL
	public final static int SDK_ERRCODE_QUERY_RECORD_QUERY_REQ_FAILED = 62004;//  ѯƽ̨¼    ִ   
	public final static int SDK_ERRCODE_QUERY_RECORD_QUERY_RSP_FAILED = 62005;//  ѯƽ̨¼ 񷵻ؽ    ִ   
	public final static int SDK_ERRCODE_QUERY_RECORD_QUERY_NUM_ZERO = 62006;//  ƽ̨20  ȡ  ¼   ļ     Ϊ0
	public final static int SDK_ERRCODE_RECORD_SEEK_TIME_OUT_RANG = 62007;//vcr    ʱseektimeʱ    ļ 
	public final static int SDK_ERRCODE_RECORD_STOP_PLAY_NTF = 62008;// յ MVS¼  ط ֹ֪ͣͨ
	public final static int SDK_ERRCODE_RECORD_TYPE_WRONG = 62009;//¼     Ͳ      
	public final static int SDK_ERRCODE_RECORD_GET_DEV_CHN_WRONG = 62010;//¼  ط ʱ  ѯ 豸  Ϣ    
	public final static int SDK_ERRCODE_RECORD_GET_TIME_RANGE = 62011;//¼  ط ʱ  ȡ¼  ʼ    ʱ     
	//      豸 Ĵ     
	public final static int SDK_ERRCODE_SEARCH_DVC_OK = 62100;//     豸  
	public final static int SDK_ERRCODE_SERACH_DVC_THREAD_EXITS = 62101;//     豸 ߳  Ѵ   
	public final static int SDK_ERRCODE_SERACH_DVC_NO_DEVICES = 62102;//     豸      
	// ¼     صĴ     
	public final static int SDK_ERRCODE_RECORD_DOWNLOAD_ERR_CREATE_KEDAPLAYER_ERR = 62110;//¼     ش   kedaplayer    
	public final static int SDK_ERRCODE_RECORD_DOWNLOAD_ERR_PLATFORM_CONNECT_FAIL = 62111;//¼          ƽ̨    
	public final static int SDK_ERRCODE_RECORD_DOWNLOAD_ERR_PLATFORM_DIRCRIPTION_NULL = 62112;//¼           ļ Ϊ  
	public final static int SDK_ERRCODE_RECORD_DOWNLOAD_ERR_LOCAL_DISK_FULL = 62113;//   ش  ̿տռ     
	public final static int SDK_ERRCODE_RECORD_DOWNLOAD_ERR_LOCAL_FULL_NAME_NULL = 62114;//   ر    ļ   Ϊ  
	public final static int SDK_ERRCODE_RECORD_DOWNLOAD_ERR_DOWNLOAD_ERR = 62115;//¼             
	// ocx    ش     
	public final static int SDK_ERRCODE_OCX_INIT_ERR = 66000;//  ʼ      
	public final static int SDK_ERRCODE_OCX_WAIT_REC_OVERTIME = 66001;//¼   ѯ ȴ     ־  ʱ
	public final static int SDK_ERRCODE_OCX_UNINIT_ERR = 66002;//    ʼ      

}
