package com.a2sidorov.mychat;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class NetworkServer {

    /*

    private boolean running = false;
    private ArrayList<DataOutputStream> outputs;
    private String charset = "UTF-8";
    private ServerSocketChannel channel;
    private ByteBuffer buffer;
    private ByteBuffer messageBuffer;
    private Set<SocketChannel> channels = new HashSet<>();
    private List<String> nicknames = new ArrayList<>();


    private NetworkServer(int port) throws Exception {
        this.port = port;
        this.address = InetAddress.getLoopbackAddress();
        buffer = ByteBuffer.allocate(256);

    }

    //implementing the Singleton pattern.
    private static NetworkServer server;
    public static NetworkServer getInstance(int port) throws Exception {
        if(server == null) {
            server = new NetworkServer(port);
        }
        return server;
    }

    public void stopServer() {
        running = false;
    }

    public void startServer() throws Exception {
        Selector selector = Selector.open();
        channel = ServerSocketChannel.open();
        channel.bind(new InetSocketAddress(address, port));
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_ACCEPT);




        System.out.println("Listening for connections ... ");
        running = true;
        new Thread(() -> {
            while (running) {

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

    private void register(Selector selector, ServerSocketChannel serverSocket) throws IOException {
        SocketChannel channel = serverSocket.accept();
        channels.add(channel);
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ);
    }

    private void read(Selector selector, SelectionKey key) throws IOException {
        System.out.println("reading");
        SocketChannel channel = (SocketChannel) key.channel();

        channel.read(buffer);
        buffer.flip();
        char packetType = buffer.getChar();
        String data = drain();
        buffer.clear();

        if (packetType == 'm') {
            messageBuffer.putChar('m');
            messageBuffer.put(data.getBytes());
            for(SocketChannel c : channels) {
                c.register(selector, SelectionKey.OP_WRITE);
            }

        }
    }

    private void write(SelectionKey key) throws IOException {
        System.out.println("writing");
        SocketChannel channel = (SocketChannel) key.channel();
        String s = "hello";
        buffer.put(s.getBytes());
        buffer.flip();
        channel.write(buffer);
        buffer.clear();
        key.cancel();
    }

    public boolean isRunning() {
        return channel.isOpen();
    }







    private String drain() {
        StringBuffer sb = new StringBuffer();
        while (buffer.hasRemaining()) {
            sb.append((char)buffer.get());
        }
        return sb.toString();
    }

    private void fill(String str) {
        for (int i = 0; i < str.length(); i++) {
            buffer.putChar(str.charAt(i));
        }
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();

        channel.read(buffer);
        buffer.flip();
        char packetType = buffer.getChar();
        String data = drain();
        buffer.clear();

        if (packetType == 'c') {
            nicknames.add(data);
            broadcastNicnameList(nicknames);
            String message = data + " has joined the chat.";
            broadcastMessage(message);
        }

        if (packetType == 'm') {
           broadcastMessage(data);
        }

    }





    private void broadcastMessage(String message) throws IOException {
        buffer.putChar('m');
        buffer.put(message.getBytes());
        buffer.flip();
        for(SocketChannel c : channels) {
            c.write(buffer);
            buffer.rewind();
        }
        buffer.clear();
    }

    private void broadcastNicnameList(List<String> nicknames) throws IOException {
        buffer.putChar('l');

        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < nicknames.size(); i++) {
            if (i == nicknames.size() - 1) {
                sb.append(nicknames.get(i));
                break;
            }
            sb.append(nicknames.get(i));
            sb.append(",");
        }
        buffer.put(sb.toString().getBytes());

        buffer.flip();

        for (SocketChannel c : channels) {
            c.write(buffer);
            buffer.rewind();
        }
        buffer.clear();
    }
    */




}
