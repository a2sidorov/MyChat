package com.a2sidorov.mychat;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@DisplayName("Testing SocketWriter class")
class SocketWriterTest {

    private static BlockingQueue<String> outboundPacketQueue;
    private static List<Client> clients;
    private static ByteBuffer writeBuffer;

    private static SocketWriter socketWriter;

    private static String packetFull;
    private static byte[] packetBytesFull;
    private static byte[] packetBytesPart1;
    private static byte[] packetBytesPart2;

    @BeforeAll
    static void initAll() {
        outboundPacketQueue = new ArrayBlockingQueue<>(64);
        clients = new LinkedList<>();
        socketWriter = new SocketWriter(outboundPacketQueue, clients);
        writeBuffer = ByteBuffer.allocate(1024);

        packetFull = "m/message";
        packetBytesFull = new byte[2 + packetFull.getBytes().length];
        packetBytesFull[1] = (byte) packetFull.getBytes().length;
        System.arraycopy(packetFull.getBytes(),0, packetBytesFull,2, packetFull.getBytes().length);

        packetBytesPart1 = Arrays.copyOfRange(packetBytesFull, 0, 6);
        packetBytesPart2 = Arrays.copyOfRange(packetBytesFull, 6, packetBytesFull.length);
    }

    @Nested
    @DisplayName("Testing writeToSocket method")
    class writeToSocketTest {

        @DisplayName("when the method is called then write the passed packet to the socket")
        @Test
        void writeToSocketTest1() throws IOException {

            byte[] output = new byte[packetBytesFull.length];

            SocketChannel socketChannel = mock(SocketChannel.class);
            when(socketChannel.write(writeBuffer)).then(i -> {
                writeBuffer.get(output);
                return output.length;
            });

            SelectionKey key = mock(SelectionKey.class);
            when(key.channel()).thenReturn(socketChannel);
            when(key.attachment()).thenReturn(null);

            socketWriter.writeToSocket(key, writeBuffer, packetFull);

            assertTrue(Arrays.equals(packetBytesFull, output));
        }

        /*
        @DisplayName("when a packet is not written then attach it to the key")
        @Test
        void writeToSocketTest2() throws IOException {

            SocketChannel socketChannel = mock(SocketChannel.class);
            when(socketChannel.write(writeBuffer)).then(i -> {
                writeBuffer.position(0);
                return 0;
            });

            SelectionKey key = mock(SelectionKey.class);
            when(key.channel()).thenReturn(socketChannel);
            when(key.attachment()).thenReturn(null);
            when(key.attach(packetBytesFull)).then(i -> {
                when(key.attachment()).thenReturn(packetBytesFull);
                return null;
            });

            socketWriter.writeToSocket(key, packetFull);

            assertTrue(Arrays.equals(packetBytesFull, (byte[]) key.attachment()));
        }



        @DisplayName("when a packet is written partly then attach the remaining part to the key")
        @Test
        void writeToSocketTest3() throws IOException {

            byte[] output = new byte[packetBytesPart1.length];

            SocketChannel socketChannel = mock(SocketChannel.class);
            when(socketChannel.write(writeBuffer)).then(i -> {
                        writeBuffer.get(output);
                        return output.length;
            });

            SelectionKey key = mock(SelectionKey.class);
            when(key.channel()).thenReturn(socketChannel);
            when(key.attachment()).thenReturn(null);
            when(key.attach(packetBytesPart2)).then(i -> {
                when(key.attachment()).thenReturn(packetBytesPart2);
                return null;
            });

            socketWriter.writeToSocket(key, packetFull);

            assertTrue(Arrays.equals(packetBytesPart2, (byte[]) key.attachment()));
        }



        @DisplayName("when a key has a partial packet then write it to packet")
        @Test
        void writeToSocketTest4() throws IOException {

            byte[] output = new byte[packetBytesPart2.length + packetBytesFull.length];

            SocketChannel socketChannel = mock(SocketChannel.class);
            when(socketChannel.write(writeBuffer)).then(i -> {
                writeBuffer.get(output);
                return output.length;
            });

            SelectionKey key = mock(SelectionKey.class);
            when(key.channel()).thenReturn(socketChannel);
            when(key.attachment()).thenReturn(packetBytesPart2);

            socketWriter.writeToSocket(key, packetFull);

            byte[] outputPartialPacket = Arrays.copyOfRange(output, 0, packetBytesPart2.length);

            assertTrue(Arrays.equals(packetBytesPart2, outputPartialPacket));
        }
        */

        @DisplayName("when socketChannel write return -1 then cancel the key and close the channel")
        @Test
        void writeSocketTest4() throws IOException {

            SocketChannel socketChannel = mock(SocketChannel.class);
            when(socketChannel.write(writeBuffer)).then(i -> {
                writeBuffer.clear();
                return -1;
            });

            SelectionKey key = mock(SelectionKey.class);
            when(key.channel()).thenReturn(socketChannel);
            when(key.attachment()).thenReturn(null);

            socketWriter.writeToSocket(key, writeBuffer, packetFull);

            verify(key).cancel();
            verify(socketChannel).close();
        }
    }


}
