package data.structures;

import java.io.*;
import java.util.*;

public class Game{
  public String type;
  public String url;
  public String whitePlayer;
  public String blackPlayer;
  public String result;
  public String opening;
  public ArrayList<String> moves;
  public int line;
  public long startingByte;

  public static Game[] createFromFile(String file, long startingByte){
    return null;
  }

  public Game(){
    moves = new ArrayList<String>();
  }

  public Game(String file, long startingByte){
    this.startingByte = startingByte;
    moves = new ArrayList<String>();
    try {
      FileInputStream in = new FileInputStream("Src/"+file);
      in.skip(this.startingByte);
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      int blankLineCpt = 0;
      do{
        String lineContent = reader.readLine();
        //System.out.println(lineContent);
        if(lineContent != null){
          if(lineContent.startsWith("[Event")){
            this.type = lineContent.substring(8,lineContent.length()-2);
          }
          if(lineContent.startsWith("[Site")){
            this.url = lineContent.substring(7,lineContent.length()-2);
          }
          if(lineContent.startsWith("[White ")){
            this.whitePlayer = lineContent.substring(8,lineContent.length()-2);
          }
          if(lineContent.startsWith("[Black ")){
            this.blackPlayer = lineContent.substring(8,lineContent.length()-2);
          }
          if(lineContent.startsWith("[Result")){
            this.result = lineContent.substring(9,lineContent.length()-2);
          }
          if(lineContent.startsWith("[Opening")){
            this.opening = lineContent.substring(10,lineContent.length()-2);
          }
          if(lineContent.equals("")){
            blankLineCpt++;
          }
        }
      }while(blankLineCpt < 2);
      reader.close();
      in.close();
    } catch(IOException e) {
      e.printStackTrace();
    }
  }

  public String toString(){
    String res = "";
    res += "Type : "+this.type+"\n";
    res += "URL : "+this.url+"\n";
    res += "White : "+this.whitePlayer+"\n";
    res += "Black : "+this.blackPlayer+"\n";
    res += "Result : "+this.result+"\n";
    res += "Opening : "+this.opening+"\n";
    res += "Moves : "+this.moves+"\n";
    return res;
  }
}
