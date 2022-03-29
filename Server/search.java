import java.io.*;
import java.util.*;
import data.structures.*;
import data.searching.*;

public class search{
  public static void main(String args[]){
    PlayerGamesSearcher searcher = new PlayerGamesSearcher("fil77");
    Scanner sc = new Scanner(System.in);
    boolean found = false;
    do{
      long t = System.currentTimeMillis();
      Game[] playerGames = searcher.loadNextThousand();
      if(playerGames!=null && playerGames.length>0){
        System.out.println(playerGames.length+" games found");
        for(int i=0;i<playerGames.length;i++){
          System.out.println(playerGames[i].toString());
        }
        found = true;
        System.out.println("Continuer d'afficher ? ("+(System.currentTimeMillis()-t)+"ms)");
      }else{
        System.out.println("No game found for this nickname");
        found = false;
      }
    }while(sc.nextLine().equals("y") && found);
  }
}
