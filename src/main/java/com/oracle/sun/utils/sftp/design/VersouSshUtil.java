package com.oracle.sun.utils.sftp.design;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.Charset;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
/**
 * 
 * @url
 * @Description
 * @author admin
 * @date 2018年6月8日
 * @version V1.0
 * @说明
 */
public class VersouSshUtil {
    private Session session = null; 
    private static int deftimeout = 60000;  
    private static final Logger LOG = Logger.getLogger(VersouSshUtil.class);  
    /**
     * @Description
     * 返回类型 Session
     * @param timeout 连接超时时间 默认60秒
     * @return
     * @throws JSchException 
     * @注 没有用构造器方式实例化，后面session自己建ChannelExec或channelShell了
     */
    public VersouSshUtil()  {
        LOG.info("尝试连接到....host:" + SFTPConstants.SFTP_REQ_USERNAME + ",username:" + SFTPConstants.SFTP_REQ_HOST + ",password:" + ",port:" + SFTPConstants.SFTP_DEFAULT_PORT);  
        String ftpHost =SFTPConstants.SFTP_REQ_HOST;
        String port =SFTPConstants.SFTP_REQ_PORT;
        String ftpUserName = SFTPConstants.SFTP_REQ_USERNAME;
        String ftpPassword = SFTPConstants.SFTP_REQ_PASSWORD;
        
        int ftpPort = SFTPConstants.SFTP_DEFAULT_PORT;
        if (port != null && !port.equals("")) {
            ftpPort = Integer.valueOf(port);
        }
        try {
            JSch jsch = new JSch(); // 创建JSch对象
            session = jsch.getSession(ftpUserName, ftpHost, ftpPort); // 根据用户名，主机ip，端口获取一个Session对象
            LOG.debug("Session created.");
            if (ftpPassword != null) {
                session.setPassword(ftpPassword); // 设置密码
            }
        	Properties config = new Properties();  
        	config.put("StrictHostKeyChecking", "no");  
        	session.setConfig(config); // 为Session对象设置properties  
			session.setTimeout(deftimeout); // 设置timeout时间
			session.connect(); // 通过Session建立链接
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    }  
    
    public void closeChannel(Channel channel){
    	if (channel != null) 
            channel.disconnect();
    	if (session != null) 
    		session.disconnect();
    }
    public void closeChannel(){
        if (session != null) {
            session.disconnect();
        }
    }
    /**
     * @Description	建立ChannelSftp 上传，下载文件
     * 返回类型 ChannelSftp
     * @return
     * @注	文件夹的上传需要开发
     */
    public ChannelSftp getChannelSftp()  {
    	ChannelSftp channel=null;
		try {
			channel = (ChannelSftp) session.openChannel("sftp");// 打开SFTP通道
			channel.connect(); // 建立SFTP通道的连接
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return channel;
    }
    /**
     * @Description
     * 返回类型 String
     * @param cmd	Linux命令
     * @return
     * @throws Exception
     * @注	返回结果需要改进
     */
    public  String executeExecCmd(String cmd) throws Exception {
    	String result="";
    	ChannelExec channelExec = (ChannelExec)session.openChannel("exec");
        channelExec.setCommand(cmd);  
        channelExec.setInputStream(null);  
        channelExec.setErrStream(System.err);  
        channelExec.connect();
        InputStream in = channelExec.getInputStream();  
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.forName("utf-8")));  
        String buf = null;  
        while ((buf = reader.readLine()) != null){  
        	result +=buf;
        }  
        reader.close();
        channelExec.disconnect();  
        return result;
    } 
    
    /**
     * https://www.jianshu.com/p/2463dc3bb0a1
     * @Description 执行shell命令，返回执行结果
     * 返回类型 String
     * @param cmd	sell命令
     * @return
     * @throws Exception
     * @注	执行脚本 返回结果
     */
    public String executeShellCmd(String cmd) throws Exception {
    	//保存返回结果
	    ByteArrayOutputStream retOut = new ByteArrayOutputStream();
	    
	    ChannelShell channelShell = (ChannelShell)session.openChannel("shell");// 打开SFTP通道
	    PipedInputStream cmdIn = new PipedInputStream();
	    PipedOutputStream cmdOut = new  PipedOutputStream(cmdIn); 
	    channelShell.setInputStream(cmdIn);
	    channelShell.setOutputStream(retOut);
	    channelShell.connect(30000);// 建立SFTP通道的连接
	    cmdOut.write(cmd.getBytes()); 
	    cmdOut.flush();
	    //缓冲时间和执行时间
	    Thread.sleep(2000);
	    cmdOut.close();
	    cmdIn.close();
	   
	   // String retMsg = retOut.toString();
	    String retMsg=new String(retOut.toByteArray(),"utf-8");
	    retOut.close();
	    channelShell.disconnect(); //关闭通道
	    return  retMsg;
    }

}
