package com.note.adapter;

/**
 * 作者：Sparks
 * 创建时间：2023/3/5  17:50
 * 描述：TODO
 */

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.note.database.Note;
import com.note.jin.MainActivity;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/*
 * Author:Maxwell
 * Date:2020-02-04
 */
public class FtpUtils {

    private static final String TAG = "FTP";
    public static final String ftpFileName = "data.txt";
    private FTPClient ftpClient;
    private Context context;

    public FtpUtils(Context context) {
        // this.ftpClient = ftpClient;
        this.context = context;
    }

    public FTPClient getFTPClient(String ftpHost, int ftpPort, String ftpUserName, String ftpPassword) {
        if (ftpClient == null) {
            ftpClient = new FTPClient();
        }
        if (ftpClient.isConnected()) {
            return ftpClient;
        }
        //  Log.d(TAG, "ftpHost:" + ftpHost + ",ftpPort:" + ftpPort);

        try {
            // connect to the ftp server
            // set timeout
            ftpClient.setConnectTimeout(50000);
            // 设置中文编码集，防止中文乱码
            ftpClient.setControlEncoding("UTF-8");

            ftpClient.connect(ftpHost, ftpPort);
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                 Log.d(TAG, "connect fail: replyCode:" + replyCode);
               // Toast.makeText(context, "connect fail", Toast.LENGTH_SHORT).show();
                return null;
            }
             Log.d(TAG, "connect success: replyCode" + replyCode);
           // Toast.makeText(context, "connect success", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
            return null;
        }

        // Log.d(TAG, "ftpUserName:" + ftpUserName + ",ftpPassword:" + ftpPassword);
        // login on the ftp server
        try {

            ftpClient.login(ftpUserName, ftpPassword);
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                ftpClient.disconnect();
                Log.d(TAG, "login fail: replyCode:" + replyCode);
               // Toast.makeText(context, "login fail", Toast.LENGTH_SHORT).show();
                return null;
            }
           // Toast.makeText(context, "login success", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "login success: replyCode:" + replyCode);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
            return null;
        }

        return ftpClient;
    }


    /**
     * 关闭FTP方法
     *
     * @param ftp
     * @return
     */
    public boolean closeFTP(FTPClient ftp) {

        try {
            ftp.logout();
        } catch (Exception e) {
            Log.e(TAG, "FTP关闭失败");
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                    Log.e(TAG, "FTP关闭失败");
                }
            }
        }

        return false;

    }


    /**
     * 下载FTP下指定文件
     *
     * @param ftp      FTPClient对象
     * @param filePath FTP文件路径
     * @param fileName 文件名
     * @param downPath 下载保存的目录
     * @return true:success, false:fail
     */
    public boolean downLoadFTP(FTPClient ftp, String filePath, String fileName, String downPath) {
        // 默认失败
        boolean flag = false;
        FTPFile[] files;
        // 跳转到文件目录
        try {
            ftp.changeWorkingDirectory(filePath);
            // 获取目录下文件集合
            ftp.enterLocalPassiveMode();
            files = ftp.listFiles();
        } catch (IOException e) {
            Log.e(TAG, "downLoadFTP: " + e);
            e.printStackTrace();
            return false;
        }

        for (FTPFile file : files) {
            // 取得指定文件并下载
            if (file.getName().equals(fileName)) {
                Log.d(TAG, "fileName:" + fileName);
                File downDir = new File(downPath);
                if (!downDir.exists()) {
                  //  downDir.mkdirs();
                    Log.d("loadftp","找不到文件");
                    return false;
                }

                FileOutputStream fos = null;
                try {
                    File downFile = new File(downPath + File.separator + file.getName());
                    if (!downFile.exists()) {
                        downFile.createNewFile();
                    }
                    fos = new FileOutputStream(downFile);
                    // 绑定输出流下载文件,需要设置编码集，不然可能出现文件为空的情况
                    flag = ftp.retrieveFile(new String(file.getName().getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1), fos);
                    // 下载成功删除文件,看项目需求
                    // ftpClient.deleteFile(new String(fileName.getBytes("UTF-8"),"ISO-8859-1"));
                    fos.flush();
                    if (flag) {

                        Log.d(TAG, "Params downloaded successful.");
                    } else {
                        Log.e(TAG, "Params downloaded failed.");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return flag;
    }

    /**
     * FTP文件上传工具类
     *
     * @param ftp      ftpClient
     * @param filePath filePath
     * @param ftpPath  ftpPath
     * @return true:success, false:fail
     */
    public boolean uploadFile_old(FTPClient ftp, String filePath, String ftpPath) {
        boolean flag = false;
        InputStream in = null;
        try {
            // 设置PassiveMode传输
            ftp.enterLocalPassiveMode();
            //设置二进制传输，使用BINARY_FILE_TYPE，ASC容易造成文件损坏
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            //判断FPT目标文件夹时候存在不存在则创建
            if (!ftp.changeWorkingDirectory(ftpPath)) {
                ftp.makeDirectory(ftpPath);
            }
            //跳转目标目录
            ftp.changeWorkingDirectory(ftpPath);

            //上传文件
            File file = new File(filePath);
            in = new FileInputStream(file);
            String tempName = ftpPath + File.separator + file.getName();
            flag = ftp.storeFile(new String(tempName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1), in);
            if (flag) {
                Log.d(TAG, "上传成功");
            } else {
                Log.e(TAG, "上传失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "上传失败");
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }


    public  boolean uploadFile(FTPClient ftpClient, String destDictionary, File file) throws IOException {
        InputStream in = null;
        ftpClient.enterLocalPassiveMode();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Date date = new Date(System.currentTimeMillis());         //获取系统的当前时
        String finalDate = sdf.format(date);        //格式化日期



        try {
            if (ftpClient.changeWorkingDirectory(destDictionary)) {
                in = new FileInputStream(file);
                String sn = finalDate+".jin";
                return ftpClient.storeFile(new String(sn.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1), in);
            }
        }catch (Exception e){
            Log.d("uploadFile", e.toString());
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }



    public boolean putData(List<Note> noteArr){
       // String name = "data.txt";
        ObjectOutputStream fos = null;
        boolean flag = false;
        try {
            fos = new ObjectOutputStream(context.openFileOutput(ftpFileName,Context.MODE_PRIVATE));
            fos.writeObject(noteArr);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(fos!=null){
                try {
                    fos.flush();
                    fos.close();
                    flag = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
           return flag;
    }


    public List<Note> openData(){
       // String name = "data.txt";
        ObjectInputStream fos = null;
        List<Note> notearr = null;
        try {
            fos = new ObjectInputStream(context.openFileInput(ftpFileName));
            notearr = (List<Note>)fos.readObject();
            fos.close();
            Log.d("open", notearr.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return notearr;
    }
}