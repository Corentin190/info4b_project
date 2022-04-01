import java.util.*;
import java.io.*;
import data.structures.*;
import data.handling.*;

public class DBReader2{
  public static void main(String[] args) {
    final int NB_THREAD;
    if(args.length>0){
      NB_THREAD = Integer.parseInt(args[0])-1;
    }else NB_THREAD = 1;
    File folder = new File("Src/");
    ArrayList<String> fileFolder = new ArrayList<>();
    for(int i=0;i<folder.list().length;i++){
      if(folder.list()[i].endsWith(".pgn"))fileFolder.add(folder.list()[i]);
    }
    fileFolder.sort(String::compareToIgnoreCase);
    for(int i=0;i<fileFolder.size();i++){
      String dataFile = fileFolder.get(i);
      System.out.println("================= Processing "+dataFile+" =================");
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
        buffer.setReaderDone();
        for(int j=0;j<tab.length;j++){
          tab[j].join();
        }
        System.out.println("Reader done !");
        System.out.println("Buffer full event : "+buffer.bufferFullEvent);
        System.out.println("Buffer empty event : "+buffer.bufferEmptyEvent);
        System.out.println("Game processed : "+ressources.gameNumber);
      }catch(InterruptedException e){
        e.printStackTrace();
      }
    }
  }
}
