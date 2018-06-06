package com.oracle.sun.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Properties;

public class PropUtil {
	//private Logger log=
	
	 /**
    * 读Properties文件
    * @path 路径  eg :<br>"src/log4j.properties"---项目/src目录下;
    *             <br> "config/projectPath.properties"---项目/config 下
    *
    */
   protected static Properties getProperties(String path) {
       Properties prop = new Properties(); InputStream in = null;
       try {
         //根目录为 src
         //in =Tools.class.getClassLoader().getResourceAsStream(path);
         //根目录为项目下(不需要写"/")
           in = new BufferedInputStream(new FileInputStream(path));
           //prop.load(in);//直接这么写，如果properties文件中有汉子，则汉字会乱码。因为未设置编码格式。
           prop.load(new InputStreamReader(in, "utf-8"));
       } catch (Exception e) {
           System.out.println(e.getMessage());
       } finally {
           if (in != null) {
               try {
                   in.close();
               } catch (IOException e) {
                   System.out.println(e.getMessage());
               }
           }
       }
       return prop;
   }
   /**
    * @param path 路径  eg :<br>"src/log4j.properties"---项目/src目录下;
    *             <br> "config/projectPath.properties"---项目/config 下
    * @param key 键值对 中的key
    * @return
    */
   public static String getProperties(String path,String key) {
     Properties prop =getProperties(path);
    return prop.getProperty(key);
   }
   
   /**
    * 写Properties文件
    * @param path 路径
    * @param key  
    * @param value
    */
   protected static void writePropertiesFile(String path,String key,String value) {
	   Properties prop=new Properties();
       prop.setProperty(key, value);
       FileOutputStream oFile = null;
       try {
           //保存属性到b.properties文件
           oFile = new FileOutputStream(path, true);//true表示追加打开,false每次都是清空再重写
           //prop.store(oFile, "此参数是保存生成properties文件中第一行的注释说明文字");//这个会两个地方乱码
           //prop.store(new OutputStreamWriter(oFile, "utf-8"), "汉字乱码");//这个就是生成的properties文件中第一行的注释文字乱码
           prop.store(new OutputStreamWriter(oFile, "utf-8"), "The New properties file");

       } catch (Exception e) {
         //log.info("Tools "+e.getMessage());
           System.out.println(e.getMessage());
       } finally {
           if (oFile != null) {
               try {
                   oFile.close();
               } catch (IOException e) {
                  //log.info("Tools "+e.getMessage());
                   System.out.println(e.getMessage());
               }
           }
       }
   }

   
}
