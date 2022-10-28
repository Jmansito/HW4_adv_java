package edu.ucdenver.multiserver;

import edu.ucdenver.morse.Morse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

public class ClientWorker implements Runnable{
    private Server server;
    private Socket connection;
    private Morse morse;
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
        String newMessage;
        displayMessage("Getting Data Streams");
        try{
            getOutputStream(connection);
            getInputStream(connection);
            sendMessage("Connected", output);
            while(this.keepRunningClient) {
                String message = input.readLine();
                newMessage = processClientMessage(message);
                displayMessage(newMessage);
                sendMessage(newMessage, output);
                if(Objects.equals(message, "T|")){
                    sendMessage("0|", output);
                    break;
                }
            //   message = input.readLine();
            }
            closeConnection();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private String processClientMessage(String message){
        Morse process = new Morse();
        String newMessage;

        if(Objects.equals(message, "T|")){
            newMessage = "TERMINATE";
            forceShutdown();
        }
        else if(Objects.equals(message, "E") || Objects.equals(message, "E|")
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

    protected void forceShutdown(){

    }
}
