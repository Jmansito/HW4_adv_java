package edu.ucdenver.server;

import edu.ucdenver.morse.Morse;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server implements Runnable {

    private int port;
    private int backlog;
    private int connectionCounter;
    private Socket connection = null;
    private ServerSocket socketServer = null;
    private Morse morse;

    public Server(int port, int backlog){
        this.port = port;
        this.backlog = backlog;
        morse = new Morse();
    }

    private Socket waitForConnection() throws IOException {

        displayMessage("Waiting for connection\n");
        connection = socketServer.accept();
        displayMessage("Connection " + connectionCounter + " received from: " + connection.getInetAddress().getHostName());
        return connection;
    }


    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        String message = scanner.nextLine();
        BufferedReader input = null;
        PrintWriter output = null;

        try {
            connection = waitForConnection();
            socketServer = new ServerSocket(port, backlog);
            while (true) {
                try {
                    input = getInputStream(connection); // get input & output streams
                    output = getOutputStream(connection);

                }
                catch (EOFException eofException) {
                    displayMessage("\nServer terminated connection");
                }
                finally
                {
                    closeConnection(connection, input, output); // close connection
                    ++connectionCounter;
                }
            }
        }
        catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void displayMessage(String message){
        SwingUtilities.invokeLater(
                new Runnable(){
                    public void run(){
                        displayMessage(message);
                    }
                }
        );
    }

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
        output.write("SERVER>>> " + message);
        output.flush();
        displayMessage("\nSERVER>>> " + message);
    }

    protected String processClientMessage(String message){


        return message;
    }
}
