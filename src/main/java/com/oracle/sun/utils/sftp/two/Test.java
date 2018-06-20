package com.oracle.sun.utils.sftp.two;

import com.jcraft.jsch.Session;

public class Test {

	public static void main(String[] args) throws Exception {
		
		//test1();
		test2();
	}
	/**
	 * 
	 * @Description runCmd测试
	 * 返回类型 void
	 * @throws Exception
	 * @注
	 */
	private static void test1() throws Exception {
		VersouSshUtil ve=new VersouSshUtil();
		String patterns="git";
		String cmd = "ls -ltr /usr/soft/git-2.16.0/|grep "+patterns+"|tail -1|awk {'print $NF'}\n";
		//执行的命令，这里为查找目录下最近更新的文件名带有patterns的文件
		ve.runCmd(cmd, "utf-8");
	}
	/**
	 * @Description getLatestFileName测试 
	 * 返回类型 void
	 * @throws Exception
	 * @注
	 */
	private static void test2() throws Exception {
		VersouSshUtil ve=new VersouSshUtil();
		String patterns="git";
		String cmd = "ls -ltr /usr/soft/git-2.16.0/|grep "+patterns+"|tail -1|awk {'print $NF'}\n";
		//执行的命令，这里为查找目录下最近更新的文件名带有patterns的文件
		String retMsg=ve.getLatestFileName(cmd);
		System.out.println(retMsg);
		//得到的结果包含很多行，需要处理
	    String[] retArr= retMsg.split("\n");
		System.out.println(retArr[retArr.length-2].trim());
	}
}
