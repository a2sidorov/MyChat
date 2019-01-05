package com.a2sidorov.mychat.network;

import com.a2sidorov.mychat.MychatClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;

public class Client {

    private int serverPort;
    private String serverAddress;
    private String nickname = "Noname";
    private SocketChannel channel;
    private ByteBuffer buffer;

    SocketReader socketReader;
    SocketWriter socketWriter;

    public void connectToServer(String serverAddress, int serverPort) throws IOException {
        channel = SocketChannel.open(new InetSocketAddress(serverAddress, serverPort));
        buffer = ByteBuffer.allocate(256);

        socketReader = new SocketReader(channel);
        socketWriter = new SocketWriter(channel);

        Thread readerThread = new Thread(socketReader);
        Thread writerThread = new Thread(socketWriter);

        readerThread.start();
        writerThread.start();

    }


    public void sendMessage(String message) {

        socketWriter.add(message);
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }



}
