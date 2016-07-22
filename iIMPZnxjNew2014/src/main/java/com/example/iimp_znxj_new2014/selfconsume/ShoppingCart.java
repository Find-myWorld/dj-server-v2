package com.example.iimp_znxj_new2014.selfconsume;
/**
 *@author 作者 E-mail
 *@version 创建时间:2015-11-9上午11:44:18
 *类说明
 */
public class ShoppingCart {
	
	private int gId;        //商品ID
	private int gCount;     //商品数量
	private String gPrice;  //商品单价
	
	public ShoppingCart(){
	}
	
	public ShoppingCart(int id,int count,String price){
		this.gId = id;
		this.gCount = count;
		this.gPrice = price;
	}

	public int getgId() {
		return gId;
	}

	public void setgId(int gId) {
		this.gId = gId;
	}

	public int getgCount() {
		return gCount;
	}

	public void setgCount(int gCount) {
		this.gCount = gCount;
	}

	public String getgPrice() {
		return gPrice;
	}

	public void setgPrice(String gPrice) {
		this.gPrice = gPrice;
	}
}
