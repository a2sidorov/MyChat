package com.a2sidorov.mychat.network;

import com.a2sidorov.mychat.controller.MainController;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

class SocketReader implements Runnable {

    private SocketChannel socketChannel;
    private ByteBuffer readBuffer;
    private MainController mainController;

    SocketReader(SocketChannel socketChannel, MainController mainController) {
        this.socketChannel = socketChannel;
        this.readBuffer = ByteBuffer.allocate(1024);
        this.mainController = mainController;
    }

    @Override
    public void run() {
        while(true) {
            readFromSocket();
        }
    }

    private void readFromSocket() {
        int bytesRead;
        try {
            bytesRead = socketChannel.read(this.readBuffer);
        } catch (IOException e) {
            return;
        }

        if (bytesRead == -1) {
            System.out.println("End of stream");
            return;
        }

        readBuffer.flip();
        readFullPackets(readBuffer);
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
                parsePacket(new String(packetBytes));
            } else {
                readBuffer.reset();
                return; //returns if a packet is not fully read

            }
        }
    }

    void parsePacket(String message) {
        /*
        Inbound packet prefixes:
        m/ - user message
        s/ - server notofication
        n/ - nickname list
        */
        String prefix = message.substring(0, 2);

        if (prefix.equals("m/")) {
            mainController.updateTextArea(message.substring(2));
        }

        if (prefix.equals("s/")) {
            String serverMessage = message.substring(2);
            mainController.updateTextArea(serverMessage);
        }

        if (prefix.equals("n/")) {
            String[] nicknames = message.substring(3, message.length() - 1).split(",");
            mainController.updateListNicknames(nicknames);
        }


    }

}
