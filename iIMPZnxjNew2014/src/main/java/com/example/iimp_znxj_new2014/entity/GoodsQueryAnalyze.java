package com.example.iimp_znxj_new2014.entity;



import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.util.Log;
import android.util.Xml;

public class GoodsQueryAnalyze {

	private final static String TAG = "GoodsQueryAnalyze";
	public static List<GoodsQuery> getGoodsQuery(InputStream inStream)
			throws Exception {
		
		List<GoodsQuery> goodsQyList = null;
		GoodsQuery goodsqy = null;
		
		XmlPullParser pull = Xml.newPullParser();
		pull.setInput(inStream, "UTF-8");
		int event = pull.getEventType();// ��ȡ��ǰ��ǩ

		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_DOCUMENT:
				goodsQyList = new ArrayList<GoodsQuery>();
				break;
			case XmlPullParser.START_TAG:
				if("Consume".equals(pull.getName())){
					int id = new Integer(pull.getAttributeValue(0));
					goodsqy = new GoodsQuery();
					goodsqy.setId(id);
				}else if(goodsqy != null){
					if("Content".equals(pull.getName())){
						goodsqy.setContent(pull.nextText());
					}else if("Price".equals(pull.getName())){
						goodsqy.setPrice(pull.nextText());
					}else if("Date".equals(pull.getName())){
						goodsqy.setDate(pull.nextText());
					}
				}
				break;
			case XmlPullParser.END_TAG:
				if("Consume".equals(pull.getName())){
					goodsQyList.add(goodsqy);
				}
				break;
			}
			event = pull.next();
		}
		return goodsQyList;
	}
}
