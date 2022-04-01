package data.structures;

import java.util.*;
import java.util.concurrent.locks.*;

public class GameBuffer{
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
