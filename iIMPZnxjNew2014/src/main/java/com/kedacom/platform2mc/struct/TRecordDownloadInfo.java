package com.kedacom.platform2mc.struct;

import java.io.Serializable;
import com.kedacom.platform2mc.struct.TPeriod;
public class TRecordDownloadInfo implements Serializable 
{
	public TPeriod getM_RecPeriod()
	{
		return m_tRecPeriod;
	}
	public void setM_RecPeriod(TPeriod period)
	{
		this.m_tRecPeriod = period;
	}
	public String getM_FileFullName() 
	{
		return m_pFileFullName;
	}
	public void setM_FileFullName(String strFileFullName) 
	{
		this.m_pFileFullName = strFileFullName;
	}
	public int getM_FileType() 
	{
		return m_emFileType;
	}
	public void setM_FileType(int nFileType) 
	{
		this.m_emFileType = nFileType;
	}
	public int getM_RecordType() 
	{
		return m_byRecordType;
	}
	public void setM_RecordType(int nRecType) 
	{
		this.m_byRecordType = nRecType;
	}
	public int getM_Reserve1() 
	{
		return m_dwReserve1;
	}
	public void setM_Reserve1(int nReserve1) 
	{
		this.m_dwReserve1 = nReserve1;
	}
	public int getM_Reserve2() 
	{
		return m_dwReserve2;
	}
	public void setM_Reserve2(int nReserve2) 
	{
		this.m_dwReserve2 = nReserve2;
	}

	public TPeriod m_tRecPeriod;
	public String m_pFileFullName;
	public int m_emFileType;//ELocalRecType default emMp4
	public int m_byRecordType;//eRecordType default emInvailedRecord
	public int m_dwReserve1;
	public int m_dwReserve2;
}