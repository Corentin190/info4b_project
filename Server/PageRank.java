import java.io.*;
import java.util.*;
import data.structures.*;
import data.searching.*;



public class PageRank{

	// public static class Graph{
	// 	ArrayList<ArrayList<String>> graph;
	// 	int V;

	// 	Graph(int nodes) {
	// 		V = nodes;
	// 		graph = new ArrayList<ArrayList<String>>();
	// 		for(int i=0; i<V;i++){
	// 			graph.add(new ArrayList<String>());
	// 		}
	// 	}
	// 	void eddEdge(String player1, String player2){
	// 		graph.get(player1).add(player2);
	// 		graph.get(player2).add(player1);
	// 	}
	// 	void printGraph(){
	// 		for(int i=0;i<V;i++){
	// 			System.out.print("Node "+i);
	// 			for(int x : graph.get(i))System.out.print(" -> "+x);
	// 			System.out.println();
	// 		}
	// 	}
	// }



	private static long PlayersCount(){
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
	    }catch (IOException e){
	      e.printStackTrace();
	    }
	    return overallCpt;
	}

  public static void main(String args[]){
    double d = 0.85;	//DampingFactor
    double n = PlayersCount();	//N
    double initialPR = 1/PlayersCount();

    // Graph g = new Graph(5);
    // g.addEdge("Corentin", "Laura");
    // g.addEdge("Corentin", "Justine");
    // g.addEdge("Corentin", "Emmanuelle");
    // g.addEdge("Corentin", "Jeremie");
    // g.addEdge("Corentin", "Danielle");
    // g.addEdge("Corentin", "Roger");
    // g.addEdge("Corentin", "Romane");
    // g.printGraph();

  }
}
