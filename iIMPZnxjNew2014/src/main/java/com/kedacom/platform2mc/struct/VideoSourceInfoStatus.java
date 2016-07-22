package com.kedacom.platform2mc.struct;

import java.io.Serializable;

import com.kedacom.platform2mc.ntv.IPhoenixSDKConstantVal;;

public class VideoSourceInfoStatus implements Serializable {
    public DeviceInfo getmVideoSrcInfoSDK() {
		return mVideoSrcInfoSDK;
	}

	public void setmVideoSrcInfoSDK(DeviceInfo mVideoSrcInfoSDK) {
		this.mVideoSrcInfoSDK = mVideoSrcInfoSDK;
	}

	public boolean ismIsRecording() {
		return mIsRecording;
	}

	public void setmIsRecording(boolean mIsRecording) {
		this.mIsRecording = mIsRecording;
	}

	public int getmDBPostion() {
		return mDBPostion;
	}

	public void setmDBPostion(int mDBPostion) {
		this.mDBPostion = mDBPostion;
	}

	public int getPlayId() {
		return playId;
	}

	public void setPlayId(int playId) {
		this.playId = playId;
	}

	public String getDevName() {
		return devName;
	}

	public void setDevName(String devName) {
		this.devName = devName;
	}

	public int getResolution() {
		return resolution;
	}

	public void setResolution(int resolution) {
		this.resolution = resolution;
	}

	public int getmChannel() {
		return mChannel;
	}

	public void setmChannel(int mChannel) {
		this.mChannel = mChannel;
	}

	public String getmDisplayName() {
		return mDisplayName;
	}

	public void setmDisplayName(String mDisplayName) {
		this.mDisplayName = mDisplayName;
	}

	private DeviceInfo mVideoSrcInfoSDK;
    private boolean mIsRecording = false;
    private int mDBPostion = -1;
    public int playId = -1;
    public String devName = "";
    public int resolution = IPhoenixSDKConstantVal.SDK_VIDEO_PLAY_FLUENT;
    public int mChannel = 0;
    public String mDisplayName = "";

    public void setVideoSrcInfoSDK(DeviceInfo vsi) {
        mVideoSrcInfoSDK = vsi;
    	if(vsi == null){
    		devName = "";
    		resolution = IPhoenixSDKConstantVal.SDK_VIDEO_PLAY_FLUENT;
    	}
    }

    public DeviceInfo getVideoSrcInfoSDK() {
        return mVideoSrcInfoSDK;
    }

    public void setRecordingStatus(boolean status) {
        mIsRecording = status;
    }

    public boolean getRecordingStatus() {
        return mIsRecording;
    }

    public void setDBPostion(int position) {
        mDBPostion = position;
    }

    public int getDBPostion() {
        return mDBPostion;
    }
}