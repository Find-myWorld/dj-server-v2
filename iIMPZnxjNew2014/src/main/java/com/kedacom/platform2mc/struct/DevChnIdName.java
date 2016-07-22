package com.kedacom.platform2mc.struct;

import java.io.Serializable;

public class DevChnIdName implements Serializable {
	

	public int getM_devChnId() {
		return m_devChnId;
	}
	public void setM_devChnId(int m_devChnId) {
		this.m_devChnId = m_devChnId;
	}
	public String getM_dev_ChnName() {
		return m_devChnName;
	}
	public void setM_dev_ChnName(String m_devChnName) {
		this.m_devChnName = m_devChnName;
	}
	public int m_devChnId; // the name of video source
	public String  m_devChnName;
}