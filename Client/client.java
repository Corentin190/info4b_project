import java.io.*;
import java.net.*;
import java.util.*;

public class client {
  public static void main(String args[]) {
    try{
      InputStream inputStream;
      DataInputStream dataInputStream;
      OutputStream outputStream;
      DataOutputStream dataOutputStream;

      System.out.println("Welcome !\n-------\nIf you are looking for all the game of a player, tap 'search <nickname>'.\nFor example : search 2girls1cup\n-------\nIf you want to quit, tap 'exit'");
      String scanner="";
      while(true){
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
          long startTime = System.currentTimeMillis();
          System.out.println("Searching for "+scanner.substring(7,scanner.length()));
          String in = "";
          int gamesFound = 0;
          boolean transmissionOver = false;
          do{
            do{
              in = dataInputStream.readUTF();
              if(!in.equals("[METADATA]") && !in.equals("[/METADATA]")){
                System.out.println(in);
                gamesFound = Integer.parseInt(in.substring(0,in.length()-13));
              }
            }while(!in.equals("[/METADATA]") && !in.equals("No game found for this nickname\n"));
            System.out.println("API response time : "+(System.currentTimeMillis()-startTime)+" ms");
            do{
              in = dataInputStream.readUTF();
              if(!in.equals("fin"))System.out.println(in);
            }while(!in.equals("fin"));
            if(gamesFound>=1000){
              System.out.println("Voulez-vous voir plus ?");
              if(sc.nextLine().equals("y"))dataOutputStream.writeUTF("keep_reading");
              else transmissionOver = true;
            }else transmissionOver = true;
          }while(!transmissionOver);
        }
      }
    }catch (IOException e){
      e.printStackTrace();
    }
  }
}
