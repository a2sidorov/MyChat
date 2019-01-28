package com.a2sidorov.mychat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

class SocketReader {

    private BlockingQueue<String> inboundPacketQueue;
    private BlockingQueue<String> outboundPacketQueue;
    private Map<String, String> nicknames;
    private ByteBuffer readBuffer;

    SocketReader(BlockingQueue<String> inboundPacketQueue,
                 BlockingQueue<String> outboundPacketQueue,
                 ByteBuffer readBuffer,
                 Map<String, String> nicknames) {
        this.inboundPacketQueue = inboundPacketQueue;
        this.outboundPacketQueue = outboundPacketQueue;
        this.nicknames = nicknames;
        this.readBuffer = readBuffer;
    }

    void readFromSocket(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        byte[] partialPacket = (byte[]) key.attachment();

        if (partialPacket != null) {
            this.readBuffer.put(partialPacket);
            key.attach(null);
        }

        int bytesRead = 0;
        try {
            bytesRead = socketChannel.read(this.readBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bytesRead == -1) {
            String nickname = nicknames.remove(socketChannel.getRemoteAddress().toString());
            this.outboundPacketQueue.add("s/" + nickname + " has left the chat.");

            List<String> list = nicknames.entrySet()
                    .stream()
                    .map(e -> e.getValue())
                    .collect(Collectors.toList());

            this.outboundPacketQueue.add("n/" + list);

            System.out.println(socketChannel.getRemoteAddress() + " has disconnected.");
            key.cancel();
            socketChannel.close();
            return;
        }

        this.readBuffer.flip();
        processFullPackets();

        if (this.readBuffer.position() != this.readBuffer.limit()) {
            int numOfBytes = this.readBuffer.limit() - this.readBuffer.position();
            partialPacket = new byte[numOfBytes];
            this.readBuffer.get(partialPacket);
            key.attach(partialPacket);
        }
        this.readBuffer.clear();
    }

    void processFullPackets() {

        short packetSize;
        int bytesInBuffer;

        while (this.readBuffer.hasRemaining()) {
            bytesInBuffer = this.readBuffer.limit() - this.readBuffer.position();

            if (bytesInBuffer == 1) { //return if the size of a packet(first two bytes) is not fully read
                return;
            }

            this.readBuffer.mark();
            packetSize = this.readBuffer.getShort();
            bytesInBuffer = this.readBuffer.limit() - this.readBuffer.position();

            if (packetSize <= bytesInBuffer) {
                byte[] packetBytes = new byte[packetSize];
                this.readBuffer.get(packetBytes);
                this.inboundPacketQueue.add(new String(packetBytes));
            } else {
                this.readBuffer.reset();
                return; //returns if a packet is not fully read

            }
        }

    }

}
