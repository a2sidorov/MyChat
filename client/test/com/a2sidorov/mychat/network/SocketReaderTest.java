package com.a2sidorov.mychat.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

import com.a2sidorov.mychat.controller.MainController;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Testing SocketReader class")
class SocketReaderTest {

    private static SocketChannel socketChannelMocked;
    private static MainController mainControllerMocked;
    private static ByteBuffer readBuffer;

    private static SocketReader socketReader;


    @BeforeAll
    static void initAll() {
        socketChannelMocked = mock(SocketChannel.class);
        mainControllerMocked = mock(MainController.class);
        readBuffer = ByteBuffer.allocate(1024);

        socketReader = new SocketReader(socketChannelMocked, readBuffer, mainControllerMocked);
    }


    @Nested
    @DisplayName("Testing readFromSocket")
    class readFromSocketTest {

        @DisplayName("when the connection has been closed then notify the user")
        @Test
        void readFromSocketTest1() throws IOException {
            when(socketChannelMocked.read(readBuffer)).thenReturn(-1);
            socketReader.readFromSocket();
            verify(mainControllerMocked).updateTextArea("Server has closed the connection");
        }

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

        @DisplayName("when a full packet is read then call parsePacket with it")
        @Test
        void processFullPacketTest1() {
            SocketReader socketReaderSpied = spy(socketReader);

            String packet = "m/message";
            readBuffer.putShort((short) packet.getBytes().length);
            readBuffer.put(packet.getBytes());

            readBuffer.flip();

            socketReaderSpied.processFullPackets();
            readBuffer.clear();
            verify(socketReaderSpied).parsePacket(packet);
        }

        @DisplayName("when a partial packet is read then skip parsing until the packet is fully read")
        @Test
        void processFullPacketTest2() {
            SocketReader socketReaderSpied = spy(socketReader);

            String fullPacket = "m/message";
            String parialPacketPart1 = "m/mess";
            String parialPacketPart2 = "age";
            readBuffer.putShort((short) fullPacket.getBytes().length);
            readBuffer.put(parialPacketPart1.getBytes());
            readBuffer.flip();

            socketReaderSpied.processFullPackets();
            verify(socketReaderSpied, never()).parsePacket(anyString());


            readBuffer.put(parialPacketPart2.getBytes());
            readBuffer.flip();

            socketReaderSpied.processFullPackets();
            verify(socketReaderSpied).parsePacket(fullPacket);
        }

        @DisplayName("when the partial size  of a pcket is read then skip parsing until the packet is fully read")
        @Test
        void processFullPacketTest3() {
            SocketReader socketReaderSpied = spy(socketReader);

            String fullPacket = "m/message";
            readBuffer.put((byte) 0);
            readBuffer.flip();

            socketReaderSpied.processFullPackets();
            verify(socketReaderSpied, never()).parsePacket(anyString());


            readBuffer.put((byte) fullPacket.getBytes().length);
            readBuffer.flip();

            socketReaderSpied.processFullPackets();
            verify(socketReaderSpied, never()).parsePacket(fullPacket);

            readBuffer.put(fullPacket.getBytes());
            readBuffer.flip();

            socketReaderSpied.processFullPackets();
            verify(socketReaderSpied).parsePacket(fullPacket);
        }
    }

    @Nested
    @DisplayName("Testing parsePacket method")
    class parsePacketTest {

        @DisplayName("when packet is a message then update the text area")
        @Test
        void parsePacketTest1() {
            String packet = "m/user message";

            socketReader.parsePacket(packet);
            verify(mainControllerMocked).updateTextArea("user message");
        }

        @DisplayName("when packet is a server message then update the text area")
        @Test
        void parsePacketTest2() {
            String packet = "s/server message";

            socketReader.parsePacket(packet);
            verify(mainControllerMocked).updateTextArea("server message");
        }

        @DisplayName("when packet is a nickname list then update the user list")
        @Test
        void parsePacketTest3() {
            String packet = "n/[nickname1,nickname2]";
            String[] nicknames = packet.substring(3, packet.length() - 1).split(",");

            socketReader.parsePacket(packet);
            verify(mainControllerMocked).updateListNicknames(nicknames);
        }
    }

    @AfterEach
    void tearDown() {
        readBuffer.clear();
    }
}
