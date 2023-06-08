package com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;


public class JdCommentCrawler {
	static int port = 43151;
	static MySocketServer server;
	
	public static void main(String[] args){
		server = new MySocketServer(port);
	}
	
	public JdCommentCrawler(String productId, int pageCount){
		ArrayList<String> comments = JdComment.getComments("https://club.jd.com/comment/productPageComments.action?productId=" + productId + "&score=0&sortType=5&pageSize=10", pageCount, server);
		server.sendMsg("STEP 1 评论爬取完成");
		long taskId = System.currentTimeMillis();
        // 写入爬取的所有评论到HDFS
		server.sendMsg("STEP 2 写入爬取的评论到HDFS...");
		try {
			FileSystem hdfs;
			Configuration conf=new Configuration();
			conf.set("fs.default.name", "hdfs://master:9000");
			hdfs = FileSystem.get(conf);
	        Path inFile =new Path("/MRDataClean/server_cache/" + taskId);
	        FSDataOutputStream outputStream = hdfs.create(inFile);
	        for(int i = 0 ; i < comments.size(); i ++){
	        	outputStream.writeUTF(comments.get(i).replaceAll("\n", "") + "\n");
	        }
	        outputStream.flush();
	        outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		server.sendMsg("STEP 2 HDFS写入完成");
		server.sendMsg("STEP 3 开始分词...");
		try {
			MyIKAnalyzer.run(taskId, server);
		} catch (ClassNotFoundException | IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		server.sendMsg("STEP 4 分词完成");
		String result = "";
		  Path path = new Path("hdfs://master:9000/MRDataClean/server_cache/out/" + taskId + "/part-r-00000");
		  Configuration configuration = new Configuration();
		  FSDataInputStream fsDataInputStream = null;
		  FileSystem fileSystem = null;
		  BufferedReader br = null;
		  // 定义一个字符串用来存储文件内容
		  try {
		      fileSystem = path.getFileSystem(configuration);
		      fsDataInputStream = fileSystem.open(path);
		      br = new BufferedReader(new InputStreamReader(fsDataInputStream));
		      String str2;
		      while ((str2 = br.readLine()) != null) {
		          // 遍历抓取到的每一行并将其存储到result里面
				  server.sendMsg("result", str2.replace("	", " "));
			  }
		  } catch (IOException e) {
		      e.printStackTrace();
		  }
		server.sendMsg("STEP 5 结果传送完成");
		server.sendMsg("end","");
		server.close();
		server = new MySocketServer(port);
	}
}
