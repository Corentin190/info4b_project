import java.io.*;
import java.net.*;
import java.util.*;

public class client {
  public static void main(String args[]) {
    Socket clientSocket = new Socket();
    try {
      // //clientSocket.connect(new InetSocketAddress("127.0.0.1",1085));
      InputStream inputStream;
      DataInputStream dataInputStream;
      OutputStream outputStream;
      DataOutputStream dataOutputStream;

      //System.out.println("Welcome !\n-------\nIf you are looking for all the game of a player, tap 'search <nickname>'.\nFor example : search 2girls1cup\n-------\nIf you want to quit, tap 'exit'");
      String scanner="";
      String result="";
      while(!scanner.equals("exit")) {
        System.out.println("Enter a command");
        Scanner sc = new Scanner(System.in);
        scanner = sc.nextLine();
        if(scanner.startsWith("search ")){
          clientSocket.connect(new InetSocketAddress("127.0.0.1",1085));
          inputStream = clientSocket.getInputStream();
          dataInputStream = new DataInputStream(inputStream);
          outputStream = clientSocket.getOutputStream();
          dataOutputStream = new DataOutputStream(outputStream);
          dataOutputStream.writeUTF(scanner);
          System.out.println("Do you want to see the results ? (y or whatever");
          ///////////////
          ///////////////
          ///////////////
          ///////////////
          ///////////////
          ///////////////
          ///////////////
          ///////////////
          ///////////////
          if(sc.nextLine().equals("y")) {
            do {
              System.out.println(dataInputStream.readUTF());
            }while(!dataInputStream.readUTF().contains("games found"));
            //System.out.println(dataInputStream.readUTF());
          }
          ///////////////
          ///////////////
          ///////////////
          ///////////////
          ///////////////C'EST RELIÉ À ÇA
          ///////////////
          ///////////////
          ///////////////CASSE LA TETE
          ///////////////
          ///////////////

        }
        
          clientSocket.close();
      //   //dataOutputStream.writeUTF(scanner);
      //   System.out.println("Requête envoyée");
      //   long clock = System.currentTimeMillis();


      //   //clientSocket.close();
      //   //clientSocket.shutdownOutput();

      //   String message="";
      //   while(!message.equals("End of communication")) {
      //     if(message.startsWith("Do you")) {
      //       System.out.println("API response time : "+(System.currentTimeMillis()-clock)+"ms");
      //       scanner = sc.nextLine();
      //       dataOutputStream.writeUTF(scanner);
      //     }
      //     message = dataInputStream.readUTF();
      //     System.out.println(message);
      //   }
      // }
      // clientSocket.close();


}
    }catch (IOException e){
      e.printStackTrace();
    }



  }
}
