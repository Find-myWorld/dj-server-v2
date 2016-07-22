package com.kedacom.platform2mc.struct;

import java.io.Serializable;

public class SearchDvcRspInfo implements Serializable {
	
	public short getM_DvcInfoType() {
		return m_DvcInfoType;
	}
	public void setM_DvcInfoType(short m_DvcInfoType) {
		this.m_DvcInfoType = m_DvcInfoType;
	}
	public String getM_ParentID() {
		return m_ParentID;
	}
	public void setM_ParentID(String m_ParentID) {
		this.m_ParentID = m_ParentID;
	}
	public String getM_DevDirectory() {
		return m_DevDirectory;
	}
	public void setM_DevDirectory(String m_DevDirectory) {
		this.m_DevDirectory = m_DevDirectory;
	}
	public DeviceInfo getM_DevInfo() {
		return m_DevInfo;
	}
	public void setM_DevInfo(DeviceInfo m_DevInfo) {
		this.m_DevInfo = m_DevInfo;
	}
	public DeviceGroupInfo getM_GroupInfo() {
		return m_GroupInfo;
	}
	public void setM_GroupInfo(DeviceGroupInfo m_GroupInfo) {
		this.m_GroupInfo = m_GroupInfo;
	}
	public short  					m_DvcInfoType;//1: group 2:device
    public String  					m_ParentID;
    public String 					m_DevDirectory;
    
    public DeviceInfo				m_DevInfo;
    public DeviceGroupInfo 			m_GroupInfo ;
}
