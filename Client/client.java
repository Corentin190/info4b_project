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
      if(args.length>0){
        ip = args[0];
      }

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
            System.out.println("Do you want to see those games ? (Y/N)");
            if(sc.nextLine().toLowerCase().equals("y")){
              dataOutputStream.writeUTF("show");
              int input = 0;
              boolean error;
              do{
                error = false;
                System.out.println("How many ?");
                try{
                  input = Integer.parseInt(sc.nextLine());
                  if(input>nb || input<0)error = true;
                }catch(NumberFormatException e){
                  error = true;
                }
                if(error)System.out.println("Invalid number, please try a valid number between 0 and "+nb);
              }while(error);
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
        }else{
          System.out.println("Wrong command, type help to show help guide");
        }
      }
    }catch (IOException e){
      e.printStackTrace();
    }
  }
}
