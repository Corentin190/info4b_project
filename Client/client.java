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

      String ip="127.0.0.1";
      int port = 1085;

      System.out.println("Welcome !\n-------\nIf you are looking for all the game of a player, tap 'search <nickname>'.\nFor example : search 2girls1cup\n-------\nIf you want to quit, tap 'exit'");
      String scanner="";
      while(true){
        Socket clientSocket = new Socket();
        System.out.println("Enter a command");
        Scanner sc = new Scanner(System.in);
        scanner = sc.nextLine();
        if(scanner.startsWith("exit"))break;
        else if(scanner.startsWith("search ")){
          clientSocket.connect(new InetSocketAddress(ip,port));
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

          int nb = Integer.parseInt(dataInputStream.readUTF());
          System.out.println(nb+" games found for "+scanner.substring(7,scanner.length()));
          if(nb>0){
            System.out.println("Do you want to see those games ?");
            if(sc.nextLine().equals("yes")){
              int input;
              do{
                System.out.println("How many ?");
                input = Integer.parseInt(sc.nextLine());
              }while(input<=0 || input >nb);
              dataOutputStream.writeUTF(input+"");
              do{
                in = dataInputStream.readUTF();
                if(!in.equals("fin"))System.out.println(in);
              }while(!in.equals("fin"));
            }
          }
        }else if(scanner.startsWith("opening")){
          clientSocket.connect(new InetSocketAddress(ip,port));
          inputStream = clientSocket.getInputStream();
          dataInputStream = new DataInputStream(inputStream);
          outputStream = clientSocket.getOutputStream();
          dataOutputStream = new DataOutputStream(outputStream);
          dataOutputStream.writeUTF(scanner);
          while(dataInputStream.readBoolean())
          System.out.println(dataInputStream.readUTF());
        }else if(scanner.startsWith("active")){
          clientSocket.connect(new InetSocketAddress(ip,port));
          inputStream = clientSocket.getInputStream();
          dataInputStream = new DataInputStream(inputStream);
          outputStream = clientSocket.getOutputStream();
          dataOutputStream = new DataOutputStream(outputStream);
          dataOutputStream.writeUTF(scanner);
          while(dataInputStream.readBoolean())
          System.out.println(dataInputStream.readUTF());
        }
      }
    }catch (IOException e){
      e.printStackTrace();
    }
  }
}
