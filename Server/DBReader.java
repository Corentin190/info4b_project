import java.io.*;
import java.util.*;
import data.structures.*;
import data.searching.*;

class fileReader extends Thread{
  private ArrayList<String> fileFolder;
  public int cpt;

  public fileReader(ArrayList<String> fileFolder){
    this.fileFolder = fileFolder;
    this.cpt = 0;
  }

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

  private void extractOpeningIteration(Hashtable<String,Integer> openingHashtable, Game game) {
    if(openingHashtable.containsKey(game.opening)){
      openingHashtable.put(game.opening,openingHashtable.get(game.opening)+1);
    }else{
      openingHashtable.put(game.opening,1);
    }
  }

  private void saveOpeningData(File openingDataFile, Hashtable<String,Integer> openingHashtable){
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

  private void extractUrl(Hashtable<String,Long> urlHashtable, Game game) {
    urlHashtable.put(game.url,game.startingByte);
  }

  private void saveUrlIndex(File urlIndexFile, Hashtable<String,Long> urlHashtable){
    try{
      if(!urlIndexFile.exists())urlIndexFile.createNewFile();
      FileOutputStream out = new FileOutputStream(urlIndexFile,true);
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

  private synchronized String popFirstFile(){
    if(this.fileFolder.size()>0){
      String res = this.fileFolder.get(0);
      this.fileFolder.remove(0);
      return res;
    }else return null;
  }

  public void run(){
    try{
      while(!fileFolder.isEmpty()){

        String dataFile = popFirstFile();

        /*
        Création d'un fichier .dat correspondant aux données des joueurs.
        Création d'un lecteur permettant de lire le fichier .pgn et des Hashtable permettant le stockage temporaire des données traitées.
        */

        String playersDataFile = "Src/"+dataFile.substring(0,dataFile.length()-4)+"_player_data.dat";
        String openingDataFile = "Src/"+dataFile.substring(0,dataFile.length()-4)+"_opening_data.dat";
        String urlIndexFile = "Src/"+dataFile.substring(0,dataFile.length()-4)+"_url_index.dat";

        File outputPlayerData = new File(playersDataFile);
        File outputOpeningData = new File(openingDataFile);
        File outputUrlIndex = new File(urlIndexFile);

        if(!outputPlayerData.exists() || !outputOpeningData.exists() || !outputUrlIndex.exists()){

          if(outputUrlIndex.exists())outputUrlIndex.delete();

          FileInputStream in = new FileInputStream("Src/"+dataFile);
          BufferedReader reader = new BufferedReader(new InputStreamReader(in));
          Hashtable<String,Integer> openingHashtable = new Hashtable<String,Integer>();
          Hashtable<String,ArrayList<Long>> playersHashtable = new Hashtable<String,ArrayList<Long>>();
          Hashtable<String,Long> urlHashtable = new Hashtable<String,Long>();
          int lineCpt = 0;
          long byteCpt = 0;
          int startCpt = this.cpt;
          int saveCpt = 1;
          System.out.println(this.getName()+" : Processing "+dataFile);
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
                if(line.startsWith("[UTCDate")){
                  tmp.result = line.substring(10,line.length()-2);
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
            if(playersHashtable.size()>100000){
              //System.out.println(this.getName()+" : playersHashtable size : "+playersHashtable.size());
            }
            if(openingHashtable.size()>100000){
              //System.out.println(this.getName()+" : openingHashtable size : "+openingHashtable.size());
            }
            if(urlHashtable.size()>100000){
              //System.out.println(this.getName()+" : urlHashtable size : "+urlHashtable.size());
              saveUrlIndex(outputUrlIndex,urlHashtable);
              urlHashtable.clear();
            }
          }while(reader.ready());

          /*
          Fermeture du lecteur et sauvegarde des données traitées.
          */

          System.out.println(this.getName()+" : Saving data as "+playersDataFile);
          savePlayerData(outputPlayerData,playersHashtable);
          System.out.println(this.getName()+" : Saving data as "+openingDataFile);
          saveOpeningData(outputOpeningData,openingHashtable);
          System.out.println(this.getName()+" : Saving data as "+urlIndexFile);
          saveUrlIndex(outputUrlIndex,urlHashtable);
          reader.close();
          in.close();
          //================================ Print de debug ================================
          System.out.println(this.getName()+" : ==> "+(cpt-startCpt)+" Games read");
          System.out.println(this.getName()+" : ==> "+playersHashtable.size()+" Players saved");
        }
      }
    }catch(IOException e){
      e.printStackTrace();
    }
  }
}

public class DBReader{
  public static void main(String[] args) {
    int overall_cpt = 0;
    try{
      File folder = new File("Src/");

      /*
      Lecture de tous les fichiers contenus dans le dossier "Src/" et stockage de leur chemin dans une ArrayList.
      */

      ArrayList<String> fileFolder = new ArrayList<>();
      for(int i=0;i<folder.list().length;i++){
        if(folder.list()[i].endsWith(".pgn")){
          fileFolder.add(folder.list()[i]);
        }
      }
      fileFolder.sort(String::compareToIgnoreCase);

      ArrayList<fileReader> frTab = new ArrayList<fileReader>();
      for(int i=0;i<16;i++){
        frTab.add(new fileReader(fileFolder));
      }

      for(int i=0;i<frTab.size();i++) {
        frTab.get(i).start();
      }

      for(int i=0;i<frTab.size();i++){
        frTab.get(i).join();
        overall_cpt+=frTab.get(i).cpt;
        System.out.println("Thread ("+(i+1)+"/"+frTab.size()+") fini !");
      }
      System.out.format("\n\n==> %,8d Games read across all files\n", overall_cpt);
    }catch (InterruptedException e){
      e.printStackTrace();
    }
  }
}
