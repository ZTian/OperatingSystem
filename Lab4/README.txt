Description
This program monitors demand paging. Paging.java has the main function and read in the file information. There is a FrameTable interface, which implemented by Lru.java, Lifo.java and Random.java accordingly. Lru.java runs the LRU algorithm. Lifo.java moniors LIFO algorithm and Random.java monitors random algorithm. For all algorithms, they first try to fill the frame table before they evict any page. RandomNumber.java is used for storing random numbers. TableEntry.java is used for storing the information of each table, including what process is using it, which page is referencing to, when it is loaded and when it is referenced.

Usage
Input the input information through command line. The result is shown on the screen.

Notice
The input format should be strictly following the input format given by professor, which is like "10 10 20 1 10 lru 0".

Compile
Compile the file through code "javac Paging.java". And run the compiled file through command like "java Paging 10 10 20 1 10 lru 0".