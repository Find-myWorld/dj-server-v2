package com.example.iimp_znxj_new2014.entity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

public class LifeMenuAnalyze {
	public static List<LifeMenu> getLifeMenu(InputStream inStream)
			throws Exception {
		List<LifeMenu> lifeMenus = null;
		LifeMenu lifeMenu = null;
		XmlPullParser pullParser = Xml.newPullParser();
		pullParser.setInput(inStream, "UTF-8");
		int event = pullParser.getEventType();// 触发第一事件
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_DOCUMENT:
				lifeMenus = new ArrayList<LifeMenu>();
				break;
			case XmlPullParser.START_TAG:
				if ("Menu".equals(pullParser.getName())) {
					int id = new Integer(pullParser.getAttributeValue(0));
					lifeMenu = new LifeMenu();
					lifeMenu.setId(id);
				} else if (lifeMenu != null) {
					if ("TodayMorMenu".equals(pullParser.getName())) {
						lifeMenu.setTodayBreak(pullParser.nextText());
					} else if ("TodayNoonMenu".equals(pullParser.getName())) {
						lifeMenu.setTodaylunch(pullParser.nextText());
					} else if ("TodayNightMenu".equals(pullParser.getName())){
						lifeMenu.setTodayDiner(pullParser.nextText());
					} else if ("MorMenu".equals(pullParser.getName())){
						lifeMenu.setTomorBreak(pullParser.nextText());
					} else if ("NoonMenu".equals(pullParser.getName())){
						lifeMenu.setTomorlunch(pullParser.nextText());
					} else if ("NightMenu".equals(pullParser.getName())){
						lifeMenu.setTomorDiner(pullParser.nextText());
					}
				}
				break;
			case XmlPullParser.END_TAG:
				if ("Menu".equals(pullParser.getName())) {
					lifeMenus.add(lifeMenu);
				}
				break;
			}
			event = pullParser.next();
		}
		return lifeMenus;
	}
}
