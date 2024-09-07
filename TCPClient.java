import java.io.*;
import java.net.*;

public class TCPClient {
    public static void main(String[] args) throws IOException {
        String hostName = "localhost"; int portNumber = 6969;
        try (Socket kkSocket = new Socket(hostName, portNumber);
             PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
        ) {
            Thread receiver = new Thread(() -> {
                try {
                    String fromServer;
                    while ((fromServer = in.readLine()) != null) {
                        System.out.println(fromServer);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            receiver.start();
            String fromUser;
            while ((fromUser = stdIn.readLine()) != null) {
                out.println(fromUser);
            }
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            System.out.println("IO Exception");
            e.printStackTrace();
        }
    }
}
