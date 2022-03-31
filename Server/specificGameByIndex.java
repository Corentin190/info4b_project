import java.util.*;
import java.io.*;
import data.structures.*;
import data.searching.*;

public class specificGameByIndex{
  public static void main(String[] args) {
    try{
      File folder = new File("Src/");
      ArrayList<String> fileFolder = new ArrayList<>();
      for(int i=0;i<folder.list().length;i++){
        fileFolder.add(folder.list()[i]);
      }
      fileFolder.sort(String::compareToIgnoreCase);
      Game tmp = new Game();
      String targetUrl = "sSiiPuco";
      for(int i=0;i<fileFolder.size();i++){
        String dataFile = fileFolder.get(i);
        if(dataFile.endsWith("_url_index.dat")){
          FileInputStream in = new FileInputStream("Src/"+dataFile);
          BufferedReader reader = new BufferedReader(new InputStreamReader(in));
          System.out.println("Processing "+dataFile);
          int moy = 0;
          int cpt = 0;
          String line = "";
          do{
            int blankLineCpt = 0;
            long startTime = System.currentTimeMillis();
            line = reader.readLine();
            if(line.startsWith("[Url \""+targetUrl+"\" : ")){
              String pgnFile = dataFile.substring(0,dataFile.length()-14)+".pgn";
              long startingByte = Long.parseLong(line.substring((10+targetUrl.length()),line.length()-1));
              System.out.println(pgnFile+" "+startingByte);
              tmp = new Game(pgnFile,startingByte);
              //
            }
          }while(reader.ready() && !line.startsWith("[Url \""+targetUrl+"\" : "));
          reader.close();
          in.close();
          if(tmp.url != null && tmp.url.equals(targetUrl))break;
        }
      }
      if(tmp.url != null && tmp.url.equals(targetUrl))System.out.println(tmp.toString());
      else System.out.println("No game found for this URL");
    }catch (IOException e){
      e.printStackTrace();
    }
  }
}
