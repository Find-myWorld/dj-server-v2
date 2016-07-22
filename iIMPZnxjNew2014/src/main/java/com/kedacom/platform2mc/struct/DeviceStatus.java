package com.kedacom.platform2mc.struct;

import java.io.Serializable;

public class DeviceStatus implements Serializable {
    public int getEmStatusType() {
		return emStatusType;
	}
	public void setEmStatusType(int emStatusType) {
		this.emStatusType = emStatusType;
	}
	public boolean isbOnline() {
		return bOnline;
	}
	public void setbOnline(boolean bOnline) {
		this.bOnline = bOnline;
	}
	public int getDwAlarmType() {
		return dwAlarmType;
	}
	public void setDwAlarmType(int dwAlarmType) {
		this.dwAlarmType = dwAlarmType;
	}
	public int getByAlarmStatus() {
		return byAlarmStatus;
	}
	public void setByAlarmStatus(int byAlarmStatus) {
		this.byAlarmStatus = byAlarmStatus;
	}
	public int getDwTime() {
		return dwTime;
	}
	public void setDwTime(int dwTime) {
		this.dwTime = dwTime;
	}
	public int emStatusType; // 0, check online     1, check alarm
    public boolean bOnline; // 
    public int dwAlarmType; // 
    public int byAlarmStatus; // 
    public int dwTime; // 
}