package com.oracle.sun.utils.sftp.design;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.oracle.sun.utils.sftp.design.impl.FileProgressMonitor;

public class Demo {

	public static void main(String[] args) throws Exception {
		
		/*
		String src = "D:\\垃圾箱/APPSTORE宣传图.zip"; // 本地文件名
    	String dst = "/usr/games/APPSTORE宣传图.zip"; // 目标文件
    	uploadFile( src, dst);
		*/
		
    	/*
    	String src = "/usr/games/比赛默认图.zip";
    	String dst = "D:\\opt\\比赛默认图.zip";
    	downloadFile( src, dst);
    	*/
		
		executeExecCmd();
		executeSellCmd();
		
	}
	/**
	 * @Description 大下载文件
	 * 返回类型 void
	 * @param src	Linux文件
	 * @param dst	目标目录 window
	 * @throws FileNotFoundException
	 * @throws SftpException
	 * @注	保证 src,dst的存在性 单文件
	 */
	public static void downloadFile(String src,String dst) throws FileNotFoundException, SftpException {
		//实力化
		VersouSshUtil vs=new VersouSshUtil();
		//建立sftp连接
		ChannelSftp chSftp=vs.getChannelSftp();
		//获取Linux目标文件大小
	    SftpATTRS attr = chSftp.stat(src);
        long fileSize = attr.getSize();
        
        OutputStream out = new FileOutputStream(dst);
		try {
            /*
             * 代码段1：直接将目标服务器上文件名为src的文件下载到本地，本地文件名为dst。
             * （注：使用这个方法时，dst可以是目录，若dst为目录，则下载到本地的文件名将与src文件名相同）
             */
            //chSftp.get(src, dst, new FileProgressMonitor(fileSize)); // 代码段1
          
			/**
             * 代码段3：采用读取get方法返回的输入流数据的方式来下载文件。
             * 这个示例中，将读取的数据写入到了本地的一个文件中。采用这种方式，可以由应用程序设定每次读取输入流的数据块大小，也就是每次传输的数据块大小
             * 
             */
            InputStream is = chSftp.get(src, new FileProgressMonitor(fileSize));
            byte[] buff = new byte[1024 * 2];
            int read;
            if (is != null) {
                System.out.println("Start to read input stream");
                do {
                    read = is.read(buff, 0, buff.length);
                    if (read > 0) {
                        out.write(buff, 0, read);
                    }
                    out.flush();
                } while (read >= 0);
                System.out.println("input stream read done.");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            chSftp.quit();
            vs.closeChannel(chSftp);
        }
	}
	
	/**
	 * @Description 上传文件  可以显示上传进度
	 * 返回类型 void
	 * @param src window文件
	 * @param dst linux路径 
	 * @注	保证 src,dst的存在性   必须时 单文件
	 */
	public static void uploadFile(String src,String dst) {
    	File file = new File(src);
    	long fileSize = file.length();
		VersouSshUtil vs=new VersouSshUtil();
		ChannelSftp chSftp=vs.getChannelSftp();
		try {
			//上传文件
			chSftp.put(new FileInputStream(src), dst, ChannelSftp.OVERWRITE); // 代码段1
			
			// 代码段2
	    	OutputStream out = chSftp.put(dst, new FileProgressMonitor(fileSize), ChannelSftp.OVERWRITE); // 使用OVERWRITE模式
	    	byte[] buff = new byte[1024 * 256]; // 设定每次传输的数据块大小为256KB
	    	int read;
	    	if (out != null) {
	    		System.out.println("Start to read input stream");
	    		InputStream is = new FileInputStream(src);
	    		do {
	    			read = is.read(buff, 0, buff.length);
	    			if (read > 0) {
	    				out.write(buff, 0, read);
	    			}
	    			out.flush();
	    		} while (read >= 0);
	    		System.out.println("input stream read done.");
	    	}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			chSftp.quit();
			vs.closeChannel(chSftp);
	    }
	}
	/**
	 * @Description
	 * 返回类型 void
	 * @注	将结果都放到一个string中了
	 */
	public static void executeExecCmd() {
		VersouSshUtil vs=new VersouSshUtil();
		try {
			String patterns="git";
			String cmd = "ls -ltr /usr/soft/git-2.16.0/|grep "+patterns+"|tail -1|awk {'print $NF'}\n";
			String print1=vs.executeExecCmd(cmd);
			System.out.println(print1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			vs.closeChannel();
	    }
	}
	
	/**
	 * 
	 * @Description shell脚本运行
	 * 返回类型 void
	 * @注 返回值存在 编码问题  没有解决
	 */
	public static void executeSellCmd() {
		VersouSshUtil vs=new VersouSshUtil();
		try {
			String retMsg=vs.executeShellCmd("ls /usr/games \n");
			//得到的结果包含很多行，需要处理
			String[] retArr= retMsg.split("\n");
			System.out.println(retArr[retArr.length-2].trim());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			vs.closeChannel();
		}
	}
	
}
