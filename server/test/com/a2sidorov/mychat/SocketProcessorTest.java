package com.a2sidorov.mychat;

import org.junit.jupiter.api.*;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testing SocketProcessor class")
class SocketProcessorTest {

    private static BlockingQueue<SocketChannel> socketQueue;
    private static BlockingQueue<String> inboundPacketQueue;
    private static BlockingQueue<String> outboundPacketQueue;
    private static Map<String, String> nicknames;
    private static SocketProcessor socketProcessor;


    @BeforeAll
    static void initAll() {
        socketQueue = new ArrayBlockingQueue<>(64);
        inboundPacketQueue = new ArrayBlockingQueue<>(64);
        outboundPacketQueue = new ArrayBlockingQueue<>(64);
        nicknames = new HashMap<>();

        socketProcessor = new SocketProcessor(socketQueue,
                inboundPacketQueue,
                outboundPacketQueue,
                nicknames);
    }

    @Nested
    @DisplayName("Testing parsePackets class")
    class parsePacketsTest {

        @DisplayName("when a message packet is in the inbound queue then add it to the outbound queue")
        @Test
        void parsePacketsTest1() {

            String mesagePacket = "m/Nickname: message";
            inboundPacketQueue.add(mesagePacket);
            socketProcessor.parsePackets();
            assertEquals(mesagePacket, outboundPacketQueue.poll());

        }

        @DisplayName("when a nickname packet is in the inbound queue then add notification and the nickname list to the outbound queue")
        @Test
        void parsePacketsTest2() {

            nicknames.put("127.0.0.1", "Unknown");
            String nicknamePacket = "n/127.0.0.1/nickname";
            inboundPacketQueue.add(nicknamePacket);
            socketProcessor.parsePackets();
            assertEquals("s/nickname has joined the chat.", outboundPacketQueue.poll());
            assertEquals("n/[nickname]", outboundPacketQueue.poll());

        }
    }


}
