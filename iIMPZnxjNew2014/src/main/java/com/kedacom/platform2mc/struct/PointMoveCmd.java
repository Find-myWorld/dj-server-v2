package com.kedacom.platform2mc.struct;

import java.io.Serializable;

public class PointMoveCmd implements Serializable {
	public short getM_wPosX() {
		return m_wPosX;
	}
	public void setM_wPosX(short m_wPosX) {
		this.m_wPosX = m_wPosX;
	}
	public short getM_wPosY() {
		return m_wPosY;
	}
	public void setM_wPosY(short m_wPosY) {
		this.m_wPosY = m_wPosY;
	}
	public short getM_wScreenWidth() {
		return m_wScreenWidth;
	}
	public void setM_wScreenWidth(short m_wScreenWidth) {
		this.m_wScreenWidth = m_wScreenWidth;
	}
	public short getM_wScreenHeight() {
		return m_wScreenHeight;
	}
	public void setM_wScreenHeight(short m_wScreenHeight) {
		this.m_wScreenHeight = m_wScreenHeight;
	}
	public short m_wPosX; 
	public short m_wPosY; 
	public short m_wScreenWidth; 
	public short m_wScreenHeight; 

}