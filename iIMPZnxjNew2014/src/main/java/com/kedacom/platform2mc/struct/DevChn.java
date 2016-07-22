package com.kedacom.platform2mc.struct;

import java.io.Serializable;

public class DevChn implements Serializable {
	public String getDomainID() {
		return domainID;
	}
	public void setDomainID(String domainID) {
		this.domainID = domainID;
	}
	public String getDeviceID() {
		return deviceID;
	}
	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}
	public short getnChn() {
		return nChn;
	}
	public void setnChn(short nChn) {
		this.nChn = nChn;
	}
	public short getnSrc() {
		return nSrc;
	}
	public void setnSrc(short nSrc) {
		this.nSrc = nSrc;
	}
	public String domainID; // the id of group
    public String deviceID; // the id of parent group
    public short nChn; // the name of group
    public short nSrc; // has device or not
}
