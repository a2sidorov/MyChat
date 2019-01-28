package com.a2sidorov.mychat.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;

class SocketReader implements Runnable {

    private SocketChannel socketChannel;
    private ByteBuffer readBuffer;
    private BlockingQueue<String> inboundPacketQueue;

    SocketReader(SocketChannel socketChannel,
                 ByteBuffer readBuffer,
                 BlockingQueue<String> inboundPacketQueue) {
        this.socketChannel = socketChannel;
        this.readBuffer = readBuffer;
        this.inboundPacketQueue = inboundPacketQueue;
    }

    @Override
    public void run() {
        while(true) {
            readFromSocket();
        }
    }

    void readFromSocket() {
            int bytesRead;
            try {
                bytesRead = this.socketChannel.read(this.readBuffer);
            } catch (IOException e) {
                return;
            }

            if(bytesRead == 0) {
                return;
            }

            if (bytesRead == -1) {
                try {
                    this.socketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.inboundPacketQueue.add("c/");
                return;
            }
            this.readBuffer.flip();
            processFullPackets();
    }

    void processFullPackets() {
        while (this.readBuffer.hasRemaining()) {
            int bytesLeft = this.readBuffer.limit() - this.readBuffer.position();

            if (bytesLeft == 1) {
                this.readBuffer.clear();
                this.readBuffer.position(1);
                return;
            }
            short packetSize = this.readBuffer.getShort();

            if (packetSize > bytesLeft - 2) {
                this.readBuffer.clear();
                this.readBuffer.position(bytesLeft);
                return;
            }
            byte[] packetBytes = new byte[packetSize];
            this.readBuffer.get(packetBytes);
            this.inboundPacketQueue.add(new String(packetBytes));
        }
        this.readBuffer.clear();
    }

}
