package com.example.iimp_znxj_new2014.entity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.content.res.XmlResourceParser;
import android.util.Xml;

public class AgentInfoList {

	public List<AgentInfo> m_list = new ArrayList<AgentInfo>();
	public String m_comment;

	public static AgentInfoList parse(InputStream in) throws Exception {
		
		AgentInfoList infolist = new AgentInfoList();

		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();

			while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {

				if (parser.getEventType() == XmlResourceParser.START_TAG) {
					String name = parser.getName();

					if (name.equals("Agent")) {

						String timeStart = parser.getAttributeValue(null, "Start_Time");
						String timeEnd = parser.getAttributeValue(null, "End_Time");
						String pid = parser.getAttributeValue(null, "PID");
						String pname = parser.getAttributeValue(null, "Name");						

						infolist.m_list.add(new AgentInfo(timeStart, timeEnd, pid, pname));
					}
					else if (name.equals("Comment"))
					{
						infolist.m_comment = parser.getAttributeValue(null, "Content");	
					}
				}

				else if (parser.getEventType() == XmlPullParser.END_TAG) {
				} else if (parser.getEventType() == XmlPullParser.TEXT) {
				}

				parser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return infolist;
	}
}
