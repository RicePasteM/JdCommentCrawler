# JdCommentCrawler
JdCommentCrawler - 基于Hadoop与Electron的京东商品评论词云统计系统

合肥工业大学软件工程专业《云计算、大数据技术与应用》课程综合设计报告。
爬取京东或淘宝某一商品的评论1000条，统计词频（使用MapReduce或HBase或Hive），并以词云的方式可视化呈现，最后设计为一套可以操作的系统。
项目采用Electron+Hadoop技术栈实现，前后端使用Socket进行通讯。

部署要求：
1. Hadoop版本：>= 3.2.2
2. Node.js版本：16.20.0
3. Electron版本：见package.json

项目截图：

![Alt text](http://img.codesocean.top/image/1686251172870)

![Alt text](http://img.codesocean.top/image/1686251236618)

![Alt text](http://img.codesocean.top/image/1686251106759)

![Alt text](http://img.codesocean.top/image/1686251174913)
