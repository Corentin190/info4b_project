package data.handling;

import data.structures.*;
import java.io.*;
import java.util.*;

public class PGNReader extends Thread{
  private String dataFile;
  private GameBuffer buffer;
  private ArrayList<String> internalBuffer;
  private int INTERNAL_BUFFER_DUMP_FREQUENCY;

  public PGNReader(String dataFile, GameBuffer buffer){
    this.dataFile = dataFile;
    this.buffer = buffer;
    this.internalBuffer = new ArrayList<String>();
    this.INTERNAL_BUFFER_DUMP_FREQUENCY = 10000;
  }

  public void run(){
    try{
      long fileBytesSize = new File("Src/"+this.dataFile).length();
      long currentlyReadBytes = 0;
      long lastReadBytes = 0;
      FileInputStream in = new FileInputStream("Src/"+this.dataFile);
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      int gameCpt = 0;
      long startReadTime = System.currentTimeMillis();
      while(reader.ready()){
        do{
          String gameText = "";
          String line;
          int blankLineCpt = 0;
          gameText+="[Byte "+currentlyReadBytes+"]\n";
          do{
            line = reader.readLine();
            currentlyReadBytes += (line.getBytes().length+1);
            if(line != null){
              gameText+=line;
              gameText+="\n";
              if(line.equals("")){
                blankLineCpt++;
              }
            }
          }while(blankLineCpt<2);
          gameCpt++;
          internalBuffer.add(gameText);
        }while(gameCpt%this.INTERNAL_BUFFER_DUMP_FREQUENCY>0 && reader.ready());
        buffer.add(internalBuffer);
        if(System.currentTimeMillis()-startReadTime>1000){
          System.out.println(this.dataFile+" : ("+currentlyReadBytes+"/"+fileBytesSize+")"+"("+(currentlyReadBytes*100/fileBytesSize)+"%)("+(currentlyReadBytes-lastReadBytes)/(System.currentTimeMillis()-startReadTime)/1000+"MB/s)(Buffer health : "+buffer.getBufferHealth()+")");
          startReadTime = System.currentTimeMillis();
          lastReadBytes = currentlyReadBytes;
        }
      }
      while(internalBuffer.size()>0){
        buffer.add(internalBuffer);
      }
      System.out.println("Thread done");
      reader.close();
      in.close();
    }catch(IOException e){
      e.printStackTrace();
    }
  }
}
