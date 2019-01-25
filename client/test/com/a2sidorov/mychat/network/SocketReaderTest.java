package com.a2sidorov.mychat.network;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

import com.a2sidorov.mychat.controller.MainController;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Testing SocketReader class")
class SocketReaderTest {

    private static SocketReader socketReader;
    private static MainController mainController;
    private static ByteBuffer readBuffer;

    @BeforeAll
    static void initAll() {
        SocketChannel socketChannel = mock(SocketChannel.class);
        mainController = mock(MainController.class);
        socketReader = new SocketReader(socketChannel, mainController);
        readBuffer = ByteBuffer.allocate(1024);
    }

    @Nested
    @DisplayName("Testing readFullPackets")
    class readFullPacketsTest {

        @DisplayName("when a full packet is read then call parsePacket with it")
        @Test
        void readFullPacketTest1() {
            SocketReader socketReaderSpied = spy(socketReader);

            String packet = "m/message";
            readBuffer.putShort((short) packet.getBytes().length);
            readBuffer.put(packet.getBytes());

            readBuffer.flip();

            socketReaderSpied.readFullPackets(readBuffer);
            verify(socketReaderSpied).parsePacket(packet);
        }

        @DisplayName("when a partial packet is read then skip parsing")
        @Test
        void readFullPacketTest2() {
            SocketReader socketReaderSpied = spy(socketReader);

            String fullPacket = "m/message";
            String parialPacket = "m/mess";
            readBuffer.putShort((short) fullPacket.getBytes().length);
            readBuffer.put(parialPacket.getBytes());

            readBuffer.flip();

            socketReaderSpied.readFullPackets(readBuffer);
            verify(socketReaderSpied, never()).parsePacket(parialPacket);
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
            verify(mainController).updateTextArea("user message");
        }

        @DisplayName("when packet is a server message then update the text area")
        @Test
        void parsePacketTest2() {
            String packet = "s/server message";

            socketReader.parsePacket(packet);
            verify(mainController).updateTextArea("server message");
        }

        @DisplayName("when packet is a nickname list then update the user list")
        @Test
        void parsePacketTest3() {
            String packet = "n/[nickname1,nickname2]";
            String[] nicknames = packet.substring(3, packet.length() - 1).split(",");

            socketReader.parsePacket(packet);
            verify(mainController).updateListNicknames(nicknames);
        }
    }
}
