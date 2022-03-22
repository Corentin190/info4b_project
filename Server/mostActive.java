import java.io.*;
import java.util.*;
import data.structures.*;
import data.searching.*;

public class mostActive{
  public static void main(String args[]){
    MostActiveSearcher searcher = new MostActiveSearcher(1000);
    searcher.load();
    for(int i=0;i<searcher.mostActivePlayers.length;i++){
      System.out.println(i+". "+searcher.mostActivePlayers[i]+" - "+searcher.playersGamesList.get(searcher.mostActivePlayers[i]));
    }
    System.out.println(searcher.playersGamesList.size());
  }
}
