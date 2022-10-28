package edu.ucdenver.multiserver;

import edu.ucdenver.morse.Morse;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    private int port;
    private int backlog;
    private int connectionCounter;
    private Boolean keepServerRunning;
    ExecutorService executorService;

    private ServerSocket socketServer;

    public Server(int port, int backlog) {
        this.port = port;
        this.backlog = backlog;
        this.connectionCounter = 0;
        this.keepServerRunning = true;

    }

    @Override
    public void run() {
        executorService = Executors.newCachedThreadPool();
        try{

            this.socketServer = new ServerSocket(this.port, this.backlog);

            while(true){
                try{
                    Socket connection = this.waitForConnection();
                    ClientWorker cw = new ClientWorker(this, connection);
                    executorService.execute(cw);
                }
                catch (IOException ioe){
                    displayMessage("Server Terminated");
                    ioe.printStackTrace();
                    break;
                }

            }
        } catch(IOException ioe){
            displayMessage("Cannot open the server");
            shutdown();
            ioe.printStackTrace();
        }

    }

    private Socket waitForConnection() throws IOException {
        Socket connection = this.socketServer.accept();
        this.connectionCounter++;
        return connection;
    }
    private void displayMessage(String message){
        System.out.println(message);
    }



    public void shutdown(){ //TODO
        executorService.shutdown();
    }



}
