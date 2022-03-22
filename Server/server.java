import java.net.*;
import java.io.*;
import java.util.*;
import data.searching.*;
import data.structures.*;

class clientConnexion extends Thread{
  private Socket clientSocket;
  private ArrayList<clientConnexion> clients;

  public clientConnexion(Socket clientSocket, ArrayList<clientConnexion> clients){
    this.clientSocket = clientSocket;
    this.clients = clients;
  }

  private void search(String nickname){
    PlayerGamesSearcher searcher = new PlayerGamesSearcher(nickname);
    Game[] playerGames = searcher.load();
    if(playerGames!=null && playerGames.length>0){
      System.out.println(playerGames.length+" games found");
      for(int i=0;i<playerGames.length;i++){
        //System.out.println(playerGames[i].toString());
      }
    }else System.out.println("No game found for this nickname");
    try{
      clientSocket.getOutputStream().write("Fin de la requête".getBytes());
    }catch(IOException e){
      e.printStackTrace();
    }
  }

  public void run(){
    try{
      InputStreamReader reader = new InputStreamReader(clientSocket.getInputStream());
      int line = 0;

      String received="";
      while(line!=(-1)){
        line = reader.read();
        if(line!=(-1)){
          received+=(char)line;
        }
      }
      System.out.println(received);
      if(received.startsWith("search"))search(received.substring(7,received.length()));
      System.out.println("Closed connexion with"+clientSocket.getInetAddress());
      clientSocket.close();
      clients.remove(this);
    }catch(IOException e){
      e.printStackTrace();
    }
  }
}

public class server{
  public static void main(String[] args) {
    try{
      final int port = 1085;
      ServerSocket server = new ServerSocket(port);
      System.out.println("Server listenning on port "+port);
      ArrayList<clientConnexion> clients = new ArrayList<clientConnexion>();
      while(true){
        if(clients.size() < 5){
          Socket clientSocket = server.accept();
          System.out.println("New connexion from "+clientSocket.getInetAddress()+" ("+clients.size()+")");
          clients.add(new clientConnexion(clientSocket,clients));
          clients.get(clients.size()-1).start();
        }
      }
    }catch(IOException e){
      e.printStackTrace();
    }
  }
}
