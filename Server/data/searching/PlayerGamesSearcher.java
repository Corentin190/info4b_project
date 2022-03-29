package data.searching;

import java.io.*;
import java.util.*;
import data.structures.Game;

public class PlayerGamesSearcher{
  private String playerName;
  private ArrayList<Game> playerGames;
  private int lastFile;
  private int lastByte;

  public PlayerGamesSearcher(String playerName){
      this.playerName = playerName;
      this.playerGames = new ArrayList<Game>();
      resetMark();
  }

  private void mark(int lastFile, int lastByte){
    this.lastFile = lastFile;
    this.lastByte = lastByte;
  }

  private void resetMark(){
    this.lastFile = 0;
    this.lastByte = 0;
  }

  public Game[] loadNextThousand(){
    try{
      this.playerGames.clear();
      File folder = new File("Src/");
      ArrayList<String> fileFolder = new ArrayList<>();
      for(int i=0;i<folder.list().length;i++){
        if(folder.list()[i].endsWith("_player_data.dat"))fileFolder.add(folder.list()[i]);
      }
      fileFolder.sort(String::compareToIgnoreCase);

      System.out.println("Resuming at "+fileFolder.get(this.lastFile)+" at byte number : "+this.lastByte);

      /*
      Parcours de tous les fichiers et traitement des fichiers XXXXXX_player_data.dat
      */

      boolean stopped = false;
      int fileIndex = this.lastFile;
      int cpt = 0;
      do{
        long previousTime = System.currentTimeMillis();
        System.out.println("======= Reading "+fileFolder.get(fileIndex)+"=======");

        /*
        Création du lecteur pour la lecture du fichier et recherche du joueur.
        */

        FileInputStream in = new FileInputStream("Src/"+fileFolder.get(fileIndex));
        System.out.println("Reading content");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        System.out.println("Scanning for "+this.playerName+" games");
        String line = "";
        Boolean found = false;
        while(reader.ready() && !found){
          line = reader.readLine();
          if(line.equals("[Pseudo \""+this.playerName+"\"]"))found = true;
        }
        if(found){
          System.out.println(this.playerName+" found ! ("+(System.currentTimeMillis()-previousTime)+"ms)");
          /*
          Lecture de la ligne contenant tous les octets de départ des parties du joueur trouvé.
          Ajout de chaque octet de départ dans une ArrayList et extraction de toutes les parties.
          */
          line = reader.readLine();
          StringTokenizer tokenizer = new StringTokenizer(line);
          String pgnFile = fileFolder.get(fileIndex).substring(0,fileFolder.get(fileIndex).length()-16)+".pgn";
          ArrayList<Long> startingBytes = new ArrayList<Long>();
          for(int i=0;i<this.lastByte;i++){
            tokenizer.nextToken();
          }
          int byteIndex = 0;
          while(tokenizer.hasMoreTokens() && !stopped){
            long startingByte = Long.parseLong(tokenizer.nextToken());
            cpt++;
            byteIndex++;
            startingBytes.add(startingByte);
            if(cpt>=1000){
              stopped = true;
              this.mark(fileIndex,byteIndex);
            }
          }
          long[] tab = new long[startingBytes.size()];
          for(int j=0;j<startingBytes.size();j++){
            tab[j] = startingBytes.get(j);
          }
          Game[] gamesTab = Game.createFromFile(pgnFile,tab);
          for(int j=0;j<startingBytes.size();j++){
            playerGames.add(gamesTab[j]);
          }
        }
        fileIndex++;
        if(!stopped)this.resetMark();
        else System.out.println("Stopping at "+fileFolder.get(this.lastFile)+" at byte number : "+this.lastByte);
      }while(fileIndex<fileFolder.size() && !stopped);
    }catch(IOException e) {
      e.printStackTrace();
    }
    Game[] res = new Game[playerGames.size()];
    playerGames.toArray(res);
    if(playerGames!=null && playerGames.size()>0)return res;
    else return null;
  }

  public Game[] load(){
    try{
      File folder = new File("Src/");
      ArrayList<String> fileFolder = new ArrayList<>();
      for(int i=0;i<folder.list().length;i++){
        fileFolder.add(folder.list()[i]);
      }
      fileFolder.sort(String::compareToIgnoreCase);

      /*
      Parcours de tous les fichiers et traitement des fichiers XXXXXX_player_data.dat
      */

      for(int i=0;i<fileFolder.size();i++){
        if(fileFolder.get(i).endsWith("_player_data.dat")){
          int cpt = playerGames.size();
          long previousTime = System.currentTimeMillis();
          System.out.println("======= Reading "+fileFolder.get(i)+"=======");

          /*
          Création du lecteur pour la lecture du fichier et recherche du joueur.
          */

          FileInputStream in = new FileInputStream("Src/"+fileFolder.get(i));
          System.out.println("Reading content");
          BufferedReader reader = new BufferedReader(new InputStreamReader(in));
          System.out.println("Scanning for "+this.playerName+" games");
          String line = "";
          Boolean found = false;
          while(reader.ready() && !found){
            line = reader.readLine();
            if(line.equals("[Pseudo \""+this.playerName+"\"]"))found = true;
          }
          if(found){
            System.out.println(this.playerName+" found ! ("+(System.currentTimeMillis()-previousTime)+"ms)");
            previousTime = System.currentTimeMillis();

            /*
            Lecture de la ligne contenant tous les octets de départ des parties du joueur trouvé.
            Ajout de chaque octet de départ dans une ArrayList et extraction de toutes les parties.
            */

            line = reader.readLine();
            StringTokenizer tokenizer = new StringTokenizer(line);
            String pgnFile = fileFolder.get(i).substring(0,fileFolder.get(i).length()-16)+".pgn";
            ArrayList<Long> startingBytes = new ArrayList<Long>();
            while(tokenizer.hasMoreTokens()){
              long startingByte = Long.parseLong(tokenizer.nextToken());
              startingBytes.add(startingByte);
            }
            long[] tab = new long[startingBytes.size()];
            for(int j=0;j<startingBytes.size();j++){
              tab[j] = startingBytes.get(j);
            }
            Game[] gamesTab = Game.createFromFile(pgnFile,tab);
            for(int j=0;j<startingBytes.size();j++){
              playerGames.add(gamesTab[j]);
            }
          }
          System.out.println((playerGames.size()-cpt)+" games found ("+(System.currentTimeMillis()-previousTime)+"ms)");
          previousTime = System.currentTimeMillis();
          reader.close();
          in.close();
          System.out.println("file closed");
        }
      }
    }catch(IOException e) {
      e.printStackTrace();
    }
    Game[] res = new Game[playerGames.size()];
    playerGames.toArray(res);
    if(playerGames!=null && playerGames.size()>0)return res;
    else return null;
  }
}
