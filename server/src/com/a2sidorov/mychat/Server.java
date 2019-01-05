package com.a2sidorov.mychat;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * The MyChat Server.
 *
 * @author Andrei Sidorov
 * @version 1.0
 * @since 2018.11.09
 */

public class Server {

    private SocketAccepter socketAccepter;
    private SocketProcessor socketProcessor;

    private int port;
    private InetAddress address;

    private Server(String address, int port) {
        this.port = port;
        try {
            this.address = InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

    //implementing the Singleton pattern
    private static Server server;
    public static Server getInstance(String address, int port) {
        if (server == null) {
            server = new Server(address, port);
        }
        return server;
    }

    public void start() throws IOException {

        BlockingQueue<SocketChannel> socketQueue = new ArrayBlockingQueue<>(1024);

        this.socketAccepter = new SocketAccepter(this.address, this.port, socketQueue);
        this.socketProcessor = new SocketProcessor(socketQueue);

        Thread accepterThread = new Thread(this.socketAccepter);
        Thread processorThread = new Thread(this.socketProcessor);


        accepterThread.start();
        processorThread.start();
    }

    public static void main(String[] args) throws IOException {
        Server server = Server.getInstance("localhost", 1050);
        server.start();
    }

}
