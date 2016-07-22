package com.kedacom.platform2mc.struct;

import java.io.Serializable;

public class StreamUrlList implements Serializable {

 	public int m_byUrlNum;
    public StreamUrlInfo m_StreamUrl[];
	public int getM_byUrlNum() {
		return m_byUrlNum;
	}
	public void setM_byUrlNum(int m_byUrlNum) {
		this.m_byUrlNum = m_byUrlNum;
	}
	public StreamUrlInfo[] getM_StreamUrl() {
		return m_StreamUrl;
	}
	public void setM_StreamUrl(StreamUrlInfo[] m_StreamUrl) {
		this.m_StreamUrl = m_StreamUrl;
	}

}