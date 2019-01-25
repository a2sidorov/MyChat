package com.a2sidorov.mychat.controller;

import com.a2sidorov.mychat.model.Settings;
import com.a2sidorov.mychat.view.ConnectionView;

public class SettingsController {

    private Settings model;
    private ConnectionView view;

    public SettingsController(Settings model, ConnectionView view) {
        this.model = model;
        this.view = view;
    }

    public void initView() {
        view
    }
}
