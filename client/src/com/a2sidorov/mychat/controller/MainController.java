package com.a2sidorov.mychat.controller;

import com.a2sidorov.mychat.model.MainModel;
import com.a2sidorov.mychat.view.MainView;

public class MainController {

    private MainModel mainModel;
    private MainView mainView;

    public MainController(MainModel mainModel, MainView mainView) {
        this.mainModel = mainModel;
        this.mainView = mainView;
    }

    public void initController() {
    }

    public void updateTextArea(String message) {
        mainView.getTextArea().setText(message);
    }

    public void updateListNicknames(String[] nicknames) {
        mainView.getListNicknames().setListData(nicknames);
    }
}
