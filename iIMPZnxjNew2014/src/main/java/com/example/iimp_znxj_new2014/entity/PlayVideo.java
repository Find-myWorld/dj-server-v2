package com.example.iimp_znxj_new2014.entity;

public class PlayVideo {
	private String severIp;
	private String fileName;
	private String port;
	private String local;
	private String loop; //判断是否循环
	
	private String curPercent;//续播时此参数会控制播放进度
	
	private String volume;
	
	
	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	public String getCurPercent() {
		return curPercent;
	}

	public void setCurPercent(String curPercent) {
		this.curPercent = curPercent;
	}

	public String getLoop() {
		return loop;
	}

	public void setLoop(String loop) {
		this.loop = loop;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getSeverIp() {
		return severIp;
	}

	public void setSeverIp(String severIp) {
		this.severIp = severIp;
	}
}
