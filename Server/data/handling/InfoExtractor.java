package data.handling;

import data.structures.*;
import java.io.*;
import java.util.*;

public class InfoExtractor extends Thread{
  private GameBuffer buffer;
  private CommonRessources ressources;
  private final int BUFFER_POP_AMOUNT;

  private Hashtable<String,Integer> openingHashtable;
  private Hashtable<String,ArrayList<Long>> playersHashtable;
  private Hashtable<String,Long> urlHashtable;

  public InfoExtractor(GameBuffer buffer, CommonRessources ressources){
    this.buffer = buffer;
    this.ressources = ressources;
    /*
    Same issue as the producer thread. Because too much synchronized access to the buffer greatly slows the reading speed we have to limit it's pop frequency.
    This constant is the number of games popped at once by this specific thread each time it accesses the buffer.
    */
    this.BUFFER_POP_AMOUNT = 1000;
    /*
    Each consumer thread has it's own hashtables. This increases RAM usage but greatly improves reading speed.
    */
    this.openingHashtable = new Hashtable<String,Integer>();
    this.playersHashtable = new Hashtable<String,ArrayList<Long>>();
    this.urlHashtable = new Hashtable<String,Long>();
  }

  /*
  This function extract the players related Data from the game passed in arguments.
  If the player already exists in the Hashtable it just add the game to it's key. Else it creates a new key wich is the player's name.
  */
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
      ArrayList<String> internalBuffer = buffer.popMultiple(this.BUFFER_POP_AMOUNT);
      while(internalBuffer.size()>0){     //In this case, if the internalBuffer is empty it means that the popMultiple() function hasn't return any games.
        while(internalBuffer.size()>0){   //In this case, if the internalBuffer is empty it means that the consumer thread has processed all it's games.
          /*
          Exctract informations from the first game contained in it's internalBuffer.
          */
          BufferedReader reader = new BufferedReader(new StringReader(internalBuffer.get(0)));
          String line = "";
          Game tmp = new Game();
          boolean sameGame=false;
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
                tmp.date = line.substring(10,line.length()-2);
              }
              if(line.startsWith("[Opening")){
                tmp.opening = line.substring(10,line.length()-2);
              }
            }
          }while(line!=null);
          /*
          Increment the shared game counter in the CommonRessources and extracts player's data, opening data and url related data to it's internal Hastable
          */
          ressources.incrGame();
          this.extractPlayerData(tmp);
          this.extractOpeningIteration(tmp);
          this.extractUrl(tmp);
          readGameStart = System.currentTimeMillis();
          internalBuffer.remove(0);
        }
        internalBuffer = buffer.popMultiple(this.BUFFER_POP_AMOUNT);
      }
      ressources.mergePlayerData(this.playersHashtable);
      ressources.mergeOpeningData(this.openingHashtable);
      ressources.mergeUrlData(this.urlHashtable);
    }catch(IOException e){
      e.printStackTrace();
    }
  }
}
