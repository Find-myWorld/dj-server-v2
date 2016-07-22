package com.example.iimp_znxj_new2014.entity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.content.res.XmlResourceParser;
import android.util.Xml;

public class ThreeGdPwAnalyze {

	public List<BedInfo> m_list = new ArrayList<BedInfo>();
	public static ThreeGdPwAnalyze getThreeGdPw(InputStream inStream)
			throws Exception {
		ThreeGdPwAnalyze infolist = new ThreeGdPwAnalyze();
//		ThreeGdPw pw = null;
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(inStream, "UTF-8");
		int event = parser.getEventType();// ������һ�¼�
		while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {

			if (parser.getEventType() == XmlResourceParser.START_TAG) {
				String name = parser.getName();

				if (name.equalsIgnoreCase("Bed")) {

				
					String id = parser.getAttributeValue(null, "ID");

					String pname = parser.getAttributeValue(null, "Name");

					infolist.m_list.add(new BedInfo(id, pname));
				}
			}

			else if (parser.getEventType() == XmlPullParser.END_TAG) {
			} else if (parser.getEventType() == XmlPullParser.TEXT) {
			}

			parser.next();
		}
		return infolist;
	}
}

