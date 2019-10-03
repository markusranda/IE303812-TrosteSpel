package no.ntnu.trostespel.networking;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class ConnectionClient implements Runnable{

    public InetAddress getInetAddress() {
        return socket.getInetAddress();
    }

    private Socket socket;
    private Scanner scanner;
    String username = "lemurium";

    public ConnectionClient(InetAddress serverAddress, int serverPort) throws Exception {
        this.socket = new Socket(serverAddress, serverPort);
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        long result = initialConnect(username);
        System.out.println(result);
    }

    /**
     * Tries to connect to a server with username as parameter, this is the same username
     * that the player wishes to use in the game. This method will return a long value
     * with playerID, or a negative number describing what went wrong.
     *
     * -1 : username was null
     * -2 : IOException
     * -3 : Server didn't respond with a number
     *
     * @param username The username the player wishes to use in the game
     * @return playerID or negative number as error code.
     */
    public long initialConnect(String username) {
        if (username == null) return -1;
        try {
            System.out.println("\r\nConnected to Server: " + getInetAddress());
            String data = null;

            // Print username to server
            PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);
            out.println(username);

            // Get answer from server
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (data == null) {
                data = in.readLine();
            }

            socket.close();
            return Long.parseLong(data);

        } catch (IOException e) {
            e.printStackTrace();
            return -2;
        } catch (NumberFormatException nfe) {
            return -3;
        }
    }
}
