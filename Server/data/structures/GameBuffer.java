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

  public synchronized void setReaderDone(){
    this.readerDone = true;
    this.notifyAll();
  }

  public synchronized void add(String gameText){
    while(this.buffer.size()>=BUFFER_SIZE){
      this.bufferFullEvent++;
      try{
        this.wait();
      }catch(InterruptedException e){
        e.printStackTrace();
      }
    }
    buffer.add(gameText);
    this.notifyAll();
  }

  public synchronized void add(ArrayList<String> gameTextList){
    while(this.buffer.size()>=BUFFER_SIZE-gameTextList.size()){
      //System.out.println("Buffer full");
      this.bufferFullEvent++;
      try{
        this.wait();
      }catch(InterruptedException e){
        e.printStackTrace();
      }
      //System.out.println("Buffer has been empty, resuming ops");
    }
    for(int i=0;i<gameTextList.size();i++){
      buffer.add(gameTextList.get(i));
    }
    this.notifyAll();
  }

  public synchronized String pop(){
    while(this.buffer.size()<=0 && !readerDone){
      this.bufferEmptyEvent++;
      //System.out.println("Buffer empty");
      try{
        this.wait();
      }catch(InterruptedException e){
        e.printStackTrace();
      }
      //System.out.println("Buffer has been fullfiled, resuming ops");
    }
    String gameText = null;
    if(this.buffer.size()>0){
      gameText = this.buffer.get(0);
      this.buffer.remove(0);
    }
    this.notifyAll();
    return gameText;
  }
}
