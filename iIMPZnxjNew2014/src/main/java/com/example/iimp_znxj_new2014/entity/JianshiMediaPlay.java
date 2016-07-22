package com.example.iimp_znxj_new2014.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/7/18.
 */
public class JianshiMediaPlay  {
    private List<String> movieFiles;
    private List<String> jianqu1List;
    private List<String> jianqu2List;
    private List<String> jianqu3List;
    private List<String> jianqu4List;

    public List<String> getMovieFiles() {
        return movieFiles;
    }

    public void setMovieFiles(List<String> movieFiles) {
        this.movieFiles = movieFiles;
    }

    public List<String> getJianqu1List() {
        return jianqu1List;
    }

    public void setJianqu1List(List<String> jianqu1List) {
        this.jianqu1List = jianqu1List;
    }

    public List<String> getJianqu2List() {
        return jianqu2List;
    }

    public void setJianqu2List(List<String> jianqu2List) {
        this.jianqu2List = jianqu2List;
    }

    public List<String> getJianqu3List() {
        return jianqu3List;
    }

    public void setJianqu3List(List<String> jianqu3List) {
        this.jianqu3List = jianqu3List;
    }

    public List<String> getJianqu4List() {
        return jianqu4List;
    }

    public void setJianqu4List(List<String> jianqu4List) {
        this.jianqu4List = jianqu4List;
    }

    @Override
    public String toString() {
        return "JianshiAndJianqu{" +
                "movieFiles=" + movieFiles +
                ", jianqu1List=" + jianqu1List +
                ", jianqu2List=" + jianqu2List +
                ", jianqu3List=" + jianqu3List +
                ", jianqu4List=" + jianqu4List +
                '}';
    }
}

