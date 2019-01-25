package com.a2sidorov.mychat.network;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SocketReader {

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
