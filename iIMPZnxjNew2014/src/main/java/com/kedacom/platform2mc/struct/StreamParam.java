package com.kedacom.platform2mc.struct;

import java.io.Serializable;

public class StreamParam implements Serializable {
	public String getM_szServerIp() {
		return m_szServerIp;
	}
	public void setM_szServerIp(String m_szServerIp) {
		this.m_szServerIp = m_szServerIp;
	}
	public short getM_wServerPort() {
		return m_wServerPort;
	}
	public void setM_wServerPort(short m_wServerPort) {
		this.m_wServerPort = m_wServerPort;
	}
	public short getM_wScreenWidth() {
		return m_wScreenWidth;
	}
	public void setM_wScreenWidth(short m_wScreenWidth) {
		this.m_wScreenWidth = m_wScreenWidth;
	}
	public short getM_wScreenHeight() {
		return m_wScreenHeight;
	}
	public void setM_wScreenHeight(short m_wScreenHeight) {
		this.m_wScreenHeight = m_wScreenHeight;
	}
	public String getM_szManufactor() {
		return m_szManufactor;
	}
	public void setM_szManufactor(String m_szManufactor) {
		this.m_szManufactor = m_szManufactor;
	}
	public String m_szServerIp;
	public short m_wServerPort; 
    public short m_wScreenWidth; 
    public short m_wScreenHeight; 
    public String m_szManufactor; 

}
