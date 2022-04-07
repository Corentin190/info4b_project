import java.io.*;
import java.util.*;


/*
This function create a file called 'playersData.dat' that contains the name of all players with their number of game.
They are sorted from the most active to the less.
The most active who is the one with the higher count of game.
*/

public class PlayersSearcher{
  public static void main(String[] args) {
    //Creation of an arraylist wich contains all the data files '*_player_data.dat', in alphabetic order.
    File folder = new File("Src/");
    ArrayList<String> fileFolder = new ArrayList<>();
    for(int i=0;i<folder.list().length;i++){
      if(folder.list()[i].endsWith("_player_data.dat")){
        fileFolder.add(folder.list()[i]);
      }
    }
    fileFolder.sort(String::compareToIgnoreCase);
    try{
      Hashtable<String,Integer> players = new Hashtable<String, Integer>();
      String dataFile="";
      FileInputStream in;
      BufferedReader reader; 
      String player="";
      String line="";
      int nbGame;
      for(int i=0;i<fileFolder.size();i++){
        dataFile = fileFolder.get(i);
        System.out.println("Dealing with "+dataFile+"...");
        in = new FileInputStream("Src/"+dataFile);
        reader = new BufferedReader(new InputStreamReader(in));
        /*
        Reading of each line of the files, if the line begins with '[Pseudo', then the player is added to an Hashtable and his number of game is added too.
        If the player is already in the Hashtable, his number of game is updated.
        */
        do{
          line=reader.readLine();
          if(line.startsWith("[Pseudo")){
            player=line.substring(9,line.length()-2);
            reader.readLine();
            line=reader.readLine();
            nbGame = Integer.parseInt(line.substring(14,line.length()-1));
            if(players.containsKey(player)){
              players.put(player,(players.get(player)+nbGame));
            }else{
              players.put(player,nbGame);
            }
            
          }
        }while(reader.ready());
        System.out.println(dataFile+" done.");
      }
      /*
          Sort the Hashtable from the most to the less active.
      */
      ArrayList<Map.Entry<String, Integer>> l = new ArrayList(players.entrySet());
      Collections.sort(l, new Comparator<Map.Entry<String, Integer>>(){
        public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2){
          return o2.getValue().compareTo(o1.getValue());
        }});
      String playersTab[] = new String[players.size()];
      for(int i=0;i<playersTab.length;i++){
        playersTab[i] = l.get(i).getKey();
      }
      /*
      Writing of the Hashtable in file 'playersData.dat', in order to be read easily.
      */
      File outputPlayer = new File("Src/playersData.dat");
      FileOutputStream out = new FileOutputStream(outputPlayer);
      OutputStreamWriter writer = new OutputStreamWriter(out);
      Enumeration<String> keys = players.keys();
      for(int i=0; i<playersTab.length; i++){
        writer.write("[Pseudo \""+playersTab[i]+"\"]\n");
        writer.write("[NumberOfGame "+players.get(playersTab[i])+"]\n");
      }
      writer.close();
      out.close();
    }catch (IOException e){
      e.printStackTrace();
    }
  }
}