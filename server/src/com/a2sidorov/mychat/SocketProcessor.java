package com.a2sidorov.mychat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

class SocketProcessor implements Runnable {
    private BlockingQueue<SocketChannel> socketQueue;
    private Map<String, String> nicknames;

    private BlockingQueue<String> inboundPacketQueue;
    private BlockingQueue<String> outboundPacketQueue;

    private ByteBuffer readBuffer;
    private ByteBuffer writeBuffer;

    private Selector readSelector;
    private Selector writeSelector;

    private SocketReader socketReader;
    private SocketWriter socketWriter;

    SocketProcessor(BlockingQueue<SocketChannel> socketQueue,
                    BlockingQueue<String> inboundPacketQueue,
                    BlockingQueue<String> outboundPacketQueue,
                    Map<String, String> nicknames) {
        this.socketQueue = socketQueue;
        this.nicknames = nicknames;

        this.inboundPacketQueue = inboundPacketQueue;
        this.outboundPacketQueue  = outboundPacketQueue;

        this.readBuffer = ByteBuffer.allocate(1024);
        this.writeBuffer = ByteBuffer.allocate(1024);

        try {
            this.readSelector = Selector.open();
            this.writeSelector = Selector.open();

        } catch (IOException e) {
            e.printStackTrace();
        }

        this.socketReader = new SocketReader(
                this.inboundPacketQueue,
                this.outboundPacketQueue,
                this.readBuffer,
                this.nicknames);

        this.socketWriter = new SocketWriter(this.readBuffer);
    }

    public void run() {
        while (true) {
            try {
                register();
                readFromSockets();
                parsePackets();
                writeToSockets();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void register() throws IOException {
        SocketChannel socketChannel = this.socketQueue.poll();

        while (socketChannel != null) {
            nicknames.put(socketChannel.getRemoteAddress().toString(), "Unknown");
            socketChannel.configureBlocking(false);
            socketChannel.register(this.readSelector, SelectionKey.OP_READ);
            socketChannel.register(this.writeSelector, SelectionKey.OP_WRITE);
            socketChannel = this.socketQueue.poll();
        }
    }

    private void readFromSockets() throws IOException {
        int readReady = this.readSelector.selectNow();

        if (readReady > 0) {
            Set<SelectionKey> selectedKeys = this.readSelector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            while(keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                socketReader.readFromSocket(key);
                keyIterator.remove();
            }
            selectedKeys.clear();
        }
    }

    private void writeToSockets() throws IOException {

        String packet = outboundPacketQueue.poll();

        while (packet != null) {
            int writeReady = this.writeSelector.selectNow();

            if (writeReady > 0) {
                Set<SelectionKey> selectionKeys = this.writeSelector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectionKeys.iterator();

                while(keyIterator.hasNext()){
                    SelectionKey key = keyIterator.next();
                    socketWriter.writeToSocket(key, packet);
                    keyIterator.remove();
                }
                selectionKeys.clear();
            }
            packet = outboundPacketQueue.poll();
        }
    }

    void parsePackets() {
        /*
        Inbound packet prefixes:
        m/ - user message (eg: "m/Nickname: message");
        n/ - nickname (eg: "n/127.0.0.1/nickname");

        Outbound packet prefixes:
        m/ - user message (eg: "m/Nickname: message");
        s/ - server notification (eg: "s/notification");
        n/ - nickname list (eg: "n/[nickname1, nickname2]");
        */

        String inboundPacket = this.inboundPacketQueue.poll();

        while (inboundPacket != null) {
            String prefix = inboundPacket.substring(0, 2);

            if (prefix.equals("n/")) {
                int i = inboundPacket.lastIndexOf('/');
                String address = inboundPacket.substring(2, i);
                String nickname = inboundPacket.substring(i + 1);

                nicknames.replace(address, nickname);

                List<String> list = nicknames.entrySet()
                        .stream()
                        .map(e -> e.getValue())
                        .collect(Collectors.toList());

                outboundPacketQueue.add("s/" + nickname + " has joined the chat.");
                outboundPacketQueue.add("n/" + list);
            }

            if (prefix.equals("m/")) {
                outboundPacketQueue.add(inboundPacket);
            }
            inboundPacket = inboundPacketQueue.poll();
        }

    }

}
