package data.structures;

import java.util.*;

public class GameBuffer{
  private final int BUFFER_SIZE;
  private ArrayList<String> buffer;
  private boolean readerDone;
  public int bufferEmptyEvent;
  public int bufferFullEvent;
  private long lastPopTime;
  private long popByteRate;

  public GameBuffer(final int BUFFER_SIZE){
  	/*
    Smaller BUFFER_SIZE should result in slightly less RAM usage but may cause some slowdowns if the consumer threads are slow.
    */
    this.BUFFER_SIZE = BUFFER_SIZE;
    this.buffer = new ArrayList<String>();
    this.readerDone = false;
    this.bufferEmptyEvent = 0;
    this.bufferFullEvent = 0;
    this.lastPopTime = System.currentTimeMillis();
    this.popByteRate = 0;
  }

  public String getBufferHealth(){
    return this.buffer.size()+"/"+this.BUFFER_SIZE;
  }

  public void setReaderDone(){
    this.readerDone = true;
  }
  /*
	Add a single game to the buffer. If the buffer is already full, the producer Thread will wait until notified.
  */
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
  /*
  Try to add every game contained in the ArrayList. If the Game can't be added to the buffer because it's full, then do nothing.
  Very usefull to limit synchronized access to the buffer and greatly increase exchange speed between the producer and the consumer threads.
  */
  public synchronized void add(ArrayList<String> gameTextList){
    int cpt = 0;
    while(this.buffer.size()<BUFFER_SIZE && gameTextList.size()>0){
      this.buffer.add(gameTextList.get(0));
      gameTextList.remove(0);
      cpt++;
    }
    this.notifyAll();
  }
  /*
	Pop a single game to the buffer. If the buffer is already empty, the consumer Thread will wait until notified.
  */
  public synchronized String pop(){
    while(this.buffer.size()<=0 && !readerDone){
      this.bufferEmptyEvent++;
      try{
        this.wait();
      }catch(InterruptedException e){
        e.printStackTrace();
      }
    }
    String gameText = null;
    if(this.buffer.size()>0){
      gameText = this.buffer.get(0);
      this.buffer.remove(0);
      this.popByteRate+=gameText.getBytes().length;
    }
    if(this.buffer.size()>0 && readerDone && System.currentTimeMillis()-lastPopTime>1000){
      System.out.println("Still processing ... ("+popByteRate/1000000+"MB/s)");
      this.lastPopTime = System.currentTimeMillis();
      this.popByteRate = 0;
    }else if(System.currentTimeMillis()-lastPopTime>1000){
      this.lastPopTime = System.currentTimeMillis();
      this.popByteRate = 0;
    }
    this.notifyAll();
    return gameText;
  }
  /*
  Try to pop a specified amount of games contained in the buffer. If the Game can't be pop from the buffer because it's empty, then it returns an empty ArrayList.
  Very usefull to limit synchronized access to the buffer and greatly increase exchange speed between the producer and the consumer threads.
  */
  public synchronized ArrayList<String> popMultiple(int size){
    while(this.buffer.size()<=0 && !readerDone){
      this.bufferEmptyEvent++;
      try{
        this.wait();
      }catch(InterruptedException e){
        e.printStackTrace();
      }
    }
    ArrayList<String> res = new ArrayList<String>();
    String gameText = null;
    int cpt = 0;
    while(this.buffer.size()>0 && cpt<size){
      gameText = this.buffer.get(0);
      this.buffer.remove(0);
      this.popByteRate+=gameText.getBytes().length;
      res.add(gameText);
      cpt++;
      gameText = null;
    }
    if(this.buffer.size()>0 && readerDone && System.currentTimeMillis()-lastPopTime>1000){
      System.out.println("Still processing ... ("+popByteRate/1000000+"MB/s)");
      this.lastPopTime = System.currentTimeMillis();
      this.popByteRate = 0;
    }else if(System.currentTimeMillis()-lastPopTime>1000){
      this.lastPopTime = System.currentTimeMillis();
      this.popByteRate = 0;
    }
    this.notifyAll();
    return res;
  }
}
