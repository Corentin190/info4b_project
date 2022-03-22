import java.io.*;
import java.net.*;
import java.util.*;

public class client {
    public static void main(String args[]) {
        Socket clientSocket = new Socket();
        InputStream input;
        InputStreamReader reader;
        OutputStream output;
        try {
            clientSocket.connect(new InetSocketAddress("127.0.0.1",1085));
            output = clientSocket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            Scanner sc = new Scanner(System.in);
            String i = sc.nextLine();
           // System.out.println(i);

            writer.println(i);
            clientSocket.close();
        }catch (IOException e){
        e.printStackTrace();
    }

    }
}
