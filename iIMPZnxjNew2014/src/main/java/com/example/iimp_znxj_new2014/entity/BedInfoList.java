package com.example.iimp_znxj_new2014.entity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.content.res.XmlResourceParser;
import android.util.Log;
import android.util.Xml;

public class BedInfoList {
	
	public List<BedInfo> m_list = new ArrayList<BedInfo>();

	public static BedInfoList parse(InputStream in) throws Exception {
		
		BedInfoList infolist = new BedInfoList();

		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();

			while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {

				if (parser.getEventType() == XmlResourceParser.START_TAG) {
					String name = parser.getName();

					if (name.equalsIgnoreCase("Bed")) {

					
						String id = parser.getAttributeValue(null, "ID");
						Log.i("TTSS","��λID=>"+id);
						String pname = parser.getAttributeValue(null, "Name");

						infolist.m_list.add(new BedInfo(id, pname));
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
