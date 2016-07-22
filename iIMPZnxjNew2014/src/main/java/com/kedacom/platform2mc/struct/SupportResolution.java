package com.kedacom.platform2mc.struct;

import java.io.Serializable;

public class SupportResolution implements Serializable {
	
    public byte getM_byVideoFormat() {
		return m_byVideoFormat;
	}
	public void setM_byVideoFormat(byte m_byVideoFormat) {
		this.m_byVideoFormat = m_byVideoFormat;
	}
	public int getM_dwSupportResolution() {
		return m_dwSupportResolution;
	}
	public void setM_dwSupportResolution(int m_dwSupportResolution) {
		this.m_dwSupportResolution = m_dwSupportResolution;
	}
	public byte m_byVideoFormat; // the name of video source
    public int  m_dwSupportResolution; // the sn of video source
}