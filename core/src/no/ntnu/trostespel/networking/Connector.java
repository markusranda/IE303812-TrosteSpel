package no.ntnu.trostespel.networking;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * This class wil be responsible for connecting to a server, and notifying the server wether this client has
 * got a live connection to the server.
 */
public class Connector extends CommunicationManager {
    @Override
    public String connect() {
        String result = null;
        Socket socket = null;

        try {
            // TODO: 01.10.2019 Add constant from a config file instead of hardcoded url and port
            socket = new Socket("192.168.50.50", 7083);

            // Bytes
            OutputStream outputStream = socket.getOutputStream();
            // Primitives
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            // Bytes
            InputStream inputStream = socket.getInputStream();
            // Byte reader
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            // Characters
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            // Send message to server
            dataOutputStream.writeBytes("Markus");

            // Read characters from server
            System.out.println(bufferedReader.readLine());

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
