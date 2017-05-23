import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Scheduler {
  private static ArrayList<Process> processes = new ArrayList<Process>();
  private static ArrayList<Process> sortedProcesses = new ArrayList<Process>();
  public static int numOfProcesses = 0;
  private static String fileName = "";
  private static boolean[] toPrint = { true, true, true, true };
  private static boolean verbose = false;
  
  public static void readInProcesses() {
    Scanner sc = null;
    try {
      sc = new Scanner(new File( fileName ));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    if( sc.hasNext() ) {
      numOfProcesses = Integer.parseInt(sc.next());
    }
    for( int i = 0; i < numOfProcesses; ++i ) {
      Process hold = new Process();
      for( int j = 0; j < 4; ++j ) {
        String s = sc.next();
        switch(j) {        
        case 0: hold.setArrival( Integer.parseInt(s.substring(1,s.length())));
          break;
        case 1: hold.setInterval( Integer.parseInt(s));
          break;
        case 2: hold.setCPUtime( Integer.parseInt(s));
          break;
        case 3: hold.setIO( Integer.parseInt(s.substring(0,s.length()-1)));
          break;
        default: System.out.println( "Invalid parameter" );
          break;
        }
      }
      processes.add( hold );
    }
    sc.close();
    sortedProcesses.addAll(processes);
    Collections.sort( sortedProcesses );
    for( int i = 0; i < numOfProcesses; ++i ) {
      sortedProcesses.get(i).setArrivalID( i );
    }
  }
  
  public static void printProcesses( ArrayList<Process> p ) {
    for( Process cur : p ) {
      System.out.println( cur.getArrival() + " " + cur.getInterval() + " " + cur.getCPUtime() + " " + cur.getIO());
    }
  }
  
  public static ArrayList<Process> getProcesses() {
    return processes;
  }
  
  public static ArrayList<Process> getSortedProcesses() {
    return sortedProcesses;
  }
  
  public static int getNumOfProcesses() {
    return numOfProcesses;
  }
  
  public static void parseArguments( String[] args ) {
    switch( args.length ) {
      case 1: fileName = args[0]; 
        break;
      case 2: 
        if( args[0].charAt(0) == '-' ) {
          verbose = true;
        }
        fileName = args[1];
        break;
      case 3: verbose = true;
        fileName = args[1];
        for( int i = 0; i < 4; ++i ) {
          if( args[2].charAt(i) == '0' ) {
            toPrint[i] = false;
          }
        }
    }
  }

  public static void main(String[] args) {
    RandomNumber.initialize();
    parseArguments(args);
    //verbose=true;
    //fileName = "p4.txt";
    Scheduler.readInProcesses();
    
    if( toPrint[0] ) {
      FCFS fcfs = new FCFS();
      fcfs.runFCFS( verbose );
    }
    RandomNumber.reset();
    if( toPrint[1] ) {
      RR roundRobin = new RR();
      roundRobin.runRR( verbose );
    }
    RandomNumber.reset();
    if( toPrint[3] ) {
      Uniprogram uni = new Uniprogram();
      uni.runUni( verbose );      
    }
    RandomNumber.reset();
    if( toPrint[2] ) {
      SJF sjf = new SJF();
      sjf.runSJF( verbose );
    }
  }
}
