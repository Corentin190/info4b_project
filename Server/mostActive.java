import java.io.*;
import java.util.*;
import data.structures.*;
import data.searching.*;

public class mostActive{
  public static void main(String args[]){
    MostActiveSearcher searcher = new MostActiveSearcher(10);
    searcher.load();
    Hashtable<String,Integer> players = searcher.mostActiveList;
    // Enumeration<String> keys = players.keys();
    // int cpt = 0;
    // while(keys.hasMoreElements()){
    //   String key = keys.nextElement();
    //   if(cpt<10){
    //     System.out.println(key+" : "+players.get(key));
    //   }
    //   cpt++;
    // }
    Hashtable<Integer, String> reversedHashtable = new Hashtable<Integer, String>();
    players.forEach((key, value) -> {
      reversedHashtable.put(value,key);
    });
    List<Integer> valueList = new ArrayList<Integer>(players.values());
    Collections.sort(valueList);
    Collections.reverse(valueList);
    String[] mostActiveTable = new String[valueList.size()];
    for(int i=0;i<valueList.size();i++) {
      mostActiveTable[i]=reversedHashtable.get(i);
    }
    // for(int i=0;i<10;i++) {
    //   System.out.println(reversedHashtable.get(valueList.get(i)));
    // }

  }
}
