package com.leyuwei.infoinspector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.os.Environment;

public class FileUtils {
	
	/**
	 * 20181127 修正：在运行Android4以上系统的设备上无法使用一级外部存储的问题
	 * 20181127 修正：在运行Android4以上系统的设备上无需再考虑内置存储，仅在两级外部存储中做选择
	 **/
	
	public static String getSDPath(Context context){
		String sdDir = null;
		if (isSDExist())
			sdDir = System.getenv("SECONDARY_STORAGE"); // 获取二级存储
		else
			sdDir = System.getenv("EXTERNAL_STORAGE"); // 获取一级存储（现在2018年市面上大部分手机都是使用一级存储）
		return sdDir;
	}

	
	public static boolean isSDExist() {
		return System.getenv("SECONDARY_STORAGE") != null;	// 判别二级外部存储（外部实体SD卡）是否存在
	}
	
	
	public static String createDir(String dirName, Context context) {
		File file = new File(getSDPath(context) + dirName);
        if (!file.exists()) {
        	file.mkdir();
        }
        return getSDPath(context) + dirName;
	}
    
	
    public static String createFile(String fileName, Context context) {
        File file = new File(getSDPath(context) + fileName);
        if (!file.exists()) {
        	// 当文件不存在才执行下面操作
	        if (fileName.indexOf(".") != -1) {
	            // 说明包含，即创建文件, 返回值为-1就说明不包含.,即使文件
	            try {
	                file.createNewFile();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        } else {
	            // 创建文件夹
	            file.mkdir();
	        }
        }
        return getSDPath(context) + fileName;
    }
    
    
    public static InputStream getFileStream(String fileName, Context context) {
    	String strFile = getSDPath(context) + fileName;
    	try
        {
            File f=new File(strFile);
            if (!f.exists())
        		return null;
            FileInputStream fis = new FileInputStream(f);
            InputStream is = fis;
            return is;
        }
    	catch (Exception e)
        {
            return null;
        }
    }
    
    
	//判断文件是否存在
    public static boolean fileExists(String fileName, Context context)
    {
    	String strFile = getSDPath(context) + fileName;
        try
        {
            File f=new File(strFile);
            if (!f.exists())
        		return false;
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }

}
