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
    try{
      OutputStream outputStream = clientSocket.getOutputStream();
      DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
      InputStream inputStream = clientSocket.getInputStream();
      DataInputStream dataInputStream = new DataInputStream(inputStream);
      int nbGamesFound = 0;
      do{
        Game[] playerGames = searcher.loadNextThousand();
        nbGamesFound = playerGames.length;
        if(playerGames!=null && playerGames.length>0){
          dataOutputStream.writeUTF("[METADATA]");
          dataOutputStream.writeUTF(playerGames.length+" games found.");
          dataOutputStream.writeUTF("[/METADATA]");
          for(int i=0;i<playerGames.length;i++){
            dataOutputStream.writeUTF("Type : "+playerGames[playerGames.length-i-1].type);
            dataOutputStream.writeUTF("URL : "+playerGames[playerGames.length-i-1].url);
            dataOutputStream.writeUTF("White : "+playerGames[playerGames.length-i-1].whitePlayer);
            dataOutputStream.writeUTF("Black : "+playerGames[playerGames.length-i-1].blackPlayer);
            dataOutputStream.writeUTF("Result : "+playerGames[playerGames.length-i-1].result);
            dataOutputStream.writeUTF("Date : "+playerGames[playerGames.length-i-1].date);
            dataOutputStream.writeUTF("Opening : "+playerGames[playerGames.length-i-1].opening);
            dataOutputStream.writeUTF("\n");
          }
          dataOutputStream.writeUTF("fin");
        }else dataOutputStream.writeUTF("No game found for this nickname\n");
      }while(nbGamesFound>=1000 && dataInputStream.readUTF().equals("keep_reading"));
    }catch(IOException e){
      e.printStackTrace();
    }
  }

  public void run(){
    try{
      InputStream inputStream = clientSocket.getInputStream();
      DataInputStream dataInputStream = new DataInputStream(inputStream);
      OutputStream outputStream = clientSocket.getOutputStream();
      DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
      String received = dataInputStream.readUTF();
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
