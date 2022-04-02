package data.handling;

import data.structures.*;
import java.io.*;

public class InfoExtractor extends Thread{
  private GameBuffer buffer;
  private CommonRessources ressources;

  public InfoExtractor(GameBuffer buffer, CommonRessources ressources){
    this.buffer = buffer;
    this.ressources = ressources;
  }

  public void run(){
    try{
      String gameText = buffer.pop();
      while(gameText!=null){
        BufferedReader reader = new BufferedReader(new StringReader(gameText));
        String line = "";
        Game tmp = new Game();
        do{
          line = reader.readLine();
          if(line != null){
            if(line.startsWith("[Event")){
              tmp.type = line.substring(8,line.length()-2);
            }
            if(line.startsWith("[Site")){
              tmp.url = line.substring(27,line.length()-2);
            }
            if(line.startsWith("[White ")){
              tmp.whitePlayer = line.substring(8,line.length()-2);
            }
            if(line.startsWith("[Black ")){
              tmp.blackPlayer = line.substring(8,line.length()-2);
            }
            if(line.startsWith("[Result")){
              tmp.result = line.substring(9,line.length()-2);
            }
            if(line.startsWith("[UTCDate")){
              tmp.result = line.substring(10,line.length()-2);
            }
            if(line.startsWith("[Opening")){
              tmp.opening = line.substring(10,line.length()-2);
            }
          }
        }while(line!=null);
        ressources.incrGame();
        //ressources.extractPlayerData(tmp);
        //ressources.extractOpeningIteration(tmp);
        //ressources.extractUrl(tmp);
        gameText = buffer.pop();
      }
    }catch(IOException e){
      e.printStackTrace();
    }
  }
}
