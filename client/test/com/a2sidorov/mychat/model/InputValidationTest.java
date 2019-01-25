package com.a2sidorov.mychat.model;

import java.io.*;
import java.util.List;
import java.util.Properties;

import com.a2sidorov.mychat.view.Notification;
import org.junit.jupiter.api.*;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Testing Settings class")
class SettingsTest {


    @Nested
    @DisplayName("Testing initTesting method")
    class initSettingsTest {

        @DisplayName("when when address is invalid then error message is shown")
        @Test
        void initSettingsTest1() {


            Properties properties = mock(Properties.class);
            when(properties.getProperty("address")).thenReturn("");
            when(properties.getProperty("port")).thenReturn("1050");
            when(properties.getProperty("nickname")).thenReturn("Nickname");


            Settings settings = new Settings(properties);
            settings.load();

            System.out.println(properties.getProperty("address"));

            assertThrows(IllegalArgumentException.class, () -> settings.initSettings());



        }


    }

}
