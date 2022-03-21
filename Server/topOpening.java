import java.io.*;
import java.util.*;
import data.structures.*;
import data.searching.*;

public class topOpening{
  public static void main(String args[]){
    TopOpeningSearcher searcher = new TopOpeningSearcher(1000);
    searcher.load();
    for(int i=0;i<searcher.topOpening.length;i++){
      System.out.println(i+". "+searcher.topOpening[i]+" - "+searcher.openingList.get(searcher.topOpening[i]));
    }
  }
}
