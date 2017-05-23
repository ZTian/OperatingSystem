
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;


public class FCFS {
  private ArrayList<Process> processList = new ArrayList<Process> ();
  private Queue<Integer> unstartedList = new LinkedList<Integer> ();
  private Queue<Integer> readyList = new LinkedList<Integer>();
  private ArrayList<Integer> blockedList = new ArrayList<Integer>();
  private Queue<Integer> runningList = new LinkedList<Integer>();
  private int finishTime = -1;
  private int IO = 0;
  
  public void passInProcesses() {
    ArrayList<Process> hold = Scheduler.getSortedProcesses();
    for( int i = 0; i < Scheduler.getNumOfProcesses(); ++i ) {
      Process p = new Process( );
      p.setArrival( hold.get(i).getArrival() );
      p.setInterval( hold.get(i).getInterval() );
      p.setCPUtime( hold.get(i).getCPUtime() );
      p.setIO( hold.get(i).getIO() );
      processList.add(p);
    }
  }
  
  public void runFCFS( boolean verbose) {
    passInProcesses();
    printAllProcesses(verbose);
    int timer = 0;
    int count = 0;
    beforeStart( verbose );
    while( count != Scheduler.getNumOfProcesses() ) {
      if( blockedList.size() != 0 ) {
        IO++;
        block();
      }
      unstartedToReady( timer );
      timer++;
      if( runningList.size() == 0 ) {
        if( readyList.size() != 0 ) {
          promoteToRun( verbose );
          processList.get(readyList.peek()).setState( State.Running );
          runningList.add(readyList.poll());
        }
      }
      else {
        int index = runningList.peek();
        run( index );
        /**
         * Check if the process has run out of the CPU burst time
         */
        if( processList.get(index).getRunTime() == 0 ) {
          /**
           * The process has finished. Move to terminated list.
           */
          if( processList.get(index).getCPUtime() == processList.get(index).getProcessTime() ) {
            processList.get(index).setState( State.Terminated );
            processList.get(index).setFinishingTime( timer-1 );           
            count++;              
          }
          /**
           * Process not finished. Move to blocked list.
           */
          else {
            blockedList.add( index );
            processList.get(index).setState( State.Blocked );
          }
          runningList.poll();
          if( runningList.size() == 0 && readyList.size() != 0 ) {
            promoteToRun( verbose );
            processList.get(readyList.peek()).setState( State.Running );
            runningList.add(readyList.poll());
          }
        }
      }
      if( count == Scheduler.getNumOfProcesses() ) {
        finishTime = timer-1;
        break;
      }      
      if( verbose ) {
        printPass( timer );
      }
      increaseWaiting();
    }
    printSummary();
  }
  
  public void increaseWaiting() {
    for( int i = 0; i < Scheduler.getNumOfProcesses(); ++i ) {
      if( processList.get(i).getState() == State.Ready ) {
        processList.get(i).increaseWaitingTime();
      }
    }
  }
  
  public void block() {
    ArrayList<Integer> toAdd = new ArrayList<Integer> ();
    for( int i = 0; i < blockedList.size(); ++i ) {      
      processList.get(blockedList.get(i)).decreaseBlockTime();
      processList.get(blockedList.get(i)).increaseIOTime();
      if( processList.get(blockedList.get(i)).getBlockTime() == 0 ) {
        toAdd.add(blockedList.get(i));
        processList.get(blockedList.get(i)).setState( State.Ready );
        blockedList.remove(i);
        i--;
      }
    }
    Collections.sort(toAdd);
    readyList.addAll( toAdd );
  }
  
  public void printSummary() {
    int CPUusage = 0;
    int turnaround = 0;
    int waiting = 0;
    System.out.println( "The scheduling algorithm used was First Come First Served" );
    System.out.println();
    for( int i = 0; i < Scheduler.getNumOfProcesses(); ++i ) {
      System.out.println( "Process " + i + ":" );
      processList.get(i).printProcessInfo();
      CPUusage += processList.get(i).getCPUtime();
      waiting += processList.get(i).getWaitingTime();
      turnaround += processList.get(i).getTurnaroundTime();
    }
    
    System.out.println( );
    System.out.println( "Summary Data:");
    System.out.println( "\tFinishing time: " + finishTime );
    System.out.printf( "\tCPU Utilization: %.6f\n", (float)CPUusage / finishTime );
    System.out.printf( "\tI/O Utilization: %.6f\n", (float)IO / finishTime );
    System.out.printf( "\tThroughput: %.6f", (float)Scheduler.getNumOfProcesses() * 100 / finishTime );
    System.out.println( " processes per hundred cycles" );
    System.out.printf( "\tAverage turnaround time: %.6f\n", (float)turnaround / Scheduler.getNumOfProcesses() );
    System.out.printf( "\tAverage waiting time: %.6f\n", (float)waiting / Scheduler.getNumOfProcesses() );
    
    System.out.println( );
    System.out.println( );
  }
  
  public void run( int index ) {
    processList.get(index).decreaseRunTime();
    processList.get(index).increaseProcessTime();    
  }
  
  /**
   * Promote ready process to run.
   * @param verbose whether to show the random number
   */
  public void promoteToRun( boolean verbose ) {
    int curProcess = readyList.peek();
    int rand = RandomNumber.randomOS( processList.get( curProcess ).getInterval(), verbose );
    if ( rand <= (processList.get(curProcess).getCPUtime() - processList.get(curProcess).getProcessTime()) ) {
      processList.get( curProcess ).setRunTime( rand );
    }
    else {
      processList.get( curProcess ).setRunTime( processList.get(curProcess).getCPUtime() - 
          processList.get(curProcess).getProcessTime() );
    }
    processList.get( curProcess ).setBlockTime( rand*processList.get(curProcess).getIO() );
  }
  
  public void unstartedToReady( int timer ) {
    while( !unstartedList.isEmpty() && processList.get( unstartedList.peek() ).getArrival() == timer ) {
      processList.get( unstartedList.peek() ).setState( State.Ready );
      readyList.add( unstartedList.poll() );
    }
  }
  
  public void beforeStart( boolean verbose ) {
    for( int i = 0; i < Scheduler.getNumOfProcesses(); ++i ) {
      unstartedList.add(i);
      processList.get(i).setState( State.Unstarted );
    }
    if( verbose ) {
      printPass(0);
    }
  }
  
  public void printPass( int timer) {
    System.out.printf( "Before cycle %5d:", timer );
    for( int i = 0; i < Scheduler.getNumOfProcesses(); ++i ) {
      System.out.printf( "%12s", processList.get(i).getState().toString() );
      switch( processList.get(i).getState() ) {
        case Ready: System.out.printf( "%4d.", 0 );          
          break;
        case Running: System.out.printf( "%4d.", processList.get(i).getRunTime() );
          break;
        case Blocked: System.out.printf( "%4d.", processList.get(i).getBlockTime() );
          break;
        case Unstarted: System.out.printf( "%4d.", 0 );
          break;
        case Terminated: System.out.printf( "%4d.", 0 );
          break;
      }      
    }
    System.out.println();
  }
  
  public void printAllProcesses( boolean verbose ) {
    System.out.print( "The original input was: " + Scheduler.getNumOfProcesses() + " " );
    for( Process hold : Scheduler.getProcesses() ) {
      System.out.print( "(" + hold.getArrival() + " " + hold.getInterval() + " " + hold.getCPUtime() + " " + 
          hold.getIO() + ") " );
    }
    System.out.println();
    System.out.print( "The (sorted) input is:  " + Scheduler.getNumOfProcesses() + " " );
    for( Process hold : processList ) {
      System.out.print( "(" + hold.getArrival() + " " + hold.getInterval() + " " + hold.getCPUtime() + " " + 
          hold.getIO() + ") " );
    }
    System.out.println();
    System.out.println();
    if( verbose ) {
      System.out.println( "This detailed printout gives the state and remaining burst for each process" );
      System.out.println();
    }
  }
  
  public ArrayList<Process> getProcessQueue() {
    return processList;
  }
}
