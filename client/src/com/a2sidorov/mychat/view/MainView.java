package com.a2sidorov.mychat.view;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;

public class MainView {

    private JList listNicknames;
    private JTextArea textArea;
    private JTextField textField;
    private JButton buttonSend;

    private JPanel panelMain;
    private JFrame frame;

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
        panelMain = new JPanel(new BorderLayout());

        //east
        panelMain.add(listNicknames, BorderLayout.EAST);

        //center
        panelMain.add(textChatSP, BorderLayout.CENTER);

        //south
        JPanel panelInput = new JPanel(new BorderLayout());
        panelInput.add(textField, BorderLayout.CENTER);
        panelInput.add(buttonSend, BorderLayout.EAST);
        panelMain.add(panelInput, BorderLayout.SOUTH);
    }

    public void display() {
        JPanel contentPane = (JPanel) this.frame.getContentPane();
        contentPane.removeAll();
        contentPane.repaint();
        contentPane.add(panelMain);
        frame.setVisible(true);
    }

    public JTextArea getTextArea() {
        return textArea;
    }

    public JList getListNicknames() {
        return listNicknames;
    }

    public JTextField getTextField() {
        return textField;
    }

    public JButton getButtonSend() {
        return buttonSend;
    }
}
