import java.io.*;
import java.util.*;
import data.structures.*;

public class DBReader{
  public static void main(String[] args) {
    try(FileInputStream in = new FileInputStream("Src/lichess_db_standard_rated_2013-01.pgn");){
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      int cpt = 0;
      int lineCpt = 0;
      ArrayList<String> playerNickname = new ArrayList<String>();
      ArrayList<Player> players = new ArrayList<Player>();
      if(!new File("Players/").exists())new File("Players/").mkdir();
      System.out.println("Processing database file");
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
        // System.out.println("=========== Game "+cpt+" - DONE ! ===========");
        // System.out.println(tmp.blackPlayer+" > "+"Src/lichess_db_standard_rated_2013-01.pgn"+" > "+startingLine);
        // System.out.println(tmp.whitePlayer+" > "+"Src/lichess_db_standard_rated_2013-01.pgn"+" > "+startingLine);
        if(playerNickname.contains(tmp.blackPlayer)){
          Player blackPlayer = players.get(playerNickname.indexOf(tmp.blackPlayer));
          blackPlayer.addGame("Src/lichess_db_standard_rated_2013-01.pgn",(long)startingLine);
        }else{
          Player blackPlayer = new Player();
          blackPlayer.pseudo = tmp.blackPlayer;
          blackPlayer.addGame("Src/lichess_db_standard_rated_2013-01.pgn",(long)startingLine);
          playerNickname.add(blackPlayer.pseudo);
          players.add(blackPlayer);
        }
        if(playerNickname.contains(tmp.whitePlayer)){
          Player whitePlayer = players.get(playerNickname.indexOf(tmp.whitePlayer));
          whitePlayer.addGame("Src/lichess_db_standard_rated_2013-01.pgn",(long)startingLine);
        }else{
          Player whitePlayer = new Player();
          whitePlayer.pseudo = tmp.whitePlayer;
          whitePlayer.addGame("Src/lichess_db_standard_rated_2013-01.pgn",(long)startingLine);
          playerNickname.add(whitePlayer.pseudo);
          players.add(whitePlayer);
        }
      }while(reader.ready());
      System.out.println("Saving data");
      for(int i=0;i<players.size();i++){
        players.get(i).savePlayer("Players/"+players.get(i).pseudo+".dat");
      }
      in.close();
      System.out.println(cpt+" Games read");
    }catch (IOException e){
      e.printStackTrace();
    }
  }
}
