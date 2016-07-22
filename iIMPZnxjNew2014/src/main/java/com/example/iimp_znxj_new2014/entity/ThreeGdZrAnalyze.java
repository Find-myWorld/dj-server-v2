package com.example.iimp_znxj_new2014.entity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.content.res.XmlResourceParser;
import android.util.Xml;

public class ThreeGdZrAnalyze {

	public List<DutyInfo> m_list = new ArrayList<DutyInfo>();

	public static ThreeGdZrAnalyze parse(InputStream in) throws Exception {
		
		ThreeGdZrAnalyze infolist = new ThreeGdZrAnalyze();

		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();

			while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {

				if (parser.getEventType() == XmlResourceParser.START_TAG) {
					String name = parser.getName();

					if (name.equalsIgnoreCase("Duty")) {

						String item = parser.getAttributeValue(null, "Item");

						String pname = parser.getAttributeValue(null, "Name");
						
						String mark = parser.getAttributeValue(null, "Mark");

						infolist.m_list.add(new DutyInfo(item, pname,mark));
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
