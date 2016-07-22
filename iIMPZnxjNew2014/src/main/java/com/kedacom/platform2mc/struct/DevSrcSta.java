package com.kedacom.platform2mc.struct;

import java.io.Serializable;

public class DevSrcSta implements Serializable {
	public boolean isbSrcEnable() {
		return bSrcEnable;
	}
	public void setbSrcEnable(boolean bSrcEnable) {
		this.bSrcEnable = bSrcEnable;
	}
	public boolean isbSrcOnline() {
		return bSrcOnline;
	}
	public void setbSrcOnline(boolean bSrcOnline) {
		this.bSrcOnline = bSrcOnline;
	}
	public boolean bSrcEnable; // the id of group
    public boolean bSrcOnline; // the id of parent group

}
