/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.example.iimp_znxj_new2014.netty;

import android.util.Log;

import com.example.iimp_znxj_new2014.entity.JianshiMediaPlay;
import com.example.iimp_znxj_new2014.entity.UserInfo;
import com.example.iimp_znxj_new2014.util.Constant;
import com.example.iimp_znxj_new2014.util.JsonUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.unix.NativeInetAddress;

/**
 * Handles a server-side channel.
 */
public class ServerHandler extends SimpleChannelInboundHandler<String> {

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws UnknownHostException {

        // list so the channel received the messages from others.
        System.err.println("处于活连接");
        //ctx.writeAndFlush("Welcome to  secure chat service!\n");
    }


    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        String[] strings = msg.split("\\+");
        if(strings[1].equals("login")) {
            System.err.println("切割结果"+ strings[0]);
            loginResult(ctx,strings[0]);
        }else if(strings[1].equals("mediaPlay")) {
            mediaPlayResult(ctx, strings[0]);
        }else if(strings[1].equals("playControl")){
            playControlResult(ctx, strings[0]);
        }

    }
    /*处理播放控制*/
    private void playControlResult(ChannelHandlerContext ctx, String msg) {
        JsonProcessorFactory.changeJsonType(msg,Constant.TYPE_MEDIA_CONTROL);
    }
    /*//处理媒体播放数据*/
    private void mediaPlayResult(ChannelHandlerContext ctx, String msg) throws IOException {
        System.err.println("来自client的信息(mediaPlayResult)：" + msg);
        JianshiMediaPlay mediaPlay =  JsonUtils.decode(msg,JianshiMediaPlay.class);
        JsonProcessorFactory.changeJsonType(mediaPlay, Constant.TYPE_MEDIA_PLAY);
        Log.d("tag",mediaPlay.toString());
    }
    /*处理登陆数据*/
    private void loginResult(ChannelHandlerContext ctx, String msg) throws IOException {

        System.err.println("来自client的信息：" + msg);

        UserInfo userInfo = JsonUtils.decode(msg,UserInfo.class);

        msg = "用户名:"+ userInfo.getUsername()+ "    密码:" + userInfo.getPassword() + "----" + ctx.channel().remoteAddress()+ "----" + msg;
        Log.d("tag",msg);
        System.out.println(ctx.channel().id());
        ctx.writeAndFlush("1\n");
        //ctx.writeAndFlush("[" + ctx.channel().remoteAddress() + "] " + msg + '\n');

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
