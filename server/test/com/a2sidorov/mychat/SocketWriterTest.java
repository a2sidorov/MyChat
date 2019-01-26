package com.a2sidorov.mychat;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@DisplayName("Testing SocketWriter class")
class SocketWriterTest {

    private static BlockingQueue<String> outboundPacketQueue;
    private static ByteBuffer writeBuffer;
    private static Map<String, String> nicknames;

    private static SocketWriter socketWriter;

    private static String packetFull;
    private static byte[] packetBytesFull;
    private static byte[] packetBytesPart1;
    private static byte[] packetBytesPart2;

    @BeforeAll
    static void initAll() {
        outboundPacketQueue = new ArrayBlockingQueue<>(64);
        nicknames = new HashMap<>();
        writeBuffer = ByteBuffer.allocate(1024);

        socketWriter = new SocketWriter(outboundPacketQueue, writeBuffer, nicknames);

    }

    @Nested
    @DisplayName("Testing writeToSocket method")
    class writeToSocketTest {

        @DisplayName("when the method is called then write the passed packet to the socket channel")
        @Test
        void writeToSocketTest1() throws IOException {

            SocketChannel socketChannelMocked = mock(SocketChannel.class);
            when(socketChannelMocked.write(writeBuffer)).then(i -> {
                short packetSize = writeBuffer.getShort();
                byte[] packetBytes = new byte[packetSize];
                writeBuffer.get(packetBytes);
                return packetSize + 2;
            });

            SelectionKey key = mock(SelectionKey.class);
            when(key.channel()).thenReturn(socketChannelMocked);
            when(key.attachment()).thenReturn(null);

            socketWriter.writeToSocket(key, "m/Nickname: message");

            verify(socketChannelMocked).write(writeBuffer);
        }

        @DisplayName("when a client is disconnected then queue a notification")
        @Test
        void writeToSocketTest2() throws IOException {

            SocketAddress socketAddressMockaed = mock(SocketAddress.class);
            when(socketAddressMockaed.toString()).thenReturn("clientAddress");

            SocketChannel socketChannelMocked = mock(SocketChannel.class);
            when(socketChannelMocked.write(writeBuffer)).thenReturn(-1);
            when(socketChannelMocked.getRemoteAddress()).thenReturn(socketAddressMockaed);

            nicknames.put(socketChannelMocked.getRemoteAddress().toString(), "Unknown");
            System.out.println(nicknames);

            SelectionKey key = mock(SelectionKey.class);
            when(key.channel()).thenReturn(socketChannelMocked);
            when(key.attachment()).thenReturn(null);

            socketWriter.writeToSocket(key, "m/Nickname: message");

            assertEquals("s/Unknown has left the chat.", outboundPacketQueue.poll());
        }
    }


}
