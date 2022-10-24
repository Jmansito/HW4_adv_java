package edu.ucdenver.multiserver;

import edu.ucdenver.morse.Morse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientWorker implements Runnable{
    private Socket connection = null;
    private Morse morse;
    private Boolean keepRunningClient;

    public ClientWorker(Server server, Socket socket){
        morse = new Morse();
        this.connection = socket;
    }

    @Override
    public void run() {

    }

    private String processClientMessage(String message){
        return "Temp string";
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

    }

    private void sendMessage(String message, PrintWriter output){

    }

    private void displayMessage(String message){

    }

    protected void forceShutdown(){

    }
}
