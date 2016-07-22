package com.example.iimp_znxj_new2014.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

public class FileUtils {
	public static final String TAG = "FileUtils";

	/**
	 * 外部存储目录
	 */
	private static final String BASE_PATH = Environment
			.getExternalStorageDirectory().getAbsolutePath();

	/**
	 * splash缓存目录
	 */
	private static final String SPLASH_CACHE_PATH = BASE_PATH + "daily_life";

	public static boolean isHasSdCard(){
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if(sdCardExist){
			return true;
		}else{
			return false;
		}
		
	}
	
	public static String getStringCache(String folderName, String fileName,
			Context context) {

		if (context == null) {
			return null;
		}
		String folder = context.getFilesDir() + "/" + folderName;
		if (!checkAndCreateFolder(folder)) {
			return null;
		}
		String filePath = folder + "/" + fileName;
		File file = new File(filePath);
		if (!file.exists()) {
			return null;
		}
		FileInputStream inStream = null;
		ByteArrayOutputStream stream = null;
		String streamStr;
		try {
			inStream = new FileInputStream(filePath);
			stream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length = -1;
			while ((length = inStream.read(buffer)) != -1) {
				stream.write(buffer, 0, length);
			}
		   streamStr = new String(stream.toByteArray());
		   return streamStr;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} finally {
			try {
				stream.close();
				inStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static void saveStringToCache(Context context, String folderName,
			String fileName, String jsonString) {
		Log.d(TAG, "write file to disk folderName = {} fileName = {} = "
				+ folderName + fileName);
		if (context == null) {
			return;
		}
		String folder = context.getFilesDir() + "/" + folderName;
		if (!checkAndCreateFolder(folder)) {
			return;
		}
		String filePath = folder + "/" + fileName;
		File file = new File(filePath);
		deleteFolder(context, file);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {

			}
		}
		String saveStr = jsonString;
		try {
			Log.d(TAG, "save file to {} jsonString = {} = " + filePath
					+ jsonString);
			FileOutputStream outStream = new FileOutputStream(filePath);
			outStream.write(saveStr.getBytes());
			outStream.close();
		} catch (FileNotFoundException e) {
			return;
		} catch (IOException e) {
			return;
		}
	}

	public static boolean checkAndCreateFolder(String folder) {
		try {
			File dirFile = new File(folder);
			if (!(dirFile.exists()) && !(dirFile.isDirectory())) {
				return dirFile.mkdirs();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static void deleteFolder(Context context, File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}

		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				file.delete();
				return;
			}

			for (int i = 0; i < childFiles.length; i++) {
				deleteFolder(context, childFiles[i]);
			}
			file.delete();
		}
	}

	public static void deleteAllFileCache(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}
		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				file.delete();
				return;
			}

			for (int i = 0; i < childFiles.length; i++) {
				deleteAllFileCache(childFiles[i]);
			}
			file.delete();
		}
	}

	/**
	 * @return float 单位为M
	 * @description 获取文件夹大小
	 * @date 2013-12-03
	 * @author huyongsheng
	 */
	public static float getFolderSize(File folder) {
		float size = 0;
		try {
			File[] fileList = folder.listFiles();
			if (null == fileList) {
				return 0;
			}
			for (File file : fileList) {
				if (file.isDirectory()) {
					size = size + getFolderSize(file);
				} else {
					size = size + file.length();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return size / 1048576;
	}

	/**
	 * 获取Splash文件大小
	 * 
	 * @date 2014-04-25
	 * @author huyongsheng
	 */
	public static float getSplashFolderSize() {
		return getFolderSize(new File(SPLASH_CACHE_PATH));
	}
	
	/*
	 * 添加到已经存在的定时任务文件中
	 * @author
	 */
	
	public static void	addStringToCache(){
		
	}
	
	
	
}
