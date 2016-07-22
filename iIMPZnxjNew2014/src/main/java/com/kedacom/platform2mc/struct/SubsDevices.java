package com.kedacom.platform2mc.struct;

import java.io.Serializable;

public class SubsDevices implements Serializable {
	public byte getBySubsDevNum() {
		return bySubsDevNum;
	}
	public void setBySubsDevNum(byte bySubsDevNum) {
		this.bySubsDevNum = bySubsDevNum;
	}
	public DeviceID[] getVctDevID() {
		return vctDevID;
	}
	public void setVctDevID(DeviceID[] vctDevID) {
		this.vctDevID = vctDevID;
	}
	public byte bySubsDevNum;
	public DeviceID vctDevID[] = new DeviceID[20];
	//public DeviceID vctDevID[];
}
