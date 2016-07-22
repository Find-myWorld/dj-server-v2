package com.kedacom.platform2mc.struct;

import java.io.Serializable;

public class DeviceInfo implements Serializable {
    public int getnSn() {
		return nSn;
	}
	public void setnSn(int nSn) {
		this.nSn = nSn;
	}
	public String getDeviceID() {
		return deviceID;
	}
	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}
	public String getDomainID() {
		return domainID;
	}
	public void setDomainID(String domainID) {
		this.domainID = domainID;
	}
	public int getnDevSrcNum() {
		return nDevSrcNum;
	}
	public void setnDevSrcNum(int nDevSrcNum) {
		this.nDevSrcNum = nDevSrcNum;
	}
	public int getnEncoderChnNum() {
		return nEncoderChnNum;
	}
	public void setnEncoderChnNum(int nEncoderChnNum) {
		this.nEncoderChnNum = nEncoderChnNum;
	}
	public String getSzDevSrcAlias() {
		return szDevSrcAlias;
	}
	public void setSzDevSrcAlias(String szDevSrcAlias) {
		this.szDevSrcAlias = szDevSrcAlias;
	}
	public int getnDevType() {
		return nDevType;
	}
	public void setnDevType(int nDevType) {
		this.nDevType = nDevType;
	}
	public int getnDevCap() {
		return nDevCap;
	}
	public void setnDevCap(int nDevCap) {
		this.nDevCap = nDevCap;
	}
	public int getnCallType() {
		return nCallType;
	}
	public void setnCallType(int nCallType) {
		this.nCallType = nCallType;
	}
	public VideoSrcInfoSDK[] getaDevSrcChn() {
		return aDevSrcChn;
	}
	public void setaDevSrcChn(VideoSrcInfoSDK[] aDevSrcChn) {
		this.aDevSrcChn = aDevSrcChn;
	}
	public String getSzManufacturer() {
		return szManufacturer;
	}
	public void setSzManufacturer(String szManufacturer) {
		this.szManufacturer = szManufacturer;
	}
	public int nSn; // the sn of device
    public String deviceID; // the id of device
    public String domainID; // the id of domainID
    public int nDevSrcNum; // the count of video source
    public int nEncoderChnNum; // the count of encoder
    public String szDevSrcAlias; // the alias of device
    public int nDevType; // the type of device
    public int nDevCap;
    public int nCallType; // the type of call
    public VideoSrcInfoSDK aDevSrcChn[]; // the info of video source, max is 64
    public String szManufacturer; // the manufacturer
}
