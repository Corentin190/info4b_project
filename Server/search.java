import java.io.*;
import java.util.*;
import data.structures.*;
import data.searching.*;

public class search{
  public static void main(String args[]){
    PlayerGamesSearcher searcher = new PlayerGamesSearcher("2girls1cup");
    Game[] playerGames = searcher.load();
    if(playerGames!=null && playerGames.length>0){
      System.out.println(playerGames.length+" games found");
      for(int i=0;i<playerGames.length;i++){
        //System.out.println(playerGames[i].url);
      }
    }else System.out.println("No game found for this nickname");
  }
}
