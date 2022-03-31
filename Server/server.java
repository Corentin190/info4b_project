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
      // do{
        Game[] playerGames = searcher.load(nbGames);
        for(int i=0;i<nbGames;i++){
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
       // }else dataOutputStream.writeUTF("No game found for this nickname\n");
      // }while(nbGamesFound>=1000 && dataInputStream.readUTF().equals("keep_reading"));
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
      int nbb=Integer.parseInt(dataInputStream.readUTF());
      System.out.println(nbb);
      //System.out.println(Integer.parseInt(dataInputStream.readUTF()));

      search(nickname,nbb);


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
        
        // System.out.println(i+". "+searcher.topOpening[i]+" - "+searcher.openingList.get(searcher.topOpening[i]));
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
      for(int i=0;i<searcher.mostActivePlayers.length;i++){
        dataOutputStream.writeBoolean(true);
        dataOutputStream.writeUTF(i+". "+searcher.mostActivePlayers[i]+" - "+searcher.playersGamesList.get(searcher.mostActivePlayers[i]));
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
