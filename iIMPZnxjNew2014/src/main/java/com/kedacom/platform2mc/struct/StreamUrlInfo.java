package com.kedacom.platform2mc.struct;

import java.io.Serializable;

public class StreamUrlInfo implements Serializable {

	
		public String getM_szUrl() {
			return m_szUrl;
		}
		public void setM_szUrl(String m_szUrl) {
			this.m_szUrl = m_szUrl;
		}
		public int getM_dwWidth() {
			return m_dwWidth;
		}
		public void setM_dwWidth(int m_dwWidth) {
			this.m_dwWidth = m_dwWidth;
		}
		public int getM_dwHeight() {
			return m_dwHeight;
		}
		public void setM_dwHeight(int m_dwHeight) {
			this.m_dwHeight = m_dwHeight;
		}
		public String getM_szDesc() {
			return m_szDesc;
		}
		public void setM_szDesc(String m_szDesc) {
			this.m_szDesc = m_szDesc;
		}
		public String getM_manuFactory() {
			return m_manuFactory;
		}
		public void setM_manuFactory(String m_manuFactory) {
			this.m_manuFactory = m_manuFactory;
		}
		public String getM_reserve() {
			return m_reserve;
		}
		public void setM_reserve(String m_reserve) {
			this.m_reserve = m_reserve;
		}
	
		public String m_szUrl;
		public int m_dwWidth; 
    public int m_dwHeight; 
    public String m_szDesc; 
    public String m_manuFactory; 
    public String m_reserve; 
    public int m_UrlType;

}
