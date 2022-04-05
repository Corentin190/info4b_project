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
    int cpt = 0;
    while(this.buffer.size()<BUFFER_SIZE && gameTextList.size()>0){
      this.buffer.add(gameTextList.get(0));
      gameTextList.remove(0);
      cpt++;
    }
    this.notifyAll();
  }

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
