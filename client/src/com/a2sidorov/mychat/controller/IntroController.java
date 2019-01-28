package com.a2sidorov.mychat.controller;

import com.a2sidorov.mychat.model.Settings;
import com.a2sidorov.mychat.network.NetworkClient;
import com.a2sidorov.mychat.view.IntroView;

import java.io.IOException;

public class IntroController {

    private Settings settings;
    private IntroView introView;
    private NetworkClient networkClient;
    private MainController mainController;

    public IntroController(Settings settings, IntroView introView, NetworkClient networkClient) {
        this.settings = settings;
        this.introView = introView;
        this.networkClient = networkClient;
    }

    public void initView() {
        introView.display();
        introView.getTextFieldAddress().setText(this.settings.getAddress());
        introView.getTextFieldPort().setText(this.settings.getPort());
        introView.getTextFieldNickname().setText(this.settings.getNickname());
    }

    public void initController() {
        introView.getConnectButton().addActionListener(e -> {
            if (!areSettingsValid()) {
                return;
            }
            settings.updateConfigFile();

            try {
                networkClient.connectToServer(this.settings.getAddress(), this.settings.getPort());
                networkClient.sendNickname(settings.getNickname());
            } catch (IOException ex) {
                this.settings.getNotification().error("Cannot connect to the chat server");
                return;
            }
            mainController.display();
            mainController.initController();
        });
    }

    private boolean areSettingsValid() {

        String newAddress = introView.getTextFieldAddress().getText().trim();
        if (!this.settings.setAddress(newAddress)) {
            return false;
        }

        String newPort = introView.getTextFieldPort().getText().trim();
        if (!this.settings.setPort(newPort)) {
            return false;
        }

        String newNickname = introView.getTextFieldNickname().getText().trim();
        if (!this.settings.setNickname(newNickname)) {
            return false;
        }
        return true;
    }



    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

}
