package edu.ucdenver.server;
import edu.ucdenver.morse.Morse;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Objects;

public class Server implements Runnable {

    private static int port;
    private static int backlog;
    private int connectionCounter;
    private Socket connection;
    private ServerSocket socketServer;

    public Server(int port, int backlog){
        Server.port = port;
        Server.backlog = backlog;

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
        BufferedReader input = null;
        PrintWriter output = null;
        String newMessage;


        try {
            // bind -> listen -> accept -> terminate
            socketServer = new ServerSocket(port, backlog);

//            input = new BufferedReader(new InputStreamReader(connection.getInputStream())); //testing

               try{

                   waitForConnection();
                   //connection = socketServer.accept(); //testing
                   input = getInputStream(connection);
                   output = getOutputStream(connection);

                   // Listen for requests


                   String message = input.readLine();
//            System.out.println("Message received: " + message);
                   newMessage = processClientMessage(message);
                   displayMessage(newMessage);
                   sendMessage(newMessage, output);
               }
               catch (Exception e){
                   e.printStackTrace();
               }
               finally {
                   try {
                       System.out.println("Terminating connection");
                       closeConnection(connection, input, output);
                       ++connectionCounter;
                   }catch(Exception e){ e.printStackTrace();}
                   try{socketServer.close();}
                   catch(Exception e){ e.printStackTrace();}
               }



        } catch(IOException ioException){
            ioException.printStackTrace();
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
            e.printStackTrace();
        }
    }

    private void sendMessage(String message, PrintWriter output){
        output.write(message);
        output.flush();
        displayMessage(message);

    }

    protected String processClientMessage(String message){

        Morse process = new Morse();
        String newMessage;

        String[] toProcess = message.split("\\|");

        if(Objects.equals(toProcess[0], "E")){
            if(toProcess[1] == null) newMessage = "2|Invalid Message Format";
            else {newMessage = "0|" + process.encode(toProcess[1]);}
        }
        else if(Objects.equals(toProcess[0], "D")){
            if(toProcess[1] == null) newMessage = "2|Invalid Message Format";
            else {newMessage = "0|" + process.decode(toProcess[1]);}
        }
        else newMessage = "1|Not Implemented";

        return newMessage;
    }
}
