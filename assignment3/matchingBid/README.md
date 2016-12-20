# Mining Big Data
Assignment 2

# Authors
Hsiayu Chiang (a1675750) : a1675750@student.adelaide.edu.au

Zi Yang Pang (a1681939) : a1681939@student.adelaide.edu.au

# File Structure

* Ski Buying : `mining_big_data/assignment3/ski-buying`

* Matching Bids : `mining_big_data/assignment3/matchingBid`

* Report (including Exercise 1 and explanation of Exercise 2 ) : `mining_big_data/assignment3/report.pdf`

## Bid Matching

We are using the second variant where we remove duplicates from both the document and the bids. We included the bid.csv file as a template bid file for you to run the program and also the google 10000 words file is named as 10000.txt.

To run the textbook bid matching algorithm for Exercise 2, change directory to the matchingBid folder and compile the java `javac matchingBid.java`

To run the our own implementation of the bid matching algorithm for Exercise 2, change directory to the matchingBid folder and compile the java

`javac implementTest.java`

Once compiled you can run it on its own with the following command for all the bids and pages for the textbook matching algorithm.

`java matchingBid bid.csv <google 10000 word text file> <bids folder directory> <document folder directory>`

Once compiled you can run it on its own with the following command for all the bids and pages for our own matching algorithm.

`java implementTest bid.csv <google 10000 word text file> <bids folder directory> <document folder directory>`

To output the file into a csv file you could pipe the output to a csv file using the following command for any of the two programs :

`java matchingBid bid.csv <google 10000 word text file> <bids folder directory> <document folder directory> > log.csv`

The final output is in the following format :

document name , matched keywords , price , running time in milliseconds
Final total running time in milliseconds to match all the documents

You can then view the output csv file in  Excel (Microsoft) / LibreOffice Calc (Linux) / Numbers (Mac)

log.csv is the log file for the algorithm from the textbook and the log1.csv is the log file for our own algorithm
