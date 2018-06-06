package com.oracle.sun.utils;

import java.io.File;
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
    //创建文件  如果没有

    //判断文件/夹是否已经存在
    /**
     *    创建文件最后一个是文件名 <br>
     *    默认不覆盖<br>
     *  eg: E:\\360Downloads\\src\\coms\\service\\user.txt  user.txt是文件夹名而非文件名
     * @param file
     * @throws IOException
     */
    public static void createDir(File file) throws IOException{
      if (!file.exists()) {
           file.mkdirs();
         }
    }
    /**
     * 创建文件最后一个是文件名 <br>
     * 默认不覆盖<br>
     * eg: E:\\360Downloads\\src\\coms\\service\\user  user是文件名而非文件夹
     * @param file
     * @throws IOException
     */
    public static void createFile(File file){
     if (!file.exists()) {
          file.getParentFile().mkdirs();
     }
     if(!file.isFile()){
          try {
                   file.createNewFile();
              } catch (IOException e) {
                   // TODO Auto-generated catch block
                   e.printStackTrace();
              }
     }
    }
    /**
     * 返回文件类型 小写
     * @param file  
     * @return	失败返回  ""	
     * eg:"E:\\360Downloads\\src\\com_1\\service\\user.TXT" 返回值:txt
     */
    public static String getFileType(File file){
     if(file.exists() && file.isFile()){
          String file_type = file.getName().substring(file.getName().indexOf(".")+1,file.getName().length());
          return file_type.toLowerCase();
     }
     return null;
    }
    /**
     * 获取文件名称 必须是文件类型
     * @param file      eg:E:\\360Downloads\\src\\com_1\\service\\user.TXT
     * @return  失败返回  null   eg:user
     */
    public static String getFileName(File file){
     if(file.exists() && file.isFile()){
      String name = file.getName().substring(0,file.getName().indexOf("."));
         return name;
     }
     return null;
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    //判断文件是否是某种类型   //TODO 大小写敏感
    public static Boolean isFileType(File file,String type){
     if(file.exists() && file.isFile()){
          if(file.getName().endsWith(type))
              return true;
     }
     return false;
    }
}