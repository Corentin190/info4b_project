import java.io.*;
import java.util.*;
import data.structures.*;

public class DBReader{
  public static void extractPlayerData(Hashtable<String,ArrayList<Integer>> playersHashtable, Game game){
    if(playersHashtable.containsKey(game.blackPlayer) && playersHashtable.containsKey(game.whitePlayer)){
      ArrayList<Integer> blackGames = playersHashtable.get(game.blackPlayer);
      ArrayList<Integer> whiteGames = playersHashtable.get(game.whitePlayer);
      if(!blackGames.contains(game.line))blackGames.add(game.line);
      if(!whiteGames.contains(game.line))whiteGames.add(game.line);
    }else if(!playersHashtable.containsKey(game.blackPlayer)){
      playersHashtable.put(game.blackPlayer,new ArrayList<Integer>());
      playersHashtable.get(game.blackPlayer).add(game.line);
    }else{
      playersHashtable.put(game.whitePlayer,new ArrayList<Integer>());
      playersHashtable.get(game.whitePlayer).add(game.line);
    }
  }

  public static void savePlayerData(File playerDataFile, Hashtable<String,ArrayList<Integer>> playersHashtable){
    try{
      if(!playerDataFile.exists())playerDataFile.createNewFile();
      FileOutputStream out = new FileOutputStream(playerDataFile);
      ObjectOutputStream oout = new ObjectOutputStream(out);
      oout.writeObject(playersHashtable);
      oout.close();
      out.close();
    }catch(IOException e){
      e.printStackTrace();
    }
  }

  public static void extractOpeningIteration(Hashtable<String,Integer> openingHashtable, Game game) {
    if(openingHashtable.containsKey(game.opening)){
      openingHashtable.put(game.opening,openingHashtable.get(game.opening)+1);
    }else{
      openingHashtable.put(game.opening,1);
    }
  }

  public static void displayOpeningIteration(Hashtable<String,Integer> openingHashtable) {
    openingHashtable.forEach((key, value) -> {
            System.out.println("Opening: "+key+" & Iterations: "+value);
        });
  }

  public static void displayTopOpening(Hashtable<String,Integer> openingHashtable, int top) {
    //display top opening
    Hashtable<String,Integer> localHashtable = openingHashtable;
    ArrayList<String> listKeys = Collections.list(localHashtable.keys());
    String topOpening=listKeys.get((int)Math.random()*listKeys.size());
    Set keys = localHashtable.keySet();
    Iterator itr = keys.iterator();
    for(int i=1;i<=top;i++) {
      for(int j=0;j<listKeys.size();j++) {
        if(localHashtable.get(listKeys.get(j))>localHashtable.get(topOpening)) {
          topOpening=listKeys.get(j);
        }
      }
      System.out.println("Top "+i+" opening :: "+topOpening+" with "+localHashtable.get(topOpening)+" iterations");
      listKeys.remove(topOpening);
      topOpening=listKeys.get((int)Math.random()*listKeys.size());
    }
  }

  public static void main(String[] args) {

    try{
      String dataFile = "Src/lichess_db_standard_rated_2013-01.pgn";
      String playersDataFile = "Src/lichess_db_standard_rated_2013-01_player_data.dat";
      FileInputStream in = new FileInputStream(dataFile);
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      Hashtable<String,Integer> openingHashtable = new Hashtable<String,Integer>();
      int cpt = 0;
      int lineCpt = 0;
      Hashtable<String,ArrayList<Integer>> playersHashtable = new Hashtable<String,ArrayList<Integer>>();
      System.out.println("Processing database file");
      do{
        Game tmp = new Game();
        int startingLine = lineCpt;
        tmp.line = startingLine;
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
        extractPlayerData(playersHashtable,tmp);
        extractOpeningIteration(openingHashtable,tmp);
      }while(reader.ready());
      System.out.println("Saving data");
      File output = new File(playersDataFile);
      savePlayerData(output,playersHashtable);
      in.close();
      //================================ Print de debug ================================
      System.out.println(cpt+" Games read");
      Enumeration<String> en = playersHashtable.keys();
      int cptPlayer = 0;
      while(en.hasMoreElements()){
        cptPlayer++;
        String key = en.nextElement();
      }
      System.out.println(cptPlayer+" Player saved");
    //  displayOpeningIteration(openingHashtable);
      displayTopOpening(openingHashtable,5);
    }catch (IOException e){
      e.printStackTrace();
    }
  }
}
