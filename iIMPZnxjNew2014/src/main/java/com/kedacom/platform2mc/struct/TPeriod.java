package com.kedacom.platform2mc.struct;

import java.io.Serializable;

public class TPeriod implements Serializable {

    public int getM_dwStartTime() {
		return m_dwStartTime;
	}
	public void setM_dwStartTime(int m_dwStartTime) {
		this.m_dwStartTime = m_dwStartTime;
	}
	public int getM_dwEndTime() {
		return m_dwEndTime;
	}
	public void setM_dwEndTime(int m_dwEndTime) {
		this.m_dwEndTime = m_dwEndTime;
	}
	public int m_dwStartTime; // start time
    public int m_dwEndTime; // end time
}
