package com.a2sidorov.mychat.network;

import com.a2sidorov.mychat.MychatClient;

import javax.swing.*;
import javax.swing.text.Utilities;
import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLOutput;

public class NetworkClient {

    private String ipAddress;
    private int serverPort;
    private Socket socket;
    //private ObjectInputStream input;
    //private ObjectOutputStream output;
    private DataOutputStream output;
    private DataInputStream input;
    private static String nickname;

    public static void setNickname(String nickname) {
        NetworkClient.nickname = nickname;
    }

    public NetworkClient(String ipAddress, int serverPort) {
        this.ipAddress = ipAddress;
        this.serverPort = serverPort;
    }

    public void connectToServer() {
        new Thread(() -> {
            try {
                //connecting to server
                socket = new Socket(ipAddress, serverPort);
                System.out.println("Client socket initialized on port " + socket.getPort());

                //setting up streams
                output = new DataOutputStream(socket.getOutputStream());
                input = new DataInputStream(socket.getInputStream());
                System.out.println("Streams are set up.");

                //sending login packet
                output.writeUTF("LOGIN;" + nickname);

                while(socket.isConnected()) {
                    //MychatClient.textChat.append(input.readUTF() + "\n");
                    String rawData = input.readUTF();
                    process(rawData);
                }

            } catch(ConnectException ce) {
                MychatClient.textChat.append("Server is down.\n");
                return;
            } catch(SocketException se) {
                return;
            } catch(IOException e) {
                e.printStackTrace();
                return;
            }

        }).start();

    }

    private void process(String rawData) {
        String[] data = rawData.trim().split(";");
        System.out.println(rawData);
        PacketType type = PacketType.valueOf(data[0]);
        switch(type) {
            case MESSAGE:
                MychatClient.textChat.append(data[1] +": " +  data[2] + "\n");
                break;
            case NICKNAMES:
                String[] userlist = java.util.Arrays.copyOfRange(data, 1, data.length);
                //System.out.println(str);
                MychatClient.listUsers.setListData(userlist);
                break;
                default: System.out.println("Invalid packet.");
        }

    }


    public void send(String text) {
        try {
            if(socket == null && text.equals("enter")) {
                connectToServer();
            }
            if(output != null) {
                output.writeUTF("MESSAGE;" + nickname + ";" + text);
                output.flush();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if(socket != null) {
                output.writeUTF("LOGOUT;" + nickname);
                output.close();
                input.close();
                socket.close();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }



}
