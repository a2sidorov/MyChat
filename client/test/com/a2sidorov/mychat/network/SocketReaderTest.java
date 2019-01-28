package com.a2sidorov.mychat.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.a2sidorov.mychat.controller.MainController;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Testing SocketReader class")
class SocketReaderTest {

    private static SocketChannel socketChannelMocked;
    private static MainController mainControllerMocked;
    private static BlockingQueue<String> inboundPacketQueue;
    private static BlockingQueue<String> outboundPacketQueue;
    private static ByteBuffer readBuffer;
    private static SocketReader socketReader;

    @BeforeAll
    static void initAll() {
        socketChannelMocked = mock(SocketChannel.class);
        mainControllerMocked = mock(MainController.class);
        inboundPacketQueue = new ArrayBlockingQueue<>(64);
        outboundPacketQueue = new ArrayBlockingQueue<>(64);
        readBuffer = ByteBuffer.allocate(1024);
        socketReader = new SocketReader(socketChannelMocked, readBuffer, inboundPacketQueue);
    }

    @Nested
    @DisplayName("Testing readFromSocket")
    class readFromSocketTest {

        @DisplayName("when bytes are read then process full packets")
        @Test
        void readFromSocketTest2() throws IOException {
            SocketReader socketReaderSpied = spy(socketReader);
            when(socketChannelMocked.read(readBuffer)).thenReturn(5);
            socketReaderSpied.readFromSocket();
            verify(socketReaderSpied).processFullPackets();
        }
    }

    @Nested
    @DisplayName("Testing processFullPackets method")
    class processFullPacketsTest {

        @DisplayName("when a full packet is read then clear the buffer")
        @Test
        void processFullPacketTest1() {
            String packet = "m/Nickname: message";
            readBuffer.putShort((short) packet.getBytes().length);
            readBuffer.put(packet.getBytes());

            readBuffer.flip();

            socketReader.processFullPackets();
            assertEquals(0, readBuffer.position());
            assertEquals(1024, readBuffer.limit());
        }

        @DisplayName("when a full packet is read then add it to the inbound queue")
        @Test
        void processFullPacketTest2() {
            String packet = "m/Nickname: message";
            readBuffer.putShort((short) packet.getBytes().length);
            readBuffer.put(packet.getBytes());

            readBuffer.flip();

            socketReader.processFullPackets();
            assertEquals(packet, inboundPacketQueue.poll());
        }

        @DisplayName("when the partial size of a packet is read then leave it in buffer until the rest is read")
        @Test
        void processFullPacketTest3() {
            readBuffer.put((byte) 0);
            readBuffer.flip();

            socketReader.processFullPackets();
            assertEquals(1, readBuffer.position());
            assertEquals(1024, readBuffer.limit());
        }

        @DisplayName("when the partial size of a packet is read then don't add it to the packet queue")
        @Test
        void processFullPacketTest4() {
            readBuffer.put((byte) 0);
            readBuffer.flip();

            socketReader.processFullPackets();
            assertEquals(null, inboundPacketQueue.poll());
        }

        @DisplayName("when a partial packet is read then leave bytes in buffer until a full packet arrives")
        @Test
        void processFullPacketTest5() {
            String fullPacket = "m/Nickname: message";
            String parialPacketPart1 = "m/Nickname: mess";

            readBuffer.putShort((short) fullPacket.getBytes().length);
            readBuffer.put(parialPacketPart1.getBytes());
            readBuffer.flip();

            socketReader.processFullPackets();
            assertEquals(2 + parialPacketPart1.getBytes().length, readBuffer.position());
            assertEquals(1024, readBuffer.limit());
        }

        @DisplayName("when a partial packet is read then leave bytes in buffer don't add it to inbound packet queue")
        @Test
        void processFullPacketTest6() {
            String fullPacket = "m/Nickname: message";
            String parialPacketPart1 = "m/Nickname: mess";

            readBuffer.putShort((short) fullPacket.getBytes().length);
            readBuffer.put(parialPacketPart1.getBytes());
            readBuffer.flip();

            socketReader.processFullPackets();
            assertEquals(null, inboundPacketQueue.poll());
        }

        @DisplayName("when one and a parial size is read then process full one and leave the partial size in the buffer")
        @Test
        void processFullPacketTest7() {
            String fullPacket = "m/Nickname: message";
            readBuffer.putShort((short) fullPacket.getBytes().length);
            readBuffer.put(fullPacket.getBytes());
            readBuffer.put((byte) 0);
            readBuffer.flip();

            socketReader.processFullPackets();
            assertEquals(1, readBuffer.position());
            assertEquals(1024, readBuffer.limit());
        }

        @DisplayName("when one and a parial size is read then process full one and leave the partial size in the buffer")
        @Test
        void processFullPacketTest8() {
            SocketReader socketReaderSpied = spy(socketReader);

            String fullPacket = "m/Nickname: message";
            readBuffer.putShort((short) fullPacket.getBytes().length);
            readBuffer.put(fullPacket.getBytes());
            readBuffer.put((byte) 0);
            readBuffer.flip();

            socketReaderSpied.processFullPackets();
            assertEquals(fullPacket, inboundPacketQueue.poll());

        }

        @DisplayName("when one and a partial packet is read then process full one and leave the partial packet in the buffer")
        @Test
        void processFullPacketTest9() {
            String fullPacket = "m/Nickname: message";
            String parialPacketPart1 = "m/Nickname: mess";
            readBuffer.putShort((short) fullPacket.getBytes().length);
            readBuffer.put(fullPacket.getBytes());
            readBuffer.putShort((short) fullPacket.getBytes().length);
            readBuffer.put(parialPacketPart1.getBytes());
            readBuffer.flip();

            socketReader.processFullPackets();
            assertEquals(2 + parialPacketPart1.getBytes().length, readBuffer.position());
            assertEquals(1024, readBuffer.limit());
        }

    }

    @AfterEach
    void tearDown() {
        readBuffer.clear();
        inboundPacketQueue.clear();
    }
}
