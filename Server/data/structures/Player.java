package data.structures;

import java.util.*;

public class Player{
  public String pseudo;
  public Hashtable<String,ArrayList<Long>> gameLocations;

  public Player(String pseudo){
    this.pseudo = pseudo;
    this.gameLocations = new Hashtable<String,ArrayList<Long>>();
  }

  public void addGame(String file, long startingByte){
    if(!gameLocations.containsKey(file)){
      gameLocations.put(file,new ArrayList<Long>());
      gameLocations.get(file).add(startingByte);
    }else{
      if(!gameLocations.get(file).contains(startingByte)){
        gameLocations.get(file).add(startingByte);
      }
    }
  }

  public Game[] getGames(){
    String[] keys = this.gameLocations.keySet.toArray();
    ArrayList<Game> games = new ArrayList<Game>();
    for(int i=0;i<keys.length;i++){
      for(int j=0;j<this.gameLocations.get(keys[i]).size();j++){
        games.add(new Game(keys[i],this.gameLocations.get(keys[i]).get(j)));
      }
    }
    return games.toArray();
  }

  public String toString(){

  }
}
