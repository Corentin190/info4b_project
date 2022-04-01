import java.util.*;
import java.io.*;

/*class GameBuffer{
  public String buffer;

  public GameBuffer(final int BUFFER_SIZE){
    this.buffer = new String[BUFFER_SIZE];
  }
}
*/
public class DBReader2{
  public static void main(String[] args) {
    try{
      File folder = new File("Src/");
      ArrayList<String> fileFolder = new ArrayList<>();
      for(int i=0;i<folder.list().length;i++){
        if(folder.list()[i].endsWith(".pgn")){
          fileFolder.add(folder.list()[i]);
        }
      }
      fileFolder.sort(String::compareToIgnoreCase);

      for(int i=0;i<fileFolder.size();i++){
        String dataFile = fileFolder.get(i);
        FileInputStream in = new FileInputStream("Src/"+dataFile);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        ArrayList<String> bufferSimulation = new ArrayList<String>();
        long prevTime = System.currentTimeMillis();
        while(reader.ready()){
          String gameText = "";
          String line;
          int blankLineCpt = 0;
          do{
            line = reader.readLine();
            if(line != null){
              gameText+=line;
              gameText+="\n";
              if(line.equals("")){
                blankLineCpt++;
              }
            }
          }while(blankLineCpt<2);
          bufferSimulation.add(gameText);
        }
        System.out.println("Time to read "+dataFile+" ("+bufferSimulation.size()+" games) : "+(System.currentTimeMillis()-prevTime)+"ms");
      }
    }catch(IOException e){
        e.printStackTrace();
    }
  }
}
