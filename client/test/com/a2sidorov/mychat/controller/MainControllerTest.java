package com.a2sidorov.mychat.controller;

import com.a2sidorov.mychat.model.Settings;
import com.a2sidorov.mychat.view.MainView;
import com.a2sidorov.mychat.view.Notification;
import org.junit.jupiter.api.*;

import javax.swing.*;

import static org.mockito.Mockito.*;

@DisplayName("Testing MainController class")
class MainControllerTest {

    private JTextArea textAreaMocked;
    private Notification notificationMocked;
    private IntroController introControllerMocked;
    private MainController mainController;

    @BeforeEach
    void init() {
        textAreaMocked = mock(JTextArea.class);
        MainView mainViewMocked = mock(MainView.class);
        when(mainViewMocked.getTextArea()).thenReturn(textAreaMocked);

        notificationMocked = mock(Notification.class);
        Settings settingsMocked = mock(Settings.class);
        when(settingsMocked.getNotification()).thenReturn(notificationMocked);

        mainController = new MainController(settingsMocked,
                mainViewMocked,
                null);
        introControllerMocked = mock(IntroController.class);
        mainController.setIntroController(introControllerMocked);
    }


    @Nested
    @DisplayName("Testing parsePackets method")
    class paresePacketTest {

        @DisplayName("when a message is passed then append it to the text area")
        @Test
        void parsePacketsTest1() {
            mainController.parsePacket("m/Nickname: messsage");
            verify(textAreaMocked).append(anyString());
        }

        @DisplayName("when a server notification is passed then append it to the text area")
        @Test
        void parsePacketsTest2() {
            mainController.parsePacket("s/server messsage");
            verify(textAreaMocked).append(anyString());
        }

        @DisplayName("when the server closed the connection then switch to the intro view")
        @Test
        void parsePacketsTest3() {
            mainController.parsePacket("c/");
            verify(introControllerMocked).initView();
            verify(notificationMocked).error("Server has closed the connection unexpectedly");
        }
    }
}
