import java.io.*;
import java.util.*;

public class miningRule
{
	public static String transaFile;
	public static int numItems = 0;
	public static int numTransactions = 0;
	public static ArrayList<int[]> itemsets = new ArrayList<int[]>();
	public static ArrayList<String> transacData = new ArrayList<String>();
	public static int limit;
	public static double minSup;
	public static int itemsetNumber = 1;

	public static void main(String[] args)
	{
			miningRule driver = new miningRule();
			driver.readFile(args);
			long start = System.currentTimeMillis();

			int setCounter = 0;

			while(itemsetNumber <= limit)
			{
				driver.calculate();
				itemsetNumber++;
				if(itemsets.size() != 0 && itemsetNumber != limit+1)
				{
					driver.subsetGen();
				}


			}
			long end = System.currentTimeMillis();
			System.out.println(((double)(end-start)/1000) + " seconds.");
	}

	public void readFile(String[] args)
	{
		transaFile = args[0];
		minSup = Double.valueOf(args[1]);
		limit = Integer.parseInt(args[2]);

		try
		{

			BufferedReader input = new BufferedReader(new FileReader(transaFile));
			while(input.ready())
			{
				String current = input.readLine();
				transacData.add(current);
				numTransactions++;

				StringTokenizer tokenizer = new StringTokenizer(current," ");
				while(tokenizer.hasMoreTokens())
				{
					int x = Integer.parseInt(tokenizer.nextToken());

					if(x+1 > numItems)
					{
						numItems = x+1;
					}
				}
			}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}

		System.out.println(numItems);

		for(int i=0; i<numItems; i++)
		{
				int[] cand = {i};
				itemsets.add(cand);
		}

	}

	public void calculate()
	{
	        ArrayList<int[]> frequentCandidates = new ArrayList<int[]>();
	        int count[] = new int[itemsets.size()];
			boolean flag;

	        for (int i = 0; i < numTransactions; i++) {
	                String line = transacData.get(i);

					String[] currentLine = line.split(" ");
					boolean[] trans = new boolean[numItems];
					for(String s : currentLine)
					{
						trans[Integer.parseInt(s)] = true;
					}

					for (int j = 0; j < itemsets.size(); j++) {
	                        flag = true;
	                        int[] cand = itemsets.get(j);
	                        for (int c : cand) {
	                                if (trans[c] == false) {
	                                        flag = false;
	                                        break;
	                                }
	                        }
	                        if (flag) {
	                                count[j]++;
	                        }
	                }

	        }


	        for (int i = 0; i < itemsets.size(); i++) {
	                if ((count[i] / (double) (numTransactions)) >= minSup) {
	                        //System.out.println(Arrays.toString(itemsets.get(i))+" "+count[i]);
	                        frequentCandidates.add(itemsets.get(i));
	                }
	        }
			System.out.println("For itemsetnumber "+itemsetNumber+", found "+frequentCandidates.size());
	        itemsets = frequentCandidates;
	}

	public void subsetGen()
	{
	        System.out.println("===================================================");

	        HashMap<String, int[]> tempList = new HashMap<String, int[]>();

	        for(int i=0; i<itemsets.size(); i++)
	        {
	                for(int j=i+1; j<itemsets.size(); j++)
	                {
	                        int[] xArray = itemsets.get(i);
	                        int[] yArray = itemsets.get(j);

	                        int [] candidate = new int[itemsetNumber+1];

	                        for(int subString =0; subString < yArray.length; subString++)
	                        {
								ArrayList<Integer> xList = new ArrayList<Integer>();
								xList = toArrayList(xArray);

								if(!xList.contains(yArray[subString]))
								{
									xList.add(yArray[subString]);
									candidate = xList.stream().mapToInt(Integer::intValue).toArray();
									Arrays.sort(candidate);
									tempList.put(Arrays.toString(candidate),candidate);
								}

	                        }
	                }
	        }
	        itemsets = new ArrayList<int[]>(tempList.values());
	}

	public ArrayList<Integer> toArrayList(int[] current)
	{
		ArrayList<Integer> output = new ArrayList<Integer>();
		for(int x : current)
		{
			output.add(x);
		}
		return output;
	}
}
