package com.a2sidorov.mychat.network;

import com.a2sidorov.mychat.controller.MainController;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.mockito.Mockito.*;

@DisplayName("Testing SocketWriter class")
class SocketWriterTest {

    private static BlockingQueue<String> outboundPacketQueue;
    private static ByteBuffer writeBuffer;
    private static SocketChannel socketChannelMocked;
    private static MainController mainControllerMocked;

    private static SocketWriter socketWriter;

    @BeforeAll
    static void initAll() {
        socketChannelMocked = mock(SocketChannel.class);
        writeBuffer = ByteBuffer.allocate(1024);
        outboundPacketQueue = new ArrayBlockingQueue<>(64);
        mainControllerMocked = mock(MainController.class);

        socketWriter = new SocketWriter(
                socketChannelMocked,
                writeBuffer,
                outboundPacketQueue,
                mainControllerMocked);
    }

    @Nested
    @DisplayName("Testing writeToSocket method")
    class writeToSocketTest {

        @DisplayName("when a packet is in the packet queue then write it to the socket")
        @Test
        void writeToSocketTest1() throws IOException {
            outboundPacketQueue.add("m/message");

            when(socketChannelMocked.write(writeBuffer)).then(i -> {
                short packetSize = writeBuffer.getShort();
                byte[] packetBytes = new byte[packetSize];
                writeBuffer.get(packetBytes);
                return packetSize + 2;
            });

            socketWriter.writeToSocket();
            verify(socketChannelMocked).write(writeBuffer);
        }

        @DisplayName("when the server closed the connection then inform the user")
        @Test
        void writeToSocketTest2() throws IOException {
            outboundPacketQueue.add("m/message");
            when(socketChannelMocked.write(writeBuffer)).thenReturn(-1);

            socketWriter.writeToSocket();
            verify(mainControllerMocked).updateTextArea("Server has closed the connection");


        }
    }


}
