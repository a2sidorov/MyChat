package com.a2sidorov.mychat;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;

class SocketWriter {
    private BlockingQueue<String> outboundPacketQueue;
    private List<Client> clients;

    SocketWriter(BlockingQueue<String> outboundPacketQueue, List<Client> clients) {
        this.outboundPacketQueue = outboundPacketQueue;
        this.clients = clients;
    }

    void writeToSocket(SelectionKey key, ByteBuffer writeBuffer, String packet) throws IOException {
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

            String nickname = "";

            Iterator<Client> iter = clients.iterator();
            while(iter.hasNext()) {
                Client client = iter.next();
                if (client.getSocketChannel() == socketChannel) {

                    nickname = client.getNickname();
                    iter.remove();
                }
            }

            System.out.println(socketChannel + " has disconnected.");
            this.outboundPacketQueue.add("s/" + nickname + " has left the chat.");

            key.cancel();
            socketChannel.close();
            return;
        }



        writeBuffer.clear();
    }


}
