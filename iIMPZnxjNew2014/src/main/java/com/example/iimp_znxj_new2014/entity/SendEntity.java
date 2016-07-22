package com.example.iimp_znxj_new2014.entity;

public class SendEntity {
    private String paraName;
    private String contenBody;
    
    public String getParaName() {
        return paraName;
    }

    public void setParaName(String paraName) {
        this.paraName = paraName;
    }

    public Object getContenBody() {
        return contenBody.toString();
    }

    public void setContenBody(String contenBody) {
        this.contenBody = contenBody;
//        this.contenBody = StringBody.create(contenBody, "text/html", Charset.forName(HTTP.UTF_8));
    }
}
