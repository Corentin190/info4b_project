import java.io.*;
import java.net.*;
import java.util.*;

/*
Client part of the client/server program.
*/

public class client {
  public static void main(String args[]) {
    try{
      InputStream inputStream;
      DataInputStream dataInputStream;
      OutputStream outputStream;
      DataOutputStream dataOutputStream;
      String ip="";
      if(args.length!=0) {
        ip=args[0];
      }else {
        ip="127.0.0.1";
      }
      int port = 1085;
      System.out.println("Connecting to "+ip+":"+port);
      if(args.length>0){
        ip = args[0];
      }

      System.out.println("Welcome !\n-------\nEnter 'help' to get some help.\n-------\nIf you want to quit, tap 'exit'");
      String scanner="";
      while(true){
        /*
        Setting up the socket in order to etablish the connextion to the server.
        */
        Socket clientSocket = new Socket();
        System.out.println("Enter a command");
        Scanner sc = new Scanner(System.in);
        scanner = sc.nextLine();
        /*
        Check what the client wants, then execute this or that command.
        */
        /*
        if 'exit', close the client
        */
        if(scanner.startsWith("exit"))break;
        /*
        if 'search', search the number of games or the specified player, then display or not those games
        */
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
          System.out.println(nb+" games found for "+scanner.substring(7,scanner.length())+" in "+(System.currentTimeMillis()-startTime)+"ms");
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
              startTime = System.currentTimeMillis();
              do{
                in = dataInputStream.readUTF();
                if(!in.equals("fin"))System.out.println(in);
              }while(!in.equals("fin"));
              System.out.println("Result found in "+(System.currentTimeMillis()-startTime)+"ms");
            }
          }
          /*
          if 'opening', display the most <parameter> openings
          */
        }else if(scanner.startsWith("opening")){
          long startTime = System.currentTimeMillis();
          clientSocket.connect(new InetSocketAddress(ip,port));
          inputStream = clientSocket.getInputStream();
          dataInputStream = new DataInputStream(inputStream);
          outputStream = clientSocket.getOutputStream();
          dataOutputStream = new DataOutputStream(outputStream);
          dataOutputStream.writeUTF(scanner);
          while(dataInputStream.readBoolean()){
            System.out.println(dataInputStream.readUTF());
          }
          System.out.println("Done in "+(System.currentTimeMillis()-startTime)+"ms");
          /*
          if 'active', diaplay the most <parameter> most active players
          */
        }else if(scanner.startsWith("active")){
          long startTime = System.currentTimeMillis();
          clientSocket.connect(new InetSocketAddress(ip,port));
          inputStream = clientSocket.getInputStream();
          dataInputStream = new DataInputStream(inputStream);
          outputStream = clientSocket.getOutputStream();
          dataOutputStream = new DataOutputStream(outputStream);
          dataOutputStream.writeUTF(scanner);
          while(dataInputStream.readBoolean()){
            System.out.println(dataInputStream.readUTF());
          }
          System.out.println("Done in "+(System.currentTimeMillis()-startTime)+"ms");
          /*
          if 'help', open the file named 'help.txt', to show wich command use
          */
        }else if(scanner.equals("help")){
          File file = new File("help.txt");
          BufferedReader br = new BufferedReader(new FileReader(file));
          String st="";
          while ((st = br.readLine()) != null){
              System.out.println(st);
          }
          /*
          if a non-repertoried command as been enter, print 'Wrong command, type help to show help guide'
          */
        }else{
          System.out.println("Wrong command, type help to show help guide");
        }
      }
    }catch (IOException e){
      e.printStackTrace();
    }
  }
}
