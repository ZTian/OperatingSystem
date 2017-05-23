import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Lab {
  private Task[] tasks;
  private int resource_type = 0;
  private int task_number = 0;
  Fifo fifo = new Fifo();
  Banker banker = new Banker();

  public static void main(String[] args) {
    Lab lab = new Lab();
    String fileName = args[0];
    lab.runAlgo( fileName );
  }

  public void runAlgo( String fileName ) {
    readInFile( fileName );
    //printFifo( fifo );
    fifo.run( tasks, false );
    System.out.println();
    for( int i=1; i<=task_number; ++i ) {
      tasks[i].reset();
    }
    banker.run(tasks, false);
  }
  
  /**
   * Print out the read in information stored in FIFO
   * @param fifo
   */
  public void printFifo( Fifo fifo) {
    System.out.print( task_number  + " " + resource_type );
    for( int i=1; i<=resource_type; ++i ) {
      System.out.print( " "+fifo.resources[i] );
    }
    System.out.println();
    for( int i=1; i<=task_number; ++i ) {
      for( Activity activity : tasks[i].activities ) {
        activity.print();
      }
    }
  }
  
  
  /**
   * Read in file. Store all the information about each task in the array named tasks.
   * @param fileName : the input file
   */
  public void readInFile( String fileName ) {
    Scanner sc = null;
    try {
      sc = new Scanner(new File( fileName ));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    
    //Read in the number of tasks and resources. And then read in all resource amounts
    task_number = Integer.parseInt(sc.next());
    tasks = new Task [task_number+1];
    for( int i=1; i <=task_number; ++i ) {
      tasks[i] = new Task(i);
    }
    resource_type = Integer.parseInt(sc.next());
    fifo.initialize(task_number, resource_type);
    banker.initialize(task_number, resource_type);
    for( int i=1; i<=resource_type; ++i ) {
      int amount = Integer.parseInt(sc.next());
      fifo.resources[i] = amount;
      banker.resources[i] = amount;
    }    
    

    //Store all activity information
    int task_id;
    int amount;
    int resource_id;
    Activity act;
    while( sc.hasNextLine() ) {
      String type = sc.next();
      switch( type ) {
      case "initiate":
        task_id = Integer.parseInt(sc.next());
        resource_id = Integer.parseInt(sc.next());
        amount = Integer.parseInt(sc.next());
        act = new Activity.Builder(ActivityType.INITIATE, task_id).amount(amount).type(resource_id).build();
        tasks[task_id].activities.add(act);
        break;
      case "terminate":
        task_id = Integer.parseInt(sc.next());
        sc.next();
        sc.next();
        act = new Activity.Builder(ActivityType.TERMINATE, task_id).build();
        tasks[task_id].activities.add(act);
        break;
      case "compute":
        task_id = Integer.parseInt(sc.next());
        amount = Integer.parseInt(sc.next());
        sc.next();
        act = new Activity.Builder( ActivityType.COMPUTE, task_id).amount(amount).build();
        tasks[task_id].activities.add(act);
        break;
      case "request":
        task_id = Integer.parseInt(sc.next());
        resource_id = Integer.parseInt(sc.next());
        amount = Integer.parseInt(sc.next());
        act = new Activity.Builder(ActivityType.REQUEST, task_id).amount(amount).type(resource_id).build();
        tasks[task_id].activities.add(act);
        break;
      case "release":
        task_id = Integer.parseInt(sc.next());
        resource_id = Integer.parseInt(sc.next());
        amount = Integer.parseInt(sc.next());
        act = new Activity.Builder(ActivityType.RELEASE, task_id).amount(amount).type(resource_id).build();
        tasks[task_id].activities.add(act);
        break;
      default:
        System.out.println("Wrong format of input!");
      }
    }
    sc.close();
  }
}
