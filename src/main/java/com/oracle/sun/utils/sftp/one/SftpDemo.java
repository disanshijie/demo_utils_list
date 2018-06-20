package com.oracle.sun.utils.sftp.one;

import java.io.File;

import com.jcraft.jsch.ChannelSftp;

public class SftpDemo {

	
	 //测试
    public static void main(String[] args) throws Exception {
    	//test1();
    	//test2();
    	//test3();
    	downloadDir();
    }
    
    public static void downloadDir() throws Exception {
    	
    	 ChannelSftp sftp = SftpUtil.getSftpConnect("59.110.224.8", 22, "root", "sjc@7ZXJPDZ");
         String dest = "D:\\opt\\qwe";
         
         System.out.println("下载文件开始...");
         //下载文件夹
         String src = "/usr/games";
        // SftpUtil.downloadDir(src, dest, sftp);
         
         //下载文件
         String src2 = "/usr/games/sql.zip";
         String dest3 = "D:\\opt\\qwe/sql.zip";
         SftpUtil.download(src2, dest3, sftp);
         
         SftpUtil.exit(sftp);
         System.exit(0);
    }
    /**
     * OK
     * @Description
     * 返回类型 void
     * @throws Exception
     * @注	
     */
    public static void test1() throws Exception {
    	 ChannelSftp sftp = SftpUtil.getSftpConnect("59.110.224.8", 22, "root", "sjc@7ZXJPDZ");
         String pathString = "D:\\sunjinchao\\垃圾箱/new33.txt";
         String dir = "D:\\opt\\tomcat-7.0";
         
         String directory = "/usr/games";
         System.out.println("上传文件开始...");
         //上传文件
        // SftpUtil.uploadFile(new File(pathString), directory, sftp);
         //上传文件夹
         SftpUtil.uploadFile(new File(dir), directory, sftp);
         
         //上传文件，指定Linux上文件名称
         SftpUtil.uploadFile(pathString, directory,"sql.zip", sftp);
         
         SftpUtil.exit(sftp);
         System.exit(0);
    }
    /**
     * OK
     * @Description
     * 返回类型 void
     * @throws Exception
     * @注	
     */
    public static void test2() throws Exception {
    	 ChannelSftp sftp = SftpUtil.getSftpConnect("59.110.224.8", 22, "root", "sjc@7ZXJPDZ");
         String pathString = "/usr/games/比赛默认图.zip";
         //删除文件
         SftpUtil.rmFile(pathString, sftp);
         
         //递归删除目录下的内容
         String pathString2 = "/usr/games/";
         SftpUtil.rmDirFile(pathString2, sftp);
         
         //递归删除目录
         //SftpUtil.rmDir(pathString2, sftp, true);
         
         SftpUtil.exit(sftp);
         System.exit(0);
    }
    //上传文件
   
}
