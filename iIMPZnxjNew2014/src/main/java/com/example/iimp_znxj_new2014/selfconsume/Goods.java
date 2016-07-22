package com.example.iimp_znxj_new2014.selfconsume;


public class Goods {
	private int id;
	private String name;
	private String description;
	private String price;
	private byte[] pic;
	
   
	public Goods()
    {
    	
    }

	public Goods(int id,String name,String description,String price,byte[] pic)
	{
		this.id = id;
		this.name = name;
		this.description = description;
		this.price = price;
		this.pic = pic;
	}
	
	public Goods(int i,String name,String price,String description)
	{
		this.id = i;
		this.name = name;
		this.description = description;
		this.price = price;
	}
	
	public Goods(byte[] pic)
	{
		this.pic = pic;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setUserName(String name) {
		this.name = name;
	}
	public String getDescrip() {
		return description;
	}
	public void setDescrip(String description) {
		this.description = description;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public byte[] getPic() {
		return pic;
	}
	public void setPic(byte[] pic) {
		this.pic = pic;
	}
	
	public String toString()
	{
		return "��ƣ�"+ name + "   ������"+description + "   Id��"+  id + "   �۸�"+price ;
	}	
}
