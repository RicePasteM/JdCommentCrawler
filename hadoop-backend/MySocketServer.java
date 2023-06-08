package com;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
 
//服务器类，用于处理最初的PortWaiter的创建任务以及向客户端发送消息。
class MySocketServer extends Thread{
    Socket client;
    ServerSocket server;
    BufferedReader br;
    BufferedWriter bw;
    InputStream is;
    OutputStream os;
    int port;
    MySocketServer(int port) {
        this.port = port;
        this.start();
    }
 
    @Override
    public void run() {
        super.run();
            try {
                server = new ServerSocket(port);
                System.out.println("- 服务已在端口 " + port + "上启动。\n");
                //从ServerSocket等待新连接的Socket。
                client = server.accept();
                System.out.println("- " + client.getInetAddress().getLocalHost() + " 已连接到服务。\n");
                is = client.getInputStream();
                os = client.getOutputStream();
                br = new BufferedReader(new InputStreamReader(is));
                bw = new BufferedWriter(new OutputStreamWriter(os));
                while(true) {
                    String newMsg = br.readLine();
                    if (newMsg != null) {
                    	System.out.println(">> " + newMsg + "\n");
                    	// 判断客户端发来需要爬取的信息来源
                    	if (newMsg.startsWith("#StartProgram")) {
                    		String[] params = newMsg.substring("#StartProgram".length()).split("#");
                    		String productId = params[0];
                    		int pageCount = Integer.parseInt(params[1]);
                    		JdCommentCrawler jcc = new JdCommentCrawler(productId, pageCount);
                    	}
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (e instanceof java.net.ConnectException)
                	System.out.println("- 服务启动失败，请重试或更换端口。" + "\n");
                else
                	System.out.println("- 与客户端的连接已断开，服务停止。\n");
            } finally {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
 
            }
    }
 
    public void sendMsg(String type,String msg) {
        System.out.println(msg);
        try {
            bw.write("{\"type\":\"" + type + "\",\"data\":\"" + msg + "\"}\n|");
            bw.flush();
            System.out.println("<< " + msg + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void sendMsg(String msg) {
        System.out.println(msg);
        try {
        	bw.write("{\"type\":\"" + "message" + "\",\"data\":\"" + msg + "\"}\n|");
            bw.flush();
            System.out.println("<< " + msg + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void close(){
    	try {
			server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}