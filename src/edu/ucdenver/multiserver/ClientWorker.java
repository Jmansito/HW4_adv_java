package edu.ucdenver.multiserver;

import edu.ucdenver.morse.Morse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

public class ClientWorker implements Runnable{
    private Server server;
    private Socket connection;
    private Boolean keepRunningClient;
    private PrintWriter output;
    private BufferedReader input;

    public ClientWorker(Server server, Socket socket){
        this.connection = socket;
        this.server = server;
        this.keepRunningClient = true;
    }

    @Override
    public void run() {
        BufferedReader input;
        PrintWriter output;
        String newMessage;

        displayMessage("Getting Data Streams");

            try{
                output = getOutputStream(connection);
                input = getInputStream(connection);

                String message = input.readLine();
                // Will stop listening to messages when client sends T|
                while(this.keepRunningClient) {
                    newMessage = processClientMessage(message);
                    if(Objects.equals(message, "T|")){
                        sendMessage("0|OK", output);
                        this.keepRunningClient = false;
                        break;
                    }
                    displayMessage(newMessage);
                    sendMessage(newMessage, output);
                    message = input.readLine();

                }


            }catch(IOException | InterruptedException e){
            //    e.printStackTrace();

            }
            finally {
                try {
                    System.out.println("Terminating connection");
                    closeConnection();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }

    private String processClientMessage(String message) throws IOException, InterruptedException {
        Morse process = new Morse();
        String newMessage;

        if(Objects.equals(message, "TERMINATE|")){
            newMessage = "TERMINATE";
            Server.shutdown();
        }
        else if(Objects.equals(message, "E") || Objects.equals(message, "E|")
                || Objects.equals(message, "D|") || Objects.equals(message, "D")){
            newMessage = "2|Invalid Message Format";
        }
        else {
            String[] toProcess = message.split("\\|");
            if(Objects.equals(toProcess[0], "E")){
                newMessage = "0|" + process.encode(toProcess[1]);
            }
            else if(Objects.equals(toProcess[0], "D")){
                newMessage = "0|" + process.decode(toProcess[1]);
            }
            else newMessage = "1|Not Implemented";
        }
        return newMessage;
    }


    private PrintWriter getOutputStream(Socket socket) throws IOException {
        return new PrintWriter(socket.getOutputStream(), true);

    }

    private BufferedReader getInputStream(Socket socket) throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    private void closeConnection(){
        try{this.input.close();} catch(IOException|NullPointerException e){e.printStackTrace();}
        try{this.output.close();} catch(NullPointerException e){e.printStackTrace();}
        try{this.connection.close();} catch(IOException|NullPointerException e){e.printStackTrace();}
    }

    private void sendMessage(String message, PrintWriter output){output.println(message);}

    private void displayMessage(String message){System.out.println(message);}

    protected void forceShutdown() throws IOException {
        this.keepRunningClient = false;
        connection.close();
    }
}
