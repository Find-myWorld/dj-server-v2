package com.example.iimp_znxj_new2014.entity;

import java.util.ArrayList;
import java.util.List;

public class FullSubTitlePlan {
	private List<RollSubTitle> rollSubTitlePlan;

	public FullSubTitlePlan() {
		rollSubTitlePlan = new ArrayList<RollSubTitle>();
	}

	public void add(RollSubTitle rollSubTitle) {
		this.rollSubTitlePlan.add(rollSubTitle);
	}

	public void remove(RollSubTitle rollSubTitle) {
		this.rollSubTitlePlan.remove(rollSubTitle);
	}

	public RollSubTitle get(int index) {
		return rollSubTitlePlan.get(index);
	}

	public int size() {
		return rollSubTitlePlan.size();
	}
	
	public void clear(){
		rollSubTitlePlan.clear();
	}

}
