package data.handling;

import data.structures.*;
import java.io.*;
import java.util.*;

public class InfoExtractor extends Thread{
  private GameBuffer buffer;
  private CommonRessources ressources;

  private Hashtable<String,Integer> openingHashtable;
  private Hashtable<String,ArrayList<Long>> playersHashtable;
  private Hashtable<String,Long> urlHashtable;

  public InfoExtractor(GameBuffer buffer, CommonRessources ressources){
    this.buffer = buffer;
    this.ressources = ressources;
    this.openingHashtable = new Hashtable<String,Integer>();
    this.playersHashtable = new Hashtable<String,ArrayList<Long>>();
    this.urlHashtable = new Hashtable<String,Long>();
  }

  public synchronized void extractPlayerData(Game game){
    if(this.playersHashtable.containsKey(game.blackPlayer)) {
      this.playersHashtable.get(game.blackPlayer).add(game.startingByte);
    }else{
      this.playersHashtable.put(game.blackPlayer,new ArrayList<Long>());
      this.playersHashtable.get(game.blackPlayer).add(game.startingByte);
    }
    if(playersHashtable.containsKey(game.whitePlayer)) {
      this.playersHashtable.get(game.whitePlayer).add(game.startingByte);
    }else{
      this.playersHashtable.put(game.whitePlayer,new ArrayList<Long>());
      this.playersHashtable.get(game.whitePlayer).add(game.startingByte);
    }
  }

  public synchronized void extractOpeningIteration(Game game) {
    if(this.openingHashtable.containsKey(game.opening)){
      this.openingHashtable.put(game.opening,openingHashtable.get(game.opening)+1);
    }else{
      this.openingHashtable.put(game.opening,1);
    }
  }

  public synchronized void extractUrl(Game game) {
    this.urlHashtable.put(game.url,game.startingByte);
  }

  public void run(){
    try{
      long readGameStart = System.currentTimeMillis();
      long readTime = 0;
      String gameText = buffer.pop();
      while(gameText!=null){
        BufferedReader reader = new BufferedReader(new StringReader(gameText));
        String line = "";
        Game tmp = new Game();
        do{
          line = reader.readLine();
          if(line != null){
            if(line.startsWith("[Byte")){
              tmp.startingByte = Long.parseLong(line.substring(6,line.length()-1));
            }
            if(line.startsWith("[Event")){
              tmp.type = line.substring(8,line.length()-2);
            }
            if(line.startsWith("[Site")){
              tmp.url = line.substring(27,line.length()-2);
            }
            if(line.startsWith("[White ")){
              tmp.whitePlayer = line.substring(8,line.length()-2);
            }
            if(line.startsWith("[Black ")){
              tmp.blackPlayer = line.substring(8,line.length()-2);
            }
            if(line.startsWith("[Result")){
              tmp.result = line.substring(9,line.length()-2);
            }
            if(line.startsWith("[UTCDate")){
              tmp.result = line.substring(10,line.length()-2);
            }
            if(line.startsWith("[Opening")){
              tmp.opening = line.substring(10,line.length()-2);
            }
          }
        }while(line!=null);
        ressources.incrGame();
        this.extractPlayerData(tmp);
        this.extractOpeningIteration(tmp);
        this.extractUrl(tmp);
        readTime = System.currentTimeMillis()-readGameStart;
        if(readTime>250)System.out.println(this.getName()+" took some time to read a game : "+readTime+"ms");
        readGameStart = System.currentTimeMillis();
        gameText = buffer.pop();
      }
      ressources.mergePlayerData(this.playersHashtable);
      ressources.mergeOpeningData(this.openingHashtable);
      ressources.mergeUrlData(this.urlHashtable);
    }catch(IOException e){
      e.printStackTrace();
    }
  }
}
