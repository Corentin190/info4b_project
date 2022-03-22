import java.io.*;
import java.net.*;

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
            writer.println("This is a message sent to the server");
            clientSocket.close();
        }catch (IOException e){
        e.printStackTrace();
    }

    }
}
