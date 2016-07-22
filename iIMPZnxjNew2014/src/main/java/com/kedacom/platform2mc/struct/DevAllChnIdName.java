package com.kedacom.platform2mc.struct;

import java.io.Serializable;

public class DevAllChnIdName implements Serializable {
	
	public int getM_DevChnTotal() {
		return m_DevChnTotal;
	}
	public void setM_DevChnTotal(int m_DevChnTotal) {
		this.m_DevChnTotal = m_DevChnTotal;
	}
	public DevChnIdName[] getM_DevChnIdName() {
		return m_DevChnIdName;
	}
	public void setM_DevChnIdName(DevChnIdName[] m_DevChnIdName) {
		this.m_DevChnIdName = m_DevChnIdName;
	}
	public int m_DevChnTotal; // the name of video source
    public DevChnIdName m_DevChnIdName[]; // the control level of PTZ
}