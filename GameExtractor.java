import java.io.*;

class Game{
  public String type;
  public String url;
  public String whitePlayer;
  public String blackPlayer;
  public String result;
  public String opening;

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

public class GameExtractor{
  public static void main(String arg[]){
    try(FileInputStream in = new FileInputStream("lichess_db_standard_rated_2013-01.pgn");){
      StreamTokenizer st = new StreamTokenizer(in);
      int token;
      st.ordinaryChar((int)'#');
      Game tmp = new Game();
      do{
        token = st.nextToken();
        if(st.sval != null && st.sval.equals("Event")){
          st.nextToken();
          tmp.type = st.sval;
        }else if(st.sval != null && st.sval.equals("Site")){
          st.nextToken();
          tmp.url = st.sval;
        }else if(st.sval != null && st.sval.equals("White")){
          st.nextToken();
          tmp.whitePlayer = st.sval;
        }else if(st.sval != null && st.sval.equals("Black")){
          st.nextToken();
          tmp.blackPlayer = st.sval;
        }else if(st.sval != null && st.sval.equals("Result")){
          st.nextToken();
          tmp.result = st.sval;
        }else if(st.sval != null && st.sval.equals("Opening")){
          st.nextToken();
          tmp.opening = st.sval;
        }
      }while(st.lineno() < 120000*18);
      System.out.println(tmp.toString());
      System.out.println("====== FIN FICHIER ======");
    }catch (IOException e) {
      e.printStackTrace();
    }
  }
}



// import java.nio.file.*;

// class Main {
//   public static void main(String[] args) {

//     try {

//       Path file = Paths.get("input.txt");

//       long count = Files.lines(file).count();
//       System.out.println("Total Lines: " + count);

//     } catch (Exception e) {
//       e.getStackTrace();
//     }
//   }
// }