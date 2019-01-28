package com.a2sidorov.mychat.model;

import com.a2sidorov.mychat.view.Notification;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("Testing Settings class")
class SettingsTest {

    private static Notification notificationMocked;
    private static Settings settings;

    @BeforeAll
    static void initAll() {
        notificationMocked = mock(Notification.class);
        settings = new Settings(notificationMocked);
    }

    @Nested
    @DisplayName("Testing setAddress method")
    class setAddressTest {
        @DisplayName("when the address is valid then set it to the settings")
        @Test
        void setAddressTest1() {
            settings.setAddress("192.160.0.1");
            assertEquals("192.160.0.1", settings.getAddress());
        }
    }

    @Nested
    @DisplayName("Testing setPort method")
    class setPortTest {

        @DisplayName("when the port number is less than 1024 then call notification")
        @Test
        void setPortTest1() {
            Notification notificationMocked = mock(Notification.class);
            Settings settings = new Settings(notificationMocked);
            settings.setPort("13");
            verify(notificationMocked).error(anyString());
        }

        @DisplayName("when the port number is greater than 65535 then call notification")
        @Test
        void setPortTest2() {
            Notification notificationMocked = mock(Notification.class);
            Settings settings = new Settings(notificationMocked);
            settings.setPort("65536");
            verify(notificationMocked).error(anyString());
        }

        @DisplayName("when the port number is valid then set it to the settings")
        @Test
        void setPortTest3() {
            settings.setPort("1024");
            assertEquals("1024", settings.getPort());
        }
    }

    @Nested
    @DisplayName("Testing setNickname method")
    class setNicknameTest {

        @DisplayName("when the nickname is valid then set it to the settings")
        @Test
        void setNicknameTest1() {
            settings.setNickname("Valid_Nickname");
            assertEquals("Valid_Nickname", settings.getNickname());
        }
    }

}
