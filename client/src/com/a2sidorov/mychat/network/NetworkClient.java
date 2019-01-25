package com.a2sidorov.mychat.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;

public class NetworkClient {

    private InetAddress serverAddress;
    private int serverPort;
    private String nickname;
    private SocketChannel socketChannel;
    private ByteBuffer readBuffer;
    private ByteBuffer writeBuffer;

    SocketReader socketReader;
    SocketWriter socketWriter;

    public NetworkClient() {
        readBuffer = ByteBuffer.allocate(1024);
        writeBuffer = ByteBuffer.allocate(1024);
    }

    public void connectToServer(String serverAddress, int serverPort) throws IOException {
        socketChannel = SocketChannel.open(new InetSocketAddress(serverAddress, serverPort));

        //socketReader = new SocketReader(socketChannel);
        socketWriter = new SocketWriter(socketChannel);

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
