package com.example.iimp_znxj_new2014.netty;


import com.example.iimp_znxj_new2014.entity.JianshiMediaPlay;
import com.example.iimp_znxj_new2014.entity.PlayVCR;
import com.example.iimp_znxj_new2014.entity.PlayVideo;
import com.example.iimp_znxj_new2014.event.JsonEvent;
import com.example.iimp_znxj_new2014.util.Constant;
import com.example.iimp_znxj_new2014.util.JsonUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2016/7/21.
 */
public class JsonProcessorFactory {
    public static final String PORT = "6700";
    public static final String SEVER_IP = "192.168.1.253";

    public static void changeJsonType(Object object,String type) {
        System.err.println("进入工厂----");
        switch (type){
            case Constant.TYPE_MEDIA_CONTROL:
                PlayVCR playVCR = new PlayVCR();


                break;
            case Constant.TYPE_MEDIA_PLAY:
            if(object instanceof JianshiMediaPlay) {
                List<String> files = ((JianshiMediaPlay) object).getMovieFiles();
                final PlayVideo playVideo = new PlayVideo();
                playVideo.setFileName(files.get(0));
                playVideo.setPort(PORT);
                playVideo.setSeverIp(SEVER_IP);
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            String s = JsonUtils.encode(playVideo);
                            System.err.println(s);
                            EventBus.getDefault().post(new JsonEvent(s));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                break;
            }
        }

    }


}
