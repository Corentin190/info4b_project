package data.searching;

import java.io.*;
import java.util.*;

public class TopOpeningSearcher{
  private int nbTopOpening;
  public Hashtable<String,Integer> openingList;
  public String[] topOpening;

  public TopOpeningSearcher(int nbTopOpening){
    this.nbTopOpening = nbTopOpening;
    this.openingList = new Hashtable<String,Integer>();
    this.topOpening = new String[nbTopOpening];
  }

  /*
  Fonction initialisant les valeurs dans le tableau de topOpening.
  */

  private void sort(){
    List<String> keyList = new ArrayList<String>();
    Enumeration<String> keys = openingList.keys();
    while(keys.hasMoreElements()){
      keyList.add(keys.nextElement());
    }

    //thanks https://stackoverflow.com/questions/5176771/sort-hashtable-by-values
    ArrayList<Map.Entry<String, Integer>> l = new ArrayList(openingList.entrySet());
    Collections.sort(l, new Comparator<Map.Entry<String, Integer>>(){
    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2){
      return o2.getValue().compareTo(o1.getValue());
    }});
    //much thanks, such wow

    for(int i=0;i<topOpening.length;i++){
      topOpening[i] = l.get(i).getKey();
    }
  }

  public void load(){
    try{
      File root = new File("Src/");

      /*
      Parcours de tous les fichiers et traitement des fichiers XXXXXX_opening_data.dat
      */

      for(int i=0;i<root.list().length;i++){
        if(root.list()[i].endsWith("_opening_data.dat")){
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
            if(line.startsWith("[Opening ")){
              String opening = line.substring(10,line.length()-2);
              if(this.openingList.containsKey(opening)){
                do{
                  line = reader.readLine();
                }while(!line.startsWith("[NumberOfOccurence "));
                this.openingList.put(opening,this.openingList.get(opening)+Integer.parseInt(line.substring(20,line.length()-2)));
              }else{
                do{
                  line = reader.readLine();
                }while(!line.startsWith("[NumberOfOccurence "));
                this.openingList.put(opening,Integer.parseInt(line.substring(20,line.length()-2)));
              }
            }
          }
          System.out.println("Time to read file : ("+(System.currentTimeMillis()-previousTime)+")");
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
