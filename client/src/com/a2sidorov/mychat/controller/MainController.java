package com.a2sidorov.mychat.controller;

import com.a2sidorov.mychat.model.Settings;
import com.a2sidorov.mychat.network.NetworkClient;
import com.a2sidorov.mychat.view.MainView;
import com.a2sidorov.mychat.view.Notification;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;

public class MainController {

    private Settings settings;
    private MainView mainView;
    private NetworkClient networkClient;
    private IntroController introController;

    public MainController(Settings settings, MainView mainView, NetworkClient networkClient) {
        this.settings = settings;
        this.mainView = mainView;
        this.networkClient = networkClient;
    }

    public void display() {
        this.mainView.display();
    }

    public void initController() {
        mainView.getButtonSend().addActionListener(e -> {
           this.networkClient.sendMessage(this.settings.getNickname() + ": " + this.mainView.getTextField().getText());
           this.mainView.getTextField().setText("");
        });

        new Thread(() -> {
            BlockingQueue<String> inboundPacketQueue = this.networkClient.getInboundPacketQueue();
            while(true) {
                System.out.println("parse packet loop");
                try {
                    String packet = inboundPacketQueue.take();
                    parsePacket(packet);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    void parsePacket(String packet) {
        /*
        Inbound packet prefixes:
        m/ - user message (eg. "m/Nickname: message")
        s/ - server notification (eg. "s/Nickname has joined the chat.")
        n/ - nickname list (eg. "n/[nickname1, nickname2]")
        c/ - server closed the connection
        */

        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
        String localDateTimeString = localDateTime.format(formatter);

        String prefix = packet.substring(0, 2);

        if (prefix.equals("m/")) {

            this.mainView.getTextArea().append("(" + localDateTimeString + ") " + packet.substring(2)
                    + System.lineSeparator());
        }

        if (prefix.equals("s/")) {
            this.mainView.getTextArea().append("(" + localDateTimeString + ") " + packet.substring(2)
                    + System.lineSeparator());
            //TODO change font or color
        }

        if (prefix.equals("n/")) {
            String[] nicknames = packet.substring(2, packet.length() - 1)
                    .replace('[', ' ')
                    .split(",");
            this.mainView.getListNicknames().setListData(nicknames);
        }

        if (prefix.equals("c/")) {
            this.introController.initView();
            this.settings.getNotification().error("Server has closed the connection unexpectedly");
        }

    }

    public void setIntroController(IntroController introController) {
        this.introController = introController;
    }

}
