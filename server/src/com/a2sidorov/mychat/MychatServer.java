package com.a2sidorov.mychat;


import com.a2sidorov.mychat.network.NetworkServer;

public class MychatServer {

    public static void main(String[] args) {

        NetworkServer.getInstance(1050).startServer();

    }

}
