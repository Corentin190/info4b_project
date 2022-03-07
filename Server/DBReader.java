import java.io.*;
import java.util.*;
import data.structures.*;

public class DBReader{
  public static void main(String[] args) {
    try(FileInputStream in = new FileInputStream("lichess_db_standard_rated_2013-01.pgn");){
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      int cpt = 0;
      int lineCpt = 0;
      do{
        Game tmp = new Game();
        int startingLine = lineCpt;
        String line = "";
        int blankLineCpt = 0;
        do{
          line = reader.readLine();
          if(line != null){
            if(line.startsWith("[Event")){
              tmp.type = line.substring(8,line.length()-2);
            }
            if(line.startsWith("[Site")){
              tmp.url = line.substring(7,line.length()-2);
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
            if(line.startsWith("[Opening")){
              tmp.opening = line.substring(10,line.length()-2);
            }
            if(line.equals("")){
              blankLineCpt++;
            }
          }
          lineCpt++;
        }while(blankLineCpt < 2);
        cpt++;
        System.out.println("=========== Game "+cpt+" - DONE ! ===========");
        // System.out.println(tmp.blackPlayer+" > "+"lichess_db_standard_rated_2013-01.pgn"+" > "+startingLine);
        // System.out.println(tmp.whitePlayer+" > "+"lichess_db_standard_rated_2013-01.pgn"+" > "+startingLine);
        if(new File("Players/"+tmp.blackPlayer+".dat").exists()){
          Player blackPlayer = new Player("Players/"+tmp.blackPlayer+".dat");
          blackPlayer.addGame("lichess_db_standard_rated_2013-01.pgn",(long)startingLine);
          blackPlayer.savePlayer("Players/"+tmp.blackPlayer+".dat");
        }else{
          Player blackPlayer = new Player();
          blackPlayer.pseudo = tmp.blackPlayer;
          blackPlayer.addGame("lichess_db_standard_rated_2013-01.pgn",(long)startingLine);
          blackPlayer.savePlayer("Players/"+tmp.blackPlayer+".dat");
        }
        if(new File("Players/"+tmp.whitePlayer+".dat").exists()){
          Player whitePlayer = new Player("Players/"+tmp.whitePlayer+".dat");
          whitePlayer.addGame("lichess_db_standard_rated_2013-01.pgn",(long)startingLine);
          whitePlayer.savePlayer("Players/"+tmp.whitePlayer+".dat");
        }else{
          Player whitePlayer = new Player();
          whitePlayer.pseudo = tmp.whitePlayer;
          whitePlayer.addGame("lichess_db_standard_rated_2013-01.pgn",(long)startingLine);
          whitePlayer.savePlayer("Players/"+tmp.whitePlayer+".dat");
        }
      }while(reader.ready());
      in.close();
      System.out.println(cpt);
      System.out.println("====== FIN FICHIER ======");
    }catch (IOException e){
      e.printStackTrace();
    }
  }
}
