package com.example.iimp_znxj_new2014.util;

public class Constant {
	
	public static String DJAPP_ALIVE="djapp_alive";//第三方检测程序接收广播
	
	public static String VIDEO_VOLUME="video_volume";//流媒体音量
	public static String VCR_VOLUME="vcr_volume";//直播音量
	public static String SCROLL_VOLUME="scroll_volume";//滚动字幕音量
	
	public static  String VCR_START_TIME="";//全局最后一次播放的直播开始时间
	public static  String VCR_END_TIME="";//全局最后一次播放的结束时间
	public static  String VIDEO_START_TIME="";//全局最后一次播放的流媒体开始时间
	public static  String VIDEO_END_TIME="";//全局最后一次播放的流媒体结束时间
	
	public static final String VCR_OR_VIDEO_CONTINUE="vcr_or_video_continue";//插播导致的中断的流媒体或直播计划
	
	public static final String CURRENT_VIDEO_PERCENT="current_video_percent";//缓存当前播放文件及进度标签
	
	public static final String DUTY_PALTRL="duty_paltrl";//值班签到
	public static final String DUTY_REMIND="duty_remind";//值班提醒
	
	public static final String VIDEO_IP="video_ip";//播放流媒体ip
    public static final String VIDEO_PORT="video-port";//播放流媒体端口

	
	public static final String CHANGE_BACK_PORT="back_port";
	public static final String SERVER_PART = "AndroidService/";
	public static final String TIMING_SERVER= "AndroidService/Getcurtime.aspx";
	
	public static final String PLAY_VCR_IP = "play_vcr_ip";
	public static final String PLAY_VCR_PORT = "play_vcr_port";
	public static final String PLAY_VCR_CHANNEL = "play_vcr_channel";
	public static final String PLAY_VCR_USER_NAME = "play_vcr_username";
	public static final String PLAY_VCR_PASSWORD = "play_vcr_password";

	public static final String PLAY_VIDEO_IP = "play_video_ip";
	public static final String PLAY_VIDEO_PORT = "play_video_port";
	public static final String PLAY_VIDEO_FILE_NAME = "play_video_filename";
	public static final String PLAY_VIDEO_BEGIN_TIME = "play_video_begin_time";
	public static final String PLAY_VIDEO_LOCAL = "play_video_local";
	public static final String PLAY_VIDEO_LOOP = "play_video_loop";
	
	
	public static final String SERVER_IP_PORT = "192.168.1.253:6700";
	public static final String TERMINAL_IP = "play_video_local";

	public static final String PLAY_VIDEO_PLAN_IP = "play_video_plan_ip";
	public static final String PLAY_VIDEO_PLAN_BEGIN_TIME = "play_video_plan_begin_time";
	public static final String PLAY_VIDEO_PLAN_FILE_NAME = "play_video_plan_filename";
	public static final String PLAY_VIDEO_PLAN_PORT = "play_video_plan_port";
	
	public static final String PLAY_LOCAL_VIDEO_FILE_NAME = "play_local_video__filename";

	public static final String PLAY_AUDIO_IP = "play_audio_ip";
	public static final String PLAY_AUDIO_FILE_NAME = "play_file_name";
	public static final String PLAY_AUDIO_START_TIME = "play_audio_starttime";
	public static final String PLAY_AUDIO_END_TIME = "play_audio_endtime";
	public static final String STOP_AUDIO = "stop_audio";

	public static final String DAILY_LIFE_FOLDER = "daily_life_folder";
	public static final String DAILY_LIFE_FILE_NAME = "dailylife";
	
	public static final String TIMING_PLAN_FOLDER_NAME = "timing_plan_folder";
	public static final String TIMING_PLAN_FILE_NAME = "timingplan";
	
	public static final String SHUT_PLAN_FOLDER_NAME="shut_plan_folder_name";
	public static final String SHUT_PLAN_FILE_NAME="shut_plan_file_name";
	
	public static final String FULLSUBTITLE_PLAN_FOLDER_NAME="fullSubTitle_plan_folder_name";
	public static final String FULLSUBTITLE_PLAN_FILE_NAME="fullSubTitle_plan_file_name";
	
	public static final String TIMING_LIVING_FOLDER = "timing_living_folder";
	public static final String TIMING_LIVING_FILE_NAME = "timingliving";

	public static final String EMERG_NOTE_SERVER_IP = "emergy_note_ip";
	public static final String EMERG_NOTE_PIC_NAME = "emergy_note_pic_name";
	public static final String EMERG_NOTE_CONTENT = "emergy_note_content";
	public static final String EMERG_NOTE_SHOW_TIME = "emergy_note_show_time";
	public static final String EMERG_NOTE_PORT = "emergy_note_port";

	public static final String CHECK_NOTE_SERVER_IP = "check_note_ip";
	public static final String CHECK_NOTE_PIC_NAME = "check_note_pic_name";
	public static final String CHECK_NOTE_CONTEENT = "check_note_content";
	public static final String CHECK_NOTE_SHOW_TIME = "check_note_show_time";
	public static final String CHECK_NOTE_PORT = "check_note_port";
	
	public static final String TIMING_PLAN_SERVER_IP = "timing_plan_server_ip";
	public static final String TIMING_PLAN_PORT = "timing_plan_port";
	public static final String TIMING_PLAN_PLAYTIME = "timing_plan_playtime";
	public static final String TIMING_PLAN_FILENAME = "timing_plan_filename";
	public static final String TIMING_PLAN_ENDTIME = "timing_plan_endtime";
	public static final String TIMING_PLAN_LOCAL = "timing_plan_local";
	
	public static final String CONFIGURE_INFO_SERVERIP = "192.168.1.253";
	public static final String CONFIGURE_INFO_PORT = "6700";
	public static final String CONFIGURE_INFO_CELLNUMBER = "0101";
	public static final String CONFIGURE_INFO_DURATION = "5";
	
	public static final String MESSAGE_TYPE_ACTION = "com.example.iimp_znxj_new2014.message.type";
	
	public static final String MESSAGE_SETREALTIMECONTENT="com.example.iimp_znxj_new2014.setRealTimeContent";
	
	
	public static final String DUTYTIME_TIMEARRAY = "dutytime_timearray";
	
	public static final String SUB_TITLE = "sub_title";
	public static final String SUB_TIME = "1";
	public static final String SERVER_IP = "server_ip";
	public static final String SERVER_PORT = "server_port";
	public static final String ORDER_ACTION = "action:";
	public static final String ORDER_RESULT_OK = ",result:success";
	
	public static final String ORDER_TYPE=",type:";
	public static final String ORDER_FILEPATH=",filepath|";
	public static final String ORDER_STATE=",state:";
	public static final String ORDER_PROGREEBAR=",progressbar:";
	public static final String ORDER_CHANNEL=",channel:";
	
	
	
	public static final String ORDER_RESULT_FAILER = ",result:failer";
	public static final String ORDER_RESULT_EXTRA = "order_result";
	public static final String ORDER_RESULT_VERSION = ",version:v1.0.7.0";    //升级测试apk
	
	public static final String ORDER_PLAYVIDEO_OK = "playVideo:true";
	public static final String ORDER_PLAYVIDEO_FAILER = "playVideo:false";
	
	
	public static final String DOWNLOAD_FLAG = "null";
	
	public static final String VOLUME_VALUE = "up";
	
	public static final String VOLUME_NUM_VALUE = "10";
	public static final String VOLUME_NUM_VALUE_MEDIA = "13";
	
	public static final String PERCENT_VALUE = "50";
	public static final String MESSEGE_COMEFROM_ALARM = "true";
	
	public static final String ORDER_RESULT_ACTION = "com.example.iimp_znxj_new2014.order.result";

	public static final String PLAY_COMPLETE = "com.example.iimp_znxj_new2014.play_complete";

	public static final String ADJUST_SERVER_URL = "http://192.168.1.253:89/GetCurTime.aspx";
	public static final String ADJUST_SERVER_FLAG = "null"; 

	public static final String PLAY_PLAN_FOLDER_NAME = "play_plan_folder";

	public static final String PLAY_PLAN_FILE_NAME = "playplan";

	public static final String OFF_ON_TIME_ON_TIME = "off_on_time_ontime";

	public static final String OFF_ON_TIME_OFF_TIME = "off_on_time_offtime";
	
	public static final String STOP_PLAY = "stop_play";
	public static final String STOP_PLAY_SUB_TITLE = "stop_play_sub_title";
	
	public static final String PAUSE_ACTION = "com.example.iimp_znxj_new2014.pause";
	public static final String CONTINUE_PLAY_ACTION = "com.example.iimp_znxj_new2014.continueplay";
	public static final String SUBTITLE_ACTION = "com.example.iimp_znxj_new2014.subtitle";
	public static final String STOP_SUBTITLE_ACTION = "com.example.iimp_znxj_new2014.stopsubtitle";
	public static final String STOPPLAY_ACTION = "com.example.iimp_znxj_new2014.stopPlay";
	public static final String STOPPLAY_VCR_ACTION = "com.example.iimp_znxj_new2014.stopPlay_vcr";
	public static final String ANOTHERPLAY_ACTION = "com.example.iimp_znxj_new2014.anotherVideoPlay";
	public static final String ANOTHEVCRRPLAY_ACTION = "com.example.iimp_znxj_new2014.anotherVCRPlay";
	public static final String ANOTHELOCALVIDEO_ACTION = "com.example.iimp_znxj_new2014.anotherlocalPlay";
	public static final String CHECKONLINE_ACTION="com.example.iimp_znxj_new2014.checkOnline";
	
	public static final String VIDEO_SHOW_ACTIVITY_NAME = "com.example.iimp_znxj_new2014.activity.VideoShowActivity";
	public static final String CHECK_NOTE_ACTIVITY_NAME = "com.example.iimp_znxj_new2014.activity.CheckNoteActivity";
	public static final String LOCAL_VIDEO_ACTIVITY_NAME = "com.example.iimp_znxj_new2014.activity.PlayLocalVideoActivity";
	public static final String LOCAL_VIDEO_ACTIVITY_PLANNAME = "com.example.iimp_znxj_new2014.activity.PlayPlanLocalVideoActivity";
	public static final String LOCAL_VIDEO_ACTIVITY_BNAME = "com.example.iimp_znxj_new2014.activity.VideoViewPlayingActivity";
	public static final String VCR_SHOW_ACTIVITY_NAME = "com.example.iimp_znxj_new2014.activity.VCRActivity";
	public static final String INDEX_ACTIVITY_NAME = "com.example.iimp_znxj_new2014.activity.IndexActivity";
	public static final String AUDIO_ACTIVITY_NAME = "com.example.iimp_znxj_new2014.activity.AudioActivity";
	public static final String EMERGY_NOTE_ACTIVITY_NAME = "com.example.iimp_znxj_new2014.activity.EmergyNoteActivity"; 
	public static final String SELFCONSUME_BUYACTIVITY_NAME = "com.example.iimp_znxj_new2014.selfconsume.BuyActivity";
	public static final String TVACTIVITY_NAME="com.example.iimp_znxj_new2014.activity.TVActivity";
	
	public static final String THREE_GD_ACTIVITY_NAME = "com.example.iimp_znxj_new2014.activity.ThreeGdActivity"; 
	public static final String LIFE_FOODMENU_ACTIVITY_NAME = "com.example.iimp_znxj_new2014.activity.LifeActivity"; 
	public static final String ROLLCALL_ACTIVITY_NAME = "com.example.iimp_znxj_new2014.activity.RollCallActivity"; 
	
	
	public static final String PRE_PLAY_VIDEO_TIME = "pre_play_video_time";
	public static final String PRE_PLAY_VCR_TIME = "pre_play_vcr_time";
	public static final String MODIFY_CLIENT_IP = "modify_client_ip";
	public static final String MODIFY_CLIENT_SUBNETMASK = "modify_client_subnet_mask";
	public static final String PACKAGE_NAME = "com.example.iimp_znxj_new2014";
	
	public static final String SLIDE_IP = "slide_picture_ip";
	public static final String SLIDE_PORT = "slide_picture_port";
	public static final String SLIDE_SHOWTIME = "slide_picture_showtime";
	public static final String SLIDE_NAME = "slide_picture_picname";
	public static final String SLIDE_INTERVAL = "slide_picture_interval";
	
	public static boolean isManualStop = false;
	
	public static final String  ROLL_CALL_TYPE = "0";
	
	public static final String SCROLL_URL = "http://192.168.1.253:89/1.txt";
	public static int SCROLL_SPEED = 30;
	public static final String SCROLL_SHWOTIME = "10";
	
	public static final String SERIAL_ACTION_KEYCODE="serial_action_key"; //用来传递串口KeyCode的广播的Action
	public static final String SERIAL_ACTION_CARDNUM="serial_action_cardNum";//用来传递串口CardNum的广播的Action
	
	public static final String ERROR_MSG = "error";
	public static final String NORMAL_MSG = "normal";
	
	// 额外类,图片幻灯片效果参数
	public static class Extra {
		public static final String IMAGES = "com.nostra13.example.universalimageloader.IMAGES";
		public static final String IMAGE_POSITION = "com.nostra13.example.universalimageloader.IMAGE_POSITION";
	}
	
	public static final String SHAREDPREFERENCE_VCR_TYPE="VCR_type";//默认播放模式，1-海康，2-科达
	
	public static final String AUTO_GET_ROOMID = "AndroidService/getRoomIDAndName.aspx";//根据请求者的IP获取其对应的监室号和监室名称
	public static String SERVER_URL = "http://192.168.1.253:6700/";
	public static boolean isLiveTime=false;


	public static final String TYPE_MEDIA_PLAY = "media_play";//不同类型的控制板数据
	public static final String TYPE_MEDIA_CONTROL = "media_control";
	public static final String TYPE_LOGIN = "login";
}
