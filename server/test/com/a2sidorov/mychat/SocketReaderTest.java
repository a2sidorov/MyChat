package com.a2sidorov.mychat;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@DisplayName("Testing SocketReader class")
class SocketReaderTest {

    private static BlockingQueue<String> inboundPacketQueue;
    private static BlockingQueue<String> outboundPacketQueue;
    private static List<Client> clients;

    private static ByteBuffer readBuffer;
    private static SocketReader socketReader;

    private static String packetFull;
    private static byte[] packetBytesFull;
    private static byte[] packetBytesPart1;
    private static byte[] packetBytesPart2;

    @BeforeAll
    static void initAll() {
        inboundPacketQueue = new ArrayBlockingQueue<>(256);
        socketReader = new SocketReader(inboundPacketQueue);
        readBuffer = ByteBuffer.allocate(1024);

        packetFull = "m/message";
        packetBytesFull = new byte[2 + packetFull.getBytes().length];
        packetBytesFull[1] = (byte) packetFull.getBytes().length;
        System.arraycopy(packetFull.getBytes(),0, packetBytesFull,2, packetFull.getBytes().length);

        packetBytesPart1 = Arrays.copyOfRange(packetBytesFull, 0, 6);
        packetBytesPart2 = Arrays.copyOfRange(packetBytesFull, 6, packetBytesFull.length);
    }

    @Nested
    @DisplayName("Testing readFromSocket method")
    class readFromSocketTest {

        @DisplayName("when a packet is partially read then attach the read part to the key")
        @Test
        void readFromSocketTest1() throws IOException {

            SocketChannel socketChannel = mock(SocketChannel.class);
            when(socketChannel.read(readBuffer)).then(i -> {
                readBuffer.put(packetBytesPart1);
                return packetBytesPart1.length;
            });

            SelectionKey key = spy(SelectionKey.class);
            when(key.channel()).thenReturn(socketChannel);
            when(key.attachment()).thenReturn(null);
            when(key.attach(packetBytesPart1)).then(i -> {
                when(key.attachment()).thenReturn(packetBytesPart1);
                return null;
            });

            socketReader.readFromSocket(key, readBuffer);

            assertTrue(Arrays.equals(packetBytesPart1, (byte[]) key.attachment()));
        }

        @DisplayName("when a key has a partial packet then add it to the buffer and read the rest from socket")
        @Test
        void readFromSocketTest2() throws IOException {

            SocketChannel socketChannel = mock(SocketChannel.class);
            when(socketChannel.read(readBuffer)).then(i -> {
                readBuffer.put(packetBytesPart2);
                return packetBytesPart2.length;
            });

            SelectionKey key = spy(SelectionKey.class);
            when(key.channel()).thenReturn(socketChannel);
            when(key.attachment()).thenReturn(packetBytesPart1);

            socketReader.readFromSocket(key, readBuffer);

            assertEquals(packetFull, inboundPacketQueue.poll());
        }

        @DisplayName("when socketChannel read return -1 then cancel the key and close the channel")
        @Test
        void readFromSocketTest3() throws IOException {

            SocketChannel socketChannel = mock(SocketChannel.class);
            when(socketChannel.read(readBuffer)).thenReturn(-1);

            SelectionKey key = mock(SelectionKey.class);
            when(key.channel()).thenReturn(socketChannel);
            when(key.attachment()).thenReturn(null);

            socketReader.readFromSocket(key, readBuffer);

            verify(key).cancel();
            verify(socketChannel).close();
        }
    }

    @Nested
    @DisplayName("Testing readFullPackets method")
    class readFullPacketsTest {
        @DisplayName("when one full packet then add it to the queue")
        @Test
        void readFullPacketsTest1() {
            String packet = "m/message";
            byte[] packetBytes = packet.getBytes();
            readBuffer.putShort((short) packetBytes.length);
            readBuffer.put(packetBytes);

            readBuffer.flip();
            socketReader.readFullPackets(readBuffer);
            readBuffer.clear();
            assertEquals(packet, inboundPacketQueue.poll());
        }


        @DisplayName("when two full packets then add them to the queue")
        @Test
        void readFullPacketsTest2() {
            String packet1 = "m/message1";
            byte[] packet1Bytes = packet1.getBytes();
            readBuffer.putShort((short) packet1Bytes.length);
            readBuffer.put(packet1Bytes);

            String packet2 = "m/message2";
            byte[] packet2Bytes = packet2.getBytes();
            readBuffer.putShort((short) packet2Bytes.length);
            readBuffer.put(packet2Bytes);

            readBuffer.flip();
            socketReader.readFullPackets(readBuffer);
            readBuffer.clear();
            assertEquals(packet1, inboundPacketQueue.poll());
            assertEquals(packet2, inboundPacketQueue.poll());
        }

        @DisplayName("when one and a half packets then add one to the queue")
        @Test
        void readFullPacketsTest3() {
            String packet1 = "m/message1";
            byte[] packet1Bytes = packet1.getBytes();
            readBuffer.putShort((short) packet1Bytes.length);
            readBuffer.put(packet1Bytes);

            String packet2 = "m/message2";
            byte[] bytes2 = packet2.getBytes();
            byte[] firstPart = "m/me".getBytes();
            readBuffer.putShort((short) bytes2.length);
            readBuffer.put(firstPart);

            readBuffer.flip();
            socketReader.readFullPackets(readBuffer); //processing the first part
            readBuffer.clear();
            assertEquals(packet1, inboundPacketQueue.poll());
            assertNull(inboundPacketQueue.poll());
        }

        @DisplayName("when the partial size of a packet then add nothing to the queue")
        @Test
        void readFullPacketsTest4() {

            readBuffer.put((byte) 0);

            readBuffer.flip();
            socketReader.readFullPackets(readBuffer);
            readBuffer.clear();
            assertNull(inboundPacketQueue.poll());
        }

        @DisplayName("when a partial packet then add nothing to the queue")
        @Test
        void readFullPacketsTest5() {
            String packet = "m/message";
            byte[] bytes = packet.getBytes();
            readBuffer.putShort((short) bytes.length);

            readBuffer.flip();
            socketReader.readFullPackets(readBuffer); //processing the first part
            readBuffer.clear();
            assertNull(inboundPacketQueue.poll());
        }

        @DisplayName("when a packet and one byte of the size of the next packet then add one to the queue")
        @Test
        void readFullPacketsTest6() {

            String packet1 = "m/message1";
            byte[] bytes1 = packet1.getBytes();
            readBuffer.putShort((short) bytes1.length);
            readBuffer.put(bytes1);

            readBuffer.put((byte) 0);

            readBuffer.flip();
            socketReader.readFullPackets(readBuffer);
            readBuffer.clear();
            assertEquals(packet1, inboundPacketQueue.poll());
            assertNull(inboundPacketQueue.poll());
        }

        @DisplayName("when a packet and two bytes of the size of the next packet then add one to the queue")
        @Test
        void readFullPacketsTest7() {

            String packet1 = "m/message1";
            byte[] bytes1 = packet1.getBytes();
            readBuffer.putShort((short) bytes1.length);
            readBuffer.put(bytes1);

            String packet2 = "m/message2";
            byte[] bytes2 = packet2.getBytes();
            readBuffer.putShort((short) bytes2.length);

            readBuffer.flip();
            socketReader.readFullPackets(readBuffer); //processing the first part
            readBuffer.clear();
            assertEquals(packet1, inboundPacketQueue.poll());
            assertNull(inboundPacketQueue.poll());
        }

        @DisplayName("when a packet missing last byte then add nothing to the queue")
        @Test
        void readFullPacketsTest8() {

            String packet1 = "m/message1";
            byte[] packet1Bytes = packet1.getBytes();
            readBuffer.putShort((short) packet1Bytes.length);
            readBuffer.put(packet1Bytes);

            String packet2 = "m/message2";
            byte[] packet2Bytes = packet2.getBytes();
            byte[] packet2BytesPart1 = Arrays.copyOfRange(packet2Bytes, 0, packet2Bytes.length - 1);
            byte[] packet2BytesPart2 = Arrays.copyOfRange(packet2Bytes, packet2Bytes.length - 1, packet2Bytes.length);

            readBuffer.putShort((short) packet2Bytes.length);
            readBuffer.put(packet2BytesPart1);

            readBuffer.flip();
            socketReader.readFullPackets(readBuffer); //processing the first part
            readBuffer.clear();

            assertEquals(packet1, inboundPacketQueue.poll());
            assertNull(inboundPacketQueue.poll());
        }
    }
}
