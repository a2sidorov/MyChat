package com.a2sidorov.mychat.controller;

import com.a2sidorov.mychat.model.Settings;
import com.a2sidorov.mychat.network.NetworkClient;
import com.a2sidorov.mychat.view.IntroView;

import java.io.IOException;

public class IntroController {

    private Settings settings;
    private IntroView introView;
    private MainController mainController;

    public IntroController(Settings settings, IntroView introView) {
        this.settings = settings;
        this.introView = introView;
    }

    public void initView() {
        introView.display();
        introView.getTextFieldAddress().setText(this.settings.getAddress());
        introView.getTextFieldPort().setText(this.settings.getPort());
        introView.getTextFieldNickname().setText(this.settings.getNickname());
    }

    public void initController() {
        introView.getConnectButton().addActionListener(e -> {
            checkSettingsForChanges();

            NetworkClient networkClient = new NetworkClient();
            try {
                networkClient.connectToServer(this.settings.getAddress(), this.settings.getPort());
                networkClient.sendNickname(settings.getNickname());
            } catch (IOException ex) {
                this.settings.getNotification().error("Cannot connect to the chat server");
                return;
            }
            mainController.display();
            mainController.initController();
            mainController.setNetworkClient(networkClient);
        });
    }

    private void checkSettingsForChanges() {
        boolean areSettingsChanged = false;

        //checking if the address was changed
        String newAddress = introView.getTextFieldAddress().getText().trim();
        if (!this.settings.getAddress().equals(newAddress)) {
            areSettingsChanged = true;
            this.settings.setAddress(newAddress);
        }

        //checking if the address was changed
        String newPort = introView.getTextFieldPort().getText().trim();
        if (!this.settings.getPort().equals(newPort)) {
            areSettingsChanged = true;
            this.settings.setPort(newPort);
        }

        //checking if the address was changed
        String newNickname = introView.getTextFieldNickname().getText().trim();
        if (!this.settings.getNickname().equals(newNickname)) {
            areSettingsChanged = true;
            this.settings.setNickname(newNickname);
        }

        if (areSettingsChanged) {
            this.settings.updateConfigFile();
        }
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

}
