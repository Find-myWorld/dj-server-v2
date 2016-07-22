package com.example.iimp_znxj_new2014.dao;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Properties;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

/*
 * ���ܣ�����дSD��
 * ע�������SD��Ȩ��
 */
public class FileHelper {
	private Context context;
	/** SD���Ƿ���� **/
	private boolean hasSD = false;
	/** SD����·�� **/
	private String SDPATH;
	/** ��ǰ������·�� **/
	private String FILESPATH;

	public FileHelper(Context context) {
		this.context = context;
		hasSD = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		SDPATH = Environment.getExternalStorageDirectory().getPath();
		FILESPATH = this.context.getFilesDir().getPath();
	}

	/**
	 * ��SD���ϴ����ļ�
	 * @throws IOException
	 */
	public File createSDFile(String fileName) throws IOException {
		File file = new File(SDPATH + "//" + fileName);
		if (!file.exists()) {
			file.createNewFile();
		}
		return file;
	}

	/**
	 * ɾ��SD���ϵ��ļ�
	 * @param fileName
	 */
	public boolean deleteSDFile(String fileName) {
		File file = new File(SDPATH + "//" + fileName);
		if (file == null || !file.exists() || file.isDirectory())
			return false;
		return file.delete();
	}

	/**
	 * д�����ݵ�SD���е�txt�ı��� strΪ����
	 */
	public void writeSDFile(String str, String fileName) {
		try {
			FileWriter fw = new FileWriter(SDPATH + "//" + fileName);
			File f = new File(SDPATH + "//" + fileName);
			fw.write(str);
			FileOutputStream os = new FileOutputStream(f);
			DataOutputStream out = new DataOutputStream(os);
			out.writeShort(2);
			out.writeUTF("");
			System.out.println(out);
			fw.flush();
			fw.close();
			System.out.println(fw);
		} catch (Exception e) {
		}
	}

	/**
	 * ��ȡSD�����ı��ļ�
	 * @param fileName
	 * @return
	 */
	public String readSDFile(String fileName) {
		StringBuffer sb = new StringBuffer();
		File file = new File(SDPATH + "//" + fileName);
		try {
			FileInputStream fis = new FileInputStream(file);
			int c;
			while ((c = fis.read()) != -1) {
				sb.append((char) c);
			}
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	 /*
	 * ��ȡ�����ļ�
     */
    public Properties loadConfig(Context context,String file){
    	Properties properties = new Properties();
    	try{
    		FileInputStream s = new FileInputStream(file);
    		properties.load(s);
    	}catch(Exception e){
    		e.printStackTrace();
    		//Log.i(TAG,"loadConfig,Exception��"+e.getMessage());
    	}
		return properties;
    }
	    
    //���ַ�д�뵽�ı��ļ���
    public static void WriteTxtFile(String strcontent,String strFilePath)
    {
		// ÿ��д��ʱ��������д
		String strContent = strcontent + "\n";
		try {
			File file = new File(strFilePath);
			if (!file.exists()) {
				Log.d("TestFile", "Create the file:" + strFilePath);
				file.createNewFile();
			}
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			raf.seek(file.length());
			raf.write(strContent.getBytes());
			raf.close();
		} catch (Exception e) {
			Log.e("TestFile", "Error on write File.");
		}
    }
    
    //�жϸ��ļ��Ƿ����
	public static boolean fileIsExists(String path){  //����ֻ���ж�SD���µ�·��
		try{
			File f = new File(path);
			if(!f.exists()){
				return false;
			}
		}catch(Exception e){
			return false;
		}
		return true;
	}
    
    
    /*
     * ���������ļ�
     */
    public boolean saveConfig(Context context,String file,Properties properties){
    	try{
    		File fl = new File(file);
    		if(!fl.exists()){
    			fl.createNewFile();
    			FileOutputStream s = new FileOutputStream(fl);
    			properties.store(s, "");
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    	return true;
    }

	public String getFILESPATH() {
		return FILESPATH;
	}

	public String getSDPATH() {
		return SDPATH;
	}

	public boolean hasSD() {
		return hasSD;
	}
}
