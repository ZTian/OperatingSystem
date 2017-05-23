/**
 * This class represents an entry of the frame table.
 * It records the reference time of the page, the process_id  of the page, the number of the page
 * and the load time of the page.
 */
public class TableEntry {
  private int process_id = -1;
  private int page_num = -1;
  private int load_time;
  private int reference_time;
  
  public TableEntry() {
    process_id = -1;
    page_num = -1;
  }
  
  public int getProcessId() {
    return process_id;
  }

  public int getLoadTime() {
    return load_time;
  }

  public int getReferenceTime() {
    return reference_time;
  }

  public void setReferenceTime(int reference_time) {
    this.reference_time = reference_time;
  }

  public int getPageNum() {
    return page_num;
  }
  
  public void setInfo(int process_id, int page_num, int load_time, int reference_time) {
    this.reference_time = reference_time;
    this.page_num = page_num;
    this.process_id = process_id;
    this.load_time = load_time;
  }
}
