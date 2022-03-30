import java.util.*;
import java.io.*;
import data.structures.*;
import data.searching.*;

public class PlayersCount{
	public static void main(String[] args) {
		long overallCpt=0;
		try{
	      File folder = new File("Src/");
	      ArrayList<String> fileFolder = new ArrayList<>();
	      for(int i=0;i<folder.list().length;i++){
	        fileFolder.add(folder.list()[i]);
	      }
	      fileFolder.sort(String::compareToIgnoreCase);
	      for(int i=0;i<fileFolder.size();i++){
	        String dataFile = fileFolder.get(i);
	        if(dataFile.endsWith("_player_data.dat")){
	          FileInputStream in = new FileInputStream("Src/"+dataFile);
	          BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	          System.out.println("Processing "+dataFile);
	          String line = "";
	          do{
	          	reader.readLine();
	          	overallCpt++;
	          }while(reader.ready());
	          overallCpt=(overallCpt-1)/3;
	          reader.close();
	          in.close();
	        }
	      }
	      System.out.format("%,8d players in total\n", overallCpt);
	    }catch (IOException e){
	      e.printStackTrace();
	    }
	}
}