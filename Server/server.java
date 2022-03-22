import java.net.*;
import java.io.*;

public class server{
  public static void main(String[] args) {
    try{
      final int port = 1085;
      ServerSocket server = new ServerSocket(port);
      System.out.println("Server listenning on port "+port);
      Socket clientSocket = server.accept();
      InputStreamReader reader = new InputStreamReader(clientSocket.getInputStream());
      while(true){
        char line = (char)reader.read();
        System.out.print(line);
      }
    }catch(IOException e){
      e.printStackTrace();
    }
  }
}
