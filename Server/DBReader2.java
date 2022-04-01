import java.util.*;
import java.io.*;
import java.util.concurrent.locks.*;
import data.structures.*;

class GameBuffer{
  private final int BUFFER_SIZE;
  private ArrayList<String> buffer;
  private ReentrantLock lock;
  private boolean readerDone;
  public int bufferEmptyEvent;
  public int bufferFullEvent;

  public GameBuffer(final int BUFFER_SIZE){
    this.BUFFER_SIZE = BUFFER_SIZE;
    this.buffer = new ArrayList<String>();
    this.lock = new ReentrantLock();
    this.readerDone = false;
    this.bufferEmptyEvent = 0;
    this.bufferFullEvent = 0;
  }

  public synchronized void setReaderDone(){
    this.readerDone = true;
    notifyAll();
  }

  public synchronized void add(String gameText){
    try{
      while(this.buffer.size()>=BUFFER_SIZE){
        this.bufferFullEvent++;
        wait();
      }
    }catch(InterruptedException e){
      e.printStackTrace();
    }
    buffer.add(gameText);
    notifyAll();
  }

  public synchronized String pop(){
    try{
      while(this.buffer.size()<=0 && !readerDone){
        this.bufferEmptyEvent++;
        wait();
      }
    }catch(InterruptedException e){
      e.printStackTrace();
    }
    String gameText = null;
    if(this.buffer.size()>0){
      gameText = this.buffer.get(0);
      this.buffer.remove(0);
      notifyAll();
    }else System.out.println("Reader done and buffer empty, job done !");
    return gameText;
  }
}

class CommonRessources{
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

  public synchronized void incrGame(){
    this.gameNumber++;
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
    try{
      String gameText = buffer.pop();
      while(gameText!=null){
        BufferedReader reader = new BufferedReader(new StringReader(gameText));
        String line = "";
        Game tmp = new Game();
        do{
          line = reader.readLine();
          if(line != null){
            if(line.startsWith("[Event")){
              tmp.type = line.substring(8,line.length()-2);
            }
            if(line.startsWith("[Site")){
              tmp.url = line.substring(27,line.length()-2);
            }
            if(line.startsWith("[White ")){
              tmp.whitePlayer = line.substring(8,line.length()-2);
            }
            if(line.startsWith("[Black ")){
              tmp.blackPlayer = line.substring(8,line.length()-2);
            }
            if(line.startsWith("[Result")){
              tmp.result = line.substring(9,line.length()-2);
            }
            if(line.startsWith("[UTCDate")){
              tmp.result = line.substring(10,line.length()-2);
            }
            if(line.startsWith("[Opening")){
              tmp.opening = line.substring(10,line.length()-2);
            }
          }
        }while(line!=null);
        ressources.incrGame();
        gameText = buffer.pop();
      }
    }catch(IOException e){
      e.printStackTrace();
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
      reader.close();
      in.close();
      System.out.println("Time to read "+this.dataFile+" ("+gameCpt+" games) : "+(System.currentTimeMillis()-prevTime)+"ms");
    }catch(IOException e){
      e.printStackTrace();
    }
  }
}

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
