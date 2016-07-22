package com.kedacom.platform2mc.struct;

import java.io.Serializable;

public class VidOSD implements Serializable {
	
    public String getM_abyContent() {
		return m_abyContent;
	}
	public void setM_abyContent(String m_abyContent) {
		this.m_abyContent = m_abyContent;
	}
	public short getM_wLeftMargin() {
		return m_wLeftMargin;
	}
	public void setM_wLeftMargin(short m_wLeftMargin) {
		this.m_wLeftMargin = m_wLeftMargin;
	}
	public short getM_wTopMargin() {
		return m_wTopMargin;
	}
	public void setM_wTopMargin(short m_wTopMargin) {
		this.m_wTopMargin = m_wTopMargin;
	}
	public String m_abyContent; // the name of video source
    public short m_wLeftMargin; // the sn of video source
    public short m_wTopMargin; // the control level of PTZ
}