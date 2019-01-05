package com.a2sidorov.mychat.network;

import com.a2sidorov.mychat.MychatClient;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class NetworkClient {
    /*

    private SocketChannel channel;
    private String serverAddress;
    private int serverPort;
    private String nickname = "Noname";
    private ByteBuffer buffer;

    public NetworkClient(String serverAddress, int serverPort) throws Exception {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        buffer = ByteBuffer.allocate(256);

    }

    public void disconnect() throws IOException {
        channel.close();
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


    public void connect() throws IOException {
        Selector selector = Selector.open();
        channel = SocketChannel.open(new InetSocketAddress(serverAddress, serverPort));
        channel.configureBlocking(false);
        //sendNickname();

        new Thread(() -> {
            while (true) {

                try {
                    selector.select();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = selectedKeys.iterator();
                while (iter.hasNext()) {
                    System.out.println("looping");

                    SelectionKey key = iter.next();

                    if (key.isAcceptable()) {
                        try {
                            register(selector, channel);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Client connected.");
                    }

                    if (key.isReadable()) {
                        try {
                            read(selector, key);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    writeToSockets();

                    if (key.isWritable()) {
                        try {
                            write(key);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    iter.remove();
                }
            }
        }).start();
    }

    private void sendNickname() throws IOException {
        buffer.putChar('c');
        buffer.put(nickname.getBytes());
        buffer.flip();
        channel.write(buffer);
        buffer.clear();
    }

    private void process() throws IOException {
        channel.read(buffer);
        buffer.flip();
        char packetType = buffer.getChar();
        String data = drain(buffer);
        buffer.clear();

        if (packetType == 'l') {
            String[] userList = data.split(",");
            MychatClient.listUsers.setListData(userList);
        }

        if (packetType == 'm') {
            MychatClient.textChat.append(data + '\n');
        }

    }

    public void sendMessage(String message) throws IOException {
        buffer.putChar('m');
        String data = nickname + ": " + message;
        buffer.put(data.getBytes());
        buffer.flip();
        channel.write(buffer);
        buffer.clear();

    }

    private String drain(ByteBuffer buffer) {
        StringBuffer sb = new StringBuffer();
        while (buffer.hasRemaining()) {
            sb.append((char)buffer.get());
        }
        return sb.toString();
    }

    public boolean isConnected() {
        return channel.isConnected();
    }
    */

}
