/**
 * This class implements the random algorithm.
 *
 */
public class Random implements FrameTable{
  private TableEntry[] table;
  private int table_size;
  private int[] residency_time;
  private int[] faults_num;
  private int[] eviction_num;
  
  public Random( int table_size, int process_num ) {
    this.table_size = table_size;
    table = new TableEntry[table_size];
    for(int i=0; i<table_size; ++i) {
      table[i] = new TableEntry();
    }
    residency_time = new int [process_num+1];
    faults_num = new int [process_num+1];
    eviction_num = new int [process_num+1];
  }

  @Override
  public void tryReference(int page_num, int process_id, int reference_time, boolean isVerbose) {    
    if(!hasPageFault(page_num, process_id, reference_time, isVerbose)) {
      return;
    }
    
    if(isVerbose) {
      System.out.print(": Fault, ");
    }
    placePage(page_num, process_id, reference_time, isVerbose);
  }

  /**
   * Check if there is a page fault for the current reference.
   * @param page_num  the page referenced by the current process
   * @param process_id  the id of the current process
   * @param reference_time  the time of the program
   * @param isVerbose  whether to print out detailed debug information
   * @return  true, if there is a page fault. Otherwise, false.
   */
  public boolean hasPageFault(int page_num, int process_id, int reference_time, boolean isVerbose) {
    for(int page=0; page<table_size; ++page) {
      if(table[page].getPageNum() == page_num && table[page].getProcessId() == process_id) {
        if(isVerbose) {
          System.out.println(": Hit in frame "+page);
        }
        table[page].setReferenceTime(reference_time);
        return false;
      }
    }
    faults_num[process_id]++;
    return true;
  }
  
  /**
   * Place the page with the least recently used page. To keep the code simple, using array instead of LinkedList under
   * this case.
   * @param page_num  the page referenced by the current process
   * @param process_id  the id of the current process
   * @param reference_time  the time of the program
   * @param isVerbose  whether to print out detailed debug information
   */
  private void placePage(int page_num, int process_id, int reference_time, boolean isVerbose) {
    for(int page=table_size-1; page>=0; --page) {
      //Check if there exists free frame
      if(table[page].getPageNum() == -1 && table[page].getProcessId() == -1) {
        table[page].setInfo(process_id, page_num, reference_time, reference_time);
        if(isVerbose) {
          System.out.println("using free frame "+page);
        }
        return;
      }
    }
    int index = RandomNumber.next() % table_size;
    residency_time[table[index].getProcessId()] += reference_time - table[index].getLoadTime();
    eviction_num[table[index].getProcessId()]++;
    if(isVerbose) {
      System.out.println("evicting page "+table[index].getPageNum()+" of "+
          table[index].getProcessId()+" from frame "+index);
    }
    table[index].setInfo(process_id, page_num, reference_time, reference_time);
  }
  
  @Override
  public int getResidencyTime(int process_id) {
    return residency_time[process_id];
  }

  @Override
  public int getFaultsNum(int process_id) {
    return faults_num[process_id];
  }

  @Override
  public int getEvictionNum(int process_id) {
    return eviction_num[process_id];
  }
}
