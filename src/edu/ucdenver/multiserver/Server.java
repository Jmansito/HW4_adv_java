package edu.ucdenver.multiserver;

import edu.ucdenver.morse.Morse;

import java.net.ServerSocket;
import java.net.Socket;

public class Server extends edu.ucdenver.server.Server {
    private static int port;
    private static int backlog;
    private int connectionCounter;
    private Boolean keepServerRunning;
    private ServerSocket socketServer;

    public Server(int port, int backlog) {
        super(port, backlog);
    }


    public static void main(String[] args) {
        Morse test = new Morse();
        Thread t1 = new Thread(new Server(port, backlog));
        t1.start();
    }

    @Override
    public void run() {


    }

    public void shutdown(){

    }
    private Socket waitForConnection(){
        Socket connection = new Socket();
//        displayMessage("Waiting for connection\n");
//        connection = socketServer.accept();
//        displayMessage("Connection " + connectionCounter + " received from: " + connection.getInetAddress().getHostName());
        return connection;
    }
    private void displayMessage(String message){}
}
