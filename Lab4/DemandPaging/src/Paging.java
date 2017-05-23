
public class Paging {
  /*
   * From the standard input.
   */
  private int machine_size;
  private int page_size;
  private int process_size;
  private int job_mix;
  private int reference_num;
  private AlgorithmType algo;
  
  private int quantum = 3;
  
  private int process_num;
  private double[][] case_table;
  private int table_size;
  private FrameTable table;
  
  public void run(String[] args) {
    RandomNumber.readInNumber();
    machine_size = Integer.parseInt(args[0]);
    page_size = Integer.parseInt(args[1]);
    process_size = Integer.parseInt(args[2]);
    job_mix = Integer.parseInt(args[3]);
    reference_num = Integer.parseInt(args[4]);
    if(args[5].equals("lru")) {
      algo = AlgorithmType.LRU;
    }
    else if(args[5].equals("lifo")) {
      algo = AlgorithmType.LIFO;
    }
    else if(args[5].equals("random")) {
      algo = AlgorithmType.RANDOM;
    }
    else {
      System.out.println("Invalid algorithm type!");
      return;
    }
    generateCaseTable();
    table_size = machine_size / page_size;
    boolean isVerbose = false;
    switch(algo) {
    case LRU:
      table = new Lru(table_size, process_num);
      break;
    case LIFO:
      table = new Lifo(table_size, process_num);
      break;
    case RANDOM:
      table = new Random(table_size, process_num);
      break;
    default:
      System.out.println("Invalid type of algorithm!");
    }
    runAlgo(isVerbose);
  }
  
  private void printSummary() {
    System.out.println("The machine size is "+machine_size);
    System.out.println("The page size is "+page_size);
    System.out.println("The process size is "+process_size);
    System.out.println("The job mix number is "+job_mix);
    System.out.println("The number of references per process is "+reference_num);
    System.out.println("The replacement algorithm is "+algo);
    System.out.println("The level of debugging output is 0");
    System.out.println();
    
    int faults = 0;
    int residency = 0;
    int eviction = 0;
    for(int process_id=1; process_id<=process_num; ++process_id) {
      faults += table.getFaultsNum(process_id);
      residency += table.getResidencyTime(process_id);
      eviction += table.getEvictionNum(process_id);
      if(table.getEvictionNum(process_id) == 0) {
        System.out.println("Process "+process_id+" had "+table.getFaultsNum(process_id)+" faults.");
        System.out.println("\tWith no evictions, the average residence is undefined.");
      }
      else {
        System.out.println("Process "+process_id+" had "+table.getFaultsNum(process_id)+" faults and "+
            (double)table.getResidencyTime(process_id)/table.getEvictionNum(process_id)+" average residency.");
      }
    }
    System.out.println();
    
    if( eviction == 0 ) {
      System.out.println("The total number of faults is "+faults);
      System.out.println("\tWith no evictions, the overall average residence is undefined.");
    }
    else {
      System.out.println("The total number of faults is "+faults+" and the overall average residency is "+
          (double)residency/eviction);
    }
  }
  
  private void runAlgo(boolean isVerbose) {
    int[] references = new int [process_num+1];
    for(int process=1; process<=process_num; ++process) {
      references[process]=initializeReference(process);
    }
    int reference_cycle = 1;
    int ref=1;
    int quantum_counter = quantum;
    while(reference_cycle<=reference_num*process_num) {
      for(int process=1; process<=process_num; ++process) {
        ref = quantum_counter-2;
        for(;ref<=quantum_counter && ref<=reference_num; ++ref) {
          int page_num = references[process] / page_size;
          if(isVerbose) {
            System.out.print(process+" references word "+references[process]+" (page "+page_num+") at time "
                +reference_cycle);
          }      
          table.tryReference(page_num, process,reference_cycle, isVerbose);
          references[process] = generateReference(process,references[process], isVerbose);
          reference_cycle++;
        }
      }
      quantum_counter += quantum;
    }
    if(isVerbose) {
      System.out.println();
      System.out.println();
    }
    printSummary();
  }
  
  private int generateReference(int process_id, int cur_reference, boolean isVerbose) {
    int random_number = RandomNumber.next();
    if(isVerbose){
      System.out.println(process_id+" uses random number: "+random_number);
    }
    double quotient = random_number / (Integer.MAX_VALUE+1d);
    if(quotient < case_table[process_id][0]) {
      return (cur_reference+1+process_size) % process_size;
    }
    else if(quotient < case_table[process_id][1]) {
      return (cur_reference-5+process_size) % process_size;
    }
    else if(quotient < case_table[process_id][2]) {
      return (cur_reference+4+process_size) % process_size;
    }
    return RandomNumber.next() % process_size;
  }
  
  private void generateCaseTable() {
    switch(job_mix) {
    case 1:
      process_num = 1;
      case_table = new double[process_num+1][3];
      case_table[1][0] = 1;
      case_table[1][1] = 0 + case_table[1][0];
      case_table[1][2] = 0 + case_table[1][1];
      break;
    case 2:
      process_num = 4;
      case_table = new double[process_num+1][3];
      for(int process = 1; process <= process_num; ++process) {
        case_table[process][0] = 1.0;
        case_table[process][1] = 0 + case_table[process][0];
        case_table[process][2] = 0 + case_table[process][1];
      }
      break;
    case 3:
      process_num = 4;
      case_table = new double[process_num+1][3];
      for(int process = 1; process <= process_num; ++process) {
        case_table[process][0] = 0;
        case_table[process][1] = 0 + case_table[process][0];
        case_table[process][2] = 0 + case_table[process][1];
      }
      break;
    case 4:
      process_num = 4;
      case_table = new double[process_num+1][3];
      int process = 1;
      case_table[process][0] = 0.75;
      case_table[process][1] = 0.25 + case_table[process][0];
      case_table[process][2] = 0 + case_table[process][1];
      process++;
      case_table[process][0] = 0.75;
      case_table[process][1] = 0 + case_table[process][0];
      case_table[process][2] = 0.25 + case_table[process][1];
      process++;
      case_table[process][0] = 0.75;
      case_table[process][1] = 0.125 + case_table[process][0];
      case_table[process][2] = 0.125 + case_table[process][1];
      process++;
      case_table[process][0] = 0.5;
      case_table[process][1] = 0.125 + case_table[process][0];
      case_table[process][2] = 0.125 + case_table[process][1];
      break;
    default:
      System.out.println("Invalid job mix value!");
    }
  }
  
  public int initializeReference(int process_id) {
    return 111*process_id % process_size;
  }

  public static void main(String[] args) {
    new Paging().run(args);
  } 
}
