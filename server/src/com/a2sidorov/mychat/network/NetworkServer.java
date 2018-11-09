package com.a2sidorov.mychat.network;

import sun.nio.ch.Net;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

public class NetworkServer {

    private ServerSocket socket;
    private int port;
    private boolean running = false;
    private ArrayList<String> nicknames;
    private ArrayList<DataOutputStream> outputs;

    private NetworkServer(int port) {
        this.port = port;
    }

    //implementing the Singleton pattern.
    private static NetworkServer server;
    public static NetworkServer getInstance(int port) {
        if(server == null) {
            server = new NetworkServer(port);
        }
        return server;
    }

    public void startServer() {
        try {
            socket = new ServerSocket(port);
            outputs = new ArrayList<DataOutputStream>();
            nicknames = new ArrayList<String>();

            System.out.println("Server socket initialized on port");
        } catch(Exception e) {
            e.printStackTrace();
        }

        //listening for connections
        new Thread(() -> {
            System.out.println("Listening for clients...");
            while(running) {
                try {
                    Socket client = socket.accept();
                    String name = client.getRemoteSocketAddress().toString();
                    System.out.println("Client has connected. " + name);

                    //setting up streams
                    DataInputStream input = new DataInputStream(client.getInputStream());
                    DataOutputStream output = new DataOutputStream(client.getOutputStream());
                    System.out.println("Sreams are set up.");

                    //tracking connected users
                    outputs.add(output);
                    //nicknames.add(name);

                    //sending the list of connected users
                    //broadcast(new Packet(nicknames));

                    //listening for data
                    new Thread(() -> {
                        boolean error = false;
                        while (!error && client.isConnected()) {
                            try {
                                String rawData = input.readUTF();
                                process(rawData);
                            } catch (SocketException se) {
                                error = true;
                            } catch (EOFException eofe) {
                                error = true;
                            } catch (IOException e) {
                                e.printStackTrace();
                                System.out.println("IOException");
                            }
                        }
                        System.out.println("Client has diconnected. " + name);

                        //updatingthe list of connected users
                        outputs.remove(output);
                        //nicknames.remove();

                        //sending the updated list of connected users
                        //broadcast(new Packet(nicknames));
                        //broadcast(new Packet("message", "Server", name + " has left the chat."));
                        System.out.println("exiting client thread");
                    }).start();

                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        running = true;
    }

    private void process(String rawData) {
        String[] data = rawData.trim().split(";");
        //String name = data[1];
        PacketType type = PacketType.valueOf(data[0]);
        switch(type) {
            case LOGIN:
                nicknames.add(data[1]);
                System.out.println("test " + nicknames.get(0));
                broadcast(new Packet(nicknames));
                broadcast(new Packet("MESSAGE", "Server", data[1] + " has joined the chat."));
                break;
            case MESSAGE:
                System.out.println(data[1]);
                broadcast(new Packet("MESSAGE", data[1], data[2]));
                break;
            case LOGOUT:
                nicknames.remove(data[1]);
                broadcast(new Packet(nicknames));
                broadcast(new Packet("MESSAGE", "Server", data[1] + " has left the chat."));
                break;
            default:
                System.out.println("Invalid request.");
        }
    }

    private void broadcast(Packet p) {
        for(DataOutputStream o : outputs) {
            try {
                o.writeUTF(p.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopServer() {
        running = false;
    }

}
