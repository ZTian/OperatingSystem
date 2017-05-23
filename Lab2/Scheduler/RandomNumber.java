

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class RandomNumber {
  private static ArrayList<Integer> num = new ArrayList<Integer>();
  private static int pos;
  
  /**
   * Read in the file that contains all random number.
   */
  public static void readInRandom(){
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
  }
  
  /**
   * Print out all random numbers.
   */
  public static void printNumber() {
    for( Integer i:num ) {
      System.out.println( i );
    }
  }
  
  public static void reset() {
    pos = 0;
  }
  
  /**
   * Generate random number according to the formula 1+(X mod U)
   * 
   * @param upperBound the maximum value of the random number
   * @return target random number
   */
  public static int randomOS( int upperBound, boolean verbose ) {
    int rand = num.get(pos);
    if( verbose ) {
      System.out.println( "Find burst when choosing ready process to run " + rand );
    }
    pos++;
    if( pos == num.size() ) {
      pos = 0;
    }
    return 1 + ( rand % upperBound );
  }
  
  public int getPos() {
    return pos;
  }

  public static void setPos(int pos) {
    RandomNumber.pos = pos;
  }
  
  public static void initialize() {
    RandomNumber.readInRandom();
    RandomNumber.setPos(0);
  }
}