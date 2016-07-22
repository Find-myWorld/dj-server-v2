package com.example.iimp_znxj_new2014.entity;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

import android.content.res.XmlResourceParser;
import android.util.Log;
import android.util.Xml;

/**
 *@author 作者 E-mail
 *@version 创建时间:2015-11-5上午10:07:29
 *类说明
 */
public class RoomAnalyze {
	
	public static Room parse(InputStream in) throws Exception {
		try{  
        	XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, "UTF-8");
            parser.nextTag();
            while (parser.getEventType() != XmlResourceParser.END_DOCUMENT){  
            	Log.i("TTSS","Room parse,in=5==>"+parser.getEventType()+"|"+XmlPullParser.START_TAG);
                if (parser.getEventType() == XmlPullParser.START_TAG)
            	{   
            		String name = parser.getName();
                    if(name.equalsIgnoreCase("Room")){   
                    	Room room = new Room();
                    	String roomId = parser.getAttributeValue(null, "RoomID");
                    	String roomName = parser.getAttributeValue(null, "RoomName");
                    	String rollCallTime=parser.getAttributeValue(null,"RollCallTime");
                    	Log.i("TTSS","roomId1="+roomId+","+roomName);
                    	room.setRoomID(roomId);
                    	room.setRoomName(roomName);
                    	room.setRollCallTime(rollCallTime);
                    	return room;
                    }
                    if(name.equalsIgnoreCase("Result")){   
                    	Room room = new Room();
                    	String roomId = parser.getAttributeValue(null, "IsSuccess");
                    	String roomName = parser.getAttributeValue(null, "Msg");
                    	Log.i("TTSS","roomId2="+roomId+","+roomName);
                    	room.setRoomID(roomId);
                    	room.setRoomName(roomName);
                    	return room;
                    }
            	}else if (parser.getEventType() == XmlPullParser.END_TAG){    
            		Log.i("TTSS","Room parse,in=8==>"+XmlPullParser.END_TAG);
                }else if (parser.getEventType() == XmlPullParser.TEXT){    
                	Log.i("TTSS","Room parse,in=9==>"+XmlPullParser.TEXT);
                }
            	parser.next(); 
            } 
        }catch (Exception e) {
        	Log.i("TTSS","Error=>"+e.getMessage());
        	e.printStackTrace();
        }
        return null;
	}
}
