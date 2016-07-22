package com.kedacom.platform2mc.struct;

import java.io.Serializable;

public class DeviceGroupInfo implements Serializable {
    public String getGroupID() {
		return groupID;
	}
	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}
	public String getParentID() {
		return parentID;
	}
	public void setParentID(String parentID) {
		this.parentID = parentID;
	}
	public String getSzGroupName() {
		return szGroupName;
	}
	public void setSzGroupName(String szGroupName) {
		this.szGroupName = szGroupName;
	}
	public boolean isbHasDevice() {
		return bHasDevice;
	}
	public void setbHasDevice(boolean bHasDevice) {
		this.bHasDevice = bHasDevice;
	}
	public String groupID; // the id of group
    public String parentID; // the id of parent group
    public String szGroupName; // the name of group
    public boolean bHasDevice; // has device or not
}