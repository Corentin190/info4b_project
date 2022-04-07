package data.handling;

import data.structures.*;
import java.io.*;
import java.util.*;

public class PGNReader extends Thread{
  private String dataFile;
  private GameBuffer buffer;
  private ArrayList<String> internalBuffer;
  private int INTERNAL_BUFFER_FLUSH_FREQUENCY;

  public PGNReader(String dataFile, GameBuffer buffer){
    this.dataFile = dataFile;
    this.buffer = buffer;
    /*
    Creating an internal buffer in order to limit synchronized access to the buffer.
    Smaller INTERNAL_BUFFER_FLUSH_FREQUENCY should result in slightly less RAM usage but will increase the frequency of synchronized access to the buffer wich slows the reading speed.
    */
    this.internalBuffer = new ArrayList<String>();
    this.INTERNAL_BUFFER_FLUSH_FREQUENCY = 10000;
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
      /*
      Start reading the .pgn file passed as constructor args.
      Each games seems to end with a blank line followed by the list of moves then followed by a second blank line.
      We just have to count for 2 blank lines and we know a game has been completely read.
      The program then add it to it's internalBuffer. Each time the internalBuffer meets it's INTERNAL_BUFFER_FLUSH_FREQUENCY, it sends it's buffer to the buffer.
      */
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
        }while(gameCpt%this.INTERNAL_BUFFER_FLUSH_FREQUENCY>0 && reader.ready());
        buffer.add(internalBuffer);
        /*
        The following code is just for debugging purposes !
        It prints where the reader is in the .pgn file, it's average reading speed and the buffer's health once a second.
        */
        if(System.currentTimeMillis()-startReadTime>1000){
          System.out.println(this.dataFile+" : ("+currentlyReadBytes+"/"+fileBytesSize+")"+"("+(currentlyReadBytes*100/fileBytesSize)+"%)("+(currentlyReadBytes-lastReadBytes)/(System.currentTimeMillis()-startReadTime)/1000+"MB/s)(Buffer health : "+buffer.getBufferHealth()+")");
          startReadTime = System.currentTimeMillis();
          lastReadBytes = currentlyReadBytes;
        }
      }
      /*
      Once the reader is done reading it's .pgn file, it flushes it's internalBuffer to the buffer.
      */
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
