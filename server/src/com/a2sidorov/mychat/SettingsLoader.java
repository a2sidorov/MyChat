package com.a2sidorov.mychat;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

class SettingsLoader {

    String configPath;
    Properties properties;
    private Map<String, String> defaultProperties;

    SettingsLoader() {
        String rootPath = System.getProperty("user.dir");
        configPath = rootPath + "/.config";

        properties = new Properties();

        defaultProperties = new HashMap<>();
        defaultProperties.put("address", "127.0.0.1");
        defaultProperties.put("port", "1050");
    }

    Properties load() {
        try {
            properties.load(new FileInputStream(configPath));
        } catch (IOException e) {
            System.out.println("the file is not found");
            try {
                properties.putAll(defaultProperties);
                properties.store(new FileWriter(configPath), "--- Server Configuration ---");
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }
        return properties;
    }




}
