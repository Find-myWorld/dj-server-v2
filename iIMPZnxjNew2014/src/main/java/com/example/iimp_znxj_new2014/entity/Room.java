package com.example.iimp_znxj_new2014.entity;
/**
 *   监房类
 */
public class Room {
	
	public String RoomID;
	public String RoomName;

	public String RollCallTime;
	
	
	
	public String getRollCallTime() {
		return RollCallTime;
	}
	public void setRollCallTime(String rollCallTime) {
		RollCallTime = rollCallTime;
	}
	public String getRoomID() {
		return RoomID;
	}
	public void setRoomID(String roomID) {
		RoomID = roomID;
	}
	public String getRoomName() {
		return RoomName;
	}
	public void setRoomName(String roomName) {
		RoomName = roomName;
	}
}
