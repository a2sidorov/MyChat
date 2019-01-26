package com.a2sidorov.mychat.network;

import com.a2sidorov.mychat.controller.MainController;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

class SocketReader implements Runnable {

    private SocketChannel socketChannel;
    private ByteBuffer readBuffer;
    private MainController mainController;

    SocketReader(SocketChannel socketChannel, ByteBuffer readBuffer, MainController mainController) {
        this.socketChannel = socketChannel;
        this.readBuffer = readBuffer;
        this.mainController = mainController;
    }

    @Override
    public void run() {
        while(true) {
            readFromSocket();
        }
    }

    void readFromSocket() {
        int bytesRead;
        try {
            bytesRead = socketChannel.read(this.readBuffer);
        } catch (IOException e) {
            return;
        }

        if (bytesRead == -1) {
            mainController.updateTextArea("Server has closed the connection");
            return;
        }

        this.readBuffer.flip();
        processFullPackets();
    }

    void processFullPackets() {

        short packetSize;
        int bytesInBuffer;

        while (readBuffer.hasRemaining()) {
            bytesInBuffer = readBuffer.limit() - readBuffer.position();

            if (bytesInBuffer == 1) { //return if the size of a packet(first two bytes) is not fully read
                this.readBuffer.clear();
                this.readBuffer.position(bytesInBuffer);
                return;
            }

            //readBuffer.mark();
            packetSize = this.readBuffer.getShort();

            if (packetSize <= bytesInBuffer - 2) {
                byte[] packetBytes = new byte[packetSize];
                this.readBuffer.get(packetBytes);
                parsePacket(new String(packetBytes));
            } else {
                this.readBuffer.clear();
                this.readBuffer.position(bytesInBuffer);
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
