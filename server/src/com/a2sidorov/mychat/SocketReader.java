package com.a2sidorov.mychat;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;

class SocketReader {

    private BlockingQueue<String> inboundPacketQueue;
    private BlockingQueue<String> outboundPacketQueue;
    private List<Client> clients;

    SocketReader(BlockingQueue<String> inboundPacketQueue) {
        this.inboundPacketQueue = inboundPacketQueue;
    }

    void readFromSocket(SelectionKey key, ByteBuffer readBuffer) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        byte[] partialPacket = (byte[]) key.attachment();

        if (partialPacket != null) {
            readBuffer.put(partialPacket);
            key.attach(null);
        }

        int bytesRead = 0;
        try {
            bytesRead = socketChannel.read(readBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bytesRead == -1) {
            key.cancel();
            socketChannel.close();
            return;
        }



        readBuffer.flip();
        readFullPackets(readBuffer);

        if (readBuffer.position() != readBuffer.limit()) {
            int numOfBytes = readBuffer.limit() - readBuffer.position();
            partialPacket = new byte[numOfBytes];
            readBuffer.get(partialPacket);
            key.attach(partialPacket);
        }
        readBuffer.clear();
    }

    void readFullPackets(ByteBuffer readBuffer) {

        short packetSize;
        int bytesInBuffer;

        while (readBuffer.hasRemaining()) {
            bytesInBuffer = readBuffer.limit() - readBuffer.position();

            if (bytesInBuffer == 1) { //return if the size of a packet(first two bytes) is not fully read
                return;
            }

            readBuffer.mark();
            packetSize = readBuffer.getShort();
            bytesInBuffer = readBuffer.limit() - readBuffer.position();

            if (packetSize <= bytesInBuffer) {
                byte[] packetBytes = new byte[packetSize];
                readBuffer.get(packetBytes);
                this.inboundPacketQueue.add(new String(packetBytes));
            } else {
                readBuffer.reset();
                return; //returns if a packet is not fully read

            }
        }
    }

}
