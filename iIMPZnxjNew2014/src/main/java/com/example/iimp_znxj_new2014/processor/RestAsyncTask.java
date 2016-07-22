package com.example.iimp_znxj_new2014.processor;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import android.os.AsyncTask;
import android.util.Log;

import com.example.iimp_znxj_new2014.entity.SendEntity;
import com.example.iimp_znxj_new2014.model.response.BaseServerResponse;
import com.example.iimp_znxj_new2014.service.UdpService;
import com.example.iimp_znxj_new2014.util.JsonUtils;

public abstract class RestAsyncTask<InputInfo, Result>
        extends AsyncTask<List<SendEntity>, Void, Result> {

    private static final String LOG_TAG = RestAsyncTask.class.getName();
    
    @Override
    @SuppressWarnings("unchecked")
    protected Result doInBackground(List<SendEntity>... params) {
        try {
        	//String jsonString = WebServiceManager.getServerResponse(getRequestUrl(), params[0]);
        	String jsonString = UdpService.getServerStr();
        	BaseServerResponse result = JsonUtils.decode(jsonString, BaseServerResponse.class);
            Object data = result.getData();
            Class<Result> clazz = (Class<Result>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
            return JsonUtils.decode(JsonUtils.encode(data), clazz);
        } catch (Exception e) {
        	e.printStackTrace();
            return null;
        } 
    }
}
