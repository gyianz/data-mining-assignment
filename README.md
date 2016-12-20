# Mining Big Data
Assignment 2

# Authors
Hsiayu Chiang (a1675750) : a1675750@student.adelaide.edu.au

Ziyang Pang (a1681939) : a1681939@student.adelaide.edu.au

# File Structure

* PageRank : `mining_big_data/assignment2/PageRank` (only provided Standalone Mode)

* Frequent Itemsets : `mining_big_data/assignment2/apriori`

* Report (including Exercise 1 and briefly explantion of 2 and 3) : `mining_big_data/assignment2/report.pdf`

## PageRank

Before running this program, please download the dataset `web-google.txt` as we don't include the huge data in our file.

1. Open your eclipse

2. Import our project from `mining_big_data/assignment2/PageRank`. (File->Import->General->Exisiting Project into Workspace)

3. Right-click the project and select "Run as" to configure Java application.

4. Create a new launch configuration on Java Application and type "PageRank" in Main Class at right window.

5. Switch to Arguments, and type `web-Google.txt output` in the field of Program arguments.

6. Click the button of "Run" to run our program.

7. Check the output file in the current path (e.g. `~/mining_big_data/assignment2/PageRank/sortResult.txt`)

8. The files, `sortResult.txt` and `Top10_PR.txt`, are our final output.

Note that the iteration is 10 as default in this programe. Please change the variable `iteration` in the `src/PageRank.java` if you would like to do more iterations. The execution time will show up on the Console windown after ran our PageRank program.

## Frequent Itemsets

1. To run our Apriori algorithm for Exercise 3, change directory to the apriori folder and compile the java `javac miningRule.java`

2. Once compiled you can run it on its own with the following command. Example of 0.8 minimum support and 5 itemset
`java miningRule data/<any .dat file here> 0.8 5`

3. To run all the test, please make sure there is an output folder in the current directory. Then run the bash script as such `bash run.sh`

4. The output folder will have all the test results including the computation time in each of the .log file.
