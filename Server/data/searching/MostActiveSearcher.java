package data.searching;

import java.io.*;
import java.util.*;

public class MostActiveSearcher{
  private int nbMostActive;
  public Hashtable<String,Integer> playersGamesList;
  public String[] mostActivePlayers;

  public MostActiveSearcher(int nbMostActive){
    this.nbMostActive = nbMostActive;
    this.playersGamesList = new Hashtable<String,Integer>();
    this.mostActivePlayers = new String[nbMostActive];
  }

  private void sort(){
    List<String> keyList = new ArrayList<String>();
    Enumeration<String> keys = playersGamesList.keys();
    while(keys.hasMoreElements()){
      keyList.add(keys.nextElement());
    }

    //============= DEBUT TRI =============
    System.out.println("Sorting array");
    for(int i=0;i<keyList.size();i++){
      for(int j=0;j<keyList.size()-i-1;j++){
        System.out.println(i+"/"+keyList.size()+" : "+j+"/"+keyList.size());
        if(playersGamesList.get(keyList.get(j))>playersGamesList.get(keyList.get(j+1))){
          String tmp = keyList.get(j);
          keyList.set(j,keyList.get(j+1));
          keyList.set(j+1,tmp);
        }
      }
    }

    //============== FIN TRI ==============

    for(int i=0;i<mostActivePlayers.length;i++){
      mostActivePlayers[i] = keyList.get(i);
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
              if(this.playersGamesList.containsKey(playerNickname)){
                do{
                  line = reader.readLine();
                }while(!line.startsWith("[NumberOfGame "));
                this.playersGamesList.put(playerNickname,this.playersGamesList.get(playerNickname)+Integer.parseInt(line.substring(14,line.length()-1)));
              }else{
                do{
                  line = reader.readLine();
                }while(!line.startsWith("[NumberOfGame "));
                this.playersGamesList.put(playerNickname,Integer.parseInt(line.substring(14,line.length()-1)));
              }
            }
          }
        }
      }
    }catch(IOException e) {
      e.printStackTrace();
    }
    sort();
  }
}
