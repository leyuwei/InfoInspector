package com.leyuwei.infoinspector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.os.Environment;

public class FileUtils {
	
	
	public static String getSDPath(Context context){
		File sdDir = null;
		if (isSDExist())
			sdDir = Environment.getExternalStorageDirectory(); // 获取根目录
		else
			sdDir = context.getFilesDir(); // 没有SD卡就返回下载目录
		return sdDir.toString();
	}

	
	public static boolean isSDExist() {
		return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
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
