package com.a2sidorov.mychat;



import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;


@DisplayName("Testing MyChatServer class")
class MainTest {

    private static MyChatServer server;

    private static SocketChannel clientSocketChannel;
    private static ByteBuffer clientReadBuffer;
    private static ByteBuffer clientWriteBuffer;

    @BeforeAll
    static void initAll() throws IOException, InterruptedException {

        Properties properties = new Properties();
        properties.put("address", "127.0.0.1");
        properties.put("port", "1050");

        server = MyChatServer.getInstance(properties);
        server.start();

        clientReadBuffer = ByteBuffer.allocate(1024);
        clientWriteBuffer = ByteBuffer.allocate(1024);
        clientSocketChannel = SocketChannel.open(new InetSocketAddress("localhost", 1050));
        Thread.sleep(100);
    }

    @DisplayName("when a client disconnects abruptly then the server sends notification")
    @Test
    void mainTest1() throws IOException {

        String packet = "n/" + clientSocketChannel.getLocalAddress() + "/nickname";
        clientWriteBuffer.putShort((short) packet.getBytes().length);
        clientWriteBuffer.put(packet.getBytes());

        clientWriteBuffer.flip();

        while(clientWriteBuffer.hasRemaining()) {
            clientSocketChannel.write(clientWriteBuffer);
        }
        clientWriteBuffer.clear();

        while (clientReadBuffer.position() != 47) {
            clientSocketChannel.read(clientReadBuffer);
            clientSocketChannel.read(clientReadBuffer);
        }

        clientReadBuffer.flip();

        short packetSize1 = clientReadBuffer.getShort();
        byte[] input1 = new byte[packetSize1];
        clientReadBuffer.get(input1);


        short packetSize2 = clientReadBuffer.getShort();
        byte[] input2 = new byte[packetSize2];
        clientReadBuffer.get(input2);

        clientReadBuffer.clear();

        assertEquals("s/nickname has joined the chat.", new String(input1));
        assertEquals("n/[nickname]", new String(input2));
    }

    @AfterAll
    static void tearDownAll() throws IOException {
        clientSocketChannel.close();
    }

}
