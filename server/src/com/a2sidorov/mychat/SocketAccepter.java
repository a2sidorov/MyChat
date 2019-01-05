package com.a2sidorov.mychat;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class SocketAccepter implements Runnable {

    private int port;
    private InetAddress address;
    private ServerSocketChannel serverSocket;
    private BlockingQueue<SocketChannel> socketQueue;

    SocketAccepter(InetAddress address, int port, BlockingQueue<SocketChannel> socketQueue) {
        this.port = port;
        this.address = address;
        this.socketQueue = socketQueue;

    }

    public void run() {

        try {
            this.serverSocket = ServerSocketChannel.open();
            this.serverSocket.bind(new InetSocketAddress(address, port));
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(true) {
            System.out.println("socket accepter loop");
            try {
                SocketChannel socketChannel = this.serverSocket.accept();
                System.out.println("Socket accepted: " + socketChannel);
                this.socketQueue.add(socketChannel);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }



}


