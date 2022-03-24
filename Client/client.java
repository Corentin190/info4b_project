import java.io.*;
import java.net.*;
import java.util.*;

public class client {
  public static void main(String args[]) {
    try {
      InputStream inputStream;
      DataInputStream dataInputStream;
      OutputStream outputStream;
      DataOutputStream dataOutputStream;

      System.out.println("Welcome !\n-------\nIf you are looking for all the game of a player, tap 'search <nickname>'.\nFor example : search 2girls1cup\n-------\nIf you want to quit, tap 'exit'");
      String scanner="";
      String result="";
      while(true) {
        Socket clientSocket = new Socket();
        System.out.println("Enter a command");
        Scanner sc = new Scanner(System.in);
        scanner = sc.nextLine();
        if(scanner.startsWith("exit"))break;
        else if(scanner.startsWith("search ")){
          clientSocket.connect(new InetSocketAddress("127.0.0.1",1085));
          inputStream = clientSocket.getInputStream();
          dataInputStream = new DataInputStream(inputStream);
          outputStream = clientSocket.getOutputStream();
          dataOutputStream = new DataOutputStream(outputStream);
          dataOutputStream.writeUTF(scanner);
          do{
            result += dataInputStream.readUTF()+"\n";
          }while(dataInputStream.available()>0);
          System.out.println("Do you want to see the results ? (y or whatever)");
          if(sc.nextLine().equals("y")) {
            System.out.println(result);
          }
        }
        clientSocket.close();
      }
    }catch (IOException e){
      e.printStackTrace();
    }
  }
}
