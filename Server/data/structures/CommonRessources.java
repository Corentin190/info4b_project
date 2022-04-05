package data.structures;

import java.io.*;
import java.util.*;

public class CommonRessources{
  private File outputPlayerData;
  private File outputOpeningData;
  private File outputUrlIndex;
  public int gameNumber;

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
  public synchronized void mergeUrlData(Hashtable<String,Long> urlHashtable){
    Enumeration keys = urlHashtable.keys();
    while(keys.hasMoreElements()){
      String key = (String)keys.nextElement();
      if(this.urlHashtable.containsKey(key))System.out.println(key+" found in 2 Hashtables when trying to merge them together");
      this.urlHashtable.put(key,urlHashtable.get(key));
    }
  }

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
