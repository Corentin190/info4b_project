package data.searching;

import java.io.*;
import java.util.*;
import data.structures.Game;

public class PlayerGamesSearcher{
  private String playerName;
  private ArrayList<Game> playerGames;

  public PlayerGamesSearcher(String playerName){
      this.playerName = playerName;
      this.playerGames = new ArrayList<Game>();
  }

  public Game[] load(){
    File root = new File("Src/");
    for(int i=0;i<root.list().length;i++){
      if(root.list()[i].endsWith("_player_data.dat")){
        System.out.println("Reading "+root.list()[i]);
        FileInputStream in = new FileInputStream("Src/"+root.list()[i]);
        ObjectInputStream oin = new ObjectInputStream(in);
        Hashtable<String,ArrayList<Integer>> playersHashtable = oin.readObject();
        if(playersHashtable.containsKey(this.playerName)){
          ArrayList<Integer> gameList = playersHashtable.get(this.playerName);
          for(int j=0;j<gameList.size();j++){
            playerGames.add(new Game(root.list()[i],gameList.get(j)));
          }
        }
      }
    }
    if(playerGames.size()>0)return playerGames.toArray();
    else return null;
  }
}
