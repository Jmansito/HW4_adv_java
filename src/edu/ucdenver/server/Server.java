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
        try{
            socketServer = new ServerSocket(port, backlog);
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    private Socket waitForConnection() throws IOException {
        connection = socketServer.accept();
        return connection;
    }

    public static void main(String[] args) {
        Runnable task = new Server(port, backlog);
        Thread t1 = new Thread(task);
        t1.start();
    }

    @Override
    public void run() {

        // binding is done here
        Socket client = null;
        BufferedReader input = null;
        PrintWriter output = null;
        String newMessage;

        try {
            // bind -> listen -> accept -> terminate
            client = waitForConnection();
            input = getInputStream(client);
            output = getOutputStream(client);

            // Listen for requests
            String message = input.readLine();
            System.out.println("Message received: " + message);
            newMessage = processClientMessage(message);
            displayMessage(newMessage);
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
                try{
                    socketServer.close();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
            catch(Exception e){

                e.printStackTrace();
            }
        }
    }

    private void displayMessage(final String message){System.out.println("[SER]" + message);}

    private PrintWriter getOutputStream(Socket socket) throws IOException {
        return new PrintWriter(socket.getOutputStream(), true);
    }

    private BufferedReader getInputStream(Socket socket) throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
        if(message.charAt(0)=='D'){
            String[] toProcess = message.split("|");
            for(int i = 2; i < toProcess.length; i++){
                    processed += toProcess[i];
            }
            newMessage = process.decode(processed);
        }
        return newMessage;
    }
}
