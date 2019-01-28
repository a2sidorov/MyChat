package com.a2sidorov.mychat.model;

import com.a2sidorov.mychat.view.Notification;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class Settings {

    private String address;
    private String port;
    private String nickname;
    private String settingsPath;
    private Properties properties;
    private Notification notification;

    public Settings(Notification notification) {
        String rootPath = System.getProperty("user.dir");
        this.settingsPath = rootPath + "/mychatclient.properties";
        this.properties = new Properties();
        load(properties);
        this.address = properties.getProperty("address");
        this.port = properties.getProperty("port");
        this.nickname = properties.getProperty("nickname");
        this.notification = notification;
    }

    private void load(Properties properties) {
        try {
            properties.load(new FileInputStream(settingsPath));
        } catch (IOException e) {
            try {
                properties.put("address", "127.0.0.1");
                properties.put("port", "1050");
                properties.put("nickname", "Unknown");
                properties.store(new FileWriter(settingsPath), "--- Server Configuration ---");
            } catch (IOException e1) {
                String message = "Cannot create configuration file " + settingsPath;
                notification.error(message);
                throw new RuntimeException();
            }
        }
    }

    public void updateConfigFile() {
        try {
            properties.put("address", this.address);
            properties.put("port", this.port);
            properties.put("nickname", this.nickname);
            properties.store(new FileWriter(settingsPath), "--- Server Configuration ---");
        } catch (IOException e1) {
            String message = "Cannot update configuration file " + settingsPath;
            notification.error(message);
            throw new RuntimeException();
        }
    }

    public String getAddress() {
        return address;
    }

    public String getPort() {
        return port;
    }

    public void setAddress(String address) {
        if (InputValidation.isAddressValid(address)) {
            this.address = address;
        } else {
            notification.error("Invalid IP address");
        }
    }

    public void setPort(String port) {
        if (InputValidation.isPortValid(port)) {
            int num = Integer.parseInt(port);

            if (num > 1023 && num < 65535) {
                this.port = port;
            } else {
                notification.error("The port number must be between 1023 and 65535");
            }
        } else {
            notification.error("Invalid port address");
        }
    }

    public void setNickname(String nickname) {
        if (InputValidation.isNicknameValid(nickname)) {
            this.nickname = nickname;
        } else {
            notification.error("Invalid nickname");
        }
    }

    public String getNickname() {
        return nickname;
    }

    public Notification getNotification() {
        return notification;
    }

}
