import java.io.*;
import java.util.*;
import data.structures.*;
import data.searching.*;

public class DBReader{

  /*
  Fonction récupérant depuis une instance de Game le nom du joueur noir, le nom du joueur blanc.
  Elle associe à ces noms l'octet de départ de la partie dans le fichier pgn afin de la retrouver plus rapidement lors d'une requête sur le serveur.
  */

  public static void extractPlayerData(Hashtable<String,ArrayList<Long>> playersHashtable, Game game){
    if(playersHashtable.containsKey(game.blackPlayer)) {
      playersHashtable.get(game.blackPlayer).add(game.startingByte);
    } else{
      playersHashtable.put(game.blackPlayer,new ArrayList<Long>());
      playersHashtable.get(game.blackPlayer).add(game.startingByte);
    }
    if(playersHashtable.containsKey(game.whitePlayer)) {
      playersHashtable.get(game.whitePlayer).add(game.startingByte);
    } else{
      playersHashtable.put(game.whitePlayer,new ArrayList<Long>());
      playersHashtable.get(game.whitePlayer).add(game.startingByte);
    }
  }

  /*
  Fonction écrivant les données des joueurs dans un fichier au format "lichess_db_standard_rated_AAAA-MM_player_data.dat".
  Les données sont passées en paramètre avec la Hashtable playersHashtable.
  Les données sont enregistrées au format suivant :
  [Pseudo "2girls1cup"]
  octet_partie_1 octet_partie_2 octet_partie_3 octet_partie_4 octet_partie_5
  [NumberOfGame 5]
  */

  public static void savePlayerData(File playerDataFile, Hashtable<String,ArrayList<Long>> playersHashtable){
    try{
      if(!playerDataFile.exists())playerDataFile.createNewFile();

      FileOutputStream out = new FileOutputStream(playerDataFile);
      OutputStreamWriter writer = new OutputStreamWriter(out);
      Enumeration<String> keys = playersHashtable.keys();

      //écriture de chaque joueur dans le fichier tant que l'énumération dispose de clé.
      while(keys.hasMoreElements()){
        String key = keys.nextElement();
        writer.write("[Pseudo \""+key+"\"]\n");
        ArrayList<Long> value = playersHashtable.get(key);
        for(int i=0;value != null && i<value.size();i++){
          writer.write(value.get(i)+" ");
        }
        writer.write("\n[NumberOfGame "+value.size()+"]\n");
      }
      writer.close();

    }catch(IOException e){
      e.printStackTrace();
    }
  }

  /*
  Fonction écrivant les données concernant les ouvertures dans un fichier au format "lichess_db_standard_rated_AAAA-MM_opening_data.dat".
  Les données sont enregistrées au format suivant :
  [Ouverture "openning_name"]
  [NumberOfOccurence "1337"]
  */

  public static void extractOpeningIteration(Hashtable<String,Integer> openingHashtable, Game game) {
    if(openingHashtable.containsKey(game.opening)){
      openingHashtable.put(game.opening,openingHashtable.get(game.opening)+1);
    }else{
      openingHashtable.put(game.opening,1);
    }
  }

  public static void saveOpeningData(File openingDataFile, Hashtable<String,Integer> openingHashtable){
    try{
      if(!openingDataFile.exists())openingDataFile.createNewFile();
      FileOutputStream out = new FileOutputStream(openingDataFile);
      OutputStreamWriter writer = new OutputStreamWriter(out);
      Enumeration<String> keys = openingHashtable.keys();
      while(keys.hasMoreElements()){
        String key = keys.nextElement();
        writer.write("[Opening \""+key+"\"]\n");
        writer.write("[NumberOfOccurence \""+openingHashtable.get(key)+"\"]\n");
      }
      writer.close();
    }catch(IOException e){
      e.printStackTrace();
    }
  }

  public static void extractUrl(Hashtable<String,Long> urlHashtable, Game game) {
    urlHashtable.put(game.url,game.startingByte);
  }

  public static void saveUrlIndex(File urlIndexFile, Hashtable<String,Long> urlHashtable){
    try{
      if(!urlIndexFile.exists())urlIndexFile.createNewFile();
      FileOutputStream out = new FileOutputStream(urlIndexFile);
      OutputStreamWriter writer = new OutputStreamWriter(out);
      Enumeration<String> keys = urlHashtable.keys();
      while(keys.hasMoreElements()){
        String key = keys.nextElement();
        writer.write("[Url \""+key+"\" : "+urlHashtable.get(key)+"]\n");
      }
      writer.close();
    }catch(IOException e){
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    int overall_cpt = 0;
    try{
      File folder = new File("Src/");

      /*
      Lecture de tous les fichiers contenus dans le dossier "Src/" et stockage de leur chemin dans une ArrayList.
      */

      ArrayList<String> fileFolder = new ArrayList<>();
      for(int i=0;i<folder.list().length;i++){
        fileFolder.add(folder.list()[i]);
      }

      /*
      Traitement de chaque fichier .pgn
      */

      for(int i=0;i<fileFolder.size();i++){
        String dataFile = fileFolder.get(i);
        if(dataFile.endsWith(".pgn")){
          /*
          Création d'un fichier .dat correspondant aux données des joueurs.
          Création d'un lecteur permettant de lire le fichier .pgn et des Hashtable permettant le stockage temporaire des données traitées.
          */
          String playersDataFile = "Src/"+dataFile.substring(0,dataFile.length()-4)+"_player_data.dat";
          String openingDataFile = "Src/"+dataFile.substring(0,dataFile.length()-4)+"_opening_data.dat";
          String urlIndexFile = "Src/"+dataFile.substring(0,dataFile.length()-4)+"_url_index.dat";
          FileInputStream in = new FileInputStream("Src/"+dataFile);
          BufferedReader reader = new BufferedReader(new InputStreamReader(in));
          Hashtable<String,Integer> openingHashtable = new Hashtable<String,Integer>();
          Hashtable<String,ArrayList<Long>> playersHashtable = new Hashtable<String,ArrayList<Long>>();
          Hashtable<String,Long> urlHashtable = new Hashtable<String,Long>();
          int cpt = 0;
          int lineCpt = 0;
          long byteCpt = 0;
          System.out.println("Processing "+dataFile);
          do{
            Game tmp = new Game();
            tmp.line = lineCpt;
            tmp.startingByte = byteCpt;
            String line = "";
            int blankLineCpt = 0;

            /*
            La section suivante lit ligne par ligne tant qu'elle ne rencontre pas 2 lignes blanches.
            Lorsque 2 lignes blanches ont été lues, une partie entière a été lue.
            Chaque ligne lue est traitée afin d'en extraire les informations et les stocker dans une instance de Game.
            */

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
              byteCpt += line.getBytes().length+1;
            }while(blankLineCpt < 2);
            cpt++;
            extractPlayerData(playersHashtable,tmp);
            extractOpeningIteration(openingHashtable,tmp);
            extractUrl(urlHashtable,tmp);
          }while(reader.ready());

          /*
          Fermeture du lecteur et sauvegarde des données traitées.
          */

          File outputPlayerData = new File(playersDataFile);
          File outputOpeningData = new File(openingDataFile);
          File outputUrlIndex = new File(urlIndexFile);
          System.out.println("Saving data as "+playersDataFile);
          savePlayerData(outputPlayerData,playersHashtable);
          System.out.println("Saving data as "+openingDataFile);
          saveOpeningData(outputOpeningData,openingHashtable);
          System.out.println("Saving data as "+urlIndexFile);
          saveUrlIndex(outputUrlIndex,urlHashtable);
          reader.close();
          in.close();
          //================================ Print de debug ================================
          System.out.println("==> "+cpt+" Games read");
          System.out.println("==> "+playersHashtable.size()+" Players saved");
          overall_cpt += cpt;
        }
      }
      System.out.println("\n\n==> "+overall_cpt+" Games read across all files");
    }catch (IOException e){
      e.printStackTrace();
    }
  }
}
