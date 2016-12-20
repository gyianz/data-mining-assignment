
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class PageRank
{
	public static int TotalNodes = 875713;
	//public static int TotalNodes = 43;
	public static float beta = 0.80f; //range from 0.8 to 0.9
	public static float v_init = 1.0f/TotalNodes; // the value of first vector
	public static int iteration = 10;
	public static int calc_first = 0;
	//public static HashMap<String, Float> v_map = new HashMap<String, Float>();
	public static Map<String, Float> v_map = new TreeMap<String, Float>();
	
	public static class Map1 extends Mapper<LongWritable, Text, Text, Text>
	{
		/*
		 * First Map will just emit source and destination of a node to next Reducer 
		 *  
		 */
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException
		{
	    	StringTokenizer tokenizer = new StringTokenizer(value.toString(), "");

	    	// exit if we hit a blank line
	    	if (! tokenizer.hasMoreTokens())
	    		return;
	    	
	    	
	    	while (tokenizer.hasMoreTokens())
	    	{
	    		String token = tokenizer.nextToken();	    		
	    		
	    		// ignore "#" character
	    		if(token.indexOf("#") < 0)
	    		{
	    			
					String[] s = token.split("\\s+");
					
					Text from = new Text(s[0]);
					Text to = new Text(s[1]);
					
					// output : <src, dest>
					context.write(from, to); 					
	    		}
	    	}
		}
	}
	
	public static class Reduce1 extends Reducer<Text, Text, Text, Text>
	{
		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException
		{				
			float degree = 0.0f;
			ArrayList<Integer> dest = new ArrayList<Integer>();		
			
			// Store src's out of degree
			for (Text val : values) {			

				dest.add(Integer.parseInt(val.toString()));
				degree++;
			}
			
			// There emits node's destination and its possibility rate (each node is 1 divided by its link).
			// The reason is that we want to send its dest as row instead of column.
			// When the value of matrix is zero then we know what column vector we should calculate for.
			for(int i = 0; i < dest.size(); i++)
			{
				Text matrix = new Text();
				Text combine = new Text();
				
				matrix = new Text(Float.toString((1.0f/degree)));	
					
				combine = new Text(dest.get(i).toString() + " " + matrix);	
					
				//output : <dest, matrix>
				context.write(key, combine);				
			}
		}
	}
	
	public static class Map2 extends Mapper<LongWritable, Text, Text, Text>
	{
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException
		{
			float mv = 0.0f;

			//System.out.println("map2 value = " + value.toString());

			String[] s = value.toString().split("\\s+");			
			
			Text src = new Text(s[0]);
			
			Text dest = new Text(s[1]);
			
			
			
			// When iteration is 1, the initial vector must be 1 divided by total nodes.
			// After that, a vector should be the node of vector computed by previous time.
			if(calc_first == 1)
			{
				//System.out.println("Calculate frist = " + v_init);
				mv = Float.parseFloat(s[2]) * v_init;
			}
			else
			{
				//System.out.println("src = " + src + " v_map = " + v_map.get(src.toString()));
				mv = Float.parseFloat(s[2]) * v_map.get(dest.toString());
			}
			
			//System.out.println("src : " + src + " dest :" + dest + " : " + "pr = " + pr );
			
			//ouput <dest, pr >
			context.write(new Text(dest), new Text(Float.toString(mv)));
		}
	}
	
	public static class Reduce2 extends Reducer<Text, Text, Text, Text>
	{
		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException
		{
			float PR = 0.0f;
			float pre_pr = 0.0f;
						
			//System.out.println("reduce2 key = " + key);			
			
			// add the linked node's pr values
			for (Text val : values) {				
				
				//System.out.println("reduce2 value = " + val.toString());
				pre_pr =  pre_pr + Float.parseFloat(val.toString());
			
			}
			
			//System.out.println("pre_pr = " + pre_pr);
			
			//calculate pagerank value in each linked node.
			PR = beta * (pre_pr) + (1-beta) * (1.0f/TotalNodes);
			
			//System.out.println("key = " + key.toString());
			
			//store its pr value in order to use it at next iteration.
			v_map.put(key.toString(), PR);
			
			//System.out.println("V: " + key + " PR = " + v_map.get(key.toString()));
			
			//output <linked node, pr>
			context.write(key, new Text(String.valueOf(PR)));
		}
	}

	public static void main(String[] args) throws Exception
	{		
		long startTime = System.currentTimeMillis();
		
		Configuration conf = new Configuration();
		
	    Job job1 = new Job(conf, "PageRank");
	    
	    // delete output folder if compile
	    File output = new File("output");
	    File MatrixData = new File("MatrixData");

	    deletefolder(output);
	    deletefolder(MatrixData);	 
	    
	    for(int i = 0; i < iteration; i++)
	    {
	    	File path = new File("output" + "_" + Integer.toString(i));
	    	deletefolder(path);
	    }
	    
	    job1.setJarByClass(PageRank.class);
	    job1.setOutputKeyClass(Text.class);
	    job1.setOutputValueClass(Text.class);

	    job1.setMapperClass(Map1.class);
	    job1.setReducerClass(Reduce1.class);

	    job1.setInputFormatClass(TextInputFormat.class);
	    job1.setOutputFormatClass(TextOutputFormat.class);

	    FileInputFormat.addInputPath(job1, new Path(args[0]));
	    FileOutputFormat.setOutputPath(job1, new Path("MatrixData"));
	    
	    job1.waitForCompletion(true);

	    String output_dir = args[1];
	    String output_ori = output_dir;
	    
	    /*
	     * The second MapReduce will do iteration declared as global.
	     * 
	     * This Map is constantly fed by original Matrix data which is generated 
	     * by the first MapReduce.
	     */
	    calc_first = 1; 
	   for(int i = 0; i < iteration; i++)
	    {
		    Job job2 = new Job(conf, "PageRank2");
		    
		    job2.setJarByClass(PageRank.class);
		    job2.setOutputKeyClass(Text.class);
		    job2.setOutputValueClass(Text.class);

		    job2.setMapperClass(Map2.class);
		    job2.setReducerClass(Reduce2.class);

		    job2.setInputFormatClass(TextInputFormat.class);
		    job2.setOutputFormatClass(TextOutputFormat.class);

		    FileInputFormat.addInputPath(job2, new Path("MatrixData"));
		    FileOutputFormat.setOutputPath(job2, new Path(output_dir));	    		    
		    
		    job2.waitForCompletion(true);
		    
		    output_dir = output_ori + "_" + Integer.toString(i);
		    
		    calc_first = 0;
	    }
	   
	   /*
	    *  Sort final result and pick its TOP10 up after computed.
	    */
	   HashMap<String, Float> v_map1 = new HashMap<String, Float>(v_map);
	   HashMap<String, Float> sortMap = sortByValues(v_map1);
	   
	   Set set2 = sortMap.entrySet();
	   Iterator iterator2 = set2.iterator();
	   
	   FileWriter fw = new FileWriter("sortResult.txt");
	   
	   while(iterator2.hasNext())
	   {
		   Map.Entry sortAll = (Map.Entry)iterator2.next();		   
		   String ws = sortAll.getKey() + ": " + sortAll.getValue() + "\n";
		   fw.write(ws);
	   }
	   
	   fw.close();
	   
	   Set set3 = sortMap.entrySet();
	   Iterator iterator3 = set3.iterator();
	   FileWriter fw1 = new FileWriter("Top10_PR.txt");
	   
	   for(int i = 0; i < 10; i++)
	   {

		   Map.Entry top = (Map.Entry)iterator3.next();		   
		   String top10 = top.getKey() + ": " + top.getValue() + "\n";  
		   fw1.write(top10);
	   }
	   
	   fw1.close();
	   
	    long endTime = System.currentTimeMillis();
	    
	    long totalTime = endTime - startTime;
	    
	    System.out.println("Exeuction time = " + totalTime);
	}
	
	public static HashMap sortByValues(HashMap map)
	{
		List list = new LinkedList(map.entrySet());
		
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o2)).getValue()).compareTo(((Map.Entry) (o1)).getValue());
			}
		});
		
		HashMap sortedHashMap = new LinkedHashMap();
		
		for(Iterator it = list.iterator(); it.hasNext();)
		{
			Map.Entry entry = (Map.Entry) it.next();
			sortedHashMap.put(entry.getKey(), entry.getValue());
		}
		
		return sortedHashMap;
	}
	
	public static void deletefolder(File path)
	{
		if(path.exists())
		{
		    String[] entries = path.list();
		    for(String s: entries)
		    {
		    	File currentFile = new File(path.getPath(), s);
		    	currentFile.delete();
		    }
		    path.delete(); 
		}
	}
}