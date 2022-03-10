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
          int cpt = playerGames.size();
          System.out.println("======= Reading "+root.list()[i]+"=======");
          FileInputStream in = new FileInputStream("Src/"+root.list()[i]);
          System.out.println("Reading content");
          BufferedReader reader = new BufferedReader(new InputStreamReader(in));
          System.out.println("Scanning for "+this.playerName+" games");
          String line = "";
          Boolean found = false;
          while(reader.ready() && !found){
            line = reader.readLine();
            if(line.equals("[Pseudo \""+this.playerName+"\"]"))found = true;
          }
          if(found){
            line = reader.readLine();
            StringTokenizer tokenizer = new StringTokenizer(line);
            while(tokenizer.hasMoreTokens()){
              long startingByte = Long.parseLong(tokenizer.nextToken());
              //System.out.println(startingByte);
              playerGames.add(new Game(root.list()[i].substring(0,root.list()[i].length()-16)+".pgn",startingByte));
            }
          }
          System.out.println((playerGames.size()-cpt)+" games found");
          reader.close();
          in.close();
          System.out.println("file closed");
        }
      }
    }catch(IOException e) {
      e.printStackTrace();
    }
    Game[] res = new Game[playerGames.size()];
    playerGames.toArray(res);
    if(playerGames!=null && playerGames.size()>0)return res;
    else return null;
  }
}
