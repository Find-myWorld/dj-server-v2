package com.kedacom.platform2mc.struct;

import java.io.Serializable;

public class TRecordInfo implements Serializable {

    public String getM_dwRecID() {
		return m_dwRecID;
	}
	public void setM_dwRecID(String m_dwRecID) {
		this.m_dwRecID = m_dwRecID;
	}
	public int getM_eRecType() {
		return m_eRecType;
	}
	public void setM_eRecType(int m_eRecType) {
		this.m_eRecType = m_eRecType;
	}
	public TPeriod getM_RecPeriod() {
		return m_RecPeriod;
	}
	public void setM_RecPeriod(TPeriod m_RecPeriod) {
		this.m_RecPeriod = m_RecPeriod;
	}
	public DevChn getM_DevChn() {
		return m_DevChn;
	}
	public void setM_DevChn(DevChn m_DevChn) {
		this.m_DevChn = m_DevChn;
	}
	public String m_dwRecID; // 
    public int m_eRecType;
    public TPeriod m_RecPeriod; // 
    public DevChn m_DevChn; //
    
}
