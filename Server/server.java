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

  private void search(String nickname, int nbGames){
    PlayerGamesSearcher searcher = new PlayerGamesSearcher(nickname);
    try{
      OutputStream outputStream = clientSocket.getOutputStream();
      DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
      InputStream inputStream = clientSocket.getInputStream();
      DataInputStream dataInputStream = new DataInputStream(inputStream);
      int nbGamesFound = 0;
      Game[] playerGames = searcher.load(nbGames);
      for(int i=0;i<nbGames;i++){
        String result = "";
        result += ("============== Game "+(i+1)+" ==============\n");
        result += ("Type : "+playerGames[playerGames.length-i-1].type+"\n");
        result += ("URL : https://lichess.org/"+playerGames[playerGames.length-i-1].url+"\n");
        result += ("White : "+playerGames[playerGames.length-i-1].whitePlayer+"\n");
        result += ("Black : "+playerGames[playerGames.length-i-1].blackPlayer+"\n");
        result += ("Result : "+playerGames[playerGames.length-i-1].result+"\n");
        result += ("Date : "+playerGames[playerGames.length-i-1].date+"\n");
        result += ("Opening : "+playerGames[playerGames.length-i-1].opening+"\n");
        result += ("\n");
        dataOutputStream.writeUTF(result);
      }
      dataOutputStream.writeUTF("fin");
    }catch(IOException e){
      e.printStackTrace();
    }
  }

  private void searchNumber(String nickname){
    PlayerGamesSearcher searcher = new PlayerGamesSearcher(nickname);
    int nbGames = searcher.loadNumber();
    try{
      OutputStream outputStream = clientSocket.getOutputStream();
      DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
      InputStream inputStream = clientSocket.getInputStream();
      DataInputStream dataInputStream = new DataInputStream(inputStream);

      dataOutputStream.writeUTF(nbGames+"");
      if(nbGames>0){
        String input = dataInputStream.readUTF();
        if(input.equals("show")){
          input = dataInputStream.readUTF();
          System.out.println(Integer.parseInt(input));
          search(nickname,Integer.parseInt(input));
        }
      }
    }catch(IOException e){
      e.printStackTrace();
    }
  }

  private void opening(int quantity){
    TopOpeningSearcher searcher = new TopOpeningSearcher(quantity);
    try{
      OutputStream outputStream = clientSocket.getOutputStream();
      DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
      InputStream inputStream = clientSocket.getInputStream();
      DataInputStream dataInputStream = new DataInputStream(inputStream);
      searcher.load();
      for(int i=0;i<searcher.topOpening.length;i++){
        dataOutputStream.writeBoolean(true);
        dataOutputStream.writeUTF(i+1+". "+searcher.topOpening[i]+" - "+searcher.openingList.get(searcher.topOpening[i]));
      }dataOutputStream.writeBoolean(false);
    }catch(IOException e){
      e.printStackTrace();
    }
  }

  private void active(int quantity){
    MostActiveSearcher searcher = new MostActiveSearcher(quantity);
    try{
      OutputStream outputStream = clientSocket.getOutputStream();
      DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
      InputStream inputStream = clientSocket.getInputStream();
      DataInputStream dataInputStream = new DataInputStream(inputStream);
      searcher.load();
      for(int i=0;i<searcher.mostActivePlayers.size();i++){
        dataOutputStream.writeBoolean(true);
        dataOutputStream.writeUTF(searcher.mostActivePlayers.get(i));
      }
      dataOutputStream.writeBoolean(false);
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
      if(received.startsWith("search")) {
        searchNumber(received.substring(7,received.length()));
      }
      else if(received.startsWith("opening"))opening(Integer.parseInt(received.substring(8,received.length())));
      else if(received.startsWith("active"))active(Integer.parseInt(received.substring(7,received.length())));
      System.out.println("Closed connexion with"+clientSocket.getInetAddress());
      clientSocket.close();
      clients.remove(this);
    }catch(IOException e){
      clientSocket.close();
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
