package com.a2sidorov.mychat.network;

import com.a2sidorov.mychat.controller.MainController;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;

class SocketWriter implements Runnable {

    private SocketChannel socketChannel;
    private ByteBuffer writeBuffer;
    private BlockingQueue<String> packetQueue;
    private MainController mainController;

    SocketWriter(SocketChannel socketChannel,
                 ByteBuffer writeBuffer,
                 BlockingQueue<String> packetQueue,
                 MainController mainController) {
        this.socketChannel = socketChannel;
        this.writeBuffer = writeBuffer;
        this.packetQueue = packetQueue;
        this.mainController = mainController;
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("writing loop");
            writeToSocket();
        }
    }

    void writeToSocket() {

        String packet = "";
        try {
            packet = this.packetQueue.take();
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
            mainController.updateTextArea("Server has closed the connection");
        }
        writeBuffer.clear();

    }

}
