package com.a2sidorov.mychat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

class SocketWriter {
    private BlockingQueue<String> outboundPacketQueue;
    private ByteBuffer writeBuffer;
    private Map<String, String> nicknames;

    SocketWriter(BlockingQueue<String> outboundPacketQueue,
                 ByteBuffer writeBuffer,
                 Map<String, String> nicknames) {
        this.outboundPacketQueue = outboundPacketQueue;
        this.writeBuffer = writeBuffer;
        this.nicknames = nicknames;
    }

    void writeToSocket(SelectionKey key, String packet) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        byte[] packetBytes = packet.getBytes();
        writeBuffer.putShort((short) packetBytes.length);
        writeBuffer.put(packetBytes);
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

            String nickname = nicknames.remove(socketChannel.getRemoteAddress().toString());
            this.outboundPacketQueue.add("s/" + nickname + " has left the chat.");

            key.cancel();
            socketChannel.close();
            return;
        }

        writeBuffer.clear();
    }


}
