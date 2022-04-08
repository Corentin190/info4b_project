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
  public String date;
  public ArrayList<String> moves;
  public int line;
  public long startingByte;
  /*
  This static function is very helpful to create multiple instances of Game at once like when you want to read every games of a specific player over a certain amount of time.
  The fact that it creates multiple games from a single file and an array of long representing the starting bytes of the game inside the file,
  we can greatly reduce reading time by creating only one input stream and reading all games by skipping from one to the other in a snap.
  */
  public static Game[] createFromFile(String file, long[] startingBytes){
    Game[] games = new Game[startingBytes.length];
    try {
      FileInputStream in = new FileInputStream("Src/"+file);
      for(int i=0;i<games.length;i++){
        if(i<1)in.skip(startingBytes[i]);
        else in.skip(startingBytes[i]-in.getChannel().position());
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        int blankLineCpt = 0;
        games[i] = new Game();
        do{
          String lineContent = reader.readLine();
          if(lineContent != null){
            if(lineContent.startsWith("[Event")){
              games[i].type = lineContent.substring(8,lineContent.length()-2);
            }
            if(lineContent.startsWith("[Site")){
              games[i].url = lineContent.substring(27,lineContent.length()-2);
            }
            if(lineContent.startsWith("[White ")){
              games[i].whitePlayer = lineContent.substring(8,lineContent.length()-2);
            }
            if(lineContent.startsWith("[Black ")){
              games[i].blackPlayer = lineContent.substring(8,lineContent.length()-2);
            }
            if(lineContent.startsWith("[Result")){
              games[i].result = lineContent.substring(9,lineContent.length()-2);
            }
            if(lineContent.startsWith("[UTCDate")){
              games[i].date = lineContent.substring(10,lineContent.length()-2);
            }
            if(lineContent.startsWith("[Opening")){
              games[i].opening = lineContent.substring(10,lineContent.length()-2);
            }
            if(lineContent.equals("")){
              blankLineCpt++;
            }
            //Partie a check, experimentale
            if(blankLineCpt == 1){
              StringTokenizer token = new StringTokenizer();
              while(token.hasMoreElements())games[i].moves.add(token.nextElement());
            }
          }
        }while(blankLineCpt < 2);
      }
      in.close();
    }catch(IOException e) {
      e.printStackTrace();
    }
    return games;
  }

  public Game(){
    moves = new ArrayList<String>();
  }
  /*
  Create an instance of Game by reading it from a specific File and a specific position in the file represented by the startingByte arg.
  */
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
        if(lineContent != null){
          if(lineContent.startsWith("[Event")){
            this.type = lineContent.substring(8,lineContent.length()-2);
          }
          if(lineContent.startsWith("[Site")){
            this.url = lineContent.substring(27,lineContent.length()-2);
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
          if(lineContent.startsWith("[UTCDate")){
            this.date = lineContent.substring(10,lineContent.length()-2);
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
    res += "URL : https://lichess.org/"+this.url+"\n";
    res += "White : "+this.whitePlayer+"\n";
    res += "Black : "+this.blackPlayer+"\n";
    res += "Result : "+this.result+"\n";
    res += "Date : "+this.date+"\n";
    res += "Opening : "+this.opening+"\n";
    res += "Moves : "+this.moves+"\n";
    return res;
  }
}
