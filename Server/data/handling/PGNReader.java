package data.handling;

import data.structures.*;
import java.io.*;

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
