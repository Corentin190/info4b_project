import java.util.*;
import java.io.*;
import data.structures.*;
import data.handling.*;

//coucou

public class DBReader2{
  public static void main(String[] args) {
    /*
    Setting number of consumer threads and max GameBuffer size.
    We went for 1 000 000 games in buffer max size because it seemed like a good compromise between read speed and max RAM usage.
    */
    final int NB_THREAD;
    final int BUFFER_SIZE = 1000000;
    if(args.length>0 && Integer.parseInt(args[0])>1){
      NB_THREAD = Integer.parseInt(args[0])-1;
    }else NB_THREAD = 1;
    /*
    Setting up the "file to process"-list.
    Scanning for each files in Src/ and, if that file is a .pgn file AND it has one or multiple .dat file missing, adding it to the "file to process"-list.
    We also choose to sort the list for reasons related to the behaving of the program.
    */
    File folder = new File("Src/");
    ArrayList<String> fileFolder = new ArrayList<>();
    for(int i=0;i<folder.list().length;i++){
      if(folder.list()[i].endsWith(".pgn")){
        String playersDataFile = "Src/"+folder.list()[i].substring(0,folder.list()[i].length()-4)+"_player_data.dat";
        String openingDataFile = "Src/"+folder.list()[i].substring(0,folder.list()[i].length()-4)+"_opening_data.dat";
        String urlIndexFile = "Src/"+folder.list()[i].substring(0,folder.list()[i].length()-4)+"_url_index.dat";
        File outputPlayerData = new File(playersDataFile);
        File outputOpeningData = new File(openingDataFile);
        File outputUrlIndex = new File(urlIndexFile);
        if(!outputPlayerData.exists() || !outputOpeningData.exists() || !outputUrlIndex.exists()){
          fileFolder.add(folder.list()[i]);
        }
      }
    }
    fileFolder.sort(String::compareToIgnoreCase);
    /*
    For each file in the "file to process"-list, we create a GameBuffer, NB_THREAD-1 consumer threads and one reader that's going to read through the .pgn file.
    Once the reader is done it sends a signal to the buffer.
    Once all the consumer threads are done, the program prints some informations.
    */
    System.out.println(fileFolder.size()+" files to process");
    for(int i=0;i<fileFolder.size();i++){
      String dataFile = fileFolder.get(i);
      System.out.println("================= Processing "+dataFile+" =================");
      long prevTime = System.currentTimeMillis();
      GameBuffer buffer = new GameBuffer(BUFFER_SIZE);
      CommonRessources ressources = new CommonRessources(dataFile);
      PGNReader reader = new PGNReader(dataFile,buffer);
      InfoExtractor[] tab = new InfoExtractor[NB_THREAD];
      for(int j=0;j<tab.length;j++){
        tab[j] = new InfoExtractor(buffer,ressources);
      }
      for(int j=0;j<tab.length;j++){
        tab[j].start();
      }
      reader.start();
      try{
        reader.join();
        buffer.setReaderDone();
        System.out.println("Time to read "+dataFile+" : "+(System.currentTimeMillis()-prevTime)+"ms");
        for(int j=0;j<tab.length;j++){
          tab[j].join();
        }
        System.out.println("Buffer full event : "+buffer.bufferFullEvent);
        System.out.println("Buffer empty event : "+buffer.bufferEmptyEvent);
        System.out.println("Game processed : "+ressources.gameNumber);
        ressources.saveData();
        System.out.println("Time to process "+dataFile+" ("+ressources.gameNumber+" games) : "+(System.currentTimeMillis()-prevTime)+"ms");
      }catch(InterruptedException e){
        e.printStackTrace();
      }
    }
    /*
    This part creates a file called 'playersData.dat' that contains the name of all players with their number of game.
    They are sorted from the most active to the less.
    The most active who is the one with the higher count of game.
    */
    /*
    Creation of an arraylist wich contains all the data files '*_player_data.dat', in alphabetic order.
    */
    ArrayList<String> dataFileFolder = new ArrayList<>();
    for(int i=0;i<folder.list().length;i++){
      if(folder.list()[i].endsWith("_player_data.dat")){
        dataFileFolder.add(folder.list()[i]);
      }
    }
    dataFileFolder.sort(String::compareToIgnoreCase);
    try{
      Hashtable<String,Integer> players = new Hashtable<String, Integer>();
      String dataFile="";
      FileInputStream in;
      BufferedReader reader; 
      String player="";
      String line="";
      int nbGame;
      for(int i=0;i<dataFileFolder.size();i++){
        dataFile = dataFileFolder.get(i);
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
