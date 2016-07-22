package com.example.iimp_znxj_new2014.entity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

public class ThreeGdZbAnalyze {

	public static List<ThreeGdZb> getThreeGdZb(InputStream inStream)
			throws Exception {
		List<ThreeGdZb> zbs = null;
		ThreeGdZb zb = null;
		XmlPullParser pullParser = Xml.newPullParser();
		pullParser.setInput(inStream, "UTF-8");
		int event = pullParser.getEventType();// 触发第一事件
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_DOCUMENT:
				zbs = new ArrayList<ThreeGdZb>();
				break;
			case XmlPullParser.START_TAG:
				if ("onduty".equals(pullParser.getName())) {
					int id = new Integer(pullParser.getAttributeValue(0));
					zb = new ThreeGdZb();
					zb.setId(id);
				} else if (zb != null) {
				    if ("time".equals(pullParser.getName())) {
						zb.setTime(pullParser.nextText());
					} else if ("person".equals(pullParser.getName())) {
						zb.setPerson(pullParser.nextText());
					}  else if ("remark".equals(pullParser.getName())) {
						zb.setRemark(pullParser.nextText());
					}  
				}
				break;
			case XmlPullParser.END_TAG:
				if ("onduty".equals(pullParser.getName())) {
					zbs.add(zb);
				}
				break;
			}
			event = pullParser.next();
		}
		return zbs;
	}
}
