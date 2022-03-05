package data.structures;

import java.util.*;
import java.io.*;

public class Player{
  public String pseudo;
  public Hashtable<String,ArrayList<Long>> gameLocations;

  public Player(){
    this.pseudo = null;
    this.gameLocations = new Hashtable<String,ArrayList<Long>>();
  }

  public Player(String path){
    try{
      FileInputStream in = new FileInputStream(path);
      ObjectInputStream oin = new ObjectInputStream(in);
      this.pseudo = (String)oin.readObject();
      this.gameLocations = (Hashtable<String,ArrayList<Long>>)oin.readObject();
      oin.close();
      in.close();
    }catch(IOException e){
      e.printStackTrace();
    }catch(ClassNotFoundException e){
      e.printStackTrace();
    }
  }

  public void addGame(String DBfile, long startingByte){
    if(!this.gameLocations.containsKey(DBfile)){
      this.gameLocations.put(DBfile,new ArrayList<Long>());
      this.gameLocations.get(DBfile).add(startingByte);
    }else{
      if(!this.gameLocations.get(DBfile).contains(startingByte)){
        this.gameLocations.get(DBfile).add(startingByte);
      }
    }
  }

  public void savePlayer(String path){
    try{
      File output = new File(path);
      FileOutputStream out = new FileOutputStream(output);
      ObjectOutputStream oout = new ObjectOutputStream(out);
      oout.writeObject(this.pseudo);
      oout.writeObject(this.gameLocations);
      oout.close();
      out.close();
    }catch(IOException e){
      e.printStackTrace();
    }
  }

  public Game[] getGames(){
    return null;
  }

  public String toString(){
    String res = "";
    res += this.pseudo+"\n";
    res += this.gameLocations.toString();
    return res;
  }
}
