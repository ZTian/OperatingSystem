Description
This is a scheduler. It runs four scheduling algorithm includes FCFS, RR with quantum 2, SJF and Uniprogrammed. There are one file for each algorithm and is named after the name of the algorithm. Besides, there are RandomNumber.java, Process.java and Scheduler.java. RandomNumber.java read in random numbers and generate randomOS. Scheduler.java helps gather all algorithm together and has main function in it. 

Usage
Compile the java file with command: javac Scheduler.java. Then run the file with "java Scheduler --verbose '/path/to/input'". The last argument is required. The second to last argument is optional, which means the verbose flag is optional. If not provided, the verbose flag is set to false.

Notice
The input format should be strictly following the standard input format, which is like "1 (0 1 5 1)". The pair of round brackets is required.
