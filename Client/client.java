import java.io.*;
import java.net.*;
import java.util.*;

public class client {
  public static void main(String args[]) {
    Socket clientSocket = new Socket();
    try {
      clientSocket.connect(new InetSocketAddress("127.0.0.1",1085));
      OutputStream output = clientSocket.getOutputStream();

      Scanner sc = new Scanner(System.in);
      String i = sc.nextLine();

      output.write(i.getBytes());
      output.flush();
      System.out.println("Requête envoyée");
      //scx.start();
      //System.out.println("InputStreamThread started");

      //clientSocket.close();
    }catch (IOException e){
      e.printStackTrace();
    }
  }
}
