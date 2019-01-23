package com.a2sidorov.mychat;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collector;
import java.util.stream.Collectors;

class PacketParser {

    private BlockingQueue<String> inboundPacketQueue;
    private BlockingQueue<String> outboundPacketQueue;
    private List<Client> clients;

    PacketParser(BlockingQueue<String> inboundPacketQueue, BlockingQueue<String> outboundPacketQueue,
                 List<Client> clients) {
        this.inboundPacketQueue = inboundPacketQueue;
        this.outboundPacketQueue = outboundPacketQueue;
        this.clients = clients;
    }

    void parse() {
        /*
        Inbound packet prefixes:
        m/ - user message
        n/ - nickname

        Outbound packet prefixes:
        s/ - server notofication
        n/ - nickname list
        */

        String inboundPacket = inboundPacketQueue.poll();

        while (inboundPacket != null) {
            String prefix = inboundPacket.substring(0, 2);

            if (prefix.equals("n/")) {
                int i = inboundPacket.lastIndexOf('/');
                String nickname = inboundPacket.substring(i + 1);

                for (Client c : clients) {
                    try {
                        if (inboundPacket.contains(c.getSocketChannel().getRemoteAddress().toString())) {
                            c.setNickname(nickname);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                List<String> nicknames = clients
                        .stream()
                        .map(client -> client.getNickname())
                        .collect(Collectors.toList());

                outboundPacketQueue.add("s/" + nickname + " has joined the chat.");
                outboundPacketQueue.add("n/" + nicknames);
            }

            if (prefix.equals("m/")) {
                outboundPacketQueue.add(inboundPacket);
            }
            inboundPacket = inboundPacketQueue.poll();
        }

    }
}
