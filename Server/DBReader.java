import java.io.*;
import java.util.*;
import data.structures.*;

public class DBReader{
  public static void main(String[] args) {
    try(FileInputStream in = new FileInputStream("lichess_db_standard_rated_2013-01.pgn");){
      StreamTokenizer st = new StreamTokenizer(in);
      int token;
      int cpt = 0;
      do{
        Game tmp = new Game();
        int start = st.lineno();
        long startingByte = in.getChannel().position();
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
        }while(st.lineno()-start < 18);
        cpt++;
        System.out.println("=========== Game "+cpt+" - DONE ! ===========");
        System.out.println(tmp.blackPlayer+" > "+"lichess_db_standard_rated_2013-01.pgn"+" > "+startingByte);
        System.out.println(tmp.whitePlayer+" > "+"lichess_db_standard_rated_2013-01.pgn"+" > "+startingByte);
        if(new File("Players/"+tmp.blackPlayer+".dat").exists()){
          Player blackPlayer = new Player("Players/"+tmp.blackPlayer+".dat");
          blackPlayer.addGame("lichess_db_standard_rated_2013-01.pgn",startingByte);
          blackPlayer.savePlayer("Players/"+tmp.blackPlayer+".dat");
        }else{
          Player blackPlayer = new Player();
          blackPlayer.pseudo = tmp.blackPlayer;
          blackPlayer.addGame("lichess_db_standard_rated_2013-01.pgn",startingByte);
          blackPlayer.savePlayer("Players/"+tmp.blackPlayer+".dat");
        }
        if(new File("Players/"+tmp.whitePlayer+".dat").exists()){
          Player whitePlayer = new Player("Players/"+tmp.whitePlayer+".dat");
          whitePlayer.addGame("lichess_db_standard_rated_2013-01.pgn",startingByte);
          whitePlayer.savePlayer("Players/"+tmp.whitePlayer+".dat");
        }else{
          Player whitePlayer = new Player();
          whitePlayer.pseudo = tmp.whitePlayer;
          whitePlayer.addGame("lichess_db_standard_rated_2013-01.pgn",startingByte);
          whitePlayer.savePlayer("Players/"+tmp.whitePlayer+".dat");
        }
      }while(token != st.TT_EOF);
      in.close();
      System.out.println("====== FIN FICHIER ======");
    }catch (IOException e){
      e.printStackTrace();
    }
  }
}
