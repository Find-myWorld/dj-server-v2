package com.kedacom.platform2mc.struct;

import java.io.Serializable;

public class EncoderParam implements Serializable {
	public byte getM_byVideoFormat() {
		return m_byVideoFormat;
	}
	public void setM_byVideoFormat(byte m_byVideoFormat) {
		this.m_byVideoFormat = m_byVideoFormat;
	}
	public int getM_dwVideoResolution() {
		return m_dwVideoResolution;
	}
	public void setM_dwVideoResolution(int m_dwVideoResolution) {
		this.m_dwVideoResolution = m_dwVideoResolution;
	}
	public byte getM_byFrameRate() {
		return m_byFrameRate;
	}
	public void setM_byFrameRate(byte m_byFrameRate) {
		this.m_byFrameRate = m_byFrameRate;
	}
	public byte getM_byQuality() {
		return m_byQuality;
	}
	public void setM_byQuality(byte m_byQuality) {
		this.m_byQuality = m_byQuality;
	}
	public int getM_dwBitRate() {
		return m_dwBitRate;
	}
	public void setM_dwBitRate(int m_dwBitRate) {
		this.m_dwBitRate = m_dwBitRate;
	}
	public short getM_wInterval() {
		return m_wInterval;
	}
	public void setM_wInterval(short m_wInterval) {
		this.m_wInterval = m_wInterval;
	}
	public byte getM_byBrightness() {
		return m_byBrightness;
	}
	public void setM_byBrightness(byte m_byBrightness) {
		this.m_byBrightness = m_byBrightness;
	}
	public byte getM_byContrast() {
		return m_byContrast;
	}
	public void setM_byContrast(byte m_byContrast) {
		this.m_byContrast = m_byContrast;
	}
	public byte getM_bySaturation() {
		return m_bySaturation;
	}
	public void setM_bySaturation(byte m_bySaturation) {
		this.m_bySaturation = m_bySaturation;
	}
	public short getM_wSharpness() {
		return m_wSharpness;
	}
	public void setM_wSharpness(short m_wSharpness) {
		this.m_wSharpness = m_wSharpness;
	}
	public byte m_byVideoFormat; // 
    public int  m_dwVideoResolution; // 
    public byte  m_byFrameRate;
    public byte m_byQuality;
    public int m_dwBitRate;
    public short m_wInterval;
    public byte m_byBrightness;
    public byte m_byContrast;
    public byte m_bySaturation;
    public short m_wSharpness;
    
}
