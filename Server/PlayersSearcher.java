import java.io.*;
import java.util.*;



public class PlayersSearcher{
  public static void main(String[] args) {
    File folder = new File("Src/");
    ArrayList<String> fileFolder = new ArrayList<>();
    for(int i=0;i<folder.list().length;i++){
      if(folder.list()[i].endsWith("_player_data.dat")){
        fileFolder.add(folder.list()[i]);
      }
    }
    try{
      fileFolder.sort(String::compareToIgnoreCase);
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
      File outputPlayer = new File("Src/playersData.dat");
      ArrayList<Map.Entry<String, Integer>> l = new ArrayList(players.entrySet());
      Collections.sort(l, new Comparator<Map.Entry<String, Integer>>(){
        public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2){
          return o2.getValue().compareTo(o1.getValue());
        }});
      String playersTab[] = new String[players.size()];
      for(int i=0;i<playersTab.length;i++){
        playersTab[i] = l.get(i).getKey();
      }
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