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
			sdDir = Environment.getExternalStorageDirectory(); // ��ȡ��Ŀ¼
		else
			sdDir = context.getFilesDir(); // û��SD���ͷ�������Ŀ¼
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
        	// ���ļ������ڲ�ִ���������
	        if (fileName.indexOf(".") != -1) {
	            // ˵���������������ļ�, ����ֵΪ-1��˵��������.,��ʹ�ļ�
	            try {
	                file.createNewFile();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        } else {
	            // �����ļ���
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
    
    
	//�ж��ļ��Ƿ����
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
