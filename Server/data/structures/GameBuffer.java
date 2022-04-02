package data.structures;

import java.util.*;

public class GameBuffer{
  private final int BUFFER_SIZE;
  private ArrayList<String> buffer;
  private boolean readerDone;
  public int bufferEmptyEvent;
  public int bufferFullEvent;

  public GameBuffer(final int BUFFER_SIZE){
    this.BUFFER_SIZE = BUFFER_SIZE;
    this.buffer = new ArrayList<String>();
    this.readerDone = false;
    this.bufferEmptyEvent = 0;
    this.bufferFullEvent = 0;
  }

  public String getBufferHealth(){
    return this.buffer.size()+"/"+this.BUFFER_SIZE;
  }

  public void setReaderDone(){
    this.readerDone = true;
  }

  public void add(String gameText){
    while(this.buffer.size()>=BUFFER_SIZE){
      this.bufferFullEvent++;
      System.out.println("Buffer full "+getBufferHealth());
    }
    buffer.add(gameText);
  }
  
  public synchronized String pop(){
    System.out.println("Pop");
    while(this.buffer.size()<=0 && !readerDone){
      this.bufferEmptyEvent++;
      System.out.println("Buffer empty");
    }
    String gameText = null;
    if(this.buffer.size()>0){
      gameText = this.buffer.get(0);
      this.buffer.remove(0);
    }
    return gameText;
  }
}
