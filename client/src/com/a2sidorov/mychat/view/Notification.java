package com.a2sidorov.mychat.view;

import javax.swing.*;

public class Notification {

    public void error(String message) {
        JOptionPane.showMessageDialog(new JFrame(),
                message,
                "MyChat Error",
                JOptionPane.ERROR_MESSAGE);
    }

}
