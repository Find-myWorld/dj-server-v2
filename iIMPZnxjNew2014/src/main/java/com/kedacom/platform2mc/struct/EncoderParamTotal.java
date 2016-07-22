package com.kedacom.platform2mc.struct;

import java.io.Serializable;

public class EncoderParamTotal implements Serializable {
	public EncoderParam getM_tEncoderParam() {
		return m_tEncoderParam;
	}
	public void setM_tEncoderParam(EncoderParam m_tEncoderParam) {
		this.m_tEncoderParam = m_tEncoderParam;
	}
	public SupportResolution[] getM_atSpResolution() {
		return m_atSpResolution;
	}
	public void setM_atSpResolution(SupportResolution[] m_atSpResolution) {
		this.m_atSpResolution = m_atSpResolution;
	}
	public EncoderParam m_tEncoderParam;
    public SupportResolution m_atSpResolution[];
}
