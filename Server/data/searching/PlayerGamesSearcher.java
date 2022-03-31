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

  public Integer loadNumber(){
    int nbGames=0;
    try{
      File folder = new File("Src/");
      ArrayList<String> fileFolder = new ArrayList<>();
      for(int i=0;i<folder.list().length;i++){
        fileFolder.add(folder.list()[i]);
      }
      fileFolder.sort(String::compareToIgnoreCase);
      for(int i=0;i<fileFolder.size();i++){
        int previousNbGames = nbGames;
        if(fileFolder.get(i).endsWith("_player_data.dat")){
          FileInputStream in = new FileInputStream("Src/"+fileFolder.get(i));
          BufferedReader reader = new BufferedReader(new InputStreamReader(in));
          String line = "";
          Boolean found=false;
          while(reader.ready() && found==false){
            line = reader.readLine();
            if(line.equals("[Pseudo \""+this.playerName+"\"]")){
              found=true;
            }
            if(found){
              line = reader.readLine();
              line = reader.readLine();
              if(line.startsWith("[NumberOfGame")){
                nbGames = nbGames + Integer.parseInt(line.substring(14,line.length()-1));
              }
            }
          }
          System.out.println((nbGames-previousNbGames)+" games found in "+fileFolder.get(i));
          reader.close();
          in.close();
        }
      }
    }catch(IOException e) {
      e.printStackTrace();
    }
    if(nbGames>0)return nbGames;
    else return null;
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
      System.out.println("Hashtable building ...");
      long startTime = System.currentTimeMillis();
      while(fileIndex<fileFolder.size() && !isOver){
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
          }
          playerDataHashtable.put(fileFolder.get(fileIndex),startingBytes);
        }
        fileIndex++;
      }
      System.out.println("Hashtable done ! "+playerDataHashtable.values().size()+" games found in "+(System.currentTimeMillis()-startTime)+"ms");
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
        System.out.println(key);
        long[] startingBytes = new long[startingBytesArray.size()];
        for(int i=0;i<startingBytesArray.size();i++){
          System.out.println(""+i+" : "+startingBytesArray.get(i));
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
