import java.io.*;
import java.net.*;
import java.util.*;
/*
Client part of the client/server program.
*/

public class client {
  public static class DateFormatException extends Exception{
      DateFormatException(){
    }
  }
  public static void checkDate(String date) throws DateFormatException{
    if(date.length()!=10){
      throw new DateFormatException();
    } else if(!(date.substring(4,date.length()-5)).equals(".") && !(date.substring(7,date.length()-2)).equals(".")){
      throw new DateFormatException();
    }else if(Integer.parseInt(date.substring(5,date.length()-3))<1 || Integer.parseInt(date.substring(5,date.length()-3))>12){
      throw new DateFormatException();
    }else if(Integer.parseInt(date.substring(8,date.length()))<1 || Integer.parseInt(date.substring(8,date.length()))>31){
      throw new DateFormatException();
    }
    switch(Integer.parseInt(date.substring(5,date.length()-3))){
    default: if(Integer.parseInt(date.substring(8,date.length()))<0)throw new DateFormatException();break;
    case 1: if(Integer.parseInt(date.substring(8,date.length()))>31)throw new DateFormatException();break;
    case 2: if(Integer.parseInt(date.substring(8,date.length()))>28)throw new DateFormatException();break;
    case 3: if(Integer.parseInt(date.substring(8,date.length()))>31)throw new DateFormatException();break;
    case 4: if(Integer.parseInt(date.substring(8,date.length()))>30)throw new DateFormatException();break;
    case 5: if(Integer.parseInt(date.substring(8,date.length()))>31)throw new DateFormatException();break;
    case 6: if(Integer.parseInt(date.substring(8,date.length()))>30)throw new DateFormatException();break;
    case 7: if(Integer.parseInt(date.substring(8,date.length()))>31)throw new DateFormatException();break;
    case 8: if(Integer.parseInt(date.substring(8,date.length()))>31)throw new DateFormatException();break;
    case 9: if(Integer.parseInt(date.substring(8,date.length()))>30)throw new DateFormatException();break;
    case 10: if(Integer.parseInt(date.substring(8,date.length()))>31)throw new DateFormatException();break;
    case 11: if(Integer.parseInt(date.substring(8,date.length()))>30)throw new DateFormatException();break;
    case 12: if(Integer.parseInt(date.substring(8,date.length()))>31)throw new DateFormatException();
    }
  }

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
          if(scanner.endsWith("date")) {
            dataOutputStream.writeUTF(scanner);
            if(dataInputStream.readUTF().equals("which?")){
              String answer="";
              Boolean error;
              do{
                error = false;
                System.out.println("What date ?");
                try{
                  answer = sc.nextLine();
                  checkDate(answer);
                }catch(DateFormatException e){
                  error=true;
                }
                if(error) System.out.println("Invalid format, please enter a valid one (YYYY.MM.DD)");
              }while(error);
              dataOutputStream.writeUTF(answer);
              if(dataInputStream.readBoolean()){
                int input=0;
                do{
                  error = false;
                  System.out.println("How many ?");
                  try{
                    input = Integer.parseInt(sc.nextLine());
                    if(input<0)error = true;
                  }catch(NumberFormatException e){
                    error = true;
                  }
                  if(error)System.out.println("Invalid number, please try a valid number");
                }while(error);
                dataOutputStream.writeUTF(input+"");
                long startTime = System.currentTimeMillis();
                String in = "";
                do{
                  in = dataInputStream.readUTF();
                  if(!in.equals("fin"))System.out.println(in);
                }while(!in.equals("fin"));
                System.out.println("Result found in "+(System.currentTimeMillis()-startTime)+"ms");
              }
            }
          }else{
             dataOutputStream.writeUTF(scanner);
            long startTime = System.currentTimeMillis();
            System.out.println("Searching for "+scanner.substring(7,scanner.length()));
            
            boolean transmissionOver = false;
            int nb = Integer.parseInt(dataInputStream.readUTF());
            System.out.println(nb+" games found for "+scanner.substring(7,scanner.length())+" in "+(System.currentTimeMillis()-startTime)+"ms");
            if(nb>0){
              System.out.println("Do you want to see those games ? (Y/N)");
              if(sc.nextLine().toLowerCase().equals("y")){
                dataOutputStream.writeUTF("show");
                int input = 0;
                Boolean error;
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
                String in;
                do{
                  in = dataInputStream.readUTF();
                  if(!in.equals("fin"))System.out.println(in);
                }while(!in.equals("fin"));
                System.out.println("Result found in "+(System.currentTimeMillis()-startTime)+"ms");
              }
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
