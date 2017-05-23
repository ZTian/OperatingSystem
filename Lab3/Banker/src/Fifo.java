import java.util.ArrayList;

public class Fifo {
  //Store the information about the algorithm
  private int task_number;
  private int resource_type;
  public int[] resources;
  private int[][] allocation;
  private int[] release;
  private ArrayList<Integer> isBlocked = new ArrayList<Integer>();
  
  //Monitoring the procedure of the algorithm
  int terminateNumber = 0;
  int cycle = 0;
  int deadlockCycle = 0;
  boolean isDeadlocked = true;
  
  /**
   * Initialize the process. Helps build the array with satisfying size.
   * The resource type represents the index in the resources, release and allocation array.
   * @param task_number : number of tasks in the process.
   * @param resource_type : number of types of resources.
   */
  public void initialize( int task_number, int resource_type ) {
    this.task_number = task_number;
    this.resource_type = resource_type;
    resources = new int [resource_type+1];
    allocation = new int [task_number+1][resource_type+1];
    release = new int [resource_type+1];
  }
  
  /**
   * Monitor the process of the algorithm. For each cycle, first try to grant blocked tasks.
   * Then try to grant ready tasks. Finally, release the resources.
   * @param tasks : the array of the details of tasks.
   * @param isVerbose : if to print detailed information.
   */
  public void run( Task[] tasks, boolean isVerbose ) {
    while( terminateNumber != task_number ) {      
      isDeadlocked = true;
      if( isVerbose ) {
        System.out.println( "During "+cycle+"-"+(cycle+1));
      }
      
      //Grant blocked tasks first.
      for( int i=0; i<isBlocked.size(); ++i ) {
        int index = isBlocked.get(i);
        int resource_id = tasks[index].activities.get(tasks[index].index).getResourceType();
        int amount = tasks[index].activities.get(tasks[index].index).getAmount();
        
        if( amount <= resources[resource_id] ) {
          //Grant resources
          resources[resource_id] -= amount;
          allocation[index][resource_id] += amount;
          
          //Update relevant information
          isDeadlocked = false;
          deadlockCycle = 0;
          tasks[index].wasBlocked = true;
          tasks[index].index++;
          
          //Remove from the isBlocked list.
          isBlocked.remove(i);
          i--;
          
          if( isVerbose ) {
            System.out.println("Task "+index+" completes its request");
          }          
        }
        else {
          if( deadlockCycle < 1 ) {
            tasks[index].increaseWaiting();
          }
          
          if( isVerbose ) {
            System.out.println("Task "+index+"'s request cannot be granted");
          }
        }
      }
      
      //Try to grant ready tasks
      for( int i=1; i<=task_number; ++i ) {
        //Task has been checked.
        if( tasks[i].wasBlocked ) {
          tasks[i].wasBlocked=false;
        }
        else if( tasks[i].computeTime > 0 ) {
          if( isVerbose ) {
            System.out.println("Task "+i+" needs to compute "+ tasks[i].computeTime+" cycles");
          }
          tasks[i].computeTime--;
          isDeadlocked=false;
          deadlockCycle=0;          
        }
        else if( !isBlocked.contains(i) && !tasks[i].isTerminated() ) {
          int resource_id = 0;
          int amount = 0;
          switch( tasks[i].activities.get(tasks[i].index).getType() ) {
          case INITIATE:
            isDeadlocked = false;
            deadlockCycle = 0;
            if(isVerbose) {
              System.out.println("Task "+i+" completes its initiate");
            }
            tasks[i].index++;          
            
            break;
          case COMPUTE:
            //Get activity information
            amount = tasks[i].activities.get(tasks[i].index).getAmount();            
            
            //Update relevant information and compute time.
            tasks[i].computeTime = amount-1;
            tasks[i].index++;
            isDeadlocked = false;
            deadlockCycle=0;
            if( isVerbose ) {
              System.out.println("Task "+i+" needs to compute "+amount+" cycles");
            }
            break;
          case RELEASE:
            //Get activity information
            resource_id = tasks[i].activities.get(tasks[i].index).getResourceType();
            amount = tasks[i].activities.get(tasks[i].index).getAmount();
            
            //Update relevant information
            allocation[i][resource_id] -= amount;
            release[resource_id] += amount;
            isDeadlocked = false;
            deadlockCycle = 0;
            tasks[i].index++;
            if( isVerbose ) {
              System.out.println("Task "+i+" releases "+amount+" unit of "+resource_id);
            }
            break;
          case REQUEST:
            //Get activity information
            resource_id = tasks[i].activities.get(tasks[i].index).getResourceType();
            amount = tasks[i].activities.get(tasks[i].index).getAmount();
            
            if( amount <= resources[resource_id] ) {
              //Grant resources
              resources[resource_id] -= amount;
              allocation[i][resource_id] += amount;
              
              //Update relevant information
              isDeadlocked = false;
              deadlockCycle = 0;
              tasks[i].index++;
              
              if( isVerbose ) {
                System.out.println("Task "+i+" completes its request");
              }
            }
            else {
              if( deadlockCycle < 1 ) {
                tasks[i].increaseWaiting();
              }
              isBlocked.add(i);
              if( isVerbose ) {
                System.out.println("Task "+i+"'s request cannot be granted");
              }
            }
            break;
          case TERMINATE:
            //Update the information of the task
            tasks[i].setProcessTime(cycle);
            tasks[i].setTerminated(true);
            terminateNumber++;
            isDeadlocked = false;
            deadlockCycle = 0;
            tasks[i].index++;
            if( isVerbose ) {
              System.out.println("Task "+i+" terminates at "+cycle);
            }
            break;
          default:
            System.out.println("Wrong type!");
            break;
          }
        }
      }
      
      
      if( isDeadlocked ) {
        deadlockCycle++;
        
        //Abort the task ranked first in the task list
        int index = isBlocked.get(0);
        int pos = 0;
        for( int i=1; i<isBlocked.size(); ++i ) {
          if( isBlocked.get(i) < index ) {
            index = isBlocked.get(i);
            pos = i;
          }
        }
        tasks[index].setAborted(true);
        tasks[index].setTerminated(true);
        for( int i=1; i<=resource_type; ++i ) {
          release[i] += allocation[index][i];
        }
        isBlocked.remove(pos);
        terminateNumber++;
      }

      //Release resource
      for( int i=1; i<=resource_type; ++i ) {
        resources[i] +=release[i];
        release[i] = 0;
        if( isVerbose ) {
          System.out.println( resources[i]+" of "+i+" avaiable at "+(cycle+1));
        }
      }

      if( deadlockCycle < 2 ) {
        cycle++;
      }
    }
    
    //Print summary result.
    System.out.println("\tFIFO");
    int processTotal=0;
    int waitTotal=0;
    for(int i=1; i<=task_number; ++i ) {
      if( tasks[i].isAborted() ) {
        System.out.println("Task "+i+"\t\tAborted");
      }
      else {
        int process = tasks[i].getProcessTime();
        processTotal+=process;
        int wait = tasks[i].getWaiting();
        waitTotal+=wait;
        System.out.print(String.format( "Task %d%8d%8d%8.0f", i, process, wait, (double)wait/process*100.0));
        System.out.println("%");
      }
    }
    System.out.print(String.format( "total %8d%8d%8.0f", processTotal, waitTotal, (double)waitTotal/processTotal*100.0));
    System.out.println("%");
  }
  
}
