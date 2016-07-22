package com.example.iimp_znxj_new2014.util;

public class UnitaryCodeUtil {
	// 传入USB键值返回对应串口键值
	public static int UnitaryCode(int code_USB) {
		if (code_USB == 66) {
			return 69;
		} else if (code_USB == 111) {
			return 81;
		} else if (code_USB >= 7 && code_USB <= 16) {
			return code_USB + 41;
		} else if (code_USB == 19) {
			return 85;
		} else if (code_USB == 20) {
			return 68;
		} else if (code_USB == 21) {
			return 76;
		} else if (code_USB == 22) {
			return 82;
		}
		return code_USB;
	}
	
	public static int UnitaryOldCode(int code_USB) {
		if (code_USB == 143) {
			return 69;
		} else if (code_USB == 111) {
			return 81;
		} else if (code_USB >= 7 && code_USB <= 16) {
			return code_USB + 41;
		} else if (code_USB == 133) {
			return 85;
		} else if (code_USB == 134) {
			return 68;
		} else if (code_USB == 131) {
			return 76;
		} else if (code_USB == 132) {
			return 82;
		}
		return code_USB;
	}
	
	//传入卡号返回去除开头的0
	public static String removeStarts0(String cardNum)
	{
		return cardNum.replaceFirst("^0+", "");
	}
}
