import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Uniprogram {
  private ArrayList<Process> processList = new ArrayList<Process> ();
  private Queue<Integer> unstartedList = new LinkedList<Integer> ();
  private Queue<Integer> readyList = new LinkedList<Integer> ();
  
  private enum StateTransfer { ReadyState, RunningState, BlockedState };
  private StateTransfer stateTrans;
  
  //Record the rank of the current uniprogram 
  private int curProcess = -1;
  private int finishTime = -1;
  
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
  
  /**
   * Uniprogrammed algorithm
   * @param verbose true to print the detailed process
   */
  public void runUni( boolean verbose) {
    passInProcesses();
    printAllProcesses( verbose );
    int timer = 0;
    int count = 0;
    beforeStart( verbose );
    while( count != Scheduler.getNumOfProcesses() ) {
      unstartedToReady( timer );
      timer++;
      if( curProcess == -1 ) {
        stateTrans = StateTransfer.ReadyState;
        if( !readyList.isEmpty() ) {
          curProcess = readyList.poll();
        }
      }
      switch( stateTrans ) {
        case ReadyState: promoteToRun( verbose );
          processList.get(curProcess).setState( State.Running );
          stateTrans = StateTransfer.RunningState;
          break;
        case RunningState: run();
        /**
         * Check if the process has run out of running time.
         */
        if( processList.get(curProcess).getRunTime() == 0 ) {
          /**
           * Check if the process has finished. If finished, promote the next process in the ready list
           */
          if( processList.get(curProcess).getCPUtime() == processList.get(curProcess).getProcessTime() ) {
            processList.get(curProcess).setState( State.Terminated );
            processList.get(curProcess).setFinishingTime( timer-1 );
            if( !readyList.isEmpty() ) {
              curProcess = readyList.poll();
              promoteToRun( verbose );
              processList.get(curProcess).setState( State.Running );
              stateTrans = StateTransfer.RunningState;
            }            
            count++;              
          }
          /**
           * If not finished, put into blocked list
           */
          else {
            stateTrans = StateTransfer.BlockedState;
            processList.get(curProcess).setState( State.Blocked );
          }
        }
          break;
        case BlockedState: block();
          if( processList.get(curProcess).getBlockTime() == 0 ) {
            promoteToRun( verbose );
            processList.get(curProcess).setState( State.Running );
            stateTrans = StateTransfer.RunningState;
          }
          break;
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
  
  public void printSummary() {
    int CPUusage = 0;
    int turnaround = 0;
    int waiting = 0;
    int IO = 0;
    System.out.println( "The scheduling algorithm used was Uniprocessing" );
    System.out.println();
    for( int i = 0; i < Scheduler.getNumOfProcesses(); ++i ) {
      System.out.println( "Process " + i + ":" );
      processList.get(i).printProcessInfo();
      CPUusage += processList.get(i).getCPUtime();
      waiting += processList.get(i).getWaitingTime();
      turnaround += processList.get(i).getTurnaroundTime();
      IO += processList.get(i).getIOTime();
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
  
  public void run( ) {
    processList.get(curProcess).decreaseRunTime();
    processList.get(curProcess).increaseProcessTime();
    
  }
  
  public void block() {
    processList.get(curProcess).decreaseBlockTime();
    processList.get(curProcess).increaseIOTime();
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
  
  /**
   * Ready to Run
   */
  public void promoteToRun( boolean verbose ) {
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
  
  /**
   * Put arriving process to ready list
   * @param timer current pass
   */
  public void unstartedToReady( int timer ) {
    while( !unstartedList.isEmpty() && processList.get( unstartedList.peek() ).getArrival() == timer ) {
      processList.get( unstartedList.peek() ).setState( State.Ready );
      readyList.add( unstartedList.poll() );
    }
  }
  
  /**
   * Set processes' state to ready or unstarted.
   */
  public void beforeStart( boolean verbose ) {
    for( int i = 0; i < Scheduler.getNumOfProcesses(); ++i ) {
        unstartedList.add(i);
        processList.get(i).setState( State.Unstarted );
    }
    if( verbose ) {
      printPass(0);
    }
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
}
