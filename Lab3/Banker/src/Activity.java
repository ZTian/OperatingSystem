public class Activity {
  private ActivityType type;
  private int task_id;
  private int resource_type;
  private int amount;
  
  public int getResourceType() {
    return resource_type;
  }
  
  public int getAmount() {
    return amount;
  }
  
  public ActivityType getType() {
    return type;
  }
  
  public static class Builder {
    //Required parameters
    private final ActivityType type;
    private final int task_id;
    
    //Optional parameters, initialized to default values
    private int resource_type = 0;
    private int amount = 0;
    
    public Builder( ActivityType type, int task_id ) {
      this.type = type;
      this.task_id = task_id;
    }
    
    public Builder type( int type ) {
      resource_type = type;
      return this;
    }
    
    public Builder amount( int amount ) {
      this.amount = amount;
      return this;
    }
    
    public Activity build() {
      return new Activity(this);
    }
  }
  
  private Activity(Builder builder) {
    type = builder.type;
    task_id = builder.task_id;
    resource_type = builder.resource_type;
    amount = builder.amount;
  }
  
  public void print() {
    switch( type ) {
    case INITIATE:
      System.out.println( type + " " + task_id + " " + resource_type + " " + amount );
      break;
    case RELEASE:
      System.out.println( type + " " + task_id + " " + resource_type + " " + amount );
      break;
    case REQUEST:
      System.out.println( type + " " + task_id + " " + resource_type + " " + amount );
      break;
    case COMPUTE:
      System.out.println( type + " " + task_id + " " + amount );
      break;
    case TERMINATE:
      System.out.println( type + " " + task_id );
      break;
    }
  }
}
