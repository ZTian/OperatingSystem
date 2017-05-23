import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class RandomNumber {
  private static ArrayList<Integer> num = new ArrayList<Integer>();
  private static int pos;
  
  /**
   * Constructor. Read in random numbers.
   * @return 
   */
  public static void readInNumber(){
    Scanner sc = null;
    try {
      sc = new Scanner(new File( "random-numbers.txt" ));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    while (sc.hasNextLine()) {
      num.add( Integer.valueOf(sc.next()) );
    }
    sc.close();
    pos = 0;
  }
  
  public static void printNumber() {
    for( Integer i:num ) {
      System.out.println( i );
    }
  }
  
  public static int next() {
    int random = num.get(pos);
    pos++;
    if(pos==num.size()) {
      pos = 0;
    }
    return random;
  }
}
