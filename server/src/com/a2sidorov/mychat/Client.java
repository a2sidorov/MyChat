package com.a2sidorov.mychat;

import java.nio.channels.SocketChannel;

public class Client {

    private String nickname = "Unknown";
    private SocketChannel socketChannel;

    Client(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    String getNickname() {
        return nickname;
    }

    void setNickname(String nickname) {
        this.nickname = nickname;
    }

    SocketChannel getSocketChannel() {
        return socketChannel;
    }

    void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }
}
