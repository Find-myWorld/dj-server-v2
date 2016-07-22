package com.kedacom.platform2mc.struct;

import java.io.Serializable;

public class VideoSrcInfoSDK implements Serializable {
    public int getnSn() {
		return nSn;
	}
	public void setnSn(int nSn) {
		this.nSn = nSn;
	}
	public String getSzSrcName() {
		return szSrcName;
	}
	public void setSzSrcName(String szSrcName) {
		this.szSrcName = szSrcName;
	}
	public int getnPtzLevel() {
		return nPtzLevel;
	}
	public void setnPtzLevel(int nPtzLevel) {
		this.nPtzLevel = nPtzLevel;
	}
	public int getbFitCondition() {
		return bFitCondition;
	}
	public void setbFitCondition(int bFitCondition) {
		this.bFitCondition = bFitCondition;
	}
	public int getnVSType() {
		return nVSType;
	}
	public void setnVSType(int nVSType) {
		this.nVSType = nVSType;
	}
	public int getnStatus() {
		return nStatus;
	}
	public void setnStatus(int nStatus) {
		this.nStatus = nStatus;
	}
	public boolean isRecording() {
		return isRecording;
	}
	public void setRecording(boolean isRecording) {
		this.isRecording = isRecording;
	}
	public int nSn; // the sn of video source
    public String szSrcName; // the name of video source
    public int nPtzLevel; // the control level of PTZ
    public int bFitCondition;

    public int nVSType; //0 gun, 1 sphere
    public int nStatus; // fake status
    public boolean isRecording;

}