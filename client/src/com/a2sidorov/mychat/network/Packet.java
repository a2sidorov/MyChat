package com.a2sidorov.mychat.network;

import java.util.ArrayList;

public class Packet {

    private StringBuffer packet;

    // Message packet constructor
    public Packet(String type, String sender, String message) {
        this.packet = new StringBuffer();
        this.packet.append(type).append(";");
        this.packet.append(sender).append(";");
        this.packet.append(message);
    }

    // User list packet constructor
    public Packet(ArrayList<String> list) {
        this.packet.append("NICKNAMES;");
        for(String s : list) {
            this.packet.append(s);
            this.packet.append(";");
        }
    }

    public String toString() {
        return packet.toString();
    }

}
