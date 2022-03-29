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
          clientSocket.connect(new InetSocketAddress("172.31.18.52",1085));
          inputStream = clientSocket.getInputStream();
          dataInputStream = new DataInputStream(inputStream);
          outputStream = clientSocket.getOutputStream();
          dataOutputStream = new DataOutputStream(outputStream);
          dataOutputStream.writeUTF(scanner);
          long startTime = System.currentTimeMillis();
          System.out.println("Searching for "+scanner.substring(7,scanner.length()));
          String in = "";
          do{
            in = dataInputStream.readUTF();
            if(!in.equals("[METADATA]") && !in.equals("[/METADATA]")){
              System.out.println(in);
            }
          }while(!in.equals("[/METADATA]") && !in.equals("No game found for this nickname\n"));
          System.out.println("API response time : "+(System.currentTimeMillis()-startTime)+" ms");
          if(!in.equals("No game found for this nickname\n")) {
            System.out.println("Do you want to see the results ? (y or whatever)");
            if(sc.nextLine().equals("y")){
              do{
                in = dataInputStream.readUTF();
                if(!in.equals("fin"))System.out.println(in);
              }while(!in.equals("fin"));
            }
          }
        }
      }
    }catch (IOException e){
      e.printStackTrace();
    }
  }
}
