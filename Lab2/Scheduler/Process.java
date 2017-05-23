

public class Process implements Comparable<Process> {
  //A
  private int arrival = 0;
  //C
  private int CPUtime = 0;
  //B
  private int interval = 0;
  //M
  private int IO = 0;

  private int arrivalID = -1;
  private int runTime = 0;
  private int blockTime = 0;

  private State state;
  
  //Already run for how long.
  private int processTime = 0;
  private int IOTime = 0;
  private int waitingTime = 0;
  private int finishingTime = 0;
  private int turnaroundTime = 0;
  
  public void printProcessInfo( ) {
    System.out.println( "\t(A,B,C,M) = (" + getArrival() + "," + getInterval() + "," + getCPUtime() + "," + 
        getIO() + ")" );
    System.out.println( "\tFinishing time: " + getFinishingTime() );
    setTurnaroundTime( (getFinishingTime()-getArrival()) );
    System.out.println( "\tTurnaround time: " + getTurnaroundTime() );
    System.out.println( "\tI/O time: " + getIOTime() );
    System.out.println( "\tWaiting time: " + getWaitingTime() );
  }
  
  public void increaseRunTime() {
    runTime++;
  }
  
  public int getRemainingTime() {
    return getCPUtime() - getProcessTime();
  }
  
  public void decreaseBlockTime() {
    blockTime--;
  }
  
  public void increaseIOTime() {
    IOTime++;
  }
  
  public void increaseWaitingTime() {
    waitingTime++;
  }
  
  public void increaseProcessTime() {
    setProcessTime(getProcessTime() + 1);
  }
  
  public void decreaseRunTime() {
    runTime--;
  }
  
  public Process() {
    
  }
  
  public Process( int arrival, int CPUtime, int interval, int IO ) {
    this.setArrival(arrival);
    this.setCPUtime(CPUtime);
    this.setInterval(interval);
    this.setIO(IO);
  }
    
  public int getArrival() {
    return arrival;
  }
  
  public void setArrival(int arrival) {
    this.arrival = arrival;
  }
  
  public int getCPUtime() {
    return CPUtime;
  }
  public void setCPUtime(int CPUtime) {
    this.CPUtime = CPUtime;
  }
  
  public int getInterval() {
    return interval;
  }
  
  public void setInterval(int interval) {
    this.interval = interval;
  }
  
  public int getIO() {
    return IO;
  }
  
  public void setIO(int IO) {
    this.IO = IO;
  }
  
  /**
   * Overwrite the function compareTo so that can easily sort processes according to their arrival time. 
   */
  @Override
  public int compareTo(Process other) {
    // TODO Auto-generated method stub
    int flag = 0;
    if( this.arrival < other.arrival ) {
      flag = -1;
    }
    else if ( this.arrival > other.arrival ) {
      flag = 1;
    }
    return flag;
  }

  public int getArrivalID() {
    return arrivalID;
  }

  public void setArrivalID(int arrivalID) {
    this.arrivalID = arrivalID;
  }

  public State getState() {
    return state;
  }

  public void setState(State state) {
    this.state = state;
  }

  public int getRunTime() {
    return runTime;
  }

  public void setRunTime(int runTime) {
    this.runTime = runTime;
  }

  public int getBlockTime() {
    return blockTime;
  }

  public void setBlockTime(int blockTime) {
    this.blockTime = blockTime;
  }

  public int getProcessTime() {
    return processTime;
  }

  public void setProcessTime(int processTime) {
    this.processTime = processTime;
  }

  public int getWaitingTime() {
    return waitingTime;
  }

  public void setWaitingTime(int waitingTime) {
    this.waitingTime = waitingTime;
  }

  public int getFinishingTime() {
    return finishingTime;
  }

  public void setFinishingTime(int finishingTime) {
    this.finishingTime = finishingTime;
  }

  public int getIOTime() {
    return IOTime;
  }

  public void setIOTime(int IOTime) {
    this.IOTime = IOTime;
  }

  public int getTurnaroundTime() {
    return turnaroundTime;
  }

  public void setTurnaroundTime(int turnaroundTime) {
    this.turnaroundTime = turnaroundTime;
  }
}
