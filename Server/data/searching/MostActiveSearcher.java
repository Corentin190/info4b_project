package data.searching;

import java.io.*;
import java.util.*;

public class MostActiveSearcher{
  private int nbMostActive;
  public String[] mostActivePlayers;

  public MostActiveSearcher(int nbMostActive){
    this.nbMostActive = nbMostActive;
    this.mostActivePlayers = new String[nbMostActive];
  }


  public void load(){
    try{
      FileInputStream in = new FileInputStream("Src/playersData.dat");
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      String line="";
      for(int i=0;i<this.nbMostActive;i++){
        line = reader.readLine();
        mostActivePlayers[i] = line.substring(9,line.length()-2)+" - ";
        line = reader.readLine();
        mostActivePlayers[i] += line.substring(14,line.length()-1);
      }
    }catch(IOException e) {
      e.printStackTrace();
    }
  }
}
