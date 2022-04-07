package data.searching;

import java.io.*;
import java.util.*;
import data.structures.*;

public class PlayerGamesSearcher{
  private String playerName;
  private ArrayList<Game> playerGames;

  public PlayerGamesSearcher(String playerName){
    this.playerName = playerName;
    this.playerGames = new ArrayList<Game>();
  }
public int loadNumber(){
      int playerNbGame=0;
      try{
        FileInputStream in = new FileInputStream("data/searching/playersData.dat");
          BufferedReader reader = new BufferedReader(new InputStreamReader(in));
          String line="";
          do{
            line = reader.readLine();
            if(line.equals("[Pseudo \""+this.playerName+"\"]")){
              line=reader.readLine();
              playerNbGame = Integer.parseInt(line.substring(14,line.length()-1));
            }
          }while(reader.ready());
      }catch(IOException e){
        e.printStackTrace();
      }
        return playerNbGame;
    }

  public Game[] load(){
    return load(0,loadNumber());
  }

  public Game[] load(int nbGame){
    return load(0,nbGame);
  }

  public Game[] load(int fromGame, int toGame){
    try{
      File folder = new File("Src/");
      ArrayList<String> fileFolder = new ArrayList<>();
      for(int i=0;i<folder.list().length;i++){
        if(folder.list()[i].endsWith("_player_data.dat"))fileFolder.add(folder.list()[i]);
      }
      fileFolder.sort(String::compareToIgnoreCase);

      Hashtable<String,ArrayList<Long>> playerDataHashtable = new Hashtable<String,ArrayList<Long>>();
      boolean isOver = false;
      int fileIndex = 0;
      int cptGame = 0;
      System.out.println("Hashtable building ...");
      long startTime = System.currentTimeMillis();
      while(fileIndex<fileFolder.size() && cptGame<(toGame-fromGame) && !isOver){
        boolean found = false;
        FileInputStream in = new FileInputStream("Src/"+fileFolder.get(fileIndex));
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        while(!found && reader.ready()){
          if(reader.readLine().equals("[Pseudo \""+this.playerName+"\"]"))found = true;
        }
        if(found){
          ArrayList<Long> startingBytes = new ArrayList<Long>();
          StringTokenizer tokenizer = new StringTokenizer(reader.readLine());
          while(tokenizer.hasMoreTokens()){
            startingBytes.add(Long.parseLong(tokenizer.nextToken()));
            cptGame++;
          }
          playerDataHashtable.put(fileFolder.get(fileIndex),startingBytes);
        }
        fileIndex++;
      }
      System.out.println("Hashtable done in "+(System.currentTimeMillis()-startTime)+"ms");
      Enumeration keyEnum = playerDataHashtable.keys();
      ArrayList<String> keys = new ArrayList<String>();
      while(keyEnum.hasMoreElements()){
        keys.add((String)keyEnum.nextElement());
      }
      keys.sort(String::compareToIgnoreCase);
      int keyCpt = keys.size()-1;
      int cpt = 0;
      while(keyCpt >= 0 && cpt < toGame){
        String key = keys.get(keyCpt);
        ArrayList<Long> startingBytesArray = new ArrayList<Long>();
        for(int i=playerDataHashtable.get(key).size()-1;i>=0;i--){
          cpt++;
          startingBytesArray.add((long)playerDataHashtable.get(key).get(i));
          if(cpt>=toGame)break;
        }
        long[] startingBytes = new long[startingBytesArray.size()];
        for(int i=0;i<startingBytesArray.size();i++){
          startingBytes[i] = startingBytesArray.get(i);
        }
        Game[] res = Game.createFromFile(key.substring(0,key.length()-16)+".pgn",startingBytes);
        for(int i=0;i<res.length;i++)this.playerGames.add(res[i]);
        keyCpt--;
      }
    }catch(IOException e){
      e.printStackTrace();
    }
    Game[] res = new Game[playerGames.size()];
    playerGames.toArray(res);
    if(playerGames!=null && playerGames.size()>0)return res;
    else return null;
  }
}
