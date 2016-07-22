package com.example.iimp_znxj_new2014.selfconsume;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.util.Log;
import android.util.Xml;

public class BuyDailyAnalyze {
	
	private final static String TAG = "BuyDailyAnalyze";
	public static List<BuyShopping> getBuyDaily(InputStream inStream)
			throws Exception {
		List<BuyShopping> bsps = null;
		BuyShopping bsp = null;
		XmlPullParser pullParser = Xml.newPullParser();
		pullParser.setInput(inStream, "UTF-8");
		int event = pullParser.getEventType();
		while (event != XmlPullParser.END_DOCUMENT) {
			String nodeName = pullParser.getName();
			switch (event) {
			case XmlPullParser.START_DOCUMENT:
				bsps = new ArrayList<BuyShopping>();
				break;
			case XmlPullParser.START_TAG:
				if ("RYPProduct".equals(pullParser.getName())) {
					int id = new Integer(pullParser.getAttributeValue(0));
					bsp = new BuyShopping();  //
					bsp.setId(id);
				} else if (bsp != null) {
					if ("Count".equals(pullParser.getName())){
						bsp.setCount(pullParser.nextText());
					} else if ("PName".equals(pullParser.getName())) {
						bsp.setName(pullParser.nextText());
					} else if ("Content".equals(pullParser.getName())) {
						bsp.setExplain(pullParser.nextText());
					} else if ("PicUrl".equals(pullParser.getName())){
						bsp.setPicture(pullParser.nextText());
					} else if ("Price".equals(pullParser.getName())){
						bsp.setPrice(pullParser.nextText());
					}
				}  
				break;
			case XmlPullParser.END_TAG:
				if ("RYPProduct".equals(pullParser.getName())) {  
					bsps.add(bsp);
				}
				break;
			}
			event = pullParser.next();
		}
		return bsps;
	}
}
