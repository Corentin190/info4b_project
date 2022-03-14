package data.searching;

import java.io.*;
import java.util.*;

public class MostActiveSearcher{
  private int nbMostActive;
  public Hashtable<String,Integer> mostActiveList;

  public MostActiveSearcher(int nbMostActive){
    this.nbMostActive = nbMostActive;
    this.mostActiveList = new Hashtable<String,Integer>();
  }

  public void sort(){
    String[] topPlayers = new String[nbMostActive];
    Enumeration<String> keys = mostActiveList.keys();
    while(keys.hasMoreElements()){
      String key = keys.nextElement();
      for(int i=0;i<topPlayers.length;i++){

      }
    }
  }

  public void load(){
    try{
      File root = new File("Src/");
      for(int i=0;i<root.list().length;i++){
        if(root.list()[i].endsWith("_player_data.dat")){
          System.out.println("======= Reading "+root.list()[i]+"=======");
          FileInputStream in = new FileInputStream("Src/"+root.list()[i]);
          System.out.println("Reading content");
          BufferedReader reader = new BufferedReader(new InputStreamReader(in));
          String line = "";
          while(reader.ready()){
            line = reader.readLine();
            if(line.startsWith("[Pseudo ")){
              String playerNickname = line.substring(9,line.length()-2);
              if(this.mostActiveList.containsKey(playerNickname)){
                do{
                  line = reader.readLine();
                }while(!line.startsWith("[NumberOfGame "));
                this.mostActiveList.put(playerNickname,this.mostActiveList.get(playerNickname)+Integer.parseInt(line.substring(14,line.length()-1)));
              }else{
                do{
                  line = reader.readLine();
                }while(!line.startsWith("[NumberOfGame "));
                this.mostActiveList.put(playerNickname,Integer.parseInt(line.substring(14,line.length()-1)));
              }
            }
          }
        }
      }
    }catch(IOException e) {
      e.printStackTrace();
    }
  }
}
