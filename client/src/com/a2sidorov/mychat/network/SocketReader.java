package com.a2sidorov.mychat.network;

import com.a2sidorov.mychat.MychatClient;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SocketReader implements Runnable {

    private SocketChannel channel;
    private ByteBuffer readByteBuffer;

    public SocketReader(SocketChannel channel) {
        this.channel = channel;
        this.readByteBuffer = ByteBuffer.allocate(1024);
    }

    @Override
    public void run() {
        while(true) {
            System.out.println("reading loop");
            int bytesRead;
            try {
                bytesRead = channel.read(this.readByteBuffer);
            } catch (IOException e) {
                return;
            }

            if (bytesRead == -1) {
                System.out.println("End of stream");
                return;
            }

            readByteBuffer.flip();
            String message = drain(readByteBuffer);
            MychatClient.textChat.append(message + '\n');
            readByteBuffer.clear();
        }


    }

    private String drain(ByteBuffer buffer) {
        StringBuffer sb = new StringBuffer();
        while (buffer.hasRemaining()) {
            sb.append((char)buffer.get());
        }
        return sb.toString();
    }
}
