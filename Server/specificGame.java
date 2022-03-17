import java.io.*;
import java.util.*;
import data.structures.*;

public class specificGame{
  public static void main(String[] args) {
    try{
      File folder = new File("Src/");
      ArrayList<String> fileFolder = new ArrayList<>();
      for(int i=0;i<folder.list().length;i++){
        fileFolder.add(folder.list()[i]);
      }
      Game tmp = new Game();
      String targetUrl = "https://lichess.org/WKwWnuil";
      for(int i=0;i<fileFolder.size();i++){
        String dataFile = fileFolder.get(i);
        if(dataFile.endsWith(".pgn")){
          FileInputStream in = new FileInputStream("Src/"+dataFile);
          BufferedReader reader = new BufferedReader(new InputStreamReader(in));
          System.out.println("Processing "+dataFile);
          do{
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
                if(!line.equals("") && tmp.url != null && tmp.url.equals(targetUrl) && blankLineCpt==1){
                  StringTokenizer tokenizer = new StringTokenizer(line);
                  while(tokenizer.hasMoreTokens()){
                    tmp.moves.add(tokenizer.nextToken());
                  }
                }
              }
            }while(blankLineCpt < 2);
          }while(reader.ready() && !tmp.url.equals(targetUrl));
          reader.close();
          in.close();
        }
      }
      if(tmp.url.equals(targetUrl))System.out.println(tmp.toString());
      else System.out.println("No game found for this URL");
    }catch (IOException e){
      e.printStackTrace();
    }
  }
}
