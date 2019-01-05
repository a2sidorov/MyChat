package com.a2sidorov.mychat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Testing SocketProcessor class")
public class SocketProcessorTest {


    static private SocketProcessor socketProcessor;
    static private ByteBuffer byteBuffer;
    static private BlockingQueue<String> messageQueue;

    @BeforeAll
    static void init() throws IOException {
        BlockingQueue<SocketChannel> socketQueue = new ArrayBlockingQueue<>(1024);
        socketProcessor= new SocketProcessor(socketQueue);
        byteBuffer = ByteBuffer.allocate(1024);
        messageQueue = new ArrayBlockingQueue<String>(1024);
    }


    @Nested
    @DisplayName("Testing processReadBuffer method")
    class processReadBuffer {

        @DisplayName("Case: one full message")
        @Test
        void processReadBufferTest1() throws IOException {
            String packet = "m/message";
            byte[] packetBytes = packet.getBytes();
            byteBuffer.putShort((short) packetBytes.length);
            byteBuffer.put(packetBytes);

            byteBuffer.flip();
            socketProcessor.processReadBuffer(byteBuffer, messageQueue);
            assertEquals(packet, messageQueue.poll());
        }

        @DisplayName("Case: two full messages")
        @Test
        void processReadBufferTest2() {
            String packet1 = "m/message1";
            byte[] packet1Bytes = packet1.getBytes();
            byteBuffer.putShort((short) packet1Bytes.length);
            byteBuffer.put(packet1Bytes);

            String packet2 = "m/message2";
            byte[] packet2Bytes = packet2.getBytes();
            byteBuffer.putShort((short) packet2Bytes.length);
            byteBuffer.put(packet2Bytes);

            byteBuffer.flip();

            assertEquals(packet1, messageQueue.poll());
            assertEquals(packet2, messageQueue.poll());
        }

        @DisplayName("Case: partial read (one and a half message)")
        @Test
        void processReadBufferTest3() {
            String packet1 = "m/message1";
            byte[] packet1Bytes = packet1.getBytes();
            byteBuffer.putShort((short) packet1Bytes.length);
            byteBuffer.put(packet1Bytes);

            String packet2 = "m/message2";
            byte[] bytes2 = packet2.getBytes();
            byte[] firstPart = "m/me".getBytes();
            byteBuffer.putShort((short) bytes2.length);
            byteBuffer.put(firstPart);

            byteBuffer.flip();
            socketProcessor.processReadBuffer(byteBuffer, messageQueue); //processing the first part
            assertEquals(packet1, messageQueue.poll());
            assertEquals(null, messageQueue.poll());

            byte[] secondPart = "ssage2".getBytes();
            byteBuffer.put(secondPart);

            byteBuffer.flip();
            socketProcessor.processReadBuffer(byteBuffer, messageQueue); //processing the second part
            assertEquals("m/message2", messageQueue.poll());
        }

        @DisplayName("Case: partial read (one byte)")
        @Test
        void processReadBufferTest4() {

            byteBuffer.put((byte) 0);

            byteBuffer.flip();
            socketProcessor.processReadBuffer(byteBuffer, messageQueue);
            assertEquals(null, messageQueue.poll());

            String packet = "m/message";
            byte[] bytes = packet.getBytes();
            byteBuffer.put((byte) bytes.length);
            byteBuffer.put(bytes);

            byteBuffer.flip();
            socketProcessor.processReadBuffer(byteBuffer, messageQueue); //processing the second part
            assertEquals(packet, messageQueue.poll());

        }

        @DisplayName("Case: partial read (two bytes)")
        @Test
        void processReadBufferTest5() {
            String packet = "m/message";
            byte[] bytes = packet.getBytes();
            byteBuffer.putShort((short) bytes.length);

            byteBuffer.flip();
            socketProcessor.processReadBuffer(byteBuffer, messageQueue); //processing the first part
            assertEquals(null, messageQueue.poll());

            byteBuffer.put(bytes);

            byteBuffer.flip();
            socketProcessor.processReadBuffer(byteBuffer, messageQueue); //processing the second part
            assertEquals(packet, messageQueue.poll());

        }

        @DisplayName("Case: partial read (one messsage and one byte)")
        @Test
        void processReadBufferTest6() {

            String packet1 = "m/message1";
            byte[] bytes1 = packet1.getBytes();
            byteBuffer.putShort((short) bytes1.length);
            byteBuffer.put(bytes1);

            byteBuffer.put((byte) 0);

            byteBuffer.flip();
            socketProcessor.processReadBuffer(byteBuffer, messageQueue);
            assertEquals(packet1, messageQueue.poll());
            assertEquals(null, messageQueue.poll());


            String packet2 = "m/message2";
            byte[] bytes2 = packet2.getBytes();
            byteBuffer.put((byte) bytes2.length);
            byteBuffer.put(bytes2);

            byteBuffer.flip();
            socketProcessor.processReadBuffer(byteBuffer, messageQueue); //processing the second part
            assertEquals("m/message2", messageQueue.poll());

        }

        @DisplayName("Case: partial read (one messsage and two byte)")
        @Test
        void processReadBufferTest7() {

            String packet1 = "m/message1";
            byte[] bytes1 = packet1.getBytes();
            byteBuffer.putShort((short) bytes1.length);
            byteBuffer.put(bytes1);

            String packet2 = "m/message2";
            byte[] bytes2 = packet2.getBytes();
            byteBuffer.putShort((short) bytes2.length);

            byteBuffer.flip();
            socketProcessor.processReadBuffer(byteBuffer, messageQueue); //processing the first part
            assertEquals(packet1, messageQueue.poll());
            assertEquals(null, messageQueue.poll());

            byteBuffer.put(bytes2);

            byteBuffer.flip();
            socketProcessor.processReadBuffer(byteBuffer, messageQueue); //processing the second part
            assertEquals(packet2, messageQueue.poll());

        }

        @DisplayName("Case: partial read (two messages - 1 byte)")
        @Test
        void processReadBufferTest8() {

            String packet1 = "m/message1";
            byte[] bytes1 = packet1.getBytes();
            byteBuffer.putShort((short) bytes1.length);
            byteBuffer.put(bytes1);

            String packet2 = "m/message2";
            byte[] packet2Bytes = packet2.getBytes();
            byte[] packet2BytesPart1 = Arrays.copyOfRange(packet2Bytes, 0, packet2Bytes.length - 1);
            byte[] packet2BytesPart2 = Arrays.copyOfRange(packet2Bytes, packet2Bytes.length - 1, packet2Bytes.length);

            byteBuffer.putShort((short) packet2Bytes.length);
            byteBuffer.put(packet2BytesPart1);

            byteBuffer.flip();
            socketProcessor.processReadBuffer(byteBuffer, messageQueue); //processing the first part
            assertEquals(packet1, messageQueue.poll());
            assertEquals(null, messageQueue.poll());

            byteBuffer.put(packet2BytesPart2);

            byteBuffer.flip();
            socketProcessor.processReadBuffer(byteBuffer, messageQueue); //processing the second part
            assertEquals(packet2, messageQueue.poll());

        }
    }
}
