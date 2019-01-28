package com.a2sidorov.mychat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

class SocketWriter {
    private ByteBuffer writeBuffer;

    SocketWriter(ByteBuffer writeBuffer) {
        this.writeBuffer = writeBuffer;
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
            key.cancel();
            socketChannel.close();
            return;
        }
        writeBuffer.clear();
    }

}
