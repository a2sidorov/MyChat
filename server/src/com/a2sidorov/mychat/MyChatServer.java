package com.a2sidorov.mychat;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * The MyChat MyChatServer.
 *
 * @author Andrei Sidorov
 * @version 1.0
 * @since 2018.11.09
 */

public class MyChatServer {

    private int port;
    private InetAddress address;



    private MyChatServer(Properties properties) {
        try {
            this.address = InetAddress.getByName(properties.getProperty("address"));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.port = Integer.parseInt(properties.getProperty("port"));
    }

    //implementing the Singleton pattern
    private static MyChatServer server;
    static MyChatServer getInstance(Properties properties) {
        if (server == null) {
            server = new MyChatServer(properties);
        }
        return server;
    }

    void start() {

        BlockingQueue<SocketChannel> socketQueue = new ArrayBlockingQueue<>(64);
        BlockingQueue<String> inboundPacketQueue = new ArrayBlockingQueue<String>(64);
        BlockingQueue<String> outboundPacketQueue = new ArrayBlockingQueue<String>(64);
        Map<String, String> nicknames = new HashMap<>();

        SocketAccepter socketAccepter = new SocketAccepter(this.address, this.port, socketQueue);

        SocketProcessor socketProcessor = new SocketProcessor(
                socketQueue,
                inboundPacketQueue,
                outboundPacketQueue,
                nicknames);

        Thread accepterThread = new Thread(socketAccepter);
        Thread processorThread = new Thread(socketProcessor);


        accepterThread.start();
        processorThread.start();
    }

    public static void main(String[] args) {

        SettingsLoader config = new SettingsLoader();
        Properties properties = config.load();

        MyChatServer server = MyChatServer.getInstance(properties);
        server.start();
    }

}
