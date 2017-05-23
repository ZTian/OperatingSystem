
public interface FrameTable {
  /**
   * Try reference the page. If there exists a page fault, fix it by placing the page into the table.
   * @param page_num  the frame referenced by the current process
   * @param process_id  the id of the current process
   * @param reference_time  current time
   */
  public void tryReference(int page_num, int process_id, int reference_time, boolean isVerbose);
  
  public int getResidencyTime(int process_id);
  
  public int getFaultsNum(int process_id);
  
  public int getEvictionNum(int process_id);
}
