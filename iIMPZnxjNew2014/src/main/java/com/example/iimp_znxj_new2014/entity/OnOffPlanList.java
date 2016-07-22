package com.example.iimp_znxj_new2014.entity;

import java.util.ArrayList;
import java.util.List;

public class OnOffPlanList {
	private List<OnOff> onOffPlanList;

	public OnOffPlanList(){
		onOffPlanList=new ArrayList<OnOff>();
	}
	public List<OnOff> getOnOffPlanList() {
		return onOffPlanList;
	}

	public void setOnOffPlanList(List<OnOff> onOffPlanList) {
		this.onOffPlanList = onOffPlanList;
	}
	
	public void add(OnOff onoff){
		this.onOffPlanList.add(onoff);
	}
	
	public OnOff get(int index){
		return this.onOffPlanList.get(index);
	}
	
	public int size(){
		return this.onOffPlanList.size();
	}
}
