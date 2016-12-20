import java.util.*;
import java.io.*;

public class matchingBid
{
	static ArrayList<String> orderArray = new ArrayList<String>();
	static HashMap<String,Double> priceArray = new HashMap<String,Double>();
	static ArrayList<String> originalBids = new ArrayList<String>();

	static double maxValue = 0.00;

	public static void main(String[] args)
	{

		long totalStart = System.currentTimeMillis();
		if(args.length != 4)
		{
			System.out.println("Invalid arguments length");
			System.out.println("Program Usage :");
			System.out.println("java matchingBid <bid file> <google 10000 file> <bids folder directory> <document folder directory>");
			System.exit(0);
		}
		String bidFile = args[0];
		String orderFile = args[1];

		String bidsFolder = args[2];
		String pagesFolder = args[3];



		orderArray = processOrder(orderFile);
		HashMap<String,ArrayList<ArrayList<String>>> bid = processBid(bidFile);

		final File folder = new File(bidsFolder);
		subsequentRun(folder);

		final File webFolder = new File(pagesFolder);

		for (final File fileEntry : webFolder.listFiles())
		{
			long start = System.currentTimeMillis();
			ArrayList<String> document = processDocument(fileEntry.getPath());
			ArrayList<ArrayList<String>> matched = matchWords(document,bid);

			Set<ArrayList<String>> ndMatched = new HashSet<ArrayList<String>>();
			ndMatched.addAll(matched);

			Double totalValue = 0.00;
			StringBuilder sb = new StringBuilder();

			for(ArrayList<String> keywords : ndMatched)
			{
				String key = makeKey(keywords);

				totalValue += priceArray.get(key);
				if(priceArray.get(key) != 0.00)
				{
					sb.append(key+" ;");
				}
			}

			System.out.print(fileEntry.getPath()+","+totalValue+","+sb.toString()+","+(System.currentTimeMillis()-start));
			System.out.println();

			bid = resetTable(bid);
			//
		}

		System.out.println("=========================");
		System.out.println(System.currentTimeMillis() - totalStart);

	}


	/**
	 * Processes the document and removing any duplicates
	 * @param  transaFile [The incoming transafile from the command line]
	 * @return            [Returns the arraylist of all the tokens processed]
	 */
	public static ArrayList<String> processDocument(String transaFile)
	{
		ArrayList<String> outputArray = new ArrayList<String>();
		try
		{
			File file = new File(transaFile);
        	FileInputStream fis = new FileInputStream(file);
        	byte[] data = new byte[(int) file.length()];
        	fis.read(data);
        	fis.close();

        	String str = new String(data, "UTF-8");
        	String resultLine = str.replaceAll("[^a-zA-Z0-9]"," ").toLowerCase();

			String[] testLine = resultLine.split("\\s+");
			outputArray = sortOrder(testLine,orderArray);
		}
		catch(Exception e)
		{
			System.out.println(e);
		}


		return outputArray;
	}

	/**
	 * Process the order from the google 10000 text file
	 * @param  orderFile [The incoming order file from command line]
	 * @return           [The arraylist of processed order]
	 */
	public static ArrayList<String> processOrder(String orderFile)
	{
		ArrayList<String> outputArray = new ArrayList<String>();
		try
		{

			BufferedReader input = new BufferedReader(new FileReader(orderFile));
			while(input.ready())
			{
				String current = input.readLine();
				outputArray.add(current);
			}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}

		Collections.reverse(outputArray);
		return outputArray;
	}

	/**
	 * Function to process the incoming bid file by each bidder
	 * @param  bidFile [The incoming bid file from command line]
	 * @return         [Arraylist of the processed bid]
	 */
	public static HashMap<String,ArrayList<ArrayList<String>>> processBid(String bidFile)
	{
		HashMap<String,ArrayList<ArrayList<String>>>  outputArray = new HashMap<String,ArrayList<ArrayList<String>>> ();
		try
		{
			File file = new File(bidFile);
			FileInputStream fis = new FileInputStream(file);
			byte[] data = new byte[(int) file.length()];
			fis.read(data);
			fis.close();

			String str = new String(data, "UTF-8");
			String[] splitStr = str.split("\n");

			for(int i =1; i < splitStr.length; i++)
			{
				String current = splitStr[i];
				// current price
				String[] temp = current.split(",");
				String[] splitBid = temp[0].split("\\s+");
				ArrayList<String> arrangeBid = sortOrder(splitBid,orderArray);

				//add price to global priceArray
				priceArray.put(makeKey(arrangeBid),Double.valueOf(temp[2]));
				//Add to originalBids for easier reset
				originalBids.add(makeKey(arrangeBid));

				ArrayList<ArrayList<String>> tempValue = outputArray.get(arrangeBid.get(0));

				if(tempValue != null)
				{
					tempValue.add(arrangeBid);
				}
				else
				{
					ArrayList<ArrayList<String>> valueList = new ArrayList<ArrayList<String>>();
					valueList.add(arrangeBid);
					outputArray.put(arrangeBid.get(0),valueList);
				}

			}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		return outputArray;
	}


	/**
	 * Sort the incoming array list with the google 10000 keywords order
	 * @param  [inputArray to be sorted]
	 * @param  [the ranking array from the 10000.txt]
	 */
	public static ArrayList<String> sortOrder(String[] inputArray, ArrayList<String> orderArray)
	{
		Set<String> noDuplicates = new HashSet<String>(Arrays.asList(inputArray));
		ArrayList<String> outputArray = new ArrayList<String>();

		ArrayList<Integer> dict = new ArrayList<Integer>();
		ArrayList<String> lexi = new ArrayList<String>();

		for(String s : noDuplicates)
		{
			if(orderArray.contains(s))
			{
				dict.add(orderArray.indexOf(s));
			}
			else{
				lexi.add(s);
			}
		}

		Collections.sort(lexi);
		Collections.sort(dict);

		outputArray.addAll(lexi);

		for(Integer s : dict)
		{
			outputArray.add(orderArray.get(s));
		}

		return outputArray;

	}

	/**
	 * [matchWords description]
	 * @param   [description]
	 * @param   [description]
	 * @param   [description]
	 * @return  [description]
	 */
	public static ArrayList<ArrayList<String>> matchWords (ArrayList<String> document , HashMap<String,ArrayList<ArrayList<String>>> bid)
	{
		ArrayList<ArrayList<String>> fullyMatched = new ArrayList<ArrayList<String>>();
		for(String words : document)
		{
			ArrayList<ArrayList<String>> current = bid.get(words);
			if(current != null)
			{
				for(ArrayList<String> value : current)
				{
					Integer status = value.indexOf(words);
					status++;

					if(status == value.size())
					{
						fullyMatched.add(value);
					}
					else
					{
						ArrayList<ArrayList<String>> tempArray = bid.get(value.get(status));

						if(tempArray != null)
						{
							bid.get(value.get(status)).add(value);
							bid.remove(value.get(status-1));
						}
						else
						{
							ArrayList<ArrayList<String>> insertArray = new ArrayList<ArrayList<String>>();
							insertArray.add(value);
							bid.put(value.get(status),insertArray);
						}
					}

				}
			}
		}

		return fullyMatched;
	}

	/**
	 * Run subsequent bids to get the highest price
	 * @param File [The bids file are read to update and give the best price]
	 */
	public static void subsequentRun (final File folder)
	{
		for (final File fileEntry : folder.listFiles()) {
			try
			{
				File file = new File(fileEntry.getPath());
				FileInputStream fis = new FileInputStream(file);
				byte[] data = new byte[(int) file.length()];
				fis.read(data);
				fis.close();

				String str = new String(data, "UTF-8");
				String[] splitStr = str.split("\n");

				for(int i =1; i < splitStr.length; i++)
				{
					String current = splitStr[i];
					String[] temp = current.split(",");

					String[] splitBid = temp[0].split("\\s+");
					ArrayList<String> arrangeBid = sortOrder(splitBid,orderArray);

					Double currentPrice = Double.valueOf(temp[2]);
					String key = makeKey(arrangeBid);

					if(priceArray.get(key) < currentPrice)
					{
						priceArray.put(key,currentPrice);
					}
				}
			}
			catch(Exception e)
			{

			}
		}

	}

	/**
	 * Creates a formal key that's consistent with all the other keys despite removing duplicate
	 * @param  inputArray [The keyword input array]
	 * @return            [a string of the keyword separated by space]
	 */
	public static String makeKey(ArrayList<String> inputArray)
	{
		StringBuilder sb = new StringBuilder();

		for(String s : inputArray)
		{
			sb.append(s+" ");
		}
		return sb.toString();
	}

	/**
	 * Reset the hashtable without accessing file
	 * @param   [the original HashTable]
	 * @return  [new reseted hashtable]
	 */
	public static HashMap<String,ArrayList<ArrayList<String>>> resetTable(HashMap<String,ArrayList<ArrayList<String>>> inputHash)
	{
		inputHash = new HashMap<String,ArrayList<ArrayList<String>>>();

		for(String current : originalBids)
		{

			ArrayList<String> arrangeBid = new ArrayList<String>(Arrays.asList(current.split("\\s+")));
			ArrayList<ArrayList<String>> tempValue = inputHash.get(arrangeBid.get(0));

			if(tempValue != null)
			{
				tempValue.add(arrangeBid);
			}
			else
			{
				ArrayList<ArrayList<String>> valueList = new ArrayList<ArrayList<String>>();
				valueList.add(arrangeBid);
				inputHash.put(arrangeBid.get(0),valueList);
			}
		}

		return inputHash;

	}



}
