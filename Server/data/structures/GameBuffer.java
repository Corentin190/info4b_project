package data.structures;

import java.util.*;
import java.util.concurrent.locks.*;

public class GameBuffer{
  private final int BUFFER_SIZE;
  private ArrayList<String> buffer;
  private ReentrantLock addersLock;
  private ReentrantLock poppersLock;
  private boolean readerDone;
  public int bufferEmptyEvent;
  public int bufferFullEvent;

  public GameBuffer(final int BUFFER_SIZE){
    this.BUFFER_SIZE = BUFFER_SIZE;
    this.buffer = new ArrayList<String>();
    this.addersLock = new ReentrantLock();
    this.poppersLock = new ReentrantLock();
    this.readerDone = false;
    this.bufferEmptyEvent = 0;
    this.bufferFullEvent = 0;
  }

  public String getBufferHealth(){
    return this.buffer.size()+"/"+this.BUFFER_SIZE;
  }

  public synchronized void setReaderDone(){
    this.readerDone = true;
    this.notifyAll();
  }

  public synchronized void add(String gameText){
    try{
      while(this.buffer.size()>=BUFFER_SIZE){
        this.bufferFullEvent++;
        this.wait();
      }
    }catch(InterruptedException e){
      e.printStackTrace();
    }
    buffer.add(gameText);
    this.notifyAll();
  }

  public synchronized String pop(){
    try{
      while(this.buffer.size()<=0 && !readerDone){
        this.bufferEmptyEvent++;
        this.wait();
      }
    }catch(InterruptedException e){
      e.printStackTrace();
    }
    String gameText = null;
    if(this.buffer.size()>0){
      gameText = this.buffer.get(0);
      this.buffer.remove(0);
      this.notifyAll();
    }
    return gameText;
  }
}
