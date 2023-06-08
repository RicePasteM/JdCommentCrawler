package com;
import java.util.ArrayList;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.alibaba.fastjson.*;


/**
 * @Description: x东商品评论爬取
 * @Author: Am0xil
 * @Date: 2020/7/26
 **/
public class JdComment {
    public static ArrayList<String> getComments(String url, int pageCount, MySocketServer server) {
        // url 表示请求的地址，里面的参数值可以对照前面的参数解释自行替换
//        String url = "https://club.jd.com/comment/productPageComments.action?productId=100015183759&score=0&sortType=5&page=0&pageSize=10";
        ArrayList<String> comments = new ArrayList<>();
        for(int page = 0; page < pageCount; page ++){
        	server.sendMsg("爬取商品评论中[" + new Integer(page + 1).toString() + "/" + new Integer(pageCount).toString() + "]");
            // 创建 HttpClient 对象，用于发送请求
            CloseableHttpClient httpClient = HttpClients.createDefault();
            // 因为使用的是 GET 请求，所以创建一个 HttpGet 对象
            HttpGet httpGet = new HttpGet(url + "&page=" + new Integer(page).toString());
            // 写入请求头参数
            httpGet.setHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.61 Safari/537.36");
            // 创建一个 HttpResponse 用于接受请求结果
            CloseableHttpResponse response = null;
        	try {
                // 发送请求
                response = httpClient.execute(httpGet);
                // 将返回的结果转换为 JSONObject 类型，便与解析
                JSONObject object = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
                // 因为返回的结果中包含 10 条评论，因此将所有的评论存储到 JSONArray 对象中
                JSONArray rawComment = JSONArray.parseArray(String.valueOf(object.get("comments")));
                // 对 JSONArray 对象进行遍历，拿到所有的评论，其他的信息可根据字段名自行捕获
                for(int i = 0 ; i < rawComment.size() ; i ++ ){
                	String comment = ((JSONObject)rawComment.get(i)).get("content").toString();
                	if(!comment.equals("此用户未填写评价内容")){
                		System.out.println(comment);
                    	comments.add(comment);
                    	System.out.print("\n");
                	}
                }
            } catch (Exception e) {
                // 请求异常捕获
                System.out.println("发送请求失败，请检查您的网络");
            } finally {
                // 请求完毕，关闭 Http 对象
                if (response != null) {
                    try {
                        response.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                try {
                    httpClient.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return comments;
    }
}
