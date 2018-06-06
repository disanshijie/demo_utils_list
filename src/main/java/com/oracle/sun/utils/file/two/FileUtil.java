package com.oracle.sun.utils.file.two;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {
	
	 /**
     * 查找文件夹下是否有"Impl" 文件夹,如果没有 创建该文件夹;如果有返回改子文件夹
     * @param targetDir 文件夹
     * @param dirName 子文件夹的名称
     * @return 子文件夹路径
     */
    public static File createDirIfnone(File targetDir,String dirName ){
      // 检查impl目录是否村子啊，若不存在则创建该目录
        File dir = null;
        for( File temp : targetDir.listFiles()){
            if( temp != null && dirName.equals(temp.getName())){
                dir = temp;
                break;
            }
        }
        if( dir == null ){
            dir = new File(targetDir,dirName);
            dir.mkdir();
        }
        return dir;
    }
	
	/**
	 * 将一个文件夹下的内容复制到另一个文件夹下
	 * @param srcFile	源目录
	 * @param desFile	目标目录
	 * @throws IOException
	 */
	 protected static void copyFolder(File srcFile, File desFile) throws IOException  {
         if(srcFile.isDirectory()) {
             //是文件夹,首先在目标位置创建同名文件夹，然后遍历文件夹下的文件，进行递归调用copyFolder函数
             File newFolder = new File(desFile, srcFile.getName());
             newFolder.mkdir();
             File[] fileArray = srcFile.listFiles();
             for(File file : fileArray) {
                 copyFolder(file, newFolder);
             }
         }else{
             //是文件，直接copy到目标文件夹
             File newFile = new File(desFile, srcFile.getName());
             copyFile(srcFile, newFile);
         }
     }
	 /**
	  * 流的形式复制文件
	  * @param srcFile	源文件
	  * @param newFile	目标文件
	  * @throws IOException
	  */
     protected static void copyFile(File srcFile, File newFile) throws IOException {
         //复制文件到指定位置
         BufferedInputStream bis = new BufferedInputStream(new FileInputStream(srcFile));
         BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(newFile));
         byte[] b = new byte[1024];
         Integer len = 0;
         while((len = bis.read(b)) != -1) {
             bos.write(b, 0, len);
         }
         bis.close();
         bos.close();
     }
}
