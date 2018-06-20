package com.oracle.sun.utils.sftp.two;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.Charset;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
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
  //  private ChannelExec channelExec;  
    private Session session = null;  
    private static int detimeout = 60000;  
    private static final Logger LOG = Logger.getLogger(VersouSshUtil.class);  
    /**
     * @Description
     * 返回类型 Session
     * @param timeout
     * @return
     * @throws Exception
     * @注 没有用构造器方式实例化，后面session自己建ChannelExec或channelShell了
     */
    public VersouSshUtil() throws Exception {
        LOG.info("尝试连接到....host:" + SFTPConstants.SFTP_REQ_USERNAME + ",username:" + SFTPConstants.SFTP_REQ_HOST + ",password:" + ",port:" + SFTPConstants.SFTP_DEFAULT_PORT);  
        JSch jsch = new JSch(); // 创建JSch对象  
        session = jsch.getSession(SFTPConstants.SFTP_REQ_USERNAME, SFTPConstants.SFTP_REQ_HOST,SFTPConstants.SFTP_DEFAULT_PORT); // 根据用户名，主机ip，端口获取一个Session对象  
        session.setPassword(SFTPConstants.SFTP_REQ_PASSWORD); // 设置密码  
        Properties config = new Properties();  
        config.put("StrictHostKeyChecking", "no");  
        session.setConfig(config); // 为Session对象设置properties  
        session.setTimeout(detimeout); // 设置timeout时间
        session.connect(); // 通过Session建立链接  
       
    }  
    public void close(Session session) {  
        session.disconnect();  
    } 
    /**
     * 
     * @Description
     * 返回类型 void
     * @param session
     * @param cmd
     * @param charset
     * @throws Exception
     * @注
     */
    public void runCmd(String cmd, String charset) throws Exception {
    	ChannelExec channelExec; //可以作为参数传入
    	
        channelExec = (ChannelExec)session.openChannel("exec");
        channelExec.setCommand(cmd);  
        channelExec.setInputStream(null);  
        channelExec.setErrStream(System.err);  
        channelExec.connect();
        InputStream in = channelExec.getInputStream();  
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.forName(charset)));  
        String buf = null;  
        while ((buf = reader.readLine()) != null)  
        {  
            System.out.println(buf);  
        }  
        reader.close();
        channelExec.disconnect();  
    }  
    /**
     * https://www.jianshu.com/p/2463dc3bb0a1
     * @Description 执行shell命令，返回执行结果
     * 返回类型 String
     * @param patterns
     * @return
     * @throws Exception
     * @注
     */
    public String getLatestFileName(String cmd) throws Exception {
	    ByteArrayOutputStream retOut = new ByteArrayOutputStream();
	    ChannelShell channelShell = (ChannelShell)session.openChannel("shell");
	
	    PipedInputStream cmdIn = new PipedInputStream();
	    PipedOutputStream cmdOut = new  PipedOutputStream(cmdIn); 
	    channelShell.setInputStream(cmdIn);
	    channelShell.setOutputStream(retOut);
	
	    channelShell.connect(30000);
	    cmdOut.write(cmd.getBytes()); 
	    cmdOut.flush();
	    //缓冲时间和执行时间
	    Thread.sleep(2000);
	    cmdOut.close();
	    cmdIn.close();
	    String retMsg = retOut.toString();
	    retOut.close();
	    channelShell.disconnect();
	    return  retMsg;
    }

}
