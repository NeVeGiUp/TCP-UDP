package com.itc.smartbroadcast.channels.tftp;
import android.util.Log;

import org.apache.commons.net.tftp.TFTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.apache.commons.net.tftp.TFTP.BINARY_MODE;
import static org.apache.commons.net.tftp.TFTPClient.DEFAULT_MAX_TIMEOUTS;

public class FileTool{

    /**
     * Description: 向FTP服务器上传文件
     * @Version      1.0
     * @param url FTP服务器hostname
     * @param port  FTP服务器端口
     * @param filename  上传到FTP服务器上的文件名
     * @param input   输入流
     * @return 成功返回true，否则返回false *
     */
    public static boolean uploadFile(String url,// FTP服务器hostname
                                     int port,// FTP服务器端口
                                     String filename, // 上传到FTP服务器上的文件名
                                     InputStream input // 输入流
    ){
        boolean success = false;
        TFTPClient tftp = new TFTPClient();

        try {
            Log.i("test", "uploadFile ====="+System.currentTimeMillis());
            tftp.open();
            tftp.setMaxTimeouts(DEFAULT_MAX_TIMEOUTS);
            tftp.sendFile(filename,BINARY_MODE,input,url,port);
            tftp.close();
            Log.i("test", "uploadFile: success ======="+System.currentTimeMillis());
            Log.i("test", "uploadFile: success");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return success;
    }

    /**
     * 将本地文件上传到FTP服务器上 *
     */
    public static void upLoadFromProduction(String url,// FTP服务器hostname
                                            int port,// FTP服务器端口
                                            String filename, // 上传到FTP服务器上的文件名
                                            String orginfilename // 输入流文件名
    ) {
        try {
            FileInputStream in = new FileInputStream(new File(orginfilename));
            boolean flag = uploadFile(url, port,filename, in);
            System.out.println(flag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}