package com.a2sidorov.mychat;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;

class SocketAccepter implements Runnable {

    private InetAddress address;
    private int port;
    private ServerSocketChannel serverSocketChannel;
    private BlockingQueue<SocketChannel> socketQueue;

    SocketAccepter(InetAddress address, int port, BlockingQueue<SocketChannel> socketQueue) {
        this.port = port;
        this.address = address;
        this.socketQueue = socketQueue;
    }

    public void run() {
        try {
            this.serverSocketChannel = ServerSocketChannel.open();
            this.serverSocketChannel.bind(new InetSocketAddress(address, port));
            System.out.println("Listening for connections ...");
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(true) {
            try {

                SocketChannel socketChannel = this.serverSocketChannel.accept();
                System.out.println(socketChannel + " has connected.");
                this.socketQueue.add(socketChannel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}


