package com.kedacom.platform2mc.struct;

import java.io.Serializable;

public class PtzCmd implements Serializable {
	public int getM_emPtzCmd() {
		return m_emPtzCmd;
	}
	public void setM_emPtzCmd(int m_emPtzCmd) {
		this.m_emPtzCmd = m_emPtzCmd;
	}
	public byte getM_byLevel() {
		return m_byLevel;
	}
	public void setM_byLevel(byte m_byLevel) {
		this.m_byLevel = m_byLevel;
	}
	public int getM_dwHoldTimer() {
		return m_dwHoldTimer;
	}
	public void setM_dwHoldTimer(int m_dwHoldTimer) {
		this.m_dwHoldTimer = m_dwHoldTimer;
	}
	public short getM_wPtzRange() {
		return m_wPtzRange;
	}
	public void setM_wPtzRange(short m_wPtzRange) {
		this.m_wPtzRange = m_wPtzRange;
	}
	public int m_emPtzCmd; 
    public byte m_byLevel; 
    public int m_dwHoldTimer;
    public short m_wPtzRange; 
}
