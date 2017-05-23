import java.util.ArrayList;

public class Banker {
  private int task_number;
  private int resource_type;
  public int[] resources;
  private int[][] allocation;
  private int[] release;
  private int[][] claim;
  private int[][] tryAllocation;
  private int[] resourcesCopy;
  private ArrayList<Integer> isBlocked = new ArrayList<Integer>();
  
  
  int terminateNumber = 0;
  int cycle = 0;
  private boolean shouldAbort = false;
  
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
    claim = new int [task_number+1][resource_type+1];
    tryAllocation = new int[task_number+1][resource_type+1];
    resourcesCopy = new int[resource_type+1];
  }
  
  public void run( Task[] tasks, boolean isVerbose ) {
    while( terminateNumber != task_number ) {
      if( isVerbose ) {
        System.out.println( "During "+cycle+"-"+(cycle+1));
      }
      
      //Try to grant blocked tasks first
      for( int i=0; i<isBlocked.size(); ++i ) {
        int index = isBlocked.get(i);
        int resource_id = tasks[index].activities.get(tasks[index].index).getResourceType();
        int amount = tasks[index].activities.get(tasks[index].index).getAmount();
        
        shouldAbort = false;
        boolean canAllocate = tryAllocation( tasks, isBlocked.get(i) );
        if( shouldAbort ) {
          //Release resources the task has
          for( int j=1; j<=resource_type; ++j ) {
            release[j] += allocation[i][j];
            allocation[i][j]=0;
          }
          System.out.println( "Task "+i+" requests more resources than it claims");
          
          //Update relevant information
          tasks[i].setAborted(true);
          tasks[i].setTerminated(true);
          terminateNumber++;
          isBlocked.remove(i);
          i--;
        }
        else if( canAllocate ) {
          //Grant resources
          resources[resource_id] -= amount;
          allocation[index][resource_id] += amount;
          
          //Update relevant information
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
          tasks[index].increaseWaiting();
          
          if( isVerbose ) {
            System.out.println("Task "+index+"'s request cannot be granted");
          }
        }
      }
      
      for( int i=1; i<=task_number; ++i ) {
        if( tasks[i].wasBlocked ) {
          tasks[i].wasBlocked = false;
        }
        else if( tasks[i].computeTime > 0 ) {
          tasks[i].computeTime--;
        }
        else if( !isBlocked.contains(i) && !tasks[i].isTerminated() ) {
          int resource_id = 0;
          int amount = 0;
          switch( tasks[i].activities.get(tasks[i].index).getType() ) {
          case INITIATE:
            //Get activity information
            resource_id = tasks[i].activities.get(tasks[i].index).getResourceType();
            amount = tasks[i].activities.get(tasks[i].index).getAmount();
            
            //Initiate claim exceeds the amount of the resource
            if( resources[resource_id] < amount ) {
              tasks[i].setAborted(true);
              tasks[i].setTerminated(true);
              
              terminateNumber++;
              System.out.println("Task "+i+" claims more resources than the system has");
            }
            else {
              claim[i][resource_id] = amount;
              if(isVerbose) {
                System.out.println("Task "+i+" completes its initiate");
              }
              tasks[i].index++;  
            }
            break;
          case REQUEST:
            //Get activity information
            resource_id = tasks[i].activities.get(tasks[i].index).getResourceType();
            amount = tasks[i].activities.get(tasks[i].index).getAmount();
            
            shouldAbort = false;
            boolean canAllocate = tryAllocation( tasks, i );
            if( shouldAbort ) {
              //Release resources the task has
              for( int j=1; j<=resource_type; ++j ) {
                release[j] += allocation[i][j];
                allocation[i][j]=0;
              }
              System.out.println( "Task "+i+" requests more resources than it claims");
              
              //Update relevant information
              tasks[i].setAborted(true);
              tasks[i].setTerminated(true);
              terminateNumber++;
              i--;
            }
            else if( canAllocate ) {
              //Grant resources
              resources[resource_id] -= amount;
              allocation[i][resource_id] += amount;
              
              //Update relevant information
              tasks[i].index++;
              
              if( isVerbose ) {
                System.out.println("Task "+i+" completes its request");
              }  
            }
            else {
              tasks[i].increaseWaiting();
              isBlocked.add(i);
              
              if( isVerbose ) {
                System.out.println("Task "+i+"'s request cannot be granted");
              }
            }
            break;
          case RELEASE:
            //Get activity information
            resource_id = tasks[i].activities.get(tasks[i].index).getResourceType();
            amount = tasks[i].activities.get(tasks[i].index).getAmount();
            
            //Update relevant information
            allocation[i][resource_id] -= amount;
            release[resource_id] += amount;
            tasks[i].index++;
            if( isVerbose ) {
              System.out.println("Task "+i+" releases "+amount+" unit of "+resource_id);
            }
            break;
          case COMPUTE:
            //Get activity information
            amount = tasks[i].activities.get(tasks[i].index).getAmount();            
            
            //Update relevant information and compute time.
            tasks[i].computeTime = amount-1;
            tasks[i].index++;
            if( isVerbose ) {
              System.out.println("Task "+i+" needs to compute "+amount+" cycles");
            }
            break;
          case TERMINATE:
            //Update the information of the task
            tasks[i].setProcessTime(cycle);
            tasks[i].setTerminated(true);
            terminateNumber++;
            if( isVerbose ) {
              System.out.println("Task "+i+" terminates at "+cycle);
            }
            break;
          }
        }
      }
      
      //Release resource
      for( int i=1; i<=resource_type; ++i ) {
        resources[i] +=release[i];
        release[i] = 0;
        if( isVerbose ) {
          System.out.println( resources[i]+" of "+i+" avaiable at "+(cycle+1));
        }
      }
      
      cycle++;
    }
      
    //Print summary result.
    System.out.println("\tBanker");
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
    System.out.print(String.format( "total %8d%8d%8.0f", processTotal, waitTotal, 
        (double)waitTotal/processTotal*100.0));
    System.out.println("%");
  }
  
  public boolean tryAllocation( Task[] tasks, int index ) {
    //Get activity information
    int resource_id = tasks[index].activities.get(tasks[index].index).getResourceType();
    int amount = tasks[index].activities.get(tasks[index].index).getAmount();
    
    if( resources[resource_id] < amount ) {
      return false;
    }
    
    //Make a copy of the current resources and allocation. Try to proceed from the current state.
    for( int i=1; i<= resource_type; ++i ) {
      resourcesCopy[i] = resources[i];
      for( int j=1; j<=task_number; ++j ) {
        tryAllocation[j][i]=allocation[j][i];
      }
    }
    
    tryAllocation[index][resource_id] += amount;
    resourcesCopy[resource_id] -= amount;
    
    //Task request more resources than claim
    if( tryAllocation[index][resource_id] > claim[index][resource_id] ) {
      shouldAbort = true;
      return false;
    }
    else {
      //Check if the state is safe
      int remainTask = task_number;
      boolean[] finish = new boolean[task_number+1];
      for( int i=1; i<= task_number; ++i ) {
        if( tasks[i].isAborted() || tasks[i].isTerminated() ) {
          remainTask--;
          finish[i]=true;
        }
      }
      boolean isSafe = true;
      while( remainTask != 0 && isSafe ) {
        isSafe = false;
        for( int i=1; i<=task_number; ++i ) {
          if( !finish[i] ) {
            boolean canFinish = true;
            //Check if can satisfy all the requests from one task.
            for( int j=1; j<= resource_type; ++j ) {
              if( resourcesCopy[j] < claim[i][j]-tryAllocation[i][j] ) {
                canFinish = false;
              }
            }
            if( canFinish ) {
              isSafe = true;
              remainTask--;
              finish[i]=true;
              
              //Release all its resources
              for( int j=1; j<=resource_type; ++j ) {
                resourcesCopy[j] += tryAllocation[i][j];
                tryAllocation[i][j] = 0;
              }
            }
          }
        }
      }
      if( !isSafe ) {
        return false;
      }
    }
    return true;
  }
}
