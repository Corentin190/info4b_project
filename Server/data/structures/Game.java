package data.structures;

import java.io.*;

public class Game{
  public String type;
  public String url;
  public String whitePlayer;
  public String blackPlayer;
  public String result;
  public String opening;

  public Game(){
  }

  public Game(String file, long startingByte){
    try(FileInputStream in = new FileInputStream(file);){
      in.skip(startingByte);
      StreamTokenizer st = new StreamTokenizer(in);
      int token;
      do{
        token = st.nextToken();
        if(st.sval != null && st.sval.equals("Event")){
          st.nextToken();
          this.type = st.sval;
        }else if(st.sval != null && st.sval.equals("Site")){
          st.nextToken();
          this.url = st.sval;
        }else if(st.sval != null && st.sval.equals("White")){
          st.nextToken();
          this.whitePlayer = st.sval;
        }else if(st.sval != null && st.sval.equals("Black")){
          st.nextToken();
          this.blackPlayer = st.sval;
        }else if(st.sval != null && st.sval.equals("Result")){
          st.nextToken();
          this.result = st.sval;
        }else if(st.sval != null && st.sval.equals("Opening")){
          st.nextToken();
          this.opening = st.sval;
        }
      }while(st.lineno() < 18);
    }catch (IOException e) {
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
    return res;
  }
}
