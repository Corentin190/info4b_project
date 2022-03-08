import java.io.*;
import java.util.*;
import data.structures.*;

public class DBReader{
  public static void main(String[] args) {
    try{
      FileInputStream in = new FileInputStream("Src/lichess_db_standard_rated_2013-01.pgn");
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      int cpt = 0;
      int lineCpt = 0;
      Hashtable<String,ArrayList<Integer>> playersHashtable = new Hashtable<String,ArrayList<Integer>>();
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
              if(topOpening.containsKey(tmp.opening)){
                topOpening.put(tmp.opening,topOpening.get(tmp.opening)+1);
              }else{
                topOpening.put(tmp.opening,1);
              }
            }
            if(line.equals("")){
              blankLineCpt++;
            }
          }
          lineCpt++;
        }while(blankLineCpt < 2);
        cpt++;
        if(playersHashtable.containsKey(tmp.blackPlayer) && playersHashtable.containsKey(tmp.whitePlayer)){
          ArrayList<Integer> blackGames = playersHashtable.get(tmp.blackPlayer);
          ArrayList<Integer> whiteGames = playersHashtable.get(tmp.whitePlayer);
          if(!blackGames.contains(lineCpt))blackGames.add(lineCpt);
          if(!whiteGames.contains(lineCpt))whiteGames.add(lineCpt);
        }else if(!playersHashtable.containsKey(tmp.blackPlayer)){
          playersHashtable.put(tmp.blackPlayer,new ArrayList<Integer>());
          playersHashtable.get(tmp.blackPlayer).add(lineCpt);
        }else{
          playersHashtable.put(tmp.whitePlayer,new ArrayList<Integer>());
          playersHashtable.get(tmp.whitePlayer).add(lineCpt);
        }
      }while(reader.ready());
      System.out.println("Saving data");
      File output = new File("Src/lichess_db_standard_rated_2013-01.dat");
      if(!output.exists())output.createNewFile();
      FileOutputStream out = new FileOutputStream(output);
      ObjectOutputStream oout = new ObjectOutputStream(out);
      oout.writeObject(playersHashtable);
      oout.close();
      out.close();
      in.close();
      System.out.println(cpt+" Games read");
      Enumeration<String> en = playersHashtable.keys();
      int cptPlayer = 0;
      while(en.hasMoreElements()){
        cptPlayer++;
        String key = en.nextElement();
      }
      System.out.println(cptPlayer+" Player saved");
      //    System.out.println(topOpening.toString());
      Set keys = topOpening.keySet();
      Iterator itr = keys.iterator();
      String key="";
      while (itr.hasNext()) {
        key = (String) itr.next();
        System.out.println("Opening: "+key+" & Occurence: "+topOpening.get(key));
      }
    }catch (IOException e){
      e.printStackTrace();
    }
  }
}
