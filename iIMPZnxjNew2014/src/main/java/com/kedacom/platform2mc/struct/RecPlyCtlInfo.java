package com.kedacom.platform2mc.struct;

import java.io.Serializable;

public class RecPlyCtlInfo implements Serializable {
	public char getM_byCtrlType() {
		return m_byCtrlType;
	}
	public void setM_byCtrlType(char m_byCtrlType) {
		this.m_byCtrlType = m_byCtrlType;
	}
	public long getM_dwRange() {
		return m_dwRange;
	}
	public void setM_dwRange(long m_dwRange) {
		this.m_dwRange = m_dwRange;
	}
	public long getM_dwPlayId() {
		return m_dwPlayId;
	}
	public void setM_dwPlayId(long m_dwPlayId) {
		this.m_dwPlayId = m_dwPlayId;
	}
	public char m_byCtrlType; // the id of group
    public long m_dwRange; // the id of parent group
    public long  m_dwPlayId; // the name of group
}
