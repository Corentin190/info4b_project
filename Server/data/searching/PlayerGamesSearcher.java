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
    try{
      File root = new File("Src/");
      for(int i=0;i<root.list().length;i++){
        if(root.list()[i].endsWith("_player_data.dat")){
          System.out.println("Reading "+root.list()[i]);
          FileInputStream in = new FileInputStream("Src/"+root.list()[i]);
          ObjectInputStream oin = new ObjectInputStream(in);
          Hashtable<String,ArrayList<Integer>> playersHashtable = (Hashtable<String,ArrayList<Integer>>)oin.readObject();
          if(playersHashtable.containsKey(this.playerName)){
            ArrayList<Integer> gameList = playersHashtable.get(this.playerName);
            for(int j=0;j<gameList.size();j++){
              String fileName = root.list()[i].substring(0,root.list()[i].length()-16);
              playerGames.add(new Game(fileName+".dat",gameList.get(j)));
            }
          }
          oin.close();
          in.close();
        }
      }
    }catch(IOException e) {
      e.printStackTrace();
    }catch(ClassNotFoundException e){
      e.printStackTrace();
    }
    if(playerGames.size()>0)return (Game[])playerGames.toArray();
    else return null;
  }
}
