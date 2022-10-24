package edu.ucdenver.server;

import edu.ucdenver.morse.Morse;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Handler;

public class Server implements Runnable {

    private static int port;
    private static int backlog;
    private int connectionCounter;
    private Socket connection = null;
    private ServerSocket socketServer = null;
    private Morse morse;

    public Server(int port, int backlog){
        Server.port = port;
        Server.backlog = backlog;
        morse = new Morse();
    }

    private Socket waitForConnection() throws IOException {

    //    displayMessage("Waiting for connection\n");
        connection = socketServer.accept();
    //    displayMessage("Connection " + connectionCounter + " received from: " + connection.getInetAddress().getHostName());
        return connection;
    }

    public static void main(String[] args) {

        Runnable task = new Server(port, backlog);
        Thread t1 = new Thread(task);
        t1.start();

    }

    @Override
    public void run() {
        try {
            // binding is done here
            socketServer = new ServerSocket(port, backlog);
            Socket client = null;
            BufferedReader input = null;
            PrintWriter output = null;
            String newMessage;

         //   while (true){
                try {
                    // bind -> listen -> accept -> terminate
                    client = waitForConnection();
                    input = getInputStream(client);
                    output = getOutputStream(client);

                    // Listen for requests
                    String message = input.readLine();
                    System.out.println("Message received: " + message);
//                   System.out.println("PROCESSING MESSAGE");
                    newMessage = processClientMessage(message);
//                    System.out.println("DISPLAYING MESSAGE");
                    displayMessage(newMessage);
//                    System.out.println("SENDING MESSAGE");
                    sendMessage(newMessage, output);


                }

                catch (Exception e){
                    e.printStackTrace();
                }
                finally{
                    System.out.println("Terminating connection");
                    try{
                        closeConnection(client, input, output);
                        ++connectionCounter;
                    }
                    catch(Exception e){
                        socketServer.close();
                        e.printStackTrace();
                    }
                }
         //   }
        }

        catch (IOException ioe){
            System.err.println(ioe);
        }
    }

    private void displayMessage(final String message){System.out.println("[SER]" + message);}

    private PrintWriter getOutputStream(Socket socket) throws IOException {
        PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
        return output;
    }

    private BufferedReader getInputStream(Socket socket) throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        return input;
    }

    private void closeConnection(Socket socket, BufferedReader input, PrintWriter output){
        try{
            output.close();
            input.close();
            socket.close();
        }
        catch(Exception e){
            System.err.println(e);
        }
    }

    private void sendMessage(String message, PrintWriter output){
        output.write(message);
        output.flush();
        displayMessage(message);
    }

    protected String processClientMessage(String message){

        Morse process = new Morse();
        String newMessage = "";
        String processed = "";

        if(message.charAt(0)=='E'){
            String[] toProcess = message.split("|");
            for(int i = 2; i < toProcess.length; i++){
                processed += toProcess[i];
            }
            newMessage = process.encode(processed);

        }
        else if(message.charAt(0)=='D'){
            String[] toProcess = message.split("|");
            for(int i = 2; i < toProcess.length; i++){
                    processed += toProcess[i];
            }
            newMessage = process.decode(processed);
        }
        else newMessage = "NOTHING";





        return newMessage;
    }
}
