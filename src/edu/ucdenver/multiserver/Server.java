package edu.ucdenver.multiserver;

import edu.ucdenver.morse.Morse;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server implements Runnable {
    private int port;
    private int backlog;
    private int connectionCounter;
    private static Boolean keepServerRunning;
    static ExecutorService executorService;
    public static ArrayList<ClientWorker> clientWorkers = new ArrayList<>();
    private static ServerSocket socketServer;

    public Server(int port, int backlog) {
        this.port = port;
        this.backlog = backlog;
        this.connectionCounter = 0;
        keepServerRunning = true;
    }

    @Override
    public void run() {
        executorService = Executors.newCachedThreadPool();
        try{

            socketServer = new ServerSocket(this.port, this.backlog);

            while(keepServerRunning){
                try{
                    Socket connection = this.waitForConnection();
                    ClientWorker cw = new ClientWorker(this, connection);
                    executorService.execute(cw);
                    clientWorkers.add(cw); // Building client list
                    connectionCounter++;
                }
                catch (IOException ioe){
                    displayMessage("Server Terminated");
                    ioe.printStackTrace();
                    break;
                }
            }
        } catch(IOException ioe){
            displayMessage("Cannot open the server");
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


    public static void shutdown() throws IOException {
        executorService.shutdown();
        try {
            keepServerRunning = false;
            socketServer.close();
        } catch (IOException e) {
          //  e.printStackTrace(); //ignoring errors
        } finally {
            // Shut down client workers one by one?
            for(ClientWorker worker: clientWorkers)
                worker.forceShutdown();
//              executorService.awaitTermination(2, TimeUnit.SECONDS);
//              executorService.shutdownNow();

            }
        }
    }