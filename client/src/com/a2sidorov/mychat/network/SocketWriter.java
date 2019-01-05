package com.a2sidorov.mychat.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SocketWriter implements Runnable {

    private SocketChannel channel;
    private BlockingQueue<String> packetsQueue;
    private ByteBuffer buffer;

    SocketWriter(SocketChannel channel) {
        this.channel = channel;
        this.packetsQueue = new ArrayBlockingQueue<>(32);
        buffer = ByteBuffer.allocate(1024);
    }

    @Override
    public void run() {
        String packet = "";
        int bytesWriten = 0;
        byte[] bytesToWrite;
        int packetSize;
        while(true) {
            System.out.println("writing loop");
            try {
                packet = packetsQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            bytesToWrite = packet.getBytes();
            packetSize = bytesToWrite.length;

            buffer.putInt(packetSize);
            buffer.put(bytesToWrite);
            packetSize = buffer.position();
            buffer.flip();
            try {
                bytesWriten = channel.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (2 + packetSize == bytesWriten) { // 4 bytes added for int value
                buffer.clear();
            } else {
                buffer.compact();
            }

        }

    }

    public void add(String message) {
        String packet = "/m/" + message;
        try {
            packetsQueue.put(packet);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
