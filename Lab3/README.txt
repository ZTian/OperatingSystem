Description
This program monitors fifo algorithm and banker's algorithm. Lab.java has the main function and read in the file information. Banker.java is running banker's algorithm and Fifo.java is for fifo alogrithm. Other file includes classes, which helps store all the information. The banker's algorithm includes error checks, which are checking if the task claims more resources than the system has and checking if the task requests more resources than it claims. For both algorithm, blocked tasks have the priority to be checked first. 

Usage
Input the input file through command line. The result is shown on the screen.

Notice
The input format should be strictly following the input format given by professor, which is like "request task_number resource_type amount".

Compile
Compile the file through code "javac Lab.java". And run the compiled file through command "java Lab '/path/to/file'".