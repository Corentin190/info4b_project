package data.handling;

import data.structures.*;
import java.io.*;
import java.util.*;

public class PGNReader extends Thread{
  private String dataFile;
  private GameBuffer buffer;
  private ArrayList<String> internalBuffer;

  public PGNReader(String dataFile, GameBuffer buffer){
    this.dataFile = dataFile;
    this.buffer = buffer;
    this.internalBuffer = new ArrayList<String>();
  }

  public void run(){
    try{
      long fileBytesSize = new File("Src/"+this.dataFile).length();
      long currentlyReadBytes = 0;
      long lastReadBytes = 0;
      FileInputStream in = new FileInputStream("Src/"+this.dataFile);
      BufferedReader reader = new BufferedReader(new InputStreamReader(in),1024*16);
      int gameCpt = 0;
      long startReadTime = System.currentTimeMillis();
      long bufferStartReadTime = startReadTime;
      while(reader.ready()){
        do{
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
          currentlyReadBytes+=gameText.getBytes().length;
          internalBuffer.add(gameText);
        }while(gameCpt%10000>0 && reader.ready());
        System.out.println("Time to fullfill internal buffer : "+(System.currentTimeMillis()-bufferStartReadTime)+"ms");
        bufferStartReadTime = System.currentTimeMillis();
        buffer.add(internalBuffer);
        internalBuffer.clear();
        //System.out.println("Dumping internal buffer into gameBuffer");
        if(System.currentTimeMillis()-startReadTime>1000){
          System.out.println(this.dataFile+" : ("+currentlyReadBytes+"/"+fileBytesSize+")"+"("+(currentlyReadBytes*100/fileBytesSize)+"%)("+(currentlyReadBytes-lastReadBytes)/(System.currentTimeMillis()-startReadTime)/1000+"MB/s)(Buffer health : "+buffer.getBufferHealth()+")");
          startReadTime = System.currentTimeMillis();
          lastReadBytes = currentlyReadBytes;
        }
      }
      System.out.println("Thread done");
      reader.close();
      in.close();
    }catch(IOException e){
      e.printStackTrace();
    }
  }
}
