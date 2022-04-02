import java.util.*;
import java.io.*;
import data.structures.*;
import data.handling.*;

public class DBReader2{
  public static void main(String[] args) {
    final int NB_THREAD;
    if(args.length>0 && Integer.parseInt(args[0])>1){
      NB_THREAD = Integer.parseInt(args[0])-1;
    }else NB_THREAD = 1;
    File folder = new File("Src/");
    ArrayList<String> fileFolder = new ArrayList<>();
    for(int i=0;i<folder.list().length;i++){
      if(folder.list()[i].endsWith(".pgn")){
        String playersDataFile = "Src/"+folder.list()[i].substring(0,folder.list()[i].length()-4)+"_player_data.dat";
        String openingDataFile = "Src/"+folder.list()[i].substring(0,folder.list()[i].length()-4)+"_opening_data.dat";
        String urlIndexFile = "Src/"+folder.list()[i].substring(0,folder.list()[i].length()-4)+"_url_index.dat";
        File outputPlayerData = new File(playersDataFile);
        File outputOpeningData = new File(openingDataFile);
        File outputUrlIndex = new File(urlIndexFile);
        if(!outputPlayerData.exists() || !outputOpeningData.exists() || !outputUrlIndex.exists())fileFolder.add(folder.list()[i]);
      }
    }
    fileFolder.sort(String::compareToIgnoreCase);
    System.out.println(fileFolder.size()+" files to process");
    for(int i=0;i<fileFolder.size();i++){
      String dataFile = fileFolder.get(i);
      System.out.println("================= Processing "+dataFile+" =================");
      long prevTime = System.currentTimeMillis();
      GameBuffer buffer = new GameBuffer(1000000);
      CommonRessources ressources = new CommonRessources(dataFile);
      PGNReader reader = new PGNReader(dataFile,buffer);
      InfoExtractor[] tab = new InfoExtractor[NB_THREAD];
      for(int j=0;j<tab.length;j++){
        tab[j] = new InfoExtractor(buffer,ressources);
      }
      for(int j=0;j<tab.length;j++){
        tab[j].start();
      }
      reader.start();
      try{
        reader.join();
        synchronized(buffer){
          buffer.setReaderDone();
        }
        System.out.println("Time to read "+dataFile+" : "+(System.currentTimeMillis()-prevTime)+"ms");
        for(int j=0;j<tab.length;j++){
          tab[j].join();
        }
        System.out.println("Buffer full event : "+buffer.bufferFullEvent);
        System.out.println("Buffer empty event : "+buffer.bufferEmptyEvent);
        System.out.println("Game processed : "+ressources.gameNumber);
        ressources.saveData();
        System.out.println("Time to process "+dataFile+" ("+ressources.gameNumber+" games) : "+(System.currentTimeMillis()-prevTime)+"ms");
      }catch(InterruptedException e){
        e.printStackTrace();
      }
    }
  }
}
