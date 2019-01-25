package com.a2sidorov.mychat.controller;

import com.a2sidorov.mychat.model.MainModel;
import com.a2sidorov.mychat.model.Settings;
import com.a2sidorov.mychat.view.IntroView;
import com.a2sidorov.mychat.view.MainView;

import javax.swing.*;
import java.awt.*;

public class IntroController {

    private Settings settings;
    private IntroView introView;

    public IntroController(Settings settings, IntroView introView) {
        this.settings = settings;
        this.introView = introView;
    }

    public void initView() {
        introView.getTextFieldAddress().setText(settings.getAddress());
        introView.getTextFieldPort().setText(settings.getPort());
        introView.getTextFieldNickname().setText(settings.getNickname());
    }

    public void initController() {
        JFrame frame = introView.getFrame();
        introView.getConnectButton().addActionListener(e -> {
            checkSettingsForChanges();



            frame.getContentPane().removeAll();
            frame.getContentPane().repaint();


            SwingUtilities.invokeLater(() -> {
                MainModel mainModel = new MainModel();
                MainView mainView = new MainView(frame);
                //MainController mainController = new IntroController(mainModel, mainView);
                //MainController.initView();
                //MainController.initController();
            });

;





                });

    }

    private void checkSettingsForChanges() {
        boolean areSettingsChanged = false;

        //checking if the address was changed
        String newAddress = introView.getTextFieldAddress().getText().trim();
        if (!settings.getAddress().equals(newAddress)) {
            areSettingsChanged = true;
            settings.setAddress(newAddress);
        }

        //checking if the address was changed
        String newPort = introView.getTextFieldPort().getText().trim();
        if (!settings.getPort().equals(newPort)) {
            areSettingsChanged = true;
            settings.setPort(newPort);
        }

        //checking if the address was changed
        String newNickname = introView.getTextFieldNickname().getText().trim();
        if (!settings.getNickname().equals(newNickname)) {
            areSettingsChanged = true;
            settings.setNickname(newNickname);
        }

        if (areSettingsChanged) {
            settings.updateConfigFile();
        }

    }



}
