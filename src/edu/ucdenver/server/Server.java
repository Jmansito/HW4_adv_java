package edu.ucdenver.server;
import edu.ucdenver.morse.Morse;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Objects;

public class Server implements Runnable {

    private  int port;
    private  int backlog;
    private int connectionCounter;
    private Socket connection;
    private ServerSocket socketServer;

    // constructor, experimenting with creating the socket here instead of in run. It was working both ways.
    public Server(int port, int backlog){
        this.port = port;
        this.backlog = backlog;

        try{
            socketServer = new ServerSocket(this.port, this.backlog);
        } catch(IOException ioException){
            ioException.printStackTrace();
        }
    }
    private Socket waitForConnection() throws IOException {
        this.connection = this.socketServer.accept();
        return this.connection;
    }

    // Run will build the connection as long as it is getting requests
    // Will loop the requests and process each one, then send the correct message back to the client
    @Override
    public void run() {
        BufferedReader input = null;
        PrintWriter output = null;
        String newMessage;
   //     try {
            // bind -> listen -> accept -> terminate
//            this.socketServer = new ServerSocket(this.port, this.backlog);
        while(true) {
            try {
                this.connection = waitForConnection();
                input = getInputStream(connection);
                output = getOutputStream(connection);
                // Listen for requests
                String message = input.readLine();
                while (message != null) {
                    newMessage = processClientMessage(message);
                    displayMessage(newMessage);
                    sendMessage(newMessage, output);
                    message = input.readLine();
                }
            } catch (Exception e) {
              //  e.printStackTrace();
                break;
            } finally {
                try {
                    System.out.println("Terminating connection");
                    closeConnection(connection, input, output); // close connection here when it is done looping
                    ++connectionCounter;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    socketServer.close(); // then finally close the socket
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
 //       }
//        catch(IOException ioException){
//            ioException.printStackTrace();
//        }
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

    private void sendMessage(String message, PrintWriter output){output.println(message);}

    // Simple process client: Check the message and if it is valid, encode or decode.
    protected String processClientMessage(String message){

        Morse process = new Morse();
        String newMessage;

        if(Objects.equals(message, "E") || Objects.equals(message, "E|")
                || Objects.equals(message, "D|") || Objects.equals(message, "D")){
            newMessage = "2|Invalid Message Format";
        }
        else {
            String[] toProcess = message.split("\\|");
            if(Objects.equals(toProcess[0], "E")){
                newMessage = "0|" + process.encode(toProcess[1]);
                //newMessage = process.encode(toProcess[1]);
            }
            else if(Objects.equals(toProcess[0], "D")){
                newMessage = "0|" + process.decode(toProcess[1]);
                //newMessage = process.decode(toProcess[1]);
            }
            else newMessage = "1|Not Implemented";
        }

        return newMessage;
    }
}
