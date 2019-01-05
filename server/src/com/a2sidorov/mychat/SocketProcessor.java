package com.a2sidorov.mychat;

import jdk.management.resource.internal.inst.SocketChannelImplRMHooks;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
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

    private List<SocketChannel> socketChannels;

    private ByteBuffer readByteBuffer;
    private ByteBuffer partialReadBuffer;
    private ByteBuffer writeByteBuffer;

    private ByteBuffer packetBuffer;

    private Selector readSelector;
    private Selector writeSelector;

    private BlockingQueue<SocketChannel> socketQueue;
    private BlockingQueue<String> messageQueue;

    private MessageReader messageReader;

    public SocketProcessor(BlockingQueue<SocketChannel> socketQueue) throws IOException {
        this.socketQueue = socketQueue;
        this.messageQueue = new ArrayBlockingQueue<String>(1024);
        this.readSelector = Selector.open();
        this.writeSelector = Selector.open();
        this.socketChannels = new LinkedList<>();
        readByteBuffer = ByteBuffer.allocate(1024);
        partialReadBuffer = ByteBuffer.allocate(1024);
        writeByteBuffer = ByteBuffer.allocate(1024);
        packetBuffer = ByteBuffer.allocate(1024);

        this.messageReader = new MessageReader(messageQueue);
    }

    public void run() {
        while (true) {
            try {
                //System.out.println("socket processor loop");
                executeCycle();
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

    private void executeCycle() throws IOException {
        takeNewSockets();
        readFromSockets();
        writeToSockets();
    }

    private void takeNewSockets() throws IOException {
        SocketChannel socketChannel = this.socketQueue.poll();


        while(socketChannel != null) {
            socketChannels.add(socketChannel);
            socketChannel.configureBlocking(false);
            SelectionKey key = socketChannel.register(this.readSelector, SelectionKey.OP_READ);
            //key.attach(socketChannel);
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
                readFromSocket(key);
                keyIterator.remove();
            }
            selectedKeys.clear();
        }
    }

    private void readFromSocket(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        byte[] partiallyReadBytes = (byte[]) key.attachment();

        if (partiallyReadBytes != null) {
            readByteBuffer.put(partiallyReadBytes);
        }

        int bytesRead;
        try {
            bytesRead = socketChannel.read(this.readByteBuffer);
        } catch (IOException e) {
            key.cancel();
            socketChannel.close();
            return;
        }

        if (bytesRead == -1) {
            key.channel().close();
            key.cancel();
            return;
        }

        readByteBuffer.flip();
        partiallyReadBytes = messageReader.processReadBuffer(readByteBuffer, messageQueue);
        key.attach(partiallyReadBytes);
        //messageQueue.add(drain(readByteBuffer));
    }



    private void writeToSockets() throws IOException {

        registerSocketsForWriting();

        String message = messageQueue.poll();

        while (message != null) {

            int writeReady = this.writeSelector.selectNow();

            if (writeReady > 0) {
                Set<SelectionKey> selectionKeys = this.writeSelector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectionKeys.iterator();

                while(keyIterator.hasNext()){
                    SelectionKey key = keyIterator.next();

                    SocketChannel socketChannel = (SocketChannel) key.channel();

                    writeToSocket(socketChannel, message);

                    keyIterator.remove();
                }
                selectionKeys.clear();
            }

            message = messageQueue.poll();
        }
    }

    private void registerSocketsForWriting() throws ClosedChannelException {
        for(SocketChannel socketChannel: socketChannels) {
            try {
                socketChannel.register(this.writeSelector, SelectionKey.OP_WRITE);
            } catch(ClosedChannelException e) {
                socketChannels.remove(socketChannel);
            }

        }
    }

    private void writeToSocket(SocketChannel socketChannel, String message) throws IOException {
        System.out.println("writing to sockets " + message);
        writeByteBuffer.put(message.getBytes());
        writeByteBuffer.flip();
        socketChannel.write(writeByteBuffer);
        writeByteBuffer.clear();
    }


}
