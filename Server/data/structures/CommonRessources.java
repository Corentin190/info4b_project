package data.structures;

import java.io.*;
import java.util.*;

public class CommonRessources{
  private File outputPlayerData;
  private File outputOpeningData;
  private File outputUrlIndex;
  public int gameNumber;    //indicates how many games have been read in the file once each Thread have merge their data.
  /*
  Creating the 3 Hashtables that's going to contain the merging of all InfoExtractors internal Hashtables.
  Thoses 3 Hashtables will then be used to create the 3 files used for browsing the DB in a blink of an eye.
  */
  public Hashtable<String,Integer> openingHashtable;
  public Hashtable<String,ArrayList<Long>> playersHashtable;
  public Hashtable<String,Long> urlHashtable;

  public CommonRessources(String dataFile){
    String playersDataFile = "Src/"+dataFile.substring(0,dataFile.length()-4)+"_player_data.dat";
    String openingDataFile = "Src/"+dataFile.substring(0,dataFile.length()-4)+"_opening_data.dat";
    String urlIndexFile = "Src/"+dataFile.substring(0,dataFile.length()-4)+"_url_index.dat";
    this.outputPlayerData = new File(playersDataFile);
    this.outputOpeningData = new File(openingDataFile);
    this.outputUrlIndex = new File(urlIndexFile);
    this.openingHashtable = new Hashtable<String,Integer>();
    this.playersHashtable = new Hashtable<String,ArrayList<Long>>();
    this.urlHashtable = new Hashtable<String,Long>();
    this.gameNumber = 0;
  }
  /*
  This function will merge the data contained in the playersHashtable arg inside the shared playersHashtable.
  If a player already exists in the shared Hashtable, his games are added to his previous games and the list of his games is sorted.
  */
  public synchronized void mergePlayerData(Hashtable<String,ArrayList<Long>> playersHashtable){
    Enumeration keys = playersHashtable.keys();
    while(keys.hasMoreElements()){
      String key = (String)keys.nextElement();
      if(this.playersHashtable.containsKey(key)) {
        this.playersHashtable.get(key).addAll(playersHashtable.get(key));
      }else{
        this.playersHashtable.put(key,new ArrayList<Long>());
        this.playersHashtable.get(key).addAll(playersHashtable.get(key));
      }
      Collections.sort(this.playersHashtable.get(key));
    }
  }
  /*
  This function will merge the data contained in the openingHashtable arg inside the shared openingHashtable.
  If an opening already exists in the shared Hashtable, the number of iterations is simply added to the previous number.
  */
  public synchronized void mergeOpeningData(Hashtable<String,Integer> openingHashtable){
    Enumeration keys = openingHashtable.keys();
    while(keys.hasMoreElements()){
      String key = (String)keys.nextElement();
      if(this.openingHashtable.containsKey(key)){
        this.openingHashtable.put(key,this.openingHashtable.get(key)+openingHashtable.get(key));
      }else{
        this.openingHashtable.put(key,openingHashtable.get(key));
      }
    }
  }
  /*
  This function will merge the data contained in the urlHashtable arg inside the shared urlHashtable.
  There's no point to do anything if an url already exists because every game has a unique url therefore 2 games shouldn't share the same key.
  The only exception is encountered 5 times over 3 099 534 127 games. 5 of them appear twice in the database. This means that they are the same game so there's no point in doing anything.
  The real number of games is 3 099 534 122.
  */
  public synchronized void mergeUrlData(Hashtable<String,Long> urlHashtable){
    Enumeration keys = urlHashtable.keys();
    while(keys.hasMoreElements()){
      String key = (String)keys.nextElement();
      this.urlHashtable.put(key,urlHashtable.get(key));
    }
  }
  /*
  This function will save the data contained in the shared playersHashtable to a file named like this :
  lichess_standard_rated_AAAA_MM_player_data.dat
  */
  private void savePlayerData(){
    try{
      if(!this.outputPlayerData.exists())this.outputPlayerData.createNewFile();
      FileOutputStream out = new FileOutputStream(this.outputPlayerData);
      OutputStreamWriter writer = new OutputStreamWriter(out);
      Enumeration<String> keys = this.playersHashtable.keys();
      while(keys.hasMoreElements()){
        String key = keys.nextElement();
        writer.write("[Pseudo \""+key+"\"]\n");
        ArrayList<Long> value = this.playersHashtable.get(key);
        for(int i=0;value != null && i<value.size();i++){
          writer.write(value.get(i)+" ");
        }
        writer.write("\n[NumberOfGame "+value.size()+"]\n");
      }
      writer.close();
      out.close();
    }catch(IOException e){
      e.printStackTrace();
    }
  }
  /*
  This function will save the data contained in the openingHashtable to a file named like this :
  lichess_standard_rated_AAAA_MM_opening_data.dat
  */
  private void saveOpeningData(){
    try{
      if(!this.outputOpeningData.exists())this.outputOpeningData.createNewFile();
      FileOutputStream out = new FileOutputStream(this.outputOpeningData);
      OutputStreamWriter writer = new OutputStreamWriter(out);
      Enumeration<String> keys = this.openingHashtable.keys();
      while(keys.hasMoreElements()){
        String key = keys.nextElement();
        writer.write("[Opening \""+key+"\"]\n");
        writer.write("[NumberOfOccurence \""+this.openingHashtable.get(key)+"\"]\n");
      }
      writer.close();
      out.close();
    }catch(IOException e){
      e.printStackTrace();
    }
  }
  /*
  This function will save the data contained in the urlHashtable to a file named like this :
  lichess_standard_rated_AAAA_MM_url_index.dat
  */
  private void saveUrlIndex(){
    try{
      if(!this.outputUrlIndex.exists())this.outputUrlIndex.createNewFile();
      FileOutputStream out = new FileOutputStream(this.outputUrlIndex);
      OutputStreamWriter writer = new OutputStreamWriter(out);
      Enumeration<String> keys = this.urlHashtable.keys();
      while(keys.hasMoreElements()){
        String key = keys.nextElement();
        writer.write("[Url \""+key+"\" : "+this.urlHashtable.get(key)+"]\n");
      }
      writer.close();
      out.close();
    }catch(IOException e){
      e.printStackTrace();
    }
  }
  
  public void saveData(){
    System.out.println("Saving data as "+outputPlayerData.getName());
    savePlayerData();
    System.out.println("Saving data as "+outputOpeningData.getName());
    saveOpeningData();
    System.out.println("Saving data as "+outputUrlIndex.getName());
    saveUrlIndex();
  }

  public synchronized void incrGame(){
    this.gameNumber++;
  }
}
