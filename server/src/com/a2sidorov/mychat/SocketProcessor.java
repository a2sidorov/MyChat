package com.a2sidorov.mychat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class SocketProcessor implements Runnable {
    private BlockingQueue<SocketChannel> socketQueue;
    private List<Client> clients;

    private BlockingQueue<String> inboundPacketQueue;
    private BlockingQueue<String> outboundPacketQueue;

    private ByteBuffer readBuffer;
    private ByteBuffer writeBuffer;

    private Selector readSelector;
    private Selector writeSelector;

    private SocketReader socketReader;
    private SocketWriter socketWriter;

    private PacketParser packetParser;

    SocketProcessor(BlockingQueue<SocketChannel> socketQueue) {
        this.socketQueue = socketQueue;
        this.clients = new LinkedList<>();

        this.inboundPacketQueue = new ArrayBlockingQueue<String>(64);
        this.outboundPacketQueue  = new ArrayBlockingQueue<String>(64);

        this.readBuffer = ByteBuffer.allocate(1024);
        this.writeBuffer = ByteBuffer.allocate(1024);

        try {
            this.readSelector = Selector.open();
            this.writeSelector = Selector.open();

        } catch (IOException e) {
            e.printStackTrace();
        }

        this.socketReader = new SocketReader(this.inboundPacketQueue);
        this.packetParser = new PacketParser(this.inboundPacketQueue, this.outboundPacketQueue, this.clients);
        this.socketWriter = new SocketWriter(this.outboundPacketQueue, this.clients);
    }

    public void run() {
        while (true) {
            try {
                register();
                readFromSockets();
                packetParser.parse();
                writeToSockets();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void register() throws IOException {
        SocketChannel socketChannel = this.socketQueue.poll();

        while (socketChannel != null) {
            clients.add(new Client(socketChannel));
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
                socketReader.readFromSocket(key, this.readBuffer);
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
                    socketWriter.writeToSocket(key, this.writeBuffer, packet);
                    keyIterator.remove();
                }

                selectionKeys.clear();
            }
            packet = outboundPacketQueue.poll();
        }
    }




}
