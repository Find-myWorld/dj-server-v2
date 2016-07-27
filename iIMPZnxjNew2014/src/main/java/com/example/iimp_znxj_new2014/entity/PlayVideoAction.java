package com.example.iimp_znxj_new2014.entity;

/**
 * Created by Administrator on 2016/7/27.
 */
public class PlayVideoAction {

    /**
     * action : playVideo
     * data : {"serverIp":"192.168.0.90","port":"8000","fileName":"xxxx"}
     */

    private String action;
    /**
     * serverIp : 192.168.0.90
     * port : 8000
     * fileName : xxxx
     */

    private DataBean data;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private String serverIp;
        private String port;
        private String fileName;

        public String getServerIp() {
            return serverIp;
        }

        public void setServerIp(String serverIp) {
            this.serverIp = serverIp;
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
    }
}
