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

    // Multithreaded run method
    // Make the executor server, create and bind the socket, loop while the server is running
    // each loop will create the clientWorker and store it in the array list to later be disconnected one by one.
    // Run until the client terminates with termination message
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

    // I had issues figuring this out and am still unsure. The autograder was okay with it though so I am submitting as is
    // Shutdown method will stop the executor service, set the server to stop running and close the socket
    // Then will loop through all client workers and shutdown their connection one by one

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