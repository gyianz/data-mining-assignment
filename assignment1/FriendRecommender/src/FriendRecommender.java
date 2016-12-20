import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class FriendRecommender
{
	public static class Map1 extends Mapper<LongWritable, Text, Text, IntWritable>
	{
		private final static IntWritable minus1 = new IntWritable(-1);
		private final static IntWritable zero = new IntWritable(0);
	    private final static IntWritable one = new IntWritable(1);

	    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException
	    {
	    	StringTokenizer tokenizer = new StringTokenizer(value.toString(), " \t,");

	    	// exit if we hit a blank line
	    	if (! tokenizer.hasMoreTokens())
	    		return;

	    	int user = Integer.parseInt(tokenizer.nextToken());

	    	ArrayList<Integer> friends =  new ArrayList<Integer>();

	    	while (tokenizer.hasMoreTokens())
			{
	    		int friend = Integer.parseInt(tokenizer.nextToken());
	    		friends.add(friend);
			}
	    	
	    	if(friends.size() == 0)
	    	{
	    		String noFriends = Integer.toString(user)+" "+Integer.toString(user);
	    		context.write(new Text(noFriends), one);
	    	}

	    	friends.add(user);
	    	Collections.sort(friends);
	    	
	    	for(int x = 0; x < friends.size();x++)
	    	{
	    		for(int y = x+1; y<friends.size(); y++)
	    		{
	    			String output = Integer.toString(friends.get(x))+" "+ Integer.toString(friends.get(y));
	    			if(friends.get(x) == user || friends.get(y) == user)
	    			{
	    				context.write(new Text(output),minus1);
	    			}
	    			else
	    				context.write(new Text(output),one);
	    		}
	    	}
	    }
	}

	public static class Reduce1 extends Reducer<Text, IntWritable, Text, IntWritable>
	{
	    public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException
	    {
	    	int mutualFriends = 0;

	        for (IntWritable val : values)
	        {
	        	if (val.get() == -1)
	        	{
	        		return;
	        	}
	        		
	        	mutualFriends += val.get();
	        }

	        context.write(key, new IntWritable(mutualFriends));
	    }
	 }

	public static class Map2 extends Mapper<LongWritable, Text, IntWritable, Text>
	{
	    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException
	    {
	    	StringTokenizer tokenizer = new StringTokenizer(value.toString());

	    	if (! tokenizer.hasMoreTokens())
	    	{
	    		return;
	    	}
	    		
	    	int user1 = Integer.parseInt(tokenizer.nextToken());
	    	int user2 = Integer.parseInt(tokenizer.nextToken());
	    	int mutualFriends = Integer.parseInt(tokenizer.nextToken());
	    	
	    	if(user1 == user2)
	    	{
	    		String x = user1+","+mutualFriends;
	    		context.write(new IntWritable(user1), new Text(x));
	    		return;
	    	}

	    	String s1 = user2 + "," + mutualFriends;
	    	context.write(new IntWritable(user1), new Text(s1));

	    	String s2 = user1 + "," + mutualFriends;
	    	context.write(new IntWritable(user2), new Text(s2));
	    }
	}

	public static class Reduce2 extends Reducer<IntWritable, Text, IntWritable, Text>
	{
	    public void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException
	    {
	    	int user = key.get();
	    	ArrayList<String> tempValue = new ArrayList<String>();
	    	
	    	for(Text val : values)
	    	{
	    		tempValue.add(val.toString());
	    	}
	    	
	        Collections.sort(tempValue, new Comparator<String>() {
	            @Override
	            public int compare(String o1, String o2) {
	            	int mutualValue = Integer.parseInt(o2.split(",")[1]) - Integer.parseInt(o1.split(",")[1]);
	            	if(mutualValue == 0)
	            	{
	            		return Integer.parseInt(o1.split(",")[0]) - Integer.parseInt(o2.split(",")[0]);
	            	}
	            	else
	            		return mutualValue;
	            }
	        });
	        StringBuilder sb = new StringBuilder();
	        sb.append("\t");
	        String prefix = "";
	        for(int i = 0; i < 10 && i < tempValue.size() ; i++)
	        {
	        	sb.append(prefix);
		        prefix = ",";
		        if(Integer.parseInt(tempValue.get(i).split(",")[0]) == user)
		        {
		        	context.write(new IntWritable(user), new Text("\t"));
		        	return;
		        }
	        	sb.append(tempValue.get(i).split(",")[0]);
	        }
	        
	        context.write(new IntWritable(user), new Text(sb.toString()));
	    }
	 }

	 public static void main(String[] args) throws Exception
	 {
	    Configuration conf = new Configuration();


	    Job job1 = new Job(conf, "FriendRecommender");
	    job1.setJarByClass(FriendRecommender.class);
	    job1.setOutputKeyClass(Text.class);
	    job1.setOutputValueClass(IntWritable.class);

	    job1.setMapperClass(Map1.class);
	    job1.setReducerClass(Reduce1.class);

	    job1.setInputFormatClass(TextInputFormat.class);
	    job1.setOutputFormatClass(TextOutputFormat.class);

	    FileInputFormat.addInputPath(job1, new Path(args[0]));
	    FileOutputFormat.setOutputPath(job1, new Path("IntermediateData"));

	    job1.waitForCompletion(true);


	    Job job2 = new Job(conf, "FriendRecommender2");
	    job2.setJarByClass(FriendRecommender.class);
	    job2.setOutputKeyClass(IntWritable.class);
	    job2.setOutputValueClass(Text.class);

	    job2.setMapperClass(Map2.class);
	    job2.setReducerClass(Reduce2.class);

	    job2.setInputFormatClass(TextInputFormat.class);
	    job2.setOutputFormatClass(TextOutputFormat.class);

	    FileInputFormat.addInputPath(job2, new Path("IntermediateData"));
	    FileOutputFormat.setOutputPath(job2, new Path(args[1]));

	    job2.waitForCompletion(true);
	 }
}