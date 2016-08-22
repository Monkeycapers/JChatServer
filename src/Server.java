/**
 * Created by Evan on 8/21/2016.
 */
import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class Server {

    ServerSocket serverSocket;
    ClientFactory clientFactory;
    String message = "Welcome to JChat";

    public Server() {
        clientFactory = new ClientFactory(16000);
        clientFactory.run();
    }

    public static void main(String[] args) {
        new Server();
    }

    private class ClientFactory implements Runnable {
        int portNumber;
        public ClientFactory(int portNumber) {
            this.portNumber = portNumber;

        }
        public void run() {
            //Create client workers
            serverSocket = null;
            try {
                serverSocket = new ServerSocket(portNumber);
            }
            catch (Exception e) {
                System.out.println("Could not create a socket");
            }
            System.out.println("Socket init");
            while (true) {
                ClientWorker w;
                try {
                    //Accept the connection
                    w = new ClientWorker(serverSocket.accept());

                    //Start a new thread to handle communication with that client

                    Thread t = new Thread(w);
                    t.start();
                    System.out.println("Started the connection");

                }
                catch (Exception e) {
                    //Could not connect to the client
                    System.out.println("Could not connect");
                    System.exit(-1);

                }
            }
        }
    }

    private class ClientWorker implements Runnable {

        private Socket client;

        public ClientWorker (Socket client) {
            System.out.println("Got a client");
            //Set this class's client to the client given
            this.client = client;
        }
        public void run() {
            //Interfaces with JChat client
            String line;
            DataInputStream in = null;
            DataOutputStream out = null;
            //Create a DataIn and DataOut Stream
            try {
                in = new DataInputStream(client.getInputStream());
                out = new DataOutputStream(client.getOutputStream());
            }
            catch (Exception e) {
                System.out.println("Could not create in/out streams");
            }
            //
            boolean running = true;
            boolean auth = false;
            while (running) {
                try {
                    out.writeUTF(message);
                }
                catch (IOException e) {
                    System.out.println("Could not send a message");
                    running = false;
                }

                try {
                    line = in.readUTF();
                    if (line.charAt(0) == '<') {
                        if (!auth) {
                            message = "[Guest] " + line;
                        }
                        else {
                            message = line;
                        }
                        System.out.println(line);
                    }

                }
                catch (IOException e) {
                    System.out.println("Could not receive a message");
                    running = false;
                }
                //System.out.println("Running");


                try {Thread.sleep(1000); } catch (Exception ex) { }
            }

        }
    }

    private class MessageReceiver implements Runnable {
        DataInputStream in;
        public MessageReceiver(DataInputStream in) {
            this.in = in;
        }
        public void run() {
            String line;
            boolean running = true;
            while (running) {
                try {
                    line = in.readUTF();
                    if (line.charAt(0) == '<') {
                        message = line;
                        System.out.println(line);
                    }

                }
                catch (IOException e) {
                    System.out.println("Could not receive a message");
                    running = false;
                }

            }
        }
    }

    private class MessageSender implements Runnable {
        DataOutputStream out;
        public MessageSender(DataOutputStream out) {
            this.out = out;
        }
        public void run() {
            boolean running = true;
            while (running) {
                try {
                    out.writeUTF(message);
                }
                catch (IOException e) {
                    System.out.println("Could not send a message");
                    running = false;
                }
            }
        }
    }

}
