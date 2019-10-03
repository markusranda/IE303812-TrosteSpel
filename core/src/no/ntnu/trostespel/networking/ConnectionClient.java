package no.ntnu.trostespel.networking;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class ConnectionClient implements Runnable{

    public InetAddress getInetAddress() {
        return socket.getInetAddress();
    }

    private Socket socket;
    private Scanner scanner;

    public ConnectionClient(InetAddress serverAddress, int serverPort) throws Exception {
        this.socket = new Socket(serverAddress, serverPort);
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run() {

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
            String result = null;
            PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out.println(username);
            while ( result == null ) {
                result = input.readLine();
            }
            socket.close();
            return Long.parseLong(result);
        } catch (IOException e) {
            e.printStackTrace();
            return -2;
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            return -3;
        }
    }
}
