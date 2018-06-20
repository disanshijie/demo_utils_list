package com.oracle.sun.utils.sftp.one;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * 文件工具类.<br>
 * 1.所有的文件路径必须以'/'开头和结尾，否则路径最后一部分会被当做是文件名<br>
 * 1.1通过程序获取的路径 需要 =replaceAll("\\\\","/")<br>
 * 2. @since version-0.3 方法出现异常的时候，<del>会关闭sftp连接(但是不会关闭session和channel)</del>(del @ version 0.31)，异常会抛出<br>
 * @author Leon Lee
 * 缺陷 路劲必须是 /形式的
 * 
 */
public class SftpUtil {
    /**
     * sftp连接池.
     */
    private static final Map<String, Channel> SFTP_CHANNEL_POOL = new HashMap<String, Channel>();

    /**
     * 获取sftp协议连接.
     * @param host 主机名
     * @param port 端口
     * @param username 用户名
     * @param password 密码
     * @return 连接对象
     * @throws JSchException 异常
     */
    public static ChannelSftp getSftpConnect(final String host, final int port, final String username,
            final String password) throws JSchException {
        Session sshSession = null;
        Channel channel = null;
        ChannelSftp sftp = null;
        String key = host + "," + port + "," + username + "," + password;
        if (null == SFTP_CHANNEL_POOL.get(key)) {
            JSch jsch = new JSch();
            jsch.getSession(username, host, port);
            sshSession = jsch.getSession(username, host, port);
            sshSession.setPassword(password);
            Properties sshConfig = new Properties();
            //设置第一次登陆的时候提示，可选值:(ask | yes | no) 
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            sshSession.connect();
            //30秒连接超时 
            //sshSession.connect(30000); 
            //sshSession.setTimeout(30000); // 设置timeout时间  好像一样
            channel = sshSession.openChannel("sftp");
            channel.connect();
            SFTP_CHANNEL_POOL.put(key, channel);
        } else {
            channel = SFTP_CHANNEL_POOL.get(key);
            sshSession = channel.getSession();
            if (!sshSession.isConnected())
                sshSession.connect();
            if (!channel.isConnected())
                channel.connect();
        }
        sftp = (ChannelSftp) channel;
        return sftp;
    }

    /**
     * 下载文件夹 
     * @Description
     * 返回类型 Boolean
     * @param srcFile
     * @param localdst
     * @param sftp
     * @return
     * @throws Exception
     * @注	
     */
    public static Boolean downloadDir(final String srcPath, final String localdst, final ChannelSftp sftp) throws Exception {
        File destFile=new File(localdst);
        if(localdst.contains(".")) {
        	destFile.createNewFile();
        }else {
        	destFile.mkdir();
        }
        
    	if(dirExist(srcPath, sftp)) {
    		//List<String> list = formatPath(srcPath);
    		String pathString=srcPath;
            Vector<LsEntry> vector = sftp.ls(pathString);
            if (vector.size() == 1) { // 文件，
            	download(pathString,localdst,sftp);
            } else if (vector.size() == 2) { // 空文件夹，
                System.out.println("空文件夹");
            } else {
                // 删除文件夹下所有文件
                for (LsEntry en : vector) {
                	String  fileName = en.getFilename();
                    if (".".equals(fileName) || "..".equals(fileName)) {
                        continue;
                    } else {
                    	downloadDir(pathString + "/" + fileName,localdst+"/"+fileName, sftp);
                    }
                }
            }
    	}
    	return false;
    }
    
    /**
     * 下载文件-sftp协议.
     * @param downloadFile 下载的文件
     * @param saveFile 存在本地的路径 包括文件名
     * @param sftp sftp连接
     * @return 文件
     * @throws Exception 异常
     */
    protected static File download(final String downloadFile, final String saveFile,final ChannelSftp sftp) throws Exception {
        FileOutputStream os = null;
        File file = new File(saveFile);
        try {
            if (!file.exists()) {
                File parentFile = file.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                file.createNewFile();
            }
            os = new FileOutputStream(file);
            List<String> list = formatPath(downloadFile);
            
            sftp.get(list.get(0) + list.get(1), os);
            
        } catch (Exception e) {
            throw e;
        } finally {
            os.close();
        }
        return file;
    }
   
    /**
     * 下载文件-sftp协议.
     * @param downloadFile 下载的文件
     * @param saveFile 存在本地的路径
     * @param sftp sftp连接
     * @return 文件
     * @throws Exception 异常
     */
    protected static File download(final String downloadFile, final String saveFile,final String fileName, final ChannelSftp sftp)
            throws Exception {
        FileOutputStream os = null;
        File file = new File(saveFile,fileName);
        try {
            if (!file.exists()) {
                File parentFile = file.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                file.createNewFile();
            }
            os = new FileOutputStream(file);
            List<String> list = formatPath(downloadFile);
            
            sftp.get(list.get(0) + list.get(1), os);
            
        } catch (Exception e) {
            throw e;
        } finally {
            os.close();
        }
        return file;
    }

    /**
     * 下载文件-sftp协议.
     * @param downloadFile 下载的文件
     * @param saveFile 存在本地的路径
     * @param sftp sftp连接
     * @return 文件 byte[]
     * @throws Exception 异常
     */
    public static byte[] downloadAsByte(final String downloadFile, final ChannelSftp sftp) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            List<String> list = formatPath(downloadFile);
            sftp.get(list.get(0) + list.get(1), os);
        } catch (Exception e) {
            throw e;
        } finally {
            os.close();
        }
        return os.toByteArray();
    }


    /**
     * 上传文件-sftp协议.
     * @param srcFile 源文件	必须是文件啊
     * @param dir 保存路径（Linux）	需要/开头和结尾
     * @param fileName 保存文件名
     * @param sftp sftp连接
     * @throws Exception 异常
     */
    public static void uploadFile(final String srcFile, final String dir, final String fileName, final ChannelSftp sftp)
            throws SftpException {
    	if(dir!=null) {
        	 String md =formatPathLinux(dir); //格式化了Linux路径
        	 mkdirs(md, sftp); //可以递归
             sftp.cd(md);
             sftp.put(srcFile, fileName);
             // sftp.put(srcFile, new String(fileName.getBytes(),"UTF-8"));  //中文名称的  
             //sftp.setFilenameEncoding("UTF-8");  
    	}else {
    		System.out.println("Linux路径空");
    	}
        
    }
    /**
    * 上传文件-sftp协议. 将源文件/夹上传到指定Linux文件夹下，
    * @param srcFile 源文件  文件或者文件夹
     * @param directory 保存路径 Linux文件夹
     * @param sftp sftp连接
     * @throws FileNotFoundException 
     * @throws UnsupportedEncodingException 
     * @throws Exception 异常
     */
    public static void uploadFile(File srcFile, String directory, ChannelSftp sftp)
    		throws SftpException, FileNotFoundException, UnsupportedEncodingException {
    	if(srcFile.isFile()){
    		uploadFile(srcFile.getAbsolutePath(), directory, srcFile.getName(),sftp);
         }else if(srcFile.isDirectory()){
             File[] files = srcFile.listFiles();
             for (File file2 : files) {
                 if(file2.isDirectory()){
                     directory =directory+"/"+file2.getName();
                 }
                 uploadFile(file2,directory,sftp);  //递归
             }  
         } 
    }
    /**
     * OK
     * 递归创建文件夹.
     * @param dir 路径 必须是 /xxx/xxx/ 不能就单独一个/
     * @param sftp sftp连接
     * @return 是否创建成功
     * @throws SftpException 异常
     */
    private static boolean mkdirs(final String dir, final ChannelSftp sftp) throws SftpException {
        String dirs = dir.substring(1, dir.length() - 1);
        String[] dirArr = dirs.split("/");
        String base = "";
        for (String d : dirArr) {
            base += "/" + d;
            if (dirExist(base + "/", sftp)) {
                continue;
            } else {
                sftp.mkdir(base + "/");
            }
        }
        return true;
    }

    /**
     * OK
     * 判断文件夹是否存在.
     * @param dir 文件夹路径， /xxx/xxx/
     * @param sftp sftp协议
     * @return 是否存在
     */
    private static boolean dirExist(final String dir, final ChannelSftp sftp) {
        try {
            Vector<?> vector = sftp.ls(dir);
            if (null == vector)
                return false;
            else
                return true;
        } catch (SftpException e) {
            return false;
        }
    }


    /**
     * 删除文件-sftp协议.
     * @param pathString 要删除的文件
     * @param sftp sftp连接
     * @throws SftpException 异常
     */
    public static void rmFile(final String srcPath, final ChannelSftp sftp) throws SftpException {
        String path = srcPath.replaceAll("\\\\", "/").trim();
        if (dirExist(path, sftp)) {
            sftp.rm(path);
        }
    }

    /**
     * 删除文件夹-sftp协议.如果文件夹有内容，则会抛出异常.
     * @param pathString 文件夹路径
     * @param sftp sftp连接
     * @param resursion 递归删除
     * @throws SftpException 异常
     */
    public static void rmDir(final String pathString, final ChannelSftp sftp, final boolean recursion)
            throws SftpException {
        String fp = formatPathLinux(pathString);
        if (dirExist(fp, sftp)) {
            if (recursion)
                exeRmRec(fp, sftp);
            else
                sftp.rmdir(fp);
        }
    }

    /**
     * 删除文件夹下的所有文件
     * @Description
     * 返回类型 void
     * @param pathString
     * @param sftp
     * @throws SftpException
     * @注
     */
    public static void rmDirFile(final String pathString, final ChannelSftp sftp  )
            throws SftpException {
    	 Vector<LsEntry> vector = sftp.ls(pathString);
         if (vector.size() == 1) { // 文件，直接删除
             sftp.rm(pathString);
         } else if (vector.size() == 2) { // 空文件夹，直接删除
           
         } else {
             String fileName = "";
             // 删除文件夹下所有文件
             for (LsEntry en : vector) {
                 fileName = en.getFilename();
                 if (".".equals(fileName) || "..".equals(fileName)) {
                     continue;
                 } else {
                     exeRmRec(pathString + "/" + fileName, sftp);
                 }
             }
             // 删除文件夹
             sftp.rmdir(pathString);
         }
    }
    /**
     * OK
     * 递归删除执行.
     * @param pathString 文件路径
     * @param sftp sftp连接
     * @throws SftpException
     */
    private static void exeRmRec(final String pathString, final ChannelSftp sftp) throws SftpException {
        @SuppressWarnings("unchecked")
        Vector<LsEntry> vector = sftp.ls(pathString);
        if (vector.size() == 1) { // 文件，直接删除
            sftp.rm(pathString);
        } else if (vector.size() == 2) { // 空文件夹，直接删除
            sftp.rmdir(pathString);
        } else {
            String fileName = "";
            // 删除文件夹下所有文件
            for (LsEntry en : vector) {
                fileName = en.getFilename();
                if (".".equals(fileName) || "..".equals(fileName)) {
                    continue;
                } else {
                    exeRmRec(pathString + "/" + fileName, sftp);
                }
            }
            // 删除文件夹
            sftp.rmdir(pathString);
        }
    }
    
    /**
     * @Description
     * 返回类型 List<String>
     * @param srcPath
     * @return	将 /xxx\\xxx/xxx 转化为 /xxx/xxx/xxx/
     * @注
     */
    private static String formatPathLinux(String srcPath) {
    	String path = srcPath.replaceAll("\\\\", "/").trim();
    	 int pl=path.length();
    	 if (path.startsWith("/")) {
    		  if (pl>1) {
    			  path = path.endsWith("/") ? path : path+"/";
	 		  }else {
	 			  System.out.println("Linux路径至跟目录");
	 			  path="/";
	 		  }
    	 }else {
    		 path="/";
    		 System.out.println("Linux路径有问题，默认至跟目录");
    	 }
    	 return path;
    }
    
    private static List<String> formatPath(String srcPath) {
    	  List<String> list = new ArrayList<String>(2);
    	  
    	  if(srcPath.trim().length()>3) {
	          File file=new File(srcPath);
	          String name=file.getName();	//文件名称
	          
	      	  String parentPath=file.getParent();//文件所在目录
	      	  int pl=parentPath.length();
	      		  parentPath = parentPath.replaceAll("\\\\", "/");
	      		  if(pl>1) {
	      			  parentPath = parentPath.startsWith("/") ? "/"+parentPath : parentPath;
	      			  parentPath = parentPath.endsWith("/") ? parentPath : parentPath+"/";
	      		  }else if(pl==1) {
	      			  parentPath="/";
	      		  }else {
	      			  System.out.println("Linux路径有问题，默认至跟目录");
	      			  parentPath="/";
	      		  }
	      		  list.add(parentPath);
	      		  list.add(name);
      	  }
          return list;
    }
   
    /**
     * 关闭协议-sftp协议.(关闭会导致连接池异常，因此不建议用户自定义关闭)
     * @param sftp sftp连接
     */
    public static void exit(final ChannelSftp sftp) {
        sftp.exit();
    }
}
