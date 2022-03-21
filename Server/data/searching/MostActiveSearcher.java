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

  /*
  Fonction initialisant les valeurs dans le tableau de mostActivePlayers.
  */

  private void sort(){
    List<String> keyList = new ArrayList<String>();
    Enumeration<String> keys = playersGamesList.keys();
    while(keys.hasMoreElements()){
      keyList.add(keys.nextElement());
    }

    //thanks https://stackoverflow.com/questions/5176771/sort-hashtable-by-values
    ArrayList<Map.Entry<String, Integer>> l = new ArrayList(playersGamesList.entrySet());
    Collections.sort(l, new Comparator<Map.Entry<String, Integer>>(){
    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2){
      return o2.getValue().compareTo(o1.getValue());
    }});
    //much thanks, such wow

    for(int i=0;i<mostActivePlayers.length;i++){
      mostActivePlayers[i] = l.get(i).getKey();
    }
  }

  public void load(){
    try{
      File root = new File("Src/");

      /*
      Parcours de tous les fichiers et traitement des fichiers XXXXXX_player_data.dat
      */

      for(int i=0;i<root.list().length;i++){
        if(root.list()[i].endsWith("_player_data.dat")){
          long previousTime = System.currentTimeMillis();

          /*
          Création du lecteur pour la lecture du fichier et recherche du joueur.
          */

          System.out.println("======= Reading "+root.list()[i]+"=======");
          FileInputStream in = new FileInputStream("Src/"+root.list()[i]);
          System.out.println("Reading content");
          BufferedReader reader = new BufferedReader(new InputStreamReader(in));
          String line = "";

          /*
          Extraction de tous les joueurs et stockage dans une hashtable
          */

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
          System.out.println("Time to read file : "+(System.currentTimeMillis()-previousTime)+"ms");
          reader.close();
          in.close();
        }
      }
    }catch(IOException e) {
      e.printStackTrace();
    }
    sort();
  }
}
