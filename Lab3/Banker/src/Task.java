import java.util.ArrayList;

public class Task {
  public ArrayList<Activity> activities = new ArrayList<Activity>();
  // use index to record the current activity
  public int index = 0;
  private int task_id = 0;
  private int processTime = 0;
  private int waitingTime = 0;
  public int computeTime = 0;
  private boolean isAborted = false;
  private boolean isTerminated = false;
  public boolean wasBlocked = false;
  
  public Task( int id ) {
    setTaskId(id);
    setProcessTime(0);
    waitingTime = 0;
    computeTime = 0;
    setAborted(false);
    activities = new ArrayList<Activity>();
  }
  
  public void next() {
    index++;
  }
  
  public void increaseWaiting() {
    waitingTime++;
  }
  
  public int getWaiting() {
    return waitingTime;
  }

  public boolean isTerminated() {
    return isTerminated;
  }

  public void setTerminated(boolean isTerminated) {
    this.isTerminated = isTerminated;
  }

  public boolean isAborted() {
    return isAborted;
  }

  public void setAborted(boolean isAborted) {
    this.isAborted = isAborted;
  }

  public int getProcessTime() {
    return processTime;
  }

  public void setProcessTime(int processTime) {
    this.processTime = processTime;
  }

  public int getTaskId() {
    return task_id;
  }

  public void setTaskId(int task_id) {
    this.task_id = task_id;
  }
  
  public void reset() {
    index = 0;
    processTime = 0;
    waitingTime = 0;
    computeTime = 0;
    isAborted = false;
    isTerminated = false;
    wasBlocked = false;
  }
}
