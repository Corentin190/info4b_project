import java.net.*;
import java.io.*;
import java.util.*;

class clientConnexion extends Thread{
  private Socket clientSocket;
  private ArrayList<clientConnexion> clients;

  public clientConnexion(Socket clientSocket, ArrayList<clientConnexion> clients){
    this.clientSocket = clientSocket;
    this.clients = clients;
  }

  public void run(){
    try{
      InputStreamReader reader = new InputStreamReader(clientSocket.getInputStream());
      int line = 0;
      while(line!=(-1)){
        line = reader.read();
        if(line!=(-1))System.out.print((char)line);
      }
      sleep(5000);
      System.out.println("Closed connexion with"+clientSocket.getInetAddress());
      clientSocket.close();
      clients.remove(this);
    }catch(IOException e){
      e.printStackTrace();
    }catch(InterruptedException e) {
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
