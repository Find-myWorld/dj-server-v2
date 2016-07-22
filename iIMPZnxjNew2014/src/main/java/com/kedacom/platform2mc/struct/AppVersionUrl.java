package com.kedacom.platform2mc.struct;

import java.io.Serializable;

public class AppVersionUrl implements Serializable {

    public String getM_appVersion() {
		return m_appVersion;
	}
	public void setM_appVersion(String m_appVersion) {
		this.m_appVersion = m_appVersion;
	}
	public String getM_appUpdateUrl() {
		return m_appUpdateUrl;
	}
	public void setM_appUpdateUrl(String m_appUpdateUrl) {
		this.m_appUpdateUrl = m_appUpdateUrl;
	}
	public String m_appVersion; // app version
    public String m_appUpdateUrl; // app update url

}
