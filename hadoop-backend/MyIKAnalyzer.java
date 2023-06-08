package com;


import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import java.io.Serializable;
import java.io.StringReader;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class MyIKAnalyzer implements Serializable{

    private static final long serialVersionUID = 1L;
    
    private static MySocketServer myServer;

    // 分词
    public  String splitWords(String line){
    	String result = "";
    	IKAnalyzer analyzer = new IKAnalyzer(true);
    	try {
			TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(line));
			tokenStream.addAttribute(CharTermAttribute.class);
			StringBuilder sb = new StringBuilder();
			while (tokenStream.incrementToken()) {
				CharTermAttribute charTermAttribute = tokenStream.getAttribute(CharTermAttribute.class);
				//System.out.print(charTermAttribute.toString() + "|");
				sb.append(charTermAttribute.toString());
				sb.append("\t");
			}
			result = sb.toString();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
    	analyzer.close();
    	return  result;
    }

	public static void run(long taskId, MySocketServer server) throws IOException,
			ClassNotFoundException, InterruptedException {
		myServer = server;
		Job job = Job.getInstance();
		job.setJobName("Sales");
		// 类
		job.setJarByClass(MyIKAnalyzer.class);
		job.setMapperClass(mapp.class);
		job.setReducerClass(redu.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		// 输入路径和输出路径
		Path input_file = new Path(
				"hdfs://master:9000/MRDataClean/server_cache/" + taskId);
		Path output_file = new Path("hdfs://master:9000/MRDataClean/server_cache/out/" + taskId);
		FileInputFormat.addInputPath(job, input_file);
		FileOutputFormat.setOutputPath(job, output_file);
		// 执行完成后退出
		job.waitForCompletion(true);
		return;
	}

	public static class mapp extends Mapper<Object, Text, Text, IntWritable> {
		public static final IntWritable one = new IntWritable(1);
		public static Text word = new Text();

		@Override
		protected void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			// 输入处理
			// 数据输入
			String line = value.toString();
            // 分词
            IKAnalyzer analyzer = new IKAnalyzer(true);
            try {
                TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(line));
                tokenStream.addAttribute(CharTermAttribute.class);
                while (tokenStream.incrementToken()) {
                    CharTermAttribute charTermAttribute = tokenStream.getAttribute(CharTermAttribute.class);
                    System.out.print(charTermAttribute.toString() + "|");
                    myServer.sendMsg("word", charTermAttribute.toString());
                    word.set(charTermAttribute.toString());
                    context.write(word, one);
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
            analyzer.close();
		}
	}

	public static class redu extends
			Reducer<Text, IntWritable, Text, IntWritable> {
		private IntWritable res = new IntWritable();

		@Override
		protected void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			int result = 0;
			// 对每个输入，执行统计数量，加入到对应字典类型
			for (IntWritable value : values) {
				result += value.get();
			}
			res.set(result);
			context.write(key, res);
		}
	}
}