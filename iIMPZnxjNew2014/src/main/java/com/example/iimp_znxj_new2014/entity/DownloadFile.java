package com.example.iimp_znxj_new2014.entity;

/*
 * 文件下载到本地
 */
public class DownloadFile {
	private String serverIp;
	private String port;
	private String fileName;
	
	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}


	public String getServerIp() {
		return serverIp;
	}
	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
