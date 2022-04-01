import java.util.*;
import java.io.*;
import java.util.concurrent.locks.*;

class GameBuffer{
  private final int BUFFER_SIZE;
  public ArrayList<String> buffer;
  private ReentrantLock lock;

  public GameBuffer(final int BUFFER_SIZE){
    this.BUFFER_SIZE = BUFFER_SIZE;
    this.buffer = new ArrayList<String>();
  }

  public synchronized void add(String gameText){
    try{
      while(this.buffer.size()>=BUFFER_SIZE){
        System.out.println("BUFFER OVERFLOW");
        wait();
      }
    }catch(InterruptedException e){
      e.printStackTrace();
    }
    buffer.add(gameText);
    notifyAll();
  }

  public String pop(){
    this.lock.lock();
    System.out.println("A thread acquired the lock");
    try{
      while(this.buffer.size()<=0){
        System.out.println("EMPTY BUFFER, WAITING FOR DATA");
        wait();
        System.out.println("RESUMING OPERATIONS");
      }
    }catch(InterruptedException e){
      e.printStackTrace();
    }
    String gameText = this.buffer.get(0);
    this.buffer.remove(0);
    notifyAll();
    System.out.println("A thread returned the lock");
    this.lock.unlock();
    return gameText;
  }
}

class CommonRessources{
  private File outputPlayerData;
  private File outputOpeningData;
  private File outputUrlIndex;

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
  }
}

class InfoExtractor extends Thread{
  private GameBuffer buffer;
  private CommonRessources ressources;

  public InfoExtractor(GameBuffer buffer, CommonRessources ressources){
    this.buffer = buffer;
    this.ressources = ressources;
  }

  public void run(){
    String gameText = buffer.pop();
    while(gameText!=null){
      gameText = buffer.pop();
    }
  }
}

class PGNReader extends Thread{
  private String dataFile;
  private GameBuffer buffer;

  public PGNReader(String dataFile, GameBuffer buffer){
    this.dataFile = dataFile;
    this.buffer = buffer;
  }

  public void run(){
    try{
      FileInputStream in = new FileInputStream("Src/"+this.dataFile);
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      long prevTime = System.currentTimeMillis();
      int gameCpt = 0;
      while(reader.ready()){
        String gameText = "";
        String line;
        int blankLineCpt = 0;
        do{
          line = reader.readLine();
          if(line != null){
            gameText+=line;
            gameText+="\n";
            if(line.equals("")){
              blankLineCpt++;
            }
          }
        }while(blankLineCpt<2);
        gameCpt++;
        buffer.add(gameText);
      }
      System.out.println("Time to read "+this.dataFile+" ("+gameCpt+" games) : "+(System.currentTimeMillis()-prevTime)+"ms");
    }catch(IOException e){
      e.printStackTrace();
    }
  }
}

public class DBReader2{
  public static void main(String[] args) {
    File folder = new File("Src/");
    ArrayList<String> fileFolder = new ArrayList<>();
    for(int i=0;i<folder.list().length;i++){
      if(folder.list()[i].endsWith(".pgn"))fileFolder.add(folder.list()[i]);
    }
    fileFolder.sort(String::compareToIgnoreCase);

    for(int i=0;i<fileFolder.size();i++){
      String dataFile = fileFolder.get(i);
      GameBuffer buffer = new GameBuffer(1000000);
      CommonRessources ressources = new CommonRessources(dataFile);
      PGNReader reader = new PGNReader(dataFile,buffer);
      InfoExtractor[] tab = new InfoExtractor[7];
      for(int j=0;i<tab.length;j++){
        tab[i] = new InfoExtractor(buffer,ressources);
      }
      reader.start();
      for(int j=0;j<tab.length;j++){
        tab[i].start();
      }
      try{
        reader.join();
      }catch(InterruptedException e){
        e.printStackTrace();
      }
    }
  }
}
