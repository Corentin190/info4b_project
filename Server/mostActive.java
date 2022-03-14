import java.io.*;
import java.util.*;
import data.structures.*;
import data.searching.*;

public class mostActive{
  public static void main(String args[]){
    MostActiveSearcher searcher = new MostActiveSearcher(10);
    searcher.load();
    Hashtable<String,Integer> players = searcher.mostActiveList;
    Enumeration<String> keys = players.keys();
    int cpt = 0;
    while(keys.hasMoreElements()){
      String key = keys.nextElement();
      if(cpt<10){
        System.out.println(key+" : "+players.get(key));
      }
      cpt++;
    }
  }
}
