import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;


public class SJF {
  private ArrayList<Process> processList = new ArrayList<Process> ();
  private Queue<Integer> unstartedList = new LinkedList<Integer> ();
  private ArrayList<Integer> readyList = new ArrayList<Integer>();
  private ArrayList<Integer> blockedList = new ArrayList<Integer>();
  private Queue<Integer> runningList = new LinkedList<Integer>();

  private int finishTime = -1;
  private int IO = 0;
  
  public void runSJF( boolean verbose) {
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
          promoteToRun(verbose);
        }
      }
      else {
        int index = runningList.peek();
        run(index);
        /**
         * Check if the process has run out of CPU burst time.
         */
        if( processList.get(index).getRunTime() == 0 ) {
          /**
           * If the process has finished.
           */
          if( processList.get(index).getCPUtime() == processList.get(index).getProcessTime() ) {
            processList.get(index).setState( State.Terminated );
            processList.get(index).setFinishingTime( timer-1 );
            count++;
          }
          else {
            blockedList.add( index );
            processList.get(index).setState( State.Blocked );
          }
          runningList.poll();
          if( runningList.size() == 0 && readyList.size() != 0 ) {
            promoteToRun(verbose);
          }
        }
      }
      if( count == Scheduler.getNumOfProcesses() ) {
        finishTime = timer-1;
        break;
      }
      if( verbose ) {
        printPass(timer);
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
  
  public void printSummary() {
    int CPUusage = 0;
    int turnaround = 0;
    int waiting = 0;
    System.out.println( "The scheduling algorithm used was Shortest Job First" );
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
  
  public void run( int index ) {
    processList.get(index).decreaseRunTime();
    processList.get(index).increaseProcessTime();    
  }
  
  public void promoteToRun( boolean verbose ) {
    int curProcess = findShortest();
    int index = readyList.get(curProcess);
    int rand = RandomNumber.randomOS( processList.get( index ).getInterval(), verbose );
    if ( rand <= (processList.get(index).getRemainingTime() ) ) {
      processList.get( index ).setRunTime( rand );
    }
    else {
      processList.get( index ).setRunTime( processList.get(index).getRemainingTime() );
    }
    processList.get( index ).setBlockTime( rand*processList.get(index).getIO() );
    processList.get(index).setState( State.Running );
    runningList.add(index);
    readyList.remove( curProcess );
  }
  
  public int findShortest() {
    int shortest = 0;
    for( int i = 1; i < readyList.size(); ++i ) {
      if( processList.get( readyList.get(i)).getRemainingTime() < 
          processList.get( readyList.get(shortest) ).getRemainingTime() ) {
        shortest = i;
      }
    }
    return shortest;
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
}
