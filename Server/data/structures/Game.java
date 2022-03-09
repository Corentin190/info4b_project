package data.structures;

import java.io.*;

public class Game{
  public String type;
  public String url;
  public String whitePlayer;
  public String blackPlayer;
  public String result;
  public String opening;
  public String moves;
  public int line;

  public Game(){
  }

  public Game(String file,)

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
