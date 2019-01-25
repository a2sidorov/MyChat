package com.a2sidorov.mychat.view;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;


public class MainView {

    private JFrame frame;
    private JList listNicknames;
    private JTextArea textArea;
    private JTextField textField;
    private JButton buttonSend;

    public MainView(JFrame frame) {
        this.frame = frame;

        /* Create UI emements */
        listNicknames = new JList();
        listNicknames.setPreferredSize(new Dimension(200, 0));
        listNicknames.setBorder(BorderFactory.createTitledBorder("Connected Users"));

        textArea = new JTextArea();
        textArea.setEditable(false);
        ((DefaultCaret) textArea.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE); //auto scroll down to last message
        JScrollPane textChatSP = new JScrollPane(textArea);
        textChatSP.setBorder(BorderFactory.createTitledBorder("Chat"));

        textField = new JTextField();

        buttonSend = new JButton("Send");

        /* Add UI elements to frame */
        JPanel mainPanel = new JPanel(new BorderLayout());


        //East
        mainPanel.add(listNicknames, BorderLayout.EAST);

        //Center
        mainPanel.add(textChatSP, BorderLayout.CENTER);

        //South
        JPanel panelInput = new JPanel(new BorderLayout());
        panelInput.add(textField, BorderLayout.CENTER);
        panelInput.add(buttonSend, BorderLayout.EAST);
        mainPanel.add(panelInput, BorderLayout.SOUTH);

        frame.getContentPane().add(mainPanel);
        frame.setVisible(true);
    }

    public JTextArea getTextArea() {
        return textArea;
    }

    public JList getListNicknames() {
        return listNicknames;
    }
}
