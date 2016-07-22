package com.kedacom.platform2mc.struct;

import java.io.Serializable;

public class EventInfo implements Serializable {
    public int getM_emWork() {
		return m_emWork;
	}
	public void setM_emWork(int m_emWork) {
		this.m_emWork = m_emWork;
	}
	public int getM_dwErrorCode() {
		return m_dwErrorCode;
	}
	public void setM_dwErrorCode(int m_dwErrorCode) {
		this.m_dwErrorCode = m_dwErrorCode;
	}
	public int getM_dwWorkID() {
		return m_dwWorkID;
	}
	public void setM_dwWorkID(int m_dwWorkID) {
		this.m_dwWorkID = m_dwWorkID;
	}
	public int getM_dwReserve1() {
		return m_dwReserve1;
	}
	public void setM_dwReserve1(int m_dwReserve1) {
		this.m_dwReserve1 = m_dwReserve1;
	}
	public int getM_dwReserve2() {
		return m_dwReserve2;
	}
	public void setM_dwReserve2(int m_dwReserve2) {
		this.m_dwReserve2 = m_dwReserve2;
	}
	public int m_emWork; // work
    public int m_dwErrorCode; // error code
    public int m_dwWorkID; // work id: playid/taskid...
    public int m_dwReserve1;
    public int m_dwReserve2;
}
