package com.kedacom.platform2mc.struct;

import java.io.Serializable;

public class ChnTranData implements Serializable {
	public char getM_byAppType() {
		return m_byAppType;
	}
	public void setM_byAppType(char m_byAppType) {
		this.m_byAppType = m_byAppType;
	}
	public short getM_wTransBufferLen() {
		return m_wTransBufferLen;
	}
	public void setM_wTransBufferLen(short m_wTransBufferLen) {
		this.m_wTransBufferLen = m_wTransBufferLen;
	}
	public byte[] getM_byTransBuffer() {
		return m_byTransBuffer;
	}
	public void setM_byTransBuffer(byte[] m_byTransBuffer) {
		this.m_byTransBuffer = m_byTransBuffer;
	}
	public char m_byAppType; // 
    public short m_wTransBufferLen; // 
    public byte m_byTransBuffer[];
}
