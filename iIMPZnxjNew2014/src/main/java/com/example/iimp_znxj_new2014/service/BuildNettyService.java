package com.example.iimp_znxj_new2014.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.iimp_znxj_new2014.entity.UserInfo;
import com.example.iimp_znxj_new2014.netty.NettyServer;
import com.google.gson.Gson;


/**
 * Created by yang on 2016/7/6.
 */
public class BuildNettyService extends Service {
    @Override
    public void onCreate() {
        Log.d("tag","服务打开");
        /*开启一个线程，不断地监听端口*/
        new Thread() {
            @Override
            public void run() {
                Log.d("tag","扫描线程开启");
                try {
                    NettyServer.openChannel();//开启一个服务端channel
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
