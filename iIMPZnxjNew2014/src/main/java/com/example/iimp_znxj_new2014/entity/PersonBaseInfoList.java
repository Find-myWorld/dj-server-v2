package com.example.iimp_znxj_new2014.entity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.content.res.XmlResourceParser;
import android.util.Xml;

public class PersonBaseInfoList {
	
	public List<PersonBaseInfo> m_list = new ArrayList<PersonBaseInfo>();

	public static PersonBaseInfoList parse(InputStream in) throws Exception {
		
		PersonBaseInfoList infolist = new PersonBaseInfoList();

		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();

			while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {

				if (parser.getEventType() == XmlResourceParser.START_TAG) {
					String name = parser.getName();

					if (name.equalsIgnoreCase("PersonBaseInfo")) {

					
						String id = parser.getAttributeValue(null, "PersonID");

						String pname = parser.getAttributeValue(null, "PersonName");

						String cardId=parser.getAttributeValue(null,"CardNum");
						infolist.m_list.add(new PersonBaseInfo(id, pname,cardId));
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
