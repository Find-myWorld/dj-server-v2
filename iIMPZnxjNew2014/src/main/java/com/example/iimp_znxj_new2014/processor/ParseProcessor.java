package com.example.iimp_znxj_new2014.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.iimp_znxj_new2014.DianJiaoApplication;
import com.example.iimp_znxj_new2014.entity.AdjustDate;
import com.example.iimp_znxj_new2014.entity.CheckNote;
import com.example.iimp_znxj_new2014.entity.ConfigureInfo;
import com.example.iimp_znxj_new2014.entity.ContinuePlay;
import com.example.iimp_znxj_new2014.entity.DailyLife;
import com.example.iimp_znxj_new2014.entity.DailyLifeList;
import com.example.iimp_znxj_new2014.entity.Delete;
import com.example.iimp_znxj_new2014.entity.DeleteFile;
import com.example.iimp_znxj_new2014.entity.DownLoadPlan;
import com.example.iimp_znxj_new2014.entity.DownLoadPlanList;
import com.example.iimp_znxj_new2014.entity.DownloadFile;
import com.example.iimp_znxj_new2014.entity.DutyTime;
import com.example.iimp_znxj_new2014.entity.EmergNote;
import com.example.iimp_znxj_new2014.entity.FullSubTitlePlan;
import com.example.iimp_znxj_new2014.entity.GetCurrentState;
import com.example.iimp_znxj_new2014.entity.GetFolderList;
import com.example.iimp_znxj_new2014.entity.GetProgressBar;
import com.example.iimp_znxj_new2014.entity.GetRealTimeContent;
import com.example.iimp_znxj_new2014.entity.ImageSlide;
import com.example.iimp_znxj_new2014.entity.JumpPercentage;
import com.example.iimp_znxj_new2014.entity.ModifyClientIp;
import com.example.iimp_znxj_new2014.entity.OnDuty;
import com.example.iimp_znxj_new2014.entity.OnLine;
import com.example.iimp_znxj_new2014.entity.OnOff;
import com.example.iimp_znxj_new2014.entity.OnOffPlanList;
import com.example.iimp_znxj_new2014.entity.Pause;
import com.example.iimp_znxj_new2014.entity.PlayLocal;
import com.example.iimp_znxj_new2014.entity.PlayPlan;
import com.example.iimp_znxj_new2014.entity.PlayPlanList;
import com.example.iimp_znxj_new2014.entity.PlaySubTitle;
import com.example.iimp_znxj_new2014.entity.PlayVideo;
import com.example.iimp_znxj_new2014.entity.Reboot;
import com.example.iimp_znxj_new2014.entity.RollCall;
import com.example.iimp_znxj_new2014.entity.RollSubTitle;
import com.example.iimp_znxj_new2014.entity.SaveScreen;
import com.example.iimp_znxj_new2014.entity.SearchDevices;
import com.example.iimp_znxj_new2014.entity.ServerIpChanged;
import com.example.iimp_znxj_new2014.entity.ServerIpPort;
import com.example.iimp_znxj_new2014.entity.SetEmpty;
import com.example.iimp_znxj_new2014.entity.SetVcrType;
import com.example.iimp_znxj_new2014.entity.StopPlay;
import com.example.iimp_znxj_new2014.entity.StopPlaySubTitle;
import com.example.iimp_znxj_new2014.entity.TVShow;
import com.example.iimp_znxj_new2014.entity.TimingLiving;
import com.example.iimp_znxj_new2014.entity.TimingLivingList;
import com.example.iimp_znxj_new2014.entity.TimingPlan;
import com.example.iimp_znxj_new2014.entity.TimingPlanList;
import com.example.iimp_znxj_new2014.entity.UpdateVersion;
import com.example.iimp_znxj_new2014.entity.Volume;
import com.example.iimp_znxj_new2014.service.UdpService;
import com.example.iimp_znxj_new2014.util.AlarmUtil;
import com.example.iimp_znxj_new2014.util.Constant;
import com.example.iimp_znxj_new2014.util.FileUtils;
import com.example.iimp_znxj_new2014.util.PreferenceUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

public class ParseProcessor {
	private static final String LOG_TAG = ParseProcessor.class.getSimpleName();
	private static ParseProcessor instance;
	private Context mContext;
	protected Map<Class<ExcuteAysncTask>, ExcuteAysncTask> mAsyncTasks;
	private ParseListener mParseListener;
	private static final String TAG = "ParseProcessor";

	private ParseProcessor(Context context) {
		mContext = context;
		mAsyncTasks = new HashMap<Class<ExcuteAysncTask>, ExcuteAysncTask>();
	}

	public static ParseProcessor getInstance(Context context) {
		if (instance == null) {
			instance = new ParseProcessor(context);
		}
		return instance;
	}

	public void registerParseListener(ParseListener listener) {
		mParseListener = listener;
	}

	protected void checkRestAsyncTask(ExcuteAysncTask task) {
		ExcuteAysncTask oldTask = mAsyncTasks.get(ExcuteAysncTask.class);
		if (oldTask != null && oldTask.isCancelled()) {
			oldTask.cancel(false);
		}
		mAsyncTasks.put(ExcuteAysncTask.class, task);
	}

	public void destroy() {
		for (ExcuteAysncTask task : mAsyncTasks.values()) {
			if (!task.isCancelled()) {
				task.cancel(true);
			}
		}
	}

	public void parseData(String data) {
		ExcuteAysncTask task = new ExcuteAysncTask();
		checkRestAsyncTask(task);
		task.execute(data);
	}

	private class ExcuteAysncTask extends AsyncTask<String, Void, Object> {
		@Override
		protected Object doInBackground(String... arg0) {
			Log.d("jiayy", "arg0[0] = " + arg0[0]);
			try {
				JSONObject jsonObject = new JSONObject(arg0[0]);
				String action = jsonObject.getString("action");
				Log.i("TTSS", "action=" + action);
				if ("dailyLife".equals(action) || "playPlan".equals(action)
						|| "downLoadPlan".equals(action)
						|| "timingPlan".equals(action)
						|| "alarmVCR".equals(action)
						|| "onofftimePlan".equals(action)
						|| "fullSubTitlePlan".equals(action)) { // 一次解析多个json数据
					JSONArray jsonArray = jsonObject.getJSONArray("data");
					int iSize = jsonArray.length();
					if ("dailyLife".equals(action)) {
						// 每日都有
						List<DailyLife> dailyLifeList = new ArrayList<DailyLife>();
						DailyLifeList dailyLifeListBean = new DailyLifeList();
						for (int i = 0; i < iSize; i++) {
							DailyLife dailyLife = new DailyLife();
							JSONObject dataObject = jsonArray.getJSONObject(i);
							if (dataObject != null) {
								dailyLife.setServerIp(dataObject
										.getString("serverIp"));
								dailyLife.setBeginTime(dataObject
										.getString("startTime"));
								dailyLife.setEndTime(dataObject
										.getString("endTime"));
								dailyLife.setFileName(dataObject
										.getString("fileName"));
								dailyLife.setPort(dataObject.getString("port"));
							}
							dailyLifeList.add(dailyLife);
						}
						dailyLifeListBean.setDailyLifeList(dailyLifeList);
						FileUtils.saveStringToCache(
								DianJiaoApplication.getInstance(),
								Constant.DAILY_LIFE_FOLDER,
								Constant.DAILY_LIFE_FILE_NAME, arg0[0]);
						return dailyLifeListBean;
					}

					if ("alarmVCR".equals(action)) { // 每日都有 //直播定时计划
						Log.i("TEST", "ParseProcessor");
						List<TimingLiving> timingLivingList = new ArrayList<TimingLiving>();
						TimingLivingList timingLivingListBean = new TimingLivingList();
						for (int i = 0; i < iSize; i++) {
							TimingLiving timingLiving = new TimingLiving();
							JSONObject dataObject = jsonArray.getJSONObject(i);
							if (dataObject != null) {
								timingLiving.setServerIp(dataObject
										.getString("serverIp"));
								timingLiving.setPort(dataObject
										.getString("port"));
								timingLiving.setChannel(dataObject
										.getString("channel"));
								timingLiving.setUserName(dataObject
										.getString("userName"));
								timingLiving.setPassWord(dataObject
										.getString("passWord"));
								timingLiving.setStartTime(dataObject
										.getString("startTime"));
								timingLiving.setEndTime(dataObject
										.getString("endTime"));
								if (dataObject.has("weekDay")) {
									timingLiving.setWeekDay(dataObject
											.getString("weekDay"));
								}
								if (dataObject.has("volume")) {
									PreferenceUtil.putSharePreference(mContext,
											Constant.VCR_VOLUME,
											dataObject.getString("volume"));
									timingLiving.setVolume(dataObject
											.getString("volume"));
								} else {
									timingLiving.setVolume(PreferenceUtil
											.getSharePreference(mContext,
													Constant.VCR_VOLUME, "13"));
								}

							}
							timingLivingList.add(timingLiving);
						}
						timingLivingListBean
								.setTimingLivingList(timingLivingList);
						FileUtils.saveStringToCache(
								DianJiaoApplication.getInstance(),
								Constant.TIMING_LIVING_FOLDER,
								Constant.TIMING_LIVING_FILE_NAME, arg0[0]);
						return timingLivingListBean;
					}

					if ("playPlan".equals(action)) { // 指定一天
						List<PlayPlan> playPlanList = new ArrayList<PlayPlan>();
						PlayPlanList playPlanListBean = new PlayPlanList();
						for (int i = 0; i < iSize; i++) {
							PlayPlan playPlan = new PlayPlan();
							JSONObject dataObject = jsonArray.getJSONObject(i);
							if (dataObject != null) {
								playPlan.setServerIp(dataObject
										.getString("serverIp"));
								playPlan.setPlayTime(dataObject
										.getString("playTime"));
								playPlan.setPlayFileName(dataObject
										.getString("playFileName"));
								playPlan.setPort(dataObject.getString("port"));
							}
							playPlanList.add(playPlan);
						}
						playPlanListBean.setPlayPlanList(playPlanList);
						FileUtils.saveStringToCache(
								DianJiaoApplication.getInstance(),
								Constant.PLAY_PLAN_FOLDER_NAME,
								Constant.PLAY_PLAN_FILE_NAME, arg0[0]);
						return playPlanListBean;
					}
					if ("downLoadPlan".equals(action)) {
						List<DownLoadPlan> downloadPlanList = new ArrayList<DownLoadPlan>();
						DownLoadPlanList downloadPlanListBean = new DownLoadPlanList();
						for (int i = 0; i < iSize; i++) {
							DownLoadPlan downloadPlan = new DownLoadPlan();
							JSONObject dataObject = jsonArray.getJSONObject(i);
							if (dataObject != null) {
								downloadPlan.setServerIp(dataObject
										.getString("serverIp"));
								downloadPlan.setBeginTime(dataObject
										.getString("begintime"));
								downloadPlan.setFileName(dataObject
										.getString("filename"));
								downloadPlan.setPort(dataObject
										.getString("port"));
							}
							downloadPlanList.add(downloadPlan);
						}
						downloadPlanListBean.setDownLoadList(downloadPlanList);
						FileUtils.saveStringToCache(
								DianJiaoApplication.getInstance(),
								Constant.PLAY_PLAN_FOLDER_NAME,
								Constant.PLAY_PLAN_FILE_NAME, arg0[0]);
						return downloadPlanListBean;
					}
					if ("timingPlan".equals(action)) {// 流媒体定时计划
						List<TimingPlan> timingPlanList = new ArrayList<TimingPlan>();
						TimingPlanList timingPlanListBean = new TimingPlanList();
						for (int i = 0; i < iSize; i++) {
							TimingPlan timingPlan = new TimingPlan();
							JSONObject dataObject = jsonArray.getJSONObject(i);

							if (dataObject != null) {
								timingPlan.setServerIp(dataObject
										.getString("serverIp"));
								timingPlan
										.setPort(dataObject.getString("port"));
								timingPlan.setPlayTime(dataObject
										.getString("playTime"));
								timingPlan.setFileName(dataObject
										.getString("fileName"));
								timingPlan.setEndTime(dataObject
										.getString("endTime"));
								timingPlan.setLocal(dataObject
										.getString("local"));
								if (dataObject.has("weekDay")) {
									timingPlan.setWeekDay(dataObject
											.getString("weekDay"));
								}
								if (dataObject.has("loop")) {
									timingPlan.setLoop(dataObject
											.getString("loop"));
								}
								if (dataObject.has("volume")) {
									timingPlan.setVolume(dataObject
											.getString("volume"));
									PreferenceUtil.putSharePreference(mContext,
											Constant.VIDEO_VOLUME,
											dataObject.getString("volume"));
								} else {
									timingPlan
											.setVolume(PreferenceUtil
													.getSharePreference(
															mContext,
															Constant.VIDEO_VOLUME,
															"8"));
								}
							}
							timingPlanList.add(timingPlan);
						}
						timingPlanListBean.setTimingPlanList(timingPlanList);
						// 将定时计划写入文件
						FileUtils.saveStringToCache(
								DianJiaoApplication.getInstance(),
								Constant.TIMING_PLAN_FOLDER_NAME,
								Constant.TIMING_PLAN_FILE_NAME, arg0[0]);
						return timingPlanListBean;
					}
					if ("onofftimePlan".equals(action)) {
						OnOffPlanList onoffPlanList = new OnOffPlanList();
						Log.v("8.1", "parse接收到数据");
						Log.v("8.1", "iSize=" + iSize);
						for (int i = 0; i < iSize; i++) {
							Log.v("8.1", i + "");
							OnOff onOff = new OnOff();
							JSONObject dataObject = jsonArray.getJSONObject(i);
							if (dataObject != null) {
								onOff.setOffTime(dataObject
										.getString("offtime"));
								Log.v("8.1", onOff.getOffTime());
								onOff.setOnTime(dataObject.getString("ontime"));
								Log.v("8.1", onOff.getOnTime());
								onOff.setWeekDay(dataObject
										.getString("weekDay"));
								Log.v("8.1", onOff.getOnTime());
							}
							onoffPlanList.add(onOff);
						}
						FileUtils.saveStringToCache(
								DianJiaoApplication.getInstance(),
								Constant.SHUT_PLAN_FOLDER_NAME,
								Constant.SHUT_PLAN_FILE_NAME, arg0[0]);
						return onoffPlanList;
					}
					if ("fullSubTitlePlan".equals(action)) {
						FullSubTitlePlan fullSubTitlePlanList = new FullSubTitlePlan();
						for (int i = 0; i < iSize; i++) {
							RollSubTitle rollTitle = new RollSubTitle();
							JSONObject dataObject = jsonArray.getJSONObject(i);
							if (dataObject != null) {
								rollTitle.setServerIp(dataObject
										.getString("serverIp"));
								rollTitle.setPort(dataObject.getString("port"));
								rollTitle.setFileName(dataObject
										.getString("fileName"));
								rollTitle.setRollSpeed(dataObject
										.getString("rollSpeed"));
								rollTitle.setShowTime(dataObject
										.getString("showTime"));
								rollTitle.setStartTime(dataObject
										.getString("startTime"));
								rollTitle.setEndTime(dataObject
										.getString("endTime"));
								rollTitle.setWeek(dataObject
										.getString("weekDay"));

							}
							fullSubTitlePlanList.add(rollTitle);
						}
						FileUtils.saveStringToCache(
								DianJiaoApplication.getInstance(),
								Constant.FULLSUBTITLE_PLAN_FOLDER_NAME,
								Constant.FULLSUBTITLE_PLAN_FILE_NAME, arg0[0]);
						return fullSubTitlePlanList;
					}

				} else {
					JSONObject dataObject = jsonObject.getJSONObject("data"); // 每次只需解析一个数据
					if (dataObject != null) {
						if ("configureInfo".equals(action)) {
							ConfigureInfo bgInfo = new ConfigureInfo();
							bgInfo.setServerIp(dataObject.getString("serverIp"));
							bgInfo.setPort(dataObject.getString("port"));
							bgInfo.setCellNumber(dataObject
									.getString("cellNumber"));
							bgInfo.setDuration(dataObject.getString("duration"));
							return bgInfo;
						}
						if ("setEmpty".equals(action)) {
							SetEmpty empty = new SetEmpty();
							// empty.setReboot(dataObject.getString("reboot"));
							return empty;
						}
						if ("rollCall".equals(action)) {
							Log.i("TEST",
									"ParsePro，点名类型："
											+ dataObject.getString("type"));
							RollCall call = new RollCall();
							if (dataObject.has("responsePort")) {
								call.setResponsePort(dataObject
										.getString("responsePort"));
							}
							call.setDowhat(dataObject.getString("dowhat"));
							PreferenceUtil.putSharePreference(mContext,
									Constant.ROLL_CALL_TYPE,
									dataObject.getString("type"));
							return call;
						}
						if ("playVCR".equals(action)) {
							/*
							 * PlayVCR playData = new PlayVCR();
							 * playData.setChannel
							 * (dataObject.getString("channel"));
							 * playData.setServerIp(dataObject
							 * .getString("serverIp"));
							 * playData.setPassWord(dataObject
							 * .getString("passWord"));
							 * playData.setPort(dataObject.getString("port"));
							 * playData.setUserName(dataObject
							 * .getString("userName"));
							 */
							//
							// SharedPreferences
							// sp=DianJiaoApplication.getInstance().getSharedPreferences("TvShow",
							// Context.MODE_PRIVATE);
							// SharedPreferences.Editor editor=sp.edit();
							// editor.putString("ip",
							// dataObject.getString("serverIp"));
							// editor.putString("port",
							// dataObject.getString("port"));
							// editor.putString("channel",
							// dataObject.getString("channel"));
							// editor.putString("user",
							// dataObject.getString("userName"));
							// editor.putString("passWord",
							// dataObject.getString("passWord"));
							// if(dataObject.has("deviceChannel")){
							// editor.putString("deviceChannel",
							// dataObject.getString("deviceChannel"));
							//
							// }
							// editor.commit();
							// TVShow tvShow=new TVShow();
							// tvShow.setIp(dataObject.getString("serverIp"));
							// tvShow.setPort(dataObject.getString("port"));
							// tvShow.setChannel(dataObject.getString("channel"));
							// tvShow.setUser(dataObject.getString("userName"));
							// tvShow.setPassword(dataObject.getString("passWord"));
							// if(dataObject.has("deviceChannel")){
							// tvShow.setDeviceChannel(dataObject.getString("deviceChannel"));
							// }
							// return tvShow;

							// 新增，为了防止停止播放后点击播放过快导致黑屏

							/*
							 * PlayVCR playData = new PlayVCR();
							 * playData.setChannel
							 * (dataObject.getString("channel"));
							 * playData.setServerIp(dataObject
							 * .getString("serverIp"));
							 * playData.setPassWord(dataObject
							 * .getString("passWord"));
							 * playData.setPort(dataObject.getString("port"));
							 * playData.setUserName(dataObject
							 * .getString("userName"));
							 */

							SharedPreferences sp = DianJiaoApplication
									.getInstance().getSharedPreferences(
											"TvShow", Context.MODE_PRIVATE);
							SharedPreferences.Editor editor = sp.edit();
							editor.putString("ip",
									dataObject.getString("serverIp"));
							editor.putString("port",
									dataObject.getString("port"));
							editor.putString("channel",
									dataObject.getString("channel"));
							Log.e("channel", dataObject.getString("channel"));
							editor.putString("user",
									dataObject.getString("userName"));
							editor.putString("passWord",
									dataObject.getString("passWord"));
							if (dataObject.has("deviceChannel")) {
								editor.putString("deviceChannel",
										dataObject.getString("deviceChannel"));

							}
							if (dataObject.has("volume")) {
								editor.putString("volume",
										dataObject.getString("volume"));
							}
							editor.commit();
							TVShow tvShow = new TVShow();
							tvShow.setIp(dataObject.getString("serverIp"));
							tvShow.setPort(dataObject.getString("port"));
							tvShow.setChannel(dataObject.getString("channel"));
							tvShow.setUser(dataObject.getString("userName"));
							tvShow.setPassword(dataObject.getString("passWord"));
							if (dataObject.has("deviceChannel")) {
								tvShow.setDeviceChannel(dataObject
										.getString("deviceChannel"));
							}
							if (dataObject.has("volume")) {
								tvShow.setVolume(dataObject.getString("volume"));
								PreferenceUtil.putSharePreference(mContext,
										Constant.VCR_VOLUME,
										dataObject.getString("volume"));
							}
							{
								tvShow.setVolume(PreferenceUtil
										.getSharePreference(mContext,
												Constant.VCR_VOLUME, "13"));
							}
							return tvShow;
						}
						if ("playVideo".equals(action)) {
							PlayVideo playVideo = new PlayVideo();
							playVideo.setSeverIp(dataObject
									.getString("serverIp"));
							playVideo.setPort(dataObject.getString("port"));
							playVideo.setFileName(dataObject
									.getString("fileName"));
							if (dataObject.has("loop")) {
								playVideo.setLoop(dataObject.getString("loop"));
							}

							if (dataObject.has("curPercent")) {
								playVideo.setCurPercent(dataObject
										.getString("curPercent"));
							}

							if (dataObject.has("volume")) {
								playVideo.setVolume(dataObject
										.getString("volume"));
								PreferenceUtil.putSharePreference(mContext,
										Constant.VIDEO_VOLUME,
										dataObject.getString("volume"));
							} else {
								playVideo.setVolume(PreferenceUtil
										.getSharePreference(mContext,
												Constant.VIDEO_VOLUME, "8"));

							}
							return playVideo;
						}

						if ("playLocalVideo".equals(action)) {
							PlayLocal playLocal = new PlayLocal();
							playLocal.setFileName(dataObject
									.getString("fileName"));
							return playLocal;
						}
						if ("adjustDate".equals(action)) {
							AdjustDate adjustDate = new AdjustDate();
							adjustDate.setAdjustUrl(dataObject
									.getString("serverUrl"));
							return adjustDate;
						}
						if ("searchDevices".equals(action)) {
							SearchDevices searchDevices = new SearchDevices();
							searchDevices.setServerPort("port");
							return searchDevices;
						}
						if ("playSubTitle".equals(action)) {
							PlaySubTitle playSubTitle = new PlaySubTitle();
							playSubTitle.setSubTitleStr(dataObject
									.getString("subTitle"));
							playSubTitle.setShowTime(dataObject
									.getString("showTime"));
							return playSubTitle;
						}
						if ("stopSubTitle".equals(action)) {
							StopPlaySubTitle stopSubTitle = new StopPlaySubTitle();
							stopSubTitle.setStopPlaySubTitle(dataObject
									.getString("stopPlay"));
							return stopSubTitle;
						}
						if ("checknote".equals(action)) {
							CheckNote checkNote = new CheckNote();
							checkNote.setServerIp(dataObject
									.getString("serverIp"));
							checkNote.setPicName(dataObject
									.getString("picName"));
							checkNote.setPort(dataObject.getString("port"));
							checkNote.setShowTime(dataObject
									.getString("showTime"));
							return checkNote;
						}
						if ("emergNote".equals(action)) {
							EmergNote emergNote = new EmergNote();
							emergNote.setServerIp(dataObject
									.getString("serverIp"));
							emergNote.setNoteContent(dataObject
									.getString("noteContent"));
							emergNote.setPort(dataObject.getString("port"));
							emergNote.setPicName(dataObject
									.getString("picName"));
							emergNote.setShowTime(dataObject
									.getString("showTime"));
							return emergNote;
						}

						if ("saveScreen".equals(action)) {
							SaveScreen saveScreen = new SaveScreen();
							saveScreen.setServerIp(dataObject
									.getString("serverIp"));
							return saveScreen;
						}
						if ("pause".equals(action)) {
							Pause pause = new Pause();
							pause.setPause(dataObject.getString("pause"));
							return pause;
						}
						if ("continuePlay".equals(action)) {
							ContinuePlay continuePlay = new ContinuePlay();
							continuePlay.setContinuePlay(dataObject
									.getString("continuePlay"));
							return continuePlay;
						}

						if ("update".equals(action)) {
							UpdateVersion updateVersion = new UpdateVersion();
							updateVersion.setFileName(dataObject
									.getString("appName"));
							updateVersion.setServerIp(dataObject
									.getString("serverIp"));
							updateVersion.setPort(dataObject.getString("port"));
							return updateVersion;
						}
						if ("download".equals(action)) {
							DownloadFile downloadFile = new DownloadFile();
							downloadFile.setFileName(dataObject
									.getString("filename"));// 这里用UTF-8解码了
							downloadFile.setServerIp(dataObject
									.getString("serverIp"));
							downloadFile.setPort(dataObject.getString("port"));
							return downloadFile;
						}
						if ("getfilelist".equals(action)) {
							GetFolderList getfolderlist = new GetFolderList();
							getfolderlist.setGetlist(dataObject
									.getString("getlist"));
							return getfolderlist;
						}
						if ("delfile".equals(action)) {
							DeleteFile delfile = new DeleteFile();
							delfile.setFilename(dataObject
									.getString("filename"));
							return delfile;
						}
						if ("serverIpChange".equals(action)) {
							ServerIpChanged serverIpChanged = new ServerIpChanged();
							serverIpChanged.setServerIp(dataObject
									.getString("serverIp"));
							serverIpChanged.setPort(dataObject
									.getString("port"));
							return serverIpChanged;
						}
						if ("modifyClientIp".equals(action)) {
							ModifyClientIp modifyClientIp = new ModifyClientIp();
							modifyClientIp.setClientIp(dataObject
									.getString("clientIp"));
							modifyClientIp.setSubnetmask(dataObject
									.getString("subnetmask"));
							return modifyClientIp;
						}
						if ("online".equals(action)) {
							OnLine onLine = new OnLine();
							if (dataObject.has("dowhat")) {// 第三方检测程序指令，防止程序挂掉！发送广播通知
								Intent intent = new Intent();
								intent.setAction(Constant.DJAPP_ALIVE);
								mContext.sendBroadcast(intent);
								return null;
							}
							onLine.setPort(dataObject.getString("port"));
							return onLine;
						}
						if ("getRealTimeContent".equals(action)) {
							GetRealTimeContent getRealTimeContent = new GetRealTimeContent();
							getRealTimeContent.setmessage(dataObject
									.getString("message"));
							return getRealTimeContent;
						}
						/*
						 * if ("onofftime".equals(action)) { OnOff onOff = new
						 * OnOff();
						 * onOff.setOnTime(dataObject.getString("ontime"));
						 * onOff.setOffTime(dataObject.getString("offtime"));
						 * return onOff; }
						 */
						if ("stopPlay".equals(action)) {
							StopPlay stopPlay = new StopPlay();
							stopPlay.setStopPlay(dataObject
									.getString("stopPlay"));
							return stopPlay;
						}
						if ("exit".equals(action)) {
							Log.i("TEST", "强行退出的命令");
							// SuUtil.kill(Constant.VIDEO_SHOW_ACTIVITY_NAME);
							android.os.Process.killProcess(android.os.Process
									.myPid()); // 获取PID
							System.exit(0);
						}
						if ("reboot".equals(action)) {
							Reboot reboot = new Reboot();
							reboot.setReboot(dataObject.getString("reboot"));
							return reboot;
						}
						if ("getCurrentState".equals(action)) {
							GetCurrentState currentState = new GetCurrentState();
							currentState.setGetState(dataObject
									.getString("getState"));
							return currentState;
						}
						if ("volume".equals(action)) {
							Volume volume = new Volume();
							volume.setMovement(dataObject.getString("movement"));
							return volume;
						}
						if ("progressBar".equals(action)) {
							GetProgressBar getProgress = new GetProgressBar();
							getProgress.setPercent(dataObject
									.getString("state"));
							return getProgress;
						}

						if ("progressBar".equals(action)) {
							GetProgressBar getProgress = new GetProgressBar();

							return getProgress;
						}
						if ("jumpTo".equals(action)) {
							JumpPercentage getPercent = new JumpPercentage();
							getPercent.setPercent(dataObject
									.getString("jumpTo"));
							return getPercent;
						}
						if ("imageSlide".equals(action)) {
							ImageSlide imageSlide = new ImageSlide();
							imageSlide.setServerIp(dataObject
									.getString("serverIp"));
							imageSlide.setPicName(dataObject
									.getString("picName"));
							imageSlide.setPort(dataObject.getString("port"));
							imageSlide.setShowTime(dataObject
									.getString("showTime"));
							imageSlide.setInterval(dataObject
									.getString("interval"));
							return imageSlide;
						}
						if ("delete".equals(action)) {
							Delete del = new Delete();
							del.setDelete(dataObject.getString("delete"));
							return del;
						}
						if ("fullSubTitle".equals(action)) {
							RollSubTitle rollTitle = new RollSubTitle();
							rollTitle.setServerIp(dataObject
									.getString("serverIp"));
							rollTitle.setPort(dataObject.getString("port"));
							rollTitle.setFileName(dataObject
									.getString("fileName"));
							rollTitle.setRollSpeed(dataObject
									.getString("rollSpeed"));
							rollTitle.setShowTime(dataObject
									.getString("showTime"));

							if (dataObject.has("volume")) {
								rollTitle.setVolume(dataObject
										.getString("volume"));
								PreferenceUtil.putSharePreference(mContext,
										Constant.SCROLL_VOLUME,
										dataObject.getString("volume"));
							} else {
								rollTitle.setVolume(PreferenceUtil
										.getSharePreference(mContext,
												Constant.SCROLL_VOLUME, "8"));
							}

							return rollTitle;
						}
						if ("TVShow".equals(action)) {
							TVShow tvShow = new TVShow();
							tvShow.setIp(dataObject.getString("ip"));
							tvShow.setPort(dataObject.getString("port"));
							tvShow.setChannel(dataObject.getString("channel"));
							tvShow.setUser(dataObject.getString("user"));
							tvShow.setPassword(dataObject.getString("passWord"));

							SharedPreferences sp = DianJiaoApplication
									.getInstance().getSharedPreferences(
											"TvShow", Context.MODE_PRIVATE);
							SharedPreferences.Editor editor = sp.edit();
							editor.putString("ip", dataObject.getString("ip"));
							editor.putString("port",
									dataObject.getString("port"));
							editor.putString("channel",
									dataObject.getString("channel"));
							editor.putString("user",
									dataObject.getString("user"));
							editor.putString("passWord",
									dataObject.getString("passWord"));
							editor.commit();
							return tvShow;
						}
						if ("onduty".equals(action)) {
							Log.i("TTSS", "ParseProcessor收到了");
							OnDuty dTime = new OnDuty();
							dTime.setBeginHour(dataObject
									.getString("beginhour"));
							dTime.setBeginMinute(dataObject
									.getString("beginminute"));
							dTime.setInterval(dataObject.getString("interval"));
							dTime.setCycles(dataObject.getString("cycles"));
							return dTime;
						}
						if ("dutytime".equals(action)) {
							Log.i("TTSS", "ParseProcessor收到了");
							DutyTime dTime = new DutyTime();
							dTime.setTimeArray(dataObject
									.getString("timeArray").trim());
							return dTime;
						}
						if ("setVCRType".equals(action)) {
							SetVcrType vcrType = new SetVcrType();
							vcrType.setType(dataObject.getString("type"));
							PreferenceUtil.putSharePreference(
									(UdpService) mParseListener,
									Constant.SHAREDPREFERENCE_VCR_TYPE,
									dataObject.getString("type"));
							return vcrType;
						}
						if ("writeSDfile".equals(action)) {
							// String serverStr =
							// dataObject.getString("serverIp");//如：192.168.0.253:6700
							ServerIpPort serIpPort = new ServerIpPort();
							serIpPort.setServerIp(dataObject
									.getString("serverIp"));
							return serIpPort;
						}

						else if (action.equals("setDuty")) {
							PreferenceUtil.putSharePreference(
									mContext,
									Constant.DUTY_PALTRL,
									dataObject.getJSONObject("data").getString(
											"DutyPaltrl"));
							if ("2".equals(dataObject.getJSONObject("data")
									.getString("DutyRemind"))) {
								AlarmUtil.clearBroadCastAlarm(mContext);
							}
							PreferenceUtil.putSharePreference(
									mContext,
									Constant.DUTY_REMIND,
									dataObject.getJSONObject("data").getString(
											"DutyRemind"));
						}

					}
				}
			} catch (JSONException e) {
				Log.e(LOG_TAG, "exception is " + e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			Log.v("8.1", "解析完成");
			if (result == null) {
				return;
			}
			if (mParseListener != null) {
				mParseListener.onParseFinished(result);
			}
		}

	}

	public interface ParseListener {
		void onParseFinished(Object data);
	}
}
