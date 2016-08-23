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
        clientFactory = new ClientFactory(16000, 1000);
        clientFactory.run();
    }

    public static void main(String[] args) {
        new Server();
    }

    private class ClientFactory implements Runnable {
        int portNumber;
        ClientWorker[] workers;
        int amountConnected;
        public ClientFactory(int portNumber, int maxPlayers) {
            this.portNumber = portNumber;
            workers = new ClientWorker[maxPlayers];
            amountConnected = 0;
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
                    workers[amountConnected] = w;
                    amountConnected ++;
                    //TODO: Detect client disconnects

                }
                catch (Exception e) {
                    //Could not connect to the client
                    System.out.println("Could not connect");
                    System.exit(-1);

                }
            }
        }
        public String getPlayerList () {
            String pl = "";
            for (ClientWorker w: workers) {
                try {
                    pl += w.nick + "\n";
                }
                catch (NullPointerException e) {

                }
            }
            return pl;
        }
    }

    private class ClientWorker implements Runnable {

        private Socket client;

        String nick = "Anon";

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
            boolean sendSecretMessage = false;
            String smessage = "";
            //Rank rank = Rank.Guest;
            User user = new User();
            while (running) {
                try {
                    if (sendSecretMessage) {
                        out.writeUTF("[Server] to you: " + smessage);
                        sendSecretMessage = false;
                    }
                    else {
                        out.writeUTF(message);
                    }

                }
                catch (IOException e) {
                    System.out.println("Could not send a message");
                    //TODO: This is a client disconnect, tell clientfactory that this spot is free
                    running = false;
                }

                try {
                    line = in.readUTF();
                    nick = line.split("\\,")[0];
                    if (line.charAt(0) == '<') {
                        //Message
                        if (!auth) {
                            message = "[Guest] " + line;
                        }
                        else {
                            message = "[" + user.rank.name() + "] " + line;
                        }
                        System.out.println(line);
                    }
                    else if (line.contains("userlist")) {
                        smessage = clientFactory.getPlayerList();
                        sendSecretMessage = true;
                    }
                    else if (line.contains("stop")) {
                        if (user.rank.equals(Rank.Admin)  || user.rank.equals(Rank.Op)) {
                            System.exit(2);
                        }
                        else {
                            smessage = "You are not privileged enough for this command.";
                            sendSecretMessage = true;
                        }
                    }
                    else if (line.contains("auth")) {
                        if (user.rank.equals(Rank.Guest)) {
                            String[] split = line.split("\\,");

                            user.rank = Authenticate.login(split[2], split[3]);
                            if (user.rank.equals(Rank.Guest)) {
                                smessage = "Invalid user or pass.";
                            }
                            else {
                                auth = true;
                                user.username = split[2];
                                smessage = "You have logged in. You are a: " + user.rank.name();
                            }
                            sendSecretMessage = true;
                        }
                        else {
                            smessage = "You are already logged in!";
                            sendSecretMessage = true;
                        }
                    }
                    else if (line.contains("signup")) {
                        if (user.rank.equals(Rank.Guest)) {
                            String[] split = line.split("\\,");

                            user.rank = Authenticate.signup(split[2], split[3]);
                            if (user.rank.equals(Rank.Guest)) {
                                smessage = "User already exists";
                            }
                            else {
                                auth = true;
                                user.username = split[2];
                                smessage = "You have signed up and logged in. You are a: " + user.rank.name();
                            }
                            sendSecretMessage = true;
                        }
                        else {
                            smessage = "You are already logged in!";
                            sendSecretMessage = true;
                        }
                    }
                    else if (line.contains("promote")) {
                        Promote cmdPromote = new Promote();
                        String[] split = line.split("\\,");
                        System.out.println("test");
                        System.out.println(cmdPromote.invoke(user, new User(Rank.valueOf(split[3]), split[2])));
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
}
