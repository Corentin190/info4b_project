package data.searching;

import java.io.*;
import java.util.*;

public class MostActiveSearcher{
  private int nbMostActive;
  public ArrayList<String> mostActivePlayers;

  public MostActiveSearcher(int nbMostActive){
    this.nbMostActive = nbMostActive;
    this.mostActivePlayers = new ArrayList<String>();
  }


  public void load(){
    try{
      FileInputStream in = new FileInputStream("Src/playersData.dat");
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));

      for(int i=0;i<this.nbMostActive && reader.ready();i++){
        String line="";
        String result="";
        result =i+1+". ";
        line = reader.readLine();
        result+=line.substring(9,line.length()-2)+" - ";
        line = reader.readLine();
        result+=line.substring(14,line.length()-1);
        mostActivePlayers.add(result);
      }
      if(mostActivePlayers.size()<this.nbMostActive){
        mostActivePlayers.add("Max player count reached");
      }
    }catch(IOException e) {
      e.printStackTrace();
    }
  }
}
