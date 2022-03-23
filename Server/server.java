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
    try{
      OutputStream outputStream = clientSocket.getOutputStream();
      DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
      InputStream inputStream = clientSocket.getInputStream();
      DataInputStream dataInputStream = new DataInputStream(inputStream);


      if(playerGames!=null && playerGames.length>0){
        dataOutputStream.writeUTF(playerGames.length+" games found\n");
        dataOutputStream.writeUTF("Do you want to see all games ? (y or whatever)");
        String answer = dataInputStream.readUTF();
        if(answer.equals("y")) {
          for(int i=0;i<playerGames.length;i++){
            dataOutputStream.writeUTF(playerGames[i].toString());
          }
        }else {
          dataOutputStream.writeUTF("End of communication");
        }
      }else dataOutputStream.writeUTF("No game found for this nickname\n");
      dataOutputStream.writeUTF("Fin de la requête");

      dataOutputStream.writeUTF("End of communication");
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


      while(!clientSocket.isClosed()) {
        String received = dataInputStream.readUTF();
        if(received.equals("exit")) {
          dataOutputStream.writeUTF("End of communication");
          System.out.println("Closed connexion with"+clientSocket.getInetAddress());
          clientSocket.close();
          clients.remove(this);
        }else
        if(received.startsWith("search"))search(received.substring(7,received.length()));
        else {
          dataOutputStream.writeUTF("try sth else");
          dataOutputStream.writeUTF("End of communication");

        }
        received="";
      }
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
