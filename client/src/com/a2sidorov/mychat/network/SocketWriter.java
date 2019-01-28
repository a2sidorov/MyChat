package com.a2sidorov.mychat.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;

class SocketWriter implements Runnable {

    private SocketChannel socketChannel;
    private ByteBuffer writeBuffer;
    private BlockingQueue<String> inboundPacketQueue;
    private BlockingQueue<String> outboundPacketQueue;

    SocketWriter(SocketChannel socketChannel,
                 ByteBuffer writeBuffer,
                 BlockingQueue<String> inboundPacketQueue,
                 BlockingQueue<String> outboundPacketQueue) {
        this.socketChannel = socketChannel;
        this.writeBuffer = writeBuffer;
        this.inboundPacketQueue = inboundPacketQueue;
        this.outboundPacketQueue = outboundPacketQueue;
    }

    @Override
    public void run() {
        while (true) {
            writeToSocket();
        }
    }

    void writeToSocket() {
        String packet = "";
        try {
            packet = this.outboundPacketQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        writeBuffer.putShort((short) packet.getBytes().length);
        writeBuffer.put(packet.getBytes());
        writeBuffer.flip();

        int bytesWritten = 0;
        try {
            while(writeBuffer.hasRemaining() && bytesWritten != -1) {
                bytesWritten = socketChannel.write(writeBuffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bytesWritten == -1) {
            try {
                socketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            inboundPacketQueue.add("c/");
            return;
        }
        writeBuffer.clear();
    }

}
