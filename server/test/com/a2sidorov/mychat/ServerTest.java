package com.a2sidorov.mychat;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.*;

class ServerTest {

    static Server server;
    static ByteBuffer buffer;
    static SocketChannel clientChannel;

    @BeforeAll
    static void init() throws IOException {
        server = Server.getInstance("localhost", 1050);
        server.start();
        buffer = ByteBuffer.allocate(1024);
    }

    @DisplayName("Server should accept a connection request")
    @Test
    void testingConnection() {
        try {
            clientChannel = SocketChannel.open(new InetSocketAddress("localhost", 1050));
            assertTrue(clientChannel.isConnected());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }








}
