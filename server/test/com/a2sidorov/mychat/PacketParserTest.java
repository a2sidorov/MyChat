package com.a2sidorov.mychat;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Testing PacketParser class")
class PacketParserTest {

    private static BlockingQueue<String> inboundPacketQueue;
    private static BlockingQueue<String> outboundPacketQueue;
    private static List<Client> clients;
    private static PacketParser packetParser;

    @BeforeAll
    static void init() {
        inboundPacketQueue = new ArrayBlockingQueue<>(64);
        outboundPacketQueue = new ArrayBlockingQueue<>(64);
        clients = new LinkedList<>();
        packetParser = new PacketParser(inboundPacketQueue, outboundPacketQueue, clients);
    }

    @DisplayName("when an inbound packet is null then add nothing to the outbound queue")
    @Test
    void parseTest1() {
        packetParser.parse();
        assertNull(outboundPacketQueue.poll());
    }

    @DisplayName("when an inbound packet has n/ prefix then add nickname to the list and add the list to outbound queue")
    @Test
    void parseTest2() throws IOException {

        SocketAddress socketAddress = mock(SocketAddress.class);
        when(socketAddress.toString()).thenReturn("address");

        SocketChannel socketChannel = mock(SocketChannel.class);
        when(socketChannel.getRemoteAddress()).thenReturn(socketAddress);

        clients.add(new Client(socketChannel));

        String inboundPacket = "n/address/nickname";
        inboundPacketQueue.add(inboundPacket);
        packetParser.parse();
        assertEquals("s/nickname has joined the chat.", outboundPacketQueue.poll());
        assertEquals("n/[nickname]", outboundPacketQueue.poll());
    }

    @DisplayName("when an inbound packet has m/ prefix then add it to the outbound queue")
    @Test
    void parseTest3() {
        String inboundPacket = "m/message";
        inboundPacketQueue.add(inboundPacket);
        packetParser.parse();
        assertEquals(inboundPacket, outboundPacketQueue.poll());
    }

}
