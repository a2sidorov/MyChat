package com.a2sidorov.mychat.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class NetworkClient {

    private SocketChannel socketChannel;
    private ByteBuffer readBuffer;
    private ByteBuffer writeBuffer;
    private BlockingQueue<String> inboundPacketQueue;

    private BlockingQueue<String> outboundPacketQueue;
    private SocketReader socketReader;
    private SocketWriter socketWriter;

    public NetworkClient() {
        this.readBuffer = ByteBuffer.allocate(1024);
        this.writeBuffer = ByteBuffer.allocate(1024);
        this.inboundPacketQueue = new ArrayBlockingQueue<>(64);
        this.outboundPacketQueue = new ArrayBlockingQueue<>(64);
    }

    public void connectToServer(String serverAddress, String serverPort) throws IOException {

        InetAddress address = InetAddress.getByName(serverAddress);
        int port = Integer.parseInt(serverPort);

        socketChannel = SocketChannel.open(new InetSocketAddress(address, port));

        socketReader = new SocketReader(
                this.socketChannel,
                this.readBuffer,
                this.inboundPacketQueue);

        socketWriter = new SocketWriter(
                this.socketChannel,
                this.writeBuffer,
                this.inboundPacketQueue,
                this.outboundPacketQueue);

        Thread readerThread = new Thread(socketReader);
        Thread writerThread = new Thread(socketWriter);

        readerThread.start();
        writerThread.start();
    }

    public BlockingQueue<String> getInboundPacketQueue() {
        return inboundPacketQueue;
    }

    public void sendMessage(String message) {
        outboundPacketQueue.add("m/" + message);
    }

    public void sendNickname(String nickname) throws IOException {
        outboundPacketQueue.add("n/" + socketChannel.getLocalAddress().toString() + "/" + nickname);
    }



}
