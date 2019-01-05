package com.a2sidorov.mychat;

import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;

class MessageReader {

    private BlockingQueue<String> messageQueue;

    MessageReader(BlockingQueue<String> messageQueue) {
        this.messageQueue = messageQueue;
    }

    byte[] processReadBuffer(ByteBuffer buffer, BlockingQueue<String> messageQueue) {

        short packetSize;
        int numOfBytes;

        while (buffer.hasRemaining()) {

            numOfBytes = buffer.limit() - buffer.position();

            if (numOfBytes == 1) { //return if the size of a packet(first two bytes) is not fully read
                byte[] partiallyReadBytes = new byte[1];
                buffer.get(partiallyReadBytes);
                buffer.clear();
                return partiallyReadBytes;
            }

            buffer.mark();
            packetSize = buffer.getShort();
            numOfBytes = buffer.limit() - buffer.position();

            if (packetSize <= numOfBytes) { //return if a packet is not fully read
                byte[] arr = new byte[packetSize];
                buffer.get(arr);
                messageQueue.add(new String(arr));
            } else {
                buffer.reset();
                byte[] partiallyReadBytes = new byte[2 + numOfBytes];
                buffer.get(partiallyReadBytes);
                buffer.clear();
                return partiallyReadBytes;
            }

        }
        buffer.clear();
        return null;

    }


}
